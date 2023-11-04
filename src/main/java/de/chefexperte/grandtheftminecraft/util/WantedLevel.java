package de.chefexperte.grandtheftminecraft.util;

import org.bukkit.entity.Entity;

import java.util.HashMap;

public class WantedLevel {

    public static HashMap<Entity, Integer> wantedLevels = new HashMap<>();

    public static void setWantedLevel(Entity entity, int level) {
        wantedLevels.put(entity, level);
    }

    public static int getWantedLevel(Entity entity) {
        return wantedLevels.getOrDefault(entity, 0);
    }

}
