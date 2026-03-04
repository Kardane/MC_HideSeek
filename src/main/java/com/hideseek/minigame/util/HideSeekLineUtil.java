package com.hideseek.minigame.util;

import java.util.ArrayList;
import java.util.List;

public final class HideSeekLineUtil {
    private HideSeekLineUtil() {
    }

    public static List<String> splitTemplateLines(String template) {
        if (template == null || template.isBlank()) {
            return List.of("");
        }

        String normalized = template.replace("\\n", "\n");
        String[] rawLines = normalized.split("\n");
        List<String> lines = new ArrayList<>();
        for (String rawLine : rawLines) {
            lines.add(rawLine == null ? "" : rawLine);
        }
        return lines;
    }
}
