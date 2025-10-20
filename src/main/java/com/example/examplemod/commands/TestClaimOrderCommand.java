package com.example.examplemod.commands;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;

import com.example.examplemod.helpers.ItemHelper;
import com.example.examplemod.helpers.BazaarDebugManager;
import com.example.examplemod.listeners.BazaarChatListener;

import java.util.Iterator;

public class TestClaimOrderCommand extends CommandBase {

    @Override
    public String getCommandName() {
        return "testclaimorder";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/testclaimorder <item>";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (!(sender instanceof EntityPlayer)) return;
        EntityPlayer player = (EntityPlayer) sender;

        if (args.length < 1) {
            player.addChatMessage(new ChatComponentText(getCommandUsage(sender)));
            return;
        }

        String itemName = args[0];
        boolean claimedAny = false;

        for (Iterator it = BazaarChatListener.recentPurchases.iterator(); it.hasNext();) {
            BazaarChatListener.BazaarPurchase purchase = (BazaarChatListener.BazaarPurchase) it.next();

            if (!purchase.itemName.equalsIgnoreCase(itemName)) continue;
            if (!purchase.type.equals("BUY_ORDER") && !purchase.type.equals("CLAIMED_ORDER")) continue;

            // Give item to player
            ItemStack stack = new ItemStack(ItemHelper.getItemByName(itemName));
            int amt = purchase.remaining;
            stack.stackSize = amt;
            ItemHelper.addLore(stack, "§aClaimed from BUY_ORDER");
            ItemHelper.addLore(stack, "§aPaid: " + String.format("%,.2f", purchase.perItem) + " coins each");

            player.inventory.addItemStackToInventory(stack);

            BazaarDebugManager.sendDebug("Claimed " + amt + "x " + itemName);
            purchase.remaining = 0;
            it.remove();
            claimedAny = true;
            break; // only claim one purchase at a time
        }

        if (!claimedAny) {
            player.addChatMessage(new ChatComponentText("No pending buy order found for: " + itemName));
        }
    }
}
