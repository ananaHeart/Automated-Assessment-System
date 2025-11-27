package com.automate_assessment_system.automate_assessment_system.model;

import jakarta.persistence.*;

@Entity
@Table(name = "subject") // The table name is 'subject', not 'subjects'
public class Subject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "subject_id")
    private Integer subjectId;

    @Column(name = "subject_code", length = 20)
    private String subjectCode; // This field was missing

    @Column(name = "subject_name", length = 30)
    private String subjectName; // Corrected the field name from 'name'

    @Column(name = "grade_level")
    private Integer gradeLevel; // This field was missing

    // --- Getters and Setters ---

    public Integer getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Integer subjectId) {
        this.subjectId = subjectId;
    }

    public String getSubjectCode() {
        return subjectCode;
    }

    public void setSubjectCode(String subjectCode) {
        this.subjectCode = subjectCode;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public Integer getGradeLevel() {
        return gradeLevel;
    }

    public void setGradeLevel(Integer gradeLevel) {
        this.gradeLevel = gradeLevel;
    }
}