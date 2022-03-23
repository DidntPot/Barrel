package org.barrelmc.barrel.network.translator.bedrock;

import com.github.steveice10.mc.protocol.data.game.MessageType;
import com.github.steveice10.mc.protocol.packet.ingame.clientbound.ClientboundChatPacket;
import com.nukkitx.protocol.bedrock.BedrockPacket;
import net.kyori.adventure.text.Component;
import org.barrelmc.barrel.network.translator.interfaces.BedrockPacketTranslator;
import org.barrelmc.barrel.player.Player;

public class TextPacket implements BedrockPacketTranslator {

    @Override
    public void translate(BedrockPacket pk, Player player) {
        com.nukkitx.protocol.bedrock.packet.TextPacket packet = (com.nukkitx.protocol.bedrock.packet.TextPacket) pk;
        //System.out.println(packet.getMessage());
        switch (packet.getType()) {
            case SYSTEM: {
                player.javaSession.send(new ClientboundChatPacket(Component.text(packet.getMessage()), MessageType.SYSTEM));
                break;
            } 
            case TIP:
            case POPUP: {
                player.sendTip(packet.getMessage());
                break;
            }
            default: {
                player.sendMessage(packet.getMessage());
                break;
            }
        }
    }
}
