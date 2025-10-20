package com.example.examplemod.helpers;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;

public class BazaarDebugManager {

    private static boolean debugEnabled = false;

    public static boolean isDebugEnabled() {
        return debugEnabled;
    }

    public static void toggleDebug() {
        debugEnabled = !debugEnabled;
        sendCritical("§b[Bazaar Debug] §fDebug mode is now " + (debugEnabled ? "§aENABLED" : "§cDISABLED"));
    }

    public static void sendDebug(String msg) {
        if (!debugEnabled) return;
        sendMessage("§b[Bazaar Debug] §7" + msg);
    }

    public static void sendCritical(String msg) {
        sendMessage("§b[Bazaar] §f" + msg);
    }

    private static void sendMessage(String msg) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc != null && mc.thePlayer != null) {
            mc.thePlayer.addChatMessage(new ChatComponentText(msg));
        }
    }
}
