package com.halalbuilds.config;

import com.halalbuilds.model.PasteMode;
import java.util.List;
import java.util.Set;
import org.bukkit.Material;

public record HalalBuildsConfig(
    int maxSelectionVolume,
    int maxPasteVolumeBeforePreview,
    int maxTerrainChangesBeforeConfirm,
    int maxFoundationDepth,
    PasteMode defaultPasteMode,
    boolean pasteAirBlocks,
    boolean clearTerrainAboveFootprint,
    boolean requirePreviewForLargePastes,
    int pendingOperationTimeoutSeconds,
    Material foundationMaterial,
    boolean fillUnderFootprint,
    boolean onlyFillUnderNonAirBlocks,
    boolean stopOnDenylistedBlocks,
    Set<Material> denylistedBlocks,
    boolean allowOverwrite,
    List<String> allowedImportExtensions,
    boolean requireFawe,
    boolean disablePluginIfMissing,
    WorldsConfig worlds,
    String messagePrefix
) {
    public record WorldsConfig(String mode, Set<String> enabledWorlds, Set<String> disabledWorlds) {
        public boolean isWorldEnabled(String worldName) {
            return switch (mode) {
                case "allow_list" -> enabledWorlds.contains(worldName);
                case "deny_list" -> !disabledWorlds.contains(worldName);
                default -> true;
            };
        }
    }
}

