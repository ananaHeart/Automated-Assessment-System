package com.automate_assessment_system.automate_assessment_system.model;

public class LoginResponse {

    private final String jwt;

    public LoginResponse(String jwt) {
        this.jwt = jwt;
    }

    public String getJwt() {
        return jwt;
    }
}