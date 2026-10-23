
package io.github.einmensch002.rodlink;

import io.github.einmensch002.rodlink.listener.PlayerFishListener;
import io.github.einmensch002.rodlink.listener.PrepareItemCraftListener;
import io.github.einmensch002.rodlink.manager.AnchorManager;
import io.github.einmensch002.rodlink.manager.PressurePlateManager;
import io.github.einmensch002.rodlink.manager.RodDataManager;
import io.github.einmensch002.rodlink.task.ShimmerParticleTask;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Hauptklasse für das RodLink Paper-Plugin.
 * Verlinkt Angelruten mit Redstone-Druckplatten.
 */
public final class RodLinkPlugin extends JavaPlugin {

    private RodDataManager rodDataManager;
    private PressurePlateManager pressurePlateManager;
    private AnchorManager anchorManager;

    @Override
    public void onEnable() {
        // Standard-Konfiguration erstellen/laden
        saveDefaultConfig();

        // Initialisierung der Manager
        this.rodDataManager = new RodDataManager(this);
        this.anchorManager = new AnchorManager(this);
        this.pressurePlateManager = new PressurePlateManager(this, this.anchorManager);

        // Registrierung der Event-Listener
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(this.pressurePlateManager, this);
        pm.registerEvents(new PlayerFishListener(this, this.rodDataManager, this.pressurePlateManager, this.anchorManager), this);
        pm.registerEvents(new PrepareItemCraftListener(this, this.rodDataManager, this.pressurePlateManager), this);

        // Verlinkungen aus vorhandenen Ankern wiederherstellen und Aufräum-Prüfung starten
        this.pressurePlateManager.start();

        // Start des Schimmer-Partikel-Tasks (Start-Delay: 20 Ticks, Period: 10 Ticks / 0.5s)
        new ShimmerParticleTask(this, this.pressurePlateManager).runTaskTimer(this, 20L, 10L);

        getLogger().info("RodLink v" + getPluginMeta().getVersion() + " wurde erfolgreich aktiviert!");
    }

    @Override
    public void onDisable() {
        // Abbruch aller laufenden Tasks
        Bukkit.getScheduler().cancelTasks(this);

        getLogger().info("RodLink wurde erfolgreich deaktiviert.");
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
