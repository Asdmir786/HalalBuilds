package com.halalbuilds;

import com.halalbuilds.clipboard.ClipboardService;
import com.halalbuilds.command.HalalBuildsCommand;
import com.halalbuilds.config.ConfigService;
import com.halalbuilds.config.HalalBuildsConfig;
import com.halalbuilds.paste.PasteService;
import com.halalbuilds.preview.VisualPreviewService;
import com.halalbuilds.schematic.SchematicService;
import com.halalbuilds.selection.SelectionService;
import com.halalbuilds.storage.BuildStorageService;
import com.halalbuilds.terrain.TerrainPlanner;
import com.halalbuilds.undo.UndoService;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

public final class HalalBuildsPlugin extends JavaPlugin {
    private ConfigService configService;
    private BuildStorageService storageService;
    private SelectionService selectionService;
    private SchematicService schematicService;
    private ClipboardService clipboardService;
    private UndoService undoService;
    private PasteService pasteService;
    private VisualPreviewService visualPreviewService;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        this.configService = new ConfigService(getLogger());
        reloadPluginConfig();

        if (!checkFaweDependency()) {
            return;
        }

        this.storageService = new BuildStorageService(Path.of(getDataFolder().toURI()));
        this.selectionService = new SelectionService();
        this.schematicService = new SchematicService();
        this.clipboardService = new ClipboardService();
        this.undoService = new UndoService();
        this.visualPreviewService = new VisualPreviewService(this);
        this.pasteService = new PasteService(
            storageService,
            schematicService,
            clipboardService,
            new TerrainPlanner(schematicService),
            undoService
        );

        try {
            storageService.ensureDirectories();
        } catch (IOException exception) {
            getLogger().severe("Failed to create HalalBuilds data folders: " + exception.getMessage());
            exception.printStackTrace();
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        PluginCommand command = Objects.requireNonNull(getCommand("halalbuilds"), "plugin.yml command not loaded");
        HalalBuildsCommand executor = new HalalBuildsCommand(this);
        command.setExecutor(executor);
        command.setTabCompleter(executor);

        getLogger().info("HalalBuilds enabled.");
    }

    @Override
    public void onDisable() {
        if (clipboardService != null) {
            clipboardService.clearAll();
        }
        if (undoService != null) {
            undoService.clearAll();
        }
        if (visualPreviewService != null) {
            visualPreviewService.clearAll();
        }
    }

    public void reloadPluginConfig() {
        reloadConfig();
        configService.load(getConfig());
    }

    private boolean checkFaweDependency() {
        boolean installed = getServer().getPluginManager().getPlugin("FastAsyncWorldEdit") != null;
        if (installed || !config().requireFawe()) {
            return true;
        }

        String message = "FastAsyncWorldEdit is required for HalalBuilds v1 but was not found.";
        if (config().disablePluginIfMissing()) {
            getLogger().severe(message + " Disabling plugin.");
            getServer().getPluginManager().disablePlugin(this);
            return false;
        }

        getLogger().warning(message + " The plugin will remain loaded but unusable.");
        return true;
    }

    public HalalBuildsConfig config() {
        return configService.config();
    }

    public BuildStorageService storageService() {
        return storageService;
    }

    public SelectionService selectionService() {
        return selectionService;
    }

    public SchematicService schematicService() {
        return schematicService;
    }

    public ClipboardService clipboardService() {
        return clipboardService;
    }

    public UndoService undoService() {
        return undoService;
    }

    public PasteService pasteService() {
        return pasteService;
    }

    public VisualPreviewService visualPreviewService() {
        return visualPreviewService;
    }
}
