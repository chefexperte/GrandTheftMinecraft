package de.chefexperte.grandtheftminecraft.util;

import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.UUID;

public class CivilianUtil {

    public static Zombie spawnCivilian(World w, Location l) {
        Zombie civilian = w.spawn(l, Zombie.class, civ -> {
            PersistentDataContainer c = civ.getPersistentDataContainer();
            c.set(new NamespacedKey("gtm", "civilian"), PersistentDataType.BYTE, (byte) 1);
            c.set(new NamespacedKey("gtm", "fake"), PersistentDataType.BYTE, (byte) 1);
        });
        civilian.setShouldBurnInDay(false);
        civilian.setSilent(true);
        return civilian;
    }

    public static void entitySpawnListener(Player p, UUID uuid) {
        PacketUtils.entitySpawnListener(p, uuid, "Civilian", "Suit Villager", true);
    }

}
