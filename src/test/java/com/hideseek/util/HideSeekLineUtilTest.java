package com.hideseek.util;

import com.hideseek.minigame.HideSeekUtils;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HideSeekLineUtilTest {
    @Test
    void splitTemplateLinesReturnsSingleEmptyForBlankInput() {
        assertEquals(List.of(""), HideSeekUtils.splitTemplateLines(null));
        assertEquals(List.of(""), HideSeekUtils.splitTemplateLines("   "));
    }

    @Test
    void splitTemplateLinesSupportsEscapedNewline() {
        assertEquals(
                List.of("line1", "line2"),
                HideSeekUtils.splitTemplateLines("line1\\nline2")
        );
    }

    @Test
    void splitTemplateLinesSupportsActualNewline() {
        assertEquals(
                List.of("line1", "line2"),
                HideSeekUtils.splitTemplateLines("line1\nline2")
        );
    }
}
