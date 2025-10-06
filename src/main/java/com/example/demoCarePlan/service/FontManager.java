package com.example.demoCarePlan.service;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

public class FontManager {

	private static final String DEFAULT_FONT = "DejaVuSans";
	private static final String FONT_PATH = "fonts/";
	private static final Logger logger = LoggerFactory.getLogger(FontManager.class);

	private final Map<String, PDFont> cache = new HashMap<>();
	private final PDDocument doc;

	public FontManager(PDDocument doc) {
		this.doc = doc;
	}

	public PDFont getFont(String fontName) {
		String effectiveName = (fontName == null || fontName.isBlank()) ? DEFAULT_FONT : fontName;
		return cache.computeIfAbsent(effectiveName, this::loadFontSafe);
	}

	private PDFont loadFontSafe(String fontName) {
		return tryLoadFont(fontName).orElseGet(() -> tryLoadFont(DEFAULT_FONT).orElse(null));
	}

	private Optional<PDFont> tryLoadFont(String fontName) {
		try (InputStream fis = new ClassPathResource(FONT_PATH + fontName + ".ttf").getInputStream()) {
			logger.debug("Carregando fonte '{}'.", fontName);
			return Optional.of(PDType0Font.load(doc, fis, true));
		} catch (Exception e) {
			logger.error("Erro ao carregar a fonte '{}': {}", fontName, e.getMessage());
			return Optional.empty();
		}
	}
}