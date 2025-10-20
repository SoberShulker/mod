package com.example.examplemod.commands;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;

import com.example.examplemod.helpers.BazaarDebugManager;

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
        if (!(sender instanceof EntityPlayer)) return;
        BazaarDebugManager.toggleDebug();
    }
}
