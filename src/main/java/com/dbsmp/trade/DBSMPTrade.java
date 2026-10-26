package com.dbsmp.trade;

import java.util.Locale;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class DBSMPTrade extends JavaPlugin implements Listener, CommandExecutor {

    @Override
    public void onEnable() {
        saveDefaultConfig();

        getServer().getPluginManager().registerEvents(this, this);

        getCommand("trade").setExecutor(this);
        getCommand("sell").setExecutor(this);
        getCommand("shop").setExecutor(this);

        getLogger().info("DBSMPTrade enabled!");
        if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
            getLogger().warning("Vault not found. Money features need a compatible economy provider.");
        }
    }

    private void msg(CommandSender sender, String message) {
        sender.sendMessage(ChatColor.translateAlternateColorCodes(
            '&', "&8[&bDBSMP&8] &r" + message
        ));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command,
                             String label, String[] args) {

        if (!(sender instanceof Player)) {
            msg(sender, "&cPlayers only.");
            return true;
        }

        Player player = (Player) sender;
        String cmd = command.getName().toLowerCase(Locale.ENGLISH);

        if (cmd.equals("trade")) {
            msg(player, "&ePlayer trading is being set up.");
            msg(player, "&7Usage: /trade <player>, /trade accept, /trade cancel");
            return true;
        }

        if (cmd.equals("sell")) {
            if (args.length == 0 || args[0].equalsIgnoreCase("prices")) {
                msg(player, "&eUse /sell hand to sell the item you're holding.");
                return true;
            }

            if (args[0].equalsIgnoreCase("hand")) {
                Material item = player.getItemInHand().getType();
                msg(player, "&eHeld item: &f" + item.name());
                msg(player, "&cSelling will work after the economy connection is added.");
                return true;
            }

            msg(player, "&eUsage: /sell hand, /sell all, /sell prices");
            return true;
        }

        if (cmd.equals("shop")) {
            msg(player, "&eThe server shop GUI will be added next.");
            return true;
        }

        return true;
    }
}
