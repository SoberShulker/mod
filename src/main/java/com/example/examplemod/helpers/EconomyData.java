package com.example.examplemod.helpers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Tracks full purchase and sale history for all items.
 */
public class EconomyData {

    // Map itemName -> list of all purchases
    public static final Map<String, List<PurchaseInfo>> purchaseHistory = new HashMap<String, List<PurchaseInfo>>();

    // Map itemName -> list of all sales
    public static final Map<String, List<SaleInfo>> saleHistory = new HashMap<String, List<SaleInfo>>();

    /** Record a purchase */
    public static void recordPurchase(String itemName, int amount, double pricePerItem) {
        if (itemName == null) return;

        List<PurchaseInfo> list = purchaseHistory.get(itemName);
        if (list == null) {
            list = new ArrayList<PurchaseInfo>();
            purchaseHistory.put(itemName, list);
        }
        list.add(new PurchaseInfo(amount, pricePerItem));
    }

    /** Record a sale */
    public static void recordSale(String itemName, int amount, double pricePerItem) {
        if (itemName == null) return;

        List<SaleInfo> list = saleHistory.get(itemName);
        if (list == null) {
            list = new ArrayList<SaleInfo>();
            saleHistory.put(itemName, list);
        }
        list.add(new SaleInfo(amount, pricePerItem));
    }

    /** Inner class for purchase info */
    public static class PurchaseInfo {
        public final int amount;
        public final double pricePerItem;

        public PurchaseInfo(int amount, double pricePerItem) {
            this.amount = amount;
            this.pricePerItem = pricePerItem;
        }

        public double getTotalPrice() {
            return amount * pricePerItem;
        }
    }

    /** Inner class for sale info */
    public static class SaleInfo {
        public final int amount;
        public final double pricePerItem;

        public SaleInfo(int amount, double pricePerItem) {
            this.amount = amount;
            this.pricePerItem = pricePerItem;
        }

        public double getTotalPrice() {
            return amount * pricePerItem;
        }
    }

    /** Utility: get total spent across all purchases */
    public static double getTotalSpent() {
        double total = 0;
        for (List<PurchaseInfo> list : purchaseHistory.values()) {
            for (PurchaseInfo p : list) total += p.getTotalPrice();
        }
        return total;
    }

    /** Utility: get total earned across all sales */
    public static double getTotalEarned() {
        double total = 0;
        for (List<SaleInfo> list : saleHistory.values()) {
            for (SaleInfo s : list) total += s.getTotalPrice();
        }
        return total;
    }

    /** Utility: get total profit */
    public static double getTotalProfit() {
        return getTotalEarned() - getTotalSpent();
    }
}
