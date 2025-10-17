package com.example.pdfbox.service.impl;

import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("TextWrapper")
class TextWrapperTest {

    @Test
    @DisplayName("Retorna lista vazia para texto nulo ou em branco")
    void testEmptyOrNullText() {
        PDFont font = PDType1Font.HELVETICA;
        assertTrue(TextWrapper.wrapText(null, font, 12f, 100f).isEmpty());
        assertTrue(TextWrapper.wrapText("   ", font, 12f, 100f).isEmpty());
    }

    @Test
    @DisplayName("Mantém texto em uma linha quando cabe na largura")
    void testSingleLineWrap() {
        PDFont font = PDType1Font.HELVETICA;
        List<String> lines = TextWrapper.wrapText("Texto curto", font, 12f, 500f);
        assertEquals(1, lines.size());
        assertEquals("Texto curto", lines.get(0));
    }

    @Test
    @DisplayName("Divide texto em múltiplas linhas quando excede largura")
    void testMultiLineWrap() {
        PDFont font = PDType1Font.HELVETICA;
        String text = "Este é um texto que deve ser quebrado em várias linhas porque é muito longo";
        List<String> lines = TextWrapper.wrapText(text, font, 12f, 100f);
        assertTrue(lines.size() > 1);
    }

    @Test
    @DisplayName("Divide palavra longa que não cabe na linha")
    void testForceSplitLongWord() {
        PDFont font = PDType1Font.HELVETICA;
        String text = "Supercalifragilisticexpialidocious";
        List<String> lines = TextWrapper.wrapText(text, font, 12f, 50f);
        assertTrue(lines.size() >= 1);
        assertTrue(lines.get(0).length() < text.length());
    }

    @Test
    @DisplayName("Preserva quebras de parágrafo")
    void testParagraphsAreHandledSeparately() {
        PDFont font = PDType1Font.HELVETICA;
        String text = "Primeiro parágrafo\nSegundo parágrafo";
        List<String> lines = TextWrapper.wrapText(text, font, 12f, 500f);
        assertEquals(2, lines.size());
        assertEquals("Primeiro parágrafo", lines.get(0));
        assertEquals("Segundo parágrafo", lines.get(1));
    }

    @Test
    @DisplayName("Usa fallback de largura quando getStringWidth lança exceção")
    void testFallbackWidthCalculation() {
        PDFont font = mock(PDFont.class);
        try {
            when(font.getStringWidth(anyString())).thenThrow(new RuntimeException("Erro simulado"));
        } catch (Exception ignored) {}

        List<String> lines = TextWrapper.wrapText("Texto com erro", font, 10f, 50f);
        assertFalse(lines.isEmpty());
    }
    

    @Test
    @DisplayName("Deve usar fallback de largura ao dividir palavra quando getStringWidth lança exceção")
    void testForceSplitWordFallbackOnException() throws Exception {
        PDFont font = mock(PDFont.class);
        // Simula exceção para qualquer chamada
        when(font.getStringWidth(anyString())).thenThrow(new RuntimeException("Simulado"));

        String word = "PalavraMuitoLongaQueExcedeLimite";
        float fontSize = 10f;
        float maxWidth = 30f;

        List<String> result = TextWrapper.wrapText(word, font, fontSize, maxWidth);

        assertFalse(result.isEmpty());
        assertTrue(result.get(0).length() < word.length(), "A palavra deve ser dividida");
    }
}
