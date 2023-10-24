package de.chefexperte.grandtheftminecraft;

import java.util.ArrayList;

public class Guns {

    public static ArrayList<Gun> guns = new ArrayList<>();
    public static Gun DESERT_EAGLE = new Gun(1, "Desert Eagle", "Desert Eagle Bullet", 8, 11, 500, "minecraft:gtm.deagle");
    public static Gun ROCKET_LAUNCHER = new Gun(2, "Rocket Launcher", "Rocket", 1, 50, 30000, "minecraft:entity.firework_rocket.launch");

    public static void init() {
        guns.add(DESERT_EAGLE);
        guns.add(ROCKET_LAUNCHER);
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

        public Gun(int id, String name, String bulletName, float bulletSpeed, float damage, float price, String sound) {
            this.id = id;
            this.name = name;
            this.bulletName = bulletName;
            this.bulletSpeed = bulletSpeed;
            this.damage = damage;
            this.price = price;
            this.sound = sound;
        }
    }

}
