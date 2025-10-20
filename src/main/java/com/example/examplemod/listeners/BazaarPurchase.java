package com.example.examplemod.listeners;

public class BazaarPurchase {
    public String type;      // BUY_ORDER, SELL_ORDER
    public String itemName;  // item identifier
    public int remaining;    // amount left
    public double perItem;   // price per item
    public double price;     // total price (optional)
    public long timestamp;   // creation time

    // Existing constructor (optional)
    public BazaarPurchase(String type, int remaining, double price, String id, long timestamp) {
        this.type = type;
        this.remaining = remaining;
        this.price = price;
        this.timestamp = timestamp;
        this.itemName = "";
        this.perItem = 0;
    }

    // ✅ New constructor for test commands
    public BazaarPurchase(String type, String itemName, int remaining, double price, double perItem) {
        this.type = type;
        this.itemName = itemName;
        this.remaining = remaining;
        this.price = price;
        this.perItem = perItem;
        this.timestamp = System.currentTimeMillis();
    }
}
