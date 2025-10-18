package com.example.examplemod.commands;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;

import com.example.examplemod.helpers.EconomyData;
import com.example.examplemod.helpers.ItemHelper;
import com.example.examplemod.helpers.TestCommandHelper;

public class TestBuyCommand extends CommandBase {

    @Override
    public String getCommandName() {
        return "testbuy";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/testbuy <item> <amount> <price>";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (!(sender instanceof EntityPlayer)) return;
        EntityPlayer player = (EntityPlayer) sender;

        if (args.length < 3) {
            player.addChatMessage(new ChatComponentText("Usage: /testbuy <item> <amount> <price>"));
            return;
        }

        String itemName = args[0];
        int amount = TestCommandHelper.parseIntSafe(args[1], 1);
        double price = TestCommandHelper.parseDoubleSafe(args[2], 0);

        // Record purchase correctly
        EconomyData.recordPurchase(itemName, amount, price);

        // Only give test items in singleplayer
        if (TestCommandHelper.isSingleplayer(player)) {
            ItemStack stack = new ItemStack(TestCommandHelper.getItemByName(itemName), amount);
            ItemHelper.giveItemWithPrice(player, stack, price);

            player.addChatMessage(new ChatComponentText(
                    "§aBought " + amount + "x " + itemName + " for " + (price * amount)
            ));
        } else {
            player.addChatMessage(new ChatComponentText(
                    "§aRecorded purchase of " + amount + "x " + itemName + " for " + (price * amount)
            ));
        }
    }
}
