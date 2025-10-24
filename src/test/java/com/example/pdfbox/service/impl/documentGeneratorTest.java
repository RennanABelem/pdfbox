package com.example.pdfbox.service.impl;

import br.com.sulamerica.formulariopadrao.core.domain.entity.DocumentTemplate;
import br.com.sulamerica.formulariopadrao.infrastructure.documentgenerator.pdfrender.JsonPdfRenderer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DocumentGeneratorImplTest {

    @InjectMocks
    private DocumentGeneratorImpl documentGenerator;

    @Mock
    private ObjectMapper mapper;

    @Mock
    private JsonPdfRenderer renderer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldGenerateDocumentWithSuccess() throws Exception {
        // Arrange
        Map<String, String> variables = new HashMap<>();
        variables.put("{{name}}", "Rennan");
        variables.put("{{age}}", "30");

        String templateContent = "{\"text\": \"Hello {{name}}, age {{age}}\"}";
        String expectedFilled = "{\"text\": \"Hello Rennan, age 30\"}";

        DocumentTemplate template = mock(DocumentTemplate.class);
        when(template.getTemplate()).thenReturn(templateContent);

        JsonNode jsonNode = mock(JsonNode.class);
        when(mapper.readTree(expectedFilled)).thenReturn(jsonNode);
        when(renderer.render(jsonNode)).thenReturn("PDF_BYTES".getBytes());

        // Act
        byte[] result = documentGenerator.createDocument(variables, template);

        // Assert
        assertNotNull(result);
        assertArrayEquals("PDF_BYTES".getBytes(), result);
        verify(mapper).readTree(expectedFilled);
        verify(renderer).render(jsonNode);
    }

    @Test
    void shouldThrowRuntimeExceptionWhenMapperFails() throws Exception {
        // Arrange
        Map<String, String> variables = Map.of("{{x}}", "1");
        DocumentTemplate template = mock(DocumentTemplate.class);
        when(template.getTemplate()).thenReturn("{invalid-json}");

        when(mapper.readTree(anyString())).thenThrow(new RuntimeException("JSON error"));

        // Act + Assert
        assertThrows(RuntimeException.class, () ->
                documentGenerator.createDocument(variables, template)
        );

        verify(mapper).readTree(anyString());
        verify(renderer, never()).render(any());
    }
}