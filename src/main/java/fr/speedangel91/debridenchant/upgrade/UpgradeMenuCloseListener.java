package fr.speedangel91.debridenchant.upgrade;

import fr.speedangel91.debridenchant.DebridEnchant;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class UpgradeMenuCloseListener implements Listener {

    private final DebridEnchant plugin;

    public UpgradeMenuCloseListener(DebridEnchant plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e) {

        if (!(e.getPlayer() instanceof Player player)) return;

        // 🔥 Détection EXACTE du GUI
        if (!e.getView().getTitle().equals(UpgradeMenu.TITLE)) return;

        var ctx = plugin.getUpgradeExecutor().getContext(player);
        if (ctx == null) return;

        // Si aucun item n'a été donné → rendre l'item original
        if (!ctx.given) {
            ctx.giveOnCancel(player);
        }

        plugin.getUpgradeExecutor().clear(player);
    }
}

