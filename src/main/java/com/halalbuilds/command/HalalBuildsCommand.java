package com.halalbuilds.command;

import com.halalbuilds.HalalBuildsPlugin;
import com.halalbuilds.model.BuildMetadata;
import com.halalbuilds.model.ClipboardSnapshot;
import com.halalbuilds.model.Dimensions;
import com.halalbuilds.model.PasteMode;
import com.halalbuilds.model.Rotation;
import com.halalbuilds.model.UndoSnapshot;
import com.halalbuilds.model.Vector3i;
import com.halalbuilds.selection.SelectionService;
import com.halalbuilds.util.Messages;
import com.halalbuilds.util.NameValidator;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.math.BlockVector3;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class HalalBuildsCommand implements CommandExecutor, TabCompleter {
    private final HalalBuildsPlugin plugin;
    private final CommandParser parser = new CommandParser();

    public HalalBuildsCommand(HalalBuildsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        try {
            execute(sender, args);
        } catch (IllegalArgumentException exception) {
            sender.sendMessage(Messages.prefixed(plugin.config().messagePrefix(), "&c" + exception.getMessage()));
        } catch (Exception exception) {
            plugin.getLogger().severe("HalalBuilds command failed: " + exception.getMessage());
            exception.printStackTrace();
            sender.sendMessage(Messages.prefixed(plugin.config().messagePrefix(), "&cAn unexpected error occurred. Check the console for details."));
        }
        return true;
    }

    private void execute(CommandSender sender, String[] args) throws Exception {
        CommandParseResult result = parser.parse(args);
        if (result instanceof CommandParseResult.Help) {
            sendHelp(sender);
            return;
        }

        if (result instanceof CommandParseResult.Simple simple) {
            handleSimple(sender, simple);
            return;
        }

        if (result instanceof CommandParseResult.Paste paste) {
            requirePermission(sender, paste.preview() ? "halalbuilds.preview" : "halalbuilds.paste");
            requirePermission(sender, "halalbuilds.paste");
            Player player = requirePlayer(sender);
            ensureWorldEnabled(player);
            String normalizedSourceName = NameValidator.validateBuildName(paste.sourceName());
            boolean clipboardSource = paste.sourceName().equalsIgnoreCase("clipboard");
            var plan = plugin.pasteService().previewOrCreatePending(
                player,
                clipboardSource ? player.getUniqueId().toString() : normalizedSourceName,
                clipboardSource,
                paste.rotation(),
                paste.pasteMode(),
                plugin.config(),
                paste.preview()
            );
            if (plan.summary().requiresConfirmation()) {
                var pending = plugin.pasteService().getPending(player.getUniqueId(), plugin.config())
                    .orElseThrow(() -> new IllegalStateException("Pending paste operation was not retained."));
                sender.sendMessage(Messages.prefixed(plugin.config().messagePrefix(), "&ePlacement queued for confirmation."));
                sender.sendMessage(Messages.prefixed(plugin.config().messagePrefix(), previewText(pending)));
                sender.sendMessage(Messages.prefixed(plugin.config().messagePrefix(), "&eUse /hb confirm to apply or /hb cancel to discard."));
            } else {
                plugin.pasteService().executePaste(
                    player,
                    clipboardSource ? player.getUniqueId().toString() : normalizedSourceName,
                    clipboardSource,
                    paste.rotation(),
                    paste.pasteMode(),
                    plugin.config()
                );
                sender.sendMessage(Messages.prefixed(plugin.config().messagePrefix(), "&aPaste complete."));
            }
        }
    }

    private void handleSimple(CommandSender sender, CommandParseResult.Simple simple) throws Exception {
        switch (simple.subcommand()) {
            case WAND -> {
                requirePermission(sender, "halalbuilds.wand");
                Player player = requirePlayer(sender);
                player.getInventory().addItem(new ItemStack(Material.WOODEN_AXE));
                sender.sendMessage(Messages.prefixed(plugin.config().messagePrefix(), "&aSelection wand added. Use FAWE/WorldEdit selection points with the axe."));
            }
            case SAVE -> {
                requirePermission(sender, "halalbuilds.save");
                Player player = requirePlayer(sender);
                ensureWorldEnabled(player);
                String buildName = NameValidator.validateBuildName(simple.value());
                SelectionService.SelectionData selection = requireSelection(player);
                assertSelectionWithinLimit(selection.dimensions());
                plugin.storageService().assertWritable(buildName, plugin.config().allowOverwrite());
                Clipboard clipboard = plugin.schematicService().copyRegion(selection.world(), selection.region());
                var record = plugin.storageService().prepareBuildRecord(buildName);
                plugin.schematicService().writeSchematic(clipboard, record.schematicPath());
                plugin.storageService().writeMetadata(new BuildMetadata(
                    buildName,
                    player.getUniqueId(),
                    Instant.now(),
                    selection.dimensions(),
                    selection.world().getName(),
                    new Vector3i(0, 0, 0),
                    List.of(),
                    ""
                ));
                sender.sendMessage(Messages.prefixed(plugin.config().messagePrefix(), "&aSaved build '&f" + buildName + "&a'."));
            }
            case COPY -> {
                requirePermission(sender, "halalbuilds.copy");
                Player player = requirePlayer(sender);
                ensureWorldEnabled(player);
                SelectionService.SelectionData selection = requireSelection(player);
                assertSelectionWithinLimit(selection.dimensions());
                Clipboard clipboard = plugin.schematicService().copyRegion(selection.world(), selection.region());
                plugin.clipboardService().store(new ClipboardSnapshot(
                    player.getUniqueId(),
                    clipboard,
                    selection.dimensions(),
                    new Vector3i(0, 0, 0),
                    "clipboard",
                    Instant.now()
                ));
                sender.sendMessage(Messages.prefixed(plugin.config().messagePrefix(), "&aCopied selection to your HalalBuilds clipboard (&f" + selection.volume() + "&a blocks)."));
            }
            case CUT -> {
                requirePermission(sender, "halalbuilds.cut");
                Player player = requirePlayer(sender);
                ensureWorldEnabled(player);
                SelectionService.SelectionData selection = requireSelection(player);
                assertSelectionWithinLimit(selection.dimensions());
                Clipboard clipboard = plugin.schematicService().copyRegion(selection.world(), selection.region());
                Set<Material> denylisted = scanSelectionForDenylisted(selection);
                if (!denylisted.isEmpty() && plugin.config().stopOnDenylistedBlocks()) {
                    throw new IllegalArgumentException("Cut blocked by denylisted blocks: " + denylisted);
                }
                plugin.clipboardService().store(new ClipboardSnapshot(
                    player.getUniqueId(),
                    clipboard,
                    selection.dimensions(),
                    new Vector3i(0, 0, 0),
                    "clipboard",
                    Instant.now()
                ));
                plugin.undoService().storeUndo(player.getUniqueId(), new UndoSnapshot(
                    clipboard,
                    selection.world().getName(),
                    selection.region().getMinimumPoint(),
                    Instant.now(),
                    "Undo cut"
                ));
                try (EditSession editSession = WorldEdit.getInstance().newEditSession(BukkitAdapter.adapt(selection.world()))) {
                    for (BlockVector3 point : selection.region()) {
                        editSession.setBlock(point, BukkitAdapter.adapt(Material.AIR.createBlockData()));
                    }
                }
                sender.sendMessage(Messages.prefixed(plugin.config().messagePrefix(), "&aCut complete. Your clipboard now holds the selection."));
            }
            case CONFIRM -> {
                requirePermission(sender, "halalbuilds.confirm");
                Player player = requirePlayer(sender);
                var pending = plugin.pasteService().getPending(player.getUniqueId(), plugin.config())
                    .orElseThrow(() -> new IllegalArgumentException("You do not have a pending HalalBuilds operation."));
                plugin.pasteService().executePendingPaste(player, pending, plugin.config());
                var target = pending.targetLocation();
                sender.sendMessage(Messages.prefixed(
                    plugin.config().messagePrefix(),
                    "&aPending placement confirmed at &f"
                        + target.getWorld().getName()
                        + " "
                        + target.getBlockX()
                        + ", "
                        + target.getBlockY()
                        + ", "
                        + target.getBlockZ()
                        + "&a with rotation &f"
                        + pending.rotation().degrees()
                        + "&a."
                ));
            }
            case CANCEL -> {
                requirePermission(sender, "halalbuilds.confirm");
                Player player = requirePlayer(sender);
                plugin.pasteService().clearPending(player.getUniqueId());
                sender.sendMessage(Messages.prefixed(plugin.config().messagePrefix(), "&ePending placement cancelled."));
            }
            case IMPORT -> {
                requirePermission(sender, "halalbuilds.import");
                String[] importParts = simple.value().split("\n", -1);
                String filename = importParts[0];
                String requestedName = importParts.length > 1 && !importParts[1].equals("null") ? importParts[1] : filename.replaceFirst("(?i)\\.schem$", "");
                String buildName = NameValidator.validateBuildName(requestedName);
                plugin.storageService().assertWritable(buildName, plugin.config().allowOverwrite());
                var importPath = plugin.storageService().resolveImportPath(filename, plugin.config());
                if (!java.nio.file.Files.exists(importPath)) {
                    throw new IllegalArgumentException("Import file not found in " + plugin.storageService().importsDirectory().getFileName() + ": " + filename);
                }
                Clipboard clipboard = plugin.schematicService().readSchematic(importPath);
                Dimensions dimensions = plugin.schematicService().dimensionsOf(clipboard.getRegion());
                var record = plugin.storageService().prepareBuildRecord(buildName);
                plugin.schematicService().writeSchematic(clipboard, record.schematicPath());
                plugin.storageService().writeMetadata(new BuildMetadata(
                    buildName,
                    sender instanceof Player player ? player.getUniqueId() : null,
                    Instant.now(),
                    dimensions,
                    "imported",
                    new Vector3i(0, 0, 0),
                    List.of("imported"),
                    "Imported from " + filename
                ));
                sender.sendMessage(Messages.prefixed(plugin.config().messagePrefix(), "&aImported '&f" + filename + "&a' as '&f" + buildName + "&a'."));
            }
            case EXPORT -> {
                requirePermission(sender, "halalbuilds.export");
                String buildName = NameValidator.validateBuildName(simple.value());
                var exportPath = plugin.storageService().exportBuild(buildName);
                sender.sendMessage(Messages.prefixed(plugin.config().messagePrefix(), "&aExported '&f" + buildName + "&a' to '&f" + exportPath.getFileName() + "&a'."));
            }
            case LIST -> {
                requirePermission(sender, "halalbuilds.use");
                var builds = plugin.storageService().listBuilds();
                if (builds.isEmpty()) {
                    sender.sendMessage(Messages.prefixed(plugin.config().messagePrefix(), "&eNo saved builds yet."));
                    return;
                }
                String names = builds.stream().map(record -> record.metadata().name()).reduce((left, right) -> left + ", " + right).orElse("");
                sender.sendMessage(Messages.prefixed(plugin.config().messagePrefix(), "&aSaved builds: &f" + names));
            }
            case INFO -> {
                requirePermission(sender, "halalbuilds.use");
                String buildName = NameValidator.validateBuildName(simple.value());
                var record = plugin.storageService().loadRecord(buildName);
                var metadata = record.metadata();
                sender.sendMessage(Messages.prefixed(plugin.config().messagePrefix(), "&aBuild '&f" + metadata.name() + "&a':"));
                sender.sendMessage(Messages.prefixed(plugin.config().messagePrefix(), "&7Dimensions: &f" + metadata.dimensions().width() + "x" + metadata.dimensions().height() + "x" + metadata.dimensions().length()));
                sender.sendMessage(Messages.prefixed(plugin.config().messagePrefix(), "&7Created: &f" + metadata.createdAt()));
                sender.sendMessage(Messages.prefixed(plugin.config().messagePrefix(), "&7World: &f" + metadata.originalWorld()));
                sender.sendMessage(Messages.prefixed(plugin.config().messagePrefix(), "&7Tags: &f" + String.join(", ", metadata.tags())));
                sender.sendMessage(Messages.prefixed(plugin.config().messagePrefix(), "&7Notes: &f" + metadata.notes()));
            }
            case DELETE -> {
                requirePermission(sender, "halalbuilds.delete");
                String buildName = NameValidator.validateBuildName(simple.value());
                plugin.storageService().deleteBuild(buildName);
                sender.sendMessage(Messages.prefixed(plugin.config().messagePrefix(), "&aDeleted build '&f" + buildName + "&a'."));
            }
            case UNDO -> {
                requirePermission(sender, "halalbuilds.undo");
                Player player = requirePlayer(sender);
                plugin.pasteService().undo(player);
                sender.sendMessage(Messages.prefixed(plugin.config().messagePrefix(), "&aLatest HalalBuilds action undone."));
            }
            case ROTATE -> {
                requirePermission(sender, "halalbuilds.paste");
                Player player = requirePlayer(sender);
                Rotation rotation = Rotation.fromDegrees(Integer.parseInt(simple.value()));
                var pending = plugin.pasteService().getPending(player.getUniqueId(), plugin.config())
                    .orElseThrow(() -> new IllegalArgumentException("You do not have a pending paste to rotate. Use /hb paste <name|clipboard> --preview first."));
                var rotated = plugin.pasteService().rotatePendingPaste(player, pending, rotation, plugin.config());
                sender.sendMessage(Messages.prefixed(plugin.config().messagePrefix(), "&aPending placement rotation set to &f" + rotation.degrees() + "&a degrees."));
                sender.sendMessage(Messages.prefixed(plugin.config().messagePrefix(), previewText(rotated)));
                sender.sendMessage(Messages.prefixed(plugin.config().messagePrefix(), "&eUse /hb confirm to paste this preview or /hb cancel to discard it."));
            }
            case RELOAD -> {
                requirePermission(sender, "halalbuilds.reload");
                plugin.reloadPluginConfig();
                sender.sendMessage(Messages.prefixed(plugin.config().messagePrefix(), "&aHalalBuilds config reloaded."));
            }
        }
    }

    private Player requirePlayer(CommandSender sender) {
        if (sender instanceof Player player) {
            return player;
        }
        throw new IllegalArgumentException("This command can only be used by a player.");
    }

    private void requirePermission(CommandSender sender, String permission) {
        if (!sender.hasPermission(permission) && !sender.hasPermission("halalbuilds.admin")) {
            throw new IllegalArgumentException("You do not have permission: " + permission);
        }
    }

    private void ensureWorldEnabled(Player player) {
        if (!plugin.config().worlds().isWorldEnabled(player.getWorld().getName())) {
            throw new IllegalArgumentException("HalalBuilds is disabled in this world.");
        }
    }

    private SelectionService.SelectionData requireSelection(Player player) throws IncompleteRegionException {
        return plugin.selectionService().getSelection(player);
    }

    private void assertSelectionWithinLimit(Dimensions dimensions) {
        long volume = dimensions.volume();
        if (volume > plugin.config().maxSelectionVolume()) {
            throw new IllegalArgumentException("Selection too large: " + volume + " blocks (limit " + plugin.config().maxSelectionVolume() + ").");
        }
    }

    private Set<Material> scanSelectionForDenylisted(SelectionService.SelectionData selection) {
        Set<Material> denylisted = new java.util.LinkedHashSet<>();
        for (BlockVector3 point : selection.region()) {
            Material material = selection.world().getBlockAt(point.x(), point.y(), point.z()).getType();
            if (plugin.config().denylistedBlocks().contains(material)) {
                denylisted.add(material);
            }
        }
        return denylisted;
    }

    private String previewText(com.halalbuilds.model.PendingPasteOperation pending) {
        var summary = pending.summary();
        var target = pending.targetLocation();
        String source = pending.clipboardSource() ? "clipboard" : pending.sourceName();
        return "&ePreview: &f"
            + source
            + "&e, size &f"
            + summary.dimensions().width() + "x" + summary.dimensions().height() + "x" + summary.dimensions().length()
            + "&e, volume &f" + summary.volume()
            + "&e, world &f" + (target.getWorld() == null ? "unknown" : target.getWorld().getName())
            + "&e, target &f" + target.getBlockX() + ", " + target.getBlockY() + ", " + target.getBlockZ()
            + "&e, rotation &f" + pending.rotation().degrees()
            + "&e, mode &f" + pending.pasteMode().name().toLowerCase(Locale.ROOT)
            + "&e, clear &f" + summary.terrainBlocksToClear()
            + "&e, foundation &f" + summary.foundationBlocksToPlace()
            + (summary.denylistedBlocks().isEmpty() ? "" : "&e, denylisted &f" + summary.denylistedBlocks());
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage(Messages.prefixed(plugin.config().messagePrefix(), "&aUsage:"));
        sender.sendMessage(Messages.prefixed(plugin.config().messagePrefix(), "&f/hb wand, save <name>, copy, cut, paste <name|clipboard>, rotate <0|90|180|270>, confirm, cancel"));
        sender.sendMessage(Messages.prefixed(plugin.config().messagePrefix(), "&f/hb import <file> [name], export <name>, list, info <name>, delete <name>, undo, reload"));
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) {
            return filter(args[0], List.of("wand", "save", "copy", "cut", "paste", "rotate", "confirm", "cancel", "import", "export", "list", "info", "delete", "undo", "reload"));
        }
        if (args.length == 2 && List.of("rotate", "rotation").contains(args[0].toLowerCase(Locale.ROOT))) {
            return filter(args[1], List.of("0", "90", "180", "270"));
        }
        if (args.length == 2 && List.of("paste", "info", "delete", "export").contains(args[0].toLowerCase(Locale.ROOT))) {
            List<String> names = new ArrayList<>();
            if ("paste".equalsIgnoreCase(args[0])) {
                names.add("clipboard");
            }
            try {
                plugin.storageService().listBuilds().forEach(record -> names.add(record.metadata().name()));
            } catch (IOException ignored) {
            }
            return filter(args[1], names);
        }
        if (args.length >= 3 && "paste".equalsIgnoreCase(args[0])) {
            String current = args[args.length - 1];
            String previous = args[args.length - 2];
            if (!previous.startsWith("--")) {
                return filter(current, List.of("--rotate", "--preview", "--mode"));
            }
        }
        if (args.length >= 4 && "paste".equalsIgnoreCase(args[0])) {
            if ("--rotate".equalsIgnoreCase(args[args.length - 2])) {
                return filter(args[args.length - 1], List.of("0", "90", "180", "270"));
            }
            if ("--mode".equalsIgnoreCase(args[args.length - 2])) {
                return filter(args[args.length - 1], List.of("smart_foundation", "exact"));
            }
        }
        return List.of();
    }

    private List<String> filter(String prefix, List<String> options) {
        String normalized = prefix.toLowerCase(Locale.ROOT);
        return options.stream().filter(option -> option.toLowerCase(Locale.ROOT).startsWith(normalized)).toList();
    }
}
