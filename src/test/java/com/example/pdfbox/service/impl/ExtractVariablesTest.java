package com.example.pdfbox.service.impl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ExtractVariables")
class ExtractVariablesTest {

    private final ExtractVariables extractor = new ExtractVariables();

    @Test
    @DisplayName("Extrai variáveis simples do template")
    void testExtractSimpleVariables() {
        String template = "Olá ${user.name}, seu pedido ${order.id} está pronto.";
        String payload = "{}";

        Map<String, Object> result = extractor.execute(template, payload);

        assertTrue(result.containsKey("${user.name}"));
        assertTrue(result.containsKey("${order.id}"));
    }

    @Test
    @DisplayName("Resolve variáveis com valores primitivos")
    void testResolvePrimitiveValues() {
        String template = "Olá ${user.name}, idade ${user.age}";
        String payload = """
            {
              "user": {
                "name": "Rennan",
                "age": 30
              }
            }
            """;

        Map<String, Object> result = extractor.execute(template, payload);

        assertEquals("Rennan", result.get("${user.name}"));
        assertEquals("30", result.get("${user.age}"));
    }

    @Test
    @DisplayName("Resolve variáveis com arrays")
    void testResolveArrayValues() {
        String template = "Itens: ${order.items}";
        String payload = """
            {
              "order": {
                "items": ["Livro", "Caneta", "Caderno"]
              }
            }
            """;

        Map<String, Object> result = extractor.execute(template, payload);

        assertEquals("Livro\nCaneta\nCaderno", result.get("${order.items}"));
    }

    @Test
    @DisplayName("Resolve variáveis com objetos")
    void testResolveObjectValues() {
        String template = "Endereço: ${user.address}";
        String payload = """
            {
              "user": {
                "address": {
                  "street": "Rua A",
                  "city": "São Paulo"
                }
              }
            }
            """;

        Map<String, Object> result = extractor.execute(template, payload);

        String json = (String) result.get("${user.address}");
        assertTrue(json.contains("Rua A"));
        assertTrue(json.contains("São Paulo"));
    }

    @Test
    @DisplayName("Retorna vazio para variáveis inexistentes")
    void testMissingVariableReturnsEmpty() {
        String template = "Olá ${user.name}, email: ${user.email}";
        String payload = """
            {
              "user": {
                "name": "Rennan"
              }
            }
            """;

        Map<String, Object> result = extractor.execute(template, payload);

        assertEquals("Rennan", result.get("${user.name}"));
        assertEquals("", result.get("${user.email}"));
    }

    @Test
    @DisplayName("Ignora caminhos inválidos no JSON")
    void testInvalidJsonPathReturnsEmpty() {
        String template = "Valor: ${invalid.path.here}";
        String payload = """
            {
              "valid": {
                "path": "ok"
              }
            }
            """;

        Map<String, Object> result = extractor.execute(template, payload);

        assertEquals("", result.get("${invalid.path.here}"));
    }

    @Test
    @DisplayName("Extrai variáveis únicas mesmo se repetidas")
    void testDuplicateVariablesAreExtractedOnce() {
        String template = "Olá ${user.name}, novamente ${user.name}";
        String payload = """
            {
              "user": {
                "name": "Rennan"
              }
            }
            """;

        Map<String, Object> result = extractor.execute(template, payload);

        assertEquals(1, result.size());
        assertEquals("Rennan", result.get("${user.name}"));
    }
}
