package com.example.examplemod.modules.gui;

import com.example.examplemod.gui.Module;
import com.example.examplemod.gui.Category;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.input.Mouse;

/**
 * Module to control the color theme of the client.
 * Provides full RGB sliders that affect all GUI elements.
 */
public class ThemeColor extends Module {

    private int red = 0;
    private int green = 255;
    private int blue = 0;

    private boolean draggingRed = false;
    private boolean draggingGreen = false;
    private boolean draggingBlue = false;

    private int sliderWidth = 100;
    private int sliderHeight = 10;
    private int sliderX = 50;
    private int sliderY = 80;

    public ThemeColor() {
        super("ThemeColor", true, Category.GUI);
    }

    @Override
    public int getColor() {
        // Return combined ARGB value
        return (255 << 24) | (red << 16) | (green << 8) | blue;
    }

    public int getRed() { return red; }
    public int getGreen() { return green; }
    public int getBlue() { return blue; }

    public void setRed(int r) { red = clamp(r); }
    public void setGreen(int g) { green = clamp(g); }
    public void setBlue(int b) { blue = clamp(b); }

    private int clamp(int val) {
        return Math.max(0, Math.min(255, val));
    }

    @Override
    public void onTick(EntityPlayer player) {
        // ThemeColorModule doesn't do automatic ticking
    }

    /**
     * Draw RGB sliders for the module.
     * Call this in ClickGUI.drawScreen()
     */
    public void drawSliders(Minecraft mc) {
        // Red slider
        Gui.drawRect(sliderX, sliderY, sliderX + sliderWidth, sliderY + sliderHeight, 0xFFFF0000);
        int redKnobX = sliderX + red * sliderWidth / 255;
        Gui.drawRect(redKnobX - 2, sliderY - 2, redKnobX + 2, sliderY + sliderHeight + 2, 0xFFFFFFFF);

        // Green slider
        int greenY = sliderY + 15;
        Gui.drawRect(sliderX, greenY, sliderX + sliderWidth, greenY + sliderHeight, 0xFF00FF00);
        int greenKnobX = sliderX + green * sliderWidth / 255;
        Gui.drawRect(greenKnobX - 2, greenY - 2, greenKnobX + 2, greenY + sliderHeight + 2, 0xFFFFFFFF);

        // Blue slider
        int blueY = sliderY + 30;
        Gui.drawRect(sliderX, blueY, sliderX + sliderWidth, blueY + sliderHeight, 0xFF0000FF);
        int blueKnobX = sliderX + blue * sliderWidth / 255;
        Gui.drawRect(blueKnobX - 2, blueY - 2, blueKnobX + 2, blueY + sliderHeight + 2, 0xFFFFFFFF);
    }

    /**
     * Handle mouse input for the sliders.
     * Call this in ClickGUI.handleMouseInput()
     */
    public void handleMouseInput() {
        Minecraft mc = Minecraft.getMinecraft();
        int mouseX = Mouse.getEventX() * mc.displayWidth / mc.currentScreen.width;
        int mouseY = mc.currentScreen.height - Mouse.getEventY() * mc.currentScreen.height / mc.displayHeight - 1;

        boolean leftPressed = Mouse.isButtonDown(0);

        int redY = sliderY;
        int greenY = sliderY + 15;
        int blueY = sliderY + 30;

        if (leftPressed) {
            if (isMouseOver(mouseX, mouseY, sliderX, redY, sliderWidth, sliderHeight)) draggingRed = true;
            if (isMouseOver(mouseX, mouseY, sliderX, greenY, sliderWidth, sliderHeight)) draggingGreen = true;
            if (isMouseOver(mouseX, mouseY, sliderX, blueY, sliderWidth, sliderHeight)) draggingBlue = true;
        } else {
            draggingRed = draggingGreen = draggingBlue = false;
        }

        // Update values based on mouse drag
        if (draggingRed) red = clamp((mouseX - sliderX) * 255 / sliderWidth);
        if (draggingGreen) green = clamp((mouseX - sliderX) * 255 / sliderWidth);
        if (draggingBlue) blue = clamp((mouseX - sliderX) * 255 / sliderWidth);
    }

    private boolean isMouseOver(int mx, int my, int x, int y, int width, int height) {
        return mx >= x && mx <= x + width && my >= y && my <= y + height;
    }
}
