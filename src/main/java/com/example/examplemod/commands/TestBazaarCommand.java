package com.example.examplemod.commands;

import com.example.examplemod.helpers.ItemHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;

public class TestBazaarCommand extends CommandBase {

    @Override
    public String getCommandName() {
        return "testbazaar";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/testbazaar";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc == null || mc.thePlayer == null) return;

        EntityPlayerSP player = mc.thePlayer;

        String simulatedItemName = "diamond"; // example item
        int simulatedAmount = 3;
        double simulatedPrice = 1500.0;

        Item item = Item.getByNameOrId(simulatedItemName);
        if (item == null) {
            player.addChatMessage(new ChatComponentText("TestBazaarCommand: Item not found: " + simulatedItemName));
            return;
        }

        ItemStack stack = new ItemStack(item, simulatedAmount);

        // ALWAYS add client-sided lore
        ItemHelper.addLore(stack, "§aBought via BUY");
        ItemHelper.addLore(stack, "§aPaid: " + ItemHelper.formatCoins(simulatedPrice) + " coins each");
        ItemHelper.addLore(stack, "§7Stack total: " + ItemHelper.formatCoins(simulatedPrice * simulatedAmount) + " coins");

        if (!player.inventory.addItemStackToInventory(stack)) {
            player.dropPlayerItemWithRandomChoice(stack, false);
        }
        player.inventory.markDirty();

        player.addChatMessage(new ChatComponentText(
                "TestBazaarCommand: Added " + simulatedAmount + "x " + simulatedItemName + " with client-sided lore."
        ));
    }
}
