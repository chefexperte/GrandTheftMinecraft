package de.chefexperte.grandtheftminecraft.commands;

import de.chefexperte.grandtheftminecraft.util.CivilianUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class SpawnCivilianCommand extends Command {

    private static final String usageMessage = "/spawn-civilian";

    public SpawnCivilianCommand() {
        super("spawn-civilian", "Spawns a civilian", usageMessage, Arrays.asList("spawn-civilian", "spawncivilian"));
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("You must be a player to use this command!"));
            return true;
        }
        CivilianUtil.spawnCivilian(player.getWorld(), player.getLocation());
        return true;
    }
}
