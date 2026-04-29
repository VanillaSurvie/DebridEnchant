package fr.speedangel91.debridenchant.upgrade;

import fr.speedangel91.debridenchant.DebridEnchant;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class UpgradeExecutor {

    private final DebridEnchant plugin;

    // Contexte envoyé par AnvilListener
    private Map<Enchantment, Integer> eligible;
    private ItemStack item;

    public UpgradeExecutor(DebridEnchant plugin) {
        this.plugin = plugin;
    }

    public void setContext(Map<Enchantment, Integer> eligible, ItemStack item) {
        this.eligible = eligible;
        this.item = item;
    }

    /**
     * Améliore un seul enchantement (le premier éligible)
     */
    public void upgradeOne(Player player) {

        Enchantment ench = eligible.keySet().iterator().next();
        int currentLevel = eligible.get(ench);

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

        // Application de l'amélioration
        item.removeEnchantment(ench);
        item.addUnsafeEnchantment(ench, currentLevel + 1);

        // TITLE
        sendTitle(player, ench, currentLevel + 1);

        // Logs
        plugin.getPlayerLogManager().log(
                player.getName(),
                ench.getKey().getKey(),
                currentLevel,
                currentLevel + 1,
                cost,
                item.getType().name()
        );

        player.sendMessage(
                plugin.getMessageManager().get("upgraded")
                        .replace("%level%", String.valueOf(currentLevel + 1))
        );
    }

    /**
     * Améliore tous les enchantements éligibles
     */
    public void upgradeAll(Player player) {

        int totalCost = 0;

        // Calcul du coût total
        for (Enchantment ench : eligible.keySet()) {
            int lvl = eligible.get(ench);
            totalCost += plugin.getEnchantConfig().getUpgradeCost(ench, lvl);
        }

        if (player.getLevel() < totalCost) {
            player.sendMessage(
                    plugin.getMessageManager().get("not-enough-xp")
                            .replace("%cost%", String.valueOf(totalCost))
            );
            return;
        }

        // Débit XP total
        player.setLevel(player.getLevel() - totalCost);

        // Application des améliorations
        for (Enchantment ench : eligible.keySet()) {

            int currentLevel = eligible.get(ench);

            item.removeEnchantment(ench);
            item.addUnsafeEnchantment(ench, currentLevel + 1);

            // Log individuel
            plugin.getPlayerLogManager().log(
                    player.getName(),
                    ench.getKey().getKey(),
                    currentLevel,
                    currentLevel + 1,
                    plugin.getEnchantConfig().getUpgradeCost(ench, currentLevel),
                    item.getType().name()
            );
        }

        // TITLE global
        player.sendTitle(
                plugin.getMessageManager().getTitle("title").replace("%enchant%", "MULTI"),
                plugin.getMessageManager().getTitle("subtitle").replace("%level%", "+1"),
                plugin.getConfig().getInt("title-effects.fade-in"),
                plugin.getConfig().getInt("title-effects.stay"),
                plugin.getConfig().getInt("title-effects.fade-out")
        );

        player.sendMessage("§aTous les enchantements ont été améliorés !");
    }

    /**
     * Envoie le TITLE d'amélioration
     */
    private void sendTitle(Player player, Enchantment ench, int newLevel) {

        String enchName = ench.getKey().getKey().toUpperCase().replace("_", " ");

        String title = plugin.getMessageManager().getTitle("title")
                .replace("%enchant%", enchName)
                .replace("%level%", String.valueOf(newLevel));

        String subtitle = plugin.getMessageManager().getTitle("subtitle")
                .replace("%enchant%", enchName)
                .replace("%level%", String.valueOf(newLevel));

        if (plugin.getConfig().getBoolean("title-effects.enabled")) {
            player.sendTitle(
                    title,
                    subtitle,
                    plugin.getConfig().getInt("title-effects.fade-in"),
                    plugin.getConfig().getInt("title-effects.stay"),
                    plugin.getConfig().getInt("title-effects.fade-out")
            );
        }
    }
}
