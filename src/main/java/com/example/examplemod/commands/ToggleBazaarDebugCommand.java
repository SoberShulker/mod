package com.example.examplemod.commands;

import com.example.examplemod.helpers.BazaarDebugManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;

public class ToggleBazaarDebugCommand extends CommandBase {

    @Override
    public String getCommandName() {
        return "togglebazaardebug";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/togglebazaardebug";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {

        // Always send feedback server-side
        sender.addChatMessage(new ChatComponentText("§aToggling Bazaar debug mode..."));

        // Only run client-side
        if (!sender.getEntityWorld().isRemote) return;

        EntityPlayerSP player = (EntityPlayerSP) sender;
        if (player == null) return;

        // Toggle debug mode
        BazaarDebugManager.toggleDebug();
    }
}