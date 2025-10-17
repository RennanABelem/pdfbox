package com.example.pdfbox.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.springframework.core.io.ClassPathResource;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@DisplayName("PdfServiceImpl")
class PdfServiceImplTest {

    private final PdfServiceImpl service = new PdfServiceImpl();
    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    @DisplayName("Extrai variáveis corretamente do template")
    void testExtractVariables() throws Exception {
        JsonNode template = mapper.readTree("""
            {
              "lines": [
                { "text-value": "${beneficiary.name}" },
                { "text-value": "${procedure.tuss}" }
              ]
            }
        """);

        Method method = PdfServiceImpl.class.getDeclaredMethod("extractVariables", JsonNode.class);
        method.setAccessible(true);

        @SuppressWarnings("unchecked")
		Map<String, Object> result = (Map<String, Object>) method.invoke(service, template);

        assertEquals("Rennan Belem", result.get("${beneficiary.name}"));
        assertEquals("12345678", result.get("${procedure.tuss}"));
    }


    @Nested
    @DisplayName("Método replacePlaceholders")
    class ReplacePlaceholdersTests {

    	@Test
    	@DisplayName("Substitui variáveis por valores no JSON")
    	void testReplacePlaceholders() throws Exception {
    	    JsonNode template = mapper.readTree("""
    	        {
    	          "text-value": "${beneficiary.name}",
    	          "other": "sem variável"
    	        }
    	    """);

    	    Map<String, Object> variables = Map.of("${beneficiary.name}", "Rennan Belem");

    	    Method method = PdfServiceImpl.class.getDeclaredMethod("replacePlaceholders", JsonNode.class, Map.class);
    	    method.setAccessible(true);

    	    JsonNode result = (JsonNode) method.invoke(service, template, variables);

    	    assertEquals("Rennan Belem", result.get("text-value").asText());
    	    assertEquals("sem variável", result.get("other").asText());
    	}


    	@Test
    	@DisplayName("Substitui variáveis por arrays quando necessário")
    	void testReplacePlaceholdersWithArray() throws Exception {
    	    JsonNode template = mapper.readTree("""
    	        {
    	          "text-value": "${procedure.additional_packages}"
    	        }
    	    """);

    	    Map<String, Object> variables = Map.of(
    	        "${procedure.additional_packages}",
    	        List.of("Item 1", "Item 2")
    	    );

    	    Method method = PdfServiceImpl.class.getDeclaredMethod("replacePlaceholders", JsonNode.class, Map.class);
    	    method.setAccessible(true);

    	    JsonNode result = (JsonNode) method.invoke(service, template, variables);

    	    assertTrue(result.get("text-value").isArray());
    	    assertEquals("Item 1", result.get("text-value").get(0).asText());
    	}

    }

    @Nested
    @DisplayName("Método generateFromTemplateResource")
    class GenerateFromTemplateTests {

        @Test
        @DisplayName("Gera PDF a partir de template simulado")
        void testGenerateFromTemplateResource() throws Exception {
            String templateJson = """
                {
                  "lines": [
                    { "text-value": "${beneficiary.name}" }
                  ]
                }
            """;

            try (
                MockedConstruction<JsonPdfRenderer> rendererMock = mockConstruction(JsonPdfRenderer.class,
                    (mock, context) -> when(mock.render(any())).thenReturn("PDF".getBytes()));
                MockedConstruction<ClassPathResource> resourceMock = mockConstruction(ClassPathResource.class,
                    (mock, context) -> when(mock.getInputStream()).thenReturn(new ByteArrayInputStream(templateJson.getBytes())))
            ) {
                byte[] result = new PdfServiceImpl().generateFromTemplateResource("fake-template.json", "output.pdf");

                assertNotNull(result);
                assertEquals("PDF", new String(result));
            }
        }
    }
}
