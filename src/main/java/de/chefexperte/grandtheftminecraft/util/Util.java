package de.chefexperte.grandtheftminecraft.util;

import de.chefexperte.grandtheftminecraft.GrandTheftMinecraft;
import de.chefexperte.grandtheftminecraft.guns.Guns;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class Util {
    public record PriorityItem<T>(double priority, T item) {
    }

    public static boolean isGun(ItemStack item) {
        if (item == null) return false;
        if (item.getType() != Material.IRON_INGOT) return false;
        if (!item.hasItemMeta()) return false;
        if (!item.getItemMeta().hasCustomModelData()) return false;
        return true;
    }

    public static boolean isAmmo(ItemStack item) {
        if (item == null) return false;
        if (item.getType() != Material.ARROW) return false;
        if (!item.hasItemMeta()) return false;
        if (!item.getItemMeta().hasCustomModelData()) return false;
        return true;
    }

    public static Guns.Gun getGunFromItem(ItemStack item) {
        if (isGun(item)) {
            int customModelData = item.getItemMeta().getCustomModelData();
            return Guns.getGunFromCustomModelData(customModelData);
        }
        return null;
    }

    private static <T> T getKeyFromGun(ItemStack gun, String key, PersistentDataType<T, T> type) {
        if (!isGun(gun)) return null;
        ItemMeta gunMeta = gun.getItemMeta();
        if (gunMeta == null) return null;
        NamespacedKey realKey = NamespacedKey.fromString(key, GrandTheftMinecraft.instance);
        if (realKey == null) return null;
        PersistentDataContainer container = gunMeta.getPersistentDataContainer();
        if (!container.has(realKey, type)) return null;
        return container.get(realKey, type);
    }

    private static <T> boolean setKeyForGun(ItemStack gun, String key, PersistentDataType<T, T> type, T value) {
        if (!isGun(gun)) return false;
        ItemMeta gunMeta = gun.getItemMeta();
        if (gunMeta == null) return false;
        NamespacedKey realKey = NamespacedKey.fromString(key, GrandTheftMinecraft.instance);
        if (realKey == null) return false;
        PersistentDataContainer container = gunMeta.getPersistentDataContainer();
        container.set(realKey, type, value);
        gun.setItemMeta(gunMeta);
        return true;
    }

    public static int getGunRecoilPatternId(ItemStack gun) {
        if (!isGun(gun)) return -1;
        try {
            //noinspection DataFlowIssue
            return getKeyFromGun(gun, "gtm.rp_id", PersistentDataType.INTEGER);
        } catch (NullPointerException e) {
            return -1;
        }
    }

    public static void setGunRecoilPatternId(ItemStack gun, int ammo) {
        if (!isGun(gun)) return;
        setKeyForGun(gun, "gtm.rp_id", PersistentDataType.INTEGER, ammo);
    }

    public static int getAmmoFromGunItem(ItemStack gun) {
        if (!isGun(gun)) return -1;
        try {
            //noinspection DataFlowIssue
            return getKeyFromGun(gun, "gtm.ammo", PersistentDataType.INTEGER);
        } catch (NullPointerException e) {
            return -1;
        }
    }

    public static boolean setAmmoForGunItem(ItemStack gun, int ammo) {
        if (!isGun(gun)) return false;
        return setKeyForGun(gun, "gtm.ammo", PersistentDataType.INTEGER, ammo);
    }

    public static int getRandomIdForGunItem(ItemStack gun) {
        if (!isGun(gun)) return -1;
        try {
            //noinspection DataFlowIssue
            return getKeyFromGun(gun, "gtm.r_id", PersistentDataType.INTEGER);
        } catch (NullPointerException e) {
            return -1;
        }
    }

    public static boolean isGunReloading(ItemStack gun) {
        if (!isGun(gun)) return false;
        try {
            //noinspection DataFlowIssue
            return getKeyFromGun(gun, "gtm.is_reloading", PersistentDataType.INTEGER) == 1;
        } catch (NullPointerException e) {
            return false;
        }
    }

    public static boolean setGunReloading(ItemStack gun, boolean reloading) {
        if (!isGun(gun)) return false;
        return setKeyForGun(gun, "gtm.is_reloading", PersistentDataType.INTEGER, reloading ? 1 : 0);
    }

    public static long getLastShot(ItemStack gun) {
        if (!isGun(gun)) return -1;
        try {
            //noinspection DataFlowIssue
            return getKeyFromGun(gun, "gtm.last_shot", PersistentDataType.LONG);
        } catch (NullPointerException e) {
            return -1;
        }
    }

    public static boolean setLastShot(ItemStack gun, long lastShot) {
        if (!isGun(gun)) return false;
        return setKeyForGun(gun, "gtm.last_shot", PersistentDataType.LONG, lastShot);
    }

    public static boolean updateGunDisplayName(ItemStack gun) {
        if (!isGun(gun)) return false;
        int ammo = getAmmoFromGunItem(gun);
        if (ammo == -1) return false;
        ItemMeta gunMeta = gun.getItemMeta();
        Guns.Gun g = getGunFromItem(gun);
        String reloadString = isGunReloading(gun) ? " [Reloading]" : "";
        if (g == null) return false;
        Component name = Component.text(g.name + " [" + ammo + "/" + g.magazineSize + "]" + reloadString);
        gunMeta.displayName(name);
        gun.setItemMeta(gunMeta);
        return true;
    }

    public static ItemStack createAmmo(int amount, Guns.AmmoType ammoType) {
        ItemStack ammo = new ItemStack(Material.ARROW, amount);
        ItemMeta ammoMeta = ammo.getItemMeta();
        ammoMeta.setCustomModelData(ammoType.id);
        ammoMeta.displayName(Component.text(Guns.AMMO));
        ammo.setItemMeta(ammoMeta);
        return ammo;
    }

    public static Guns.AmmoType getAmmoTypeFromItem(ItemStack item) {
        if (isAmmo(item)) {
            int customModelData = item.getItemMeta().getCustomModelData();
            return Guns.AmmoType.fromId(customModelData);
        }
        return null;
    }

    public static boolean isPoliceOfficer(Entity entity) {
        if (entity == null) return false;
        return entity.getPersistentDataContainer().has(new NamespacedKey("gtm", "police"), PersistentDataType.BYTE);
    }

    public static boolean isCivilian(Entity entity) {
        if (entity == null) return false;
        return entity.getPersistentDataContainer().has(new NamespacedKey("gtm", "civilian"), PersistentDataType.BYTE);
    }

    public static void hideNickname(Entity e) {
        Scoreboard score = GrandTheftMinecraft.instance.scoreboard;
        Team t = score.getTeam("nhide");
        if(t == null) {
            t = score.registerNewTeam("nhide");
            t.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
            //t.setOption(Team.Option.DEATH_MESSAGE_VISIBILITY, Team.OptionStatus.NEVER);
        }
        t.addEntity(e);
    }

}
