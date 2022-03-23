package org.barrelmc.barrel.network.translator.bedrock;

import com.github.steveice10.mc.protocol.packet.ingame.clientbound.ClientboundDisconnectPacket;
import com.nukkitx.protocol.bedrock.BedrockPacket;

import org.barrelmc.barrel.network.translator.interfaces.BedrockPacketTranslator;
import org.barrelmc.barrel.player.Player;

public class DisconnectPacket implements BedrockPacketTranslator {
    @Override
    public void translate(BedrockPacket pk, Player player) {
        com.nukkitx.protocol.bedrock.packet.DisconnectPacket packet = (com.nukkitx.protocol.bedrock.packet.DisconnectPacket) pk;
        String message = packet.getKickMessage();
        ClientboundDisconnectPacket clientboundDisconnectPacket = new ClientboundDisconnectPacket(message);
        player.javaSession.send(clientboundDisconnectPacket);
    }
}
