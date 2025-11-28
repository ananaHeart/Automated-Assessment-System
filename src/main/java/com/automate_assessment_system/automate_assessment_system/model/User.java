package com.automate_assessment_system.automate_assessment_system.model;

import jakarta.persistence.*;
import java.sql.Timestamp;
import java.util.Date;

@Entity
@Table(name = "user")
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "first_name", length = 30, nullable = true)
    private String firstName;

    @Column(name = "last_name", length = 30, nullable = true)
    private String lastName;

    @Column(name = "gender", length = 10, nullable = true)
    private String gender;

    @Temporal(TemporalType.DATE)
    @Column(name = "birth_date", nullable = true)
    private Date dateOfBirth;

    @Column(name = "email", unique = true, length = 80)
    private String email;

    @Column(name = "password", length = 255, nullable = true)
    private String password;

    @Column(name = "role", length = 20)
    private String role;

    @Column(name = "status", length = 20)
    private String status; // Values: "pending_activation" or "active"

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", updatable = false, insertable = false,
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Timestamp createdAt;
    
    // --- Getters and Setters ---
    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public Date getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(Date dateOfBirth) { this.dateOfBirth = dateOfBirth; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}