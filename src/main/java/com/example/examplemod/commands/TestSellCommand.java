package com.example.examplemod.commands;

import com.example.examplemod.helpers.EconomyData;
import com.example.examplemod.helpers.TestCommandHelper;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;

public class TestSellCommand extends CommandBase {

    @Override
    public String getCommandName() {
        return "testsell";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/testsell <item> <amount> <price>";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (!(sender instanceof EntityPlayer)) return;
        EntityPlayer player = (EntityPlayer) sender;

        if (!TestCommandHelper.isSingleplayer(player)) return;

        if (args.length < 3) {
            player.addChatMessage(new ChatComponentText("Usage: /testsell <item> <amount> <price>"));
            return;
        }

        String itemName = args[0];
        int requestedAmount = TestCommandHelper.parseIntSafe(args[1], 1);
        double price = TestCommandHelper.parseDoubleSafe(args[2], 0);
        ItemStack itemToSell = new ItemStack(TestCommandHelper.getItemByName(itemName));

        // Count total available items
        int totalAvailable = 0;
        for (ItemStack stack : player.inventory.mainInventory) {
            if (stack != null && stack.getItem() == itemToSell.getItem()) {
                totalAvailable += stack.stackSize;
            }
        }

        if (totalAvailable == 0) {
            player.addChatMessage(new ChatComponentText("§cYou do not have any " + itemName + " to sell."));
            return;
        }

        // Limit amount to total available
        int amountToSell = Math.min(requestedAmount, totalAvailable);

        int removedCount = 0;
        for (int i = 0; i < player.inventory.mainInventory.length && removedCount < amountToSell; i++) {
            ItemStack stack = player.inventory.mainInventory[i];
            if (stack == null || stack.getItem() != itemToSell.getItem()) continue;

            int stackSize = stack.stackSize;
            int needed = amountToSell - removedCount;

            if (stackSize <= needed) {
                removedCount += stackSize;
                player.inventory.mainInventory[i] = null;
            } else {
                stack.stackSize -= needed;
                removedCount += needed;
            }
        }

        EconomyData.recordSale(itemName, removedCount, price);

        player.addChatMessage(new ChatComponentText(
                "§aSold " + removedCount + "x " + itemName + " for " + (price * removedCount)
        ));
    }
}
