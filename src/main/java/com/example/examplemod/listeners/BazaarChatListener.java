package com.example.examplemod.listeners;

import com.example.examplemod.helpers.ItemHelper;
import com.example.examplemod.helpers.BazaarDebugManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Handles Bazaar chat messages and applies client-sided lore to purchased items immediately.
 */
public class BazaarChatListener {

    // Track recent purchases for profit calculation
    public static final List<BazaarPurchase> allPurchases = new ArrayList<BazaarPurchase>();

    // Patterns for various Bazaar messages (case-insensitive)
    private static final LinkedHashMap<Pattern, String> PATTERNS = new LinkedHashMap<Pattern, String>();

    static {
        PATTERNS.put(Pattern.compile("(?i)\\[Bazaar\\]\\s*Bought\\s+(\\d+)\\s+(.+?)\\s+for\\s+([\\d,.]+)\\s*coins\\!?"), "BUY");
        PATTERNS.put(Pattern.compile("(?i)\\[Bazaar\\]\\s*Sold\\s+(\\d+)\\s+(.+?)\\s+for\\s+([\\d,.]+)\\s*coins\\!?"), "SELL");
        PATTERNS.put(Pattern.compile("(?i)\\[Bazaar\\]\\s*Buy Order Setup!?\\s*(\\d+)\\s+(.+?)\\s+for\\s+([\\d,.]+)\\s*coins\\!?"), "BUY_ORDER");
        PATTERNS.put(Pattern.compile("(?i)\\[Bazaar\\]\\s*Sell Offer Setup!?\\s*(\\d+)\\s+(.+?)\\s+for\\s+([\\d,.]+)\\s*coins\\.?"), "SELL_OFFER");
        PATTERNS.put(Pattern.compile("(?i)\\[Bazaar\\]\\s*Claimed\\s+(\\d+)\\s+(.+?)\\s+worth\\s+([\\d,.]+)\\s+bought for\\s+([\\d,.]+)\\s*each\\!?"), "CLAIMED_ORDER");
        PATTERNS.put(Pattern.compile("(?i)\\[Bazaar\\]\\s*Claimed\\s+([\\d,.]+)\\s+from selling\\s+(\\d+)\\s+(.+?)\\s+at\\s+([\\d,.]+)\\s*each\\!?"), "CLAIMED_SELL");
    }

    @SubscribeEvent
    public void onChatReceived(ClientChatReceivedEvent event) {
        if (event == null || event.message == null) return;

        String msg = event.message.getUnformattedText();
        Matcher m;

        for (Map.Entry<Pattern, String> entry : PATTERNS.entrySet()) {
            m = entry.getKey().matcher(msg);
            if (m.find()) {
                processPurchase(m, entry.getValue());
                break;
            }
        }
    }

    private void processPurchase(Matcher m, String type) {
        String itemName;
        int amount;
        double totalCoins;

        try {
            if ("BUY".equals(type) || "SELL".equals(type) || "BUY_ORDER".equals(type) || "SELL_OFFER".equals(type)) {
                amount = Integer.parseInt(m.group(1));
                itemName = m.group(2);
                totalCoins = parseCoins(m.group(3));
            } else if ("CLAIMED_ORDER".equals(type)) {
                amount = Integer.parseInt(m.group(1));
                itemName = m.group(2);
                totalCoins = parseCoins(m.group(3));
            } else if ("CLAIMED_SELL".equals(type)) {
                amount = Integer.parseInt(m.group(2));
                itemName = m.group(3);
                totalCoins = parseCoins(m.group(1));
            } else return;

            double perItem = (amount > 0 && totalCoins > 0) ? (totalCoins / amount) : 0;
            BazaarPurchase purchase = new BazaarPurchase(itemName, amount, perItem, type, totalCoins, System.currentTimeMillis());
            allPurchases.add(purchase);

            if (BazaarDebugManager.isDebugEnabled()) {
                BazaarDebugManager.sendDebug("Detected Bazaar " + type + ": " + amount + "x " + itemName + " @ " + perItem);
            }

            // Apply lore immediately to items in inventory
            EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
            if (player != null) {
                for (int i = 0; i < player.inventory.mainInventory.length; i++) {
                    ItemStack stack = player.inventory.mainInventory[i];
                    if (stack != null && stack.getDisplayName().toLowerCase().contains(itemName.toLowerCase())) {
                        ItemHelper.addLore(stack, "§aBought via " + type.replace("_", " "));
                        ItemHelper.addLore(stack, "§aPaid: " + ItemHelper.formatCoins(perItem) + " coins each");
                        ItemHelper.addLore(stack, "§7Stack total: " + ItemHelper.formatCoins(perItem * stack.stackSize));

                        if (BazaarDebugManager.isDebugEnabled()) {
                            BazaarDebugManager.sendDebug("Added lore to " + stack.stackSize + "x " + itemName);
                        }
                        break; // Only first matching stack
                    }
                }
            }

        } catch (NumberFormatException e) {
            BazaarDebugManager.sendCritical("Failed to parse Bazaar purchase numbers: " + m.group());
        }
    }

    public static double parseCoins(String s) {
        try {
            return Double.parseDouble(s.replace(",", "").replace("\u00A0", ""));
        } catch (NumberFormatException e) {
            BazaarDebugManager.sendCritical("Failed to parse coins: " + s);
            return 0;
        }
    }

    // Profit tracking
    public static double getTotalSpent() {
        double spent = 0;
        for (BazaarPurchase p : allPurchases) {
            if (p.type.startsWith("BUY") || p.type.equals("CLAIMED_ORDER")) spent += p.price;
        }
        return spent;
    }

    public static double getTotalEarned() {
        double earned = 0;
        for (BazaarPurchase p : allPurchases) {
            if (p.type.startsWith("SELL") || p.type.equals("CLAIMED_SELL")) earned += p.price;
        }
        return earned;
    }

    public static double getTotalProfit() {
        return getTotalEarned() - getTotalSpent();
    }

    public static class BazaarPurchase {
        public String itemName;
        public int remaining;
        public double perItem;
        public String type;
        public long timestamp;
        public double price;

        public BazaarPurchase(String itemName, int remaining, double perItem, String type, double price, long timestamp) {
            this.itemName = itemName;
            this.remaining = remaining;
            this.perItem = perItem;
            this.type = type;
            this.price = price;
            this.timestamp = timestamp;
        }
    }
}
