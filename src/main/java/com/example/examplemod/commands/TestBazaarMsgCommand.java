package com.example.examplemod.commands;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import com.example.examplemod.helpers.ItemHelper;
import com.example.examplemod.helpers.BazaarDebugManager;
import com.example.examplemod.listeners.BazaarChatListener;

/**
 * Simulates a Bazaar instant buy for testing.
 * Gives the player an item with lore showing the purchase price.
 * Forge 1.8 + Java 6 compatible.
 */
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

        // Create and register a fake Bazaar purchase
        BazaarChatListener.BazaarPurchase purchase =
                new BazaarChatListener.BazaarPurchase(itemName, amount, price, "BUY", System.currentTimeMillis());
        BazaarChatListener.recentPurchases.add(purchase);

        // Get the item by name
        ItemStack stack = new ItemStack(ItemHelper.getItemByName(itemName));
        stack.stackSize = amount;

        // Ensure proper NBT setup
        if (!stack.hasTagCompound()) stack.setTagCompound(new NBTTagCompound());
        if (!stack.getTagCompound().hasKey("display")) {
            stack.getTagCompound().setTag("display", new NBTTagCompound());
        }
        if (!stack.getTagCompound().getCompoundTag("display").hasKey("Lore")) {
            stack.getTagCompound().getCompoundTag("display").setTag("Lore", new NBTTagList());
        }

        // Add lore lines
        ItemHelper.addLore(stack, "§aBought via TEST");
        ItemHelper.addLore(stack, "§aPaid: " + String.format("%,.2f", price) + " coins each");

        // Give the item and sync
        player.inventory.addItemStackToInventory(stack);
        player.inventory.markDirty();
        player.openContainer.detectAndSendChanges();

        BazaarDebugManager.sendDebug("TestBazaarMsg: " + amount + "x " + itemName + " at " + price + " coins each");
        player.addChatMessage(new ChatComponentText("§aGave test Bazaar item: " + amount + "x " + itemName));
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
