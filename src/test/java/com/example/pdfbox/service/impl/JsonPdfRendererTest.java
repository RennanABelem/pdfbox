package com.example.pdfbox.service.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;

import com.example.pdfbox.util.MultiColumnRenderer;
import com.example.pdfbox.util.SignatureRenderer;
import com.example.pdfbox.util.SingleColumnRenderer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@DisplayName("JsonPdfRenderer")
class JsonPdfRendererTest {

	private final ObjectMapper mapper = new ObjectMapper();
	private final JsonPdfRenderer renderer = new JsonPdfRenderer();

	@Test
	@DisplayName("Renderiza documento com linha do tipo 'blank'")
	void testRenderWithBlankLine() throws Exception {
		JsonNode root = mapper.readTree("""
				    {
				      "lines": [
				        { "type": "blank" }
				      ]
				    }
				""");

		try (MockedConstruction<PdfDocumentBuilder> builderMock = mockConstruction(PdfDocumentBuilder.class,
				(mock, context) -> {
					PDPageContentStream contentStream = mock(PDPageContentStream.class);
					when(mock.getContentStream()).thenReturn(contentStream);
				})) {

			renderer.render(root);

			PdfDocumentBuilder builder = builderMock.constructed().get(0);
			verify(builder).moveCursorBy(-PdfDocumentBuilder.DEFAULT_LEADING);
			verify(builder).ensureSpace(PdfDocumentBuilder.MARGIN_BOTTOM);
		}
	}

	@Test
	@DisplayName("Renderiza linha do tipo 'multi-column'")
	void testRenderWithMultiColumnLine() throws Exception {
		JsonNode root = mapper.readTree("""
				    {
				      "lines": [
				        { "type": "multi-column", "fields": [] }
				      ]
				    }
				""");

		try (MockedConstruction<MultiColumnRenderer> multiMock = mockConstruction(MultiColumnRenderer.class)) {
			renderer.render(root);

			MultiColumnRenderer multiRenderer = multiMock.constructed().get(0);
			verify(multiRenderer).render(any(JsonNode.class));
		}
	}

	@Test
	@DisplayName("Renderiza linha do tipo 'signature'")
	void testRenderWithSignatureLine() throws Exception {
		JsonNode root = mapper.readTree("""
				    {
				      "lines": [
				        { "type": "signature", "text": "Assinatura" }
				      ]
				    }
				""");

		try (MockedConstruction<SignatureRenderer> sigMock = mockConstruction(SignatureRenderer.class)) {
			renderer.render(root);

			SignatureRenderer signatureRenderer = sigMock.constructed().get(0);
			verify(signatureRenderer).render(any(JsonNode.class));
		}
	}

	@Test
	@DisplayName("Renderiza linha padrão como 'single-column'")
	void testRenderWithDefaultLineType() throws Exception {
		JsonNode root = mapper.readTree("""
				    {
				      "lines": [
				        { "text-value": "Texto padrão" }
				      ]
				    }
				""");

		try (MockedConstruction<SingleColumnRenderer> singleMock = mockConstruction(SingleColumnRenderer.class)) {
			renderer.render(root);

			SingleColumnRenderer singleRenderer = singleMock.constructed().get(0);
			verify(singleRenderer).render(any(JsonNode.class));
		}
	}

	@Test
	@DisplayName("Ignora campo 'lines' inválido e não lança exceção")
	void testRenderWithInvalidLinesField() throws Exception {
		JsonNode root = mapper.readTree("""
				    {
				      "lines": { "type": "blank" }
				    }
				""");

		assertDoesNotThrow(() -> renderer.render(root));
	}

	@Test
	@DisplayName("Gera PDF como array de bytes")
	void testRenderReturnsByteArray() throws Exception {
		JsonNode root = mapper.readTree("""
				    {
				      "lines": []
				    }
				""");

		byte[] result = renderer.render(root);
		assertNotNull(result);
		assertTrue(result.length > 0);
	}

	@Test
	@DisplayName("Usa font-size padrão quando valor é inválido")
	void testParseFloatOrDefaultFallback() throws Exception {
		JsonNode root = mapper.readTree("""
				    {
				      "font-size": "abc",
				      "lines": [
				        { "text-value": "Texto qualquer" }
				      ]
				    }
				""");

		try (MockedConstruction<PdfDocumentBuilder> builderMock = mockConstruction(PdfDocumentBuilder.class,
				(mock, context) -> {
					when(mock.getContentStream()).thenReturn(mock(PDPageContentStream.class));
				});
				MockedConstruction<SingleColumnRenderer> singleMock = mockConstruction(SingleColumnRenderer.class)) {
			renderer.render(root);

			SingleColumnRenderer singleRenderer = singleMock.constructed().get(0);
			verify(singleRenderer).render(any(JsonNode.class));
		}
	}
	
}
