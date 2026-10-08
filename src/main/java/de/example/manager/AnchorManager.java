
package de.example.plugin.manager;

import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
     * Entfernt den Stasis-Anker ArmorStand sauber aus der Welt.
     * Nutzt bei Fehlschlagen von getEntity (z. B. nach Server-Restart) eine Chunk-Prüfung und Umkreissuche.
     *
     * @param world      Die Welt, in der sich der Anker befindet.
     * @param anchorUuid Die UUID des gesuchten ArmorStands (kann null sein).
     * @param location   Die Location der verlinkten Druckplatte.
     */
    public void removeAnchor(@NotNull World world, @Nullable UUID anchorUuid, @NotNull Location location) {
        // 1. Primärer Versuch über direkte UUID-Abfrage
        if (anchorUuid != null) {
            Entity entity = world.getEntity(anchorUuid);
            if (entity instanceof ArmorStand stand && isStasisAnchor(stand)) {
                stand.remove();
                debugLog("Anker-ArmorStand über UUID (" + anchorUuid + ") erfolgreich entfernt.");
                return;
            }
        }

        // 2. Fallback: Chunk-Ladestatus prüfen & Umkreissuche durchführen
        if (!world.isChunkLoaded(location)) {
            debugLog("Chunk an " + location + " ist aktuell nicht geladen. Entfernung übersprungen.");
            return;
        }

        var nearbyEntities = world.getNearbyEntities(location, 1.5, 1.5, 1.5);
        for (Entity entity : nearbyEntities) {
            if (entity instanceof ArmorStand stand && isStasisAnchor(stand)) {
                UUID removedUuid = stand.getUniqueId();
                stand.remove();
                debugLog("Anker-ArmorStand über Fallback-Umkreissuche an " + location + " entfernt [UUID: " + removedUuid + "].");
                return;
            }
        }

        debugLog("Kein aktiver Stasis-Anker ArmorStand an Location " + location + " gefunden.");
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
