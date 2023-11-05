package de.chefexperte.grandtheftminecraft.events;

import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.persistence.PersistentDataType;

public class CivilianEvents implements Listener {

    @EventHandler
    public void onTarget(EntityTargetEvent e) {
        boolean isCustom = e.getReason() == EntityTargetEvent.TargetReason.CUSTOM;
        boolean isCivilian = e.getEntity().getPersistentDataContainer().has(new NamespacedKey("gtm", "civilian"), PersistentDataType.BYTE);
        if (!isCustom && isCivilian) {
            e.setCancelled(true);
        }
    }
}
