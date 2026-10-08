
package de.example.plugin.listener;

import de.example.plugin.manager.AnchorManager;
import de.example.plugin.manager.PressurePlateManager;
import de.example.plugin.manager.RodDataManager;
import de.example.plugin.manager.RodDataManager.RodData;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.Powerable;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

/**
 * Zentraler Listener für das Angeln, Verlinken und Entlinken von Druckplatten.
 */
public class PlayerFishListener implements Listener {

    private final Plugin plugin;
    private final RodDataManager rodDataManager;
    private final PressurePlateManager pressurePlateManager;
    private final AnchorManager anchorManager;
    private final NamespacedKey keyStoredDamage;

    public PlayerFishListener(
        @NotNull Plugin plugin,
        @NotNull RodDataManager rodDataManager,
        @NotNull PressurePlateManager pressurePlateManager,
        @NotNull AnchorManager anchorManager
    ) {
        this.plugin = plugin;
        this.rodDataManager = rodDataManager;
        this.pressurePlateManager = pressurePlateManager;
        this.anchorManager = anchorManager;
        this.keyStoredDamage = new NamespacedKey(plugin, "rod_stored_damage");
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerFish(@NotNull PlayerFishEvent event) {
        Player player = event.getPlayer();

        // 1. Ermittle die genutzte Angelrute und den Hand-Slot
        EquipmentSlot slot = EquipmentSlot.HAND;
        ItemStack rod = player.getInventory().getItemInMainHand();
        if (rod.getType() != Material.FISHING_ROD) {
            rod = player.getInventory().getItemInOffHand();
            slot = EquipmentSlot.OFF_HAND;
        }

        if (rod.getType() != Material.FISHING_ROD) {
            return;
        }

        // 2. Szenario B: Angel ist bereits verlinkt -> Einholen / Entlinken
        if (rodDataManager.isLinked(rod)) {
            handleUnlinking(player, rod, slot, event);
            return;
        }

        // 3. Szenario A: Haken landet auf einem Block -> Verlinkung prüfen
        if (event.getState() == PlayerFishEvent.State.IN_GROUND || isHookTouchingPlate(event.getHook())) {
            Block plateBlock = getHitPressurePlate(event.getHook());
            if (plateBlock != null) {
                handleLinking(player, rod, plateBlock, event.getHook());
            }
        }
    }

    /**
     * Führt die Verlinkung der Angelrute mit der Druckplatte durch.
     */
    private void handleLinking(@NotNull Player player, @NotNull ItemStack rod, @NotNull Block plateBlock, @NotNull FishHook hook) {
        Location blockLoc = plateBlock.getLocation();
        ItemMeta meta = rod.getItemMeta();
        if (meta == null) {
            return;
        }

        boolean isSingleUse = false;

        // A) Suffix [1] & Namen-Bereinigung
        if (meta.hasDisplayName()) {
            Component nameComp = meta.displayName();
            String plainName = PlainTextComponentSerializer.plainText().serialize(nameComp);

            if (plainName.contains("[1]")) {
                isSingleUse = true;
                String cleanName = plainName.replace("[1]", "").trim();
                meta.displayName(Component.text(cleanName));
            }

            // CustomModelData basierend auf Schlüsselwörtern zuweisen
            String lower = plainName.toLowerCase();
            if (lower.contains("stasis")) {
                meta.setCustomModelData(1001);
            } else if (lower.contains("orbital") || lower.contains("nuke") || lower.contains("stabshot")) {
                meta.setCustomModelData(1002);
            } else if (lower.contains("trap") || lower.contains("falle")) {
                meta.setCustomModelData(1003);
            }
        }

        // B) Sichern der Ursprungs-Haltbarkeit in PDC
        if (meta instanceof Damageable damageable) {
            meta.getPersistentDataContainer().set(keyStoredDamage, PersistentDataType.INTEGER, damageable.getDamage());

            // Bei Single-Use: Visuell auf 1 HP setzen (MaxDamage - 1)
            if (isSingleUse) {
                damageable.setDamage(rod.getType().getMaxDurability() - 1);
            }
        }

        // C) Verzauberungen verbergen
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        rod.setItemMeta(meta);

        // D) Anker-ArmorStand spawnen & Druckplatte sperren
        UUID anchorUuid = anchorManager.spawnAnchor(blockLoc);
        pressurePlateManager.addLocation(blockLoc);

        // E) PDC-Daten in die Angel schreiben
        rodDataManager.writeRodData(rod, blockLoc, anchorUuid, isSingleUse);

        // F) Bobber entfernen & Erfolgsnachricht senden
        hook.remove();
        sendConfigMessage(player, "messages.linked_success", "<green>Druckplatte erfolgreich verlinkt!");

        debugLog("Angel von " + player.getName() + " erfolgreich mit Druckplatte an " + blockLoc + " verlinkt (SingleUse: " + isSingleUse + ").");
    }

    /**
     * Handhabt das Einholen und Entlinken der Angelrute.
     */
    private void handleUnlinking(@NotNull Player player, @NotNull ItemStack rod, @NotNull EquipmentSlot slot, @NotNull PlayerFishEvent event) {
        Optional<RodData> rodDataOpt = rodDataManager.readRodData(rod);
        if (rodDataOpt.isEmpty()) {
            return;
        }

        // Bobber sofort entfernen
        if (event.getHook() != null) {
            event.getHook().remove();
        }

        RodData rodData = rodDataOpt.get();
        Location targetLoc = rodData.toLocation();
        UUID anchorUuid = rodData.armorStandUuid();
        boolean isSingleUse = rodData.isSingleUse();

        World world = Bukkit.getWorld(rodData.worldUuid());

        // 1. 16-Sekunden-Chunk-Ticket am Ziel setzen
        if (world != null) {
            int chunkX = (int) Math.floor(rodData.x()) >> 4;
            int chunkZ = (int) Math.floor(rodData.z()) >> 4;
            world.addPluginChunkTicket(chunkX, chunkZ, plugin);
        }

        // 2. Existenz-Prüfung & Bereinigung von Anker und Druckplatte
        boolean plateWasLinked = (world != null && targetLoc != null && pressurePlateManager.isLinked(targetLoc));

        if (world != null && targetLoc != null) {
            anchorManager.removeAnchor(world, anchorUuid, targetLoc);
            pressurePlateManager.removeLocation(targetLoc);
        }

        // Fail-Safe: Nachricht senden, falls die Verbindung getrennt/zerstört war
        if (!plateWasLinked) {
            sendConfigMessage(player, "messages.link_broken", "<red>Verlinkung wurde getrennt (Ziel nicht gefunden).");
            debugLog("Verlinkung für " + player.getName() + " war unterbrochen/zerstört.");
        }

        // 3. Item-Abwicklung
        if (isSingleUse) {
            // Single-Use: Angel wird beim Ziehen IMMER zerstört!
            setHandItem(player, slot, null);
            player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 1.0f, 1.0f);
            debugLog("Einweg-Angel von " + player.getName() + " wurde verbraucht und zerstört.");
        } else {
            // Mehrweg: Angel zurücksetzen
            ItemMeta meta = rod.getItemMeta();
            int storedDamage = 0;

            if (meta != null) {
                storedDamage = meta.getPersistentDataContainer().getOrDefault(keyStoredDamage, PersistentDataType.INTEGER, 0);
                meta.getPersistentDataContainer().remove(keyStoredDamage);
                meta.removeItemFlags(ItemFlag.HIDE_ENCHANTS);
                rod.setItemMeta(meta);
            }

            rodDataManager.clearRodData(rod);

            // Haltbarkeits-Pönale aus der Config abziehen
            int penalty = plugin.getConfig().getInt("durability_penalty", 10);
            int newDamage = storedDamage + penalty;
            int maxDurability = rod.getType().getMaxDurability();

            if (newDamage >= maxDurability) {
                setHandItem(player, slot, null);
                player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 1.0f, 1.0f);
                debugLog("Angel von " + player.getName() + " ist durch Haltbarkeits-Pönale zerbrochen.");
            } else {
                ItemMeta finalMeta = rod.getItemMeta();
                if (finalMeta instanceof Damageable damageable) {
                    damageable.setDamage(newDamage);
                    rod.setItemMeta(finalMeta);
                }
                debugLog("Angel von " + player.getName() + " zurückgesetzt (Damage: " + newDamage + ").");
            }
        }
    }

    /**
     * Ermittelt, ob der FishHook eine Druckplatte berührt/getroffen hat.
     */
    private @Nullable Block getHitPressurePlate(@Nullable FishHook hook) {
        if (hook == null) {
            return null;
        }

        Location loc = hook.getLocation();
        Block atLoc = loc.getBlock();
        if (atLoc.getBlockData() instanceof Powerable) {
            return atLoc;
        }

        Block below = loc.clone().add(0, -0.2, 0).getBlock();
        if (below.getBlockData() instanceof Powerable) {
            return below;
        }

        return null;
    }

    private boolean isHookTouchingPlate(@Nullable FishHook hook) {
        return getHitPressurePlate(hook) != null;
    }

    private void setHandItem(@NotNull Player player, @NotNull EquipmentSlot slot, @Nullable ItemStack item) {
        if (slot == EquipmentSlot.HAND) {
            player.getInventory().setItemInMainHand(item);
        } else {
            player.getInventory().setItemInOffHand(item);
        }
    }

    private void sendConfigMessage(@NotNull Player player, @NotNull String path, @NotNull String defaultMsg) {
        String msg = plugin.getConfig().getString(path, defaultMsg);
        if (msg != null && !msg.isEmpty()) {
            player.sendMessage(MiniMessage.miniMessage().deserialize(msg));
        }
    }

    public void debugLog(@NotNull String message) {
        if (plugin.getConfig().getBoolean("debug", false)) {
            plugin.getLogger().info("[DEBUG - PlayerFishListener] " + message);
        }
    }
}
