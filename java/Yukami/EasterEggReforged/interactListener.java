package Yukami.EasterEggReforged;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class interactListener implements Listener {

    private Main plugin;
    private final CooldownManager cooldownManager = new CooldownManager();


    public interactListener(Main plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    private void onInteract(PlayerInteractEvent e) {
        if (!e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            return;
        }
        if (!e.getClickedBlock().getType().equals(Material.SKULL)) {
            return;
        }

        Player p = e.getPlayer();
        Block b = e.getClickedBlock();

        long timeLeft = System.currentTimeMillis() - cooldownManager.getCooldown(p.getUniqueId());
        if(!(TimeUnit.MILLISECONDS.toSeconds(timeLeft) >= CooldownManager.DEFAULT_COOLDOWN)){
            return;
        }
        cooldownManager.setCooldown(p.getUniqueId(), System.currentTimeMillis());


        YamlConfiguration config = plugin.config;
        int id = plugin.getID(b);
        if (id == -1) {
            return;
        }

        YamlConfiguration uConfig = plugin.userConfig;
        if (uConfig.get(p.getUniqueId().toString() + ".foundEggs") == null) {
            addToUser(id, p);
            return;
        }
        List<Integer> foundEggs = plugin.userConfig.getIntegerList(p.getUniqueId().toString() + ".foundEggs");
        if (foundEggs.contains(id)) {
            p.sendMessage(Util.Color("&cYou have already found this Easter egg!"));
            return;
        }
        addToUser(id, p);
    }

    private void addToUser(int id, Player p) {
        UUID uuid = p.getUniqueId();
        List<Integer> foundEggs = plugin.userConfig.getIntegerList(uuid.toString() + ".foundEggs");
        foundEggs.add(id);
        plugin.userConfig.set(uuid.toString() + ".foundEggs", foundEggs);
        int found = plugin.userConfig.getInt(uuid.toString() + ".foundAmount");
        plugin.userConfig.set(uuid.toString() + ".foundAmount", (found + 1));
        plugin.saveUserConfig();


        String moneyAddCommand = plugin.config.getString("moneyCommand");
        int reward = plugin.config.getInt("rewardPerEgg");
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), moneyAddCommand.replace("%p", p.getName()).replace("%m", Integer.toString(reward)));
        int eaAmount = plugin.config.getConfigurationSection("Eggs").getKeys(false).size();
        p.sendMessage(Util.Color("&aYou found an Easter egg! Congratz!\nMoney reward: " + reward));
        if (plugin.userConfig.getInt(p.getUniqueId().toString() + ".foundAmount") == eaAmount && !plugin.userConfig.getBoolean(p.getUniqueId().toString() + ".completed")) {
            reward = plugin.config.getInt("rewardOnComplete");
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), moneyAddCommand.replace("%p", p.getName()).replace("%m", Integer.toString(reward)));
            p.sendMessage(Util.Color("&aYou found all Easter eggs! Congratz!\nMoney reward: " + reward));
            plugin.userConfig.set(p.getUniqueId().toString() + ".completed", true);
            plugin.saveUserConfig();
        }
    }
}
