package com.example.examplemod.commands;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;

import com.example.examplemod.listeners.BazaarChatListener;

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

        // Use BazaarChatListener's allPurchases to get totals
        double totalSpent = BazaarChatListener.getTotalSpent();
        double totalEarned = BazaarChatListener.getTotalEarned();
        double profit = BazaarChatListener.getTotalProfit();
        // Send messages to player
        player.addChatMessage(new ChatComponentText("§aTotal Spent: " + String.format("%,.2f", totalSpent) + " coins"));
        player.addChatMessage(new ChatComponentText("§aTotal Earned: " + String.format("%,.2f", totalEarned) + " coins"));
        player.addChatMessage(new ChatComponentText("§aTotal Profit: " + String.format("%,.2f", profit) + " coins"));
    }
}
