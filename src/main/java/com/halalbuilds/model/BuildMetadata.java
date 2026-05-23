package com.halalbuilds.model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import org.bukkit.configuration.file.YamlConfiguration;

public record BuildMetadata(
    String name,
    UUID creatorUuid,
    Instant createdAt,
    Dimensions dimensions,
    String originalWorld,
    Vector3i originOffset,
    List<String> tags,
    String notes
) {
    public BuildMetadata {
        tags = List.copyOf(tags == null ? List.of() : tags);
        notes = notes == null ? "" : notes;
    }

    public YamlConfiguration toYaml() {
        YamlConfiguration yaml = new YamlConfiguration();
        yaml.set("name", name);
        yaml.set("creator_uuid", creatorUuid == null ? null : creatorUuid.toString());
        yaml.set("created_at", createdAt.toString());
        yaml.set("dimensions.width", dimensions.width());
        yaml.set("dimensions.height", dimensions.height());
        yaml.set("dimensions.length", dimensions.length());
        yaml.set("original_world", originalWorld);
        yaml.set("origin_offset.x", originOffset.x());
        yaml.set("origin_offset.y", originOffset.y());
        yaml.set("origin_offset.z", originOffset.z());
        yaml.set("tags", new ArrayList<>(tags));
        yaml.set("notes", notes);
        return yaml;
    }

    public static BuildMetadata fromYaml(YamlConfiguration yaml) {
        String uuidValue = yaml.getString("creator_uuid");
        return new BuildMetadata(
            Objects.requireNonNullElse(yaml.getString("name"), ""),
            uuidValue == null || uuidValue.isBlank() ? null : UUID.fromString(uuidValue),
            Instant.parse(Objects.requireNonNullElse(yaml.getString("created_at"), Instant.EPOCH.toString())),
            new Dimensions(
                yaml.getInt("dimensions.width"),
                yaml.getInt("dimensions.height"),
                yaml.getInt("dimensions.length")
            ),
            Objects.requireNonNullElse(yaml.getString("original_world"), ""),
            new Vector3i(
                yaml.getInt("origin_offset.x"),
                yaml.getInt("origin_offset.y"),
                yaml.getInt("origin_offset.z")
            ),
            yaml.getStringList("tags"),
            Objects.requireNonNullElse(yaml.getString("notes"), "")
        );
    }
}

