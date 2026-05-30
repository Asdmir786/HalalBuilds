package com.halalbuilds.command;

import com.halalbuilds.model.PasteMode;
import com.halalbuilds.model.Rotation;

public sealed interface CommandParseResult permits CommandParseResult.Help, CommandParseResult.Simple, CommandParseResult.Save, CommandParseResult.Move, CommandParseResult.Paste {
    record Help() implements CommandParseResult {
    }

    record Simple(Subcommand subcommand, String value) implements CommandParseResult {
    }

    record Save(String name, Boolean saveEntities) implements CommandParseResult {
    }

    record Move(String direction, int blocks) implements CommandParseResult {
    }

    record Paste(String sourceName, Rotation rotation, boolean preview, PasteMode pasteMode, Boolean pasteAirBlocks, Boolean pasteEntities) implements CommandParseResult {
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
        MOVE,
        RELOAD
    }
}
