package com.example.examplemod.commands;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;

import com.example.examplemod.helpers.EconomyData;

public class TotalProfitCommand extends CommandBase {

    @Override
    public String getCommandName() {
        return "totalprofit";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/totalprofit";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (!(sender instanceof EntityPlayer)) return;
        EntityPlayer player = (EntityPlayer) sender;

        double totalSpent = EconomyData.getTotalSpent();
        double totalEarned = EconomyData.getTotalEarned();
        double profit = EconomyData.getTotalProfit();

        player.addChatMessage(new ChatComponentText("§aTotal Spent: " + totalSpent));
        player.addChatMessage(new ChatComponentText("§aTotal Earned: " + totalEarned));
        player.addChatMessage(new ChatComponentText("§aTotal Profit: " + profit));
    }
}
