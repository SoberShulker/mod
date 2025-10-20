package com.example.examplemod.helpers;

import com.example.examplemod.gui.Module;
import org.lwjgl.input.Keyboard;

public class ModuleSettingsHelper {

    private static boolean open = false;
    private static boolean listening = false;
    private static Module currentModule = null;
    private static int panelX, panelY;

    /** Opens the settings panel for a module at the given position */
    public static void openSettings(Module module, int x, int y) {
        currentModule = module;
        panelX = x + 160; // adjust position so panel doesn't overlap
        panelY = y;
        open = true;
        listening = false;
    }

    /** Closes the settings panel */
    public static void closeSettings() {
        open = false;
        listening = false;
        currentModule = null;
    }

    /** Starts listening for a key press to rebind the module */
    public static void startListening() {
        if (currentModule != null) {
            listening = true;
        }
    }

    /** Should be called by ClickGUI.keyTyped */
    public static void onKeyPress(int keyCode) {
        if (listening && currentModule != null && keyCode != Keyboard.KEY_ESCAPE) {
            currentModule.setKeyBind(keyCode);
        }
        listening = false;
    }

    /** Whether the settings panel is open */
    public static boolean isOpen() {
        return open;
    }

    /** Whether we're currently listening for a key press */
    public static boolean isListening() {
        return listening;
    }

    /** Returns the currently selected module */
    public static Module getCurrentModule() {
        return currentModule;
    }

    /** Panel X position */
    public static int getPanelX() {
        return panelX;
    }

    /** Panel Y position */
    public static int getPanelY() {
        return panelY;
    }
}
