package com.example.demoCarePlan.controller;

import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demoCarePlan.build.template.MapTemplateColunaHelper;
import com.example.demoCarePlan.dto.PdfRequestDTO;
import com.example.demoCarePlan.service.PdfService;

@RestController
public class PdfController {

	private final PdfService pdfService;
	private final MapTemplateColunaHelper mapperHelper;

	public PdfController(PdfService pdfService, MapTemplateColunaHelper mapperHelper) {
		this.pdfService = pdfService;
		this.mapperHelper = mapperHelper;
	}

	@PostMapping("/api/pdf")
	public ResponseEntity<byte[]> fromTemplate() {
		try {
			//mock do que viria do pub/sub
			Map<String, Object> dados = mapperHelper.gerarDados(new PdfRequestDTO(true));
			byte[] pdf = pdfService.generateFromTemplateResource("templates/template.json", dados, "relatorio.pdf");

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
