package com.example.examplemod.gui;

import net.minecraft.entity.player.EntityPlayer;

public class Module {

    private final String name;
    private boolean enabled;
    private final Category category;
    private int color = 0x00FF00;

    public Module(String name, boolean defaultState, Category category) {
        this.name = name;
        this.enabled = defaultState;
        this.category = category;
    }

    public String getName() { return name; }
    public boolean isEnabled() { return enabled; }
    public void toggle() { enabled = !enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    public Category getCategory() { return category; }
    public int getColor() { return color; }
    public void setColor(int color) { this.color = color; }

    public void onTick(EntityPlayer player) {
    }
}
