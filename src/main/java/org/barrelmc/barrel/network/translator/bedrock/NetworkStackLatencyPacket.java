package org.barrelmc.barrel.network.translator.bedrock;

import com.nukkitx.protocol.bedrock.BedrockPacket;

import org.barrelmc.barrel.network.translator.interfaces.BedrockPacketTranslator;
import org.barrelmc.barrel.player.Player;

public class NetworkStackLatencyPacket implements BedrockPacketTranslator {

    @Override
    public void translate(BedrockPacket pk, Player player) {
        com.nukkitx.protocol.bedrock.packet.NetworkStackLatencyPacket packet = (com.nukkitx.protocol.bedrock.packet.NetworkStackLatencyPacket) pk;
        if (packet.isFromServer()) {
            com.nukkitx.protocol.bedrock.packet.NetworkStackLatencyPacket resp = new com.nukkitx.protocol.bedrock.packet.NetworkStackLatencyPacket();
            resp.setTimestamp(System.currentTimeMillis());
            player.bedrockClient.getSession().sendPacketImmediately(resp);
        }
    }
}
