package Yukami.EasterEggReforged;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public class setEasteregg implements CommandExecutor {

    private Main plugin;

    public setEasteregg(Main plugin) {
        this.plugin = plugin;
        plugin.getCommand("easteregg").setExecutor(this);
    }

    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("Player only!");
            return false;
        }
        Player p = (Player) commandSender;

        if (strings.length == 0) {
            UUID uuid = p.getUniqueId();
            if (plugin.userConfig.get(uuid.toString()) == null) {
                plugin.createUser(uuid);
            }
            int foundAmount = plugin.userConfig.getInt(uuid.toString() + ".foundAmount");
            int eggAmount;
            if (plugin.config.getConfigurationSection("Eggs") != null) {
                eggAmount = plugin.config.getConfigurationSection("Eggs").getKeys(false).size();
            } else {
                eggAmount = 0;
            }
            p.sendMessage(Util.Color("&6You have found " + foundAmount + " out of " + eggAmount + " Easter eggs!"));
            return true;
        }

        if (!p.hasPermission("ear.setEgg")) {
            p.sendMessage(Util.Color("&cYou don't have enough permissions to use this command!"));
            return false;
        }

        Block b = p.getTargetBlock((Set< Material>) null, 7);
        if (!b.getType().equals(Material.SKULL)) {
            p.sendMessage(Util.Color("&cBlock must be a skull!"));
            return false;
        }
        if (strings[0].equalsIgnoreCase("remove")) {
            removeFromConfig(b, p);
            return true;
        }
        if (strings[0].equalsIgnoreCase("add")) {
            addToConfig(b, p);
            return true;
        }
        return false;
    }

    private void removeFromConfig(Block b, Player p) {
        YamlConfiguration config = plugin.config;
        int x = b.getX();
        int y = b.getY();
        int z = b.getZ();
        int id = -1;
        int keyAmount;
        if (config.getConfigurationSection("Eggs") != null ) {
            keyAmount = config.getConfigurationSection("Eggs").getKeys(false).size();
        } else {
            keyAmount = 0;
        }
        if (keyAmount != 0) {
            for (String s : config.getConfigurationSection("Eggs").getKeys(false)) {
                int x2 = config.getInt("Eggs." + s + ".X");
                int y2 = config.getInt("Eggs." + s + ".Y");
                int z2 = config.getInt("Eggs." + s + ".Z");
                if (x == x2 && y == y2 && z == z2) {
                    config.set("Eggs." + s, null);
                    plugin.saveMainConfig();
                    p.sendMessage(Util.Color("&aThis block is no longer an Easter egg!"));
                    id = Integer.parseInt(s);
                    break;
                }
            }
        } else {
            p.sendMessage(Util.Color("&cThis block is not an Easter egg!"));
            return;
        }
        if (id == -1) {
            p.sendMessage(Util.Color("&cThis block is not an Easter egg!"));
            return;
        }
        YamlConfiguration userConfig = plugin.userConfig;
        for (String uuid : userConfig.getKeys(false)) {
            if (userConfig.get(uuid + ".foundEggs") == null) {
                continue;
            }
            List<Integer> eggs = userConfig.getIntegerList(uuid + ".foundEggs");
            if (eggs.contains(id)) {
                eggs.remove(id);
                userConfig.set(uuid + ".foundEggs", eggs);
            }
        }
        plugin.saveUserConfig();
    }

    private void addToConfig(Block b, Player p) {
        YamlConfiguration config = plugin.config;
        int x = b.getX();
        int y = b.getY();
        int z = b.getZ();
        int keyAmount;
        if (config.getConfigurationSection("Eggs") != null ) {
            keyAmount = config.getConfigurationSection("Eggs").getKeys(false).size();
        } else {
            keyAmount = 0;
        }        if (keyAmount != 0) {
            for (String s : config.getConfigurationSection("Eggs").getKeys(false)) {
                int x2 = config.getInt("Eggs." + s + ".X");
                int y2 = config.getInt("Eggs." + s + ".Y");
                int z2 = config.getInt("Eggs." + s + ".Z");

                if (x == x2 && y == y2 && z == z2) {
                    p.sendMessage(Util.Color("&cThis block is already an Easter egg!"));
                    return;
                }
            }
        }
        int id = Util.generateNewID();
        while (config.get("Eggs." + id) != null) {
            id = Util.generateNewID();
        }
        config.set("Eggs." + id + ".X", x);
        config.set("Eggs." + id + ".Y", y);
        config.set("Eggs." + id + ".Z", z);
        plugin.saveMainConfig();
        p.sendMessage(Util.Color("&aThis block is now an Easter egg!"));
    }
}
