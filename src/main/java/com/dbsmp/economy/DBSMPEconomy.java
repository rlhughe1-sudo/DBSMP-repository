
package com.dbsmp.economy;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

public class DBSMPEconomy extends JavaPlugin implements Listener {
    private YamlConfiguration data;
    private File dataFile;

    private static final String PREFIX =
            ChatColor.DARK_PURPLE + "[DBSMP] " + ChatColor.GRAY;
    private static final String SHOP_TITLE =
            ChatColor.DARK_PURPLE + "DBSMP Village Shop";

    @Override
    public void onEnable() {
        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }

        dataFile = new File(getDataFolder(), "data.yml");
        data = YamlConfiguration.loadConfiguration(dataFile);

        Bukkit.getPluginManager().registerEvents(this, this);
        getLogger().info(
                "DBSMP Economy enabled. Starting balance: $20,000"
        );
    }

    @Override
    public void onDisable() {
        saveData();
    }

    private String key(Player p) {
        return p.getUniqueId().toString();
    }

    private double balance(Player p) {
        String k = key(p) + ".balance";

        if (!data.contains(k)) {
            data.set(k, 20000.0);
            saveData();
        }

        return data.getDouble(k);
    }

    private void setBalance(Player p, double amount) {
        data.set(key(p) + ".balance", Math.max(0, amount));
        saveData();
    }

    private void saveData() {
        if (data == null || dataFile == null) {
            return;
        }

        try {
            data.save(dataFile);
        } catch (IOException e) {
            getLogger().severe(
                    "Could not save economy data: " + e.getMessage()
            );
        }
    }

    private String cash(double amount) {
        return "$" + String.format(
                java.util.Locale.US, "%,.0f", amount
        );
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player p = event.getPlayer();
        String k = key(p) + ".balance";

        if (!data.contains(k)) {
            data.set(k, 20000.0);
            saveData();

            p.sendMessage(PREFIX + ChatColor.GREEN
                    + "Welcome to DBSMP! You received $20,000.");
        }
    }

    private ItemStack item(
            Material material, String name, String... lore
    ) {
        ItemStack stack = new ItemStack(material);
        ItemMeta meta = stack.getItemMeta();

        meta.setDisplayName(ChatColor.GOLD + name);

        List<String> lines = new ArrayList<String>();
        for (String line : lore) {
            lines.add(ChatColor.GRAY + line);
        }

        meta.setLore(lines);
        stack.setItemMeta(meta);
        return stack;
    }

    private void openShop(Player p) {
        Inventory inv = Bukkit.createInventory(
                null, 27, SHOP_TITLE
        );

        inv.setItem(10, item(
                Material.CHEST, "Starter Kit - $500",
                "Food, wood, and basic supplies",
                "Click to purchase"
        ));

        inv.setItem(11, item(
                Material.IRON_PICKAXE, "Miner Kit - $1,000",
                "Iron pickaxe, shovel, torches",
                "Click to purchase"
        ));

        inv.setItem(12, item(
                Material.BOW, "Archer Kit - $2,000",
                "Bow and arrows",
                "Click to purchase"
        ));

        inv.setItem(13, item(
                Material.IRON_SWORD, "Warrior Kit - $3,500",
                "Iron sword and full iron armor",
                "Click to purchase"
        ));

        inv.setItem(14, item(
                Material.DIAMOND_SWORD, "Diamond Kit - $6,000",
                "Diamond sword and diamond pickaxe",
                "Full enchanted diamond armor",
                "Protection I and Unbreaking I",
                "Click to purchase"
        ));

        inv.setItem(15, item(
                Material.BREAD, "Food Bundle - $150",
                "16 bread",
                "Click to purchase"
        ));

        inv.setItem(16, item(
                Material.WOOD, "Building Bundle - $300",
                "64 wooden planks",
                "Click to purchase"
        ));

        inv.setItem(22, item(
                Material.BOOK, "Property Office",
                "Use /dbproperty list",
                "Cottage $5,000 | Village $10,000",
                "Large $20,000 | Mansion $50,000",
                "Plot $2,000"
        ));

        p.openInventory(inv);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player)) {
            return;
        }

        if (e.getInventory() == null
                || !SHOP_TITLE.equals(e.getInventory().getTitle())) {
            return;
        }

        e.setCancelled(true);

        if (e.getCurrentItem() == null
                || !e.getCurrentItem().hasItemMeta()) {
            return;
        }

        Player p = (Player) e.getWhoClicked();
        int slot = e.getRawSlot();

        switch (slot) {
            case 10:
                buyKit(p, 500, "starter");
                break;
            case 11:
                buyKit(p, 1000, "miner");
                break;
            case 12:
                buyKit(p, 2000, "archer");
                break;
            case 13:
                buyKit(p, 3500, "warrior");
                break;
            case 14:
                buyKit(p, 6000, "diamond");
                break;
            case 15:
                buyKit(p, 150, "food");
                break;
            case 16:
                buyKit(p, 300, "building");
                break;
            default:
                break;
        }
    }

    private void give(Player p, Material material, int amount) {
        ItemStack stack = new ItemStack(material, amount);

        java.util.HashMap<Integer, ItemStack> leftovers =
                p.getInventory().addItem(stack);

        for (ItemStack left : leftovers.values()) {
            p.getWorld().dropItemNaturally(
                    p.getLocation(), left
            );
        }
    }

    private void giveEnchantedArmor(
            Player p, Material material
    ) {
        ItemStack piece = new ItemStack(material);

        piece.addEnchantment(
                Enchantment.PROTECTION_ENVIRONMENTAL, 1
        );
        piece.addEnchantment(
                Enchantment.DURABILITY, 1
        );

        java.util.HashMap<Integer, ItemStack> leftovers =
                p.getInventory().addItem(piece);

        for (ItemStack left : leftovers.values()) {
            p.getWorld().dropItemNaturally(
                    p.getLocation(), left
            );
        }
    }

    private void buyKit(
            Player p, double price, String kit
    ) {
        double bal = balance(p);

        if (bal < price) {
            p.sendMessage(PREFIX + ChatColor.RED
                    + "You need " + cash(price - bal) + " more.");
            return;
        }

        setBalance(p, bal - price);

        if ("starter".equals(kit)) {
            give(p, Material.BREAD, 8);
            give(p, Material.WOOD, 16);
            give(p, Material.STONE_PICKAXE, 1);

        } else if ("miner".equals(kit)) {
            give(p, Material.IRON_PICKAXE, 1);
            give(p, Material.IRON_SPADE, 1);
            give(p, Material.TORCH, 16);

        } else if ("archer".equals(kit)) {
            give(p, Material.BOW, 1);
            give(p, Material.ARROW, 32);

        } else if ("warrior".equals(kit)) {
            give(p, Material.IRON_SWORD, 1);
            give(p, Material.IRON_HELMET, 1);
            give(p, Material.IRON_CHESTPLATE, 1);
            give(p, Material.IRON_LEGGINGS, 1);
            give(p, Material.IRON_BOOTS, 1);

        } else if ("diamond".equals(kit)) {
            give(p, Material.DIAMOND_SWORD, 1);
            give(p, Material.DIAMOND_PICKAXE, 1);

            giveEnchantedArmor(p, Material.DIAMOND_HELMET);
            giveEnchantedArmor(p, Material.DIAMOND_CHESTPLATE);
            giveEnchantedArmor(p, Material.DIAMOND_LEGGINGS);
            giveEnchantedArmor(p, Material.DIAMOND_BOOTS);

        } else if ("food".equals(kit)) {
            give(p, Material.BREAD, 16);

        } else if ("building".equals(kit)) {
            give(p, Material.WOOD, 64);
        }

        p.sendMessage(PREFIX + ChatColor.GREEN
                + "Purchased " + kit + " for " + cash(price)
                + ". Balance: " + cash(balance(p)));

        p.closeInventory();
    }

    @Override
    public boolean onCommand(
            CommandSender sender, Command cmd,
            String label, String[] args
    ) {
        String name = cmd.getName().toLowerCase();

        if ("money".equals(name)) {
            if (args.length == 0) {
                if (!(sender instanceof Player)) {
                    sender.sendMessage(
                            "Use /money <player> from console."
                    );
                    return true;
                }

                Player p = (Player) sender;
                sender.sendMessage(PREFIX + "Balance: "
                        + ChatColor.GREEN + cash(balance(p)));
                return true;
            }

            Player target = Bukkit.getPlayer(args[0]);

            if (target == null) {
                sender.sendMessage(PREFIX + ChatColor.RED
                        + "That player must be online.");
                return true;
            }

            sender.sendMessage(PREFIX + target.getName()
                    + "'s balance: " + cash(balance(target)));
            return true;
        }

        if ("pay".equals(name)) {
            if (!(sender instanceof Player) || args.length != 2) {
                return false;
            }

            Player from = (Player) sender;
            Player to = Bukkit.getPlayer(args[0]);

            if (to == null || to.equals(from)) {
                sender.sendMessage(PREFIX + ChatColor.RED
                        + "Choose another online player.");
                return true;
            }

            double amount;

            try {
                amount = Double.parseDouble(args[1]);
            } catch (NumberFormatException ex) {
                sender.sendMessage(PREFIX + ChatColor.RED
                        + "Enter a valid amount.");
                return true;
            }

            if (amount <= 0 || amount != Math.floor(amount)) {
                sender.sendMessage(PREFIX + ChatColor.RED
                        + "Use a whole number above zero.");
                return true;
            }

            if (balance(from) < amount) {
                sender.sendMessage(PREFIX + ChatColor.RED
                        + "Not enough money.");
                return true;
            }

            setBalance(from, balance(from) - amount);
            setBalance(to, balance(to) + amount);

            from.sendMessage(PREFIX + "Paid " + to.getName()
                    + " " + cash(amount) + ".");

            to.sendMessage(PREFIX + "Received " + cash(amount)
                    + " from " + from.getName() + ".");

            return true;
        }

        if ("dbshop".equals(name)) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("Players only.");
                return true;
            }

            openShop((Player) sender);
            return true;
        }

        if ("dbproperty".equals(name)) {
            if (!(sender instanceof Player)) {
                return true;
            }

            Player p = (Player) sender;

            if (args.length == 0
                    || "list".equalsIgnoreCase(args[0])) {
                p.sendMessage(PREFIX + ChatColor.YELLOW
                        + "Property prices: cottage $5,000; "
                        + "village $10,000; large $20,000; "
                        + "mansion $50,000; plot $2,000.");

                p.sendMessage(PREFIX
                        + "Purchase with /dbproperty "
                        + "<cottage|village|large|mansion|plot>.");

                List<String> owned =
                        data.getStringList(key(p) + ".properties");

                p.sendMessage(PREFIX + "Owned properties: "
                        + (owned.isEmpty() ? "none" : owned.toString()));

                return true;
            }

            String prop = args[0].toLowerCase();
            double price;

            if ("cottage".equals(prop)) {
                price = 5000;
            } else if ("village".equals(prop)) {
                price = 10000;
            } else if ("large".equals(prop)) {
                price = 20000;
            } else if ("mansion".equals(prop)) {
                price = 50000;
            } else if ("plot".equals(prop)) {
                price = 2000;
            } else {
                p.sendMessage(PREFIX + ChatColor.RED
                        + "Unknown property. Use /dbproperty list.");
                return true;
            }

            double bal = balance(p);

            if (bal < price) {
                p.sendMessage(PREFIX + ChatColor.RED
                        + "You need " + cash(price - bal) + " more.");
                return true;
            }

            List<String> owned =
                    data.getStringList(key(p) + ".properties");

            if (owned.contains(prop)) {
                p.sendMessage(PREFIX + ChatColor.RED
                        + "You already own this property type.");
                return true;
            }

            owned.add(prop);
            data.set(key(p) + ".properties", owned);
            setBalance(p, bal - price);

            p.sendMessage(PREFIX + ChatColor.GREEN
                    + "Purchased a " + prop
                    + " property record for " + cash(price) + ".");

            p.sendMessage(PREFIX + ChatColor.YELLOW
                    + "This records ownership in the economy plugin; "
                    + "it does not automatically build or protect "
                    + "a physical house/plot.");

            saveData();
            return true;
        }

        if ("dbsmpsetmoney".equals(name)) {
            if (!sender.hasPermission("dbsmpeconomy.admin")) {
                sender.sendMessage(PREFIX + ChatColor.RED
                        + "No permission.");
                return true;
            }

            if (args.length != 2) {
                return false;
            }

            Player target = Bukkit.getPlayer(args[0]);

            if (target == null) {
                sender.sendMessage(PREFIX + ChatColor.RED
                        + "Player must be online.");
                return true;
            }

            try {
                double amount = Double.parseDouble(args[1]);

                if (amount < 0) {
                    throw new NumberFormatException();
                }

                setBalance(target, amount);

                sender.sendMessage(PREFIX + "Set "
                        + target.getName() + "'s balance to "
                        + cash(amount) + ".");

            } catch (NumberFormatException ex) {
                sender.sendMessage(PREFIX + ChatColor.RED
                        + "Enter a valid non-negative amount.");
            }

            return true;
        }

        return false;
    }
}
