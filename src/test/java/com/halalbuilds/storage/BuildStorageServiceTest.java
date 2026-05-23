package com.halalbuilds.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.halalbuilds.config.HalalBuildsConfig;
import com.halalbuilds.model.PasteMode;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import org.bukkit.Material;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class BuildStorageServiceTest {
    @TempDir
    Path tempDir;

    @Test
    void acceptsImportExtensionsCaseInsensitively() {
        BuildStorageService storageService = new BuildStorageService(tempDir);
        HalalBuildsConfig config = new HalalBuildsConfig(
            1,
            1,
            1,
            1,
            PasteMode.EXACT,
            true,
            true,
            true,
            5,
            Material.STONE,
            true,
            true,
            true,
            Set.of(),
            false,
            List.of(".schem"),
            true,
            true,
            new HalalBuildsConfig.WorldsConfig("allow_all", Set.of(), Set.of()),
            "&a[HalalBuilds]&r "
        );

        assertEquals(tempDir.resolve("imports").resolve("Castle.SCHEM").normalize(), storageService.resolveImportPath("Castle.SCHEM", config));
    }
}
