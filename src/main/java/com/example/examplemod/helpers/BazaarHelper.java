package com.example.examplemod.helpers;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Handles client-side Bazaar tracking and applies "Paid: X coins" lore.
 * Safe for both singleplayer and Hypixel SkyBlock.
 */
public class BazaarHelper {

    // Pattern for Hypixel Bazaar purchase messages
    private static final Pattern BAZAAR_BUY_PATTERN = Pattern.compile(
            "You bought (\\d+) ([A-Z_]+) for ([0-9,.]+) coins!"
    );

    @SubscribeEvent
    public void onChatReceived(ClientChatReceivedEvent event) {
        String msg = event.message.getUnformattedText();

        Matcher matcher = BAZAAR_BUY_PATTERN.matcher(msg);
        if (!matcher.find()) return;

        try {
            int amount = Integer.parseInt(matcher.group(1));
            String itemName = matcher.group(2);
            double price = Double.parseDouble(matcher.group(3).replace(",", ""));

            EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
            if (player == null) return;

            Item item = Item.getByNameOrId(itemName);
            if (item == null) return;

            ItemStack stack = new ItemStack(item, amount);

            // Add client-sided lore and track purchase
            ItemHelper.giveItemWithPrice(player, stack, price);

        } catch (NumberFormatException e) {
            // Ignore invalid numbers
        }
    }
}
