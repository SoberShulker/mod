package com.example.examplemod.gui;


public class Module {
    private final String name;
    private boolean enabled;
    private final com.example.examplemod.gui.Category category;

    // For GUI modules like ThemeColor
    private int color = 0x00FF00; // default green RGB

    public Module(String name, boolean defaultState, com.example.examplemod.gui.Category category) {
        this.name = name;
        this.enabled = defaultState;
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void toggle() {
        enabled = !enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public com.example.examplemod.gui.Category getCategory() {
        return category;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}
