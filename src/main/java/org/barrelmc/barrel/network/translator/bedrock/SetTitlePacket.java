package org.barrelmc.barrel.network.translator.bedrock;

import com.github.steveice10.mc.protocol.data.game.MessageType;
import com.github.steveice10.mc.protocol.packet.ingame.clientbound.title.*;
import com.nukkitx.protocol.bedrock.BedrockPacket;
import net.kyori.adventure.text.Component;
import org.barrelmc.barrel.network.translator.interfaces.BedrockPacketTranslator;
import org.barrelmc.barrel.player.Player;

public class SetTitlePacket  implements BedrockPacketTranslator {

    @Override
    public void translate(BedrockPacket pk, Player player) {
        com.nukkitx.protocol.bedrock.packet.SetTitlePacket packet = (com.nukkitx.protocol.bedrock.packet.SetTitlePacket) pk;
        switch(packet.getType()){
            case CLEAR: {
                player.javaSession.send(new ClientboundClearTitlesPacket(true));
                break;
            }
            case RESET:{
                player.javaSession.send(new ClientboundClearTitlesPacket(true));
                player.javaSession.send(new ClientboundSetTitleTextPacket(Component.text(packet.getText())));
                break;
            }
            case TITLE: {
                player.javaSession.send(new ClientboundSetTitleTextPacket(Component.text(packet.getText())));
                break;
            }
            case SUBTITLE: {
                player.javaSession.send(new ClientboundSetSubtitleTextPacket(Component.text(packet.getText())));
                break;
            }
            case ACTIONBAR: {
                player.javaSession.send(new ClientboundSetActionBarTextPacket(Component.text(packet.getText())));
                break;
            }
            case TIMES: {
                player.javaSession.send(new ClientboundSetTitlesAnimationPacket(packet.getFadeInTime(), packet.getFadeInTime(), packet.getFadeOutTime()));
                break;
            }
            default:
                break;
        }
    }
}
