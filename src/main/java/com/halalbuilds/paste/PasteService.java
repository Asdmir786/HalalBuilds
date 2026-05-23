package com.halalbuilds.paste;

import com.halalbuilds.clipboard.ClipboardService;
import com.halalbuilds.config.HalalBuildsConfig;
import com.halalbuilds.model.BuildRecord;
import com.halalbuilds.model.ClipboardSnapshot;
import com.halalbuilds.model.PasteMode;
import com.halalbuilds.model.PendingPasteOperation;
import com.halalbuilds.model.Rotation;
import com.halalbuilds.model.UndoSnapshot;
import com.halalbuilds.schematic.SchematicService;
import com.halalbuilds.storage.BuildStorageService;
import com.halalbuilds.terrain.PlacementPlan;
import com.halalbuilds.terrain.TerrainPlanner;
import com.halalbuilds.undo.UndoService;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.session.ClipboardHolder;
import java.io.IOException;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;

public final class PasteService {
    private final BuildStorageService storageService;
    private final SchematicService schematicService;
    private final ClipboardService clipboardService;
    private final TerrainPlanner terrainPlanner;
    private final UndoService undoService;

    public PasteService(
        BuildStorageService storageService,
        SchematicService schematicService,
        ClipboardService clipboardService,
        TerrainPlanner terrainPlanner,
        UndoService undoService
    ) {
        this.storageService = storageService;
        this.schematicService = schematicService;
        this.clipboardService = clipboardService;
        this.terrainPlanner = terrainPlanner;
        this.undoService = undoService;
    }

    public PlacementPlan previewOrCreatePending(
        Player player,
        String sourceName,
        boolean clipboardSource,
        Rotation rotation,
        PasteMode requestedMode,
        HalalBuildsConfig config,
        boolean previewRequested
    ) throws IOException {
        Clipboard clipboard = rotatedClipboard(sourceName, clipboardSource, rotation);
        Location target = resolveTargetLocation(player);
        PasteMode mode = requestedMode == null ? config.defaultPasteMode() : requestedMode;

        PlacementPlan plan = terrainPlanner.plan(player.getWorld(), clipboard, target, mode, config, previewRequested);
        if (!plan.denylistedMaterials().isEmpty() && config.stopOnDenylistedBlocks()) {
            throw new IllegalArgumentException("The target area includes denylisted blocks: " + plan.summary().denylistedBlocks());
        }

        if (plan.summary().requiresConfirmation()) {
            undoService.storePending(
                player.getUniqueId(),
                new PendingPasteOperation(sourceName, clipboardSource, rotation, mode, previewRequested, target, plan.summary(), Instant.now())
            );
        }
        return plan;
    }

    public void executePaste(
        Player player,
        String sourceName,
        boolean clipboardSource,
        Rotation rotation,
        PasteMode requestedMode,
        HalalBuildsConfig config
    ) throws IOException {
        executePasteAtTarget(
            player.getUniqueId(),
            player.getWorld(),
            resolveTargetLocation(player),
            sourceName,
            clipboardSource,
            rotation,
            requestedMode,
            config
        );
    }

    public void executePendingPaste(Player player, PendingPasteOperation pending, HalalBuildsConfig config) throws IOException {
        Location target = pending.targetLocation();
        World world = target.getWorld();
        if (world == null) {
            throw new IllegalArgumentException("The saved target world for this pending placement is unavailable.");
        }
        executePasteAtTarget(
            player.getUniqueId(),
            world,
            target,
            pending.sourceName(),
            pending.clipboardSource(),
            pending.rotation(),
            pending.pasteMode(),
            config
        );
    }

    public PendingPasteOperation rotatePendingPaste(Player player, PendingPasteOperation pending, Rotation rotation, HalalBuildsConfig config) throws IOException {
        Location target = pending.targetLocation();
        World world = target.getWorld();
        if (world == null) {
            throw new IllegalArgumentException("The saved target world for this pending placement is unavailable.");
        }

        Clipboard clipboard = rotatedClipboard(pending.sourceName(), pending.clipboardSource(), rotation);
        Location normalizedTarget = targetBlockLocation(world, target);
        PlacementPlan plan = terrainPlanner.plan(world, clipboard, normalizedTarget, pending.pasteMode(), config, true);
        if (!plan.denylistedMaterials().isEmpty() && config.stopOnDenylistedBlocks()) {
            throw new IllegalArgumentException("The target area includes denylisted blocks: " + plan.summary().denylistedBlocks());
        }

        PendingPasteOperation rotated = new PendingPasteOperation(
            pending.sourceName(),
            pending.clipboardSource(),
            rotation,
            pending.pasteMode(),
            true,
            normalizedTarget,
            plan.summary(),
            Instant.now()
        );
        undoService.storePending(player.getUniqueId(), rotated);
        return rotated;
    }

    private void executePasteAtTarget(
        UUID actorId,
        World world,
        Location target,
        String sourceName,
        boolean clipboardSource,
        Rotation rotation,
        PasteMode requestedMode,
        HalalBuildsConfig config
    ) throws IOException {
        Clipboard clipboard = rotatedClipboard(sourceName, clipboardSource, rotation);
        PasteMode mode = requestedMode == null ? config.defaultPasteMode() : requestedMode;
        Location normalizedTarget = targetBlockLocation(world, target);
        PlacementPlan plan = terrainPlanner.plan(world, clipboard, normalizedTarget, mode, config, false);
        if (!plan.denylistedMaterials().isEmpty() && config.stopOnDenylistedBlocks()) {
            throw new IllegalArgumentException("The target area includes denylisted blocks: " + plan.summary().denylistedBlocks());
        }

        UndoSnapshot undo = createUndoSnapshot(world, plan);
        applyPlan(world, clipboard, normalizedTarget, plan, config);
        undoService.storeUndo(actorId, undo);
        undoService.clearPending(actorId);
    }

    private Location targetBlockLocation(World world, Location target) {
        return new Location(world, target.getBlockX(), target.getBlockY(), target.getBlockZ());
    }

    public void undo(Player player) throws IOException {
        UndoSnapshot snapshot = undoService.getUndo(player.getUniqueId())
            .orElseThrow(() -> new IllegalArgumentException("There is no undoable HalalBuilds action for you yet."));
        World world = Objects.requireNonNull(player.getServer().getWorld(snapshot.worldName()), "Undo world is unavailable.");
        Location target = new Location(world, snapshot.targetMinimumPoint().x(), snapshot.targetMinimumPoint().y(), snapshot.targetMinimumPoint().z());
        PlacementPlan exactPlan = terrainPlanner.plan(world, snapshot.clipboard(), target, PasteMode.EXACT, fallbackConfig(), false);
        applyPlan(world, snapshot.clipboard(), target, exactPlan, fallbackConfig());
        undoService.clearUndo(player.getUniqueId());
    }

    public Optional<PendingPasteOperation> getPending(UUID playerId, HalalBuildsConfig config) {
        Optional<PendingPasteOperation> pending = undoService.getPending(playerId);
        pending.filter(operation -> operation.isExpired(config.pendingOperationTimeoutSeconds(), Instant.now()))
            .ifPresent(operation -> undoService.clearPending(playerId));
        return undoService.getPending(playerId);
    }

    public void clearPending(UUID playerId) {
        undoService.clearPending(playerId);
    }

    private Clipboard rotatedClipboard(String sourceName, boolean clipboardSource, Rotation rotation) throws IOException {
        Clipboard clipboard = clipboardSource ? loadClipboardSnapshot(sourceName).clipboard() : loadSavedBuild(sourceName);
        return schematicService.rotate(clipboard, rotation);
    }

    private ClipboardSnapshot loadClipboardSnapshot(String source) {
        UUID playerId = UUID.fromString(source);
        return clipboardService.get(playerId)
            .orElseThrow(() -> new IllegalArgumentException("Clipboard data is no longer available."));
    }

    public ClipboardSnapshot getPlayerClipboard(UUID playerId) {
        return clipboardService.get(playerId)
            .orElseThrow(() -> new IllegalArgumentException("You do not have a copied HalalBuilds clipboard."));
    }

    private Clipboard loadSavedBuild(String name) throws IOException {
        BuildRecord record = storageService.loadRecord(name);
        return schematicService.readSchematic(record.schematicPath());
    }

    private UndoSnapshot createUndoSnapshot(World world, PlacementPlan plan) {
        Clipboard clipboard = schematicService.copyCuboid(world, plan.targetMinimumPoint(), plan.targetMaximumPoint());
        return new UndoSnapshot(clipboard, world.getName(), plan.targetMinimumPoint(), Instant.now(), "Undo placement");
    }

    private void applyPlan(World world, Clipboard clipboard, Location target, PlacementPlan plan, HalalBuildsConfig config) {
        try (EditSession editSession = WorldEdit.getInstance().newEditSession(BukkitAdapter.adapt(world))) {
            for (BlockVector3 block : plan.blocksToClear()) {
                editSession.setBlock(block, BukkitAdapter.adapt(Material.AIR.createBlockData()));
            }
            for (BlockVector3 block : plan.foundationBlocks()) {
                editSession.setBlock(block, BukkitAdapter.adapt(config.foundationMaterial().createBlockData()));
            }
            ClipboardHolder holder = new ClipboardHolder(clipboard);
            Operations.complete(holder.createPaste(editSession)
                .to(BlockVector3.at(target.getBlockX(), target.getBlockY(), target.getBlockZ()))
                .ignoreAirBlocks(!config.pasteAirBlocks())
                .build());
        }
    }

    private Location resolveTargetLocation(Player player) {
        var targetBlock = player.getTargetBlockExact(64);
        if (targetBlock != null) {
            return targetBlock.getLocation().add(0, 1, 0);
        }
        return player.getLocation().getBlock().getLocation();
    }

    private HalalBuildsConfig fallbackConfig() {
        return new HalalBuildsConfig(
            250000,
            75000,
            10000,
            64,
            PasteMode.EXACT,
            true,
            true,
            false,
            120,
            Material.STONE_BRICKS,
            true,
            true,
            true,
            Set.of(),
            false,
            java.util.List.of(".schem"),
            true,
            true,
            new HalalBuildsConfig.WorldsConfig("allow_all", Set.of(), Set.of()),
            "&a[HalalBuilds]&r "
        );
    }

}
