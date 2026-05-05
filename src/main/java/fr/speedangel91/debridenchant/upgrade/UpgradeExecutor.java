package fr.speedangel91.debridenchant.upgrade;

import fr.speedangel91.debridenchant.DebridEnchant;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class UpgradeExecutor {

    private final DebridEnchant plugin;

    // Contexte PAR JOUEUR
    private final Map<UUID, UpgradeContext> contexts = new HashMap<>();

    public UpgradeExecutor(DebridEnchant plugin) {
        this.plugin = plugin;
    }

    /**
     * Enregistre le contexte d'upgrade pour un joueur
     */
    public void setContext(Player player, Map<Enchantment, Integer> eligible, ItemStack result, ItemStack slot0, ItemStack slot1) {
        UpgradeContext ctx = new UpgradeContext(
                eligible,
                result.clone(),                 // Item modifié (sera amélioré)
                result.clone(),                 // Item de base (renommé, non amélioré)
                slot0 == null ? null : slot0.clone(),
                slot1 == null ? null : slot1.clone()
        );
        contexts.put(player.getUniqueId(), ctx);
    }
    private void sendUpgradeTitle(Player player, String enchantName, int newLevel) {

        if (!plugin.getConfig().getBoolean("title-effects.enabled")) return;

        int fadeIn = plugin.getConfig().getInt("title-effects.fade-in");
        int stay = plugin.getConfig().getInt("title-effects.stay");
        int fadeOut = plugin.getConfig().getInt("title-effects.fade-out");

        String title = plugin.getMessageManager().getTitle("title")
                .replace("%enchant%", enchantName)
                .replace("%level%", String.valueOf(newLevel));

        String subtitle = plugin.getMessageManager().getTitle("subtitle")
                .replace("%enchant%", enchantName)
                .replace("%level%", String.valueOf(newLevel));
        // Envoi 1 tick plus tard
        plugin.getServer().getScheduler().runTask(plugin, () ->
        player.sendTitle(title, subtitle, fadeIn, stay, fadeOut)
    );
    }
    public UpgradeContext getContext(Player player) {
        return contexts.get(player.getUniqueId());
    }

    public void clear(Player player) {
        contexts.remove(player.getUniqueId());
    }

    /**
     * Améliore un seul enchantement
     */
    public void upgradeOne(Player player) {

        UpgradeContext ctx = getContext(player);
        if (ctx == null) return;

        Enchantment ench = ctx.eligible.keySet().iterator().next();
        int currentLevel = ctx.eligible.get(ench);

        int cost = plugin.getEnchantConfig().getUpgradeCost(ench, currentLevel);

        if (player.getLevel() < cost) {
            player.sendMessage(plugin.getMessageManager().get("not-enough-xp").replace("%cost%", String.valueOf(cost)));
            ctx.giveOnCancel(player);
            clear(player);
            return;
        }

        // Déduire XP
        player.setLevel(player.getLevel() - cost);

        // Appliquer l'amélioration
        ctx.modified.removeEnchantment(ench);
        ctx.modified.addUnsafeEnchantment(ench, currentLevel + 1);
        sendUpgradeTitle(player, ench.getKey().getKey(), currentLevel + 1);

        // Donner l'item amélioré
        ctx.giveModified(player);

        // Log
        plugin.getPlayerLogManager().log(
                player.getName(),
                ench.getKey().getKey(),
                currentLevel,
                currentLevel + 1,
                cost,
                ctx.modified.getType().name()
        );

        player.sendMessage(plugin.getMessageManager().get("upgraded").replace("%level%", String.valueOf(currentLevel + 1)));

        clear(player);
    }

    /**
     * Améliore tous les enchantements
     */
    public void upgradeAll(Player player) {

        UpgradeContext ctx = getContext(player);
        if (ctx == null) return;

        int totalCost = 0;

        for (Enchantment ench : ctx.eligible.keySet()) {
            totalCost += plugin.getEnchantConfig().getUpgradeCost(ench, ctx.eligible.get(ench));
        }

        if (player.getLevel() < totalCost) {
            player.sendMessage(plugin.getMessageManager().get("not-enough-xp").replace("%cost%", String.valueOf(totalCost)));
            ctx.giveOnCancel(player);
            clear(player);
            return;
        }

        // Déduire XP
        player.setLevel(player.getLevel() - totalCost);

        // Appliquer toutes les améliorations
        for (Enchantment ench : ctx.eligible.keySet()) {
            int currentLevel = ctx.eligible.get(ench);

            ctx.modified.removeEnchantment(ench);
            ctx.modified.addUnsafeEnchantment(ench, currentLevel + 1);

            plugin.getPlayerLogManager().log(
                    player.getName(),
                    ench.getKey().getKey(),
                    currentLevel,
                    currentLevel + 1,
                    plugin.getEnchantConfig().getUpgradeCost(ench, currentLevel),
                    ctx.modified.getType().name()
            );
        }

        // Donner l'item amélioré
        ctx.giveModified(player);

        player.sendMessage("§aTous les enchantements ont été améliorés !");
        sendUpgradeTitle(player, "Tous les enchantements", 0);
        clear(player);
    }

    /**
     * Classe interne : contexte par joueur
     */
    public static class UpgradeContext {

        public final Map<Enchantment, Integer> eligible;
        public final ItemStack modified;     // Item amélioré
        public final ItemStack baseResult;   // Item renommé, non amélioré
        public final ItemStack original0;
        public final ItemStack original1;
        public boolean given = false;

        public UpgradeContext(Map<Enchantment, Integer> eligible, ItemStack modified, ItemStack baseResult, ItemStack original0, ItemStack original1) {
            this.eligible = eligible;
            this.modified = modified;
            this.baseResult = baseResult;
            this.original0 = original0;
            this.original1 = original1;
        }

        /**
         * Rendu si le joueur annule → on rend l'item renommé (baseResult)
         */
        public void giveOnCancel(Player p) {
            p.getInventory().addItem(baseResult.clone());
            given = true;
        }

        /**
         * Rendu si upgrade → on rend l'item amélioré
         */
        public void giveModified(Player p) {
            p.getInventory().addItem(modified.clone());
            given = true;
        }
    }
}
