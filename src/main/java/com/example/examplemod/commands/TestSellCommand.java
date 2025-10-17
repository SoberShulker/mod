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

public class TestSellCommand extends CommandBase {

    @Override
    public String getCommandName() {
        return "testsell";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/testsell <item> <amount> <sellPrice> [perItem=false]";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws NumberInvalidException {
        if (!(sender instanceof EntityPlayer)) return;
        EntityPlayer player = (EntityPlayer) sender;

        if (args.length < 3) {
            player.addChatMessage(new ChatComponentText("Usage: /testsell <item> <amount> <sellPrice> [perItem=false]"));
            return;
        }

        try {
            String itemName = args[0];
            int amount = parseInt(args[1], 1);
            double sellPriceInput = parseDouble(args[2], 0);

            boolean perItem = false;
            if (args.length >= 4) {
                perItem = Boolean.parseBoolean(args[3]);
            }

            // Calculate total sell price
            double totalSellPrice = perItem ? sellPriceInput * amount : sellPriceInput;

            // Remove items from inventory
            Item sellItem = getItemByName(itemName);
            int removed = removeItemsFromInventory(player, sellItem, amount);

            // Record sale
            EconomyData.recordSale(itemName, removed, totalSellPrice);

            // Feedback
            String soldMsg = "Sold " + removed + "x " + itemName;
            if (perItem) soldMsg += " at " + sellPriceInput + " each";
            soldMsg += " for total " + totalSellPrice;
            player.addChatMessage(new ChatComponentText(soldMsg));

        } catch (NumberInvalidException e) {
            player.addChatMessage(new ChatComponentText("Invalid number: " + e.getMessage()));
        }
    }

    private Item getItemByName(String name) {
        Item item = Item.getByNameOrId(name);
        if (item == null) return Item.getItemFromBlock(Blocks.stone);
        return item;
    }

    private int removeItemsFromInventory(EntityPlayer player, Item item, int amount) {
        int removed = 0;
        for (int i = 0; i < player.inventory.mainInventory.length; i++) {
            ItemStack stack = player.inventory.mainInventory[i];
            if (stack != null && stack.getItem() == item) {
                if (stack.stackSize <= (amount - removed)) {
                    removed += stack.stackSize;
                    player.inventory.mainInventory[i] = null;
                } else {
                    stack.stackSize -= (amount - removed);
                    removed = amount;
                }
                if (removed >= amount) break;
            }
        }
        return removed;
    }
}
