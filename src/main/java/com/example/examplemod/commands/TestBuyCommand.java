package com.example.examplemod.commands;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;

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

        // Total cost
        double totalCost = price * amount;

        // Format total for Hypixel-style chat (commas and dot)
        String formattedTotal = String.format("%,.2f", totalCost);

        // Generate the Hypixel-style Bazaar message
        String bazaarMsg = "[Bazaar] Bought " + amount + " " + itemName + " for " + formattedTotal + " coins!";

        // Send message so BazaarChatListener picks it up
        player.addChatMessage(new ChatComponentText(bazaarMsg));

        // Give item only in singleplayer
        if (TestCommandHelper.isSingleplayer(player)) {
            ItemStack stack = new ItemStack(TestCommandHelper.getItemByName(itemName), amount);
            ItemHelper.giveItemWithPrice(player, stack, price);

            // Also add lore to reflect total purchase
            ItemHelper.addLore(stack, "§aBought via TESTBUY");
            ItemHelper.addLore(stack, "§aPaid: " + String.format("%,.2f", price) + " coins each");
            ItemHelper.addLore(stack, "§7Stack total: " + formattedTotal + " coins");
        }
    }
}
