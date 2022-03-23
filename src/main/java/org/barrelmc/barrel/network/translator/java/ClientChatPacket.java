package org.barrelmc.barrel.network.translator.java;

import com.alibaba.fastjson.JSONArray;
import com.github.steveice10.packetlib.packet.Packet;
import com.nukkitx.protocol.bedrock.data.command.CommandOriginData;
import com.nukkitx.protocol.bedrock.data.command.CommandOriginType;
import com.nukkitx.protocol.bedrock.packet.CommandRequestPacket;
import com.nukkitx.protocol.bedrock.packet.ModalFormResponsePacket;
import com.nukkitx.protocol.bedrock.packet.TextPacket;

import org.barrelmc.barrel.network.data.Form;
import org.barrelmc.barrel.network.translator.interfaces.JavaPacketTranslator;
import org.barrelmc.barrel.player.Player;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class ClientChatPacket implements JavaPacketTranslator {

    @Override
    public void translate(Packet pk, Player player) {
        com.github.steveice10.mc.protocol.packet.ingame.serverbound.ServerboundChatPacket chatPacket = (com.github.steveice10.mc.protocol.packet.ingame.serverbound.ServerboundChatPacket) pk;
        Character firstChar = chatPacket.getMessage().charAt(0);
        if (firstChar.equals('/')) {
            CommandRequestPacket commandRequestPacket = new CommandRequestPacket();
            commandRequestPacket.setInternal(false);
            commandRequestPacket.setCommand(chatPacket.getMessage());
            commandRequestPacket.setCommandOriginData(new CommandOriginData(CommandOriginType.PLAYER, UUID.nameUUIDFromBytes(player.UUID.getBytes(StandardCharsets.UTF_8)), "", 0));
            player.bedrockClient.getSession().sendPacket(commandRequestPacket);
        } else if (firstChar.equals('`')) {
            if (chatPacket.getMessage().length() > 1) {
                String[] commandList = chatPacket.getMessage().substring(1).split(" "), argsList = new String[commandList.length - 1];
                if (commandList.length != 1) {
                    for (int i = 1; i < commandList.length; i++) {
                        argsList[i - 1] = commandList[i];
                    }
                }
                try {
                    exec(argsList, player);
                } catch (Throwable throwable) {
                    player.sendAlert("An error occurred while running this command.");
                    throwable.printStackTrace();
                }
            }
        } else {
            TextPacket textPacket = new TextPacket();

            textPacket.setType(TextPacket.Type.CHAT);
            textPacket.setNeedsTranslation(false);
            textPacket.setSourceName(player.username);
            textPacket.setMessage(chatPacket.getMessage());
            textPacket.setXuid(player.xuid);
            textPacket.setPlatformChatId("");
            player.bedrockClient.getSession().sendPacket(textPacket);    
        }
    }

    public void exec(String[] args, Player client) {
        Form formData = client.form;
        if (formData == null) {
            client.sendAlert("No Any Form is opening now!");
            return;
        }
        switch (formData.type) {
            case SIMPLE: {
                simpleForm(formData, args, client);
                break;
            }
            case MODAL: {
                modalForm(formData, args, client);
                break;
            }
            case CUSTOM: {
                customForm(formData, args, client);
                break;
            }
        }
    }

    private void simpleForm(Form formData, String[] args, Player client) {
        switch (args[0]) {
            case "choose": {
                ModalFormResponsePacket reqPacket = new ModalFormResponsePacket();
                reqPacket.setFormId(formData.data.getInteger("id"));
                int index = new Integer(args[1]);
                if (index >= formData.array) {
                    client.sendAlert("[ERROR]Array Outside The Bound Of Array.");
                    return;
                }
                reqPacket.setFormData(index + "");
                client.bedrockClient.getSession().sendPacket(reqPacket);
                client.form = (null);
                client.sendAlert("Form Result Bound To The Server.");
                break;
            }
            case "close": {
                ModalFormResponsePacket reqPacket = new ModalFormResponsePacket();
                reqPacket.setFormId(formData.data.getInteger("id"));
                reqPacket.setFormData(null);
                client.bedrockClient.getSession().sendPacket(reqPacket);
                client.form = (null);
                client.sendAlert("Form Closed.");
                break;
            }
            case "list": {
                client.sendAlert("Buttons Below:");
                JSONArray buttons = formData.data.getJSONArray("buttons");
                for (int i = 0; i < buttons.size(); i++) {
                    client.sendMessage(i + ": " + buttons.getJSONObject(i).getString("text"));
                }
            }
            default: {
                client.sendAlert("`form choose <index> - Click a button at index");
                client.sendAlert("`form close - Close the form");
                break;
            }
        }
    }

    private void modalForm(Form formData, String[] args, Player client) {
        switch (args[0]) {
            case "choose": {
                ModalFormResponsePacket reqPacket = new ModalFormResponsePacket();
                reqPacket.setFormId(formData.data.getInteger("id"));
                if (args[1].equals("1")) {
                    reqPacket.setFormData("true");
                } else if (args[1].equals("2")) {
                    reqPacket.setFormData("false");
                } else {
                    client.sendAlert("[ERROR]Please input `form choose <1/2>");
                }
                client.bedrockClient.getSession().sendPacket(reqPacket);
                client.form = (null);
                client.sendAlert("Form Result Bound To The Server.");
                break;
            }
            case "close": {
                ModalFormResponsePacket reqPacket = new ModalFormResponsePacket();
                reqPacket.setFormId(formData.data.getInteger("id"));
                reqPacket.setFormData(null);
                client.bedrockClient.getSession().sendPacket(reqPacket);
                client.form = (null);
                client.sendAlert("Form Closed.");
                break;
            }
            default: {
                client.sendAlert("`form choose <1/2> - Click button");
                client.sendAlert("`form close - Close the form");
                break;
            }
        }
    }

    private void customForm(Form formData, String[] args, Player client) {
        switch (args[0]) {
            case "input": {
                int index = new Integer(args[1]);
                if (index >= formData.array) {
                    client.sendAlert("[ERROR]Array Outside The Bound Of Array.");
                    return;
                }
                if (!formData.data.getJSONArray("types").getString(index).equals(args[0])) {
                    client.sendAlert("Invalid type.");
                    return;
                }
                formData.data.getJSONArray("values").set(index, args[2]);
                client.sendAlert("OK");
                break;
            }
            case "toggle": {
                int index = new Integer(args[1]);
                if (index >= formData.array) {
                    client.sendAlert("[ERROR]Array Outside The Bound Of Array.");
                    return;
                }
                if (!formData.data.getJSONArray("types").getString(index).equals(args[0])) {
                    client.sendAlert("Invalid type.");
                    return;
                }
                if (args[2].equals("true")) {
                    formData.data.getJSONArray("values").set(index, true);
                } else if (args[2].equals("false")) {
                    formData.data.getJSONArray("values").add(index, false);
                }
                client.sendAlert("OK");
                break;
            }
            case "view-choose": {
                int index = new Integer(args[1]);
                if (index >= formData.array) {
                    client.sendAlert("[ERROR]Array Outside The Bound Of Array.");
                    return;
                }
                if (!formData.data.getJSONArray("types").getString(index).equals("choose")) {
                    client.sendAlert("Invalid type.");
                    return;
                }
                JSONArray contentJson = formData.data.getJSONArray("content").getJSONObject(index).getJSONArray("options");
                StringBuilder result = new StringBuilder();
                for (int i = 0; i < contentJson.size(); i++) {
                    result.append(i).append(" : ").append(contentJson.get(i)).append("\n");
                }
                client.sendAlert("Values(" + contentJson.size() + "):");
                client.sendMessage(result.toString());
                break;
            }
            case "choose": {
                int index = new Integer(args[1]);
                if (index >= formData.array) {
                    client.sendAlert("[ERROR]Array Outside The Bound Of Array.");
                    return;
                }
                if (!formData.data.getJSONArray("types").getString(index).equals(args[0])) {
                    client.sendAlert("Invalid type.");
                    return;
                }
                int choose = new Integer(args[1]);
                formData.data.getJSONArray("values").set(index, choose);
                client.sendAlert("OK");
                break;
            }
            case "slider": {
                int index = new Integer(args[1]);
                if (index >= formData.array) {
                    client.sendAlert("[ERROR]Array Outside The Bound Of Array.");
                    return;
                }
                if (!formData.data.getJSONArray("types").getString(index).equals(args[0])) {
                    client.sendAlert("Invalid type.");
                    return;
                }
                double value = new Double(args[2]);
                formData.data.getJSONArray("values").set(index, value);
                client.sendAlert("OK");
                break;
            }
            case "submit": {
                ModalFormResponsePacket reqPacket = new ModalFormResponsePacket();
                reqPacket.setFormId(formData.data.getInteger("id"));
                reqPacket.setFormData(formData.data.getJSONArray("values").toJSONString());
                client.bedrockClient.getSession().sendPacket(reqPacket);
                client.form = (null);
                client.sendAlert("Form Submitted.");
                break;
            }
            case "value": {
                int index = new Integer(args[1]);
                client.sendAlert("Value of " + args[1] + " is " + formData.data.getJSONArray("values").get(index));
                break;
            }
            case "close": {
                ModalFormResponsePacket reqPacket = new ModalFormResponsePacket();
                reqPacket.setFormId(formData.data.getInteger("id"));
                reqPacket.setFormData(null);
                client.bedrockClient.getSession().sendPacket(reqPacket);
                client.form = (null);
                client.sendAlert("Form Closed.");
                break;
            }
            default: {
                client.sendAlert("Use `form submit to submit the form.");
                client.sendAlert("Use `form value <index> view the value of the form.");
                client.sendAlert("Use `form close to close the window.");
                break;
            }
        }
    }
}
