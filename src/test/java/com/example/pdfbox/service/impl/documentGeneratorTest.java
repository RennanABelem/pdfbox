package com.example.pdfbox.service.impl;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.mockito.Mockito.*;

class DocumentGeneratorImplTest {

    @Test
    void testCreateDocument_withReflectionInjection() throws Exception {
        // Mocks
        ObjectMapper mockMapper = mock(ObjectMapper.class);
        JsonPdfRenderer mockRenderer = mock(JsonPdfRenderer.class);
        DocumentTemplate mockTemplate = mock(DocumentTemplate.class);
        JsonNode mockJsonNode = mock(JsonNode.class);

        // Dados simulados
        String rawTemplate = "{\"text\": \"Olá, {{nome}}!\"}";
        String filledTemplate = "{\"text\": \"Olá, Rennan!\"}";
        byte[] expectedPdf = new byte[]{10, 20, 30};

        // Comportamento dos mocks
        when(mockTemplate.getTemplate()).thenReturn(rawTemplate);
        when(mockMapper.readTree(filledTemplate)).thenReturn(mockJsonNode);
        when(mockRenderer.render(mockJsonNode)).thenReturn(expectedPdf);

        // Instância real
        DocumentGeneratorImpl generator = new DocumentGeneratorImpl();

        // Injeção via reflexão
        injectPrivateField(generator, "mapper", mockMapper);
        injectPrivateField(generator, "renderer", mockRenderer);

        // Execução
        Map<String, String> variables = Map.of("{{nome}}", "Rennan");
        byte[] result = generator.createDocument(variables, mockTemplate);

        // Verificação
        assertArrayEquals(expectedPdf, result);
    }

    // Método auxiliar para injetar mocks em campos privados
    private void injectPrivateField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);

        // Remove final usando reflexão (hack necessário)
        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(field, field.getModifiers() & ~java.lang.reflect.Modifier.FINAL);

        field.set(target, value);
    }
}