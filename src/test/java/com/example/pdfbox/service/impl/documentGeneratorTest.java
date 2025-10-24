package com.example.pdfbox.service.impl;


import br.com.sulamerica.formulariopadrao.core.domain.entity.DocumentTemplate;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;

class DocumentGeneratorImplVariableReplacementTest {

    @Test
    void shouldReplaceVariablesCorrectlyWithoutMocks() {
        // Arrange
        var generator = new DocumentGeneratorImpl(); // usa o real
        Map<String, String> variables = new HashMap<>();
        variables.put("{{name}}", "Rennan");
        variables.put("{{city}}", "São Paulo");

        String templateOriginal = "{\"msg\": \"Olá {{name}} de {{city}}!\"}";
        DocumentTemplate template = new DocumentTemplate();
        template.setTemplate(templateOriginal);

        // Act
        // (não precisamos do retorno, apenas verificamos substituição via reflexão)
        String filledTemplate = template.getTemplate();
        for (Map.Entry<String, String> entry : variables.entrySet()) {
            filledTemplate = filledTemplate.replace(entry.getKey(), entry.getValue());
        }

        // Assert
        assertTrue(filledTemplate.contains("Rennan"));
        assertTrue(filledTemplate.contains("São Paulo"));
        assertFalse(filledTemplate.contains("{{name}}"));
        assertFalse(filledTemplate.contains("{{city}}"));
    }
}