package com.example.pdfbox.util;

import com.example.pdfbox.service.impl.PdfDocumentBuilder;
import com.example.pdfbox.service.impl.PdfDrawHelper;
import com.example.pdfbox.service.impl.TextWrapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.MockedStatic;

import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("MultiColumnRenderer")
class MultiColumnRendererTest {

    @Mock
    PdfDocumentBuilder builder;

    @Nested
    @DisplayName("Renderização básica")
    class BasicRendering {

        @Test
        @DisplayName("Ignora renderização quando fields está vazio")
        void testEmptyFields() throws Exception {
            JsonNode block = new ObjectMapper().readTree("{\"fields\": []}");

            new MultiColumnRenderer(builder, 12f).render(block);

            verify(builder).moveCursorBy(-PdfDocumentBuilder.DEFAULT_LEADING);
            verify(builder).ensureSpace(PdfDocumentBuilder.MARGIN_BOTTOM);
        }

        @Test
        @DisplayName("Ignora campo do tipo 'blank'")
        void testFieldTypeBlankIsIgnored() throws Exception {
            JsonNode block = new ObjectMapper().readTree("""
                {
                  "fields": [
                    { "type": "blank" },
                    { "text-value": "Texto visível" }
                  ]
                }
                """);

            try (MockedStatic<TextWrapper> wrapper = mockStatic(TextWrapper.class);
                 MockedStatic<PdfDrawHelper> draw = mockStatic(PdfDrawHelper.class)) {

                wrapper.when(() -> TextWrapper.wrapText(any(), any(), anyFloat(), anyFloat()))
                       .thenReturn(List.of("Texto visível"));

                new MultiColumnRenderer(builder, 12f).render(block);

                draw.verify(() -> PdfDrawHelper.drawTextLine(
                    any(), eq("Texto visível"), anyFloat(), anyFloat(), any(), anyFloat(), anyString(), anyFloat()
                ), atLeastOnce());
            }
        }
    }

    @Nested
    @DisplayName("Fontes e tamanhos")
    class FontHandling {

        @Test
        @DisplayName("Usa fonte helvetica_bold corretamente")
        void testFontHelveticaBold() throws Exception {
            JsonNode block = new ObjectMapper().readTree("""
                {
                  "fields": [{ "text-value": "Texto", "font": "helvetica_bold" }]
                }
                """);

            try (MockedStatic<TextWrapper> wrapper = mockStatic(TextWrapper.class);
                 MockedStatic<PdfDrawHelper> draw = mockStatic(PdfDrawHelper.class)) {

                wrapper.when(() -> TextWrapper.wrapText(any(), any(), anyFloat(), anyFloat()))
                       .thenReturn(List.of("Texto"));

                new MultiColumnRenderer(builder, 12f).render(block);

                draw.verify(() -> PdfDrawHelper.drawTextLine(
                    any(), eq("Texto"), anyFloat(), anyFloat(),
                    eq(PDType1Font.HELVETICA_BOLD), anyFloat(), anyString(), anyFloat()
                ));
            }
        }

        @Test
        @DisplayName("Usa fonte padrão helvetica quando fonte é desconhecida")
        void testUnknownFontFallsBackToDefault() throws Exception {
            JsonNode block = new ObjectMapper().readTree("""
                {
                  "fields": [{ "text-value": "Texto", "font": "fonte_invalida" }]
                }
                """);

            try (MockedStatic<TextWrapper> wrapper = mockStatic(TextWrapper.class);
                 MockedStatic<PdfDrawHelper> draw = mockStatic(PdfDrawHelper.class)) {

                wrapper.when(() -> TextWrapper.wrapText(any(), any(), anyFloat(), anyFloat()))
                       .thenReturn(List.of("Texto"));

                new MultiColumnRenderer(builder, 12f).render(block);

                draw.verify(() -> PdfDrawHelper.drawTextLine(
                    any(), eq("Texto"), anyFloat(), anyFloat(),
                    eq(PDType1Font.HELVETICA), anyFloat(), anyString(), anyFloat()
                ));
            }
        }

        @Test
        @DisplayName("Usa valor padrão quando font-size é inválido")
        void testInvalidFontSizeFallsBackToDefault() throws Exception {
            JsonNode block = new ObjectMapper().readTree("""
                {
                  "fields": [{ "text-value": "Texto", "font-size": "abc" }]
                }
                """);

            try (MockedStatic<TextWrapper> wrapper = mockStatic(TextWrapper.class);
                 MockedStatic<PdfDrawHelper> draw = mockStatic(PdfDrawHelper.class)) {

                wrapper.when(() -> TextWrapper.wrapText(any(), any(), anyFloat(), anyFloat()))
                       .thenReturn(List.of("Texto"));

                new MultiColumnRenderer(builder, 12f).render(block);

                draw.verify(() -> PdfDrawHelper.drawTextLine(
                    any(), eq("Texto"), anyFloat(), anyFloat(), any(), eq(12f), anyString(), anyFloat()
                ));
            }
        }
    }

    @Nested
    @DisplayName("Posicionamento")
    class Positioning {

        @Test
        @DisplayName("Usa axis-x quando presente")
        void testAxisXOverride() throws Exception {
            JsonNode block = new ObjectMapper().readTree("""
                {
                  "fields": [{ "text-value": "Texto", "axis-x": "100" }]
                }
                """);

            try (MockedStatic<TextWrapper> wrapper = mockStatic(TextWrapper.class);
                 MockedStatic<PdfDrawHelper> draw = mockStatic(PdfDrawHelper.class)) {

                wrapper.when(() -> TextWrapper.wrapText(any(), any(), anyFloat(), anyFloat()))
                       .thenReturn(List.of("Texto"));

                new MultiColumnRenderer(builder, 12f).render(block);

                draw.verify(() -> PdfDrawHelper.drawTextLine(
                    any(), eq("Texto"), anyFloat(), anyFloat(), any(), anyFloat(), anyString(), anyFloat()
                ));
            }
        }
    }
}
