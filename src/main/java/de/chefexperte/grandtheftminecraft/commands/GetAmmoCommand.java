package de.chefexperte.grandtheftminecraft.commands;

import de.chefexperte.grandtheftminecraft.guns.Guns;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class GetAmmoCommand extends Command {

    private static final String usageMessage = "/get-ammo <amount>";

    public GetAmmoCommand() {
        super("get-arrow", "Gives you ammo", usageMessage, Arrays.asList("get-ammo", "getammo"));
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player && args.length == 1) {
            // check if requested gun is given as gun id
            try {
                Player p = (Player) sender;
                int amount = Integer.parseInt(args[0]);
                ItemStack ammo = new ItemStack(Material.ARROW, amount);
                ItemMeta ammoMeta = ammo.getItemMeta();
                ammoMeta.setCustomModelData(1);
                ammoMeta.displayName(Component.text(Guns.AMMO));
                ammo.setItemMeta(ammoMeta);
                p.getInventory().addItem(ammo);
                return true;
            } catch (NumberFormatException e) {
                // not a number
                sender.sendMessage(Component.text("You have to pass a number as argument", NamedTextColor.RED));
            }
            return true;
        }
        if (!(sender instanceof Player)) {
            sender.sendMessage(Component.text("You have to be a player to use this command!", NamedTextColor.RED));
            return false;
        }
        // send red usage message
        sender.sendMessage(Component.text("Usage: " + usageMessage, NamedTextColor.RED));
        return false;
    }

}
