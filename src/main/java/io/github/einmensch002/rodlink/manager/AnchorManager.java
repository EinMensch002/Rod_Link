package io.github.einmensch002.rodlink.manager;

import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Manager zur Erstellung und sicheren Entfernung von Marker ArmorStands als Stasis-Anker.
 */
public class AnchorManager {

    private final Plugin plugin;
    private final NamespacedKey keyAnchor;

    public AnchorManager(@NotNull Plugin plugin) {
        this.plugin = plugin;
        this.keyAnchor = new NamespacedKey(plugin, "stasis_anchor");
    }

    /**
     * Spawnt einen unsichtbaren, unzerstörbaren Marker-ArmorStand an der angegebenen Location.
     * Markiert das Entity im PDC zur Crash-Sicherheit und Wiedererkennung.
     *
     * @param location Die Ziel-Location für den Anker.
     * @return Die UUID des gespawnten ArmorStands.
     */
    public @NotNull UUID spawnAnchor(@NotNull Location location) {
        World world = location.getWorld();
        if (world == null) {
            throw new IllegalArgumentException("Die angegebene Location besitzt keine gültige Welt.");
        }

        ArmorStand armorStand = world.spawn(location, ArmorStand.class, stand -> {
            stand.setMarker(true);
            stand.setInvisible(true);
            stand.setInvulnerable(true);
            stand.setGravity(false);
            stand.getPersistentDataContainer().set(keyAnchor, PersistentDataType.BOOLEAN, true);
        });

        UUID uuid = armorStand.getUniqueId();
        debugLog("Stasis-Anker ArmorStand gespawnt an " + location + " [UUID: " + uuid + "]");
        return uuid;
    }

    /**
     * Entfernt alle Stasis-Anker, die exakt auf dem Block der angegebenen Druckplatte liegen.
     * Der Chunk muss bereits geladen sein (er wird hier bewusst NICHT geladen).
     * Anker werden über ihre Block-Koordinaten gefunden, damit Anker benachbarter Platten unberührt bleiben.
     *
     * @param plate Block-Location der Druckplatte.
     * @return Anzahl der entfernten Anker.
     */
    public int removeAnchorsAt(@NotNull Location plate) {
        World world = plate.getWorld();
        if (world == null || !world.isChunkLoaded(plate.getBlockX() >> 4, plate.getBlockZ() >> 4)) {
            debugLog("Chunk an " + plate + " ist nicht geladen. Anker-Entfernung übersprungen.");
            return 0;
        }

        int removed = 0;
        for (Entity entity : world.getNearbyEntities(plate, 1.5, 1.5, 1.5)) {
            if (entity instanceof ArmorStand stand && isStasisAnchor(stand)) {
                Location standLoc = stand.getLocation();
                if (standLoc.getBlockX() == plate.getBlockX()
                    && standLoc.getBlockY() == plate.getBlockY()
                    && standLoc.getBlockZ() == plate.getBlockZ()) {
                    stand.remove();
                    removed++;
                }
            }
        }

        debugLog(removed + " Stasis-Anker an " + plate + " entfernt.");
        return removed;
    }

    /**
     * Prüft, ob das angegebene Entity ein im PDC markierter Stasis-Anker ist.
     */
    public boolean isStasisAnchor(@NotNull ArmorStand stand) {
        return Boolean.TRUE.equals(stand.getPersistentDataContainer().get(keyAnchor, PersistentDataType.BOOLEAN));
    }

    /**
     * Konsolen-Debug-Ausgabe, steuerbar über config.yml (debug: true).
     */
    public void debugLog(@NotNull String message) {
        if (plugin.getConfig().getBoolean("debug", false)) {
            plugin.getLogger().info("[DEBUG - AnchorManager] " + message);
        }
    }
}
