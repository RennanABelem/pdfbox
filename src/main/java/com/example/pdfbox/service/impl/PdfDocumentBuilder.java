package com.example.pdfbox.service.impl;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;

public class PdfDocumentBuilder {

    public static final float MARGIN_LEFT = 40f;
    public static final float MARGIN_RIGHT = 40f;
    public static final float MARGIN_TOP = 50f;
    public static final float MARGIN_BOTTOM = 50f;
    
	public static final float DEFAULT_LEADING = 19f;
	public static final float DEFAULT_FONT_SIZE = 10f;

    private final PDDocument document;
    private PDPage currentPage;
    private PDPageContentStream contentStream;
    private float cursorY;

    public PdfDocumentBuilder(PDDocument document) throws Exception {
        this.document = document;
        addNewPage();
    }

    public PDDocument getDocument() {
        return document;
    }

    public PDPage getCurrentPage() {
        return currentPage;
    }

    public PDPageContentStream getContentStream() {
        return contentStream;
    }

    public float getCursorY() {
        return cursorY;
    }

    public void setCursorY(float cursorY) {
        this.cursorY = cursorY;
    }

    public float getUsableWidth() {
        PDRectangle mediaBox = currentPage.getMediaBox();
        return mediaBox.getWidth() - MARGIN_LEFT - MARGIN_RIGHT;
    }

    public void moveCursorBy(float deltaY) throws Exception {
        this.cursorY += deltaY;
        ensureSpace(MARGIN_BOTTOM);
    }

    public void ensureSpace(float minY) throws Exception {
        if (cursorY < minY) {
            addNewPage();
        }
    }

    private void addNewPage() throws Exception {
        closeContentStreamIfOpen();

        currentPage = new PDPage(PDRectangle.A4);
        document.addPage(currentPage);
        contentStream = new PDPageContentStream(document, currentPage);
        cursorY = currentPage.getMediaBox().getHeight() - MARGIN_TOP;
    }

    private void closeContentStreamIfOpen() {
        if (contentStream != null) {
            try {
                contentStream.close();
            } catch (Exception ignored) {
            }
        }
    }
}
