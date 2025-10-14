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

    public byte[] generateFromTemplateResource(String resourcePath, String outputFileName) throws Exception {
        try (InputStream templateIS = new ClassPathResource(resourcePath).getInputStream()) {
            JsonNode templateJson = mapper.readTree(templateIS);
            
            JsonNode filledJson = replacePlaceholders(templateJson, returnDinamicMap(templateJson));
            
            byte[] pdfBytes = renderer.render(filledJson);

            Path outputPath = Paths.get(outputFileName);
            Files.write(outputPath, pdfBytes);

            System.out.println("PDF gerado em: " + outputPath.toAbsolutePath());
            return pdfBytes;
        }
    }

	private Map<String, Object> returnDinamicMap(JsonNode templateJson) {
		
		var template = templateJson.toString();
		var payload = "{\r\n"
				+ "  \"beneficiary\": {\r\n"
				+ "    \"name\": \"Rennan Belem\",\r\n"
				+ "    \"code\": \"545xx3212xx1321xxx20\"\r\n"
				+ "  },\r\n"
				+ "  \"requester\": {\r\n"
				+ "    \"name\": \"Dr. João Amoeba\",\r\n"
				+ "    \"crm\": \"CRM 123456\"\r\n"
				+ "  },\r\n"
				+ "  \"procedure\": {\r\n"
				+ "    \"tuss\": \"12345678\",\r\n"
				+ "    \"additional_packages\": [\r\n"
				+ "      \"1 - 40303489 - TUSS_TEXTO_1\",\r\n"
				+ "      \"2 - 40303490 - TUSS_TEXTO_2\",\r\n"
				+ "      \"3 - 40303491 - TUSS_TEXTO_4\"\r\n"
				+ "    ],\r\n"
				+ "	\"procedure_package\":\"texto do procedure package\",\r\n"
				+ "	\"hospital_package\":\"texto do hospital_package\",\r\n"
				+ "	\"level\":\"3\",\r\n"
				+ "	\"surgery_approach\":\"texto da surgery_approach\"\r\n"
				+ "  },\r\n"
				+ "  \"diagnosis\": {\r\n"
				+ "    \"cid\":\"M54.2 - Cervica\",\r\n"
				+ "	\"diagnosis_time\":\"Texto sobre diagnosis_time\",\r\n"
				+ "	\"clinical_justification\":\"texto sobre clinical_justification\"\r\n"
				+ "  },\r\n"
				+ "  \"hospital_admission\": {\r\n"
				+ "	  \"uti_reservation\":\"Sim\"\r\n"
				+ "  }\r\n"
				+ "}";
		var usecase = new ExtractVariables();
		Map<String, Object> varzinha = usecase.execute(template, payload);

		varzinha.forEach((a, b) -> System.out.println("a " + a + ", b " + b));
		
		return varzinha;
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
                String key = entry.getKey();
                if (text.contains(key)) {
                    Object value = entry.getValue();
                    if (value instanceof List) {
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