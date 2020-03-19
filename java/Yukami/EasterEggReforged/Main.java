package Yukami.EasterEggReforged;

import org.bukkit.block.Block;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.UUID;
import java.util.logging.Logger;

public class Main extends JavaPlugin {

    private static final Logger log = Logger.getLogger("Minecraft");
    public YamlConfiguration config, userConfig;
    private boolean useTemplateFolder = false;

    public void onEnable() {
        loadConfigs();
        new setEasteregg(this);
        new interactListener(this);
    }

    private void loadConfigs() {
        try {
            if (useTemplateFolder) {
                File file = new File("/home/CloudNet-Wrapper/local/templates/Lobby/default/plugins/EasterEgg/config.yml");
                if (!file.exists()) {
                    InputStream in = getResource("configTemplate.yml");
                    Files.copy(in, file.toPath());
                }
                config = YamlConfiguration.loadConfiguration(file);
                file = new File("/home/CloudNet-Wrapper/local/templates/Lobby/default/plugins/EastereggReforged/users.yml");
                if (!file.exists()) {
                    file.createNewFile();
                }
                userConfig = YamlConfiguration.loadConfiguration(file);
            } else {
                if (!getDataFolder().exists()) {
                    getDataFolder().mkdir();
                }
                File file = new File(getDataFolder() + File.separator + "users.yml");
                if (!file.exists()) {
                    file.createNewFile();
                }
                userConfig = YamlConfiguration.loadConfiguration(file);
                file = new File(getDataFolder() + File.separator + "config.yml");
                if (!file.exists()) {
                    InputStream in = getResource("configTemplate.yml");
                    Files.copy(in, file.toPath());
                }
                config = YamlConfiguration.loadConfiguration(file);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveMainConfig() {
        try {
            if (useTemplateFolder) {
                File file = new File("/home/CloudNet-Wrapper/local/templates/Lobby/default/plugins/EasterEgg/config.yml");
                config.save(file);
            } else {
                File file = new File(getDataFolder() + File.separator + "config.yml");
                config.save(file);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveUserConfig() {
        try {
            if (useTemplateFolder) {
                File file = new File("/home/CloudNet-Wrapper/local/templates/Lobby/default/plugins/EastereggReforged/users.yml");
                userConfig.save(file);
            } else {
                File file = new File(getDataFolder() + File.separator + "users.yml");
                userConfig.save(file);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void createUser(UUID uuid) {
        String uu = uuid.toString();
        userConfig.set(uu + ".foundAmount", 0);
        userConfig.set(uu + ".completed", false);
        saveUserConfig();
    }

    public int getID(Block b) {
        int x = b.getX();
        int y = b.getY();
        int z = b.getZ();
        for (String s : config.getConfigurationSection("Eggs").getKeys(false)) {
            int x2 = config.getInt("Eggs." + s + ".X");
            int y2 = config.getInt("Eggs." + s + ".Y");
            int z2 = config.getInt("Eggs." + s + ".Z");
            if (x == x2 && y == y2 && z == z2) {
                return Integer.parseInt(s);
            }
        }
        return -1;
    }
}
