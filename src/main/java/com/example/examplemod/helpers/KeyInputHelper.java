package com.example.examplemod.helpers;

import com.example.examplemod.gui.Module;
import org.lwjgl.input.Keyboard;

public class KeyInputHelper {

    private static boolean listening = false;
    private static Module targetModule = null;

    public static void startListening(Module module) {
        listening = true;
        targetModule = module;
    }

    public static void stopListening() {
        listening = false;
        targetModule = null;
    }

    public static void checkKeyboard() {
        if (!listening || targetModule == null) return;

        for (int key = 0; key < Keyboard.KEYBOARD_SIZE; key++) {
            if (Keyboard.isKeyDown(key)) {
                targetModule.setKeyBind(key);
                stopListening();
                break;
            }
        }
    }

    public static boolean isListening() {
        return listening;
    }

    public static Module getTargetModule() {
        return targetModule;
    }
}
