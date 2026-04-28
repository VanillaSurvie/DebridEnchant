package fr.speedangel91.debridenchant.listeners;

import fr.speedangel91.debridenchant.DebridEnchant;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;

public class AnvilListener implements Listener {

    private final DebridEnchant plugin;

    public AnvilListener(DebridEnchant plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onAnvilClick(InventoryClickEvent e) {

        if (!(e.getWhoClicked() instanceof Player player)) return;
        if (!(e.getInventory() instanceof AnvilInventory anvil)) return;
        if (e.getSlot() != 2) return;
        if (!e.isShiftClick()) return;

        if (!player.hasPermission("debridenchant.use")) {
            player.sendMessage(plugin.getMessageManager().get("no-permission-use"));
            return;
        }

        ItemStack result = anvil.getItem(2);
        if (result == null || result.getType() == Material.AIR) return;
        if (result.getEnchantments().isEmpty()) return;

        Enchantment ench = result.getEnchantments().keySet().iterator().next();
        int currentLevel = result.getEnchantments().get(ench);

        // 🔒 Anti-cheat : empêcher d'améliorer tant que le niveau vanilla n'est pas atteint
        if (plugin.getConfig().getBoolean("require-vanilla-max")) {
            int vanillaMax = ench.getMaxLevel();

            if (currentLevel < vanillaMax) {
                player.sendMessage("§cTu dois d'abord obtenir l'enchantement niveau "
                        + vanillaMax + " via des livres ou une enclume avant de pouvoir l'améliorer avec l'XP !");
                return;
            }
        }

        // 🔧 Vérification du niveau maximum custom
        int max = plugin.getEnchantConfig().getMaxLevel(ench);
        if (currentLevel >= max) {
            player.sendMessage(plugin.getMessageManager().get("max-level"));
            return;
        }

        // 💰 Calcul du coût XP
        int cost = plugin.getEnchantConfig().getUpgradeCost(ench, currentLevel);
        if (player.getLevel() < cost) {
            player.sendMessage(
                    plugin.getMessageManager().get("not-enough-xp")
                            .replace("%cost%", String.valueOf(cost))
            );
            return;
        }

        // Débit XP
        player.setLevel(player.getLevel() - cost);

        // ✨ Application du nouvel enchantement
        result.removeEnchantment(ench);
        result.addUnsafeEnchantment(ench, currentLevel + 1);

        // 🖼️ TITLE configurable (texte = lang.yml, durées = config.yml)
        String enchName = ench.getKey().getKey().toUpperCase().replace("_", " ");

        String title = plugin.getMessageManager().getTitle("title")
                .replace("%enchant%", enchName)
                .replace("%level%", String.valueOf(currentLevel + 1));

        String subtitle = plugin.getMessageManager().getTitle("subtitle")
                .replace("%enchant%", enchName)
                .replace("%level%", String.valueOf(currentLevel + 1));

        int fadeIn = plugin.getConfig().getInt("title-effects.fade-in");
        int stay = plugin.getConfig().getInt("title-effects.stay");
        int fadeOut = plugin.getConfig().getInt("title-effects.fade-out");

        if (plugin.getConfig().getBoolean("title-effects.enabled")) {
            player.sendTitle(title, subtitle, fadeIn, stay, fadeOut);
        }

        // 📜 Logs
        plugin.getPlayerLogManager().log(
                player.getName(),
                ench.getKey().getKey(),
                currentLevel,
                currentLevel + 1,
                cost,
                result.getType().name()
        );

        player.sendMessage(
                plugin.getMessageManager().get("upgraded")
                        .replace("%level%", String.valueOf(currentLevel + 1))
        );

        plugin.getLogger().info(
                "[LOG] " + player.getName() + " a amélioré " + ench.getKey().getKey() +
                        " de " + currentLevel + " à " + (currentLevel + 1) +
                        " pour " + cost + " niveaux d'XP (item: " + result.getType().name() + ")"
        );

        anvil.setItem(2, result);
    }
}