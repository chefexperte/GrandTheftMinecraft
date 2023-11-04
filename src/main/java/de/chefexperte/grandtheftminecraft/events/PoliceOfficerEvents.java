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
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.Random;

public class PoliceOfficerEvents implements Listener {

    @EventHandler
    public void onTarget(EntityTargetEvent e) {
        boolean isCustom = e.getReason() == EntityTargetEvent.TargetReason.CUSTOM;
        boolean isPolice = e.getEntity().getPersistentDataContainer().has(new NamespacedKey("gtm", "police"), PersistentDataType.BYTE);
        if (!isCustom && isPolice) {
            if (e.getTarget() instanceof Player) {
                //Player p = (Player) e.getTarget();
                //if (p.getPersistentDataContainer().has(new NamespacedKey("gtm", "police"), PersistentDataType.BYTE)) {
                e.setCancelled(true);
                //}
            }
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {

        boolean isPolice = Util.isPoliceOfficer(e.getPlayer());
        if (!isPolice) {
            //GrandTheftMinecraft.sendDebugMessage("Player " + e.getPlayer().getName() + " is not a police officer");
        }
        //Util.hideNickname(e.getPlayer());
    }

    @EventHandler
    public void onHitEntity(EntityDamageByEntityEvent e) {
        boolean isCrime = (Util.isCivilian(e.getEntity()) || Util.isPoliceOfficer(e.getEntity()));
        if (isCrime && e.getDamager() instanceof Player p) {
            if (Util.isPoliceOfficer(p)) {
                // do nothing, police officers can hit civilians
            } else {
                if (PoliceUtil.canCrimeBeSeen(p.getLocation())) {
                    WantedLevel.setWantedLevel(p, WantedLevel.getWantedLevel(p) + 1);
                }
            }
        }
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
                        for (PoliceUtil.PoliceOfficer officer : PoliceUtil.policeOfficers.values().stream().filter(o -> o.zombie().getWorld().equals(w)).toList()) {
                            if (officer.zombie().hasLineOfSight(p)) {
                                officer.zombie().setTarget(p);
                                officer.zombie().getPathfinder().moveTo(p);
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
