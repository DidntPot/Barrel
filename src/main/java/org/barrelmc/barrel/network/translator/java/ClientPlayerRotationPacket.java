package org.barrelmc.barrel.network.translator.java;

import com.github.steveice10.packetlib.packet.Packet;
import com.nukkitx.math.vector.Vector3f;
import com.nukkitx.protocol.bedrock.packet.MovePlayerPacket;
import org.barrelmc.barrel.network.translator.interfaces.JavaPacketTranslator;
import org.barrelmc.barrel.player.Player;

public class ClientPlayerRotationPacket implements JavaPacketTranslator {

    @Override
    public void translate(Packet pk, Player player) {
        com.github.steveice10.mc.protocol.packet.ingame.serverbound.player.ServerboundMovePlayerRotPacket packet = (com.github.steveice10.mc.protocol.packet.ingame.serverbound.player.ServerboundMovePlayerRotPacket) pk;
        MovePlayerPacket movePlayerPacket = new MovePlayerPacket();

        movePlayerPacket.setMode(MovePlayerPacket.Mode.HEAD_ROTATION);
        movePlayerPacket.setOnGround(packet.isOnGround());
        movePlayerPacket.setRuntimeEntityId(player.runtimeEntityId);
        movePlayerPacket.setRidingRuntimeEntityId(0);
        movePlayerPacket.setPosition(player.getVector3f());
        movePlayerPacket.setRotation(Vector3f.from(packet.getPitch(), packet.getYaw(), 0));
        movePlayerPacket.setTeleportationCause(MovePlayerPacket.TeleportationCause.UNKNOWN);
        movePlayerPacket.setEntityType(0);

        player.setRotation(packet.getYaw(), packet.getPitch());
        player.bedrockClient.getSession().sendPacket(movePlayerPacket);
    }
}
