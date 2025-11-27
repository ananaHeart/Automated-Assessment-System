package com.automate_assessment_system.automate_assessment_system.controller;

import com.automate_assessment_system.automate_assessment_system.service.ImportService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/import")
public class ImportController {

    @Autowired
    private ImportService importService;

    @PostMapping("/analyze")
    @PreAuthorize("hasAuthority('principal')")
    public ResponseEntity<List<String>> analyzeFile(@RequestParam("file") MultipartFile file) {
        // ... (This method stays the same)
        List<String> headers = importService.analyzeFile(file);
        return ResponseEntity.ok(headers);
    }

    // --- THIS IS THE IMPROVED ENDPOINT ---
    @PostMapping("/process")
    @PreAuthorize("hasAuthority('principal')")
    public ResponseEntity<String> processFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("mapping") String mappingJson) { // We now receive mapping as a String

        try {
            // We use a library (already included in Spring) to convert the string back to a Map
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, String> columnMapping = objectMapper.readValue(mappingJson, new TypeReference<Map<String, String>>() {});
            
            importService.processFile(file, columnMapping);
            
            return ResponseEntity.ok("File processed and teachers imported successfully.");

        } catch (Exception e) {
            // If the JSON string is badly formatted, we send back an error
            return ResponseEntity.badRequest().body("Invalid mapping format: " + e.getMessage());
        }
    }
}