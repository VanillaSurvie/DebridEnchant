package fr.speedangel91.debridenchant.utils;

import fr.speedangel91.debridenchant.DebridEnchant;
import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.List;

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

        langConfig = new YamlConfiguration();

        try {
            langConfig.load(langFile);
        } catch (Exception e) {
            plugin.getLogger().warning("Impossible de charger le fichier de langue " + lang + ".yml");
        }

        prefix = color(langConfig.getString("prefix", "&6[DebridEnchant]&r "));
    }

    /**
     * Message simple
     */
    public String get(String path) {
        String msg = langConfig.getString(path, "&cMessage introuvable: " + path);
        return color(msg.replace("%prefix%", prefix));
    }

    /**
     * Message avec placeholders
     */
    public String get(String path, String... placeholders) {
        String msg = get(path);

        for (int i = 0; i < placeholders.length - 1; i += 2) {
            msg = msg.replace(placeholders[i], placeholders[i + 1]);
        }

        return msg;
    }

    /**
     * Liste de messages
     */
    public List<String> getList(String path) {
        List<String> list = langConfig.getStringList(path);
        return list.stream().map(this::color).toList();
    }

    /**
     * Texte du TITLE
     */
    public String getTitle(String path) {
        return color(langConfig.getString("title-upgrade." + path, ""));
    }

    /**
     * Durée du TITLE
     */
    public int getTitleInt(String path) {
        return langConfig.getInt("title-upgrade." + path, 10);
    }

    private String color(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }
}
