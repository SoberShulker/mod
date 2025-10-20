package com.example.examplemod.listeners;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraft.util.IChatComponent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.example.examplemod.helpers.BazaarDebugManager;

public class BazaarChatListener {

    public static boolean bazaarMenuOpen = false;

    public static final List<BazaarPurchase> recentPurchases = new ArrayList<BazaarPurchase>();

    private static final Pattern BUY_PATTERN =
            Pattern.compile("\\[Bazaar\\] Bought (\\d+) (.+) for ([\\d.,]+) coins!");
    private static final Pattern SELL_PATTERN =
            Pattern.compile("\\[Bazaar\\] Sold (\\d+) (.+) for ([\\d.,]+) coins!");
    private static final Pattern BUY_ORDER_PATTERN =
            Pattern.compile("\\[Bazaar\\] Buy Order Setup! (\\d+) (.+) for ([\\d.,]+) coins!");
    private static final Pattern SELL_OFFER_PATTERN =
            Pattern.compile("\\[Bazaar\\] Sell Offer Setup! (\\d+) (.+) for ([\\d.,]+) coins\\.");
    private static final Pattern CLAIM_ORDER_PATTERN =
            Pattern.compile("\\[Bazaar\\] Claimed (\\d+) (.+) from a Buy Order!");

    @SubscribeEvent
    public void onChatReceived(ClientChatReceivedEvent event) {
        if (event == null || event.message == null) return;
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
                recordPurchase(m.group(2), Integer.parseInt(m.group(1)), 0, "CLAIMED_ORDER");
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
        BazaarPurchase p = new BazaarPurchase(itemName, amount, perItem, type, System.currentTimeMillis());
        recentPurchases.add(p);

        // Clean old
        for (Iterator<BazaarPurchase> it = recentPurchases.iterator(); it.hasNext();) {
            BazaarPurchase old = it.next();
            if (System.currentTimeMillis() - old.timestamp > 15000L) it.remove();
        }

        BazaarDebugManager.sendDebug("Recorded " + type + ": " + amount + "x " + itemName + " @" + perItem);
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
        public final String itemName;
        public int remaining;
        public final double perItem;
        public final String type;
        public final long timestamp;

        public BazaarPurchase(String itemName, int amount, double perItem, String type, long timestamp) {
            this.itemName = itemName;
            this.remaining = amount;
            this.perItem = perItem;
            this.type = type;
            this.timestamp = timestamp;
        }
    }
}
