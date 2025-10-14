package com.example.demoCarePlan.service;

import java.util.*;
import java.util.regex.Pattern;

import com.google.gson.*;

public class ExtractVariables {

    private static final Gson gson = new Gson();

    public Map<String, Object> execute(String template, String payload) {
        List<String> variables = getAttributes(template);
        return getVariables(variables, payload);
    }

    private List<String> getAttributes(String template) {
        var pattern = Pattern.compile("\\$\\{(.*?)}");
        var matcher = pattern.matcher(template);

        Set<String> attributes = new LinkedHashSet<>();
        while (matcher.find()) {
            attributes.add(matcher.group());
        }

        return new ArrayList<>(attributes);
    }

    private Map<String, Object> getVariables(List<String> attributes, String payload) {
        Map<String, Object> variables = new LinkedHashMap<>();
        JsonElement root = gson.fromJson(payload, JsonElement.class);

        for (String attribute : attributes) {
            String path = attribute.substring(2, attribute.length() - 1);
            String[] levels = path.split("\\.");

            JsonElement element = root;
            for (String level : levels) {
                if (element == null || element.isJsonNull()) {
                    element = null;
                    break;
                }
                if (!element.isJsonObject()) {
                    element = null;
                    break;
                }
                JsonObject obj = element.getAsJsonObject();
                if (!obj.has(level)) {
                    element = null;
                    break;
                }
                element = obj.get(level);
            }

            if (element == null || element.isJsonNull()) {
                variables.put(attribute, "");
                continue;
            }

            if (element.isJsonArray()) {
                List<String> lines = new ArrayList<>();
                JsonArray arr = element.getAsJsonArray();
                for (JsonElement item : arr) {
                    if (item.isJsonPrimitive()) {
                        lines.add(item.getAsString());
                    } else {
                        lines.add(gson.toJson(item));
                    }
                }
                variables.put(attribute, lines); 
            } else if (element.isJsonPrimitive()) {
                variables.put(attribute, element.getAsString());
            } else if (element.isJsonObject()) {
                variables.put(attribute, gson.toJson(element));
            } else {
                variables.put(attribute, "");
            }
        }

        return variables;
    }
}

