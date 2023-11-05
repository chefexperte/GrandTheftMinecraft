package de.chefexperte.grandtheftminecraft.util;

import de.chefexperte.grandtheftminecraft.GrandTheftMinecraft;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.UUID;

public class PoliceUtil {

    public record PoliceOfficer(LivingEntity entity, UUID uuid) {
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

    public static boolean canCrimeBeSeen(Location crimeLocation, double officerFOV) {

        // check direct line of sight to police officers
        for (PoliceOfficer officer : policeOfficers.values()) {
            boolean hasLineOfSight = officer.entity.hasLineOfSight(crimeLocation);
            boolean isInFOV = isInFOV(officer.entity, crimeLocation, officerFOV);
            if (hasLineOfSight && isInFOV) {
                return true;
            }
        }

        return false;
    }

    private static boolean isInFOV(Entity entity, Location target, double fov) {
        Vector directionFacing = entity.getLocation().getDirection().normalize();
        Vector directionToTarget = target.clone().subtract(entity.getLocation()).toVector().normalize();

        double angle = Math.toDegrees(Math.acos(directionFacing.dot(directionToTarget)));

        return angle <= fov;
    }

    public static void checkDamageForCrime(Entity hitEntity, Entity damager) {
        boolean isCrime = (Util.isCivilian(hitEntity) || Util.isPoliceOfficer(hitEntity));
        boolean damagerIsPlayer = damager instanceof Player;
        boolean damagerIsPlayerShot = damager instanceof Arrow && ((Arrow) damager).getShooter() instanceof Player;
        boolean isHitEntityPoliceOfficer = Util.isPoliceOfficer(hitEntity);
        if (isCrime && (damagerIsPlayer || damagerIsPlayerShot)) {
            if (Util.isPoliceOfficer(damager)) {
                // do nothing, police officers can hit civilians
            } else {
                double fov = isHitEntityPoliceOfficer ? 120 : 70;
                if (PoliceUtil.canCrimeBeSeen(damager.getLocation(), fov)) {
                    WantedLevel.setWantedLevel(damager, WantedLevel.getWantedLevel(damager) + 1);
                }
            }
        }
    }

}
