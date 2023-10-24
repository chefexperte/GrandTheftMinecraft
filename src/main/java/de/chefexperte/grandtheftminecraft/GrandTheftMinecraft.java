package de.chefexperte.grandtheftminecraft;

import de.chefexperte.grandtheftminecraft.commands.GetGunCommand;
import de.chefexperte.grandtheftminecraft.events.GunEvents;
import org.bukkit.command.Command;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;
import java.util.Random;

public final class GrandTheftMinecraft extends JavaPlugin {

    public static GrandTheftMinecraft instance;
    public static Random random = new Random();

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;
        Guns.init();
        getLogger().info("Was geht ab Digga");
        this.getServer().getCommandMap().register("get-gun", new GetGunCommand());
        this.getServer().getPluginManager().registerEvents(new GunEvents(), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
