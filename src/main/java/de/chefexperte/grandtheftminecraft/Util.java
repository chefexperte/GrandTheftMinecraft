package de.chefexperte.grandtheftminecraft;

import de.chefexperte.grandtheftminecraft.guns.Guns;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

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

    public static boolean updateGunDisplayName(ItemStack gun) {
        if (!isGun(gun)) return false;
        int ammo = getAmmoFromGunItem(gun);
        if (ammo == -1) return false;
        ItemMeta gunMeta = gun.getItemMeta();
        Guns.Gun g = getGunFromItem(gun);
        if (g == null) return false;
        Component name = Component.text(g.name + " [" + ammo + "/" + g.magazineSize + "]");
        gunMeta.displayName(name);
        gun.setItemMeta(gunMeta);
        return true;
    }

}
