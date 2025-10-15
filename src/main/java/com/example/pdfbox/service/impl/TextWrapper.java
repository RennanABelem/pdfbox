package com.example.pdfbox.service.impl;

import org.apache.pdfbox.pdmodel.font.PDFont;

import java.util.ArrayList;
import java.util.List;

public class TextWrapper {

	public static List<String> wrapText(String text, PDFont font, float fontSize, float maxWidth) {
		List<String> lines = new ArrayList<>();
		if (text == null || text.isBlank())
			return lines;

		// Divide o texto em parágrafos por quebra de linha
		String[] paragraphs = text.split("\\r?\\n");

		for (String para : paragraphs) {
			String[] words = para.split("\\s+");
			StringBuilder line = new StringBuilder();

			for (String word : words) {
				String testLine = line.length() == 0 ? word : line + " " + word;
				float width = calcWidth(font, fontSize, testLine);

				if (width <= maxWidth) {
					if (line.length() > 0)
						line.append(" ");
					line.append(word);
				} else {
					if (line.length() > 0) {
						lines.add(line.toString());
						line = new StringBuilder(word);
					} else {
						String part = forceSplitWord(word, font, fontSize, maxWidth);
						lines.add(part);
						String rest = word.substring(part.length());
						line = new StringBuilder(rest);
					}
				}
			}

			if (line.length() > 0) {
				lines.add(line.toString());
			}
		}

		return lines;
	}

	private static float calcWidth(PDFont font, float fontSize, String text) {
		try {
			return font.getStringWidth(text) / 1000f * fontSize;
		} catch (Exception e) {
			return text.length() * fontSize * 0.5f; // fallback estimado
		}
	}

	private static String forceSplitWord(String word, PDFont font, float fontSize, float maxWidth) {
		StringBuilder sb = new StringBuilder();

		for (char c : word.toCharArray()) {
			sb.append(c);
			float width;
			try {
				width = font.getStringWidth(sb.toString()) / 1000f * fontSize;
			} catch (Exception e) {
				width = sb.length() * fontSize * 0.5f;
			}

			if (width > maxWidth) {
				sb.setLength(sb.length() - 1); 
				break;
			}
		}

		return sb.toString();
	}
}