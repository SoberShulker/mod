package com.example.examplemod.helpers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Handles item lore, price tracking, and Hypixel-style item lookup.
 * Fully Forge 1.8 + Java 6 compatible.
 */
public class ItemHelper {

    public static final Map<String, Double> lastPurchasePrices = new HashMap<String, Double>();
    private static final Map<String, Item> plainCache = new HashMap<String, Item>();
    private static final Map<String, Item> enchantedCache = new HashMap<String, Item>();
    private static boolean cacheInitialized = false;

    /** Force-correct lore injection (1.8 safe) */
    public static void addLore(ItemStack stack, String line) {
        if (stack == null) return;

        NBTTagCompound tag = stack.getTagCompound();
        if (tag == null) {
            tag = new NBTTagCompound();
            stack.setTagCompound(tag);
        }

        NBTTagCompound display;
        if (tag.hasKey("display", 10)) {
            display = tag.getCompoundTag("display");
        } else {
            display = new NBTTagCompound();
            tag.setTag("display", display);
        }

        NBTTagList loreList;
        if (display.hasKey("Lore", 9)) {
            loreList = display.getTagList("Lore", 8);
        } else {
            loreList = new NBTTagList();
        }

        loreList.appendTag(new NBTTagString(line));
        display.setTag("Lore", loreList);
        stack.setTagCompound(tag);
    }

    /** Give a player an item with lore & tracked price */
    public static void giveItemWithPrice(EntityPlayer player, ItemStack stack, double price) {
        giveItemWithPrice(player, stack, price, stack.stackSize);
    }

    public static void giveItemWithPrice(EntityPlayer player, ItemStack stack, double price, int amount) {
        if (stack == null || player == null || amount <= 0) return;

        ItemStack copy = stack.copy();
        copy.stackSize = amount;

        lastPurchasePrices.put(copy.getDisplayName(), price);
        addLore(copy, "§aPaid: " + String.format("%,.2f", price) + " coins each");

        if (!player.inventory.addItemStackToInventory(copy)) {
            player.dropPlayerItemWithRandomChoice(copy, false);
        }
        player.inventory.markDirty();
        player.openContainer.detectAndSendChanges();
    }

    /** Get an item by Hypixel-style name */
    public static Item getItemByName(String name) {
        if (name == null) return null;
        initializeCache();

        String cleaned = name.toLowerCase().trim();
        boolean enchanted = false;

        if (cleaned.startsWith("enchanted ")) {
            enchanted = true;
            cleaned = cleaned.replace("enchanted ", "").trim();
        }

        Map map = enchanted ? enchantedCache : plainCache;
        if (map.containsKey(cleaned)) return (Item) map.get(cleaned);

        Item fallback = Item.getByNameOrId(name);
        return fallback;
    }

    /** Build cache once */
    private static void initializeCache() {
        if (cacheInitialized) return;

        for (Iterator it = Item.itemRegistry.iterator(); it.hasNext();) {
            Object obj = it.next();
            if (!(obj instanceof Item)) continue;
            Item i = (Item) obj;
            if (i == null) continue;

            ItemStack stack = new ItemStack(i);
            String display = stack.getDisplayName().toLowerCase().trim();
            boolean isEnchanted = display.startsWith("enchanted ");
            if (isEnchanted) display = display.replace("enchanted ", "").trim();

            Map map = isEnchanted ? enchantedCache : plainCache;
            map.put(display, i);
        }

        cacheInitialized = true;
    }
}
