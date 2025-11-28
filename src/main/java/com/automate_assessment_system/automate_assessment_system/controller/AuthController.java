package com.automate_assessment_system.automate_assessment_system.controller;


import com.automate_assessment_system.automate_assessment_system.jwt.JwtUtil;
import com.automate_assessment_system.automate_assessment_system.model.LoginRequest;
import com.automate_assessment_system.automate_assessment_system.model.LoginResponse;
import com.automate_assessment_system.automate_assessment_system.model.User;
import com.automate_assessment_system.automate_assessment_system.service.AuthService;
import com.automate_assessment_system.automate_assessment_system.service.CustomUserDetailsService;
import com.automate_assessment_system.automate_assessment_system.model.ActivationRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.automate_assessment_system.automate_assessment_system.dto.EmailVerificationRequest;
import com.automate_assessment_system.automate_assessment_system.model.MessageDTO; // Assuming you have a

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    // --- THESE ARE THE NEW SERVICES WE NEED ---
    @Autowired
    private CustomUserDetailsService userDetailsService;
    
    @Autowired
    private JwtUtil jwtUtil;


    // --- THIS METHOD IS NOW UPDATED ---
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest loginRequest) {
        try {
            // First, authenticate the user. This part is the same.
            authService.loginUser(loginRequest);

            // If authentication is successful, load UserDetails
            final UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getEmail());

            // Generate the JWT
            final String jwt = jwtUtil.generateToken(userDetails);

            // Return the JWT in the response
            return ResponseEntity.ok(new LoginResponse(jwt));

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
    
    @PostMapping("/activate")
    public ResponseEntity<?> activateUser(@RequestBody ActivationRequest request) {
        try {
            User activatedUser = authService.activateUser(request);
            return ResponseEntity.ok(activatedUser);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @PostMapping("/verify")
    public ResponseEntity<?> verifyEmailForActivation(@RequestBody EmailVerificationRequest request) {
        try {
            authService.verifyActivationStatus(request);
            // If the service method completes without throwing an error, it means the email is valid.
            return ResponseEntity.ok(new MessageDTO("Email is valid for activation."));
        } catch (RuntimeException e) {
            // If the service method throws an error, we catch it and send it to the frontend.
            return ResponseEntity.badRequest().body(new MessageDTO(e.getMessage()));
        }
    }
}