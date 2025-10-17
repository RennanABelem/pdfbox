package com.example.pdfbox.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Testes unitários para PdfDocumentBuilder")
class PdfDocumentBuilderTest {

    private PDDocument doc;
    private PdfDocumentBuilder builder;

    @BeforeEach
    void setUp() throws Exception {
        doc = new PDDocument();
        builder = new PdfDocumentBuilder(doc);
    }

    @Test
    @DisplayName("Inicialização deve criar página e posicionar cursor corretamente")
    void testInitialization() {
        // Then
        assertEquals(doc, builder.getDocument(), "Documento deve ser o mesmo passado no construtor");
        assertNotNull(builder.getCurrentPage(), "Página atual");
        assertNotNull(builder.getContentStream(), "ContentStream não deve ser nulo");

        float expectedY = PDRectangle.A4.getHeight() - PdfDocumentBuilder.MARGIN_TOP;
        assertEquals(expectedY, builder.getCursorY(), "Cursor Y deve estar na posição inicial correta");
    }

    @Test
    @DisplayName("Deve calcular corretamente a largura utilizável da página")
    void testGetUsableWidth() {
        // When
        float usableWidth = builder.getUsableWidth();

        // Then
        float expectedWidth = PDRectangle.A4.getWidth()
                - PdfDocumentBuilder.MARGIN_LEFT
                - PdfDocumentBuilder.MARGIN_RIGHT;

        assertEquals(expectedWidth, usableWidth, "Largura deve considerar as margens esquerda e direita");
    }

    @Test
    @DisplayName("Deve mover cursor sem adicionar nova página se houver espaço")
    void testMoveCursorWithoutPageChange() throws Exception {
        // Given
        float initialY = builder.getCursorY();

        // When
        builder.moveCursorBy(-10f);

        // Then
        assertEquals(initialY - 10f, builder.getCursorY(), "Cursor deve mover corretamente");
        assertEquals(1, doc.getNumberOfPages(), "Não deve adicionar nova página se houver espaço suficiente");
    }

    @Test
    @DisplayName("Deve adicionar nova página se cursor ultrapassar limite inferior")
    void testEnsureSpaceTriggersNewPage() throws Exception {
        // Given
        builder.setCursorY(40f); 

        // When
        builder.ensureSpace(PdfDocumentBuilder.MARGIN_BOTTOM);

        // Then
        assertEquals(2, doc.getNumberOfPages(), "Deve adicionar nova página ao ultrapassar limite inferior");
        float expectedY = PDRectangle.A4.getHeight() - PdfDocumentBuilder.MARGIN_TOP;
        assertEquals(expectedY, builder.getCursorY(), "Cursor deve ser reposicionado no topo da nova página");
    }
}
