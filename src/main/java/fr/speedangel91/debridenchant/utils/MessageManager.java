package fr.speedangel91.debridenchant.utils;

import fr.speedangel91.debridenchant.DebridEnchant;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class MessageManager {

    private final DebridEnchant plugin;
    private FileConfiguration langConfig;
    private String prefix;

    public MessageManager(DebridEnchant plugin) {
        this.plugin = plugin;
        load();
    }

    public void load() {
        String lang = plugin.getConfig().getString("lang", "en_US");

        File langFolder = new File(plugin.getDataFolder(), "lang");
        if (!langFolder.exists()) langFolder.mkdirs();

        File langFile = new File(langFolder, lang + ".yml");

        if (!langFile.exists()) {
            plugin.saveResource("lang/" + lang + ".yml", false);
        }

        langConfig = YamlConfiguration.loadConfiguration(langFile);
        prefix = color(langConfig.getString("prefix", "&6[DebridEnchant]&r "));
    }

    public String get(String path) {
        String msg = langConfig.getString(path, "&cMessage introuvable: " + path);
        msg = msg.replace("%prefix%", prefix);
        return color(msg);
    }

    private String color(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    // Texte du TITLE (dans fr_FR.yml)
    public String getTitle(String path) {
        return color(langConfig.getString("title-upgrade." + path, ""));
    }
}