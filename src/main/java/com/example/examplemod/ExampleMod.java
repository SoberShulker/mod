package com.example.examplemod;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

@Mod(modid = ExampleMod.MODID, name = ExampleMod.NAME, version = ExampleMod.VERSION)
public class ExampleMod {
    public static final String MODID = "jumpmod";
    public static final String NAME = "Auto Jump Mod";
    public static final String VERSION = "1.0";

    private int tickCounter = 0;

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            tickCounter++;

            if (tickCounter >= 10) { // every 10 ticks
                EntityPlayer player = event.player;

                if (player.onGround) {
                    player.jump();
                    player.addChatMessage(new ChatComponentText("You jumped!"));
                }

                tickCounter = 0;
            }
        }
    }
}
