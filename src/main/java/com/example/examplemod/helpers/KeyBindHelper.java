package com.example.examplemod.helpers;

import com.example.examplemod.gui.Module;
import org.lwjgl.input.Keyboard;

public class KeyBindHelper {

    private static Module listeningModule = null;
    private static boolean listening = false;

    // Call this when "Rebind" button is clicked
    public static void startListening(Module module) {
        listeningModule = module;
        listening = true;
    }

    // Call this every tick while GUI is open
    public static void checkKeyboard() {
        if (!listening || listeningModule == null) return;

        while (Keyboard.next()) {
            int key = Keyboard.getEventKey();
            if (Keyboard.getEventKeyState()) { // key pressed
                listeningModule.setKeyBind(key); // actually set the key
                stopListening();
            }
        }
    }

    public static boolean isListening() {
        return listening;
    }

    public static void stopListening() {
        listening = false;
        listeningModule = null;
    }

    public static Module getListeningModule() {
        return listeningModule;
    }
}
