package com.example.examplemod.commands;

import com.example.examplemod.gui.ClickGUI;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;

public class OpenGUICommand extends CommandBase {

    @Override
    public String getCommandName() {
        return "opengui";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/opengui 0, 1";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        if (sender instanceof EntityPlayer) {
            Minecraft mc = Minecraft.getMinecraft();
            mc.displayGuiScreen(new ClickGUI());
        // cursor sometimes disappears, this fixes that
        if (mc.currentScreen != null) {
            mc.mouseHelper.grabMouseCursor();
            mc.setIngameFocus();
            }
        }
    }
}

