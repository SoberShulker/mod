package com.example.examplemod.listeners;

import com.example.examplemod.helpers.ItemHelper;
import com.example.examplemod.helpers.BazaarDebugManager;
import com.example.examplemod.helpers.TestCommandHelper;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.item.ItemStack;

import java.util.Iterator;

public class InventoryChangeListener {

    @SubscribeEvent
    public void onItemPickup(EntityItemPickupEvent event) {
        if (event == null || event.item == null || event.item.getEntityItem() == null) return;

        // Only operate when Bazaar menu is open
        if (!BazaarChatListener.bazaarMenuOpen) return;

        ItemStack stack = event.item.getEntityItem();
        if (stack == null || stack.getDisplayName() == null) return;
        String displayName = stack.getDisplayName();

        // Iterate over recent purchases
        for (Iterator it = BazaarChatListener.recentPurchases.iterator(); it.hasNext();) {
            BazaarChatListener.BazaarPurchase purchase = (BazaarChatListener.BazaarPurchase) it.next();

            // Only process buys and claimed orders
            if (!(purchase.type.startsWith("BUY") || purchase.type.equals("CLAIMED_ORDER"))) continue;

            // Match display name (case-insensitive)
            if (!displayName.toLowerCase().contains(purchase.itemName.toLowerCase())) continue;

            int matched = Math.min(purchase.remaining, stack.stackSize);
            purchase.remaining -= matched;
            double total = matched * purchase.perItem;

            // Only give lore in singleplayer (items are client-side)
            if (!BazaarDebugManager.isDebugEnabled() || TestCommandHelper.isSingleplayer(event.entityPlayer)) {
                ItemHelper.addLore(stack, "§aBought via " + purchase.type.replace("_", " "));
                ItemHelper.addLore(stack, "§aPaid: " + String.format("%,.2f", purchase.perItem) + " coins each");
                ItemHelper.addLore(stack, "§7Stack total: " + String.format("%,.2f", total) + " coins");
            }

            BazaarDebugManager.sendDebug("Lore added to " + matched + "x " + purchase.itemName);

            // Remove purchase if fulfilled
            if (purchase.remaining <= 0) it.remove();

            break; // Stop after first matching purchase
        }
    }
}
