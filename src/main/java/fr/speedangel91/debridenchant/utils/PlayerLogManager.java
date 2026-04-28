package fr.speedangel91.debridenchant.utils;

import fr.speedangel91.debridenchant.DebridEnchant;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class PlayerLogManager {

    private final DebridEnchant plugin;
    private final File folder;

    public PlayerLogManager(DebridEnchant plugin) {
        this.plugin = plugin;
        this.folder = new File(plugin.getDataFolder(), "logs");

        if (!folder.exists()) {
            folder.mkdirs();
        }
    }

    public void log(String playerName, String enchant, int oldLevel, int newLevel, int cost, String itemName) {
        try {
            File file = new File(folder, playerName + ".yml");

            if (!file.exists()) {
                file.createNewFile();
            }

            FileConfiguration config = YamlConfiguration.loadConfiguration(file);

            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

            int index = config.getKeys(false).size() + 1;

            config.set(index + ".time", timestamp);
            config.set(index + ".enchant", enchant);
            config.set(index + ".old-level", oldLevel);
            config.set(index + ".new-level", newLevel);
            config.set(index + ".xp-cost", cost);
            config.set(index + ".item", itemName);

            config.save(file);

        } catch (IOException e) {
            plugin.getLogger().warning("Impossible d'écrire le log du joueur " + playerName);
        }
    }
}
