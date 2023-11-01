package de.chefexperte.grandtheftminecraft;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.*;
import de.chefexperte.grandtheftminecraft.commands.GetAmmoCommand;
import de.chefexperte.grandtheftminecraft.commands.GetGunCommand;
import de.chefexperte.grandtheftminecraft.commands.SpawnPoliceCommand;
import de.chefexperte.grandtheftminecraft.events.GunEvents;
import de.chefexperte.grandtheftminecraft.events.PoliceOfficerEvents;
import de.chefexperte.grandtheftminecraft.guns.Guns;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.*;

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
        }
    };

    Scoreboard scoreboard;


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
        if (!isProtocolLibLoaded()) {
            getLogger().warning("ProtocolLib is not installed! This plugin will not work without it!");
            return;
        }
        Guns.init();
        getLogger().info("Welcome to Grand Theft Minecraft!");
        this.getServer().getCommandMap().register("gtm", new GetGunCommand());
        this.getServer().getCommandMap().register("gtm", new GetAmmoCommand());
        this.getServer().getCommandMap().register("gtm", new SpawnPoliceCommand());
        this.getServer().getPluginManager().registerEvents(new GunEvents(), this);
        this.getServer().getPluginManager().registerEvents(new PoliceOfficerEvents(), this);
        enableProtocolListener();
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
                // spawn a player instead of police officer zombie
//                if (packet.getEntityTypeModifier().read(0) != EntityType.ZOMBIE) {
//                    return;
//                }

                UUID uuid = UUID.randomUUID();
                packet.getEntityTypeModifier().write(0, EntityType.PLAYER);
                packet.getUUIDs().write(0, uuid);
                // check if custom name is "Police Officer"
                var ent = packet.getEntityModifier(event).read(0);
                //Util.hideNickname(ent);
                //Component customNameComp = ent.name();
                //if (!customNameComp.equals(Component.text("Police Officer"))) {
                //    return;
                //}
                // check if persistent data container has "gtm.police" key
                PersistentDataContainer c = ent.getPersistentDataContainer();
                if (!c.has(new NamespacedKey("gtm", "police"), PersistentDataType.BYTE)) {
                    return;
                }
                // append random number to name
                String name = "PoliceOfficer" + random.nextInt(1000);
                int latency = 0;

                PacketContainer p = protocolManager.createPacket(PacketType.Play.Server.PLAYER_INFO);
                p.getPlayerInfoActions().write(0, EnumSet.of(EnumWrappers.PlayerInfoAction.ADD_PLAYER, EnumWrappers.PlayerInfoAction.UPDATE_LISTED));
                p.getPlayerInfoDataLists().write(1, Collections.singletonList(new PlayerInfoData(
                        uuid,
                        latency,
                        true,
                        EnumWrappers.NativeGameMode.SURVIVAL,
                        new WrappedGameProfile(uuid, name),
                        WrappedChatComponent.fromText(name)
                )));
                WrappedGameProfile profile = p.getPlayerInfoDataLists().read(1).get(0).getProfile();
                setProfileTexture(profile, playerTextures.get("Police Officer"));
                protocolManager.sendServerPacket(event.getPlayer(), p);
                //PacketContainer p2 = protocolManager.createPacket(PacketType.Play.Server.SCOREBOARD_TEAM);
                //p2.getStrings().write(0, "nhide");
                //p2.getIntegers().write(0, 3);
                //p2.getStrings().write(1, name);
                //p2.getStringArrays().write(0, new String[]{name});
                //protocolManager.sendServerPacket(event.getPlayer(), p2);

            }
        });
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

    private void setProfileTexture(WrappedGameProfile profile, PlayerTexture pTexture) {
        WrappedSignedProperty texture = new WrappedSignedProperty("textures", pTexture.value, pTexture.signature);
        profile.getProperties().removeAll("textures");
        profile.getProperties().put("textures", texture);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
