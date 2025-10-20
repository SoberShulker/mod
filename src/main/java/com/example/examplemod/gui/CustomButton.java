package com.example.examplemod.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;

public class CustomButton extends GuiButton {

    private final int normalColor;
    private final int hoverColor;
    private final int textColor;

    public CustomButton(int id, int x, int y, int width, int height, String text,
                        int normalColor, int hoverColor, int textColor) {
        super(id, x, y, width, height, text);
        this.normalColor = normalColor;
        this.hoverColor = hoverColor;
        this.textColor = textColor;
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        if (!this.visible) return;
        boolean hovering = mouseX >= this.xPosition && mouseY >= this.yPosition &&
                mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;

        int color = hovering ? hoverColor : normalColor;
        drawRect(this.xPosition, this.yPosition, this.xPosition + this.width, this.yPosition + this.height, color);
        mc.fontRendererObj.drawStringWithShadow(displayString,
                this.xPosition + (this.width / 2) - (mc.fontRendererObj.getStringWidth(displayString) / 2),
                this.yPosition + (this.height - 8) / 2,
                textColor);
    }
}
