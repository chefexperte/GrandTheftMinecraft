package de.chefexperte.grandtheftminecraft.commands;

import de.chefexperte.grandtheftminecraft.GrandTheftMinecraft;
import de.chefexperte.grandtheftminecraft.Util;
import net.kyori.adventure.text.Component;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class SpawnPoliceCommand extends Command {

    private static final String usageMessage = "/spawn-police";

    public SpawnPoliceCommand() {
        super("spawn-police", "Spawns a police officer", usageMessage, Arrays.asList("spawn-police", "spawnpolice"));
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("You must be a player to use this command!"));
            return true;
        }
        Zombie officer = player.getWorld().spawn(player.getLocation(), Zombie.class, off -> {
            //off.customName(Component.text("Police Officer"));
            PersistentDataContainer c = off.getPersistentDataContainer();
            c.set(new NamespacedKey("gtm", "police"), PersistentDataType.BYTE, (byte) 1);
            c.set(new NamespacedKey("gtm", "fake"), PersistentDataType.BYTE, (byte) 1);
        });
        //officer.setAI(false);
        //officer.customName(null);
        //officer.setCustomNameVisible(false);
        //Util.hideNickname(officer);
        officer.setShouldBurnInDay(false);
        officer.setSilent(true);
        officer.getPersistentDataContainer().set(new NamespacedKey("gtm", "police"), PersistentDataType.BYTE, (byte) 1);
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
                if (closestEntity != null) {
                    officer.setTarget(closestEntity);
                } else {
                    officer.setTarget(null);
                }
            }
        }.runTaskTimer(GrandTheftMinecraft.instance, 0, 20);
        return true;
    }
}
