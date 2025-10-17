package com.example.examplemod;

/**
 * Represents a single toggleable module.
 */
public class Module {
    private final String name;
    private boolean enabled;

    public Module(String name, boolean defaultState) {
        this.name = name;
        this.enabled = defaultState;
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
}
