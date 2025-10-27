package com.example.pdfbox.service.impl;

import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

public class FontResolver {

	private FontResolver() {
	}

	public static PDFont resolve(String fontName) {
		return switch (fontName.toLowerCase()) {
		case "helvetica_bold" -> PDType1Font.HELVETICA_BOLD;
		default -> PDType1Font.HELVETICA;
		};
	}

}
