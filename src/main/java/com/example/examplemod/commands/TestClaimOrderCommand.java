package com.example.examplemod.commands;

import com.example.examplemod.helpers.ItemHelper;
import com.example.examplemod.helpers.TestCommandHelper;
import com.example.examplemod.listeners.BazaarChatListener;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.client.event.ClientChatReceivedEvent;

public class TestClaimOrderCommand extends CommandBase {

    public String getCommandName() {
        return "testclaimorder";
    }

    public String getCommandUsage(ICommandSender sender) {
        return "/testclaimorder <item> <amount> <price>";
    }

    public void processCommand(ICommandSender sender, String[] args) {
        if (!(sender instanceof EntityPlayer)) return;
        EntityPlayer player = (EntityPlayer) sender;

        if (args.length < 3) {
            player.addChatMessage(new ChatComponentText("Usage: /testclaimorder <item> <amount> <price>"));
            return;
        }

        String itemName = args[0];
        int amount = TestCommandHelper.parseIntSafe(args[1], 1);
        double price = TestCommandHelper.parseDoubleSafe(args[2], 0);

        // Bazaar claimed buy order message
        final String bazaarText = String.format("[Bazaar] Claimed %d %s worth %,.2f bought for %,.2f each!", amount, itemName, price * amount, price);

        ClientChatReceivedEvent fakeEvent = new ClientChatReceivedEvent((byte)0, new ChatComponentText(bazaarText));
        BazaarChatListener listener = new BazaarChatListener();
        listener.onChatReceived(fakeEvent);

        // Only give items in singleplayer
        if (TestCommandHelper.isSingleplayer(player)) {
            ItemStack stack = new ItemStack(TestCommandHelper.getItemByName(itemName), amount);
            ItemHelper.giveItemWithPrice(player, stack, price);

            player.addChatMessage(new ChatComponentText(
                    "§aClaimed " + amount + "x " + itemName + " for " + String.format("%,.2f", price * amount)
            ));
        } else {
            player.addChatMessage(new ChatComponentText(
                    "§aRecorded claim of " + amount + "x " + itemName + " for " + String.format("%,.2f", price * amount)
            ));
        }
    }
}
