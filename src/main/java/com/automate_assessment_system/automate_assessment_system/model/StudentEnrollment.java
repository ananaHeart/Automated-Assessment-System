package com.automate_assessment_system.automate_assessment_system.model;

import jakarta.persistence.*;

@Entity
@Table(name = "student_enrollment")
public class StudentEnrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // Corrected the column name to match your blueprint
    @Column(name = "student_enrollment_id")
    private Integer studentEnrollmentId;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    // --- THIS IS THE CORRECTED RELATIONSHIP ---
    // It links to a Section, not a Class.
    @ManyToOne
    @JoinColumn(name = "section_id", nullable = false)
    private Section section;
    
    // Your blueprint also has a school_year field
    @Column(name = "school_year")
    private String schoolYear;

    // --- Getters and Setters ---

    public Integer getStudentEnrollmentId() {
        return studentEnrollmentId;
    }

    public void setStudentEnrollmentId(Integer studentEnrollmentId) {
        this.studentEnrollmentId = studentEnrollmentId;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Section getSection() {
        return section;
    }

    public void setSection(Section section) {
        this.section = section;
    }

    public String getSchoolYear() {
        return schoolYear;
    }

    public void setSchoolYear(String schoolYear) {
        this.schoolYear = schoolYear;
    }
}