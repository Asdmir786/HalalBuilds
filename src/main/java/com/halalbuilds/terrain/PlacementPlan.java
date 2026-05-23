package com.halalbuilds.terrain;

import com.halalbuilds.model.Dimensions;
import com.halalbuilds.model.PlacementSummary;
import com.sk89q.worldedit.math.BlockVector3;
import java.util.List;
import java.util.Set;
import org.bukkit.Material;

public record PlacementPlan(
    Dimensions dimensions,
    BlockVector3 targetMinimumPoint,
    BlockVector3 targetMaximumPoint,
    List<BlockVector3> blocksToClear,
    List<BlockVector3> foundationBlocks,
    Set<Material> denylistedMaterials,
    PlacementSummary summary
) {
}

