package com.example.examplemod;

import com.example.examplemod.commands.*;
import com.example.examplemod.helpers.BazaarHelper;
import com.example.examplemod.gui.ClickGUI;
import com.example.examplemod.gui.Module;
import com.example.examplemod.gui.ModuleManager;
import com.example.examplemod.modules.misc.AutoJump;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;
import com.example.examplemod.listeners.BazaarChatListener;
import com.example.examplemod.listeners.InventoryChangeListener;

@Mod(modid = ExampleMod.MODID, name = ExampleMod.NAME, version = ExampleMod.VERSION)
public class ExampleMod {

    public static final String MODID = "jumpmod";
    public static final String NAME = "Auto Jump Mod";
    public static final String VERSION = "1.0";

    @SideOnly(Side.CLIENT)
    private KeyBinding openGuiKey;

    @Mod.EventHandler
    public void serverLoad(FMLServerStartingEvent event) {
        event.registerServerCommand(new TestBuyCommand());
        event.registerServerCommand(new TestSellCommand());
        event.registerServerCommand(new TotalProfitCommand());
        event.registerServerCommand(new ChatClearCommand());
        event.registerServerCommand(new OpenGUICommand());
        event.registerServerCommand(new TestBazaarMsgCommand());
        event.registerServerCommand(new TestClaimOrderCommand());
        event.registerServerCommand(new ToggleBazaarDebugCommand());
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new BazaarHelper());
        MinecraftForge.EVENT_BUS.register(new BazaarChatListener());
        MinecraftForge.EVENT_BUS.register(new InventoryChangeListener());

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
        if (!event.player.worldObj.isRemote || event.phase != TickEvent.Phase.END) return;

        EntityPlayer player = event.player;

        // Tick enabled modules individually
        for (Module m : ModuleManager.modules) {
            if (m.isEnabled() && m instanceof AutoJump) {
                ((AutoJump) m).tick(player);
            }
            // Future modules can be handled similarly
        }
    }
}
