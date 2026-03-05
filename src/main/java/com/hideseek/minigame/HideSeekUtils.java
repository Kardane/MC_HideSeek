package com.hideseek.minigame;

import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class HideSeekUtils {
    private HideSeekUtils() {
    }

    public static final class HideSeekLineUtil {
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

    public static final class HideSeekMathUtil {
        private HideSeekMathUtil() {
        }

        public static Vec3d centerOnBlock(Vec3d source) {
            return new Vec3d(
                    Math.floor(source.x) + 0.5D,
                    Math.floor(source.y),
                    Math.floor(source.z) + 0.5D
            );
        }

        public static Vec3d seatPosition(Vec3d anchorPos, double seatYOffset) {
            return new Vec3d(anchorPos.x, anchorPos.y + seatYOffset, anchorPos.z);
        }

        public static float clamp01(float value) {
            if (value < 0.0F) {
                return 0.0F;
            }
            if (value > 1.0F) {
                return 1.0F;
            }
            return value;
        }
    }

    public static final class HideSeekNumberFormatUtil {
        private HideSeekNumberFormatUtil() {
        }

        public static String formatPercent(long wins, long total) {
            if (total <= 0) {
                return "0.0%";
            }
            double percent = (wins * 100.0D) / total;
            return String.format(Locale.ROOT, "%.1f%%", percent);
        }

        public static String formatAverageDecimal(long total, long count) {
            if (count <= 0) {
                return "0.0";
            }
            double avg = total / (double) count;
            return String.format(Locale.ROOT, "%.1f", avg);
        }

        public static String formatAverageSeconds(long totalTicks, long count) {
            if (count <= 0) {
                return "0.0초";
            }
            double seconds = (totalTicks / (double) count) / 20.0D;
            return String.format(Locale.ROOT, "%.1f초", seconds);
        }
    }

    public static final class HideSeekTextRenderUtil {
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
}
