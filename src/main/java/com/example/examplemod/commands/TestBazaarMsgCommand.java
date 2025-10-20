package com.example.examplemod.commands;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;

import com.example.examplemod.helpers.ItemHelper;
import com.example.examplemod.helpers.BazaarDebugManager;
import com.example.examplemod.listeners.BazaarChatListener;

public class TestBazaarMsgCommand extends CommandBase {

    @Override
    public String getCommandName() {
        return "testbazaarmsg";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/testbazaarmsg <item> <amount> <price>";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (!(sender instanceof EntityPlayer)) return;
        EntityPlayer player = (EntityPlayer) sender;

        if (args.length < 3) {
            player.addChatMessage(new ChatComponentText(getCommandUsage(sender)));
            return;
        }

        String itemName = args[0];
        int amount = parseIntSafe(args[1], 1);
        double price = parseDoubleSafe(args[2], 0);

        // Add to Bazaar recent purchases directly
        BazaarChatListener.BazaarPurchase purchase =
                new BazaarChatListener.BazaarPurchase(itemName, amount, price, "BUY", System.currentTimeMillis());
        BazaarChatListener.recentPurchases.add(purchase);

        // Create item and add lore
        ItemStack stack = new ItemStack(ItemHelper.getItemByName(itemName));
        stack.stackSize = amount;
        ItemHelper.addLore(stack, "§aBought via TEST");
        ItemHelper.addLore(stack, "§aPaid: " + String.format("%,.2f", price) + " coins each");
        player.inventory.addItemStackToInventory(stack);

        BazaarDebugManager.sendDebug("TestBazaarMsg: " + amount + "x " + itemName + " at " + price + " coins each");
    }

    private int parseIntSafe(String s, int fallback) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return fallback;
        }
    }

    private double parseDoubleSafe(String s, double fallback) {
        try {
            return Double.parseDouble(s);
        } catch (NumberFormatException e) {
            return fallback;
        }
    }
}
