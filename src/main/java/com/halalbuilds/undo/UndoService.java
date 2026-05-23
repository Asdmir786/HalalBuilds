package com.halalbuilds.undo;

import com.halalbuilds.model.PendingPasteOperation;
import com.halalbuilds.model.UndoSnapshot;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class UndoService {
    private final Map<UUID, UndoSnapshot> undoSnapshots = new ConcurrentHashMap<>();
    private final Map<UUID, PendingPasteOperation> pendingOperations = new ConcurrentHashMap<>();

    public void storeUndo(UUID playerId, UndoSnapshot snapshot) {
        undoSnapshots.put(playerId, snapshot);
    }

    public Optional<UndoSnapshot> getUndo(UUID playerId) {
        return Optional.ofNullable(undoSnapshots.get(playerId));
    }

    public void clearUndo(UUID playerId) {
        undoSnapshots.remove(playerId);
    }

    public void storePending(UUID playerId, PendingPasteOperation operation) {
        pendingOperations.put(playerId, operation);
    }

    public Optional<PendingPasteOperation> getPending(UUID playerId) {
        return Optional.ofNullable(pendingOperations.get(playerId));
    }

    public void clearPending(UUID playerId) {
        pendingOperations.remove(playerId);
    }

    public void clearAll() {
        undoSnapshots.clear();
        pendingOperations.clear();
    }
}

