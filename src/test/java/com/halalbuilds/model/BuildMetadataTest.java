package com.halalbuilds.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class BuildMetadataTest {
    @Test
    void yamlRoundTripPreservesMetadata() {
        BuildMetadata original = new BuildMetadata(
            "example_house",
            UUID.fromString("00000000-0000-0000-0000-000000000123"),
            Instant.parse("2026-05-19T00:00:00Z"),
            new Dimensions(12, 8, 14),
            "world",
            new Vector3i(0, 0, 0),
            List.of("starter", "v1"),
            "A simple starter house."
        );

        BuildMetadata restored = BuildMetadata.fromYaml(original.toYaml());
        assertEquals(original, restored);
    }
}

