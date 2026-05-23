package com.halalbuilds.model;

import java.time.Instant;
import org.bukkit.Location;

public record PendingPasteOperation(
    String sourceName,
    boolean clipboardSource,
    Rotation rotation,
    PasteMode pasteMode,
    boolean previewRequested,
    Location targetLocation,
    PlacementSummary summary,
    Instant createdAt
) {
    public boolean isExpired(long timeoutSeconds, Instant now) {
        return createdAt.plusSeconds(timeoutSeconds).isBefore(now);
    }
}

