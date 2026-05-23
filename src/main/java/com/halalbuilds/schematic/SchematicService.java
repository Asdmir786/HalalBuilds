package com.halalbuilds.schematic;

import com.halalbuilds.model.Dimensions;
import com.halalbuilds.model.Rotation;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.BuiltInClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.function.operation.ForwardExtentCopy;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import org.bukkit.World;

public final class SchematicService {
    public Clipboard copyRegion(World world, Region region) {
        Dimensions dimensions = dimensionsOf(region);
        CuboidRegion clipboardRegion = new CuboidRegion(
            BlockVector3.ZERO,
            BlockVector3.at(dimensions.width() - 1, dimensions.height() - 1, dimensions.length() - 1)
        );
        BlockArrayClipboard clipboard = new BlockArrayClipboard(clipboardRegion);
        clipboard.setOrigin(BlockVector3.ZERO);

        try (EditSession source = WorldEdit.getInstance().newEditSession(BukkitAdapter.adapt(world))) {
            ForwardExtentCopy copy = new ForwardExtentCopy(source, region, clipboard, BlockVector3.ZERO);
            copy.setCopyingEntities(true);
            Operations.complete(copy);
        }
        return clipboard;
    }

    public Clipboard copyCuboid(World world, BlockVector3 minimum, BlockVector3 maximum) {
        return copyRegion(world, new CuboidRegion(BukkitAdapter.adapt(world), minimum, maximum));
    }

    public void writeSchematic(Clipboard clipboard, Path path) throws IOException {
        Files.createDirectories(path.getParent());
        try (OutputStream outputStream = Files.newOutputStream(path);
             var writer = BuiltInClipboardFormat.SPONGE_SCHEMATIC.getWriter(outputStream)) {
            writer.write(clipboard);
        }
    }

    public Clipboard readSchematic(Path path) throws IOException {
        ClipboardFormat format = ClipboardFormats.findByFile(path.toFile());
        if (format == null) {
            throw new IllegalArgumentException("WorldEdit could not determine the schematic format for " + path.getFileName());
        }
        try (InputStream inputStream = Files.newInputStream(path);
             var reader = format.getReader(inputStream)) {
            return normalize(reader.read());
        }
    }

    public Clipboard normalize(Clipboard source) {
        Dimensions dimensions = dimensionsOf(source.getRegion());
        CuboidRegion targetRegion = new CuboidRegion(
            BlockVector3.ZERO,
            BlockVector3.at(dimensions.width() - 1, dimensions.height() - 1, dimensions.length() - 1)
        );
        BlockArrayClipboard normalized = new BlockArrayClipboard(targetRegion);
        normalized.setOrigin(BlockVector3.ZERO);
        for (BlockVector3 sourcePoint : source.getRegion()) {
            BlockVector3 minimum = source.getRegion().getMinimumPoint();
            BlockVector3 destination = sourcePoint.subtract(minimum);
            normalized.setBlock(destination, source.getFullBlock(sourcePoint));
            if (source.hasBiomes()) {
                normalized.setBiome(destination, source.getBiome(sourcePoint));
            }
        }
        return normalized;
    }

    public Clipboard rotate(Clipboard source, Rotation rotation) {
        if (rotation == Rotation.DEG_0) {
            return source;
        }

        Dimensions sourceDimensions = dimensionsOf(source.getRegion());
        Dimensions rotatedDimensions = rotatedDimensions(sourceDimensions, rotation);
        CuboidRegion rotatedRegion = new CuboidRegion(
            BlockVector3.ZERO,
            BlockVector3.at(rotatedDimensions.width() - 1, rotatedDimensions.height() - 1, rotatedDimensions.length() - 1)
        );
        BlockArrayClipboard rotated = new BlockArrayClipboard(rotatedRegion);
        rotated.setOrigin(BlockVector3.ZERO);

        BlockVector3 minimum = source.getRegion().getMinimumPoint();
        for (BlockVector3 sourcePoint : source.getRegion()) {
            BlockVector3 local = sourcePoint.subtract(minimum);
            BlockVector3 rotatedPoint = rotatePoint(local, sourceDimensions, rotation);
            rotated.setBlock(rotatedPoint, source.getFullBlock(sourcePoint));
            if (source.hasBiomes()) {
                rotated.setBiome(rotatedPoint, source.getBiome(sourcePoint));
            }
        }
        return rotated;
    }

    public Dimensions dimensionsOf(Region region) {
        return new Dimensions(region.getWidth(), region.getHeight(), region.getLength());
    }

    public Dimensions rotatedDimensions(Dimensions dimensions, Rotation rotation) {
        return switch (rotation) {
            case DEG_0, DEG_180 -> dimensions;
            case DEG_90, DEG_270 -> new Dimensions(dimensions.length(), dimensions.height(), dimensions.width());
        };
    }

    public BlockVector3 rotatePoint(BlockVector3 point, Dimensions sourceDimensions, Rotation rotation) {
        int x = point.x();
        int y = point.y();
        int z = point.z();
        return switch (rotation) {
            case DEG_0 -> BlockVector3.at(x, y, z);
            case DEG_90 -> BlockVector3.at(sourceDimensions.length() - 1 - z, y, x);
            case DEG_180 -> BlockVector3.at(sourceDimensions.width() - 1 - x, y, sourceDimensions.length() - 1 - z);
            case DEG_270 -> BlockVector3.at(z, y, sourceDimensions.width() - 1 - x);
        };
    }
}
