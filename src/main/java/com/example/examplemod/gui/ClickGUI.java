package com.example.examplemod.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.EnumChatFormatting;
import org.lwjgl.input.Mouse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ClickGUI extends GuiScreen {

    private int guiX = 50, guiY = 50;
    private final int guiWidth = 120;
    private final int guiHeight = 20;
    private boolean dragging = false;
    private int dragOffsetX, dragOffsetY;

    private Category selectedCategory = Category.BAZAAR;
    private static Category defaultCategory = Category.BAZAAR;

    private final List<GuiButton> moduleButtons = new ArrayList<GuiButton>();
    private final List<GuiButton> categoryButtons = new ArrayList<GuiButton>();

    @Override
    public void initGui() {
        selectedCategory = defaultCategory; // start with last selected
        this.buttonList.clear();
        moduleButtons.clear();
        categoryButtons.clear();

        int yOffset = guiY + guiHeight;

        // Create category buttons
        int catX = guiX;
        Category[] categories = Category.values();
        for (int i = 0; i < categories.length; i++) {
            Category category = categories[i];
            GuiButton catButton = new GuiButton(
                    1000 + i,
                    catX,
                    guiY - 25,
                    60,
                    20,
                    category.name()
            );
            categoryButtons.add(catButton);
            this.buttonList.add(catButton);
            catX += 65;
        }

        // Create module buttons for selected category
        int modY = yOffset;
        List<Module> modules = ModuleManager.modules;
        for (int i = 0; i < modules.size(); i++) {
            Module m = modules.get(i);
            if (m.getCategory() == selectedCategory) {
                GuiButton button = new GuiButton(
                        i,          // button ID
                        guiX,
                        modY,
                        guiWidth,
                        20,
                        getButtonText(m)
                );
                moduleButtons.add(button);
                this.buttonList.add(button);
                modY += 22;
            }
        }

        // Add close button
        GuiButton closeButton = new GuiButton(999, guiX, modY + 5, guiWidth, 20, EnumChatFormatting.RED + "Close");
        this.buttonList.add(closeButton);
    }

    private String getButtonText(Module m) {
        return m.getName() + ": " + (m.isEnabled()
                ? EnumChatFormatting.GREEN + "ON"
                : EnumChatFormatting.RED + "OFF");
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        int id = button.id;

        // Module toggle
        if (id >= 0 && id < ModuleManager.modules.size()) {
            Module m = ModuleManager.modules.get(id);
            m.toggle();
            button.displayString = getButtonText(m);
            return;
        }

        // Category buttons
        if (id >= 1000 && id < 2000) {
            selectedCategory = Category.values()[id - 1000];
            initGui(); // refresh module buttons
            return;
        }

        // Close button
        if (id == 999) {
            defaultCategory = selectedCategory; // remember current category
            mc.displayGuiScreen(null);
        }
    }

    @Override
    public void onGuiClosed() {
        defaultCategory = selectedCategory; // save last selected category on ESC
        super.onGuiClosed();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        // Get ThemeColor module if enabled
        Module themeModule = ModuleManager.getModuleByName("ThemeColor");
        int themeColor = 0x00FF00; // default green
        if (themeModule != null && themeModule.isEnabled()) {
            themeColor = themeModule.getColor();
        }

        // Draw header
        drawRect(guiX, guiY, guiX + guiWidth, guiY + guiHeight, 0xAA000000);
        drawString(mc.fontRendererObj, "ClickGUI - " + selectedCategory.name(), guiX + 5, guiY + 5, themeColor);

        // Draw category tabs with selected highlight
        for (GuiButton b : categoryButtons) {
            boolean selected = (b.id - 1000 == selectedCategory.ordinal());
            int color = selected ? themeColor : 0xFFAAAAAA;
            drawRect(b.xPosition, b.yPosition, b.xPosition + b.width, b.yPosition + b.height, color);
            drawCenteredString(mc.fontRendererObj, b.displayString, b.xPosition + b.width / 2, b.yPosition + 6, 0xFFFFFFFF);
        }

        // Draw modules and buttons
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        // Start dragging header
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

            // Update module buttons positions
            int modY = guiY + guiHeight;
            for (GuiButton b : moduleButtons) {
                b.xPosition = guiX;
                b.yPosition = modY;
                modY += 22;
            }

            // Update close button
            for (GuiButton b : buttonList) {
                if (b.id == 999) {
                    b.xPosition = guiX;
                    b.yPosition = modY + 5;
                }
            }

            // Update category buttons
            int catX = guiX;
            for (GuiButton b : categoryButtons) {
                b.xPosition = catX;
                b.yPosition = guiY - 25;
                catX += 65;
            }
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
