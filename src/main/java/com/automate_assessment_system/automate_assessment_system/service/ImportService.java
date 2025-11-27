package com.automate_assessment_system.automate_assessment_system.service;

import com.automate_assessment_system.automate_assessment_system.model.User;
import com.automate_assessment_system.automate_assessment_system.repository.UserRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ImportService {

    @Autowired
    private UserRepository userRepository;

    public List<String> analyzeFile(MultipartFile file) {
        List<String> headers = new ArrayList<>();
        try (InputStream inputStream = file.getInputStream()) {
            Workbook workbook = new XSSFWorkbook(inputStream);
            Sheet sheet = workbook.getSheetAt(0);
            Row headerRow = sheet.getRow(0);

            if (headerRow != null) {
                for (Cell cell : headerRow) {
                    headers.add(cell.getStringCellValue());
                }
            }
            workbook.close();
        } catch (Exception e) {
            throw new RuntimeException("Failed to analyze Excel file: " + e.getMessage());
        }
        return headers;
    }

    @Transactional
    public void processFile(MultipartFile file, Map<String, String> columnMapping) {
        try (InputStream inputStream = file.getInputStream()) {
            Workbook workbook = new XSSFWorkbook(inputStream);
            Sheet sheet = workbook.getSheetAt(0);

            Map<String, Integer> headerIndexMap = new HashMap<>();
            Row headerRow = sheet.getRow(0);
            for (Cell cell : headerRow) {
                headerIndexMap.put(cell.getStringCellValue(), cell.getColumnIndex());
            }

            Integer emailIndex = headerIndexMap.get(columnMapping.get("teacherEmail"));
            if (emailIndex == null) {
                throw new RuntimeException("The required column 'teacherEmail' was not mapped.");
            }

            List<User> teachersToSave = new ArrayList<>();
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                DataFormatter formatter = new DataFormatter();
                String email = formatter.formatCellValue(row.getCell(emailIndex));

                if (email != null && !email.isBlank()) {
                    // Check if a user with this email already exists to avoid duplicates
                    if (!userRepository.findByEmail(email).isPresent()) {
                        User teacher = new User();
                        teacher.setEmail(email);
                        teacher.setFirstName(null);
                        teacher.setLastName(null);
                        teacher.setRole("teacher");
                        teacher.setPassword(null);
                        teachersToSave.add(teacher);
                    }
                }
            }

            if (!teachersToSave.isEmpty()) {
                userRepository.saveAll(teachersToSave);
            }
            workbook.close();

        } catch (Exception e) {
            throw new RuntimeException("Failed to process Excel file: " + e.getMessage());
        }
    }
}