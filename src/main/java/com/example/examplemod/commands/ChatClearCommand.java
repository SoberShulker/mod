package com.example.examplemod.commands;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiNewChat;

public class ChatClearCommand extends CommandBase {

    @Override
    public String getCommandName() {
        return "clearchat";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/clearchat - clears your chat :3";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        Minecraft mc = Minecraft.getMinecraft();
        GuiNewChat chatGUI = mc.ingameGUI.getChatGUI();
        if (sender instanceof EntityPlayer) {
            if (mc != null) {
                mc.ingameGUI.getChatGUI().clearChatMessages();
            }
        }

    }
}
