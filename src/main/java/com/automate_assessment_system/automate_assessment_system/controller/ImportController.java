package com.automate_assessment_system.automate_assessment_system.controller;

import com.automate_assessment_system.automate_assessment_system.dto.UploadPreviewResponse;
import com.automate_assessment_system.automate_assessment_system.service.ImportService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class ImportController {

    @Autowired
    private ImportService importService;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Step 1: Upload file and get preview
     */
    @PostMapping(value = "/enrollment/upload", consumes = "multipart/form-data")
    @PreAuthorize("hasRole('principal')")  // ✅ Changed from hasAuthority to hasRole
    public ResponseEntity<UploadPreviewResponse> uploadEnrollmentFile(
            @RequestParam("file") MultipartFile file) {
        
        try {
            UploadPreviewResponse preview = importService.previewFile(file);
            return ResponseEntity.ok(preview);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    /**
     * Step 2: Commit the import
     */
    @PostMapping(value = "/enrollment/commit", consumes = "multipart/form-data")
    @PreAuthorize("hasRole('principal')")  // ✅ Changed from hasAuthority to hasRole
    public ResponseEntity<Map<String, Object>> commitEnrollment(
            @RequestParam("file") MultipartFile file,
            @RequestParam("mapping") String mappingJson) {
        
        try {
            Map<String, String> columnMapping = objectMapper.readValue(mappingJson, 
                new com.fasterxml.jackson.core.type.TypeReference<Map<String, String>>() {});
            
            Map<String, Object> result = importService.commitImport(file, columnMapping);
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            Map<String, Object> error = Map.of(
                "success", false,
                "message", "Import failed: " + e.getMessage()
            );
            return ResponseEntity.badRequest().body(error);
        }
    }
}