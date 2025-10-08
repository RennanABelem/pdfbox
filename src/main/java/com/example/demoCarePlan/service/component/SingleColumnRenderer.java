package com.example.demoCarePlan.service.component;

import java.util.List;

import org.apache.pdfbox.pdmodel.font.PDFont;

import com.example.demoCarePlan.service.FontManager;
import com.example.demoCarePlan.service.PdfDocumentBuilder;
import com.example.demoCarePlan.service.PdfDrawHelper;
import com.example.demoCarePlan.service.TextWrapper;
import com.fasterxml.jackson.databind.JsonNode;

public class SingleColumnRenderer {

	private final PdfDocumentBuilder builder;
	private final FontManager fontManager;
	private final float globalFontSize;

	public SingleColumnRenderer(PdfDocumentBuilder builder, FontManager fontManager, float globalFontSize) {
		this.builder = builder;
		this.fontManager = fontManager;
		this.globalFontSize = globalFontSize;
	}

	public void render(JsonNode node) throws Exception {
		String fontName = node.path("font").asText("DejaVuSans");
		PDFont font = fontManager.getFont(fontName);
		float fontSize = parseFloatOrDefault(node.path("font-size").asText(null), globalFontSize);
		float usableWidth = builder.getUsableWidth();
		float available = usableWidth - 4f;

		List<String> wrapped = TextWrapper.wrapText(node.path("text-value").asText(""), font, fontSize, available);

		for (String row : wrapped) {
			builder.ensureSpace(PdfConstants.BOTTOM_MARGIN);
			PdfDrawHelper.drawTextLine(builder.getContentStream(), row, PdfDocumentBuilder.DEFAULT_MARGIN_LEFT,
					builder.getCursorY(), font, fontSize, node.path("text-type").asText("left"), available);
			builder.moveCursorBy(-PdfConstants.DEFAULT_LEADING);
		}

		builder.ensureSpace(PdfConstants.BOTTOM_MARGIN);
	}

	private float parseFloatOrDefault(String value, float defaultValue) {
		try {
			if (value == null || value.isBlank())
				return defaultValue;
			return Float.parseFloat(value);
		} catch (Exception e) {
			return defaultValue;
		}
	}
}
