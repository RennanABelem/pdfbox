package com.example.pdfbox.service.impl;

import com.google.gson.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExtractVariables {

	private final Gson gson = new Gson();
	private final Pattern VARIABLE_PATTERN = Pattern.compile("\\$\\{(.*?)}");

	public Map<String, Object> execute(String template, String payload) {
		List<String> variables = extractPlaceholders(template);
		JsonElement jsonRoot = gson.fromJson(payload, JsonElement.class);
		return resolveVariables(variables, jsonRoot);
	}

	private List<String> extractPlaceholders(String template) {
		Matcher matcher = VARIABLE_PATTERN.matcher(template);
		Set<String> uniqueVariables = new LinkedHashSet<>();

		while (matcher.find()) {
			uniqueVariables.add(matcher.group());
		}

		return new ArrayList<>(uniqueVariables);
	}

	private Map<String, Object> resolveVariables(List<String> variables, JsonElement root) {
		Map<String, Object> resolved = new LinkedHashMap<>();

		for (String variable : variables) {
			String path = variable.substring(2, variable.length() - 1);
			JsonElement value = navigateJsonPath(root, path);

			if (value == null || value.isJsonNull()) {
				resolved.put(variable, "");
			} else if (value.isJsonArray()) {
				List<String> items = new ArrayList<>();
				value.getAsJsonArray().forEach(item -> items.add(item.getAsString()));
				resolved.put(variable, String.join("\n", items));
			} else if (value.isJsonPrimitive()) {
				resolved.put(variable, value.getAsString());
			} else if (value.isJsonObject()) {
				resolved.put(variable, gson.toJson(value));
			} else {
				resolved.put(variable, "");
			}
		}

		return resolved;
	}

	private JsonElement navigateJsonPath(JsonElement root, String path) {
		String[] levels = path.split("\\.");
		JsonElement current = root;

		for (String level : levels) {
			if (current == null || !current.isJsonObject()) {	
				return null;
			}

			JsonObject obj = current.getAsJsonObject();
			if (!obj.has(level)) {
				return null;
			}

			current = obj.get(level);
		}

		return current;
	}
}
