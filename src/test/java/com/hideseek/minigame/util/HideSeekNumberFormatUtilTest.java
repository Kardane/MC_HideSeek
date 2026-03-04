package com.hideseek.minigame.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HideSeekNumberFormatUtilTest {
    @Test
    void formatPercentHandlesZeroTotal() {
        assertEquals("0.0%", HideSeekNumberFormatUtil.formatPercent(3, 0));
    }

    @Test
    void formatPercentFormatsOneDecimal() {
        assertEquals("33.3%", HideSeekNumberFormatUtil.formatPercent(1, 3));
    }

    @Test
    void formatAverageDecimalHandlesZeroCount() {
        assertEquals("0.0", HideSeekNumberFormatUtil.formatAverageDecimal(10, 0));
    }

    @Test
    void formatAverageDecimalFormatsOneDecimal() {
        assertEquals("2.5", HideSeekNumberFormatUtil.formatAverageDecimal(5, 2));
    }

    @Test
    void formatAverageSecondsHandlesZeroCount() {
        assertEquals("0.0초", HideSeekNumberFormatUtil.formatAverageSeconds(100, 0));
    }

    @Test
    void formatAverageSecondsConvertsTicksToSeconds() {
        assertEquals("2.5초", HideSeekNumberFormatUtil.formatAverageSeconds(100, 2));
    }
}
