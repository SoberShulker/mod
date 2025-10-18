package com.example.examplemod.gui;

import com.example.examplemod.modules.misc.AutoJump;
import com.example.examplemod.modules.gui.ThemeColor;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages all modules in the client.
 */
public class ModuleManager {

    // List of all modules
    public static final List<Module> modules = new ArrayList<Module>();

    static {
        // MISC modules
        modules.add(new AutoJump());

        // GUI modules
        modules.add(new ThemeColor());

        // Other modules (BAZAAR, AUCTION)
        modules.add(new Module("BAZAAR", true, Category.BAZAAR));
        modules.add(new Module("AUCTION", true, Category.AUCTION));
    }

    /**
     * Get a module by name
     */
    public static Module getModuleByName(String name) {
        for (Module m : modules) {
            if (m.getName().equalsIgnoreCase(name)) return m;
        }
        return null;
    }
}
