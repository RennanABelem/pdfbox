package com.example.pdfbox.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Service
public class PdfServiceImpl {

    private final ObjectMapper mapper = new ObjectMapper();
    private final JsonPdfRenderer renderer = new JsonPdfRenderer();

    public byte[] generateFromTemplateResource(String resourcePath, String outputFileName) throws Exception {
        try (InputStream templateStream = new ClassPathResource(resourcePath).getInputStream()) {
            JsonNode templateJson = mapper.readTree(templateStream);
            Map<String, Object> variables = extractVariables(templateJson);
            JsonNode filledTemplate = replacePlaceholders(templateJson, variables);

            byte[] pdfBytes = renderer.render(filledTemplate);
            Path outputPath = Paths.get(outputFileName);
            Files.write(outputPath, pdfBytes);

            System.out.println("PDF gerado em: " + outputPath.toAbsolutePath());
            return pdfBytes;
        }
    }

    private Map<String, Object> extractVariables(JsonNode templateJson) {
        String template = templateJson.toString();

        String payload = """
            {
              "beneficiary": {
                "name": "Rennan Belem",
                "code": "545xx3212xx1321xxx20"
              },
              "requester": {
                "name": "Dr. João Amoeba",
                "crm": "CRM 123456"
              },
              "procedure": {
                "tuss": "12345678",
                "additional_packages": [
                  "1 - 40303489 - TUSS_TEXTO_1",
                  "2 - 40303490 - TUSS_TEXTO_2",
                  "3 - 40303491 - TUSS_TEXTO_4"
                ],
                "procedure_package": "texto do procedure package",
                "hospital_package": "texto do hospital_package",
                "level": "3",
                "surgery_approach": "texto da surgery_approach"
              },
              "diagnosis": {
                "cid": "M54.2 - Cervica",
                "diagnosis_time": "Texto sobre diagnosis_time",
                "clinical_justification": "ustification... texto longo sobre clinical_justification... texto longo sobre clinical_justification... texto longo sobre clinical_justification... texto longo sobre clinical_justification... texto longo sobre clinical_justification... texto longo sobre clinical_justification... texto longo sobre clinical_justification... texto longo sobre clinical_justification.. texto longo sobre clinical_justification...."
              },
              "hospital_admission": {
                "uti_reservation": "Sim"
              }
            }
            """;

        ExtractVariables extractor = new ExtractVariables();
        Map<String, Object> variables = extractor.execute(template, payload);

        variables.forEach((k, v) -> System.out.println("-> " + k + " -> " + v));
        return variables;
    }

    private JsonNode replacePlaceholders(JsonNode node, Map<String, Object> variables) {
        if (node.isObject()) {
            ObjectNode obj = (ObjectNode) node;
            obj.fieldNames().forEachRemaining(field -> {
                JsonNode child = obj.get(field);
                obj.set(field, replacePlaceholders(child, variables));
            });
            return obj;
        }

        if (node.isArray()) {
            ArrayNode array = (ArrayNode) node;
            for (int i = 0; i < array.size(); i++) {
                array.set(i, replacePlaceholders(array.get(i), variables));
            }
            return array;
        }

        if (node.isTextual()) {
            String text = node.asText();
            for (Map.Entry<String, Object> entry : variables.entrySet()) {
                if (text.contains(entry.getKey())) {
                    Object value = entry.getValue();
                    if (value instanceof List<?>) {
                        ArrayNode arrayNode = mapper.createArrayNode();
                        ((List<?>) value).forEach(item -> arrayNode.add(item.toString()));
                        return arrayNode;
                    } else {
                        return mapper.getNodeFactory().textNode(value.toString());
                    }
                }
            }
        }

        return node;
    }
}
