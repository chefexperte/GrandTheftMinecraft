package de.chefexperte.grandtheftminecraft.commands;

import net.kyori.adventure.text.Component;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;
import org.bukkit.persistence.PersistentDataType;
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
        Zombie officer = player.getWorld().spawn(player.getLocation(), Zombie.class);
        //officer.setAI(false);
        officer.customName(Component.text("Police Officer"));
        officer.setCustomNameVisible(true);
        officer.setShouldBurnInDay(false);
        officer.getPersistentDataContainer().set(new NamespacedKey("gtm", "police"), PersistentDataType.BYTE, (byte) 1);
        double closest = Float.MAX_VALUE;
        LivingEntity closestEntity = null;
        for (Entity e : officer.getNearbyEntities(100, 20, 100)) {
            if (e instanceof Pillager) {
                double dist = e.getLocation().distance(officer.getLocation());
                if (dist < closest) {
                    closest = dist;
                    closestEntity = (LivingEntity) e;
                }
                break;
            }
        }
        if (closestEntity != null) {
            officer.setTarget(closestEntity);
        }
        return true;
    }
}
