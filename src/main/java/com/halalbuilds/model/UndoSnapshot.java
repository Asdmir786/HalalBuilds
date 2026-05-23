package com.halalbuilds.model;

import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.math.BlockVector3;
import java.time.Instant;

public record UndoSnapshot(
    Clipboard clipboard,
    String worldName,
    BlockVector3 targetMinimumPoint,
    Instant createdAt,
    String description
) {
}

