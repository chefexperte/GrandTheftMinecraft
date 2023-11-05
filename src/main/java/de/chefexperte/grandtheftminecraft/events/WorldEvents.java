package de.chefexperte.grandtheftminecraft.events;

import com.destroystokyo.paper.event.server.ServerTickStartEvent;
import de.chefexperte.grandtheftminecraft.GrandTheftMinecraft;
import de.chefexperte.grandtheftminecraft.util.CivilianUtil;
import de.chefexperte.grandtheftminecraft.util.PoliceUtil;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.Random;

public class WorldEvents implements Listener {

    private static final float civilianSpawnChance = 0.0015f; // 3% chance per second
    private static final float civilianSpawnMaxRadius = 65;
    private static final float policeSpawnChance = 0.0008f; // 1.6% chance per second
    private static final float policeSpawnMaxRadius = 65;

    @EventHandler
    public void tickEvent(ServerTickStartEvent e) {
        Random r = GrandTheftMinecraft.random;
        boolean spawnCivilian = r.nextFloat() < civilianSpawnChance;
        boolean spawnPolice = r.nextFloat() < policeSpawnChance;
        if (!(spawnPolice || spawnCivilian)) {
            //return;
        }
        ArrayList<World> worlds = new ArrayList<>(GrandTheftMinecraft.instance.getServer().getWorlds());
        for (World w : worlds) {
            int playerCount = w.getPlayers().size();
            if (playerCount == 0) continue;
            for (Player pl : w.getPlayers()) {
                if (pl.getAttribute(Attribute.GENERIC_ARMOR).getModifiers().stream().anyMatch(m -> m.getName().equals("gtm"))) {
                    continue;
                }
                AttributeModifier m = new AttributeModifier("gtm", 3, AttributeModifier.Operation.ADD_NUMBER);
                pl.getAttribute(Attribute.GENERIC_ARMOR).addModifier(m);
                //pl.sendMessage("§cYou are in a high crime area. Be careful!");
            }
            Player p = w.getPlayers().get(r.nextInt(playerCount));
            if (spawnPolice) {
                Location l = getRandomLocation(p.getLocation(), policeSpawnMaxRadius);
                PoliceUtil.spawnPoliceOfficer(w, l);
            }
            if (spawnCivilian) {
                Location l = getRandomLocation(p.getLocation(), civilianSpawnMaxRadius);
                CivilianUtil.spawnCivilian(w, l);
            }
        }
    }

    private Location getRandomLocation(Location l, float maxRadius) {
        Random r = GrandTheftMinecraft.random;
        l = l.add(r.nextFloat() * maxRadius * 2 - maxRadius,
                0,
                r.nextFloat() * maxRadius * 2 - maxRadius);
        l = l.getWorld().getHighestBlockAt(l).getLocation().add(0, 1, 0);
        return l;
    }

}
