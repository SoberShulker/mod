package com.example.examplemod.helpers;

import java.util.HashMap;
import java.util.Map;

/**
 * Centralized storage for all buy/sell data.
 * Java 6 compatible for Minecraft 1.8.9.
 */
public class EconomyData {

    // Purchase price per item (price per unit)
    public static Map<String, Double> purchasePriceMap = new HashMap<String, Double>();

    // Total sold price per item
    public static Map<String, Double> totalSellMap = new HashMap<String, Double>();

    // Total sold amount per item (for accurate profit calculation)
    public static Map<String, Integer> totalSoldAmountMap = new HashMap<String, Integer>();

    // Utility to record a sale
    public static void recordSale(String itemName, int amount, double totalSellPrice) {
        Double currentSell = totalSellMap.get(itemName);
        if (currentSell == null) currentSell = 0.0;
        totalSellMap.put(itemName, currentSell + totalSellPrice);

        Integer currentAmount = totalSoldAmountMap.get(itemName);
        if (currentAmount == null) currentAmount = 0;
        totalSoldAmountMap.put(itemName, currentAmount + amount);
    }

    // Utility to record a purchase
    public static void recordPurchase(String itemName, double purchasePricePerUnit) {
        purchasePriceMap.put(itemName, purchasePricePerUnit);
    }
}
