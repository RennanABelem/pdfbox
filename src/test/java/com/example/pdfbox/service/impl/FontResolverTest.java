package com.example.pdfbox.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.junit.jupiter.api.Test;

public class FontResolverTest {

    @Test
    void shouldReturnHelveticaBoldWhenFontNameIsHelveticaBold() {
        PDFont font = FontResolver.resolve("helvetica_bold");
        assertEquals(PDType1Font.HELVETICA_BOLD, font);
    }

    @Test
    void shouldReturnHelveticaWhenFontNameIsUnknown() {
        PDFont font = FontResolver.resolve("unknown_font");
        assertEquals(PDType1Font.HELVETICA, font);
    }

    @Test
    void shouldIgnoreCaseInFontName() {
        PDFont font = FontResolver.resolve("HeLvEtIcA_BoLd");
        assertEquals(PDType1Font.HELVETICA_BOLD, font);
    }

}
