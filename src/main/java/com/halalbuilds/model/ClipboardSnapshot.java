package com.halalbuilds.model;

import com.sk89q.worldedit.extent.clipboard.Clipboard;
import java.time.Instant;
import java.util.UUID;

public record ClipboardSnapshot(
    UUID owner,
    Clipboard clipboard,
    Dimensions dimensions,
    Vector3i originOffset,
    String sourceName,
    Instant createdAt
) {
}

