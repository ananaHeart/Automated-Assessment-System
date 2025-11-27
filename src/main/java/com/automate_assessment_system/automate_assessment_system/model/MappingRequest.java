package com.automate_assessment_system.automate_assessment_system.model;

import java.util.Map;

public class MappingRequest {

    // The key is our system's field name (e.g., "firstName", "email")
    // The value is the header from the Excel file (e.g., "Teacher First Name", "Teacher Email")
    private Map<String, String> columnMapping;

    // Getters and Setters
    public Map<String, String> getColumnMapping() {
        return columnMapping;
    }

    public void setColumnMapping(Map<String, String> columnMapping) {
        this.columnMapping = columnMapping;
    }
}