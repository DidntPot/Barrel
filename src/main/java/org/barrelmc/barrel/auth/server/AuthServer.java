/*
 * Copyright (c) 2021 BarrelMC Team
 * This project is licensed under the MIT License
 */

package org.barrelmc.barrel.auth.server;

import java.nio.charset.StandardCharsets;

import com.github.steveice10.mc.protocol.data.game.entity.metadata.Position;
import com.github.steveice10.mc.protocol.data.game.entity.player.GameMode;
import com.github.steveice10.mc.protocol.packet.ingame.clientbound.ClientboundChatPacket;
import com.github.steveice10.mc.protocol.packet.ingame.clientbound.ClientboundCustomPayloadPacket;
//import com.github.steveice10.mc.protocol.packet.ingame.server.ServerJoinGamePacket;
import com.github.steveice10.mc.protocol.packet.ingame.clientbound.ClientboundLoginPacket;
import com.github.steveice10.mc.protocol.packet.ingame.clientbound.entity.player.ClientboundPlayerPositionPacket;
import com.github.steveice10.mc.protocol.packet.ingame.clientbound.level.ClientboundSetDefaultSpawnPositionPacket;
import com.github.steveice10.mc.protocol.packet.ingame.serverbound.ServerboundChatPacket;
import com.github.steveice10.mc.protocol.packet.ingame.serverbound.ServerboundCustomPayloadPacket;
import com.github.steveice10.mc.protocol.packet.ingame.serverbound.player.ServerboundMovePlayerRotPacket;
import com.github.steveice10.packetlib.Session;
import com.github.steveice10.packetlib.event.session.SessionAdapter;
import net.kyori.adventure.text.Component;
import org.barrelmc.barrel.auth.AuthManager;
import org.barrelmc.barrel.config.Config;
import org.barrelmc.barrel.server.ProxyServer;

public class AuthServer extends SessionAdapter {

    private final String username;

    public AuthServer(Session session, String username) {
        this.username = username;
        session.send(new ClientboundLoginPacket(
                69420, false, GameMode.ADVENTURE, GameMode.ADVENTURE,
                1, new String[]{"minecraft:world"}, ProxyServer.instance.getDimensionTag(),
                ProxyServer.instance.getOverworldTag(), "minecraft:world", 100,
                0, 4, 5, false, true, false, false
        ));

        session.send(new ClientboundSetDefaultSpawnPositionPacket(new Position(0, 82, 0), 0));
        session.send(new ClientboundPlayerPositionPacket(0, 82, 0, 0, 0, 0, true));
        session.send(new ClientboundChatPacket(Component.text("§ePlease input your email and password.\n§aEx: account@mail.com:password123")));
    }

    @Override
    public void packetReceived(com.github.steveice10.packetlib.Session session, com.github.steveice10.packetlib.packet.Packet packet) {
        if (packet instanceof ServerboundCustomPayloadPacket) {
            ServerboundCustomPayloadPacket pk = (ServerboundCustomPayloadPacket) packet;
            if(pk.getChannel() == "minecraft:brand"){
                String band = new String(pk.getData());
                System.out.print(band);
                if(band == "Geyser"){
                    session.send(new ClientboundChatPacket(Component.text("§cDo Not Use geyser here")));
                    session.send(new ClientboundChatPacket(Component.text("§cI will try to Trasfer you to this mc server")));
                    Config config =  ProxyServer.instance.config;
                    byte[] addressBytes = config.bedrockAddress.getBytes(StandardCharsets.UTF_8);
                    byte[] data = new byte[addressBytes.length + 4];

                    data[0] = (byte) (config.bedrockPort >> 24);
                    data[1] = (byte) (config.bedrockPort >> 16);
                    data[2] = (byte) (config.bedrockPort >> 8);
                    data[3] = (byte) ((int)config.bedrockPort);
                    System.arraycopy(addressBytes, 0, data, 4, addressBytes.length);

                    session.send(new ClientboundCustomPayloadPacket("floodgate:transfer", data));
                }
                AuthManager.getInstance().band.put(this.username, band);
            }
        }
        if (packet instanceof ServerboundChatPacket) {
            String messageStr = ((ServerboundChatPacket) packet).getMessage();

            String[] message = messageStr.split(":");
            if (message.length != 2) {
                session.send(new ClientboundChatPacket(Component.text("§cWrong format")));
                return;
            }

            if (message[1].length() < 8) {
                session.send(new ClientboundChatPacket(Component.text("§cInvalid password length")));
                return;
            }

            session.send(new ClientboundChatPacket(Component.text("§eLogging in...")));

            try {
                String token = AuthManager.getInstance().xboxLogin.getAccessToken(message[0], message[1]);
                AuthManager.getInstance().accessTokens.put(this.username, token);
                AuthManager.getInstance().loginPlayers.put(this.username, true);
            } catch (Exception e) {
                session.send(new ClientboundChatPacket(Component.text("§cLogin failed! Account or password invalid, please re-input the email and password")));
                return;
            }

            session.send(new ClientboundChatPacket(Component.text("§aLogin successfull! Please re-join.")));
        }
        if (packet instanceof ServerboundMovePlayerRotPacket) {
            session.send(new ClientboundPlayerPositionPacket(0, 82, 0, 0, 0, 0, true));
        }
    }
}
