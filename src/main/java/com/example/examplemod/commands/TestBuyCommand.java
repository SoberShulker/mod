package com.example.examplemod.commands;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.NumberInvalidException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.init.Blocks;

import com.example.examplemod.helpers.EconomyData;

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

        try {
            String itemName = args[0];
            int amount = parseInt(args[1], 1);
            double price = parseDouble(args[2], 0);

            // Record purchase
            EconomyData.recordPurchase(itemName, price);

            // Give the player the item
            ItemStack stack = new ItemStack(getItemByName(itemName), amount);
            player.inventory.addItemStackToInventory(stack);

            player.addChatMessage(new ChatComponentText(
                    "Bought " + amount + "x " + itemName + " for " + price
            ));

        } catch (NumberInvalidException e) {
            player.addChatMessage(new ChatComponentText("Invalid number: " + e.getMessage()));
        }
    }

    private Item getItemByName(String name) {
        Item item = Item.getByNameOrId(name);
        if (item == null) return Item.getItemFromBlock(Blocks.stone);
        return item;
    }
}
