package de.chefexperte.grandtheftminecraft.events;

import de.chefexperte.grandtheftminecraft.GrandTheftMinecraft;
import de.chefexperte.grandtheftminecraft.Util;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

public class PoliceOfficerEvents implements Listener {

    @EventHandler
    public void onTarget(EntityTargetEvent e) {
        if (e.getEntity().getPersistentDataContainer().has(new NamespacedKey("gtm", "police"), PersistentDataType.BYTE)) {
            if (e.getTarget() instanceof Player) {
                //Player p = (Player) e.getTarget();
                //if (p.getPersistentDataContainer().has(new NamespacedKey("gtm", "police"), PersistentDataType.BYTE)) {
                    e.setCancelled(true);
                //}
            }
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e){
        boolean isPolice = Util.isPoliceOfficer(e.getPlayer());
        if (!isPolice) {
            //GrandTheftMinecraft.sendDebugMessage("Player " + e.getPlayer().getName() + " is not a police officer");
            return;
        }
        //Util.hideNickname(e.getPlayer());
    }

}
