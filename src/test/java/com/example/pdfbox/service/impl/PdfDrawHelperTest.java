package com.example.pdfbox.service.impl;

import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PdfDrawHelperTest {

    @Nested
    @DisplayName("Método Alignment.from")
    class AlignmentTests {

        @Test
        @DisplayName("Deve retornar LEFT para null")
        void testNullReturnsLeft() {
            assertEquals(PdfDrawHelper.Alignment.LEFT, PdfDrawHelper.Alignment.from(null));
        }

        @Test
        @DisplayName("Deve retornar LEFT para valor desconhecido")
        void testUnknownReturnsLeft() {
            assertEquals(PdfDrawHelper.Alignment.LEFT, PdfDrawHelper.Alignment.from("qualquer"));
        }

        @Test
        @DisplayName("Deve retornar CENTER para 'center'")
        void testCenter() {
            assertEquals(PdfDrawHelper.Alignment.CENTER, PdfDrawHelper.Alignment.from("center"));
        }

        @Test
        @DisplayName("Deve retornar RIGHT para 'right'")
        void testRight() {
            assertEquals(PdfDrawHelper.Alignment.RIGHT, PdfDrawHelper.Alignment.from("right"));
        }
    }

    @Nested
    @DisplayName("Método drawTextLine")
    class DrawTextLineTests {

        @Test
        @DisplayName("Deve desenhar texto com alinhamento LEFT")
        void testDrawLeft() throws IOException {
            PDPageContentStream cs = mock(PDPageContentStream.class);

            PdfDrawHelper.drawTextLine(cs, "Texto", 10f, 700f, PDType1Font.HELVETICA, 12f, "left", 200f);

            verify(cs).beginText();
            verify(cs).setFont(PDType1Font.HELVETICA, 12f);
            verify(cs).newLineAtOffset(anyFloat(), eq(700f));
            verify(cs).showText("Texto");
            verify(cs).endText();
        }

        @Test
        @DisplayName("Deve desenhar texto com alinhamento CENTER")
        void testDrawCenter() throws IOException {
            PDPageContentStream cs = mock(PDPageContentStream.class);

            String text = "Centralizado";
            float fontSize = 10f;
            float boxWidth = 100f;
            float x = 50f;
            float y = 700f;

            float textWidth = PDType1Font.HELVETICA.getStringWidth(text) / 1000f * fontSize;
            float expectedX = x + (boxWidth - textWidth) / 2f;

            PdfDrawHelper.drawTextLine(cs, text, x, y, PDType1Font.HELVETICA, fontSize, "center", boxWidth);

            verify(cs).newLineAtOffset(eq(expectedX), eq(y));
        }

        @Test
        @DisplayName("Deve desenhar texto com alinhamento RIGHT")
        void testDrawRight() throws IOException {
            PDPageContentStream cs = mock(PDPageContentStream.class);

            String text = "Direita";
            float fontSize = 12f;
            float boxWidth = 120f;
            float x = 40f;
            float y = 650f;

            float textWidth = PDType1Font.HELVETICA.getStringWidth(text) / 1000f * fontSize;
            float expectedX = x + (boxWidth - textWidth);

            PdfDrawHelper.drawTextLine(cs, text, x, y, PDType1Font.HELVETICA, fontSize, "right", boxWidth);

            verify(cs).newLineAtOffset(eq(expectedX), eq(y));
        }

        @Test
        @DisplayName("Deve ignorar chamada quando texto é nulo ou vazio")
        void testDrawTextLineWithEmptyText() throws IOException {
            PDPageContentStream cs = mock(PDPageContentStream.class);

            PdfDrawHelper.drawTextLine(cs, "   ", 50f, 700f, PDType1Font.HELVETICA, 12f, "left", 200f);

            verifyNoInteractions(cs);
        }

        @Test
        @DisplayName("Deve ignorar chamada quando font ou cs são nulos")
        void testDrawTextLineWithNullArgs() throws IOException {
            PdfDrawHelper.drawTextLine(null, "Texto", 50f, 700f, PDType1Font.HELVETICA, 12f, "left", 200f);
            PdfDrawHelper.drawTextLine(mock(PDPageContentStream.class), "Texto", 50f, 700f, null, 12f, "left", 200f);
        }
    }
}
