package com.example.examplemod.modules.misc;

import com.example.examplemod.gui.Module;
import com.example.examplemod.gui.Category;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

public class AutoJump extends Module {

    private static final String TAG_TICK_COUNTER = "autojump_tick_counter";

    public AutoJump() {
        super("Auto Jump", true, Category.MISC);
    }

    // Renamed from onTick to tick
    public void tick(EntityPlayer player) {
        Minecraft mc = Minecraft.getMinecraft();
        boolean isSingleplayer = mc.isSingleplayer();

        int ticks = player.getEntityData().getInteger(TAG_TICK_COUNTER);
        ticks++;

        if (isSingleplayer) {
            if (ticks >= 10 && player.onGround) { // ~0.5s
                player.jump();
                player.addChatMessage(new ChatComponentText(
                        EnumChatFormatting.GREEN + "[SP] You jumped!"
                ));
                ticks = 0;
            }
        } else {
            if (ticks >= 40 && player.onGround) { // ~2s
                player.jump();
                player.addChatMessage(new ChatComponentText(
                        EnumChatFormatting.AQUA + "[MP] You jumped (multiplayer)!"
                ));
                ticks = 0;
            }
        }

        player.getEntityData().setInteger(TAG_TICK_COUNTER, ticks);
    }
}
