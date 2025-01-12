package org.barrelmc.barrel.network.translator.bedrock;

import com.github.steveice10.mc.protocol.packet.ingame.clientbound.level.ClientboundSetTimePacket;
import com.nukkitx.protocol.bedrock.BedrockPacket;
import org.barrelmc.barrel.network.translator.interfaces.BedrockPacketTranslator;
import org.barrelmc.barrel.player.Player;

public class SetTimePacket implements BedrockPacketTranslator {

    @Override
    public void translate(BedrockPacket pk, Player player) {
        com.nukkitx.protocol.bedrock.packet.SetTimePacket packet = (com.nukkitx.protocol.bedrock.packet.SetTimePacket) pk;
        player.javaSession.send(new ClientboundSetTimePacket(69420, packet.getTime()));
    }
}
