package com.halalbuilds.storage;

import com.halalbuilds.config.HalalBuildsConfig;
import com.halalbuilds.model.BuildMetadata;
import com.halalbuilds.model.BuildRecord;
import com.halalbuilds.util.NameValidator;
import com.halalbuilds.util.PathGuard;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import org.bukkit.configuration.file.YamlConfiguration;

public final class BuildStorageService {
    private static final String METADATA_FILE = "metadata.yml";

    private final Path dataDirectory;
    private final Path buildsDirectory;
    private final Path importsDirectory;
    private final Path exportsDirectory;

    public BuildStorageService(Path dataDirectory) {
        this.dataDirectory = dataDirectory;
        this.buildsDirectory = dataDirectory.resolve("builds");
        this.importsDirectory = dataDirectory.resolve("imports");
        this.exportsDirectory = dataDirectory.resolve("exports");
    }

    public void ensureDirectories() throws IOException {
        Files.createDirectories(dataDirectory);
        Files.createDirectories(buildsDirectory);
        Files.createDirectories(importsDirectory);
        Files.createDirectories(exportsDirectory);
    }

    public Path buildsDirectory() {
        return buildsDirectory;
    }

    public Path importsDirectory() {
        return importsDirectory;
    }

    public Path exportsDirectory() {
        return exportsDirectory;
    }

    public BuildRecord prepareBuildRecord(String rawName) {
        String name = NameValidator.validateBuildName(rawName);
        Path buildDirectory = PathGuard.resolveInside(buildsDirectory, name);
        Path schematicPath = buildDirectory.resolve(name + ".schem").normalize();
        Path metadataPath = buildDirectory.resolve(METADATA_FILE).normalize();
        return new BuildRecord(null, buildDirectory, schematicPath, metadataPath);
    }

    public void assertWritable(String rawName, boolean allowOverwrite) {
        BuildRecord record = prepareBuildRecord(rawName);
        if (!allowOverwrite && Files.exists(record.directory())) {
            throw new IllegalArgumentException("A build named '" + rawName + "' already exists.");
        }
    }

    public void writeMetadata(BuildMetadata metadata) throws IOException {
        BuildRecord record = prepareBuildRecord(metadata.name());
        Files.createDirectories(record.directory());
        metadata.toYaml().save(record.metadataPath().toFile());
    }

    public BuildRecord loadRecord(String rawName) throws IOException {
        BuildRecord record = prepareBuildRecord(rawName);
        if (!Files.exists(record.schematicPath()) || !Files.exists(record.metadataPath())) {
            throw new IllegalArgumentException("No saved build named '" + rawName + "' exists.");
        }
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(record.metadataPath().toFile());
        return new BuildRecord(BuildMetadata.fromYaml(yaml), record.directory(), record.schematicPath(), record.metadataPath());
    }

    public List<BuildRecord> listBuilds() throws IOException {
        if (!Files.exists(buildsDirectory)) {
            return List.of();
        }
        List<BuildRecord> results = new ArrayList<>();
        try (var stream = Files.list(buildsDirectory)) {
            stream.filter(Files::isDirectory)
                .sorted(Comparator.comparing(path -> path.getFileName().toString()))
                .forEach(path -> {
                    Path metadataPath = path.resolve(METADATA_FILE);
                    if (Files.exists(metadataPath)) {
                        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(metadataPath.toFile());
                        BuildMetadata metadata = BuildMetadata.fromYaml(yaml);
                        String name = metadata.name();
                        results.add(new BuildRecord(
                            metadata,
                            path,
                            path.resolve(name + ".schem"),
                            metadataPath
                        ));
                    }
                });
        }
        return results;
    }

    public void deleteBuild(String rawName) throws IOException {
        BuildRecord record = loadRecord(rawName);
        try (var stream = Files.walk(record.directory())) {
            stream.sorted(Comparator.reverseOrder()).forEach(path -> {
                try {
                    Files.deleteIfExists(path);
                } catch (IOException exception) {
                    throw new RuntimeException(exception);
                }
            });
        } catch (RuntimeException exception) {
            if (exception.getCause() instanceof IOException ioException) {
                throw ioException;
            }
            throw exception;
        }
    }

    public Path resolveImportPath(String filename, HalalBuildsConfig config) {
        String validated = NameValidator.validateImportFileName(filename);
        String normalized = validated.toLowerCase(Locale.ROOT);
        boolean allowed = config.allowedImportExtensions().stream()
            .map(extension -> extension.toLowerCase(Locale.ROOT))
            .anyMatch(normalized::endsWith);
        if (!allowed) {
            throw new IllegalArgumentException("That file extension is not allowed for imports.");
        }
        return PathGuard.resolveInside(importsDirectory, validated);
    }

    public Path exportBuild(String rawName) throws IOException {
        BuildRecord record = loadRecord(rawName);
        Path exportPath = PathGuard.resolveInside(exportsDirectory, record.metadata().name() + ".schem");
        Files.copy(record.schematicPath(), exportPath, StandardCopyOption.REPLACE_EXISTING);
        return exportPath;
    }
}
