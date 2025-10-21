package com.example.examplemod.helpers;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ItemHelper {

    private static final Map<String, Item> plainCache = new HashMap<String, Item>();
    private static final Map<String, Item> enchantedCache = new HashMap<String, Item>();
    private static boolean cacheInitialized = false;

    /**
     * Safely adds a line of lore to an ItemStack.
     * All numeric formatting should use formatCoins().
     */
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
        tag.setTag("display", display);
        stack.setTagCompound(tag);
    }

    /**
     * Returns an Item by name (plain or enchanted), using a cached map for efficiency.
     */
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

        return Item.getByNameOrId(name);
    }

    /**
     * Initializes the plain and enchanted item caches (only once).
     */
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

    /**
     * Formats a double into a string with commas and two decimals,
     * safely removing any non-breaking spaces that Minecraft cannot render.
     */
    public static String formatCoins(double amount) {
        return String.format("%,.2f", amount).replace("\u00A0", "");
    }
}
