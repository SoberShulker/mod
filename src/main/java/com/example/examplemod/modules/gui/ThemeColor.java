package com.example.examplemod.modules.gui;

import com.example.examplemod.gui.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import com.example.examplemod.gui.Category;

import java.awt.Color;

public class ThemeColor extends Module {

    private int red = 0, green = 255, blue = 0;

    private final Slider redSlider = new Slider("R", 0, 255);
    private final Slider greenSlider = new Slider("G", 0, 255);
    private final Slider blueSlider = new Slider("B", 0, 255);

    public ThemeColor() {
        super("ThemeColor", true, Category.GUI);
    }

    /**
     * Returns the combined RGB color.
     */
    public int getColor() {
        return new Color(red, green, blue).getRGB();
    }

    /**
     * Draw sliders at dynamic X, Y positions.
     */
    public void drawSliders(Minecraft mc, int x, int startY) {
        int sliderY = startY;
        redSlider.draw(mc, x, sliderY);
        sliderY += 15;
        greenSlider.draw(mc, x, sliderY);
        sliderY += 15;
        blueSlider.draw(mc, x, sliderY);

        // Update values from sliders
        red = redSlider.getValue();
        green = greenSlider.getValue();
        blue = blueSlider.getValue();
    }

    /**
     * Handle mouse input for all sliders.
     */
    public void handleMouseInput() {
        redSlider.handleMouseInput();
        greenSlider.handleMouseInput();
        blueSlider.handleMouseInput();
    }

    // --- Slider inner class ---
    private static class Slider {
        private final String label;
        private int value;
        private final int min, max;
        private boolean dragging = false;
        private int x;
        private int y;
        private final int width = 120;
        private final int height = 10;

        public Slider(String label, int min, int max) {
            this.label = label;
            this.min = min;
            this.max = max;
            this.value = min;
        }

        public int getValue() {
            return value;
        }

        public void draw(Minecraft mc, int x, int y) {
            this.x = x;
            this.y = y;

            // Background bar
            Gui.drawRect(x, y, x + width, y + height, 0xAA000000);

            // Filled portion
            int filledWidth = (int) ((value - min) / (float) (max - min) * width);
            Gui.drawRect(x, y, x + filledWidth, y + height, 0xFFFF0000);

            // Draw label and value
            mc.fontRendererObj.drawString(label + ": " + value, x, y - 10, 0xFFFFFFFF);
        }

        public void handleMouseInput() {
            if (dragging) {
                int mouseX = org.lwjgl.input.Mouse.getX() * width / Minecraft.getMinecraft().displayWidth;
                int newValue = (int) ((mouseX - x) / (float) width * (max - min) + min);
                if (newValue < min) newValue = min;
                if (newValue > max) newValue = max;
                value = newValue;

                // Stop dragging if mouse button released
                if (!org.lwjgl.input.Mouse.isButtonDown(0)) {
                    dragging = false;
                }
            }

            // Start dragging if hovering and left-click
            int mouseX = org.lwjgl.input.Mouse.getX() * width / Minecraft.getMinecraft().displayWidth;
            int mouseY = Minecraft.getMinecraft().displayHeight - org.lwjgl.input.Mouse.getY() * Minecraft.getMinecraft().displayHeight / Minecraft.getMinecraft().displayHeight - 1;
            if (mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height && org.lwjgl.input.Mouse.isButtonDown(0)) {
                dragging = true;
            }
        }
    }
}
