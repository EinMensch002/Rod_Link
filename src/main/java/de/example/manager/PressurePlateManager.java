package de.example.plugin.manager;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.Powerable;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.world.EntitiesLoadEvent;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Verwalter für verlinkte Druckplatten.
 * Verhindert das automatische Abschalten des Redstone-Signals durch Minecraft, räumt Marker auf,
 * wenn die Platte zerstört wird, und baut die Verlinkungen nach einem Neustart aus den Markern wieder auf.
 */
public class PressurePlateManager implements Listener {

    private final Plugin plugin;
    private final AnchorManager anchorManager;
    private final Set<Location> linkedLocations = new HashSet<>();

    public PressurePlateManager(@NotNull Plugin plugin, @NotNull AnchorManager anchorManager) {
        this.plugin = plugin;
        this.anchorManager = anchorManager;
    }

    /**
     * Startet die Hintergrund-Prüfung und baut Verlinkungen aus bereits geladenen Chunks wieder auf.
     * Wird einmal beim Start des Plugins aufgerufen.
     */
    public void start() {
        // Stellt Verlinkungen in Chunks wieder her, die schon vor dem Plugin-Start geladen waren (z. B. Spawn-Chunks)
        plugin.getServer().getScheduler().runTask(plugin, this::restoreAllLoaded);

        // Sicherheitsnetz: Entfernt Marker, deren Platte auf irgendeinem Weg verschwunden ist (Physik, Wasser, WorldEdit ...)
        plugin.getServer().getScheduler().runTaskTimer(plugin, this::validateLinks, 40L, 20L);
    }

    /**
     * Normalisiert eine Location auf exakte Block-Koordinaten (ohne Nachkommastellen oder Blickwinkel).
     * Greift bewusst nicht auf den Block zu, damit dabei kein Chunk geladen wird.
     */
    private Location toBlockLocation(@NotNull Location location) {
        return new Location(location.getWorld(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    /**
     * Gibt an, ob der Block als verlinkbare Platte gilt.
     */
    private boolean isLinkable(@NotNull Block block) {
        return block.getBlockData() instanceof Powerable;
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
     * Gibt eine Verlinkung vollständig frei: lädt den Chunk (mit temporärem Ticket), entfernt den Anker und schaltet die
     * Platte ab. Das Ergebnis wird anschließend im Haupt-Thread an {@code done} übergeben.
     *
     * @param world    Welt der Druckplatte.
     * @param plateLoc Location der Druckplatte.
     * @param done     Erhält {@code true}, wenn eine Verlinkung (Anker oder Eintrag) gefunden und freigegeben wurde,
     *                 sonst {@code false} (Ziel nicht gefunden).
     */
    public void releaseLink(@NotNull World world, @NotNull Location plateLoc, @NotNull Consumer<Boolean> done) {
        Location blockLoc = toBlockLocation(plateLoc);
        int chunkX = blockLoc.getBlockX() >> 4;
        int chunkZ = blockLoc.getBlockZ() >> 4;

        world.addPluginChunkTicket(chunkX, chunkZ, plugin);
        world.getChunkAtAsync(chunkX, chunkZ).whenComplete((chunk, error) ->
            plugin.getServer().getScheduler().runTask(plugin, () -> {
                try {
                    if (error != null || chunk == null) {
                        plugin.getLogger().warning("Chunk für die Entlinkung konnte nicht geladen werden: " + blockLoc);
                        done.accept(false);
                        return;
                    }

                    // Stellt sicher, dass auch die Entities des Chunks geladen sind
                    chunk.getEntities();

                    boolean hadAnchor = anchorManager.removeAnchorsAt(blockLoc) > 0;
                    boolean wasLinked = linkedLocations.contains(blockLoc);
                    removeLocation(blockLoc);
                    done.accept(hadAnchor || wasLinked);
                } finally {
                    world.removePluginChunkTicket(chunkX, chunkZ, plugin);
                }
            })
        );
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

    // ------------------------------------------------------------------
    // Aufräumen bei zerstörter Platte
    // ------------------------------------------------------------------

    /**
     * Entfernt Marker und Eintrag einer Platte, die zerstört wurde. Der Block selbst muss nicht mehr abgeschaltet werden.
     */
    private void cleanupDestroyed(@NotNull Location location) {
        Location blockLoc = toBlockLocation(location);
        if (linkedLocations.remove(blockLoc)) {
            anchorManager.removeAnchorsAt(blockLoc);
            debugLog("Verlinkte Druckplatte zerstört, Anker entfernt: " + blockLoc);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockBreak(@NotNull BlockBreakEvent event) {
        cleanupDestroyed(event.getBlock().getLocation());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockExplode(@NotNull BlockExplodeEvent event) {
        for (Block block : event.blockList()) {
            cleanupDestroyed(block.getLocation());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityExplode(@NotNull EntityExplodeEvent event) {
        for (Block block : event.blockList()) {
            cleanupDestroyed(block.getLocation());
        }
    }

    /**
     * Prüft einmal pro Sekunde alle geladenen verlinkten Platten und räumt auf, falls der Block keine Platte mehr ist.
     * Fängt alle Zerstörungswege ab, für die es kein eigenes Event gibt.
     */
    private void validateLinks() {
        List<Location> broken = new ArrayList<>();
        for (Location location : linkedLocations) {
            World world = location.getWorld();
            if (world == null || !world.isChunkLoaded(location.getBlockX() >> 4, location.getBlockZ() >> 4)) {
                continue;
            }
            if (!isLinkable(location.getBlock())) {
                broken.add(location);
            }
        }
        for (Location location : broken) {
            cleanupDestroyed(location);
        }
    }

    // ------------------------------------------------------------------
    // Wiederherstellung nach Neustart
    // ------------------------------------------------------------------

    /**
     * Registriert Verlinkungen neu, sobald Chunks mit Ankern geladen werden.
     */
    @EventHandler
    public void onEntitiesLoad(@NotNull EntitiesLoadEvent event) {
        List<ArmorStand> anchors = new ArrayList<>();
        for (Entity entity : event.getEntities()) {
            if (entity instanceof ArmorStand stand && anchorManager.isStasisAnchor(stand)) {
                anchors.add(stand);
            }
        }
        if (anchors.isEmpty()) {
            return;
        }
        // Eine Tick später, damit Entities nicht mitten im Lade-Event verändert werden
        plugin.getServer().getScheduler().runTask(plugin, () -> anchors.forEach(this::restoreFromAnchor));
    }

    private void restoreAllLoaded() {
        for (World world : plugin.getServer().getWorlds()) {
            for (Chunk chunk : world.getLoadedChunks()) {
                for (Entity entity : chunk.getEntities()) {
                    if (entity instanceof ArmorStand stand && anchorManager.isStasisAnchor(stand)) {
                        restoreFromAnchor(stand);
                    }
                }
            }
        }
    }

    /**
     * Stellt die Verlinkung zu einem gefundenen Anker wieder her. Ist an der Stelle keine Platte mehr (z. B. offline
     * zerstört), wird der verwaiste Anker entfernt.
     */
    private void restoreFromAnchor(@NotNull ArmorStand stand) {
        if (!stand.isValid()) {
            return;
        }
        Location location = stand.getLocation();
        if (isLinkable(location.getBlock())) {
            addLocation(location);
            debugLog("Verlinkung nach Chunk-Laden wiederhergestellt: " + toBlockLocation(location));
        } else {
            stand.remove();
            debugLog("Verwaisten Anker entfernt (keine Platte mehr): " + toBlockLocation(location));
        }
    }

    // ------------------------------------------------------------------
    // Signal erzwingen
    // ------------------------------------------------------------------

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
     * Verliert die Platte ihren Halt, wird die Physik NICHT blockiert, damit sie wie üblich abfällt.
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockPhysics(@NotNull BlockPhysicsEvent event) {
        Block block = event.getBlock();
        Location blockLoc = toBlockLocation(block.getLocation());

        if (isLinked(blockLoc)) {
            if (block.getBlockData() instanceof Powerable powerable) {
                // Kein Halt mehr -> Platte darf abfallen, die Aufräum-Prüfung entfernt anschließend den Anker
                if (!block.canPlace(powerable)) {
                    return;
                }
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
