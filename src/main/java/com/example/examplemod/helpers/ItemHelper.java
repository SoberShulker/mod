package com.example.examplemod.helpers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;

import java.util.HashMap;
import java.util.Map;

/**
 * Handles item lore and price tracking.
 */
public class ItemHelper {

    // Map to track purchases in multiplayer (client-side only)
    public static final Map<String, Double> lastPurchasePrices = new HashMap<String, Double>();

    /**
     * Adds a single line of lore to the given ItemStack (singleplayer-safe).
     */
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

    /**
     * Give a player an item with optional lore and track purchase.
     * Works for singleplayer and safely tracks for multiplayer.
     */
    public static void giveItemWithPrice(EntityPlayer player, ItemStack stack, double price) {
        if (stack == null || player == null) return;

        // Track locally for client-sided rendering in multiplayer
        lastPurchasePrices.put(stack.getDisplayName(), price);

        addLore(stack, "§aPaid: " + price);

        if (!player.inventory.addItemStackToInventory(stack)) {
            player.dropPlayerItemWithRandomChoice(stack, false);
        }
    }
}
