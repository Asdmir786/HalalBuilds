package com.halalbuilds.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.file.Path;
import org.junit.jupiter.api.Test;

class PathGuardTest {
    @Test
    void resolvesSafeChildInsideBaseDirectory() {
        Path base = Path.of("plugins", "HalalBuilds", "imports");
        Path resolved = PathGuard.resolveInside(base, "house.schem");
        assertEquals(base.resolve("house.schem").normalize(), resolved);
    }

    @Test
    void rejectsEscapingChildPath() {
        Path base = Path.of("plugins", "HalalBuilds", "imports");
        assertThrows(IllegalArgumentException.class, () -> PathGuard.resolveInside(base, "..\\exports\\house.schem"));
    }
}
