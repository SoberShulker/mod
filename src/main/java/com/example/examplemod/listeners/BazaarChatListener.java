package com.example.examplemod.listeners;

import com.example.examplemod.helpers.BazaarDebugManager;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BazaarChatListener {

    public static boolean bazaarMenuOpen = false;
    public static final List<BazaarPurchase> recentPurchases = new ArrayList<BazaarPurchase>();

    // Regex patterns matching exact Bazaar syntax
    private static final Pattern BUY_PATTERN =
            Pattern.compile("\\[Bazaar\\] Bought (\\d+) (.+) for ([\\d.,]+) coins!");
    private static final Pattern SELL_PATTERN =
            Pattern.compile("\\[Bazaar\\] Sold (\\d+) (.+) for ([\\d.,]+) coins!");
    private static final Pattern BUY_ORDER_PATTERN =
            Pattern.compile("\\[Bazaar\\] Buy Order Setup! (\\d+) (.+) for ([\\d.,]+) coins!");
    private static final Pattern SELL_OFFER_PATTERN =
            Pattern.compile("\\[Bazaar\\] Sell Offer Setup! (\\d+) (.+) for ([\\d.,]+) coins\\.");
    private static final Pattern CLAIM_ORDER_PATTERN =
            Pattern.compile("\\[Bazaar\\] Claimed (\\d+) (.+) worth ([\\d.,]+) bought for ([\\d.,]+) each!");
    private static final Pattern CLAIM_SELL_PATTERN =
            Pattern.compile("\\[Bazaar\\] Claimed ([\\d.,]+) from selling (\\d+) (.+) at ([\\d.,]+) each!");

    @SubscribeEvent
    public void onChatReceived(ClientChatReceivedEvent event) {
        if (event == null || event.message == null) return;

        // Forge 1.8: access the public field directly
        String text = event.message.getUnformattedText();
        Matcher m;

        try {
            if ((m = BUY_PATTERN.matcher(text)).matches()) {
                recordPurchase(m.group(2), Integer.parseInt(m.group(1)), parseCoins(m.group(3)), "BUY");
            } else if ((m = SELL_PATTERN.matcher(text)).matches()) {
                recordPurchase(m.group(2), Integer.parseInt(m.group(1)), parseCoins(m.group(3)), "SELL");
            } else if ((m = BUY_ORDER_PATTERN.matcher(text)).matches()) {
                recordPurchase(m.group(2), Integer.parseInt(m.group(1)), parseCoins(m.group(3)), "BUY_ORDER");
            } else if ((m = SELL_OFFER_PATTERN.matcher(text)).matches()) {
                recordPurchase(m.group(2), Integer.parseInt(m.group(1)), parseCoins(m.group(3)), "SELL_OFFER");
            } else if ((m = CLAIM_ORDER_PATTERN.matcher(text)).matches()) {
                recordPurchase(m.group(2), Integer.parseInt(m.group(1)), parseCoins(m.group(3)), "CLAIMED_ORDER");
            } else if ((m = CLAIM_SELL_PATTERN.matcher(text)).matches()) {
                recordPurchase(m.group(3), Integer.parseInt(m.group(2)), parseCoins(m.group(1)), "CLAIMED_SELL");
            }
        } catch (Exception e) {
            BazaarDebugManager.sendCritical("Error parsing Bazaar message: " + text);
        }
    }

    @SubscribeEvent
    public void onGuiOpen(GuiOpenEvent event) {
        bazaarMenuOpen = event.gui != null &&
                event.gui.getClass().getSimpleName().toLowerCase().contains("bazaar");
    }

    private void recordPurchase(String itemName, int amount, double totalCoins, String type) {
        double perItem = (amount > 0 && totalCoins > 0) ? (totalCoins / amount) : 0;
        BazaarPurchase p = new BazaarPurchase(itemName, amount, perItem, type, totalCoins, System.currentTimeMillis());

        // Only track in debug mode for multiplayer or if relevant
        if (BazaarDebugManager.isDebugEnabled() || type.startsWith("BUY") || type.startsWith("CLAIMED")) {
            recentPurchases.add(p);

            // Remove purchases older than 15 seconds
            for (Iterator<BazaarPurchase> it = recentPurchases.iterator(); it.hasNext();) {
                BazaarPurchase old = it.next();
                if (System.currentTimeMillis() - old.timestamp > 15000L) it.remove();
            }
        }

        BazaarDebugManager.sendDebug("Recorded " + type + ": " + amount + "x " + itemName + " @ " + perItem);
    }

    public static double parseCoins(String s) {
        try {
            return Double.parseDouble(s.replace(",", ""));
        } catch (NumberFormatException e) {
            BazaarDebugManager.sendCritical("Failed to parse coin amount: " + s);
            return 0;
        }
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

        // Convenience constructor
        public BazaarPurchase(String itemName, int remaining, double perItem, String type, long timestamp) {
            this(itemName, remaining, perItem, type, remaining * perItem, timestamp);
        }
    }
}
