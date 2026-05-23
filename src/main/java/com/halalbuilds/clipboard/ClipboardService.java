package com.halalbuilds.clipboard;

import com.halalbuilds.model.ClipboardSnapshot;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class ClipboardService {
    private final Map<UUID, ClipboardSnapshot> snapshots = new ConcurrentHashMap<>();

    public void store(ClipboardSnapshot snapshot) {
        snapshots.put(snapshot.owner(), snapshot);
    }

    public Optional<ClipboardSnapshot> get(UUID owner) {
        return Optional.ofNullable(snapshots.get(owner));
    }

    public void clear(UUID owner) {
        snapshots.remove(owner);
    }

    public void clearAll() {
        snapshots.clear();
    }
}

