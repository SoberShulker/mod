package com.example.examplemod.helpers;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.ChatComponentText;

public class TestCommandHelper {

    public static boolean isSingleplayer(EntityPlayer player) {
        if (!Minecraft.getMinecraft().isSingleplayer()) {
            player.addChatMessage(new ChatComponentText("§cThis command is disabled in multiplayer."));
            return false;
        }
        return true;
    }

    public static Item getItemByName(String name) {
        Item item = Item.getByNameOrId(name);
        if (item == null) return Item.getItemFromBlock(Blocks.stone);
        return item;
    }

    public static Integer parseIntSafe(String s, int defaultValue) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public static Double parseDoubleSafe(String s, double defaultValue) {
        try {
            return Double.parseDouble(s);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}
