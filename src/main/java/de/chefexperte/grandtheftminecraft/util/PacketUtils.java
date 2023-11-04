package de.chefexperte.grandtheftminecraft.util;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.reflect.StructureModifier;
import com.comphenix.protocol.wrappers.*;
import com.google.common.collect.Lists;
import de.chefexperte.grandtheftminecraft.GrandTheftMinecraft;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;

import java.util.*;

import static de.chefexperte.grandtheftminecraft.GrandTheftMinecraft.*;

public class PacketUtils {

    public static boolean sendBlockBreakAnimation(Block block, int playerId, int stage) {
        if (GrandTheftMinecraft.isProtocolLibLoaded()) {
            PacketContainer packet = GrandTheftMinecraft.protocolManager.createPacket(PacketType.Play.Server.BLOCK_BREAK_ANIMATION);
            packet.getIntegers().write(0, playerId);
            packet.getBlockPositionModifier().write(0, new BlockPosition(block.getLocation().toVector()));
            packet.getIntegers().write(1, stage);
            try {
                for (Player p : block.getWorld().getPlayers()) {
                    GrandTheftMinecraft.protocolManager.sendServerPacket(p, packet);
                }
            } catch (Exception e) {
                GrandTheftMinecraft.instance.getLogger().warning("Failed to send block break animation packet: " + e.getMessage());
                return false;
            }
            return true;
        }
        return false;
    }

    public static TextDisplay spawnDisplayText(Player p, Location l, String text) {
        TextDisplay td = p.getWorld().spawn(l, TextDisplay.class);
        td.text(Component.text(text));
        //noinspection UnstableApiUsage
        td.setVisibleByDefault(false);
        //noinspection UnstableApiUsage
        p.showEntity(GrandTheftMinecraft.instance, td);
        int entityId = td.getEntityId();
        //int entityId = GrandTheftMinecraft.random.nextInt(100000);
        PacketContainer packet = GrandTheftMinecraft.protocolManager.createPacket(PacketType.Play.Server.SPAWN_ENTITY);
        packet.getIntegers().write(0, entityId);
        packet.getUUIDs().write(0, UUID.randomUUID());
        packet.getEntityTypeModifier().write(0, EntityType.TEXT_DISPLAY);
        packet.getDoubles().write(0, p.getLocation().getX());
        packet.getDoubles().write(1, p.getLocation().getY());
        packet.getDoubles().write(2, p.getLocation().getZ());
        //GrandTheftMinecraft.protocolManager.sendServerPacket(p, packet);

        // set text
        PacketContainer packet2 = GrandTheftMinecraft.protocolManager.createPacket(PacketType.Play.Server.ENTITY_METADATA);
        packet2.getIntegers().write(0, entityId);
        StructureModifier<List<WrappedDataValue>> watchableAccessor = packet2.getDataValueCollectionModifier();
        List<WrappedDataValue> values = Lists.newArrayList(
                new WrappedDataValue(23, WrappedDataWatcher.Registry.getChatComponentSerializer(), WrappedChatComponent.fromChatMessage(text)[0].getHandle())
        );
        watchableAccessor.write(0, values);
        //GrandTheftMinecraft.protocolManager.sendServerPacket(p, packet2);
        return td;
    }

    public static UUID entitySpawnListener(Player p, UUID uuid, String name, String texture, boolean hideNameTag) {
        Random random = GrandTheftMinecraft.random;
        //Player p = event.getPlayer();
        // append random number to name
        String randomName = name + random.nextInt(1000);
        int latency = 0;

        PacketContainer pack = protocolManager.createPacket(PacketType.Play.Server.PLAYER_INFO);
        pack.getPlayerInfoActions().write(0, EnumSet.of(EnumWrappers.PlayerInfoAction.ADD_PLAYER, EnumWrappers.PlayerInfoAction.UPDATE_LISTED));
        pack.getPlayerInfoDataLists().write(1, Collections.singletonList(new PlayerInfoData(
                uuid,
                latency,
                true,
                EnumWrappers.NativeGameMode.SURVIVAL,
                new WrappedGameProfile(uuid, randomName),
                WrappedChatComponent.fromText(name)
        )));
        WrappedGameProfile profile = pack.getPlayerInfoDataLists().read(1).get(0).getProfile();
        setProfileTexture(profile, getPlayerTexture(texture));
        protocolManager.sendServerPacket(p, pack);
        if (hideNameTag) {
            PacketContainer p2 = protocolManager.createPacket(PacketType.Play.Server.SCOREBOARD_TEAM);
            p2.getStrings().write(0, "nhide");
            p2.getIntegers().write(0, 3);
            p2.getModifier().withType(Collection.class, BukkitConverters.getListConverter(Converters.passthrough(String.class)))
                    .write(0, Collections.singletonList(randomName));
            protocolManager.sendServerPacket(p, p2);
        }
        return uuid;
    }

}
