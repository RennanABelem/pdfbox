package com.example.demoCarePlan.service;

import java.io.ByteArrayOutputStream;

import org.apache.pdfbox.pdmodel.PDDocument;

import com.example.demoCarePlan.service.component.ListRenderer;
import com.example.demoCarePlan.service.component.MultiColumnRenderer;
import com.example.demoCarePlan.service.component.PdfConstants;
import com.example.demoCarePlan.service.component.SignatureRenderer;
import com.example.demoCarePlan.service.component.SingleColumnRenderer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;

public class JsonPdfRenderer {

	public byte[] render(JsonNode root) throws Exception {
		try (PDDocument doc = new PDDocument()) {
			PdfDocumentBuilder builder = new PdfDocumentBuilder(doc);
			FontManager fontManager = new FontManager(doc);

			float globalFontSize = parseFloatOrDefault(root.path("font-size").asText(null),
					PdfConstants.DEFAULT_FONT_SIZE);

			JsonNode lines = root.path("lines");
			if (!lines.isArray())
				lines = JsonNodeFactory.instance.arrayNode();

			SingleColumnRenderer singleRenderer = new SingleColumnRenderer(builder, fontManager, globalFontSize);
			MultiColumnRenderer multiRenderer = new MultiColumnRenderer(builder, fontManager, globalFontSize);
			SignatureRenderer signatureRenderer = new SignatureRenderer(builder, fontManager, globalFontSize);
			ListRenderer listRenderer = new ListRenderer(builder, fontManager, globalFontSize);

			for (JsonNode line : lines) {
				String type = line.path("type").asText("");

				if ("blank".equalsIgnoreCase(type)) {
					builder.moveCursorBy(-PdfConstants.DEFAULT_LEADING);
					builder.ensureSpace(PdfConstants.BOTTOM_MARGIN);
					continue;
				}

				if ("multi-column".equalsIgnoreCase(type)) {
					multiRenderer.render(line);
					continue;
				}
				
				if ("signature".equalsIgnoreCase(type)) {
				    signatureRenderer.render(line);
				    continue;
				}
				
				if ("list".equalsIgnoreCase(type) || "multi-column-list".equalsIgnoreCase(type)) {
					listRenderer.render(line, root);
					continue;
				}

				singleRenderer.render(line);
			}

			builder.getContentStream().close();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			doc.save(baos);
			return baos.toByteArray();
		}
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