package com.example.examplemod.helpers;

import com.example.examplemod.listeners.BazaarChatListener;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class FakeOrderStorage {

    private static final File ORDERS_FILE = new File("config/fake_orders.dat");

    public static void saveOrders(List<BazaarChatListener.BazaarPurchase> orders) {
        try {
            NBTTagCompound tag = new NBTTagCompound();
            for (int i = 0; i < orders.size(); i++) {
                BazaarChatListener.BazaarPurchase o = orders.get(i);
                NBTTagCompound orderTag = new NBTTagCompound();
                orderTag.setString("itemName", o.itemName);
                orderTag.setInteger("remaining", o.remaining);
                orderTag.setDouble("perItem", o.perItem);
                orderTag.setDouble("price", o.price);
                orderTag.setString("type", o.type);
                orderTag.setLong("timestamp", o.timestamp);

                tag.setTag("order" + i, orderTag);
            }

            if (!ORDERS_FILE.getParentFile().exists()) ORDERS_FILE.getParentFile().mkdirs();
            FileOutputStream fos = new FileOutputStream(ORDERS_FILE);
            CompressedStreamTools.writeCompressed(tag, fos);
            fos.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<BazaarChatListener.BazaarPurchase> loadOrders() {
        List<BazaarChatListener.BazaarPurchase> orders = new ArrayList<BazaarChatListener.BazaarPurchase>();

        if (!ORDERS_FILE.exists()) return orders;

        try {
            FileInputStream fis = new FileInputStream(ORDERS_FILE);
            NBTTagCompound tag = CompressedStreamTools.readCompressed(fis);
            fis.close();

            for (int i = 0; tag.hasKey("order" + i); i++) {
                NBTTagCompound orderTag = tag.getCompoundTag("order" + i);
                BazaarChatListener.BazaarPurchase order =
                        new BazaarChatListener.BazaarPurchase(
                                orderTag.getString("itemName"),
                                orderTag.getInteger("remaining"),
                                orderTag.getDouble("perItem"),
                                orderTag.getString("type"),
                                orderTag.getDouble("price"),
                                orderTag.getLong("timestamp")
                        );
                orders.add(order);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return orders;
    }
}
