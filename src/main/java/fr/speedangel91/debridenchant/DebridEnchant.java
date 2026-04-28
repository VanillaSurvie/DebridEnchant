package fr.speedangel91.debridenchant;

import fr.speedangel91.debridenchant.commands.DebridEnchantCommand;
import fr.speedangel91.debridenchant.listeners.AnvilListener;
import fr.speedangel91.debridenchant.utils.EnchantConfig;
import fr.speedangel91.debridenchant.utils.MessageManager;
import fr.speedangel91.debridenchant.utils.PlayerLogManager;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class DebridEnchant extends JavaPlugin {

    private MessageManager messageManager;
    private EnchantConfig enchantConfig;
    private PlayerLogManager playerLogManager;
    public PlayerLogManager getPlayerLogManager() {
        return playerLogManager;
    }

    @Override
    public void onEnable() {
        // Génère config.yml si absent
        saveDefaultConfig();

        // Charge les systèmes
        messageManager = new MessageManager(this);
        enchantConfig = new EnchantConfig(this);
        playerLogManager = new PlayerLogManager(this);

        // Enregistre les listeners
        Bukkit.getPluginManager().registerEvents(new AnvilListener(this), this);

        // Enregistre la commande
        if (getCommand("debridenchant") != null) {
            getCommand("debridenchant").setExecutor(new DebridEnchantCommand(this));
        }

        getLogger().info("DebridEnchant est chargé !");
    }

    @Override
    public void onDisable() {
        getLogger().info("DebridEnchant est désactivé.");
    }

    /**
     * Recharge toute la configuration :
     * - config.yml
     * - fichier de langue
     * - paramètres d’enchantements
     */
    public void reloadAll() {
        reloadConfig();
        messageManager.load();
        enchantConfig.reload();
    }

    public MessageManager getMessageManager() {
        return messageManager;
    }

    public EnchantConfig getEnchantConfig() {
        return enchantConfig;
    }
}