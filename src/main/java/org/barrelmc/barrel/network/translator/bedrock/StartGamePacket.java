package org.barrelmc.barrel.network.translator.bedrock;

import com.github.steveice10.mc.protocol.packet.ingame.server.ServerDifficultyPacket;
import com.github.steveice10.mc.protocol.data.game.entity.EntityStatus;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerJoinGamePacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.player.ServerPlayerPositionRotationPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.ServerEntityStatusPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerPluginMessagePacket;
import com.github.steveice10.mc.protocol.data.game.setting.Difficulty;
import com.nukkitx.math.vector.Vector2f;
import com.nukkitx.math.vector.Vector3f;
import com.nukkitx.protocol.bedrock.BedrockPacket;
import com.nukkitx.protocol.bedrock.packet.SetLocalPlayerAsInitializedPacket;
import org.barrelmc.barrel.network.translator.TranslatorUtils;
import org.barrelmc.barrel.network.translator.interfaces.BedrockPacketTranslator;
import org.barrelmc.barrel.player.Player;
import org.barrelmc.barrel.server.ProxyServer;
import java.nio.charset.StandardCharsets;
public class StartGamePacket implements BedrockPacketTranslator {

    @Override
    public void translate(BedrockPacket pk, Player player) {
        com.nukkitx.protocol.bedrock.packet.StartGamePacket packet = (com.nukkitx.protocol.bedrock.packet.StartGamePacket) pk;

        ServerJoinGamePacket serverJoinGamePacket = new ServerJoinGamePacket(
                (int) packet.getRuntimeEntityId(), false,
                TranslatorUtils.translateGamemodeToJE(packet.getPlayerGameType()),
                TranslatorUtils.translateGamemodeToJE(packet.getPlayerGameType()),
                1, new String[]{"minecraft:world"}, ProxyServer.getInstance().getDimensionTag(),
                ProxyServer.getInstance().getOverworldTag(), "minecraft:world", packet.getSeed(),
                0, 16, true, true, true, false
        );
        Difficulty difficulty;
        try {
            difficulty = convertDifficultyToJE(packet.getDifficulty());
        } catch (Exception e) {
            difficulty = Difficulty.NORMAL;
        }
        ServerDifficultyPacket serverDifficultyPacket = new ServerDifficultyPacket(difficulty, true);
        ServerEntityStatusPacket serverEntityStatusPacket = new ServerEntityStatusPacket((int) packet.getRuntimeEntityId(), EntityStatus.PLAYER_OP_PERMISSION_LEVEL_0);
        Vector3f position = packet.getPlayerPosition();
        Vector2f rotation = packet.getRotation();
        ServerPlayerPositionRotationPacket serverPlayerPositionRotationPacket = new ServerPlayerPositionRotationPacket(position.getX(), position.getY(), position.getZ(), rotation.getY(), rotation.getX(), 1, true);
        ServerPluginMessagePacket serverPluginMessagePacket = new ServerPluginMessagePacket("minecraft:brand", packet.getServerEngine().getBytes(StandardCharsets.UTF_8));
        
        player.getJavaSession().send(serverJoinGamePacket);
        player.getJavaSession().send(serverPluginMessagePacket);
        player.getJavaSession().send(serverDifficultyPacket);
        player.getJavaSession().send(serverPlayerPositionRotationPacket);
        player.getJavaSession().send(serverEntityStatusPacket);

        SetLocalPlayerAsInitializedPacket setLocalPlayerAsInitializedPacket = new SetLocalPlayerAsInitializedPacket();
        setLocalPlayerAsInitializedPacket.setRuntimeEntityId(packet.getRuntimeEntityId());
        player.getBedrockClient().getSession().sendPacket(setLocalPlayerAsInitializedPacket);

        player.setRuntimeEntityId((int) packet.getRuntimeEntityId());
    }
    
    public static Difficulty convertDifficultyToJE(int diff) {
        Difficulty difficulty;
        switch (diff) {
            case 0: {
                difficulty = Difficulty.PEACEFUL;
                break;
            }
            case 1: {
                difficulty = Difficulty.EASY;
                break;
            }
            case 3: {
                difficulty = Difficulty.HARD;
                break;
            }
            default: {
                difficulty = Difficulty.NORMAL;
                break;
            }
        }
        return difficulty;
    }
    
}
