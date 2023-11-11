package de.chefexperte.grandtheftminecraft;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.*;
import de.chefexperte.grandtheftminecraft.commands.*;
import de.chefexperte.grandtheftminecraft.events.CivilianEvents;
import de.chefexperte.grandtheftminecraft.events.GunEvents;
import de.chefexperte.grandtheftminecraft.events.PoliceOfficerEvents;
import de.chefexperte.grandtheftminecraft.events.WorldEvents;
import de.chefexperte.grandtheftminecraft.guns.Guns;
import de.chefexperte.grandtheftminecraft.util.CivilianUtil;
import de.chefexperte.grandtheftminecraft.util.PoliceUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

public final class GrandTheftMinecraft extends JavaPlugin {

    public static GrandTheftMinecraft instance;
    public static ProtocolManager protocolManager;

    public static Random random = new Random();

    public record PlayerTexture(String value, String signature) {
    }

    public static HashMap<String, PlayerTexture> playerTextures = new HashMap<>() {
        {
            put("Police Officer", new PlayerTexture(
                    "eyJ0aW1lc3RhbXAiOjE1ODU3Mzk2MjY2NzksInByb2ZpbGVJZCI6Ijc3MjdkMzU2NjlmOTQxNTE4MDIzZDYyYzY4MTc1OTE4IiwicHJvZmlsZU5hbWUiOiJsaWJyYXJ5ZnJlYWsiLCJzaWduYXR1cmVSZXF1aXJlZCI6dHJ1ZSwidGV4dHVyZXMiOnsiU0tJTiI6eyJ1cmwiOiJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2JiYTE4ZDBiNDZmODEyYmZmZWFiY2YwMjQ0NzgzZTVkZjJlOTY2ZTAzZGFmN2M4OTVjMDkxYjU5Yzg0NWVmYzcifX19",
                    "YxtmbQZJFqAC1a+tml8zCd1se4qE6ivn/X7JQAw8H0QB4YukRPy+q3Y74vaT2F3ki6vjypDgeO8sYYlY3wku46Zjz2JxU/c8jPGLyJ6WDVbSMqdcx6MhT/rYwiHRDWtw3loojq9V479dfBlUoGpqMzNzkv6+zu6YjjFEDgjQ3S13quv5y3apxfIsi8/mtdReb7WG7WXTCRAUzzuv/BSbOkEKtul5UiVLYzFig9OtdksPsaRfqHw0KkoDj86+CiTZntVF52MpnVQm8CHPZExGX0NJrPW08ZWFmkLEtWTIVPnogkW30800tytE64z3OsPZRN5bhEkIO2AsUmUKHbcow8h90a3g+/QFo343+tArfG6wCs2d1FDjKP1lGT1GaPzIzkAJkpS3tdHkRyXEvc2nl01vVZrHkRNSVo7I2L+6CKBPrGK4e4yj3u1Ohsl7O70oRhYZ+pgP65s5C+uueAQuPRv9OzOqE3lZi2TSc9MvY3+JvGCMW3bB6SjrVcDGhd/tsRvU7CL0W4KgDLqeHOpm9sQxycD9DAR1SGgif0f0SCD7nfTAM1R7eIaudoaMcebWKPd8/MF08DFrs7Cuw4uppmRetcRMwua4qh8Y44RgYZl5s3uw56GTvL8YlHtdwwiTq5fDXuIzY7h0IF/z7K3O+YS+HSPP2S/1oEC+jB78rQ0="));
            put("Franklin", new PlayerTexture(
                    "eyJ0aW1lc3RhbXAiOjE1ODU5MzUxNTg1MzEsInByb2ZpbGVJZCI6IjIzZjFhNTlmNDY5YjQzZGRiZGI1MzdiZmVjMTA0NzFmIiwicHJvZmlsZU5hbWUiOiIyODA3Iiwic2lnbmF0dXJlUmVxdWlyZWQiOnRydWUsInRleHR1cmVzIjp7IlNLSU4iOnsidXJsIjoiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS8yOWE3NGY3YzZiZTA1Y2JmZTBmOTJlZGY1OWFkNzRkM2RiZGY3OTM3ZGMzNzI1ODU0OWQwZTU1NTc5NGMxZDNkIn19fQ==",
                    "iFkjQdkMIQHYY1nmP05djIff/CsnyY/jXieqzY58taJ0FaQitnctH7weDLUUjQ9AE/xjqvvyzm3qdkZt1XVsDS5vU/qlQ1W3iix4zsTtyM+bZIFnhI8AV9XjaAfDs3NzXfGKdU/3E7Ea9wED1OmlhNjZWGfTZnwgXCLhhQUqKo3lKd1EyADsZUEduxom8NhN2IQjpfY5MWaFlPdQHkWtOHYdCleL/i7vm1MKQnK48pGyN6J2okh79V1xEa9HDite8gl/qMuJQ9I5bmQ8vPPKRqCDWlS060LnEn9zSQ3qk1dZOGdx6RAyO4prj2AQfbaUc6M9jr/6rgHHskhwZrygG8Kh4czzhxKGthXTZ9rpGOI6keAyMw5JsDgkUGrEYwu3YJ4VR76rhfRKgG6XvS3M3uoPjA2xsFfAMo7solflInkOWhRyjL4NfXqzBynj23qfV+jd9PBtoa9Ltowm4FcgQWHWUwB0o2doLa7t6MfuIoj/Qnu2ocfr8Vwl30fw5vJ/BQr+SLEcIUcT2/AzvnHQP/Y4GLixtj4WM32xByy9WrYoJmhJl7p3XVS4XB1XzcurFAFFZygRWWLZYaSp8F/MpCIVr5Q+AfK0P7L9yyk52YEHtLbQrtej6asXmS10ds0A6AzX6lgYEbFLXsDJKDJR0Kt684kC4uuDGDB16h0s1/w="));
            put("Michael", new PlayerTexture(
                    "ewogICJ0aW1lc3RhbXAiIDogMTU5MTM5MDE2MDAyMywKICAicHJvZmlsZUlkIiA6ICIwNGI3MDhhMzM1NjY0ZjJmODVlYzVlZWYyN2QxNGRhZCIsCiAgInByb2ZpbGVOYW1lIiA6ICJWaW9sZXRza3l6eiIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS8yYzc2ZTc5ZDEwNWM2ODY1OTI1OTJmMmZhOGUzYzZhM2ZjMzhjYTI5ZjhhMWFjYjk0ZGY2YWI2ZGQ3YmJlYTUiCiAgICB9CiAgfQp9",
                    "Zm/WHgMkNZ4mW1SiSvSj8kM9QONdQ+Jg9v7rhFpLubVV2XZ5ArZuzAmQ+1aiSUyhcRgn9xgcZLNkJahsejo1BC+erdI3QB74C05iQCOaIA0e4ZF97S0nfA1wKs9dXMTojpxFYapn917wN0cUoZeGeX1ZtEgHrQPNUBCBc43ex5PyGTC9x85ZxFWLW18sohIGjp4r1gyvfq482fVASLm3xRJ3l4HtEZSKEM8QVbi+YmoTE7cSMnaiB3RGreOX9rqJKLtEyhVSaBRzHveJQTzq2EnHlgIY5XJXlRUuvxEPLhzj75f4Ct6F6iRzW6DPn0Lz5OCqzeouhozW2B/WWh/i8wP9ajw6+k+1odREE/pGrm7T5dOur0RyYCA2KoEdr1ZfwscdXmwWFsuj857cMtULgFmZnTci72oazZfO1u8EedUB/JjyOEmhFhmZCR67SrK1/ST/JaeSXzjCzxr99RKZEhlWnxBLVUs3tBbp+N+uAfBBU1lZhDHu1pL0uGAhrdUCt3TKOBQwhbAOUNrcHvZRBcdNuyQdp27RqXbGGd79GzaEj+I4I3KTRsxLPRNO2xuqKoS3Do971utoYQzm+XEnaW1NvYe4/SiE+qWhdtQE4urgSCGqD9SuQBSylJu4kRo5rwWLsnHkWM5tGjZHrsoZlO/sC1zK2DPbEuidZBEPk6U="));
            put("Trevor", new PlayerTexture(
                    "ewogICJ0aW1lc3RhbXAiIDogMTYxODgzNDk4NjcyMiwKICAicHJvZmlsZUlkIiA6ICJkZTU3MWExMDJjYjg0ODgwOGZlN2M5ZjQ0OTZlY2RhZCIsCiAgInByb2ZpbGVOYW1lIiA6ICJNSEZfTWluZXNraW4iLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOWUyMDZjODJkOTAyMThjOGVhY2U1NzU5ZmZmOTdjNDcyZGIwYzc0Y2ZiZDE3ODEwYjQxYjQxYTI0ZTAyZmQxOCIKICAgIH0KICB9Cn0=",
                    "ofq3SaJOnAyZwifbdvHQ5gHVwLBeOM5F71+VRIYyf6efW3cFBWabkV0PF5SN+wJ7PitMzq0bEhRxrH9eRx3BRa8of8BY4IFZkjuZLKSzkIAs5Mbkd+ZVsRFFPMLGyeS08ZKxJrxQaed9girWc00Ir4rGKWyojJ/MuJ5+pnNxp7gkSXU1803w60gzNfA99hJW359qkAzpxzNcR65a2/uAeiGAHxqJZIUEgfrC9hleoQ/m7H1HCTSjioVp8TkeovGwrJCQaeWF5419tHWms1FxSh/GksSnmgWZB2Izq6DxT/7uq6dZDWknwSg403I/fRBbRrQZM7ltLMjbqi6YWb6m1Zlj3MrMYIxkMEGq7BidTPIDnFSqVRZMRGXFQal6I/jQc8wylbf//HxB6Wr491YcUi6qbWCDLKhcSZ64WsJ5XoeraJk5C4mWFZPJY7aA8BsPTkG1oDmNQMJwnsvndaLK2PyRQogmaDy4FA/nWQWDgHUMBsJynGB1/x4ZoOMbuvPtpGgfAcJkI/VyXii+yN3x3IQNS2IHPhsQBLC0CTAouGR/XWVl94lXLrArF9Zoljp5V+OKjs6EhlFCeU/KBYQGap/Is783gXWVJ2eYxr1C7PBKEuewb2uCs1ecwtepfbRb+4K71RtU+CQguU9dimhjvAjjTv/DR6hjTanHw4jFGZc="));
            put("Suit Villager", new PlayerTexture(
                    "ewogICJ0aW1lc3RhbXAiIDogMTY0MjIwNTI3MDE0MywKICAicHJvZmlsZUlkIiA6ICJkMWY2OTc0YzE2ZmI0ZjdhYjI1NjU4NzExNjM3M2U2NSIsCiAgInByb2ZpbGVOYW1lIiA6ICJGaW9saWVzdGEiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzQyMTIxMzQ2MDUzODQ2YjUwNmVhYzBlNWYwZDA2YjBiY2YyYmVhYjRkN2Q5NmY2ZDVhZjFhNGFlZmMzYTg4NyIsCiAgICAgICJtZXRhZGF0YSIgOiB7CiAgICAgICAgIm1vZGVsIiA6ICJzbGltIgogICAgICB9CiAgICB9CiAgfQp9",
                    "JjXj+WhqLqhd/Z4/qgFopmIseg1wF3TxNrG+tcZR73ibBa9Jes7EFKPfLkLiUhDhJBppgkNop9wlwT1cTH6QvCPYPvdpaQc1XCDDurfbIAmY1SC9j2/FVy7ADhVh5mXJ3QVhAHi0qzfQCApSAPBRR5L+nkOglPvAgtwd8mKg+KvPwzoPoOK/B/EnBXSj/2QQaRBDUd8o5imJJXH/ZFtY8kyecq6xnPfLRW4mwen8tzqSrvqmI+mJBpP32Q+6CZe8cOwK5Q6+VlfF2nvPcXHmhAbK3s7zhqaieX2mYLIXbBshK9gnm7LxanjWWLhGWfU8LElJdUmBPjSEZjsXqcy4X1gWHZQvKikQllJxDfkR1+UpuWYsrd72hZFrd9RFZzJfo0B9BlYu7T0RPlrDKd2eYchGCYNTK5SgCDdjkFpgZ9jCQAgU+JFMy/swYa6FJM3n2XGMNkT7hk3JMjSBreML7OxUOTyxsIlI+eXw1ISizB4/yGMX2OOL6ddE54uSDaYNnuZJxTiWjpKVe4afV+9nF5adj92hsKQrTJu011eYI5BEv0TdZmXdn1yYPAmdDROb7i4Rke/HLagCI8Qp9+E0i71C8aij7p6T9Zl4WDGP5pqA0Te1WAF2j8mdTp3ZkdbBWbOc5WSbWLe99uBBlqkB3vTvEfAE+igCDMlCudRbVTs="));

        }
    };

    public Scoreboard scoreboard;

    public record PlayerPosRot(double x, double y, double z, float yaw, float pitch) {
    }

    public static final HashMap<Player, PlayerPosRot> playerPosRots = new HashMap<>();


    public static void sendDebugMessage(String msg) {
        for (Player p : GrandTheftMinecraft.instance.getServer().getOnlinePlayers()) {
            p.sendMessage(Component.text(msg));
        }
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        scoreboard = manager.getMainScoreboard();
        // set nametag visibility
        setupTeam();
        if (!isProtocolLibLoaded()) {
            getLogger().warning("ProtocolLib is not installed! This plugin will not work without it!");
            return;
        }
        Guns.init();
        getLogger().info("Welcome to Grand Theft Minecraft!");
        this.getServer().getCommandMap().register("gtm", new GetGunCommand());
        this.getServer().getCommandMap().register("gtm", new GetAmmoCommand());
        this.getServer().getCommandMap().register("gtm", new SpawnPoliceCommand());
        this.getServer().getCommandMap().register("gtm", new SpawnCivilianCommand());
        this.getServer().getCommandMap().register("gtm", new TestCommand());
        this.getServer().getPluginManager().registerEvents(new GunEvents(), this);
        this.getServer().getPluginManager().registerEvents(new PoliceOfficerEvents(), this);
        this.getServer().getPluginManager().registerEvents(new CivilianEvents(), this);
        this.getServer().getPluginManager().registerEvents(new WorldEvents(), this);

        enableProtocolListener();
    }

    private void setupTeam() {
        // check if team exists
        Team t = scoreboard.getTeam("nhide");
        if (t == null) {
            t = scoreboard.registerNewTeam("nhide");
        }
        t.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
    }

    public void onLoad() {
        protocolManager = ProtocolLibrary.getProtocolManager();
    }

    public static boolean isProtocolLibLoaded() {
        return Bukkit.getPluginManager().isPluginEnabled("ProtocolLib");
    }

    public void enableProtocolListener() {
        protocolManager.addPacketListener(new PacketAdapter(instance, PacketType.Play.Server.SPAWN_ENTITY) {
            @Override
            public void onPacketSending(PacketEvent event) {
                PacketContainer packet = event.getPacket();
                var ent = packet.getEntityModifier(event).read(0);
                // check if persistent data container has "gtm.police" key
                if (ent == null) {
                    return;
                }
                PersistentDataContainer c = ent.getPersistentDataContainer();
                if (!c.has(new NamespacedKey("gtm", "fake"), PersistentDataType.BYTE)) {
                    return;
                }
                // spawn a player instead of police officer zombie
                UUID uuid = UUID.randomUUID();
                packet.getEntityTypeModifier().write(0, EntityType.PLAYER);
                packet.getUUIDs().write(0, uuid);
                if (c.has(new NamespacedKey("gtm", "police"), PersistentDataType.BYTE)) {
                    PoliceUtil.entitySpawnListener(event.getPlayer(), uuid);
                    PoliceUtil.policeOfficers.put(ent.getEntityId(), new PoliceUtil.PoliceOfficer((Zombie) ent, uuid));
                }
                if (c.has(new NamespacedKey("gtm", "civilian"), PersistentDataType.BYTE)) {
                    CivilianUtil.entitySpawnListener(event.getPlayer(), uuid);
                }

            }
        });

        // Clientbound Packet info:

        // PacketType.Play.Server.ENTITY_LOOK
        // called when updating pitch!
        // this also updates the body rotation

        // PacketType.Play.Server.ENTITY_HEAD_ROTATION
        // called when updating yaw!
        // does not affect body rotation

        // PacketType.Play.Server.REL_ENTITY_MOVE_LOOK
        // relative, player can also sink into the ground and stuff
        // does also affect body rotation, but only head pitch
        // will only be called when rotating head WHILE moving

        // PacketType.Play.Server.REL_ENTITY_MOVE
        // relative, player can also sink into the ground and stuff
        // does also affect body rotation, but not head rotation
        // will only be called when moving while NOT rotating head

        // PacketType.Play.Server.ENTITY_TELEPORT
        // gets called on teleport, or when jumping or landing
        // this also updates the body rotation

        protocolManager.addPacketListener(new PacketAdapter(instance, PacketType.Play.Server.PLAYER_INFO) {
            @Override
            public void onPacketSending(PacketEvent event) {
                //PacketContainer packet = event.getPacket();
                //WrappedGameProfile profile = packet.getPlayerInfoDataLists().read(1).get(0).getProfile();
                //WrappedChatComponent displayName = packet.getPlayerInfoDataLists().read(1).get(0).getDisplayName();
                //var ent = packet.getEntityModifier(event).read(0);
                preparePlayerInfoPacket(event);
            }
        });
        protocolManager.addPacketListener(new PacketAdapter(instance, PacketType.Play.Client.LOOK) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                PacketContainer packet = event.getPacket();
                handleRotate(event, packet);
            }
        });
        protocolManager.addPacketListener(new PacketAdapter(instance, PacketType.Play.Client.POSITION_LOOK) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                PacketContainer packet = event.getPacket();
                handleRotate(event, packet);
            }
        });
        protocolManager.addPacketListener(new PacketAdapter(instance, PacketType.Play.Client.POSITION) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                PacketContainer packet = event.getPacket();
                handleRotate(event, packet);
            }
        });
    }

    private void handleRotate(PacketEvent event, PacketContainer packet) {
        boolean hasLook = false;
        boolean hasPos = false;
        double x = 0;
        double y = 0;
        double z = 0;
        float yaw = 0;
        float pitch = 0;
        if (packet.getDoubles().size() != 0 && packet.getDoubles().getValues().stream().mapToDouble(Double::doubleValue).sum() != 0) {
            hasPos = true;
            x = packet.getDoubles().read(0);
            y = packet.getDoubles().read(1);
            z = packet.getDoubles().read(2);
        }
        if (packet.getFloat().size() != 0 && packet.getFloat().getValues().stream().mapToDouble(Float::doubleValue).sum() != 0) {
            hasLook = true;
            yaw = packet.getFloat().read(0);
            //yaw = (yaw + 180) % 360 - 180;
            pitch = packet.getFloat().read(1);
        }
        playerPosRots.putIfAbsent(event.getPlayer(), new PlayerPosRot(x, y, z, yaw, pitch));
        PlayerPosRot oldPpr = playerPosRots.get(event.getPlayer());
        yaw = hasLook ? packet.getFloat().read(0) : oldPpr.yaw;
        pitch = hasLook ? packet.getFloat().read(1) : oldPpr.pitch;
        if (hasPos) {
            Vector moveDir = new Vector(x - oldPpr.x, y - oldPpr.y, z - oldPpr.z);
            float oldYaw = oldPpr.yaw;
            // convert direction to yaw
            float moveYaw = (float) -Math.toDegrees(Math.atan2(moveDir.getX(), moveDir.getZ()));
            float lookYaw = event.getPlayer().getLocation().getYaw();
            float lookDiff = moveYaw - lookYaw;
            // set moveYaw to have a maximum difference of 45 to lookYaw
            if (lookDiff > 45) {
                moveYaw = lookYaw - 45 + lookDiff;
            } else if (lookDiff < -45) {
                moveYaw = lookYaw + 45 + lookDiff;
            }
            float diff = moveYaw - oldYaw;

            // multiply moveYaw by so many times that its difference to oldYaw is less than 360
            while (diff < -180) {
                moveYaw += 360;
                diff = moveYaw - oldYaw;
            }
            while (diff > 180) {
                moveYaw -= 360;
                diff = moveYaw - oldYaw;
            }

            if (moveDir.length() >= 0.15) {
                if (diff > 5) {
                    float newYaw = (oldYaw - 5 + diff);
                    yaw = newYaw;
                    oldPpr = new PlayerPosRot(x, y, z, newYaw, pitch);
                    playerPosRots.put(event.getPlayer(), oldPpr);
                } else if (diff < -5) {
                    float newYaw = (oldYaw + 5 + diff);
                    yaw = newYaw;
                    oldPpr = new PlayerPosRot(x, y, z, newYaw, pitch);
                    playerPosRots.put(event.getPlayer(), oldPpr);
                }
            }
        }
        if (true) {
            float oldYaw = oldPpr.yaw;
            float diff = yaw - oldYaw;
            if (diff > 45) {
                float newYaw = (oldYaw - 45 + diff);
                //newYaw = (newYaw + 180) % 360 - 180;
                oldPpr = new PlayerPosRot(oldPpr.x, oldPpr.y, oldPpr.z, newYaw, pitch);
                playerPosRots.put(event.getPlayer(), oldPpr);
            } else if (diff < -45) {
                float newYaw = (oldYaw + 45 + diff);
                //newYaw = (newYaw + 180) % 360 - 180;
                oldPpr = new PlayerPosRot(oldPpr.x, oldPpr.y, oldPpr.z, newYaw, pitch);
                playerPosRots.put(event.getPlayer(), oldPpr);
            }
        }
    }

    private void sendLook(PacketEvent event, PacketContainer packet, float yaw, float pitch) {
        new BukkitRunnable() {
            float yaw2 = yaw;
            float pitch2 = pitch;

            @Override
            public void run() {
                yaw2 += 90;
                pitch2 += 0;
                PacketContainer pack2 = protocolManager.createPacket(PacketType.Play.Server.ENTITY_HEAD_ROTATION);
                pack2.getIntegers().write(0, packet.getIntegers().read(0));
                pack2.getBytes().write(0, (byte) (yaw2 * 256.0 / 360.0));
                protocolManager.sendServerPacket(event.getPlayer(), pack2);
                PacketContainer pack = protocolManager.createPacket(PacketType.Play.Server.ENTITY_LOOK);
                pack.getIntegers().write(0, packet.getIntegers().read(0));
                pack.getBytes().write(0, (byte) (yaw2 * 256.0 / 360.0));
                pack.getBytes().write(1, (byte) (pitch2 * 256.0 / 360.0));
                protocolManager.sendServerPacket(event.getPlayer(), pack);
            }
        };//.runTaskLater(this, 1);
    }

    private void preparePlayerInfoPacket(PacketEvent event) {
        PacketContainer packet = event.getPacket();
        WrappedGameProfile profile = packet.getPlayerInfoDataLists().read(1).get(0).getProfile();
        //WrappedChatComponent displayName = packet.getPlayerInfoDataLists().read(1).get(0).getDisplayName();
        var ent = packet.getPlayerInfoDataLists().read(1).get(0);
        // get the player entity that the info is about
        Entity entity = event.getPlayer().getWorld().getEntity(ent.getProfile().getUUID());
        if (entity == null) {
            return;
        }
        //Util.hideNickname(entity);
        Player player = entity instanceof Player ? (Player) entity : null;
        if (player == null) {
            return;
        }
        // check if entity is a police officer
        PersistentDataContainer c = entity.getPersistentDataContainer();
        boolean isPolice = c.has(new NamespacedKey("gtm", "police"), PersistentDataType.BYTE);
        boolean isFake = c.has(new NamespacedKey("gtm", "fake"), PersistentDataType.BYTE);
        if (isFake) {
            if (isPolice) {
                setProfileTexture(profile, playerTextures.get("Police Officer"));
            }
        } else {
            String randomCharachter = new String[]{"Franklin", "Michael", "Trevor"}[random.nextInt(3)];
            setProfileTexture(profile, playerTextures.get(randomCharachter));
        }
    }

    public static void setProfileTexture(WrappedGameProfile profile, PlayerTexture pTexture) {
        WrappedSignedProperty texture = new WrappedSignedProperty("textures", pTexture.value, pTexture.signature);
        profile.getProperties().removeAll("textures");
        profile.getProperties().put("textures", texture);
    }

    public static PlayerTexture getPlayerTexture(String name) {
        return playerTextures.get(name);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
