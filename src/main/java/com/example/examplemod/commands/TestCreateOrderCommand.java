package com.example.examplemod.commands;

import com.example.examplemod.helpers.TestCommandHelper;
import com.example.examplemod.listeners.BazaarChatListener;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.client.event.ClientChatReceivedEvent;

public class TestCreateOrderCommand extends CommandBase {

    public String getCommandName() {
        return "testcreateorder";
    }

    public String getCommandUsage(ICommandSender sender) {
        return "/testcreateorder <item> <amount> <price>";
    }

    public void processCommand(ICommandSender sender, String[] args) {
        if (!(sender instanceof EntityPlayer)) return;
        EntityPlayer player = (EntityPlayer) sender;

        if (args.length < 3) {
            player.addChatMessage(new ChatComponentText("Usage: /testcreateorder <item> <amount> <price>"));
            return;
        }

        String itemName = args[0];
        int amount = TestCommandHelper.parseIntSafe(args[1], 1);
        double price = TestCommandHelper.parseDoubleSafe(args[2], 0);

        // Bazaar buy order setup message
        final String bazaarText = String.format("[Bazaar] Buy Order Setup! %d %s for %,.2f coins!", amount, itemName, price * amount);

        ClientChatReceivedEvent fakeEvent = new ClientChatReceivedEvent((byte)0, new ChatComponentText(bazaarText));
        BazaarChatListener listener = new BazaarChatListener();
        listener.onChatReceived(fakeEvent);

        player.addChatMessage(new ChatComponentText(
                "§aCreated fake buy order: " + amount + "x " + itemName + " for " + String.format("%,.2f", price * amount)
        ));
    }
}
