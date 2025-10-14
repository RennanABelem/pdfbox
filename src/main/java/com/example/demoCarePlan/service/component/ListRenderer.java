package com.example.demoCarePlan.service.component;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.example.demoCarePlan.service.PdfDocumentBuilder;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class ListRenderer {

	private final MultiColumnRenderer multiRenderer;

	public ListRenderer(PdfDocumentBuilder builder, float globalFontSize) {
		this.multiRenderer = new MultiColumnRenderer(builder, globalFontSize);
	}

	public void render(JsonNode line, JsonNode root) throws Exception {
		List<ObjectNode> expandedLines = expandLine(line, root);
		if (expandedLines.isEmpty()) {
			multiRenderer.render(line);
			return;
		}

		for (ObjectNode newLine : expandedLines) {
			multiRenderer.render(newLine);
		}
	}

	private List<ObjectNode> expandLine(JsonNode line, JsonNode root) {
		List<ObjectNode> result = new ArrayList<>();
		JsonNode fields = line.path("fields");
		if (!fields.isArray())
			return result;

		int fieldCount = fields.size();
		List<List<String>> listsPerField = new ArrayList<>(fieldCount);
		int maxItems = 0;

		// Extrai os arrays de itens
		for (int i = 0; i < fieldCount; i++) {
			JsonNode f = fields.get(i);
			List<String> items = new ArrayList<>();

			if (f.has("list")) {
				JsonNode itemsNode = f.path("list").path("items");
				if (itemsNode.isArray()) {
					for (JsonNode it : itemsNode)
						items.add(it.asText(""));
				}
			} else if (f.has("list-ref")) {
				String ref = f.path("list-ref").asText("");
				JsonNode dataArray = root.path("data").path(ref);
				if (dataArray.isArray()) {
					for (JsonNode it : dataArray)
						items.add(it.asText(""));
				}
			}

			listsPerField.add(items);
			if (items.size() > maxItems)
				maxItems = items.size();
		}

		if (maxItems == 0)
			return result;

		for (int index = 0; index < maxItems; index++) {
			ObjectNode newLine = JsonNodeFactory.instance.objectNode();

			Iterator<String> names = line.fieldNames();
			while (names.hasNext()) {
				String name = names.next();
				if (!"fields".equals(name)) {
					newLine.set(name, line.get(name).deepCopy());
				}
			}

			ArrayNode newFields = JsonNodeFactory.instance.arrayNode();
			for (int fIdx = 0; fIdx < fieldCount; fIdx++) {
				JsonNode originalField = fields.get(fIdx);
				ObjectNode copied = originalField.deepCopy();

				List<String> items = listsPerField.get(fIdx);
				if (!items.isEmpty()) {
					String text = index < items.size() ? items.get(index) : "";
					copied.put("text-value", text);
					copied.remove("list");
					copied.remove("list-ref");
				}
				newFields.add(copied);
			}

			newLine.set("fields", newFields);
			result.add(newLine);
		}

		return result;
	}
}
