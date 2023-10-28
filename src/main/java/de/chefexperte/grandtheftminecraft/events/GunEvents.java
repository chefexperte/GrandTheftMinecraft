package de.chefexperte.grandtheftminecraft.events;

import de.chefexperte.grandtheftminecraft.GrandTheftMinecraft;
import de.chefexperte.grandtheftminecraft.Util;
import de.chefexperte.grandtheftminecraft.guns.Guns;
import de.chefexperte.grandtheftminecraft.guns.RecoilPatterns;
import io.papermc.paper.entity.TeleportFlag;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

import java.util.*;

import static de.chefexperte.grandtheftminecraft.Util.*;

public class GunEvents implements Listener {
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        if (e.getAction().isRightClick()) {
            if (isGun(e.getItem())) {
                World world = e.getPlayer().getWorld();
                Vector right = e.getPlayer().getLocation().getDirection().rotateAroundAxis(new Vector(0, 1, 0), 85).multiply(0.35);
                Vector front = e.getPlayer().getLocation().getDirection().multiply(0.52);
                Vector down = new Vector(0, -0.18, 0);
                Location gunLocation = e.getPlayer().getEyeLocation().add(right).add(front).add(down);
                Guns.Gun g = getGunFromItem(e.getItem());
                if (g == null) return;
                // code about ammo
                int ammo = Util.getAmmoFromGunItem(e.getItem());
                Util.updateGunDisplayName(e.getItem());
                if (ammo == 0) {
                    // play empty sound
                    world.playSound(gunLocation, "minecraft:gtm.empty_gun", 1, 1);
                    return;
                }
                if (ammo == -1) return;
                Util.setAmmoForGunItem(e.getItem(), ammo - 1);
                Util.updateGunDisplayName(e.getItem());

                // code for recoil
                // apply recoil by moving player view
                Location newLoc = e.getPlayer().getLocation().clone();
                RecoilPatterns.RecoilPattern recoilPattern = g.recoilPattern;
                if (recoilPattern != null) {
                    int step = Util.getGunRecoilPatternId(e.getItem());
                    if (step == -1) {
                        step = 0;
                    } else {
                        step++;
                        if (step >= recoilPattern.steps.size()) {
                            step = 0;
                        }
                    }
                    Util.setGunRecoilPatternId(e.getItem(), step);
                    newLoc.setPitch(newLoc.getPitch() + recoilPattern.steps.get(step).pitch());
                    newLoc.setYaw(newLoc.getYaw() + recoilPattern.steps.get(step).yaw());
                    // Set the player's new direction
                    //noinspection UnstableApiUsage
                    e.getPlayer().teleport(newLoc, PlayerTeleportEvent.TeleportCause.PLUGIN, TeleportFlag.Relative.X, TeleportFlag.Relative.Y, TeleportFlag.Relative.Z);
                }

                if (g == Guns.DESERT_EAGLE || g == Guns.AK47) {
                    // shoot using invisible arrow
                    Arrow a = shootBullet(e.getPlayer(), gunLocation, g);
                    // play gun effects
                    playNormalGunEffects(gunLocation, a, g);
                } else if (g == Guns.ROCKET_LAUNCHER) {
                    // shoot using invisible arrow
                    Arrow[] as = shootRocket(e.getPlayer(), gunLocation, g);
                    // play gun effects
                    playRocketLauncherEffects(gunLocation, as, g);
                } else {
                    GrandTheftMinecraft.instance.getLogger().warning("Unknown gun type: " + g.name);
                }
            }
        }
    }

    HashMap<Player, ItemStack> playerInv = new HashMap<>();

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent e) {
        if (isGun(e.getItemDrop().getItemStack())) {
            if (!playerInv.containsKey(e.getPlayer())) {
                e.setCancelled(true);
                ItemStack gun = e.getItemDrop().getItemStack();
                Guns.Gun g = getGunFromItem(gun);
                if (g == null) return;
                // player is reloading the gun
                int missingAmmo = g.magazineSize - Util.getAmmoFromGunItem(gun);
                // go through ammo and reload gun
                int reloadAmmo = 0;
                for (ItemStack item : e.getPlayer().getInventory().getContents()) {
                    if (item != null && item.getType() == Material.ARROW) {
                        if (isAmmo(item)) {
                            int reloadAmount = Math.min(missingAmmo, item.getAmount());
                            reloadAmmo += reloadAmount;
                            item.setAmount(item.getAmount() - reloadAmount);
                            missingAmmo -= reloadAmount;
                        }
                    }
                }
                // play reload sound
                e.getPlayer().getWorld().playSound(e.getPlayer().getLocation(), "minecraft:gtm.reload", 1, 1);
                // set ammo after waiting for reload time
                final int finalReloadAmmo = reloadAmmo;
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        for (ItemStack item : e.getPlayer().getInventory().getContents()) {
                            // if is gun
                            if (isGun(item)) {
                                if (Util.getRandomIdForGunItem(item) == Util.getRandomIdForGunItem(gun)){
                                    Util.setAmmoForGunItem(item, Util.getAmmoFromGunItem(item) + finalReloadAmmo);
                                    Util.updateGunDisplayName(item);
                                    //item.setItemMeta(gun.getItemMeta());
                                }
                            }
                        }
                    }
                }.runTaskLater(GrandTheftMinecraft.instance, (long) (g.reloadTime * 20));
            }
            playerInv.remove(e.getPlayer());
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getWhoClicked() instanceof Player p) {
            ItemStack current = e.getCurrentItem();
            ItemStack cursor = e.getCursor();
            if (current == null) {
                if (isGun(cursor)) {
                    playerInv.put(p, cursor);
                }
            } else if (current.getType() == Material.AIR) {
                playerInv.remove(p);
            }
        }
    }

    private void playNormalGunEffects(Location gunLocation, Arrow a, Guns.Gun g) {
        playNormalGunEffects(gunLocation, a, g, true);
    }

    private void playNormalGunEffects(Location gunLocation, Arrow a, Guns.Gun g, boolean nozzleFlash) {
        playNormalGunEffects(gunLocation, a, g, nozzleFlash, true);
    }


    private void playNormalGunEffects(Location gunLocation, Arrow a, Guns.Gun g, boolean nozzleFlash, boolean sound) {
        World world = gunLocation.getWorld();
        if (sound) {
            // play sound
            world.playSound(gunLocation, g.sound, 1, 1);
        }
        if (nozzleFlash) {
            // spawn nozzle flash particles
            world.spawnParticle(Particle.FLAME, gunLocation, 3, 0.1, 0.1, 0.1, 0.001);
        }
        // spawn smoke particles
        Location oldArrowLoc = a.getLocation();
        // smoke trail
        new BukkitRunnable() {
            int deadTimer = 0;

            @Override
            public void run() {
                if (!a.isValid() || a.isInBlock() || a.isOnGround()) {
                    deadTimer++;
                    if (deadTimer >= 1) {
                        cancel();
                    }
                }
                int tries = 0;
                while (oldArrowLoc.distance(a.getLocation()) > 0.51 && tries < 1000) {
                    tries++;
                    Vector dir = a.getLocation().clone().subtract(oldArrowLoc).toVector().normalize().multiply(0.5);
                    oldArrowLoc.add(dir);
                    world.spawnParticle(Particle.SMOKE_NORMAL, oldArrowLoc, 0, 0, 0, 0, 0, null, true);
                }
                if (tries >= 1000) {
                    // something went wrong, whatever
                    GrandTheftMinecraft.instance.getLogger().warning("Something went wrong while spawning smoke particles");
                }
            }
        }.runTaskTimer(GrandTheftMinecraft.instance, 0L, 1L);
    }

    private void playRocketLauncherEffects(Location gunLocation, Arrow[] as, Guns.Gun g) {
        World world = gunLocation.getWorld();
        // play sound
        world.playSound(gunLocation, g.sound, 1, 1);
        // spawn nuzzle flash particles
        world.spawnParticle(Particle.FLAME, gunLocation, 8, 0.2, 0.1, 0.2, 0.005);
        final ArrayList<BukkitTask> runnables = new ArrayList<>();
        for (Arrow a : as) {
            Location oldArrowLoc = a.getLocation();
            // smoke trail
            BukkitTask t = new BukkitRunnable() {
                @Override
                public void run() {
                    if (!a.isValid() || a.isInBlock() || a.isOnGround() || a.getVelocity().length() < 0.2) {
                        for (BukkitTask runnable : runnables) {
                            runnable.cancel();
                        }
                        for (Arrow a : as) {
                            a.remove();
                        }
                    } else {
                        a.setVelocity(a.getVelocity().normalize().multiply(g.bulletSpeed));
                        int tries = 0;
                        while (oldArrowLoc.distance(a.getLocation()) > 0.51 && tries < 1000) {
                            tries++;
                            Vector dir = a.getLocation().clone().subtract(oldArrowLoc).toVector().normalize().multiply(0.5);
                            oldArrowLoc.add(dir);
                            if (GrandTheftMinecraft.random.nextInt(6) == 0)
                                world.spawnParticle(Particle.FLAME, oldArrowLoc, 1, 0.01, 0.01, 0.01, 0, null, true);
                            world.spawnParticle(Particle.SMOKE_LARGE, oldArrowLoc, 2, 0.1, 0.1, 0.1, 0.05, null, true);
                        }
                        if (tries >= 1000) {
                            // something went wrong, whatever
                            GrandTheftMinecraft.instance.getLogger().warning("Something went wrong while spawning smoke particles");
                        }
                    }
                }
            }.runTaskTimer(GrandTheftMinecraft.instance, 0L, 1L);
            runnables.add(t);
        }

    }

    private Arrow shootBullet(Player p, Location gunLocation, Guns.Gun gun) {
        Location target = getExactHitPoint(p, 200);
        float gunSpeedMultiplier = gun.bulletSpeed;
        if (target != null) {
            return shootBullet(p, gunLocation, gun, target.subtract(gunLocation).toVector().normalize().multiply(gunSpeedMultiplier));
        } else {
            return shootBullet(p, gunLocation, gun, p.getLocation().add(p.getLocation().getDirection().multiply(100)).subtract(gunLocation).toVector().normalize().multiply(gunSpeedMultiplier));
        }
    }

    private Arrow shootBullet(Player p, Location gunLocation, Guns.Gun gun, Vector customVelocity) {
        Arrow a = gunLocation.getWorld().spawn(gunLocation, Arrow.class);
        a.setSilent(true);
        a.setShooter(p);
        a.setPickupStatus(Arrow.PickupStatus.DISALLOWED);
        a.setDamage(0);
        a.customName(Component.text(gun.bulletName));
        a.addCustomEffect(new PotionEffect(PotionEffectType.INVISIBILITY, PotionEffect.INFINITE_DURATION, 1, false, false), true);
        //noinspection UnstableApiUsage
        a.setVisibleByDefault(false);
        a.setVelocity(customVelocity);
        return a;
    }

    private Arrow[] shootRocket(Player p, Location gunLocation, Guns.Gun gun) {
        Location target = getExactHitPoint(p, 200);
        float gunSpeedMultiplier = gun.bulletSpeed;
        Arrow[] as = new Arrow[3];
        for (int i = 0; i < 3; i++) {
            Vector offset = switch (i) {
                case 0 -> new Vector(0, 0.5, 0);
                case 1 -> p.getLocation().getDirection().rotateAroundAxis(new Vector(0, 1, 0), 80).multiply(0.35);
                case 2 -> p.getLocation().getDirection().rotateAroundAxis(new Vector(0, 1, 0), -80).multiply(0.35);
                default -> null;
            };
            Arrow a = gunLocation.getWorld().spawn(gunLocation.clone().add(offset), Arrow.class);
            a.setSilent(true);
            a.setShooter(p);
            a.setGravity(false);
            a.setPickupStatus(Arrow.PickupStatus.DISALLOWED);
            a.setDamage(0);
            a.customName(Component.text(gun.bulletName));
            a.addCustomEffect(new PotionEffect(PotionEffectType.INVISIBILITY, PotionEffect.INFINITE_DURATION, 1, false, false), true);
            //noinspection UnstableApiUsage
            a.setVisibleByDefault(false);
            if (target != null) {
                a.setVelocity(target.clone().subtract(gunLocation).toVector().normalize().multiply(gunSpeedMultiplier));
            } else {
                a.setVelocity(p.getLocation().add(p.getLocation().getDirection().multiply(100)).subtract(gunLocation).toVector().normalize().multiply(gunSpeedMultiplier));
            }
            as[i] = a;
        }
        return as;
    }

    // Arrow hit event
    @EventHandler
    public void arrowHitEvent(ProjectileHitEvent e) {
        ProjectileSource shooter = e.getEntity().getShooter();
        if (e.getEntity().customName() == null) return;
        String bulletName = ((TextComponent) Objects.requireNonNull(e.getEntity().customName())).content();
        Guns.Gun gun = Guns.getGunFromBulletName(bulletName);
        if (gun == null) return;
        if (shooter instanceof Player) {
            Block hitBlock = e.getHitBlock();
            if (hitBlock != null) {
                Material hitMat = hitBlock.getType();
                // spawn block break particles
                spawnBulletHitParticles(e.getEntity(), hitMat, e.getHitBlockFace());
                e.getEntity().remove();
                if (isGlass(hitMat) && gun != Guns.ROCKET_LAUNCHER) {
                    hitBlock.breakNaturally();
                    // let arrow fly through glass
                    //e.setCancelled(true);
                    Vector vel = e.getEntity().getVelocity().multiply(0.8);
                    Arrow newArrow = shootBullet((Player) shooter, e.getEntity().getLocation(), gun, vel);
                    playNormalGunEffects(e.getEntity().getLocation(), newArrow, gun, false, false);
                }
            } else {
                Entity hitEntity = e.getHitEntity();
                if (hitEntity != null && e.getEntity().customName() != null) {
                    // Spawn blood particles
                    Location hitLoc = hitEntity.getLocation().add(0, 1, 0);
                    Vector bloodDir = e.getEntity().getLocation().subtract(hitLoc).toVector().normalize();
                    hitLoc.getWorld().spawnParticle(Particle.BLOCK_CRACK, hitLoc.add(bloodDir.multiply(0.5)), 15, bloodDir.getX(), bloodDir.getY(), bloodDir.getZ(), 0.001, Material.REDSTONE_BLOCK.createBlockData());
                    new BukkitRunnable() {
                        int times = 0;

                        @Override
                        public void run() {
                            times++;
                            if (times >= 6 || !hitEntity.isValid() || hitEntity.isDead()) {
                                cancel();
                            }
                            hitLoc.getWorld().spawnParticle(Particle.BLOCK_CRACK, hitEntity.getLocation().add(0, 1, 0).add(bloodDir.multiply(0.1)), 3, 0.15, 0.15, 0.15, 0.001, Material.REDSTONE_BLOCK.createBlockData());
                        }
                    }.runTaskTimer(GrandTheftMinecraft.instance, 0L, 17L);

                    // calculate damage
                    double projVel = e.getEntity().getVelocity().length();
                    double weaponDamage = gun.damage * (projVel / gun.bulletSpeed);
                    // apply damage
                    if ((hitEntity instanceof LivingEntity)) {
                        ((LivingEntity) hitEntity).damage(weaponDamage);
                        ((LivingEntity) hitEntity).setNoDamageTicks(0);
                    }
                    e.setCancelled(true);
                    // knockback
                    Vector damageVel = e.getEntity().getVelocity().normalize().multiply(0.05 * weaponDamage);
                    hitEntity.setVelocity(hitEntity.getVelocity().multiply(0.7).add(damageVel));
                    e.getEntity().remove();
                }
            }

            if (gun == Guns.ROCKET_LAUNCHER) {
                doExplosion(e.getEntity().getLocation(), 8, 100f);
            }
        }

    }

    private void spawnBulletHitParticles(Entity e, Material hitMat, BlockFace hitFace) {
        new BukkitRunnable() {
            @Override
            public void run() {
                Location shotLoc = e.getLocation();
                Vector offset = new Vector(0, 0, 0);
                if (hitFace != null) {
                    offset = hitFace.getDirection().multiply(0.75);
                }
                shotLoc.getWorld().spawnParticle(Particle.BLOCK_CRACK, shotLoc.add(offset.multiply(0.5)), 10, offset.getX(), offset.getY(), offset.getZ(), 1, hitMat.createBlockData());
            }
        }.runTaskLater(GrandTheftMinecraft.instance, 1L);
    }

    private void doExplosion(Location l, int size, float power) {
        // damage entities and throw them around
        for (Entity e : l.getWorld().getEntities()) {
            if (e.getLocation().distance(l) <= size / 2f) {
                if (e instanceof LivingEntity) {
                    ((LivingEntity) e).damage(35);
                }
                e.setVelocity(e.getVelocity().add(new Vector(GrandTheftMinecraft.random.nextDouble(-0.3, 0.3), GrandTheftMinecraft.random.nextDouble(0.5, 1.5), GrandTheftMinecraft.random.nextDouble(-0.3, 0.3))));
                if (e instanceof Item) {
                    e.remove();
                }
            }
        }
        if (size % 2 != 0) size++;
        Location start = l.clone().subtract(size / 2f, size / 2f, size / 2f);
        Location end = l.clone().add(size / 2f, size / 2f, size / 2f);

        PriorityQueue<Util.PriorityItem<Block>> priorityQueue = new PriorityQueue<>(
                Comparator.comparingDouble(Util.PriorityItem::priority)
        );
        // add all blocks in explosion radius to priority queue sorted by distance
        for (int x = (int) start.getX(); x < end.getX(); x++) {
            for (int y = (int) start.getY(); y < end.getY(); y++) {
                for (int z = (int) start.getZ(); z < end.getZ(); z++) {
                    Location loc = new Location(l.getWorld(), x, y, z);
                    if (loc.distance(l) <= size / 2f) {
                        Block b = loc.getBlock();
                        if (b.getType() != Material.AIR) {
                            priorityQueue.add(new Util.PriorityItem<>(loc.distance(l), b));
                        }
                    }
                }
            }
        }

        while (!priorityQueue.isEmpty() && power > 0) {
            Util.PriorityItem<Block> priorityItem = priorityQueue.poll();
            Block b = priorityItem.item();
            if (b.getType() == Material.BEDROCK) continue;
            float h = b.getType().getHardness();
            power -= h;
            Vector vel = new Vector(GrandTheftMinecraft.random.nextDouble(-0.3, 0.3),
                    GrandTheftMinecraft.random.nextDouble(1.5F), GrandTheftMinecraft.random.nextDouble(-0.3, 0.3));
            // Change flying block velocity depending on hardness
            if (h > 4) h = 4;
            if (h < 1) {
                vel.multiply(1.5);
            } else if (h < 2) {
                vel.multiply(1.25);
            } else if (h >= 3) {
                vel.multiply(0.75);
            }
            // break a few blocks normally, the rest will be thrown around
            if (GrandTheftMinecraft.random.nextInt(7) == 1) {
                b.breakNaturally();
            } else {
                l.getWorld().spawn(b.getLocation().add(0.5, 0.5, 0.5), FallingBlock.class, fb -> {
                    fb.setDropItem(true);
                    fb.setGravity(true);
                    fb.setHurtEntities(true);
                    fb.setVelocity(vel);
                    fb.setBlockData(b.getBlockData());
                    fb.setTicksLived(1);
                });
                b.setType(Material.AIR, false);
            }
            l.getWorld().spawnParticle(Particle.BLOCK_CRACK, b.getLocation().add(0.5, 0.5, 0.5), 10, 0.25, 0.25, 0.25, 0.001, b.getBlockData());
            if (GrandTheftMinecraft.random.nextInt(10) == 1) {
                // spawn explosion particle
                l.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, b.getLocation().add(0.5, 0.5, 0.5), 1, 0, 0, 0, 0.001);
            }
        }
        // create eight fire blocks to throw around
        for (int i = 0; i < 8; i++) {
            Vector vel = new Vector(GrandTheftMinecraft.random.nextDouble(-0.3, 0.3),
                    GrandTheftMinecraft.random.nextDouble(1.5F), GrandTheftMinecraft.random.nextDouble(-0.3, 0.3));
            l.getWorld().spawn(l.add(0.5, 0.5, 0.5), FallingBlock.class, fb -> {
                fb.setDropItem(true);
                fb.setGravity(true);
                fb.setHurtEntities(true);
                fb.setVelocity(vel);
                fb.setBlockData(Material.FIRE.createBlockData());
                fb.setTicksLived(1);
            });
        }
        // play explosion sound
        l.getWorld().playSound(l, "minecraft:entity.generic.explode", 4, 0.9f);

    }

    private boolean isGlass(Material mat) {
        return mat.toString().contains("GLASS");
    }

    public Block getTargetBlock(Player player, int maxDistance) {
        World world = player.getWorld();
        Location playerEyeLocation = player.getEyeLocation();
        Vector direction = playerEyeLocation.getDirection();

        BlockIterator iterator = new BlockIterator(world, playerEyeLocation.toVector(), direction, 0, maxDistance);
        Block targetBlock = null;

        while (iterator.hasNext()) {
            targetBlock = iterator.next();

            if (targetBlock.getType().isSolid()) {
                break;
            }
        }

        return targetBlock;
    }

    public Location getExactHitPoint(Player player, int maxDistance) {
        World world = player.getWorld();
        Location playerEyeLocation = player.getEyeLocation();
        Vector direction = playerEyeLocation.getDirection();

        BlockIterator iterator = new BlockIterator(world, playerEyeLocation.toVector(), direction, 0, maxDistance);
        Location hitLocation = null;

        while (iterator.hasNext()) {
            Block currentBlock = iterator.next();
            if (currentBlock.getType().isSolid()) {
                hitLocation = currentBlock.getLocation().add(0.5, 0.5, 0.5);
                break;
            }
        }

        if (hitLocation != null) {
            double dist = hitLocation.distance(playerEyeLocation);
            hitLocation = playerEyeLocation.add(direction.multiply(dist));
        }

        return hitLocation;
    }


}
