package de.chefexperte.grandtheftminecraft.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class GetGunCommand extends Command {

    public GetGunCommand() {
        super("get-gun", "Gives you a gun", "/get-gun", Arrays.asList("get-gun", "getgun"));
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            ItemStack gun = new ItemStack(Material.IRON_INGOT);
            ItemMeta gunMeta = gun.getItemMeta();
            gunMeta.setCustomModelData(1);
            gunMeta.displayName(Component.text("Desert Eagle"));
            gun.setItemMeta(gunMeta);
            player.getInventory().addItem(gun);
        }
        return true;
    }
}
