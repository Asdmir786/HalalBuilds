package com.halalbuilds.config;

import com.halalbuilds.model.PasteMode;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.logging.Logger;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

public final class ConfigService {
    private final Logger logger;
    private HalalBuildsConfig config;

    public ConfigService(Logger logger) {
        this.logger = logger;
    }

    public HalalBuildsConfig load(FileConfiguration raw) {
        ConfigurationSection limits = raw.getConfigurationSection("limits");
        ConfigurationSection paste = raw.getConfigurationSection("paste");
        ConfigurationSection preview = raw.getConfigurationSection("preview");
        ConfigurationSection entities = raw.getConfigurationSection("entities");
        ConfigurationSection foundation = raw.getConfigurationSection("foundation");
        ConfigurationSection protection = raw.getConfigurationSection("protection");
        ConfigurationSection storage = raw.getConfigurationSection("storage");
        ConfigurationSection dependencyCheck = raw.getConfigurationSection("dependency-check");
        ConfigurationSection worlds = raw.getConfigurationSection("worlds");

        Material foundationMaterial = parseMaterial(
            foundation == null ? null : foundation.getString("material"),
            Material.STONE_BRICKS
        );

        Set<Material> denylisted = new LinkedHashSet<>();
        List<String> denylistedNames = protection == null ? List.of() : protection.getStringList("denylisted-blocks");
        for (String entry : denylistedNames) {
            Material material = parseMaterial(entry, null);
            if (material != null) {
                denylisted.add(material);
            }
        }

        config = new HalalBuildsConfig(
            Math.max(1, getInt(limits, "max-selection-volume", 250000)),
            Math.max(1, getInt(limits, "max-paste-volume-before-preview", 75000)),
            Math.max(1, getInt(limits, "max-terrain-changes-before-confirm", 10000)),
            Math.max(1, getInt(limits, "max-foundation-depth", 64)),
            PasteMode.fromConfigValue(getString(paste, "default-mode", "smart_foundation")),
            getBoolean(paste, "paste-air-blocks", false),
            getBoolean(paste, "clear-terrain-above-footprint", true),
            getBoolean(paste, "require-preview-for-large-pastes", true),
            Math.max(5, getInt(paste, "pending-operation-timeout-seconds", 120)),
            new HalalBuildsConfig.PreviewConfig(
                getBoolean(preview, "enabled", true),
                Math.max(1, getInt(preview, "refresh-seconds", 2)),
                parseParticle(getString(preview, "particle", "END_ROD"), Particle.END_ROD),
                getBoolean(preview, "show-corners", true),
                getBoolean(preview, "show-height-pillars", true),
                getBoolean(preview, "show-facing-arrow", true)
            ),
            new HalalBuildsConfig.EntityConfig(
                getBoolean(entities, "save-entities", true),
                getBoolean(entities, "paste-entities", true)
            ),
            foundationMaterial,
            getBoolean(foundation, "fill-under-footprint", true),
            getBoolean(foundation, "only-fill-under-non-air-blocks", true),
            getBoolean(protection, "stop-on-denylisted-blocks", true),
            Set.copyOf(denylisted),
            getBoolean(storage, "allow-overwrite", false),
            List.copyOf(storage == null ? List.of(".schem") : storage.getStringList("allowed-import-extensions")),
            getBoolean(dependencyCheck, "require-fawe", true),
            getBoolean(dependencyCheck, "disable-plugin-if-missing", true),
            new HalalBuildsConfig.WorldsConfig(
                normalizeWorldMode(getString(worlds, "mode", "allow_all")),
                Set.copyOf(worlds == null ? List.of() : worlds.getStringList("enabled")),
                Set.copyOf(worlds == null ? List.of() : worlds.getStringList("disabled"))
            ),
            raw.getString("messages.prefix", "&a[HalalBuilds]&r ")
        );
        return config;
    }

    public HalalBuildsConfig config() {
        if (config == null) {
            throw new IllegalStateException("Config has not been loaded yet.");
        }
        return config;
    }

    private Material parseMaterial(String name, Material fallback) {
        if (name == null || name.isBlank()) {
            return fallback;
        }
        Material material = Material.matchMaterial(name.trim().toUpperCase(Locale.ROOT));
        if (material == null) {
            logger.warning("Unknown material in config: " + name + ". Using " + fallback + " instead.");
            return fallback;
        }
        return material;
    }

    private Particle parseParticle(String name, Particle fallback) {
        if (name == null || name.isBlank()) {
            return fallback;
        }
        try {
            return Particle.valueOf(name.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException exception) {
            logger.warning("Unknown particle in config: " + name + ". Using " + fallback + " instead.");
            return fallback;
        }
    }

    private static int getInt(ConfigurationSection section, String path, int fallback) {
        return section == null ? fallback : section.getInt(path, fallback);
    }

    private static boolean getBoolean(ConfigurationSection section, String path, boolean fallback) {
        return section == null ? fallback : section.getBoolean(path, fallback);
    }

    private static String getString(ConfigurationSection section, String path, String fallback) {
        return section == null ? fallback : section.getString(path, fallback);
    }

    private static String normalizeWorldMode(String mode) {
        return switch (mode.toLowerCase(Locale.ROOT)) {
            case "allow_list", "deny_list", "allow_all" -> mode.toLowerCase(Locale.ROOT);
            default -> "allow_all";
        };
    }
}
