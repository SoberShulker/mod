package com.example.examplemod;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.EnumChatFormatting;
import org.lwjgl.input.Mouse;

import java.io.IOException;

/**
 * Draggable ClickGUI with multiple module toggles for 1.8.9
 */
public class ClickGUI extends GuiScreen {

    private int guiX = 50, guiY = 50; // Window position
    private int guiWidth = 120, guiHeight = 20; // Header height
    private boolean dragging = false;
    private int dragOffsetX, dragOffsetY;

    private final GuiButton[] moduleButtons = new GuiButton[ModuleManager.modules.size()];

    @Override
    public void initGui() {
        this.buttonList.clear();

        // Create a button for each module
        int yOffset = guiY + guiHeight;
        for (int i = 0; i < ModuleManager.modules.size(); i++) {
            Module m = ModuleManager.modules.get(i);
            moduleButtons[i] = new GuiButton(
                    i,
                    guiX,
                    yOffset + i * 22,
                    guiWidth,
                    20,
                    getButtonText(m)
            );
            this.buttonList.add(moduleButtons[i]);
        }
    }

    private String getButtonText(Module m) {
        return m.getName() + ": " + (m.isEnabled()
                ? EnumChatFormatting.GREEN + "ON"
                : EnumChatFormatting.RED + "OFF");
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id >= 0 && button.id < ModuleManager.modules.size()) {
            Module m = ModuleManager.modules.get(button.id);
            m.toggle();
            button.displayString = getButtonText(m);
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        // Draw header
        drawRect(guiX, guiY, guiX + guiWidth, guiY + guiHeight, 0xAA000000);
        drawString(mc.fontRendererObj, "ClickGUI", guiX + 5, guiY + 5, 0xFFFFFF);

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        // Start dragging if clicked on header
        if (mouseX >= guiX && mouseX <= guiX + guiWidth &&
                mouseY >= guiY && mouseY <= guiY + guiHeight) {
            dragging = true;
            dragOffsetX = mouseX - guiX;
            dragOffsetY = mouseY - guiY;
        }
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);
        dragging = false;
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();

        if (dragging) {
            int mouseX = Mouse.getEventX() * this.width / mc.displayWidth;
            int mouseY = this.height - Mouse.getEventY() * this.height / mc.displayHeight - 1;

            guiX = mouseX - dragOffsetX;
            guiY = mouseY - dragOffsetY;

            // Update button positions
            for (int i = 0; i < moduleButtons.length; i++) {
                moduleButtons[i].xPosition = guiX;
                moduleButtons[i].yPosition = guiY + guiHeight + i * 22;
            }
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
