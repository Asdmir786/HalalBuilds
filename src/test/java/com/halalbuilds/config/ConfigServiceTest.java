package com.halalbuilds.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.halalbuilds.model.PasteMode;
import java.util.logging.Logger;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Test;

class ConfigServiceTest {
    @Test
    void loadsExpectedDefaultsAndFallsBackSafely() {
        YamlConfiguration yaml = new YamlConfiguration();
        yaml.set("limits.max-selection-volume", 250000);
        yaml.set("limits.max-paste-volume-before-preview", 75000);
        yaml.set("limits.max-terrain-changes-before-confirm", 10000);
        yaml.set("limits.max-foundation-depth", 64);
        yaml.set("paste.default-mode", "not_a_real_mode");
        yaml.set("paste.paste-air-blocks", true);
        yaml.set("paste.clear-terrain-above-footprint", true);
        yaml.set("paste.require-preview-for-large-pastes", true);
        yaml.set("paste.pending-operation-timeout-seconds", 120);
        yaml.set("foundation.material", "STONE_BRICKS");
        yaml.set("foundation.fill-under-footprint", true);
        yaml.set("foundation.only-fill-under-non-air-blocks", true);
        yaml.set("protection.stop-on-denylisted-blocks", true);
        yaml.set("protection.denylisted-blocks", java.util.List.of("CHEST", "BEDROCK"));
        yaml.set("storage.allow-overwrite", false);
        yaml.set("storage.allowed-import-extensions", java.util.List.of(".schem"));
        yaml.set("dependency-check.require-fawe", true);
        yaml.set("dependency-check.disable-plugin-if-missing", true);
        yaml.set("worlds.mode", "allow_all");
        yaml.set("worlds.enabled", java.util.List.of("world"));
        yaml.set("worlds.disabled", java.util.List.of());
        yaml.set("messages.prefix", "&a[HalalBuilds]&r ");

        HalalBuildsConfig config = new ConfigService(Logger.getLogger("test")).load(yaml);
        assertEquals(PasteMode.SMART_FOUNDATION, config.defaultPasteMode());
        assertEquals(Material.STONE_BRICKS, config.foundationMaterial());
        assertTrue(config.denylistedBlocks().contains(Material.CHEST));
        assertTrue(config.worlds().isWorldEnabled("world"));
    }
}

