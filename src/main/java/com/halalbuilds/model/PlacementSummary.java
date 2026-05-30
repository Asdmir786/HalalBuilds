package com.halalbuilds.model;

import java.util.Set;

public record PlacementSummary(
    Dimensions dimensions,
    long volume,
    long airBlocksSkipped,
    long entityCount,
    long terrainBlocksToClear,
    long foundationBlocksToPlace,
    Set<String> denylistedBlocks,
    boolean requiresConfirmation
) {
    public long totalTerrainChanges() {
        return terrainBlocksToClear + foundationBlocksToPlace;
    }
}
