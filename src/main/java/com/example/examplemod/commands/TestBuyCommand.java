package com.example.examplemod.commands;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.NumberInvalidException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.init.Blocks;

import java.util.HashMap;
import java.util.Map;

/**
 * Singleplayer test command to simulate buying items and tracking purchase price.
 * Usage: /testbuy <item> <amount> <price>
 */
public class TestBuyCommand extends CommandBase {

    // Store prices per item (key = item name)
    public static Map<String, Double> priceMap = new HashMap<String, Double>();

    @Override
    public String getCommandName() {
        return "testbuy";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/testbuy <item> <amount> <price>";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws NumberInvalidException {
        if (!(sender instanceof EntityPlayer)) return;

        EntityPlayer player = (EntityPlayer) sender;

        if (args.length < 3) {
            player.addChatMessage(new ChatComponentText("Usage: /testbuy <item> <amount> <price>"));
            return;
        }

        try {
            String itemName = args[0];
            int amount = parseInt(args[1], 1);        // defaults to 1 if invalid
            double price = parseDouble(args[2], 0);  // defaults to 0 if invalid

            // Store the "purchase price"
            priceMap.put(itemName, price);

            // Give the player the item for testing
            ItemStack stack = new ItemStack(getItemByName(itemName), amount);
            player.inventory.addItemStackToInventory(stack);

            player.addChatMessage(new ChatComponentText(
                    "Bought " + amount + "x " + itemName + " for " + price
            ));

        } catch (NumberInvalidException e) {
            player.addChatMessage(new ChatComponentText("Invalid number: " + e.getMessage()));
        }
    }

    /**
     * Maps a string to an Item. Falls back to stone if the item is not found.
     */
    private Item getItemByName(String name) {
        Item item = Item.getByNameOrId(name);
        if (item == null) {
            return Item.getItemFromBlock(Blocks.stone);
        }
        return item;
    }
}
