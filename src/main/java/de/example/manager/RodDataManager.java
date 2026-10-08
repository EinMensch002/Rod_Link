
package de.example.plugin.manager;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

/**
 * Utility-Klasse zur Datenverwaltung verlinkter Angelruten im PersistentDataContainer (PDC).
 */
public class RodDataManager {

    private final NamespacedKey keyWorldUuid;
    private final NamespacedKey keyX;
    private final NamespacedKey keyY;
    private final NamespacedKey keyZ;
    private final NamespacedKey keyArmorStandUuid;
    private final NamespacedKey keySingleUse;

    public RodDataManager(@NotNull Plugin plugin) {
        this.keyWorldUuid = new NamespacedKey(plugin, "rod_world_uuid");
        this.keyX = new NamespacedKey(plugin, "rod_x");
        this.keyY = new NamespacedKey(plugin, "rod_y");
        this.keyZ = new NamespacedKey(plugin, "rod_z");
        this.keyArmorStandUuid = new NamespacedKey(plugin, "rod_armorstand_uuid");
        this.keySingleUse = new NamespacedKey(plugin, "rod_single_use");
    }

    /**
     * Unveränderlicher Datencontainer für die ausgelesenen Angel-Daten.
     */
    public record RodData(
        @NotNull UUID worldUuid,
        double x,
        double y,
        double z,
        @NotNull UUID armorStandUuid,
        boolean isSingleUse
    ) {
        /**
         * Konvertiert die PDC-Koordinaten wieder in ein Bukkit Location-Objekt.
         */
        @Nullable,
        public Location toLocation() {
            World world = Bukkit.getWorld(worldUuid);
            return world != null ? new Location(world, x, y, z) : null;
        }
    }

    /**
     * Schreibt die Verlinkungsdaten (Welt-UUID, X/Y/Z, ArmorStand-UUID, Single-Use) in den PDC der Angel.
     */
    public void writeRodData(
        @NotNull ItemStack rod,
        @NotNull Location location,
        @NotNull UUID armorStandUuid,
        boolean isSingleUse
    ) {
        if (rod.getType().isAir() || location.getWorld() == null) {
            return;
        }

        rod.editPersistentDataContainer(pdc -> {
            pdc.set(keyWorldUuid, PersistentDataType.STRING, location.getWorld().getUID().toString());
            pdc.set(keyX, PersistentDataType.DOUBLE, location.getX());
            pdc.set(keyY, PersistentDataType.DOUBLE, location.getY());
            pdc.set(keyZ, PersistentDataType.DOUBLE, location.getZ());
            pdc.set(keyArmorStandUuid, PersistentDataType.STRING, armorStandUuid.toString());
            pdc.set(keySingleUse, PersistentDataType.BOOLEAN, isSingleUse);
        });
    }

    /**
     * Liest die Verlinkungsdaten aus dem PDC der Angel aus.
     * 
     * @return Ein Optional mit den RodData, oder Optional.empty(), falls keine oder unvollständige Daten vorliegen.
     */
    public Optional<RodData> readRodData(@NotNull ItemStack rod) {
        if (rod.getType().isAir() || !isLinked(rod)) {
            return Optional.empty();
        }

        var pdc = rod.getPersistentDataContainer();

        String worldUuidStr = pdc.get(keyWorldUuid, PersistentDataType.STRING);
        Double x = pdc.get(keyX, PersistentDataType.DOUBLE);
        Double y = pdc.get(keyY, PersistentDataType.DOUBLE);
        Double z = pdc.get(keyZ, PersistentDataType.DOUBLE);
        String armorStandUuidStr = pdc.get(keyArmorStandUuid, PersistentDataType.STRING);
        Boolean singleUse = pdc.getOrDefault(keySingleUse, PersistentDataType.BOOLEAN, false);

        if (worldUuidStr == null || x == null || y == null || z == null || armorStandUuidStr == null) {
            return Optional.empty();
        }

        try {
            UUID worldUuid = UUID.fromString(worldUuidStr);
            UUID armorStandUuid = UUID.fromString(armorStandUuidStr);
            return Optional.of(new RodData(worldUuid, x, y, z, armorStandUuid, singleUse));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    /**
     * Prüft, ob die Angel PDC-Verlinkungsdaten besitzt.
     */
    public boolean isLinked(@NotNull ItemStack rod) {
        if (rod.getType().isAir()) {
            return false;
        }
        return rod.getPersistentDataContainer().has(keyArmorStandUuid, PersistentDataType.STRING);
    }

    /**
     * Entfernt alle Verlinkungsdaten aus dem PDC der Angel (z. B. bei Trennung oder Auslösung).
     */
    public void clearRodData(@NotNull ItemStack rod) {
        if (rod.getType().isAir()) {
            return;
        }

        rod.editPersistentDataContainer(pdc -> {
            pdc.remove(keyWorldUuid);
            pdc.remove(keyX);
            pdc.remove(keyY);
            pdc.remove(keyZ);
            pdc.remove(keyArmorStandUuid);
            pdc.remove(keySingleUse);
        });
    }
}
