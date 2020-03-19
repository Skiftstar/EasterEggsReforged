package Yukami.EasterEggReforged;

import org.bukkit.ChatColor;

import java.util.Random;

public class Util {

    public static String Color(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    public static int generateNewID() {
        Random rand = new Random();
        return Math.abs(rand.nextInt(Integer.MAX_VALUE));
    }
}
