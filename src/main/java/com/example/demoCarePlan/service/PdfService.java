package com.example.demoCarePlan.service;

import java.io.InputStream;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class PdfService {

    private final ObjectMapper mapper = new ObjectMapper();
    private final JsonPdfRenderer renderer = new JsonPdfRenderer();

    public byte[] generateFromTemplateResource(String resourcePath) throws Exception {
        try (InputStream templateIS = new ClassPathResource(resourcePath).getInputStream()) {
            JsonNode templateJson = mapper.readTree(templateIS);
            return renderer.render(templateJson);
        }
    }
}