package fr.speedangel91.debridenchant.utils;

import fr.speedangel91.debridenchant.DebridEnchant;
import org.bukkit.enchantments.Enchantment;

import java.util.Map;

public class EnchantConfig {

    private final DebridEnchant plugin;

    private Map<String, Object> maxLevels;
    private Map<String, Object> multipliers;

    private int baseCost;
    private double factor;

    public EnchantConfig(DebridEnchant plugin) {
        this.plugin = plugin;
        reload();
    }

    public void reload() {
        maxLevels = plugin.getConfig().getConfigurationSection("max-levels").getValues(false);
        multipliers = plugin.getConfig().getConfigurationSection("vanilla-multipliers").getValues(false);

        baseCost = plugin.getConfig().getInt("xp-cost.base");
        factor = plugin.getConfig().getDouble("xp-cost.factor");
    }

    public int getMaxLevel(Enchantment ench) {
        Object value = maxLevels.get(ench.getKey().getKey().toUpperCase());
        if (value instanceof Number num) {
            return num.intValue();
        }
        return ench.getMaxLevel(); // fallback vanilla
    }

    public int getMultiplier(Enchantment ench) {
        Object value = multipliers.get(ench.getKey().getKey().toUpperCase());
        if (value instanceof Number num) {
            return num.intValue();
        }
        return 1; // fallback vanilla
    }

    public int getUpgradeCost(Enchantment ench, int currentLevel) {
        int nextLevel = currentLevel + 1;
        int vanillaMult = getMultiplier(ench);

        double cost = baseCost + (nextLevel * vanillaMult * factor);
        return (int) Math.round(cost);
    }
}