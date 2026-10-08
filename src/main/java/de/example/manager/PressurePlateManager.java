
package de.example.plugin.manager;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.data.Powerable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Verwalter für verlinkte Druckplatten im Speicher.
 * Verhindert das automatische Abschalten des Redstone-Signals durch Minecraft.
 */
public class PressurePlateManager implements Listener {

    private final Plugin plugin;
    private final Set<Location> linkedLocations = new HashSet<>();

    public PressurePlateManager(@NotNull Plugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Normalisiert eine Location auf exakte Block-Koordinaten (ohne Nachkommastellen oder Blickwinkel).
     */
    private Location toBlockLocation(@NotNull Location location) {
        return location.getBlock().getLocation();
    }

    /**
     * Fügt eine Druckplatten-Location zu den verlinkten Positionen hinzu und aktiviert das Redstone-Signal.
     */
    public void addLocation(@NotNull Location location) {
        Location blockLoc = toBlockLocation(location);
        if (linkedLocations.add(blockLoc)) {
            Block block = blockLoc.getBlock();
            if (block.getBlockData() instanceof Powerable powerable) {
                powerable.setPowered(true);
                block.setBlockData(powerable, true);
            }
            debugLog("Druckplatte verlinkt an Location: " + blockLoc);
        }
    }

    /**
     * Entfernt eine Druckplatten-Location aus den verlinkten Positionen und deaktiviert das Redstone-Signal.
     */
    public void removeLocation(@NotNull Location location) {
        Location blockLoc = toBlockLocation(location);
        if (linkedLocations.remove(blockLoc)) {
            Block block = blockLoc.getBlock();
            if (block.getBlockData() instanceof Powerable powerable) {
                powerable.setPowered(false);
                block.setBlockData(powerable, true);
            }
            debugLog("Druckplatte entlinkt an Location: " + blockLoc);
        }
    }

    /**
     * Prüft, ob die angegebene Druckplatten-Location aktuell verlinkt ist.
     */
    public boolean isLinked(@NotNull Location location) {
        return linkedLocations.contains(toBlockLocation(location));
    }

    /**
     * Gibt eine unveränderliche Sicht auf alle aktiv verlinkten Druckplatten-Locations zurück.
     */
    public @NotNull Set<Location> getLinkedLocations() {
        return Collections.unmodifiableSet(linkedLocations);
    }

    /**
     * Erzwingt Redstone-Signalstärke 15, solange die Druckplatte verlinkt ist.
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockRedstone(@NotNull BlockRedstoneEvent event) {
        Location blockLoc = toBlockLocation(event.getBlock().getLocation());
        if (isLinked(blockLoc)) {
            event.setNewCurrent(15);
            debugLog("BlockRedstoneEvent an " + blockLoc + " -> Signalstärke auf 15 erzwungen.");
        }
    }

    /**
     * Verhindert, dass der Blockzustand bei Physik-Updates auf setPowered(false) fällt.
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockPhysics(@NotNull BlockPhysicsEvent event) {
        Block block = event.getBlock();
        Location blockLoc = toBlockLocation(block.getLocation());

        if (isLinked(blockLoc)) {
            if (block.getBlockData() instanceof Powerable powerable) {
                if (!powerable.isPowered()) {
                    powerable.setPowered(true);
                    block.setBlockData(powerable, false);
                    debugLog("BlockPhysicsEvent an " + blockLoc + " -> setPowered(true) re-installiert.");
                }
                event.setCancelled(true);
            }
        }
    }

    /**
     * Blockiert Entity-Interaktionen (z. B. Heruntertreten oder Verlassen), die das Signal deaktivieren könnten.
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityInteract(@NotNull EntityInteractEvent event) {
        Block block = event.getBlock();
        Location blockLoc = toBlockLocation(block.getLocation());

        if (isLinked(blockLoc)) {
            if (block.getBlockData() instanceof Powerable) {
                event.setCancelled(true);
                debugLog("EntityInteractEvent an " + blockLoc + " -> Interaktion abgebrochen.");
            }
        }
    }

    /**
     * Konsolen-Debug-Ausgabe, steuerbar über config.yml (debug: true).
     */
    public void debugLog(@NotNull String message) {
        if (plugin.getConfig().getBoolean("debug", false)) {
            plugin.getLogger().info("[DEBUG - PressurePlateManager] " + message);
        }
    }
}