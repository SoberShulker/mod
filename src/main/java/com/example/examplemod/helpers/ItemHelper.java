package com.example.examplemod.util;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;

public class ItemHelper {

    /**
     * Adds a single line of lore to the given ItemStack.
     * If the item already has lore, it appends instead of overwriting.
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

        NBTTagList loreList = display.getTagList("Lore", 8); // 8 = String type
        if (loreList == null) {
            loreList = new NBTTagList();
        }

        loreList.appendTag(new NBTTagString(line));
        display.setTag("Lore", loreList);
    }
}
