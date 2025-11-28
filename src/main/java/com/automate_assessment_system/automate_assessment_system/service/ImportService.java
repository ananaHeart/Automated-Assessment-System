package com.automate_assessment_system.automate_assessment_system.service;

import com.automate_assessment_system.automate_assessment_system.dto.UploadPreviewResponse;
import com.automate_assessment_system.automate_assessment_system.model.*;
import com.automate_assessment_system.automate_assessment_system.repository.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.*;

@Service
public class ImportService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SubjectRepository subjectRepository;
    @Autowired
    private SectionRepository sectionRepository;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private SchoolClassRepository schoolClassRepository;
    @Autowired
    private StudentEnrollmentRepository studentEnrollmentRepository;

    public UploadPreviewResponse previewFile(MultipartFile file) {
        UploadPreviewResponse response = new UploadPreviewResponse();
        try (InputStream inputStream = file.getInputStream()) {
            Workbook workbook = new XSSFWorkbook(inputStream);
            Sheet sheet = workbook.getSheetAt(0);
            Row headerRow = sheet.getRow(0);

            if (headerRow == null) {
                throw new RuntimeException("File has no header row");
            }

            List<String> headers = new ArrayList<>();
            for (Cell cell : headerRow) {
                headers.add(cell.getStringCellValue().trim());
            }
            response.setHeaders(headers);

            Map<String, List<String>> sampleData = new HashMap<>();
            for (String header : headers) {
                sampleData.put(header, new ArrayList<>());
            }

            for (int i = 1; i <= Math.min(5, sheet.getLastRowNum()); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                
             
                
                for (int j = 0; j < headers.size(); j++) {
                    String header = headers.get(j);
                    Cell cell = row.getCell(j, Row.MissingCellPolicy.RETURN_NULL_AND_BLANK);
                    String value = getCellValue(cell);
                    
                    if (!value.isEmpty() && sampleData.get(header).size() < 3) {
                        sampleData.get(header).add(value);
                    }
                }
            }
            response.setSampleData(sampleData);

            Map<String, String> suggestedMappings = generateSuggestedMappings(headers);
            response.setSuggestedMappings(suggestedMappings);

            workbook.close();
            return response;

        } catch (Exception e) {
            throw new RuntimeException("Failed to preview file: " + e.getMessage());
        }
    }

    @Transactional
    public Map<String, Object> commitImport(MultipartFile file, Map<String, String> columnMapping) {
        Map<String, Object> result = new HashMap<>();
        int teachersCreated = 0, studentsCreated = 0, sectionsCreated = 0, enrollmentsCreated = 0;
        
        try (InputStream inputStream = file.getInputStream()) {
            Workbook workbook = new XSSFWorkbook(inputStream);
            Sheet sheet = workbook.getSheetAt(0);
            
            Map<String, Integer> headerIndexMap = createHeaderIndexMap(sheet.getRow(0));
            validateMappings(columnMapping, headerIndexMap);
            
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null || isRowEmpty(row)) continue;
                
                Map<String, String> rowData = extractRowData(row, columnMapping, headerIndexMap);
                
                // ✅ Check if teacher email is empty AFTER extracting rowData
                String teacherEmail = rowData.get(columnMapping.get("teacherEmail"));
                if (teacherEmail == null || teacherEmail.trim().isEmpty()) {
                    continue; // Skip rows without teacher email
                }
                
                String schoolYear = rowData.getOrDefault(columnMapping.get("schoolYear"), "2024-2025");
                
                if (teacherEmail.isEmpty()) {
                    continue; // Skip rows without teacher email
                }
                
                // Teacher
                User teacher = findOrCreateTeacher(rowData, columnMapping);
                if (teacher.getUserId() == null) {
                    teacher.setPassword(null);
                    teacher.setStatus("pending_activation");
                    teacher = userRepository.save(teacher);
                    teachersCreated++;
                }
                
                // Subject
                Subject subject = findOrCreateSubject(rowData, columnMapping);
                
                // Section
                Section section = findOrCreateSection(rowData, columnMapping);
                
                // SchoolClass
                SchoolClass schoolClass = findOrCreateSchoolClass(teacher, subject, section, schoolYear, rowData, columnMapping);
                
             // 5. Find or create Student
                Student student = findOrCreateStudent(rowData, columnMapping);
                if (student.getStudentId() == null) {
                    student = studentRepository.saveAndFlush(student);  // ✅ saveAndFlush
                    System.out.println("SAVED STUDENT ID: " + student.getStudentId());
                    
                    if (student.getStudentId() == null) {
                        throw new RuntimeException("CRITICAL: Student ID is null after flush! Database is not generating IDs.");
                    }
                    studentsCreated++;
                }
                

                // 6. Enrollment
                if (!studentEnrollmentRepository.existsByStudentAndSectionAndSchoolYear(student, section, schoolYear)) {
                    StudentEnrollment enrollment = new StudentEnrollment();
                    enrollment.setStudent(student);
                    enrollment.setSection(section);
                    enrollment.setSchoolYear(schoolYear);
                    studentEnrollmentRepository.save(enrollment);
                    enrollmentsCreated++;
                }
            }
            
            workbook.close();
            
            result.put("success", true);
            result.put("teachersCreated", teachersCreated);
            result.put("studentsCreated", studentsCreated);
            result.put("sectionsCreated", sectionsCreated);
            result.put("enrollmentsCreated", enrollmentsCreated);
            result.put("message", "Import completed successfully");
            
            return result;

        } catch (Exception e) {
            throw new RuntimeException("Import failed: " + e.getMessage() + ". All changes rolled back.");
        }
    }

    private Map<String, Integer> createHeaderIndexMap(Row headerRow) {
        Map<String, Integer> map = new HashMap<>();
        for (Cell cell : headerRow) {
            map.put(cell.getStringCellValue().trim(), cell.getColumnIndex());
        }
        return map;
    }

    private void validateMappings(Map<String, String> columnMapping, Map<String, Integer> headerIndexMap) {
        String[] requiredFields = {"teacherEmail", "subjectName", "gradeLevel", "sectionName", 
                "studentFirstName", "studentLastName"};
        
        for (String field : requiredFields) {
            String header = columnMapping.get(field);
            if (header == null || !headerIndexMap.containsKey(header)) {
                throw new RuntimeException("Required field '" + field + "' is not mapped");
            }
        }
    }

    private Map<String, String> generateSuggestedMappings(List<String> headers) {
        Map<String, String> suggestions = new HashMap<>();
        for (String header : headers) {
            String lower = header.toLowerCase().trim();
            if (lower.contains("email") || lower.contains("adviser")) {
                suggestions.put("teacherEmail", header);
            } else if (lower.contains("subject")) {
                suggestions.put("subjectName", header);
            } else if (lower.contains("grade") || lower.contains("level")) {
                suggestions.put("gradeLevel", header);
            } else if (lower.contains("section")) {
                suggestions.put("sectionName", header);
            } else if (lower.contains("first") || lower.contains("fname")) {
                suggestions.put("studentFirstName", header);
            } else if (lower.contains("last") || lower.contains("lname")) {
                suggestions.put("studentLastName", header);
            } else if (lower.contains("school") && lower.contains("year")) {
                suggestions.put("schoolYear", header);
            }
        }
        return suggestions;
    }

    private String getCellValue(Cell cell) {
        if (cell == null) return "";
        switch (cell.getCellType()) {
            case STRING: 
                return cell.getStringCellValue().trim();
            case NUMERIC: 
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                }
                return String.valueOf((int) cell.getNumericCellValue());
            case BLANK:  // ✅ Add explicit blank handling
                return "";
            default: 
                return "";
        }
    }

    private boolean isRowEmpty(Row row) {
        for (int i = 0; i < row.getLastCellNum(); i++) {
            Cell cell = row.getCell(i, Row.MissingCellPolicy.RETURN_NULL_AND_BLANK);
            if (cell != null && !getCellValue(cell).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private Map<String, String> extractRowData(Row row, Map<String, String> columnMapping, 
                                               Map<String, Integer> headerIndexMap) {
        Map<String, String> data = new HashMap<>();
        for (String field : columnMapping.keySet()) {
            String header = columnMapping.get(field);
            Integer index = headerIndexMap.get(header);
            if (index != null) {
                Cell cell = row.getCell(index, Row.MissingCellPolicy.RETURN_NULL_AND_BLANK);
                data.put(header, getCellValue(cell));
            }
        }
        return data;
    }

    private User findOrCreateTeacher(Map<String, String> rowData, Map<String, String> columnMapping) {
        String email = rowData.get(columnMapping.get("teacherEmail"));
        return userRepository.findByEmail(email)
                .orElseGet(() -> {
                    User newTeacher = new User();
                    newTeacher.setEmail(email);
                    newTeacher.setFirstName(rowData.get(columnMapping.get("teacherFirstName")));
                    newTeacher.setLastName(rowData.get(columnMapping.get("teacherLastName")));
                    newTeacher.setRole("teacher");
                    return newTeacher;
                });
    }

    private Subject findOrCreateSubject(Map<String, String> rowData, Map<String, String> columnMapping) {
        String subjectName = rowData.get(columnMapping.get("subjectName"));
        String gradeLevelStr = rowData.get(columnMapping.get("gradeLevel"));
        
        if (gradeLevelStr == null || gradeLevelStr.trim().isEmpty()) {
            throw new RuntimeException("Grade Level cannot be empty for subject: " + subjectName);
        }
        int gradeLevel = Integer.parseInt(gradeLevelStr);
        
        return subjectRepository.findBySubjectNameAndGradeLevel(subjectName, gradeLevel)
                .orElseGet(() -> {
                    Subject newSubject = new Subject();
                    newSubject.setSubjectName(subjectName);
                    newSubject.setGradeLevel(gradeLevel);
                    newSubject.setSubjectCode(subjectName.substring(0, 3).toUpperCase() + gradeLevel);
                    return subjectRepository.save(newSubject);
                });
    }

    private Section findOrCreateSection(Map<String, String> rowData, Map<String, String> columnMapping) {
        String sectionName = rowData.get(columnMapping.get("sectionName"));
        
        return sectionRepository.findBySectionName(sectionName)
                .orElseGet(() -> {
                    Section newSection = new Section();
                    newSection.setSectionName(sectionName);
                    return sectionRepository.save(newSection);
                });
    }

    private SchoolClass findOrCreateSchoolClass(User teacher, Subject subject, Section section, 
                                                String schoolYear, Map<String, String> rowData, Map<String, String> columnMapping) {
        String gradeLevelStr = rowData.get(columnMapping.get("gradeLevel"));
        
        return schoolClassRepository.findByTeacherAndSubjectAndSectionNameAndSchoolYear(
                teacher, subject, section.getSectionName(), schoolYear)
                .orElseGet(() -> {
                    SchoolClass newClass = new SchoolClass();
                    newClass.setTeacher(teacher);
                    newClass.setSubject(subject);
                    newClass.setSectionName(section.getSectionName());
                    newClass.setGradeLevel(Integer.parseInt(gradeLevelStr));
                    newClass.setSchoolYear(schoolYear);
                    newClass.setStatus("active");
                    return schoolClassRepository.save(newClass);
                });
    }

    private Student findOrCreateStudent(Map<String, String> rowData, Map<String, String> columnMapping) {
        String firstName = rowData.get(columnMapping.get("studentFirstName"));
        String lastName = rowData.get(columnMapping.get("studentLastName"));
        
        return studentRepository.findByFirstNameAndLastName(firstName, lastName)
                .orElseGet(() -> {
                    Student newStudent = new Student();
                    newStudent.setFirstName(firstName);
                    newStudent.setLastName(lastName);
                    return newStudent;
                });
    }
}