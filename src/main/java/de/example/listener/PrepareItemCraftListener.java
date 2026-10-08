
package de.example.plugin.listener;

import de.example.plugin.manager.RodDataManager;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

/**
 * Listener für das manuelle Entlinken verlinkter Angelruten im Crafting-Gitter.
 */
public class PrepareItemCraftListener implements Listener {

    private final Plugin plugin;
    private final RodDataManager rodDataManager;
    private final NamespacedKey keyStoredDamage;

    public PrepareItemCraftListener(@NotNull Plugin plugin, @NotNull RodDataManager rodDataManager) {
        this.plugin = plugin;
        this.rodDataManager = rodDataManager;
        this.keyStoredDamage = new NamespacedKey(plugin, "rod_stored_damage");
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPrepareItemCraft(@NotNull PrepareItemCraftEvent event) {
        CraftingInventory inventory = event.getInventory();
        ItemStack[] matrix = inventory.getMatrix();

        int itemCount = 0;
        ItemStack targetRod = null;

        for (ItemStack item : matrix) {
            if (item != null && !item.getType().isAir()) {
                itemCount++;
                targetRod = item;
            }
        }

        // ANTI-EXPLOIT CHECK: Es darf genau 1 Item in der Matrix liegen und es muss eine verlinkte Angel sein
        if (itemCount != 1 || targetRod == null || targetRod.getType() != Material.FISHING_ROD || !rodDataManager.isLinked(targetRod)) {
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
     * Konsolen-Debug-Ausgabe, steuerbar über config.yml (debug: true).
     */
    public void debugLog(@NotNull String message) {
        if (plugin.getConfig().getBoolean("debug", false)) {
            plugin.getLogger().info("[DEBUG - PrepareItemCraftListener] " + message);
        }
    }
}