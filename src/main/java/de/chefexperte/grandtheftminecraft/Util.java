package de.chefexperte.grandtheftminecraft;

import de.chefexperte.grandtheftminecraft.guns.Guns;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
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

    public static Guns.Gun getGunFromItem(ItemStack item) {
        if (isGun(item)) {
            int customModelData = item.getItemMeta().getCustomModelData();
            return Guns.getGunFromCustomModelData(customModelData);
        }
        return null;
    }

    private static boolean isGunMetaAndKeyValid(ItemMeta meta, NamespacedKey key) {
        if (key == null) {
            GrandTheftMinecraft.instance.getLogger().warning("get-gun: key is null");
            return false;
        }
        if (meta == null) {
            GrandTheftMinecraft.instance.getLogger().warning("get-gun: gunMeta is null");
            return false;
        }
        if (!meta.getPersistentDataContainer().has(key, PersistentDataType.INTEGER)) {
            GrandTheftMinecraft.instance.getLogger().warning("get-gun: gunMeta does not have key");
            return false;
        }
        return true;
    }

    public static int getAmmoFromGunItem(ItemStack gun) {
        if (!isGun(gun)) return -1;
        NamespacedKey key = NamespacedKey.fromString("gtm.ammo", GrandTheftMinecraft.instance);
        ItemMeta gunMeta = gun.getItemMeta();
        if (!isGunMetaAndKeyValid(gunMeta, key)) return -1;
        //noinspection DataFlowIssue
        return gunMeta.getPersistentDataContainer().get(key, PersistentDataType.INTEGER);
    }

    public static void setAmmoForGunItem(ItemStack gun, int ammo) {
        if (!isGun(gun)) return;
        NamespacedKey key = NamespacedKey.fromString("gtm.ammo", GrandTheftMinecraft.instance);
        ItemMeta gunMeta = gun.getItemMeta();
        if (!isGunMetaAndKeyValid(gunMeta, key)) return;
        //noinspection DataFlowIssue
        gunMeta.getPersistentDataContainer().set(key, PersistentDataType.INTEGER, ammo);
        gun.setItemMeta(gunMeta);
    }

    public static void updateGunDisplayName(ItemStack gun) {
        if (!isGun(gun)) return;
        int ammo = getAmmoFromGunItem(gun);
        if (ammo == -1) return;
        ItemMeta gunMeta = gun.getItemMeta();
        Guns.Gun g = getGunFromItem(gun);
        if (g == null) return;
        Component name = Component.text(g.name + " [" + ammo + "/" + g.magazineSize + "]");
        gunMeta.displayName(name);
        gun.setItemMeta(gunMeta);
    }

}
