package com.example.demoCarePlan.service;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;

public class PdfDocumentBuilder {

	public static final float DEFAULT_MARGIN_LEFT = 40f;
	public static final float DEFAULT_MARGIN_RIGHT = 40f;
	public static final float TOP_MARGIN = 50f;
	public static final float BOTTOM_MARGIN = 50f;

	private final PDDocument doc;
	private PDPage page;
	private PDPageContentStream cs;
	private float cursorY;

	public PdfDocumentBuilder(PDDocument doc) throws Exception {
		this.doc = doc;
		addNewPage();
	}

	public PDPageContentStream getContentStream() {
		return cs;
	}

	public float getCursorY() {
		return cursorY;
	}

	public void setCursorY(float cursorY) {
		this.cursorY = cursorY;
	}

	public PDPage getPage() {
		return page;
	}

	public float getUsableWidth() {
		PDRectangle media = page.getMediaBox();
		return media.getWidth() - DEFAULT_MARGIN_LEFT - DEFAULT_MARGIN_RIGHT;
	}

	public void ensureSpace(float minY) throws Exception {
		if (cursorY < minY)
			addNewPage();
	}

	public void moveCursorBy(float delta) throws Exception {
		this.cursorY += delta;
		ensureSpace(BOTTOM_MARGIN);
	}

	private void addNewPage() throws Exception {
		if (cs != null) {
			try {
				cs.close();
			} catch (Exception ignored) {
			}
		}
		page = new PDPage(PDRectangle.A4);
		doc.addPage(page);
		cs = new PDPageContentStream(doc, page);
		cursorY = page.getMediaBox().getHeight() - TOP_MARGIN;
	}
}