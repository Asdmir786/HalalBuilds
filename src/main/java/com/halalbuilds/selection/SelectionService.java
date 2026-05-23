package com.halalbuilds.selection;

import com.halalbuilds.model.Dimensions;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.session.SessionManager;
import org.bukkit.entity.Player;

public final class SelectionService {
    public SelectionData getSelection(Player bukkitPlayer) throws IncompleteRegionException {
        com.sk89q.worldedit.entity.Player actor = BukkitAdapter.adapt(bukkitPlayer);
        SessionManager manager = WorldEdit.getInstance().getSessionManager();
        LocalSession session = manager.get(actor);
        var selectionWorld = session.getSelectionWorld();
        if (selectionWorld == null) {
            throw new IncompleteRegionException();
        }
        Region region = session.getSelection(selectionWorld);
        return new SelectionData(
            BukkitAdapter.adapt(selectionWorld),
            region,
            new Dimensions(region.getWidth(), region.getHeight(), region.getLength())
        );
    }

    public record SelectionData(org.bukkit.World world, Region region, Dimensions dimensions) {
        public long volume() {
            return (long) dimensions.width() * (long) dimensions.height() * (long) dimensions.length();
        }
    }
}
