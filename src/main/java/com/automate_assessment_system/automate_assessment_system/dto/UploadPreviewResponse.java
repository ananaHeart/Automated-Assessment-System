package com.automate_assessment_system.automate_assessment_system.dto;

import java.util.List;
import java.util.Map;

public class UploadPreviewResponse {
    private List<String> headers;
    private Map<String, List<String>> sampleData;
    private Map<String, String> suggestedMappings;

    public List<String> getHeaders() { return headers; }
    public void setHeaders(List<String> headers) { this.headers = headers; }
    public Map<String, List<String>> getSampleData() { return sampleData; }
    public void setSampleData(Map<String, List<String>> sampleData) { this.sampleData = sampleData; }
    public Map<String, String> getSuggestedMappings() { return suggestedMappings; }
    public void setSuggestedMappings(Map<String, String> suggestedMappings) { this.suggestedMappings = suggestedMappings; }
}