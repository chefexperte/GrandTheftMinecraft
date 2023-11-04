package de.chefexperte.grandtheftminecraft.util;

import de.chefexperte.grandtheftminecraft.GrandTheftMinecraft;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.UUID;

public class PoliceUtil {

    public record PoliceOfficer(Zombie zombie, UUID uuid) {
    }

    public static HashMap<Integer, PoliceOfficer> policeOfficers = new HashMap<>();

    public static Zombie spawnPoliceOfficer(World w, Location l) {
        Zombie officer = w.spawn(l, Zombie.class, off -> {
            //off.customName(Component.text("Police Officer"));
            PersistentDataContainer c = off.getPersistentDataContainer();
            c.set(new NamespacedKey("gtm", "police"), PersistentDataType.BYTE, (byte) 1);
            c.set(new NamespacedKey("gtm", "fake"), PersistentDataType.BYTE, (byte) 1);
        });
        officer.setShouldBurnInDay(false);
        officer.setSilent(true);
        new BukkitRunnable() {
            @Override
            public void run() {
                if (officer.isDead() || !officer.isValid()) {
                    this.cancel();
                    return;
                }
                double closest = Float.MAX_VALUE;
                LivingEntity closestEntity = null;
                for (Entity e : officer.getNearbyEntities(100, 20, 100)) {
                    if (e instanceof Pillager) {
                        double dist = e.getLocation().distance(officer.getLocation());
                        if (dist < closest) {
                            closest = dist;
                            closestEntity = (LivingEntity) e;
                        }
                    }
                }
                officer.setTarget(closestEntity);
            }
        }.runTaskTimer(GrandTheftMinecraft.instance, 0, 20);
        return officer;
    }

    public static void entitySpawnListener(Player p, UUID uuid) {
        PacketUtils.entitySpawnListener(p, uuid, "PoliceOfficer", "Police Officer", true);
    }

    public static boolean canCrimeBeSeen(Location crimeLocation) {

        // check direct line of sight to police officers
        for (PoliceOfficer officer : policeOfficers.values()) {
            if (officer.zombie.hasLineOfSight(crimeLocation)) {
                return true;
            }
        }

        return false;
    }

}
