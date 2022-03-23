package org.barrelmc.barrel.network.translator.java;

import com.github.steveice10.mc.protocol.data.game.ClientCommand;
import com.github.steveice10.packetlib.packet.Packet;
import com.nukkitx.math.vector.Vector3f;
import com.nukkitx.protocol.bedrock.packet.RespawnPacket;
import org.barrelmc.barrel.network.translator.interfaces.JavaPacketTranslator;
import org.barrelmc.barrel.player.Player;

public class ClientRequestPacket implements JavaPacketTranslator {

    @Override
    public void translate(Packet pk, Player player) {
        com.github.steveice10.mc.protocol.packet.ingame.serverbound.ServerboundClientCommandPacket packet = (com.github.steveice10.mc.protocol.packet.ingame.serverbound.ServerboundClientCommandPacket) pk;

        if (packet.getRequest() == ClientCommand.RESPAWN) {
            RespawnPacket respawnPacket = new RespawnPacket();

            respawnPacket.setPosition(Vector3f.from(0, 0, 0));
            respawnPacket.setRuntimeEntityId(player.runtimeEntityId);
            respawnPacket.setState(RespawnPacket.State.CLIENT_READY);
            player.bedrockClient.getSession().sendPacket(respawnPacket);
        }
    }
}
