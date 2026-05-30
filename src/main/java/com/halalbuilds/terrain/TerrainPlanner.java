package com.halalbuilds.terrain;

import com.halalbuilds.config.HalalBuildsConfig;
import com.halalbuilds.model.Dimensions;
import com.halalbuilds.model.PasteMode;
import com.halalbuilds.model.PlacementSummary;
import com.halalbuilds.schematic.SchematicService;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.math.BlockVector3;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

public final class TerrainPlanner {
    private final SchematicService schematicService;

    public TerrainPlanner(SchematicService schematicService) {
        this.schematicService = schematicService;
    }

    public PlacementPlan plan(World world, Clipboard clipboard, Location target, PasteMode mode, HalalBuildsConfig config, boolean previewRequested, boolean pasteAirBlocks) {
        Dimensions dimensions = schematicService.dimensionsOf(clipboard.getRegion());
        BlockVector3 minimum = BlockVector3.at(target.getBlockX(), target.getBlockY(), target.getBlockZ());
        BlockVector3 maximum = minimum.add(dimensions.width() - 1, dimensions.height() - 1, dimensions.length() - 1);
        long airBlocks = countAirBlocks(clipboard);
        long airBlocksSkipped = pasteAirBlocks ? 0 : airBlocks;
        long entityCount = clipboard.getEntities().size();

        if (mode == PasteMode.EXACT) {
            Set<Material> denylisted = scanExactDenylisted(world, clipboard, minimum, config, pasteAirBlocks);
            PlacementSummary summary = new PlacementSummary(
                dimensions,
                dimensions.volume(),
                airBlocksSkipped,
                entityCount,
                0,
                0,
                denylisted.stream().map(Material::name).collect(java.util.stream.Collectors.toCollection(java.util.LinkedHashSet::new)),
                previewRequested || (config.requirePreviewForLargePastes() && dimensions.volume() > config.maxPasteVolumeBeforePreview())
            );
            return new PlacementPlan(dimensions, minimum, maximum, List.of(), List.of(), Set.copyOf(denylisted), summary);
        }

        List<BlockVector3> clearBlocks = new ArrayList<>();
        List<BlockVector3> foundationBlocks = new ArrayList<>();
        Set<Material> denylisted = new HashSet<>();
        Map<Long, Integer> lowestStructureByColumn = new HashMap<>();
        Set<Long> occupiedColumns = new HashSet<>();

        BlockVector3 clipboardMinimum = clipboard.getRegion().getMinimumPoint();
        for (BlockVector3 point : clipboard.getRegion()) {
            BlockVector3 local = point.subtract(clipboardMinimum);
            var fullBlock = clipboard.getFullBlock(point);
            boolean schematicAir = fullBlock.getBlockType().getMaterial().isAir();

            int worldX = minimum.x() + local.x();
            int worldY = minimum.y() + local.y();
            int worldZ = minimum.z() + local.z();

            long columnKey = columnKey(worldX, worldZ);
            if (!schematicAir || !config.onlyFillUnderNonAirBlocks()) {
                occupiedColumns.add(columnKey);
                lowestStructureByColumn.merge(columnKey, worldY, Math::min);
            }

            if (schematicAir && !pasteAirBlocks) {
                continue;
            }

            Block worldBlock = world.getBlockAt(worldX, worldY, worldZ);
            Material material = worldBlock.getType();
            if (!material.isAir()) {
                if (config.denylistedBlocks().contains(material)) {
                    denylisted.add(material);
                } else if (config.clearTerrainAboveFootprint() || pasteAirBlocks) {
                    clearBlocks.add(BlockVector3.at(worldX, worldY, worldZ));
                }
            }
        }

        if (config.fillUnderFootprint()) {
            for (Long columnKey : occupiedColumns) {
                int worldX = columnX(columnKey);
                int worldZ = columnZ(columnKey);
                int startY = lowestStructureByColumn.get(columnKey) - 1;

                for (int depth = 0; depth < config.maxFoundationDepth() && startY - depth >= world.getMinHeight(); depth++) {
                    int worldY = startY - depth;
                    Block block = world.getBlockAt(worldX, worldY, worldZ);
                    Material material = block.getType();
                    if (material.isSolid()) {
                        break;
                    }
                    if (config.denylistedBlocks().contains(material)) {
                        denylisted.add(material);
                        break;
                    }
                    foundationBlocks.add(BlockVector3.at(worldX, worldY, worldZ));
                }
            }
        }

        long terrainChanges = clearBlocks.size() + foundationBlocks.size();
        boolean requiresConfirmation = previewRequested
            || (config.requirePreviewForLargePastes() && dimensions.volume() > config.maxPasteVolumeBeforePreview())
            || terrainChanges > config.maxTerrainChangesBeforeConfirm();

        PlacementSummary summary = new PlacementSummary(
            dimensions,
            dimensions.volume(),
            airBlocksSkipped,
            entityCount,
            clearBlocks.size(),
            foundationBlocks.size(),
            denylisted.stream().map(Material::name).collect(java.util.stream.Collectors.toCollection(java.util.LinkedHashSet::new)),
            requiresConfirmation
        );
        return new PlacementPlan(dimensions, minimum, maximum, clearBlocks, foundationBlocks, Set.copyOf(denylisted), summary);
    }

    private static long countAirBlocks(Clipboard clipboard) {
        long airBlocks = 0;
        for (BlockVector3 point : clipboard.getRegion()) {
            if (clipboard.getFullBlock(point).getBlockType().getMaterial().isAir()) {
                airBlocks++;
            }
        }
        return airBlocks;
    }

    private Set<Material> scanExactDenylisted(World world, Clipboard clipboard, BlockVector3 minimum, HalalBuildsConfig config, boolean pasteAirBlocks) {
        Set<Material> denylisted = new HashSet<>();
        BlockVector3 clipboardMinimum = clipboard.getRegion().getMinimumPoint();
        for (BlockVector3 point : clipboard.getRegion()) {
            if (!pasteAirBlocks && clipboard.getFullBlock(point).getBlockType().getMaterial().isAir()) {
                continue;
            }
            BlockVector3 local = point.subtract(clipboardMinimum);
            Material material = world.getBlockAt(minimum.x() + local.x(), minimum.y() + local.y(), minimum.z() + local.z()).getType();
            if (config.denylistedBlocks().contains(material)) {
                denylisted.add(material);
            }
        }
        return denylisted;
    }

    private static long columnKey(int x, int z) {
        return (((long) x) << 32) ^ (z & 0xffffffffL);
    }

    private static int columnX(long key) {
        return (int) (key >> 32);
    }

    private static int columnZ(long key) {
        return (int) key;
    }
}
