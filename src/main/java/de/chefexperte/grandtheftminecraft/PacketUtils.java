package de.chefexperte.grandtheftminecraft;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.BlockPosition;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

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

}
