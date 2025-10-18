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

        double totalProfit = 0.0;

        for (String keyObj : EconomyData.totalSellMap.keySet()) {

            Double totalSoldPrice = EconomyData.totalSellMap.get(keyObj);
            if (totalSoldPrice == null) totalSoldPrice = 0.0;

            Double purchasePricePerUnit = EconomyData.purchasePriceMap.get(keyObj);
            if (purchasePricePerUnit == null) purchasePricePerUnit = 0.0;

            Integer amountSold = EconomyData.totalSoldAmountMap.get(keyObj);
            if (amountSold == null) amountSold = 0;

            double totalCost = purchasePricePerUnit * amountSold;
            double profit = totalSoldPrice - totalCost;

            totalProfit += profit;

            player.addChatMessage(new ChatComponentText(
                    keyObj + ": Sold " + amountSold + " units, Profit = " + profit
            ));
        }

        player.addChatMessage(new ChatComponentText(
                "Total Profit: " + totalProfit
        ));
    }
}
