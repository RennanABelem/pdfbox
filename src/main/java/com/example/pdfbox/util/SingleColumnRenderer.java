package com.example.pdfbox.util;

import java.util.List;

import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import com.example.pdfbox.service.impl.PdfDocumentBuilder;
import com.example.pdfbox.service.impl.PdfDrawHelper;
import com.example.pdfbox.service.impl.TextWrapper;
import com.fasterxml.jackson.databind.JsonNode;

public class SingleColumnRenderer {

	private final PdfDocumentBuilder builder;
	private final float defaultFontSize;

	public SingleColumnRenderer(PdfDocumentBuilder builder, float defaultFontSize) {
		this.builder = builder;
		this.defaultFontSize = defaultFontSize;
	}

	public void render(JsonNode node) throws Exception {
		PDFont font = resolveFont(node.path("font").asText("Helvetica"));
		float fontSize = parseFloatOrDefault(node.path("font-size").asText(null), defaultFontSize);

		float usableWidth = builder.getUsableWidth();
		float marginLeft = PdfDocumentBuilder.MARGIN_LEFT;

		float xPosition = calculateXPosition(node, marginLeft);
		float availableWidth = usableWidth - (xPosition - marginLeft) - 4f;

		List<String> lines = TextWrapper.wrapText(node.path("text-value").asText(""), font, fontSize, availableWidth);

		for (String line : lines) {
			builder.ensureSpace(PdfDocumentBuilder.MARGIN_BOTTOM);
			PdfDrawHelper.drawTextLine(builder.getContentStream(), line, xPosition, builder.getCursorY(), font,
					fontSize, node.path("text-type").asText("left"), availableWidth);
			builder.moveCursorBy(-PdfDocumentBuilder.DEFAULT_LEADING);
		}

		builder.ensureSpace(PdfDocumentBuilder.MARGIN_BOTTOM);
	}

	private float calculateXPosition(JsonNode node, float marginLeft) {
		if (node.has("axis-x") && !node.path("axis-x").asText("").isBlank()) {
			return marginLeft + parseFloatOrDefault(node.path("axis-x").asText(null), 0f);
		}
		return marginLeft;
	}

	private float parseFloatOrDefault(String value, float defaultValue) {
		try {
			return (value == null || value.isBlank()) ? defaultValue : Float.parseFloat(value);
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}

	private PDFont resolveFont(String fontName) {
		return switch (fontName.toLowerCase()) {
		case "helvetica_bold" -> PDType1Font.HELVETICA_BOLD;
		case "helvetica" -> PDType1Font.HELVETICA;
		default -> PDType1Font.HELVETICA;
		};
	}
}
