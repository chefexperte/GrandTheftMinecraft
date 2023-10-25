package de.chefexperte.grandtheftminecraft;

import de.chefexperte.grandtheftminecraft.commands.GetGunCommand;
import de.chefexperte.grandtheftminecraft.events.GunEvents;
import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;
import java.util.Random;

public final class GrandTheftMinecraft extends JavaPlugin {

    public static GrandTheftMinecraft instance;
    public static Random random = new Random();

    public static void sendDebugMessage(String msg) {
        for (Player p : GrandTheftMinecraft.instance.getServer().getOnlinePlayers()) {
            p.sendMessage(Component.text(msg));
        }
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;
        Guns.init();
        getLogger().info("Welcome to Grand Theft Minecraft!");
        this.getServer().getCommandMap().register("get-gun", new GetGunCommand());
        this.getServer().getPluginManager().registerEvents(new GunEvents(), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
