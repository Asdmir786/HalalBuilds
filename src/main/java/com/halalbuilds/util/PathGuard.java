package com.halalbuilds.util;

import java.nio.file.Path;

public final class PathGuard {
    private PathGuard() {
    }

    public static Path resolveInside(Path baseDirectory, String child) {
        Path resolved = baseDirectory.resolve(child).normalize();
        if (!resolved.startsWith(baseDirectory.normalize())) {
            throw new IllegalArgumentException("Path escapes the HalalBuilds storage folder.");
        }
        return resolved;
    }
}

