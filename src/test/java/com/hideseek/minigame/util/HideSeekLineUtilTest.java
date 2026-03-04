package com.hideseek.minigame.util;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HideSeekLineUtilTest {
    @Test
    void splitTemplateLinesReturnsSingleEmptyForBlankInput() {
        assertEquals(List.of(""), HideSeekLineUtil.splitTemplateLines(null));
        assertEquals(List.of(""), HideSeekLineUtil.splitTemplateLines("   "));
    }

    @Test
    void splitTemplateLinesSupportsEscapedNewline() {
        assertEquals(
                List.of("line1", "line2"),
                HideSeekLineUtil.splitTemplateLines("line1\\nline2")
        );
    }

    @Test
    void splitTemplateLinesSupportsActualNewline() {
        assertEquals(
                List.of("line1", "line2"),
                HideSeekLineUtil.splitTemplateLines("line1\nline2")
        );
    }
}
