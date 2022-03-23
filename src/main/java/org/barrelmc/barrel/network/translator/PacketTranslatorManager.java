package org.barrelmc.barrel.network.translator;
import com.github.steveice10.mc.protocol.packet.ingame.serverbound.ServerboundChatPacket;
import com.github.steveice10.mc.protocol.packet.ingame.serverbound.ServerboundClientCommandPacket;
import com.github.steveice10.mc.protocol.packet.ingame.serverbound.ServerboundClientInformationPacket;
import com.github.steveice10.mc.protocol.packet.ingame.serverbound.player.*;
import com.github.steveice10.packetlib.packet.Packet;
import com.nukkitx.protocol.bedrock.BedrockPacket;
import com.nukkitx.protocol.bedrock.packet.*;

import org.barrelmc.barrel.network.translator.interfaces.BedrockPacketTranslator;
import org.barrelmc.barrel.network.translator.interfaces.JavaPacketTranslator;
import org.barrelmc.barrel.player.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class PacketTranslatorManager {

    private final ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
            Runtime.getRuntime().availableProcessors(),
            Integer.MAX_VALUE,
            60,
            TimeUnit.SECONDS,
            new SynchronousQueue<>(),
            new ThreadPoolExecutor.CallerRunsPolicy()
    );
    private final Map<Class<? extends Packet>, JavaPacketTranslator> javaTranslators = new HashMap<>();
    private final Map<Class<? extends BedrockPacket>, BedrockPacketTranslator> bedrockTranslators = new HashMap<>();

    private final Player player;

    public PacketTranslatorManager(Player player) {
        this.player = player;
        this.registerDefaultPackets();
    }

    public void translate(BedrockPacket pk) {
        BedrockPacketTranslator translator = bedrockTranslators.get(pk.getClass());

        if (translator != null) {
            if (translator.immediate()) {
                translator.translate(pk, player);
            } else {
                threadPoolExecutor.execute(() -> translator.translate(pk, player));
            }
        }
    }

    public void translate(Packet pk) {
        JavaPacketTranslator translator = javaTranslators.get(pk.getClass());

        if (translator != null) {
            threadPoolExecutor.execute(() -> translator.translate(pk, player));
        }
    }

    private void registerDefaultPackets() {
        // Bedrock packets
        bedrockTranslators.put(AddPlayerPacket.class, new org.barrelmc.barrel.network.translator.bedrock.AddPlayerPacket());
        bedrockTranslators.put(AnimatePacket.class, new org.barrelmc.barrel.network.translator.bedrock.AnimatePacket());
        bedrockTranslators.put(BlockEventPacket.class, new org.barrelmc.barrel.network.translator.bedrock.BlockEventPacket());
        bedrockTranslators.put(LevelChunkPacket.class, new org.barrelmc.barrel.network.translator.bedrock.LevelChunkPacket());
        bedrockTranslators.put(LevelEventPacket.class, new org.barrelmc.barrel.network.translator.bedrock.LevelEventPacket());
        bedrockTranslators.put(MoveEntityAbsolutePacket.class, new org.barrelmc.barrel.network.translator.bedrock.MoveEntityAbsolutePacket());
        bedrockTranslators.put(MovePlayerPacket.class, new org.barrelmc.barrel.network.translator.bedrock.MovePlayerPacket());
        bedrockTranslators.put(PlayerListPacket.class, new org.barrelmc.barrel.network.translator.bedrock.PlayerListPacket());
        bedrockTranslators.put(RemoveEntityPacket.class, new org.barrelmc.barrel.network.translator.bedrock.RemoveEntityPacket());
        bedrockTranslators.put(RemoveObjectivePacket.class, new org.barrelmc.barrel.network.translator.bedrock.RemoveObjectivePacket());
        bedrockTranslators.put(ResourcePacksInfoPacket.class, new org.barrelmc.barrel.network.translator.bedrock.ResourcePacksInfoPacket());
        bedrockTranslators.put(ResourcePackStackPacket.class, new org.barrelmc.barrel.network.translator.bedrock.ResourcePackStackPacket());
        bedrockTranslators.put(ServerToClientHandshakePacket.class, new org.barrelmc.barrel.network.translator.bedrock.ServerToClientHandshakePacket());
        bedrockTranslators.put(SetDisplayObjectivePacket.class, new org.barrelmc.barrel.network.translator.bedrock.SetDisplayObjectivePacket());
        bedrockTranslators.put(SetScorePacket.class, new org.barrelmc.barrel.network.translator.bedrock.SetScorePacket());
        bedrockTranslators.put(SetTimePacket.class, new org.barrelmc.barrel.network.translator.bedrock.SetTimePacket());
        bedrockTranslators.put(StartGamePacket.class, new org.barrelmc.barrel.network.translator.bedrock.StartGamePacket());
        bedrockTranslators.put(TakeItemEntityPacket.class, new org.barrelmc.barrel.network.translator.bedrock.TakeItemEntityPacket());
        bedrockTranslators.put(TextPacket.class, new org.barrelmc.barrel.network.translator.bedrock.TextPacket());
        bedrockTranslators.put(SetTitlePacket.class, new org.barrelmc.barrel.network.translator.bedrock.SetTitlePacket());
        bedrockTranslators.put(SetPlayerGameTypePacket.class, new org.barrelmc.barrel.network.translator.bedrock.SetPlayerGameTypePacket());
        bedrockTranslators.put(PlayStatusPacket.class, new org.barrelmc.barrel.network.translator.bedrock.PlayStatusPacket());
        bedrockTranslators.put(ChangeDimensionPacket.class, new org.barrelmc.barrel.network.translator.bedrock.ChangeDimensionPacket());
        bedrockTranslators.put(ChunkRadiusUpdatedPacket.class, new org.barrelmc.barrel.network.translator.bedrock.ChunkRadiusUpdatedPacket());
        bedrockTranslators.put(AdventureSettingsPacket.class, new org.barrelmc.barrel.network.translator.bedrock.AdventureSettingsPacket());
        bedrockTranslators.put(ModalFormRequestPacket.class, new org.barrelmc.barrel.network.translator.bedrock.ModalFormRequestPacket());
        bedrockTranslators.put(NetworkStackLatencyPacket.class, new org.barrelmc.barrel.network.translator.bedrock.NetworkStackLatencyPacket());
        bedrockTranslators.put(DisconnectPacket.class, new org.barrelmc.barrel.network.translator.bedrock.DisconnectPacket());

        // Java packets
        javaTranslators.put(ServerboundChatPacket.class, new org.barrelmc.barrel.network.translator.java.ClientChatPacket());
        javaTranslators.put(ServerboundSetCarriedItemPacket.class, new org.barrelmc.barrel.network.translator.java.ClientPlayerChangeHeldItemPacket());
        javaTranslators.put(ServerboundMovePlayerPosPacket.class, new org.barrelmc.barrel.network.translator.java.ClientPlayerPositionPacket());
        javaTranslators.put(ServerboundMovePlayerPosRotPacket.class, new org.barrelmc.barrel.network.translator.java.ClientPlayerPositionRotationPacket());
        javaTranslators.put(ServerboundMovePlayerRotPacket.class, new org.barrelmc.barrel.network.translator.java.ClientPlayerRotationPacket());
        javaTranslators.put(ServerboundPlayerCommandPacket.class, new org.barrelmc.barrel.network.translator.java.ClientPlayerStatePacket());
        javaTranslators.put(ServerboundSwingPacket.class, new org.barrelmc.barrel.network.translator.java.ClientPlayerSwingArmPacket());
        javaTranslators.put(ServerboundClientCommandPacket.class, new org.barrelmc.barrel.network.translator.java.ClientRequestPacket());
        javaTranslators.put(ServerboundClientInformationPacket.class, new org.barrelmc.barrel.network.translator.java.ClientSettingsPacket());
    }
}
