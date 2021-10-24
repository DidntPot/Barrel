package org.barrelmc.barrel.network.translator.bedrock;

import com.github.steveice10.mc.protocol.data.game.MessageType;
import com.github.steveice10.mc.protocol.packet.ingame.server.title.*;
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
                player.getJavaSession().send(new ServerClearTitlesPacket(true));
                break;
            }
            case RESET:{
                player.getJavaSession().send(new ServerClearTitlesPacket(true));
                player.getJavaSession().send(new ServerSetTitleTextPacket(Component.text(packet.getText())));
                break;
            }
            case TITLE: {
                player.getJavaSession().send(new ServerSetTitleTextPacket(Component.text(packet.getText())));
                break;
            }
            case SUBTITLE: {
                player.getJavaSession().send(new ServerSetSubtitleTextPacket(Component.text(packet.getText())));
                break;
            }
            case ACTIONBAR: {
                player.getJavaSession().send(new ServerSetActionBarTextPacket(Component.text(packet.getText())));
                break;
            }
            case TIMES: {
                player.getJavaSession().send(new ServerSetTitlesAnimationPacket(packet.getFadeInTime(), packet.getFadeInTime(), packet.getFadeOutTime()));
                break;
            }
            default:
                break;
        }
    }
}
