package com.example.examplemod.gui;

import java.util.ArrayList;
import java.util.List;

public class ModuleManager {
    public static final List<Module> modules = new ArrayList<Module>();

    static {
        // Example modules
        modules.add(new Module("Auto Jump", true, Category.MISC));
        modules.add(new Module("ThemeColor", true, Category.GUI)); // GUI customization
        // Add more modules later
    }

    public static Module getModuleByName(String name) {
        for (Module m : modules) {
            if (m.getName().equalsIgnoreCase(name)) return m;
        }
        return null;
    }
}
