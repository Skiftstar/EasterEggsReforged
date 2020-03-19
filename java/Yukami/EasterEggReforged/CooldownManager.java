package Yukami.EasterEggReforged;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CooldownManager {

    private final Map<UUID , Long> cooldowns = new HashMap<>();

    public static final long DEFAULT_COOLDOWN = 1;

    public void setCooldown(UUID player, long time){
        if(time < 1) {
            cooldowns.remove(player);
        } else {
            cooldowns.put(player, time);
        }
    }

    public long getCooldown(UUID player){
        return cooldowns.getOrDefault(player, (long) 0);
    }
}
