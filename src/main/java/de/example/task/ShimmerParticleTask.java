
package de.example.plugin.task;

import de.example.plugin.manager.PressurePlateManager;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

/**
 * Periodischer Timer-Task zur Erzeugung des Schimmer-Effekts an verlinkten Druckplatten.
 */
public class ShimmerParticleTask extends BukkitRunnable {

    private final Plugin plugin;
    private final PressurePlateManager pressurePlateManager;

    public ShimmerParticleTask(@NotNull Plugin plugin, @NotNull PressurePlateManager pressurePlateManager) {
        this.plugin = plugin;
        this.pressurePlateManager = pressurePlateManager;
    }

    @Override
    public void run() {
        // Prüfe, ob Partikel in der Konfiguration generell aktiviert sind
        if (!plugin.getConfig().getBoolean("particles.enabled", true)) {
            return;
        }

        double radius = plugin.getConfig().getDouble("particles.radius", 10.0);

        for (Location location : pressurePlateManager.getLinkedLocations()) {
            World world = location.getWorld();
            if (world == null) {
                continue;
            }

            // PERFORMANCE-CHECK: Verhindert das unbeabsichtigte Laden von Chunks!
            if (!world.isChunkLoaded(location.getChunk())) {
                continue;
            }

            // Prüfe, ob sich ein Spieler im definierten Radius um die Druckplatte befindet
            var nearbyPlayers = world.getNearbyPlayers(location, radius);
            if (nearbyPlayers.isEmpty()) {
                continue;
            }

            // Positioniere Partikel mittig und leicht erhöht über der Druckplatte
            Location particleLoc = location.clone().add(0.5, 0.2, 0.5);

            // Partikel spawnen (z. B. END_ROD, 2-3 Partikel mit leichter Streuung)
            world.spawnParticle(
                Particle.END_ROD,
                particleLoc,
                2,          // Anzahl
                0.1, 0.1, 0.1, // Streuung (OffsetX, OffsetY, OffsetZ)
                0.01        // Geschwindigkeit / Extra
            );

            debugLog("Schimmer-Partikel erzeugt an " + particleLoc + " für " + nearbyPlayers.size() + " Spieler in der Nähe.");
        }
    }

    /**
     * Konsolen-Debug-Ausgabe, steuerbar über config.yml (debug: true).
     */
    public void debugLog(@NotNull String message) {
        if (plugin.getConfig().getBoolean("debug", false)) {
            plugin.getLogger().info("[DEBUG - ShimmerParticleTask] " + message);
        }
    }
}
