package fr.speedangel91.debridenchant.upgrade;

import fr.speedangel91.debridenchant.DebridEnchant;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedHashMap;
import java.util.Map;

public class UpgradePreview {

    private final DebridEnchant plugin;

    public UpgradePreview(DebridEnchant plugin) {
        this.plugin = plugin;
    }

    /**
     * Retourne tous les enchantements éligibles à une amélioration.
     */
    public Map<Enchantment, Integer> getEligible(ItemStack item, Player player) {

        Map<Enchantment, Integer> eligible = new LinkedHashMap<>();

        for (Enchantment ench : item.getEnchantments().keySet()) {

            int level = item.getEnchantments().get(ench);
            int vanillaMax = ench.getMaxLevel();
            int customMax = plugin.getEnchantConfig().getMaxLevel(ench);

            // Anti-cheat vanilla
            if (plugin.getConfig().getBoolean("require-vanilla-max")) {
                if (level < vanillaMax) continue;
            }

            // Max custom
            if (level >= customMax) continue;

            eligible.put(ench, level);
        }

        return eligible;
    }

    /**
     * Coût pour un seul enchantement.
     */
    public int getCostOne(Enchantment ench, int level) {
        return plugin.getEnchantConfig().getUpgradeCost(ench, level);
    }

    /**
     * Coût total pour tous les enchantements éligibles.
     */
    public int getCostAll(Map<Enchantment, Integer> eligible) {
        int total = 0;
        for (Enchantment ench : eligible.keySet()) {
            int lvl = eligible.get(ench);
            total += plugin.getEnchantConfig().getUpgradeCost(ench, lvl);
        }
        return total;
    }
}
