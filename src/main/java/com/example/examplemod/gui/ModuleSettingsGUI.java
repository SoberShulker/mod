package com.example.examplemod.gui;

import com.example.examplemod.gui.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Keyboard;

import java.io.IOException;

public class ModuleSettingsGUI extends GuiScreen {

    private final Module module;
    private boolean waitingForKey = false;

    private GuiButton rebindButton;

    public ModuleSettingsGUI(Module module) {
        this.module = module;
    }

    @Override
    public void initGui() {
        this.buttonList.clear();

        String keyName = waitingForKey ? "Press any key..." : Keyboard.getKeyName(module.getKeyBind());

        int buttonWidth = 120;
        int buttonHeight = 20;

        rebindButton = new GuiButton(0, this.width / 2 - buttonWidth / 2, this.height / 2 - buttonHeight / 2,
                buttonWidth, buttonHeight, keyName);
        this.buttonList.add(rebindButton);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();

        // Draw module name at the top
        String title = module.getName() + " Settings";
        FontRenderer fr = mc.fontRendererObj;
        int titleWidth = fr.getStringWidth(title);
        fr.drawStringWithShadow(title, (this.width - titleWidth) / 2, this.height / 2 - 40, 0xFFFFFF);

        // Draw buttons
        super.drawScreen(mouseX, mouseY, partialTicks);

        // Scale text to fit button width
        drawScaledButtonText(rebindButton);
    }

    private void drawScaledButtonText(GuiButton button) {
        String text = button.displayString;
        FontRenderer fr = mc.fontRendererObj;

        int maxWidth = button.width - 10; // padding
        float scale = 1.0f;
        if (fr.getStringWidth(text) > maxWidth) {
            scale = (float) maxWidth / (float) fr.getStringWidth(text);
        }

        int x = button.xPosition + 5;
        int y = (int) (button.yPosition + (button.height - fr.FONT_HEIGHT * scale) / 2);

        mc.fontRendererObj.drawString(text, x, y, 0xFFFFFF);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == 0) {
            waitingForKey = true; // enable key press listener
            button.displayString = "Press any key...";
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (waitingForKey) {
            module.setKeyBind(keyCode); // save the new key
            waitingForKey = false;
            initGui(); // refresh button text
        } else {
            super.keyTyped(typedChar, keyCode);
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
