package com.example.pdfbox.controller;

import com.example.pdfbox.service.impl.PdfServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PdfControllerTest {

    @Mock
    private PdfServiceImpl pdfService;

    @InjectMocks
    private PdfController pdfController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void fromTemplate_shouldReturnPdfResponse_whenServiceSucceeds() throws Exception {
        byte[] mockPdf = "PDF content".getBytes();
        when(pdfService.generateFromTemplateResource("templates/template.json", "relatorio.pdf"))
                .thenReturn(mockPdf);

        ResponseEntity<byte[]> response = pdfController.fromTemplate();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertArrayEquals(mockPdf, response.getBody());
        assertEquals(MediaType.APPLICATION_PDF, response.getHeaders().getContentType());
        assertEquals("attachment; filename=generated.pdf", response.getHeaders().getFirst(HttpHeaders.CONTENT_DISPOSITION));
    }

    @Test
    void fromTemplate_shouldReturnErrorResponse_whenServiceThrowsException() throws Exception {
        when(pdfService.generateFromTemplateResource("templates/template.json", "relatorio.pdf"))
                .thenThrow(new RuntimeException("Erro simulado"));

        ResponseEntity<byte[]> response = pdfController.fromTemplate();

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        String responseBody = new String(response.getBody());
        assertTrue(responseBody.contains("Erro gerando PDF: Erro simulado"));
    }
}

