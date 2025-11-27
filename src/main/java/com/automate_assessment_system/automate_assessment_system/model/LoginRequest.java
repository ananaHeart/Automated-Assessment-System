package com.automate_assessment_system.automate_assessment_system.model;

public class LoginRequest {

    private String email;
    private String password;

    // Getters and Setters are required
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}