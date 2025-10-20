package com.example.examplemod.listeners;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraft.item.ItemStack;

import java.util.Iterator;

import com.example.examplemod.helpers.ItemHelper;
import com.example.examplemod.helpers.BazaarDebugManager;

public class InventoryChangeListener {

    @SubscribeEvent
    public void onItemPickup(EntityItemPickupEvent event) {
        if (event == null || event.item == null || event.item.getEntityItem() == null) return;
        if (!com.example.examplemod.listeners.BazaarChatListener.bazaarMenuOpen) return;

        ItemStack stack = event.item.getEntityItem();
        String displayName = stack.getDisplayName();
        if (displayName == null) return;

        for (Iterator it = com.example.examplemod.listeners.BazaarChatListener.recentPurchases.iterator(); it.hasNext();) {
            com.example.examplemod.listeners.BazaarChatListener.BazaarPurchase purchase =
                    (com.example.examplemod.listeners.BazaarChatListener.BazaarPurchase) it.next();
            if (!(purchase.type.startsWith("BUY") || purchase.type.equals("CLAIMED_ORDER"))) continue;
            if (!displayName.toLowerCase().contains(purchase.itemName.toLowerCase())) continue;

            int matched = Math.min(purchase.remaining, stack.stackSize);
            purchase.remaining -= matched;

            double total = matched * purchase.perItem;

            ItemHelper.addLore(stack, "§aBought via " + purchase.type.replace("_", " "));
            ItemHelper.addLore(stack, "§aPaid: " + String.format("%,.2f", purchase.perItem) + " coins each");
            ItemHelper.addLore(stack, "§7Stack total: " + String.format("%,.2f", total) + " coins");

            BazaarDebugManager.sendDebug("Lore added to " + matched + "x " + purchase.itemName);

            if (purchase.remaining <= 0) it.remove();
            break;
        }
    }
}
