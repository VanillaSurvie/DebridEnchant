package fr.speedangel91.debridenchant.upgrade;

import fr.speedangel91.debridenchant.DebridEnchant;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta; // ✔ correct
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UpgradeMenu {

    // 🔥 Titre unique et constant pour détecter le GUI dans InventoryCloseEvent
    public static final String TITLE = "§8Améliorations disponibles";

    private final DebridEnchant plugin;
    private final Player player;
    private final ItemStack item;
    private final Map<Enchantment, Integer> eligible;
    private final UpgradePreview preview;

    public UpgradeMenu(DebridEnchant plugin, Player player, ItemStack item, Map<Enchantment, Integer> eligible) {
        this.plugin = plugin;
        this.player = player;
        this.item = item;
        this.eligible = eligible;
        this.preview = new UpgradePreview(plugin);
    }

    public void open() {

        // GUI 27 slots avec titre constant
        Inventory inv = Bukkit.createInventory(null, 27, TITLE);

        // Option 1 : un seul enchantement
        Enchantment first = eligible.keySet().iterator().next();
        int lvl = eligible.get(first);
        int costOne = preview.getCostOne(first, lvl);

        ItemStack one = new ItemStack(Material.EMERALD);
        ItemMeta m1 = one.getItemMeta(); // ✔ propre
        m1.setDisplayName("§aAméliorer un enchantement");
        List<String> lore1 = new ArrayList<>();
        lore1.add("§7" + first.getKey().getKey() + " §e" + lvl + " → " + (lvl + 1));
        lore1.add("§7Coût : §a" + costOne + " niveaux");
        m1.setLore(lore1);
        one.setItemMeta(m1);

        inv.setItem(11, one);

        // Option 2 : tous les enchantements
        int totalCost = preview.getCostAll(eligible);

        ItemStack all = new ItemStack(Material.DIAMOND);
        ItemMeta m2 = all.getItemMeta(); // ✔ propre
        m2.setDisplayName("§bAméliorer tous les enchantements");
        List<String> lore2 = new ArrayList<>();

        for (Enchantment ench : eligible.keySet()) {
            int l = eligible.get(ench);
            int c = preview.getCostOne(ench, l);
            lore2.add("§7" + ench.getKey().getKey() + " §e" + l + " → " + (l + 1) + " §8(" + c + " XP)");
        }
        lore2.add("§7Coût total : §a" + totalCost + " niveaux");
        m2.setLore(lore2);
        all.setItemMeta(m2);

        inv.setItem(15, all);

        // Aperçu de l'item
        inv.setItem(13, item.clone());

        // Ouverture du GUI
        player.openInventory(inv);
    }
}
