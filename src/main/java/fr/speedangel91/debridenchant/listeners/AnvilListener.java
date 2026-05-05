package fr.speedangel91.debridenchant.listeners;

import fr.speedangel91.debridenchant.DebridEnchant;
import fr.speedangel91.debridenchant.upgrade.UpgradeMenu;
import fr.speedangel91.debridenchant.upgrade.UpgradePreview;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class AnvilListener implements Listener {

    private final DebridEnchant plugin;

    public AnvilListener(DebridEnchant plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onAnvilClick(InventoryClickEvent e) {

        if (!(e.getWhoClicked() instanceof Player player)) return;
        if (!(e.getInventory() instanceof AnvilInventory anvil)) return;

        // Slot résultat uniquement
        if (e.getSlot() != 2) return;

        // SHIFT + clic obligatoire
        if (!e.isShiftClick()) return;

        // Permission
        if (!player.hasPermission("debridenchant.use")) {
            player.sendMessage(plugin.getMessageManager().get("no-permission-use"));
            return;
        }

        // Récupération de l'item résultat
        ItemStack result = anvil.getItem(2);
        if (result == null || result.getType() == Material.AIR) return;

        // Analyse des enchantements éligibles
        UpgradePreview preview = new UpgradePreview(plugin);
        Map<Enchantment, Integer> eligible = preview.getEligible(result, player);

        if (eligible.isEmpty()) {
            player.sendMessage("§cAucun enchantement de cet item ne peut être amélioré.");
            return;
        }

        // Sauvegarde des items d'entrée
        ItemStack original0 = anvil.getItem(0);
        ItemStack original1 = anvil.getItem(1);

        // 🔥 Anti-duplication : vider l'enclume AVANT fermeture
        anvil.setItem(0, null);
        anvil.setItem(1, null);
        anvil.setItem(2, null);

        // Enregistrer le contexte pour UpgradeExecutor
        plugin.getUpgradeExecutor().setContext(player, eligible, result, original0, original1);

        // Ouvrir le GUI
        new UpgradeMenu(plugin, player, result, eligible).open();
    }
}

