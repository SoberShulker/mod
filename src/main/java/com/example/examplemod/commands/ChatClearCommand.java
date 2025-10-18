package com.example.examplemod.commands;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;

public class ChatClearCommand extends CommandBase {

    @Override
    public String getCommandName() {
        return "clear";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/clear - clears your chat :3";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        Minecraft mc = Minecraft.getMinecraft();
        if (sender instanceof EntityPlayer) {
            if (mc != null) {
                mc.ingameGUI.getChatGUI().clearChatMessages();
            }
        }

    }
}
