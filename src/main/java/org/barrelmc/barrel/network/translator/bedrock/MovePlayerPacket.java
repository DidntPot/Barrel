package org.barrelmc.barrel.network.translator.bedrock;

import com.github.steveice10.mc.protocol.packet.ingame.clientbound.entity.ClientboundMoveEntityRotPacket;
import com.github.steveice10.mc.protocol.packet.ingame.clientbound.entity.ClientboundRotateHeadPacket;
import com.github.steveice10.mc.protocol.packet.ingame.clientbound.entity.ClientboundTeleportEntityPacket;
import com.github.steveice10.mc.protocol.packet.ingame.clientbound.entity.player.ClientboundPlayerPositionPacket;
import com.nukkitx.math.vector.Vector3f;
import com.nukkitx.protocol.bedrock.BedrockPacket;
import org.barrelmc.barrel.network.translator.interfaces.BedrockPacketTranslator;
import org.barrelmc.barrel.player.Player;

public class MovePlayerPacket implements BedrockPacketTranslator {

    @Override
    public void translate(BedrockPacket pk, Player player) {
        com.nukkitx.protocol.bedrock.packet.MovePlayerPacket packet = (com.nukkitx.protocol.bedrock.packet.MovePlayerPacket) pk;
        Vector3f position = packet.getPosition(), rotation = packet.getRotation();

        if (packet.getRuntimeEntityId() == player.runtimeEntityId) {
            player.javaSession.send(new ClientboundPlayerPositionPacket(position.getX(), position.getY() - 1.62, position.getZ(), rotation.getY(), rotation.getX(), 1,false));
            player.setPosition(position.getX(), position.getY() - 1.62, position.getZ());
        } else {
            player.javaSession.send(new ClientboundTeleportEntityPacket((int) packet.getRuntimeEntityId(), position.getX(), position.getY() - 1.62F, position.getZ(), rotation.getY(), rotation.getX(), packet.isOnGround()));
            player.javaSession.send(new ClientboundMoveEntityRotPacket((int) packet.getRuntimeEntityId(), packet.getRotation().getY(), packet.getRotation().getX(), packet.isOnGround()));
            player.javaSession.send(new ClientboundRotateHeadPacket((int) packet.getRuntimeEntityId(), rotation.getZ()));
        }
    }
}
