package com.example.minigame.v2.util;

import java.util.Locale;

public final class HideSeekNumberFormatUtil {
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
