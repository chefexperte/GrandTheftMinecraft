package de.chefexperte.grandtheftminecraft.commands;

import de.chefexperte.grandtheftminecraft.util.PoliceUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;
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
        PoliceUtil.spawnPoliceOfficer(player.getWorld(), player.getLocation());
        return true;
    }
}
