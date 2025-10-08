package com.example.demoCarePlan.service;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Service
public class PdfService {

    private final ObjectMapper mapper = new ObjectMapper();
    private final JsonPdfRenderer renderer = new JsonPdfRenderer();

    public byte[] generateFromTemplateResource(String resourcePath, Map<String, Object> variables, String outputFileName) throws Exception {
        try (InputStream templateIS = new ClassPathResource(resourcePath).getInputStream()) {
            JsonNode templateJson = mapper.readTree(templateIS);

            JsonNode filledJson = replacePlaceholders(templateJson, variables);

            byte[] pdfBytes = renderer.render(filledJson);

            Path outputPath = Paths.get(outputFileName);
            Files.write(outputPath, pdfBytes);

            System.out.println("PDF gerado em: " + outputPath.toAbsolutePath());
            return pdfBytes;
        }
    }
    
    private JsonNode replacePlaceholders(JsonNode node, Map<String, Object> variables) {
        if (node.isObject()) {
            ObjectNode obj = (ObjectNode) node;
            Iterator<String> fieldNames = obj.fieldNames();
            while (fieldNames.hasNext()) {
                String field = fieldNames.next();
                JsonNode child = obj.get(field);
                obj.set(field, replacePlaceholders(child, variables));
            }
            return obj;
        } else if (node.isArray()) {
            ArrayNode arr = (ArrayNode) node;
            for (int i = 0; i < arr.size(); i++) {
                arr.set(i, replacePlaceholders(arr.get(i), variables));
            }
            return arr;
        } else if (node.isTextual()) {
            String text = node.asText();
            for (Map.Entry<String, Object> entry : variables.entrySet()) {
                String key = "${" + entry.getKey() + "}";
                if (text.contains(key)) {
                    Object value = entry.getValue();
                    if (value instanceof List) {
                        // converte listas em ArrayNode
                        ArrayNode arrayNode = mapper.createArrayNode();
                        ((List<?>) value).forEach(v -> arrayNode.add(v.toString()));
                        return arrayNode;
                    } else {
                        return mapper.getNodeFactory().textNode(value.toString());
                    }
                }
            }
            return node;
        } else {
            return node;
        }
    }
}