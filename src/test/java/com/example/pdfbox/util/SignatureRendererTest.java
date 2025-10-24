package com.example.pdfbox.util;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.example.pdfbox.service.impl.PdfDocumentBuilder;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@DisplayName("SignatureRenderer")
class SignatureRendererTest {

    private PdfDocumentBuilder builder;
    private PDPageContentStream contentStream;
    private SignatureRenderer renderer;

    @BeforeEach
    void setUp() throws Exception {
        builder = mock(PdfDocumentBuilder.class);
        contentStream = mock(PDPageContentStream.class);
        when(builder.getContentStream()).thenReturn(contentStream);
        renderer = new SignatureRenderer(builder, 12f);
    }

    @Test
    @DisplayName("Renderiza com valores completos e fonte helvetica_bold")
    void testRenderWithExplicitValues() throws Exception {
        JsonNode element = new ObjectMapper().readTree("""
            {
              "text": "Assinatura",
              "width": 200,
              "font": "helvetica_bold",
              "font-size": 14,
              "axis-x": 50
            }
            """);

        renderer.render(element);

        verify(contentStream).setFont(PDType1Font.HELVETICA_BOLD, 14f);
        verify(contentStream).showText("Assinatura");
    }

    @Test
    @DisplayName("Renderiza com campos ausentes usando valores padrão")
    void testRenderWithMissingFieldsUsesDefaults() throws Exception {
        JsonNode element = new ObjectMapper().readTree("{}");

        renderer.render(element);

        verify(contentStream).setFont(PDType1Font.HELVETICA, 12f);
        verify(contentStream).showText("");
    }

    @Test
    @DisplayName("Renderiza com fonte desconhecida usando fonte padrão helvetica")
    void testRenderWithUnknownFontFallsBackToDefault() throws Exception {
        JsonNode element = new ObjectMapper().readTree("""
            {
              "text": "Assinatura",
              "font": "Font_invalida"
            }
            """);

        renderer.render(element);

        verify(contentStream).setFont(PDType1Font.HELVETICA, 12f);
        verify(contentStream).showText("Assinatura");
    }
}
