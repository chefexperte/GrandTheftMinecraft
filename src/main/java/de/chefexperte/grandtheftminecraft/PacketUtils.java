package de.chefexperte.grandtheftminecraft;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.reflect.StructureModifier;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedDataValue;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.google.common.collect.Lists;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

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

    public static int spawnFakeDisplayText(Player p, String text) {
        PacketContainer packet = GrandTheftMinecraft.protocolManager.createPacket(PacketType.Play.Server.SPAWN_ENTITY);
        int entityId = GrandTheftMinecraft.random.nextInt(100000);
        packet.getIntegers().write(0, entityId);
        packet.getUUIDs().write(0, UUID.randomUUID());
        packet.getEntityTypeModifier().write(0, EntityType.TEXT_DISPLAY);
        packet.getDoubles().write(0, p.getLocation().getX());
        packet.getDoubles().write(1, p.getLocation().getY());
        packet.getDoubles().write(2, p.getLocation().getZ());
        GrandTheftMinecraft.protocolManager.sendServerPacket(p, packet);

        // set text
        PacketContainer packet2 = GrandTheftMinecraft.protocolManager.createPacket(PacketType.Play.Server.ENTITY_METADATA);
        packet2.getIntegers().write(0, entityId);
        StructureModifier<List<WrappedDataValue>> watchableAccessor = packet2.getDataValueCollectionModifier();
        List<WrappedDataValue> values = Lists.newArrayList(
                new WrappedDataValue(23, WrappedDataWatcher.Registry.getChatComponentSerializer(), WrappedChatComponent.fromChatMessage(text)[0].getHandle())
        );
        watchableAccessor.write(0, values);
        GrandTheftMinecraft.protocolManager.sendServerPacket(p, packet2);
        return entityId;
    }

}
