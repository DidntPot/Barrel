package org.barrelmc.barrel.network.translator.bedrock;

import com.github.steveice10.mc.protocol.packet.ingame.clientbound.ClientboundLoginPacket;
import com.github.steveice10.mc.protocol.packet.ingame.clientbound.ClientboundChangeDifficultyPacket;
import com.github.steveice10.mc.protocol.data.game.entity.EntityEvent;
import com.github.steveice10.mc.protocol.data.game.entity.metadata.Position;
import com.github.steveice10.mc.protocol.packet.ingame.clientbound.entity.ClientboundEntityEventPacket;
import com.github.steveice10.mc.protocol.packet.ingame.clientbound.entity.player.ClientboundPlayerPositionPacket;
import com.github.steveice10.mc.protocol.packet.ingame.clientbound.level.ClientboundSetChunkCacheCenterPacket;
import com.github.steveice10.mc.protocol.packet.ingame.clientbound.level.ClientboundSetDefaultSpawnPositionPacket;
import com.github.steveice10.mc.protocol.packet.ingame.clientbound.ClientboundCustomPayloadPacket;
import com.github.steveice10.mc.protocol.data.game.setting.Difficulty;
import com.nukkitx.math.vector.Vector2f;
import com.nukkitx.math.vector.Vector3f;
import com.nukkitx.protocol.bedrock.BedrockPacket;
import org.barrelmc.barrel.network.translator.TranslatorUtils;
import org.barrelmc.barrel.network.translator.interfaces.BedrockPacketTranslator;
import org.barrelmc.barrel.player.Player;
import org.barrelmc.barrel.server.ProxyServer;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
public class StartGamePacket implements BedrockPacketTranslator {

    @Override
    public void translate(BedrockPacket pk, Player player) {
        com.nukkitx.protocol.bedrock.packet.StartGamePacket packet = (com.nukkitx.protocol.bedrock.packet.StartGamePacket) pk;

        ClientboundLoginPacket serverJoinGamePacket = new ClientboundLoginPacket(
                (int) packet.getRuntimeEntityId(), false,
                TranslatorUtils.translateGamemodeToJE(packet.getPlayerGameType()),
                TranslatorUtils.translateGamemodeToJE(packet.getPlayerGameType()),
                1, new String[]{"minecraft:world"}, ProxyServer.instance.getDimensionTag(),
                ProxyServer.instance.getOverworldTag(), "minecraft:world", packet.getSeed(),
                69, 16, 16, false, true, false, (packet.getGeneratorId() == 2)
        );
        for (com.nukkitx.protocol.bedrock.packet.StartGamePacket.ItemEntry itemEntry : packet.getItemEntries()) {
			if (itemEntry.getIdentifier().equals("minecraft:shield")) {
				player.bedrockClient.getSession().getHardcodedBlockingId().set(itemEntry.getId());
				break;
			}
		}
        
        Difficulty difficulty;
        try {
            difficulty = TranslatorUtils.convertDifficultyToJE(packet.getDifficulty());
        } catch (Exception e) {
            difficulty = Difficulty.NORMAL;
        }
        ClientboundChangeDifficultyPacket serverDifficultyPacket = new ClientboundChangeDifficultyPacket(difficulty, true);
        ClientboundEntityEventPacket serverEntityStatusPacket = new ClientboundEntityEventPacket((int) packet.getRuntimeEntityId(), EntityEvent.PLAYER_OP_PERMISSION_LEVEL_0);
        Vector3f position = packet.getPlayerPosition();
        Vector2f rotation = packet.getRotation();
        ClientboundPlayerPositionPacket serverPlayerPositionRotationPacket = new ClientboundPlayerPositionPacket(position.getX(), position.getY(), position.getZ(), rotation.getY(), rotation.getX(), 1, true);
        byte[] data = ((packet.getServerEngine() == "") ? "Barrel Dev" : packet.getServerEngine()).getBytes(StandardCharsets.UTF_8);
        ClientboundCustomPayloadPacket serverPluginMessagePacket1 = new ClientboundCustomPayloadPacket("minecraft:register", data);
        ClientboundCustomPayloadPacket serverPluginMessagePacket2 = new ClientboundCustomPayloadPacket("minecraft:brand", data);
        ClientboundSetDefaultSpawnPositionPacket defaultSpawnPositionPacket = new ClientboundSetDefaultSpawnPositionPacket(new Position(packet.getDefaultSpawn().getX(), packet.getDefaultSpawn().getY(), packet.getDefaultSpawn().getZ()), 0);
        ClientboundSetChunkCacheCenterPacket clientboundSetChunkCacheCenterPacket = new ClientboundSetChunkCacheCenterPacket((int)position.getX() >> 4, (int)position.getZ() >> 4); 
        player.javaSession.send(serverPluginMessagePacket1);
        player.javaSession.send(serverPluginMessagePacket2);
        player.javaSession.send(serverJoinGamePacket);
        player.javaSession.send(serverDifficultyPacket);
        player.javaSession.send(defaultSpawnPositionPacket);
        player.javaSession.send(serverPlayerPositionRotationPacket);
        player.javaSession.send(serverEntityStatusPacket);
        player.javaSession.send(clientboundSetChunkCacheCenterPacket);

        player.movementMode = packet.getPlayerMovementSettings().getMovementMode();
        player.serverversion = packet.getVanillaVersion();
        player.gameType = packet.getPlayerGameType();
        player.uniqueEntityId = ((int) packet.getUniqueEntityId());
        player.runtimeEntityId = ((int) packet.getRuntimeEntityId());
    }

    private static byte[] writeVarInt(int value) {
        byte[] data = new byte[getVarIntLength(value)];
        int index = 0;
        do {
            byte temp = (byte) (value & 0b01111111);
            value >>>= 7;
            if (value != 0) {
                temp |= 0b10000000;
            }
            data[index] = temp;
            index++;
        } while (value != 0);
        return data;
    }

    private static int getVarIntLength(int number) {
        if ((number & 0xFFFFFF80) == 0) {
            return 1;
        } else if ((number & 0xFFFFC000) == 0) {
            return 2;
        } else if ((number & 0xFFE00000) == 0) {
            return 3;
        } else if ((number & 0xF0000000) == 0) {
            return 4;
        }
        return 5;
    }
}
