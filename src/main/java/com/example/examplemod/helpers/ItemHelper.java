package com.example.examplemod.helpers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;

/**
 * Handles item lore, price tracking, and Hypixel-style item retrieval.
 * Java 6 / Forge 1.8 compatible.
 */
public class ItemHelper {

    // Track last purchase prices
    public static final Map<String, Double> lastPurchasePrices = new HashMap<String, Double>();

    // Caches for fast lookup
    private static final Map<String, Item> plainCache = new HashMap<String, Item>();
    private static final Map<String, Item> enchantedCache = new HashMap<String, Item>();
    private static boolean cacheInitialized = false;

    /** Adds a line of lore to an ItemStack */
    public static void addLore(ItemStack stack, String line) {
        if (stack == null) return;

        NBTTagCompound tag = stack.getTagCompound();
        if (tag == null) {
            tag = new NBTTagCompound();
            stack.setTagCompound(tag);
        }

        NBTTagCompound display = tag.getCompoundTag("display");
        if (display == null) {
            display = new NBTTagCompound();
            tag.setTag("display", display);
        }

        NBTTagList loreList = display.getTagList("Lore", 8);
        if (loreList == null) {
            loreList = new NBTTagList();
        }

        loreList.appendTag(new NBTTagString(line));
        display.setTag("Lore", loreList);
    }

    /** Give a player an item with price lore */
    public static void giveItemWithPrice(EntityPlayer player, ItemStack stack, double price) {
        giveItemWithPrice(player, stack, price, stack.stackSize);
    }

    /** Overload: specify amount */
    public static void giveItemWithPrice(EntityPlayer player, ItemStack stack, double price, int amount) {
        if (stack == null || player == null || amount <= 0) return;

        ItemStack copy = stack.copy();
        copy.stackSize = amount;

        lastPurchasePrices.put(copy.getDisplayName(), price);

        addLore(copy, "§aPaid: " + String.format("%,.2f", price) + " coins each");

        if (!player.inventory.addItemStackToInventory(copy)) {
            player.dropPlayerItemWithRandomChoice(copy, false);
        }
    }

    /** Get an Item by Hypixel-style name, handles normal and enchanted separately */
    public static Item getItemByName(String name) {
        if (name == null) return null;
        initializeCache();

        String cleaned = name.toLowerCase().trim();
        boolean enchanted = false;

        if (cleaned.startsWith("enchanted ")) {
            enchanted = true;
            cleaned = cleaned.replace("enchanted ", "").trim();
        }

        Map<String, Item> map = enchanted ? enchantedCache : plainCache;
        if (map.containsKey(cleaned)) return map.get(cleaned);

        // fallback: try registry name
        Item item = Item.getByNameOrId(name);
        if (item != null) return item;

        return null;
    }

    /** Builds the cache once */
    private static void initializeCache() {
        if (cacheInitialized) return;

        for (Iterator iterator = Item.itemRegistry.iterator(); iterator.hasNext();) {
            Object obj = iterator.next();
            if (!(obj instanceof Item)) continue;
            Item i = (Item) obj;
            if (i == null) continue;

            ItemStack stack = new ItemStack(i);
            String display = stack.getDisplayName().toLowerCase().trim();
            boolean isEnchanted = display.startsWith("enchanted ");
            if (isEnchanted) display = display.replace("enchanted ", "").trim();

            Map<String, Item> map = isEnchanted ? enchantedCache : plainCache;
            map.put(display, i);
        }

        cacheInitialized = true;
    }
}
