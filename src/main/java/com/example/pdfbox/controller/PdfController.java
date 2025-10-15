package com.example.pdfbox.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.pdfbox.service.impl.PdfServiceImpl;

@RestController
public class PdfController {

	@Autowired
	private PdfServiceImpl pdfService;

	
	@PostMapping("/api/pdf")
	public ResponseEntity<byte[]> fromTemplate() {
		try {
			byte[] pdf = pdfService.generateFromTemplateResource("templates/template.json", "relatorio.pdf");

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_PDF);
			headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=generated.pdf");
			return ResponseEntity.ok().headers(headers).body(pdf);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(500).body(("Erro gerando PDF: " + e.getMessage()).getBytes());
		}
	}
}
