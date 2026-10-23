package io.github.einmensch002.rodlink.listener;

import io.github.einmensch002.rodlink.manager.PressurePlateManager;
import io.github.einmensch002.rodlink.manager.RodDataManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Listener für das manuelle Entlinken verlinkter Angelruten im Crafting-Gitter.
 * Die Vorschau (PrepareItemCraftEvent) zeigt nur die bereinigte Angel. Marker und Platte werden erst freigegeben,
 * wenn das Ergebnis tatsächlich entnommen wird (InventoryClickEvent auf dem Ergebnis-Slot).
 */
public class PrepareItemCraftListener implements Listener {

    private final Plugin plugin;
    private final RodDataManager rodDataManager;
    private final PressurePlateManager pressurePlateManager;
    private final NamespacedKey keyStoredDamage;

    public PrepareItemCraftListener(
        @NotNull Plugin plugin,
        @NotNull RodDataManager rodDataManager,
        @NotNull PressurePlateManager pressurePlateManager
    ) {
        this.plugin = plugin;
        this.rodDataManager = rodDataManager;
        this.pressurePlateManager = pressurePlateManager;
        this.keyStoredDamage = new NamespacedKey(plugin, "rod_stored_damage");
    }

    /**
     * Liefert die verlinkte Angel, wenn genau dieses eine Item in der Matrix liegt, sonst null.
     * ANTI-EXPLOIT: Es darf genau 1 Item in der Matrix liegen und es muss eine verlinkte Angel sein.
     */
    private @Nullable ItemStack findSingleLinkedRod(@NotNull ItemStack[] matrix) {
        int itemCount = 0;
        ItemStack targetRod = null;

        for (ItemStack item : matrix) {
            if (item != null && !item.getType().isAir()) {
                itemCount++;
                targetRod = item;
            }
        }

        if (itemCount != 1 || targetRod == null || targetRod.getType() != Material.FISHING_ROD || !rodDataManager.isLinked(targetRod)) {
            return null;
        }
        return targetRod;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPrepareItemCraft(@NotNull PrepareItemCraftEvent event) {
        CraftingInventory inventory = event.getInventory();
        ItemStack targetRod = findSingleLinkedRod(inventory.getMatrix());
        if (targetRod == null) {
            return;
        }

        // Kopie der Angel zur Bereinigung erstellen
        ItemStack cleanRod = targetRod.clone();
        ItemMeta meta = cleanRod.getItemMeta();

        if (meta != null) {
            // Lese gespeicherte Ursprungs-Haltbarkeit aus PDC
            int storedDamage = meta.getPersistentDataContainer().getOrDefault(keyStoredDamage, PersistentDataType.INTEGER, 0);

            // Entferne PDC-Schlüssel für gespeicherten Schaden & verlinkte Daten
            meta.getPersistentDataContainer().remove(keyStoredDamage);
            meta.removeItemFlags(ItemFlag.HIDE_ENCHANTS);

            // Custom Model Data zurücksetzen -> Standardmodell
            rodDataManager.resetLinkModel(meta);

            if (meta instanceof Damageable damageable) {
                damageable.setDamage(storedDamage);
            }

            cleanRod.setItemMeta(meta);
        }

        // Säubere alle RodData PDC-Einträge
        rodDataManager.clearRodData(cleanRod);

        // Setze das gereinigte Item als Crafting-Ergebnis
        inventory.setResult(cleanRod);

        debugLog("Verlinkte Angel im Crafting-Gitter erkannt und gereinigtes Ergebnis gesetzt.");
    }

    /**
     * Gibt Marker und Druckplatte frei, sobald die bereinigte Angel aus dem Ergebnis-Slot genommen wird.
     * Läuft nach allen anderen Plugins und nur, wenn der Klick nicht abgebrochen wurde.
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onResultTaken(@NotNull InventoryClickEvent event) {
        if (event.getSlotType() != InventoryType.SlotType.RESULT
            || !(event.getInventory() instanceof CraftingInventory craftingInventory)) {
            return;
        }

        ItemStack current = event.getCurrentItem();
        if (current == null || current.getType().isAir() || event.getAction() == InventoryAction.NOTHING) {
            return;
        }

        // Das Ergebnis muss die bereinigte Angel dieses Plugins sein (nicht verlinkt, aber eine Angel)
        ItemStack targetRod = findSingleLinkedRod(craftingInventory.getMatrix());
        if (targetRod == null || current.getType() != Material.FISHING_ROD) {
            return;
        }

        rodDataManager.readRodData(targetRod).ifPresent(data -> {
            World world = Bukkit.getWorld(data.worldUuid());
            Location target = data.toLocation();
            if (world == null || target == null) {
                return;
            }
            pressurePlateManager.releaseLink(world, target, hadLink ->
                debugLog("Crafting-Entlinken abgeschlossen (Verlinkung gefunden: " + hadLink + ")."));
        });
    }

    /**
     * Konsolen-Debug-Ausgabe, steuerbar über config.yml (debug: true).
     */
    public void debugLog(@NotNull String message) {
        if (plugin.getConfig().getBoolean("debug", false)) {
            plugin.getLogger().info("[DEBUG - PrepareItemCraftListener] " + message);
        }
    }
}
