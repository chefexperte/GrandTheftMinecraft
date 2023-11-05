package de.chefexperte.grandtheftminecraft.events;

import com.destroystokyo.paper.event.server.ServerTickStartEvent;
import de.chefexperte.grandtheftminecraft.GrandTheftMinecraft;
import de.chefexperte.grandtheftminecraft.util.PoliceUtil;
import de.chefexperte.grandtheftminecraft.util.Util;
import de.chefexperte.grandtheftminecraft.util.WantedLevel;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.Random;

import static de.chefexperte.grandtheftminecraft.util.PoliceUtil.checkDamageForCrime;

public class PoliceOfficerEvents implements Listener {

    @EventHandler
    public void onTarget(EntityTargetEvent e) {
        boolean isCustom = e.getReason() == EntityTargetEvent.TargetReason.CUSTOM;
        boolean isPolice = e.getEntity().getPersistentDataContainer().has(new NamespacedKey("gtm", "police"), PersistentDataType.BYTE);
        if (!isCustom && isPolice) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {

        boolean isPolice = Util.isPoliceOfficer(e.getPlayer());
        if (isPolice) {
            PoliceUtil.entitySpawnListener(e.getPlayer(), e.getPlayer().getUniqueId());
            PoliceUtil.policeOfficers.put(e.getPlayer().getEntityId(), new PoliceUtil.PoliceOfficer(e.getPlayer(), e.getPlayer().getUniqueId()));
        }
    }

    @EventHandler
    public void onHitEntity(EntityDamageByEntityEvent e) {
        checkDamageForCrime(e.getEntity(), e.getDamager());
    }

    @EventHandler
    public void tickEvent(ServerTickStartEvent e) {
        Random r = GrandTheftMinecraft.random;
        ArrayList<World> worlds = new ArrayList<>(GrandTheftMinecraft.instance.getServer().getWorlds());
        for (World w : worlds) {
            for (Player p : w.getPlayers()) {
                if (r.nextInt(20) == 19) {
                    if (WantedLevel.getWantedLevel(p) > 0) {
                        // for all police officers in the world
                        for (PoliceUtil.PoliceOfficer officer : PoliceUtil.policeOfficers.values().stream().filter(o -> o.entity().getWorld().equals(w)).toList()) {
                            if (officer.entity().hasLineOfSight(p)) {
                                if (officer.entity() instanceof Zombie z) {
                                    z.setTarget(p);
                                    z.getPathfinder().moveTo(p);
                                }
                            }
                        }


                    }
                }
                if (r.nextInt(500) == 499) {
                    if (WantedLevel.getWantedLevel(p) > 0) {
                        WantedLevel.setWantedLevel(p, WantedLevel.getWantedLevel(p) - 1);
                    }
                }
                // display wanted level to player
                Audience.audience(p).sendActionBar(Component.text("Wanted level: " + WantedLevel.getWantedLevel(p)));
            }
        }
    }


}
