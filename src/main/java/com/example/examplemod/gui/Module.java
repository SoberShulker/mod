package com.example.examplemod.gui;

import org.lwjgl.input.Keyboard;

public class Module {

    private final String name;
    private final Category category;
    private boolean enabled = false;
    private int keyBind = Keyboard.KEY_NONE; // default: no key bound

    public Module(String name, boolean b, Category category) {
        this.name = name;
        this.category = category;
    }

    // --- Module properties ---

    public String getName() {
        return name;
    }

    public Category getCategory() {
        return category;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void toggle() {
        this.enabled = !this.enabled;
    }

    // --- Keybind management ---

    public int getKeyBind() {
        return keyBind;
    }

    public void setKeyBind(int key) {
        this.keyBind = key;
    }

    public boolean isKeyPressed() {
        return keyBind != Keyboard.KEY_NONE && Keyboard.isKeyDown(keyBind);
    }
}
