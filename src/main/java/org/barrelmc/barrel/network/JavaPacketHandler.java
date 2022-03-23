/*
 * Copyright (c) 2021 BarrelMC Team
 * This project is licensed under the MIT License
 */

package org.barrelmc.barrel.network;

import com.github.steveice10.mc.auth.data.GameProfile;
import com.github.steveice10.mc.protocol.MinecraftConstants;
import com.github.steveice10.mc.protocol.packet.login.serverbound.ServerboundHelloPacket;
import com.github.steveice10.packetlib.event.session.SessionAdapter;
import com.github.steveice10.packetlib.packet.Packet;
import org.barrelmc.barrel.auth.AuthManager;
import org.barrelmc.barrel.player.Player;
import org.barrelmc.barrel.server.ProxyServer;

import java.util.UUID;

public class JavaPacketHandler extends SessionAdapter {
    private Player player = null;

    @Override
    public void packetReceived(com.github.steveice10.packetlib.Session session, com.github.steveice10.packetlib.packet.Packet packet) {
        if (this.player == null) {
            if (packet instanceof ServerboundHelloPacket) {
                ServerboundHelloPacket loginPacket = (ServerboundHelloPacket) packet;

                if (AuthManager.getInstance().accessTokens.containsKey(loginPacket.getUsername())) {
                    new Player(loginPacket, session);
                    //System.out.println("hmm4");
                    UUID uuid = UUID.nameUUIDFromBytes((loginPacket.getUsername()).getBytes());
                    GameProfile gameProfile = new GameProfile(uuid, loginPacket.getUsername());
                    session.setFlag(MinecraftConstants.PROFILE_KEY, gameProfile);
                    this.player = ProxyServer.instance.getPlayerByName(loginPacket.getUsername());
                }
                //System.out.println("Playerlogin|"+loginPacket.getUsername());
            }
        } else {
            player.packetTranslatorManager.translate((Packet) packet);
        }
    }
    @Override
    public void packetError(com.github.steveice10.packetlib.event.session.PacketErrorEvent event) {
        System.out.println(event.getCause());
    }
}
