package fr.speedangel91.debridenchant.commands;

import fr.speedangel91.debridenchant.DebridEnchant;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class DebridEnchantCommand implements CommandExecutor {

    private final DebridEnchant plugin;

    public DebridEnchantCommand(DebridEnchant plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        // /debridenchant
        if (args.length == 0) {
            sender.sendMessage(plugin.getMessageManager().get("usage-reload"));
            return true;
        }

        // /debridenchant reload
        if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {

            if (!sender.hasPermission("debridenchant.admin")) {
                sender.sendMessage(plugin.getMessageManager().get("no-permission"));
                return true;
            }

            plugin.reloadAll();
            sender.sendMessage(plugin.getMessageManager().get("reload-success"));
            return true;
        }

        // Mauvais usage
        sender.sendMessage(plugin.getMessageManager().get("usage-reload"));
        return true;
    }
}