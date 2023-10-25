package de.chefexperte.grandtheftminecraft.commands;

import de.chefexperte.grandtheftminecraft.Guns;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class GetGunCommand extends Command {

    private static final String usageMessage = "/get-gun list | <gun name>";

    public GetGunCommand() {
        super("get-gun", "Gives you a gun", usageMessage, Arrays.asList("get-gun", "getgun"));
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1 && args[0].equals("list")) {
            TextComponent.Builder builder = Component.text().content("Guns: ");
            for (Guns.Gun g : Guns.guns) {
                builder.append(Component.text("\n- " + g.name));
            }
            sender.sendMessage(builder.build());
            return true;
        }
        Guns.Gun g;
        boolean givenAsNumer = false;
        if (sender instanceof Player && args.length == 1) {
            // check if requested gun is given as gun id
            try {
                int customModelData = Integer.parseInt(args[0]);
                givenAsNumer = true;
                g = Guns.getGunFromCustomModelData(customModelData);
                giveGunToPlayer((Player) sender, g);
                return true;
            } catch (NumberFormatException e) {
                // not a number
            }
            // check if requested gun is given as gun name
            g = Guns.getGunFromName(args[0]);
            if (g != null) {
                giveGunToPlayer((Player) sender, g);
            } else {
                if (givenAsNumer) {
                    sender.sendMessage(Component.text("Invalid gun id!"));
                } else {
                    sender.sendMessage(Component.text("Invalid gun name!"));
                }
            }
            return true;
        }
        // send red usage message
        sender.sendMessage(Component.text("Usage: " + usageMessage, NamedTextColor.RED));
        return false;
    }

    private void giveGunToPlayer(Player p, Guns.Gun g) {
        ItemStack gun = new ItemStack(Material.IRON_INGOT);
        ItemMeta gunMeta = gun.getItemMeta();
        gunMeta.setCustomModelData(g.id);
        gunMeta.displayName(Component.text(g.name));
        gun.setItemMeta(gunMeta);
        p.getInventory().addItem(gun);
    }

}
