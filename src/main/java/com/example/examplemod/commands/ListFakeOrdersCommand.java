package com.example.examplemod.commands;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;

import com.example.examplemod.listeners.BazaarChatListener;

import java.util.List;

public class ListFakeOrdersCommand extends CommandBase {

    @Override
    public String getCommandName() {
        return "listfakeorders";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/listfakeorders - Lists all active fake buy orders";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (!(sender instanceof EntityPlayer)) return;
        EntityPlayer player = (EntityPlayer) sender;

        List orders = BazaarChatListener.recentPurchases;
        if (orders.isEmpty()) {
            player.addChatMessage(new ChatComponentText("§cNo active fake orders!"));
            return;
        }

        player.addChatMessage(new ChatComponentText("§aActive Fake Orders:"));

        for (int i = 0; i < orders.size(); i++) {
            Object o = orders.get(i);
            if (!(o instanceof BazaarChatListener.BazaarPurchase)) continue;
            BazaarChatListener.BazaarPurchase order = (BazaarChatListener.BazaarPurchase) o;
            if (!"BUY_ORDER".equals(order.type)) continue;

            String line = String.format(
                    "%d) %dx %s | Per-item: %,.2f | Total: %,.2f",
                    i + 1,
                    order.remaining,
                    order.itemName,
                    order.perItem,
                    order.perItem * order.remaining
            );

            player.addChatMessage(new ChatComponentText(line));
        }
    }
}
