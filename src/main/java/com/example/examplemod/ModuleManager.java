package com.example.examplemod;

import java.util.ArrayList;
import java.util.List;

/**
 * Holds all modules for the mod.
 */
public class ModuleManager {
    // Java 6 compatible: specify generic type on both sides
    public static final List<Module> modules = new ArrayList<Module>();

    static {
        // Add modules here
        modules.add(new Module("Auto Jump", true));
        // You can add more modules later
    }

    public static Module getModuleByName(String name) {
        for (Module m : modules) {
            if (m.getName().equalsIgnoreCase(name)) return m;
        }
        return null;
    }
}
