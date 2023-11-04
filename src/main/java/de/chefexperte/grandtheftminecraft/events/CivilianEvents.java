package de.chefexperte.grandtheftminecraft.events;

import com.destroystokyo.paper.event.server.ServerTickStartEvent;
import de.chefexperte.grandtheftminecraft.GrandTheftMinecraft;
import de.chefexperte.grandtheftminecraft.util.CivilianUtil;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.Random;

public class CivilianEvents implements Listener {

    private static final float civilianSpawnChance = 0.0015f;
    private static final float civilianSpawnMaxRadius = 45;

    @EventHandler
    public void tickEvent(ServerTickStartEvent e) {
        Random r = GrandTheftMinecraft.random;
        if (r.nextFloat() < civilianSpawnChance) {
            ArrayList<World> worlds = new ArrayList<>(GrandTheftMinecraft.instance.getServer().getWorlds());
            for (World w : worlds) {
                int playerCount = w.getPlayers().size();
                if (playerCount == 0) continue;
                Player p = w.getPlayers().get(r.nextInt(playerCount));
                Location l = p.getLocation().add(r.nextFloat() * civilianSpawnMaxRadius * 2 - civilianSpawnMaxRadius,
                        0,
                        r.nextFloat() * civilianSpawnMaxRadius * 2 - civilianSpawnMaxRadius);
                l = w.getHighestBlockAt(l).getLocation().add(0, 1, 0);
                CivilianUtil.spawnCivilian(w, l);
            }
        }
    }

}
