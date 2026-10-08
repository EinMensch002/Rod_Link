
package de.example.plugin;

import de.example.plugin.listener.PlayerFishListener;
import de.example.plugin.listener.PrepareItemCraftListener;
import de.example.plugin.manager.AnchorManager;
import de.example.plugin.manager.PressurePlateManager;
import de.example.plugin.manager.RodDataManager;
import de.example.plugin.task.ShimmerParticleTask;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Hauptklasse für das StasisRod Paper-Plugin.
 * Verlinkt Angelruten mit Redstone-Druckplatten.
 */
public final class StasisRodPlugin extends JavaPlugin {

    private RodDataManager rodDataManager;
    private PressurePlateManager pressurePlateManager;
    private AnchorManager anchorManager;

    @Override
    public void onEnable() {
        // Standard-Konfiguration erstellen/laden
        saveDefaultConfig();

        // Initialisierung der Manager
        this.rodDataManager = new RodDataManager(this);
        this.pressurePlateManager = new PressurePlateManager(this);
        this.anchorManager = new AnchorManager(this);

        // Registrierung der Event-Listener
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(this.pressurePlateManager, this);
        pm.registerEvents(new PlayerFishListener(this, this.rodDataManager, this.pressurePlateManager, this.anchorManager), this);
        pm.registerEvents(new PrepareItemCraftListener(this, this.rodDataManager), this);

        // Start des Schimmer-Partikel-Tasks (Start-Delay: 20 Ticks, Period: 10 Ticks / 0.5s)
        new ShimmerParticleTask(this, this.pressurePlateManager).runTaskTimer(this, 20L, 10L);

        getLogger().info("StasisRod v" + getPluginMeta().getVersion() + " wurde erfolgreich aktiviert!");
    }

    @Override
    public void onDisable() {
        // Abbruch aller laufenden Tasks
        Bukkit.getScheduler().cancelTasks(this);

        getLogger().info("StasisRod wurde erfolgreich deaktiviert.");
    }

    public RodDataManager getRodDataManager() {
        return rodDataManager;
    }

    public PressurePlateManager getPressurePlateManager() {
        return pressurePlateManager;
    }

    public AnchorManager getAnchorManager() {
        return anchorManager;
    }
}