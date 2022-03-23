package org.barrelmc.barrel.network.translator.bedrock;

import com.github.steveice10.mc.protocol.packet.ingame.clientbound.entity.player.ClientboundPlayerAbilitiesPacket;
import com.nukkitx.protocol.bedrock.BedrockPacket;
import com.nukkitx.protocol.bedrock.data.AdventureSetting;

import com.nukkitx.protocol.bedrock.data.GameType;
import org.barrelmc.barrel.network.translator.interfaces.BedrockPacketTranslator;
import org.barrelmc.barrel.player.Player;

public class AdventureSettingsPacket implements BedrockPacketTranslator{
    @Override
    public void translate(BedrockPacket pk, Player player) {
        com.nukkitx.protocol.bedrock.packet.AdventureSettingsPacket packet = (com.nukkitx.protocol.bedrock.packet.AdventureSettingsPacket) pk;
        if(packet.getUniqueEntityId() == player.uniqueEntityId){
            boolean canfly = packet.getSettings().contains(AdventureSetting.MAY_FLY);
            boolean isfly = packet.getSettings().contains(AdventureSetting.FLYING);
            ClientboundPlayerAbilitiesPacket clientboundPlayerAbilitiesPacket = new ClientboundPlayerAbilitiesPacket(player.gameType == GameType.CREATIVE_VIEWER, canfly, isfly, player.gameType == GameType.CREATIVE, (float)0.5, (float)0.1);
            player.javaSession.send(clientboundPlayerAbilitiesPacket);
        }else{
            //todo:make for other player
        }
    }
}
