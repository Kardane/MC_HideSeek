package com.hideseek.util;

import com.hideseek.minigame.HideSeekUtils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HideSeekNumberFormatUtilTest {
    @Test
    void formatPercentHandlesZeroTotal() {
        assertEquals("0.0%", HideSeekUtils.formatPercent(3, 0));
    }

    @Test
    void formatPercentFormatsOneDecimal() {
        assertEquals("33.3%", HideSeekUtils.formatPercent(1, 3));
    }

    @Test
    void formatAverageDecimalHandlesZeroCount() {
        assertEquals("0.0", HideSeekUtils.formatAverageDecimal(10, 0));
    }

    @Test
    void formatAverageDecimalFormatsOneDecimal() {
        assertEquals("2.5", HideSeekUtils.formatAverageDecimal(5, 2));
    }

    @Test
    void formatAverageSecondsHandlesZeroCount() {
        assertEquals("0.0초", HideSeekUtils.formatAverageSeconds(100, 0));
    }

    @Test
    void formatAverageSecondsConvertsTicksToSeconds() {
        assertEquals("2.5초", HideSeekUtils.formatAverageSeconds(100, 2));
    }
}
