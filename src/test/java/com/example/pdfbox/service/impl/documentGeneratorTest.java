package com.example.pdfbox.service.impl;

import br.com.sulamerica.formulariopadrao.core.domain.entity.DocumentTemplate;
import br.com.sulamerica.formulariopadrao.infrastructure.documentgenerator.pdfrender.JsonPdfRenderer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class DocumentGeneratorImplVariableReplacementTest {

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
    void shouldReplaceVariablesInTemplateCorrectly() throws Exception {
        // Arrange
        Map<String, String> variables = new HashMap<>();
        variables.put("{{name}}", "Rennan");
        variables.put("{{city}}", "São Paulo");

        String templateOriginal = "{\"text\":\"Olá {{name}} de {{city}}!\"}";
        String templateEsperado = "{\"text\":\"Olá Rennan de São Paulo!\"}";

        DocumentTemplate template = mock(DocumentTemplate.class);
        when(template.getTemplate()).thenReturn(templateOriginal);

        // Mock comportamento mínimo para não gerar exceção
        when(mapper.readTree(templateEsperado)).thenReturn(null);
        when(renderer.render(null)).thenReturn(new byte[0]);

        // Act
        documentGenerator.createDocument(variables, template);

        // Assert — verificamos se o JSON final passou pelo mapper com as variáveis substituídas
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(mapper).readTree(captor.capture());

        String templateProcessado = captor.getValue();
        assertTrue(templateProcessado.contains("Rennan"));
        assertTrue(templateProcessado.contains("São Paulo"));
        assertFalse(templateProcessado.contains("{{name}}"));
        assertFalse(templateProcessado.contains("{{city}}"));
    }
}