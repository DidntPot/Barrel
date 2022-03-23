package org.barrelmc.barrel.network.translator.bedrock;

import com.github.steveice10.mc.protocol.packet.ingame.clientbound.ClientboundRespawnPacket;
import com.nukkitx.math.vector.Vector3i;
import com.nukkitx.protocol.bedrock.BedrockPacket;
import com.nukkitx.protocol.bedrock.data.PlayerActionType;
import com.nukkitx.protocol.bedrock.packet.PlayerActionPacket;

import org.barrelmc.barrel.network.translator.TranslatorUtils;
import org.barrelmc.barrel.network.translator.interfaces.BedrockPacketTranslator;
import org.barrelmc.barrel.player.Player;
import org.barrelmc.barrel.server.ProxyServer;

public class ChangeDimensionPacket implements BedrockPacketTranslator {

    @Override
    public void translate(BedrockPacket pk, Player player) {
        com.nukkitx.protocol.bedrock.packet.ChangeDimensionPacket packet = (com.nukkitx.protocol.bedrock.packet.ChangeDimensionPacket) pk;
        ClientboundRespawnPacket clientboundRespawnPacket = new ClientboundRespawnPacket(ProxyServer.instance.getDimensionTag(), TranslatorUtils.translateDimensionToJE(0), 0, TranslatorUtils.translateGamemodeToJE(player.gameType), TranslatorUtils.translateGamemodeToJE(player.gameType), true, true, false);
        player.javaSession.send(clientboundRespawnPacket);
        PlayerActionPacket playerActionPacket = new PlayerActionPacket();
        playerActionPacket.setAction(PlayerActionType.DIMENSION_CHANGE_SUCCESS);
        playerActionPacket.setBlockPosition(Vector3i.from(player.getFloorX(), player.getFloorY(), player.getFloorZ()));
        playerActionPacket.setFace(0);
        playerActionPacket.setRuntimeEntityId(player.runtimeEntityId);
        player.bedrockClient.getSession().sendPacket(playerActionPacket);
    }
}
