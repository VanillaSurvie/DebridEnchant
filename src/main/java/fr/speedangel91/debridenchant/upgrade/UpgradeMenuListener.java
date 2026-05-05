package fr.speedangel91.debridenchant.upgrade;

import fr.speedangel91.debridenchant.DebridEnchant;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class UpgradeMenuListener implements Listener {

    private final DebridEnchant plugin;

    public UpgradeMenuListener(DebridEnchant plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {

        if (!(e.getWhoClicked() instanceof Player player)) return;

        // 🔥 Détection EXACTE du GUI
        if (!e.getView().getTitle().equals(UpgradeMenu.TITLE)) return;

        // Empêche de prendre les items du GUI
        e.setCancelled(true);

        if (e.getClickedInventory() == null) return;
        if (e.getCurrentItem() == null) return;

        Material type = e.getCurrentItem().getType();

        // Améliorer un enchantement
        if (type == Material.EMERALD) {
            plugin.getUpgradeExecutor().upgradeOne(player);
            player.closeInventory();
            return;
        }

        // Améliorer tous les enchantements
        if (type == Material.DIAMOND) {
            plugin.getUpgradeExecutor().upgradeAll(player);
            player.closeInventory();
        }
    }
}
