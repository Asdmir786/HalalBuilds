package com.halalbuilds.schematic;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.halalbuilds.model.Dimensions;
import com.halalbuilds.model.Rotation;
import com.sk89q.worldedit.math.BlockVector3;
import org.junit.jupiter.api.Test;

class SchematicServiceTest {
    private final SchematicService schematicService = new SchematicService();

    @Test
    void swapsHorizontalDimensionsForQuarterTurns() {
        Dimensions source = new Dimensions(5, 3, 7);

        assertEquals(new Dimensions(7, 3, 5), schematicService.rotatedDimensions(source, Rotation.DEG_90));
        assertEquals(new Dimensions(7, 3, 5), schematicService.rotatedDimensions(source, Rotation.DEG_270));
        assertEquals(source, schematicService.rotatedDimensions(source, Rotation.DEG_180));
    }

    @Test
    void rotatesPointsAroundNormalizedClipboardOrigin() {
        Dimensions source = new Dimensions(5, 3, 7);
        BlockVector3 point = BlockVector3.at(0, 1, 2);

        assertEquals(BlockVector3.at(4, 1, 0), schematicService.rotatePoint(point, source, Rotation.DEG_90));
        assertEquals(BlockVector3.at(4, 1, 4), schematicService.rotatePoint(point, source, Rotation.DEG_180));
        assertEquals(BlockVector3.at(2, 1, 4), schematicService.rotatePoint(point, source, Rotation.DEG_270));
    }
}
