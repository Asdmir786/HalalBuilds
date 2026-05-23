package com.halalbuilds.model;

import java.nio.file.Path;

public record BuildRecord(BuildMetadata metadata, Path directory, Path schematicPath, Path metadataPath) {
}

