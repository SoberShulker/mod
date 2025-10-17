package com.example.examplemod;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;
import com.example.examplemod.commands.TestBuyCommand;
import com.example.examplemod.commands.TestSellCommand;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

@Mod(modid = ExampleMod.MODID, name = ExampleMod.NAME, version = ExampleMod.VERSION)
public class ExampleMod {

    public static final String MODID = "jumpmod";
    public static final String NAME = "Auto Jump Mod";
    public static final String VERSION = "1.0";

    private static final String TAG_TICK_COUNTER = "jumpmod_tick_counter";

    @SideOnly(Side.CLIENT)
    private KeyBinding openGuiKey;

    @Mod.EventHandler
    public void serverLoad(FMLServerStartingEvent event) {
        event.registerServerCommand(new TestBuyCommand());
        event.registerServerCommand(new TestSellCommand());
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);

        // Register client-side keybinding
        if (event.getSide() == Side.CLIENT) {
            initClientKeybinds();


        }
    }


    @SideOnly(Side.CLIENT)
    private void initClientKeybinds() {
        openGuiKey = new KeyBinding("Open ClickGUI", Keyboard.KEY_R, "Auto Jump Mod");
        ClientRegistry.registerKeyBinding(openGuiKey);
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (openGuiKey.isPressed()) {
            Minecraft.getMinecraft().displayGuiScreen(new ClickGUI());
        }
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        // Only run on client side and at end of tick
        if (!event.player.worldObj.isRemote || event.phase != TickEvent.Phase.END) return;

        EntityPlayer player = event.player;
        Minecraft mc = Minecraft.getMinecraft();
        boolean isSingleplayer = mc.isSingleplayer();

        // Track ticks in player NBT
        int ticks = player.getEntityData().getInteger(TAG_TICK_COUNTER);
        ticks++;

        Module autoJumpModule = ModuleManager.getModuleByName("Auto Jump");
        if (autoJumpModule != null && autoJumpModule.isEnabled()) {
            if (isSingleplayer) {
                // SP: jump every 10 ticks (~0.5s)
                if (ticks >= 10 && player.onGround) {
                    player.jump();
                    player.addChatMessage(new ChatComponentText(
                            EnumChatFormatting.GREEN + "[SP] You jumped!"
                    ));
                    ticks = 0;
                }
            } else {
                // MP: jump every 40 ticks (~2s)
                if (ticks >= 40 && player.onGround) {
                    player.jump();
                    player.addChatMessage(new ChatComponentText(
                            EnumChatFormatting.AQUA + "[MP] You jumped (multiplayer)!"
                    ));
                    ticks = 0;
                }
            }
        }

        player.getEntityData().setInteger(TAG_TICK_COUNTER, ticks);
    }
}
