package com.halalbuilds.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.halalbuilds.model.PasteMode;
import com.halalbuilds.model.Rotation;
import org.junit.jupiter.api.Test;

class CommandParserTest {
    private final CommandParser parser = new CommandParser();

    @Test
    void parsesSimplePaste() {
        CommandParseResult result = parser.parse(new String[]{"paste", "house"});
        CommandParseResult.Paste paste = assertInstanceOf(CommandParseResult.Paste.class, result);
        assertEquals("house", paste.sourceName());
        assertEquals(Rotation.DEG_0, paste.rotation());
    }

    @Test
    void parsesPasteWithFlags() {
        CommandParseResult result = parser.parse(new String[]{"paste", "house", "--rotate", "180", "--preview", "--mode", "exact"});
        CommandParseResult.Paste paste = assertInstanceOf(CommandParseResult.Paste.class, result);
        assertEquals(Rotation.DEG_180, paste.rotation());
        assertEquals(true, paste.preview());
        assertEquals(PasteMode.EXACT, paste.pasteMode());
    }

    @Test
    void rejectsInvalidPasteFlags() {
        assertThrows(IllegalArgumentException.class, () -> parser.parse(new String[]{"paste", "house", "--rotate", "45"}));
        assertThrows(IllegalArgumentException.class, () -> parser.parse(new String[]{"paste", "house", "--unknown"}));
        assertThrows(IllegalArgumentException.class, () -> parser.parse(new String[]{"save"}));
    }

    @Test
    void parsesRotateCommand() {
        CommandParseResult result = parser.parse(new String[]{"rotate", "90"});
        CommandParseResult.Simple simple = assertInstanceOf(CommandParseResult.Simple.class, result);
        assertEquals(CommandParseResult.Subcommand.ROTATE, simple.subcommand());
        assertEquals("90", simple.value());
    }
}
