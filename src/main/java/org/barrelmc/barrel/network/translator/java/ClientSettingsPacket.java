package org.barrelmc.barrel.network.translator.java;

import com.github.steveice10.packetlib.packet.Packet;
import com.nukkitx.protocol.bedrock.packet.RequestChunkRadiusPacket;
import org.barrelmc.barrel.network.translator.interfaces.JavaPacketTranslator;
import org.barrelmc.barrel.player.Player;

public class ClientSettingsPacket implements JavaPacketTranslator {

    @Override
    public void translate(Packet pk, Player player) {
        com.github.steveice10.mc.protocol.packet.ingame.serverbound.ServerboundClientInformationPacket settingsPacket = (com.github.steveice10.mc.protocol.packet.ingame.serverbound.ServerboundClientInformationPacket) pk;
        RequestChunkRadiusPacket chunkRadiusPacket = new RequestChunkRadiusPacket();

        chunkRadiusPacket.setRadius(settingsPacket.getRenderDistance());
        player.bedrockClient.getSession().sendPacket(chunkRadiusPacket);
    }
}
