package com.example.pdfbox.util;

import com.example.pdfbox.service.impl.PdfDocumentBuilder;
import com.example.pdfbox.service.impl.PdfDrawHelper;
import com.example.pdfbox.service.impl.TextWrapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.util.List;

import static org.mockito.Mockito.*;

@DisplayName("SingleColumnRenderer")
class SingleColumnRendererTest {

    private PdfDocumentBuilder builder;
    private PDPageContentStream contentStream;

    @BeforeEach
    void setUp() {
        builder = mock(PdfDocumentBuilder.class);
        contentStream = mock(PDPageContentStream.class);
        when(builder.getContentStream()).thenReturn(contentStream);
    }

    @Nested
    @DisplayName("Renderização de texto")
    class TextRendering {

        @Test
        @DisplayName("Renderiza texto básico com fonte e tamanho definidos")
        void testRenderBasicText() throws Exception {
            JsonNode node = new ObjectMapper().readTree("""
                {
                  "text-value": "Texto de teste",
                  "font": "helvetica_bold",
                  "font-size": "14"
                }
                """);

            try (MockedStatic<TextWrapper> wrapperMock = mockStatic(TextWrapper.class);
                 MockedStatic<PdfDrawHelper> drawMock = mockStatic(PdfDrawHelper.class)) {

                wrapperMock.when(() -> TextWrapper.wrapText(any(), any(), anyFloat(), anyFloat()))
                           .thenReturn(List.of("Texto de teste"));

                new SingleColumnRenderer(builder, 12f).render(node);

                drawMock.verify(() -> PdfDrawHelper.drawTextLine(
                    any(), eq("Texto de teste"), anyFloat(), anyFloat(),
                    eq(PDType1Font.HELVETICA_BOLD), eq(14f), eq("left"), anyFloat()
                ));
            }
        }

        @Test
        @DisplayName("Renderiza texto com deslocamento via axis-x")
        void testRenderWithAxisXOffset() throws Exception {
            JsonNode node = new ObjectMapper().readTree("""
                {
                  "text-value": "Texto com deslocamento",
                  "font": "helvetica",
                  "axis-x": "30"
                }
                """);

            try (MockedStatic<TextWrapper> wrapperMock = mockStatic(TextWrapper.class);
                 MockedStatic<PdfDrawHelper> drawMock = mockStatic(PdfDrawHelper.class)) {

                wrapperMock.when(() -> TextWrapper.wrapText(any(), any(), anyFloat(), anyFloat()))
                           .thenReturn(List.of("Texto com deslocamento"));

                new SingleColumnRenderer(builder, 12f).render(node);

                drawMock.verify(() -> PdfDrawHelper.drawTextLine(
                    any(), eq("Texto com deslocamento"), anyFloat(), anyFloat(),
                    eq(PDType1Font.HELVETICA), eq(12f), eq("left"), anyFloat()
                ));
            }
        }
    }

    @Nested
    @DisplayName("Tratamento de fontes")
    class FontHandling {

        @Test
        @DisplayName("Usa fonte padrão quando fonte é desconhecida")
        void testRenderWithUnknownFontUsesDefault() throws Exception {
            JsonNode node = new ObjectMapper().readTree("""
                {
                  "text-value": "Texto com fonte desconhecida",
                  "font": "Fonte_invalida"
                }
                """);

            try (MockedStatic<TextWrapper> wrapperMock = mockStatic(TextWrapper.class);
                 MockedStatic<PdfDrawHelper> drawMock = mockStatic(PdfDrawHelper.class)) {

                wrapperMock.when(() -> TextWrapper.wrapText(any(), any(), anyFloat(), anyFloat()))
                           .thenReturn(List.of("Texto com fonte desconhecida"));

                new SingleColumnRenderer(builder, 12f).render(node);

                drawMock.verify(() -> PdfDrawHelper.drawTextLine(
                    any(), eq("Texto com fonte desconhecida"), anyFloat(), anyFloat(),
                    eq(PDType1Font.HELVETICA), eq(12f), eq("left"), anyFloat()
                ));
            }
        }

        @Test
        @DisplayName("Usa valor padrão quando font-size é inválido")
        void testRenderWithInvalidFontSizeFallsBackToDefault() throws Exception {
            JsonNode node = new ObjectMapper().readTree("""
                {
                  "text-value": "Texto com font-size inválido",
                  "font-size": "abc"
                }
                """);

            try (MockedStatic<TextWrapper> wrapperMock = mockStatic(TextWrapper.class);
                 MockedStatic<PdfDrawHelper> drawMock = mockStatic(PdfDrawHelper.class)) {

                wrapperMock.when(() -> TextWrapper.wrapText(any(), any(), anyFloat(), anyFloat()))
                           .thenReturn(List.of("Texto com font-size inválido"));

                new SingleColumnRenderer(builder, 12f).render(node);

                drawMock.verify(() -> PdfDrawHelper.drawTextLine(
                    any(), eq("Texto com font-size inválido"), anyFloat(), anyFloat(),
                    any(), eq(12f), anyString(), anyFloat()
                ));
            }
        }
    }
}
