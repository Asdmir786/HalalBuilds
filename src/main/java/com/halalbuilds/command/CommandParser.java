package com.halalbuilds.command;

import com.halalbuilds.model.PasteMode;
import com.halalbuilds.model.Rotation;
import java.util.ArrayList;
import java.util.List;

public final class CommandParser {
    public CommandParseResult parse(String[] args) {
        if (args.length == 0) {
            return new CommandParseResult.Help();
        }

        String root = args[0].toLowerCase();
        return switch (root) {
            case "wand" -> new CommandParseResult.Simple(CommandParseResult.Subcommand.WAND, null);
            case "save" -> new CommandParseResult.Simple(CommandParseResult.Subcommand.SAVE, requiredValue(args, "save"));
            case "copy" -> new CommandParseResult.Simple(CommandParseResult.Subcommand.COPY, null);
            case "cut" -> new CommandParseResult.Simple(CommandParseResult.Subcommand.CUT, null);
            case "confirm" -> new CommandParseResult.Simple(CommandParseResult.Subcommand.CONFIRM, null);
            case "cancel" -> new CommandParseResult.Simple(CommandParseResult.Subcommand.CANCEL, null);
            case "import" -> new CommandParseResult.Simple(
                CommandParseResult.Subcommand.IMPORT,
                String.join("\n", importArgs(args))
            );
            case "export" -> new CommandParseResult.Simple(CommandParseResult.Subcommand.EXPORT, requiredValue(args, "export"));
            case "list" -> new CommandParseResult.Simple(CommandParseResult.Subcommand.LIST, null);
            case "info" -> new CommandParseResult.Simple(CommandParseResult.Subcommand.INFO, requiredValue(args, "info"));
            case "delete" -> new CommandParseResult.Simple(CommandParseResult.Subcommand.DELETE, requiredValue(args, "delete"));
            case "undo" -> new CommandParseResult.Simple(CommandParseResult.Subcommand.UNDO, null);
            case "rotate", "rotation" -> new CommandParseResult.Simple(CommandParseResult.Subcommand.ROTATE, requiredValue(args, "rotate"));
            case "reload" -> new CommandParseResult.Simple(CommandParseResult.Subcommand.RELOAD, null);
            case "paste" -> parsePaste(args);
            default -> throw new IllegalArgumentException("Unknown subcommand: " + args[0]);
        };
    }

    private CommandParseResult parsePaste(String[] args) {
        if (args.length < 2) {
            throw new IllegalArgumentException("Usage: /hb paste <name|clipboard> [--rotate <0|90|180|270>] [--preview] [--mode <smart_foundation|exact>]");
        }

        String source = args[1];
        Rotation rotation = Rotation.DEG_0;
        boolean preview = false;
        PasteMode pasteMode = null;

        List<String> remaining = new ArrayList<>();
        for (int index = 2; index < args.length; index++) {
            remaining.add(args[index]);
        }

        for (int index = 0; index < remaining.size(); index++) {
            String flag = remaining.get(index);
            switch (flag) {
                case "--preview" -> preview = true;
                case "--rotate" -> {
                    if (index + 1 >= remaining.size()) {
                        throw new IllegalArgumentException("Missing value for --rotate");
                    }
                    rotation = Rotation.fromDegrees(Integer.parseInt(remaining.get(++index)));
                }
                case "--mode" -> {
                    if (index + 1 >= remaining.size()) {
                        throw new IllegalArgumentException("Missing value for --mode");
                    }
                    pasteMode = PasteMode.fromConfigValue(remaining.get(++index));
                }
                default -> throw new IllegalArgumentException("Unknown paste flag: " + flag);
            }
        }

        return new CommandParseResult.Paste(source, rotation, preview, pasteMode);
    }

    private static String requiredValue(String[] args, String command) {
        if (args.length < 2) {
            throw new IllegalArgumentException("Usage: /hb " + command + " <value>");
        }
        return args[1];
    }

    private static List<String> importArgs(String[] args) {
        if (args.length < 2) {
            throw new IllegalArgumentException("Usage: /hb import <filename> [name]");
        }
        List<String> result = new ArrayList<>();
        result.add(args[1]);
        result.add(args.length >= 3 ? args[2] : null);
        return result;
    }
}
