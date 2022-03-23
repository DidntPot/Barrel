package org.barrelmc.barrel.network.translator.bedrock;

import com.github.steveice10.mc.protocol.packet.ingame.clientbound.level.ClientboundSetChunkCacheRadiusPacket;
import com.nukkitx.protocol.bedrock.BedrockPacket;

import org.barrelmc.barrel.network.translator.interfaces.BedrockPacketTranslator;
import org.barrelmc.barrel.player.Player;

public class ChunkRadiusUpdatedPacket implements BedrockPacketTranslator {
    

    @Override
    public void translate(BedrockPacket pk, Player player) {
        com.nukkitx.protocol.bedrock.packet.ChunkRadiusUpdatedPacket packet = (com.nukkitx.protocol.bedrock.packet.ChunkRadiusUpdatedPacket) pk;
        int renderDistance = packet.getRadius();
        renderDistance *= 2; //circle to square
        ClientboundSetChunkCacheRadiusPacket clientboundSetChunkCacheRadiusPacket = new ClientboundSetChunkCacheRadiusPacket(renderDistance);
        player.javaSession.send(clientboundSetChunkCacheRadiusPacket);
    }
}
