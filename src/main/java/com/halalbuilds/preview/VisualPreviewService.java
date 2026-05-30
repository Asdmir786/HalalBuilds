package com.halalbuilds.preview;

import com.halalbuilds.HalalBuildsPlugin;
import com.halalbuilds.config.HalalBuildsConfig;
import com.halalbuilds.model.Dimensions;
import com.halalbuilds.model.PendingPasteOperation;
import com.halalbuilds.model.Rotation;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

public final class VisualPreviewService {
    private final HalalBuildsPlugin plugin;
    private final Map<UUID, BukkitTask> tasks = new ConcurrentHashMap<>();

    public VisualPreviewService(HalalBuildsPlugin plugin) {
        this.plugin = plugin;
    }

    public void show(Player player, PendingPasteOperation pending) {
        clear(player.getUniqueId());
        if (!plugin.config().preview().enabled()) {
            return;
        }

        render(player, pending);
        long periodTicks = Math.max(20L, plugin.config().preview().refreshSeconds() * 20L);
        BukkitTask task = plugin.getServer().getScheduler().runTaskTimer(
            plugin,
            () -> {
                if (!player.isOnline() || plugin.pasteService().getPending(player.getUniqueId(), plugin.config()).isEmpty()) {
                    clear(player.getUniqueId());
                    return;
                }
                render(player, pending);
            },
            periodTicks,
            periodTicks
        );
        tasks.put(player.getUniqueId(), task);
    }

    public void clear(UUID playerId) {
        BukkitTask task = tasks.remove(playerId);
        if (task != null) {
            task.cancel();
        }
    }

    public void clearAll() {
        tasks.keySet().forEach(this::clear);
    }

    private void render(Player player, PendingPasteOperation pending) {
        Location target = pending.targetLocation();
        World world = target.getWorld();
        if (world == null || !world.equals(player.getWorld())) {
            return;
        }

        HalalBuildsConfig.PreviewConfig preview = plugin.config().preview();
        Particle particle = preview.particle();
        Dimensions dimensions = pending.summary().dimensions();
        double minX = target.getBlockX() + 0.5;
        double minY = target.getBlockY() + 0.15;
        double minZ = target.getBlockZ() + 0.5;
        double maxX = target.getBlockX() + dimensions.width() - 0.5;
        double maxY = target.getBlockY() + dimensions.height() - 0.5;
        double maxZ = target.getBlockZ() + dimensions.length() - 0.5;

        drawLine(player, particle, minX, minY, minZ, maxX, minY, minZ);
        drawLine(player, particle, maxX, minY, minZ, maxX, minY, maxZ);
        drawLine(player, particle, maxX, minY, maxZ, minX, minY, maxZ);
        drawLine(player, particle, minX, minY, maxZ, minX, minY, minZ);

        if (preview.showHeightPillars()) {
            drawLine(player, particle, minX, minY, minZ, minX, maxY, minZ);
            drawLine(player, particle, maxX, minY, minZ, maxX, maxY, minZ);
            drawLine(player, particle, maxX, minY, maxZ, maxX, maxY, maxZ);
            drawLine(player, particle, minX, minY, maxZ, minX, maxY, maxZ);
        }

        if (preview.showCorners()) {
            spawn(player, particle, minX, minY, minZ, 8);
            spawn(player, particle, maxX, minY, minZ, 8);
            spawn(player, particle, maxX, minY, maxZ, 8);
            spawn(player, particle, minX, minY, maxZ, 8);
        }

        if (preview.showFacingArrow()) {
            drawFacingArrow(player, particle, target, dimensions, pending.rotation());
        }
    }

    private void drawFacingArrow(Player player, Particle particle, Location target, Dimensions dimensions, Rotation rotation) {
        double centerX = target.getBlockX() + dimensions.width() / 2.0;
        double y = target.getBlockY() + 1.0;
        double centerZ = target.getBlockZ() + dimensions.length() / 2.0;
        int dx = switch (rotation) {
            case DEG_90 -> 1;
            case DEG_270 -> -1;
            default -> 0;
        };
        int dz = switch (rotation) {
            case DEG_0 -> 1;
            case DEG_180 -> -1;
            default -> 0;
        };
        double length = Math.max(2.0, Math.min(6.0, Math.max(dimensions.width(), dimensions.length()) / 2.0));
        double endX = centerX + dx * length;
        double endZ = centerZ + dz * length;
        drawLine(player, particle, centerX, y, centerZ, endX, y, endZ);
        spawn(player, particle, endX, y, endZ, 10);
    }

    private void drawLine(Player player, Particle particle, double x1, double y1, double z1, double x2, double y2, double z2) {
        double distance = Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2) + Math.pow(z2 - z1, 2));
        int steps = Math.max(1, (int) Math.ceil(distance));
        for (int index = 0; index <= steps; index++) {
            double t = index / (double) steps;
            spawn(player, particle, lerp(x1, x2, t), lerp(y1, y2, t), lerp(z1, z2, t), 1);
        }
    }

    private void spawn(Player player, Particle particle, double x, double y, double z, int count) {
        player.spawnParticle(particle, x, y, z, count, 0.02, 0.02, 0.02, 0.0);
    }

    private double lerp(double start, double end, double t) {
        return start + (end - start) * t;
    }
}
