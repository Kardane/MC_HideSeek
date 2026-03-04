package com.example.minigame.v2.util;

import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public final class HideSeekTextRenderUtil {
    private HideSeekTextRenderUtil() {
    }

    public static Text renderMessage(
            String template,
            int progressPercent,
            int cooldownSeconds,
            String finderName,
            String targetName,
            String summary,
            Text blockDisplayName
    ) {
        String base = template
                .replace("{progress}", Integer.toString(progressPercent))
                .replace("{count}", Integer.toString(progressPercent))
                .replace("{seekers}", Integer.toString(progressPercent))
                .replace("{cooldown}", Integer.toString(cooldownSeconds))
                .replace("{seconds}", Integer.toString(cooldownSeconds))
                .replace("{blocks}", Integer.toString(cooldownSeconds))
                .replace("{finder}", finderName)
                .replace("{target}", targetName)
                .replace("{summary}", summary);

        String marker = "{block}";
        MutableText text = Text.literal("");
        Style style = Style.EMPTY;
        StringBuilder buffer = new StringBuilder();
        int cursor = 0;
        while (true) {
            int next = base.indexOf(marker, cursor);
            if (next < 0) {
                break;
            }

            FormattingResult beforeBlock = appendFormattedSegment(text, base.substring(cursor, next), style, buffer);
            style = beforeBlock.style();
            buffer = beforeBlock.buffer();

            flushBufferedText(text, style, buffer);
            text.append(blockDisplayName.copy().setStyle(style));
            cursor = next + marker.length();
        }

        FormattingResult tail = appendFormattedSegment(text, base.substring(cursor), style, buffer);
        flushBufferedText(text, tail.style(), tail.buffer());
        return text;
    }

    private static FormattingResult appendFormattedSegment(MutableText text, String segment, Style startStyle, StringBuilder existingBuffer) {
        Style style = startStyle;
        StringBuilder buffer = existingBuffer;

        for (int i = 0; i < segment.length(); i++) {
            char current = segment.charAt(i);
            if ((current == '&' || current == '§') && i + 1 < segment.length()) {
                char code = Character.toLowerCase(segment.charAt(i + 1));
                Formatting formatting = Formatting.byCode(code);
                if (formatting != null) {
                    flushBufferedText(text, style, buffer);
                    style = applyFormatting(style, formatting);
                    i += 1;
                    continue;
                }
            }
            buffer.append(current);
        }

        return new FormattingResult(style, buffer);
    }

    private static void flushBufferedText(MutableText text, Style style, StringBuilder buffer) {
        if (buffer.isEmpty()) {
            return;
        }
        text.append(Text.literal(buffer.toString()).setStyle(style));
        buffer.setLength(0);
    }

    private static Style applyFormatting(Style currentStyle, Formatting formatting) {
        if (formatting == Formatting.RESET) {
            return Style.EMPTY;
        }

        if (formatting.isColor()) {
            return Style.EMPTY.withColor(formatting);
        }

        return switch (formatting) {
            case BOLD -> currentStyle.withBold(true);
            case ITALIC -> currentStyle.withItalic(true);
            case UNDERLINE -> currentStyle.withUnderline(true);
            case STRIKETHROUGH -> currentStyle.withStrikethrough(true);
            case OBFUSCATED -> currentStyle.withObfuscated(true);
            default -> currentStyle;
        };
    }

    private record FormattingResult(Style style, StringBuilder buffer) {
    }
}
