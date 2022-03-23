package org.barrelmc.barrel.network.translator.bedrock;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.steveice10.mc.auth.util.Base64;
import com.github.steveice10.mc.protocol.packet.ingame.clientbound.ClientboundCustomPayloadPacket;
import com.nukkitx.protocol.bedrock.BedrockPacket;
import org.barrelmc.barrel.network.data.Form;
import org.barrelmc.barrel.network.translator.interfaces.BedrockPacketTranslator;
import org.barrelmc.barrel.player.Player;

import java.nio.charset.StandardCharsets;

public class ModalFormRequestPacket  implements BedrockPacketTranslator {

    @Override
    public void translate(BedrockPacket pk, Player player) {
        com.nukkitx.protocol.bedrock.packet.ModalFormRequestPacket packet = (com.nukkitx.protocol.bedrock.packet.ModalFormRequestPacket) pk;
        showForm(player, packet.getFormData(), packet.getFormId());
    }

    public void showForm(Player player, String formJson, int formId) {
        JSONObject formJSON = JSONObject.parseObject(formJson);
        formJSON.put("id", formId);
        JSONObject formJ = new JSONObject();
        formJ.put("type", "form");

        formJ.put("data", Base64.decode(formJSON.toJSONString().getBytes(StandardCharsets.UTF_8)));
        player.javaSession.send(new ClientboundCustomPayloadPacket("ModalForm", formJ.toJSONString().getBytes()));
        if (formJSON.getString("type").equals("form")) {
            showSimpleForm(player, formJSON);
        } else if (formJSON.getString("type").equals("custom_form")) {
            showCustomForm(player, formJSON);
        } else if (formJSON.getString("type").equals("modal")) {
            showModalForm(player, formJSON);
        }
    }

    public void showModalForm(Player player, JSONObject formJson) {
        player.form = (new Form(Form.Type.MODAL, 0, formJson));
        player.sendAlert("You Received A Modal FormUI from the server.");
        player.sendMessage("Title:" + formJson.getString("title"));
        player.sendMessage("Content:" + formJson.getString("content"));
        player.sendMessage("Button1:" + formJson.getString("button1"));
        player.sendMessage("Button2:" + formJson.getString("button2"));
        player.sendMessage("\nUse `form choose <1/2> to click button.");
        player.sendMessage("Use `form close to close the window.");
    }

    public void showSimpleForm(Player player, JSONObject formJson) {
        JSONArray buttons = formJson.getJSONArray("buttons");
        player.form = (new Form(Form.Type.SIMPLE, buttons.size(), formJson));
        player.sendAlert("You Received A Simple FormUI from the server.");
        player.sendMessage("Title:" + formJson.getString("title"));
        player.sendMessage("Content:" + formJson.getString("content"));
        player.sendMessage("Buttons:");
        for (int i = 0; i < buttons.size(); i++) {
            player.sendMessage(i + ": " + buttons.getJSONObject(i).getString("text"));
        }
        player.sendMessage("\nUse `form choose <index> to click button.");
        player.sendMessage("Use `form close to close the window.");
    }

    public void showCustomForm(Player player, JSONObject formJson) {
        JSONArray contents = formJson.getJSONArray("content"), defaults = new JSONArray(), types = new JSONArray();
        player.form = (new Form(Form.Type.CUSTOM, contents.size(), formJson));
        player.sendAlert("You Received A Custom FormUI from the server.");
        player.sendMessage("Title:" + formJson.getString("title"));
        for (int i = 0; i < contents.size(); i++) {
            JSONObject singleObject = contents.getJSONObject(i);
            defaults.set(i, singleObject.get("default"));
            StringBuilder message = new StringBuilder(i + " Type:" + singleObject.getString("type") + " Text:" + singleObject.getString("text"));
            switch (singleObject.getString("type")) {
                case "placeholder": {
                    message.append(" Placeholder:").append(singleObject.getString("text"));
                    message.append("\nType `form input ").append(i).append(" <string> to input text");
                    break;
                }
                case "toggle": {
                    message.append("\nType `form toggle ").append(i).append(" <true/false> to change toggle");
                    break;
                }
                case "step_slider": {
                    singleObject.put("options", singleObject.getJSONArray("steps"));
                    singleObject.put("steps", null);
                    break;
                }
                case "dropdown": {
                    message.append("\nType `form view-choose ").append(i).append(" to view options");
                    message.append("\nType `form choose ").append(i).append(" <index> to choose options");
                    singleObject.put("type", "choose");
                    contents.set(i, singleObject);
                    break;
                }
                case "slider": {
                    message.append(" Min:").append(singleObject.getDouble("min")).append(" max:").append(singleObject.getDouble("max")).append(" step:").append(singleObject.getDouble("step"));
                    message.append("\nType `form slider ").append(i).append(" <value> to change value");
                    break;
                }
            }
            types.add(i, singleObject.getString("type"));
            player.sendMessage(message.toString());
        }
        player.sendMessage("\nUse `form submit to submit the form.");
        player.sendMessage("Use `form value <index> view the value of the form.");
        player.sendMessage("Use `form close to close the window.");
        formJson.put("values", defaults);
        formJson.put("types", types);
    }
}