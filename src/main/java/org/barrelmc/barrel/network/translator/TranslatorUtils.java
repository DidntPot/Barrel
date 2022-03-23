/*
 * Copyright (c) 2021 BarrelMC Team
 * This project is licensed under the MIT License
 */

package org.barrelmc.barrel.network.translator;

import com.github.steveice10.mc.protocol.data.game.entity.player.GameMode;
import com.github.steveice10.mc.protocol.data.game.setting.Difficulty;
import com.nukkitx.protocol.bedrock.data.GameType;

public class TranslatorUtils {

    public static GameMode translateGamemodeToJE(GameType gameType) {
        String gameTypeString = gameType.toString();

        if (gameTypeString.contains("VIEWER")) {
            return GameMode.SPECTATOR;
        }

        return GameMode.valueOf(gameTypeString);
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

    public static String translateDimensionToJE(int bedrockDimension) {
        switch (bedrockDimension) {
            case 0:
                return "minecraft:overworld";
            case 1:
                return "minecraft:the_nether";
            case 2:
                return "minecraft:the_end";
            default:
                return "minecraft:overworld";
        }
    }
}
