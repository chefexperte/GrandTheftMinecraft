package de.chefexperte.grandtheftminecraft.guns;

import java.util.ArrayList;

public class Guns {

    public static ArrayList<Gun> guns = new ArrayList<>();
    public static Gun DESERT_EAGLE = new Gun(1, "Desert Eagle", "Desert Eagle Bullet", 9,
            11, 500, "minecraft:gtm.desert_eagle", 7, 1.5f, 2f,
            RecoilPatterns.DESERT_EAGLE, AmmoType.FIFTY);
    public static Gun ROCKET_LAUNCHER = new Gun(2, "Rocket Launcher", "Rocket", 1,
            50, 30000, "minecraft:entity.firework_rocket.launch", 1, 2.5f,
            0.5f, RecoilPatterns.ROCKET_LAUNCHER, AmmoType.ROCKET);
    public static Gun AK47 = new Gun(3, "AK-47", "AK-47 Bullet", 8,
            5, 1700, "minecraft:gtm.ak47", 30, 1.8f, 6f,
            RecoilPatterns.AK47, AmmoType.SEVEN_SIX_TWO);

    public static String AMMO = "Ammo";

    public static void init() {
        guns.add(DESERT_EAGLE);
        guns.add(ROCKET_LAUNCHER);
        guns.add(AK47);
    }

    public static Gun getGunFromCustomModelData(int customModelData) {
        for (Gun gun : guns) {
            if (gun.id == customModelData) {
                return gun;
            }
        }
        return null;
    }

    public static Gun getGunFromName(String name) {
        for (Gun gun : guns) {
            if (gun.name.equals(name)) {
                return gun;
            }
        }
        return null;
    }

    public static Gun getGunFromBulletName(String name) {
        for (Gun gun : guns) {
            if (gun.bulletName.equals(name)) {
                return gun;
            }
        }
        return null;
    }

    public static class Gun {
        public int id;
        public String name;
        public String bulletName;
        public float bulletSpeed;
        public float damage;
        public float price;
        public String sound;
        public int magazineSize;
        public float reloadTime;
        public float fireRate;
        public RecoilPatterns.RecoilPattern recoilPattern;
        public AmmoType ammoType;

        public Gun(int id, String name, String bulletName, float bulletSpeed, float damage, float price, String sound,
                   int magazineSize, float reloadTime, float fireRate, RecoilPatterns.RecoilPattern recoilPattern, AmmoType ammoType) {
            this.id = id;
            this.name = name;
            this.bulletName = bulletName;
            this.bulletSpeed = bulletSpeed;
            this.damage = damage;
            this.price = price;
            this.sound = sound;
            this.magazineSize = magazineSize;
            this.reloadTime = reloadTime;
            this.fireRate = fireRate;
            this.recoilPattern = recoilPattern;
            this.ammoType = ammoType;
        }
    }

    public enum AmmoType {
        SEVEN_SIX_TWO(1, "7.62mm"),
        NINE(2, "9mm"),
        FIFTY(3, ".50"),
        FIVE_FIVE_SIX(4, "5.56mm"),
        ROCKET(5, "Rocket");

        public final int id;
        public final String name;

        AmmoType(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public static AmmoType fromId(int id) {
            for (AmmoType ammoType : AmmoType.values()) {
                if (ammoType.id == id) {
                    return ammoType;
                }
            }
            return null;
        }

    }

}
