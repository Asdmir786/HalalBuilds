package com.halalbuilds.command;

import com.halalbuilds.model.PasteMode;
import com.halalbuilds.model.Rotation;

public sealed interface CommandParseResult permits CommandParseResult.Help, CommandParseResult.Simple, CommandParseResult.Paste {
    record Help() implements CommandParseResult {
    }

    record Simple(Subcommand subcommand, String value) implements CommandParseResult {
    }

    record Paste(String sourceName, Rotation rotation, boolean preview, PasteMode pasteMode) implements CommandParseResult {
    }

    enum Subcommand {
        WAND,
        SAVE,
        COPY,
        CUT,
        CONFIRM,
        CANCEL,
        IMPORT,
        EXPORT,
        LIST,
        INFO,
        DELETE,
        UNDO,
        ROTATE,
        RELOAD
    }
}
