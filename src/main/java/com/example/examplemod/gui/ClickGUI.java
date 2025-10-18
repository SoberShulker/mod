package com.example.examplemod.gui;

import com.example.examplemod.modules.gui.ThemeColor;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.EnumChatFormatting;
import org.lwjgl.input.Mouse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ClickGUI extends GuiScreen {

    private int guiX = 50, guiY = 50;
    private final int guiWidth = 140;
    private final int guiHeight = 20;
    private boolean dragging = false;
    private int dragOffsetX, dragOffsetY;

    private Category expandedCategory = null;

    private final List<ModuleButton> moduleButtons = new ArrayList<ModuleButton>();
    private final List<CategoryButton> categoryButtons = new ArrayList<CategoryButton>();

    @Override
    public void initGui() {
        this.buttonList.clear();
        moduleButtons.clear();
        categoryButtons.clear();

        // Category buttons
        int catX = guiX;
        Category[] categories = Category.values();
        for (int i = 0; i < categories.length; i++) {
            CategoryButton catButton = new CategoryButton(categories[i], 1000 + i, catX, guiY - 25, 70, 20, categories[i].name());
            categoryButtons.add(catButton);
            this.buttonList.add(catButton);
            catX += 75;
        }

        // Module buttons for the expanded category
        if (expandedCategory != null) {
            buildModuleButtonsForCategory(expandedCategory);
        }

        // Close button (placed below modules or default Y)
        int closeY = guiY + guiHeight + 150;
        if (!moduleButtons.isEmpty()) {
            closeY = (int) moduleButtons.get(moduleButtons.size() - 1).targetY + 22;
        }
        GuiButton closeButton = new GuiButton(999, guiX, closeY, guiWidth, 20, EnumChatFormatting.RED + "Close");
        this.buttonList.add(closeButton);
    }

    private void buildModuleButtonsForCategory(Category category) {
        // Find the category button
        CategoryButton catButton = null;
        for (CategoryButton b : categoryButtons) {
            if (b.category == category) {
                catButton = b;
                break;
            }
        }

        int modY = (catButton != null) ? catButton.yPosition + catButton.height + 5 : guiY + guiHeight;

        for (Module m : ModuleManager.modules) {
            if (m.getCategory() != category) continue;

            ModuleButton button = new ModuleButton(m, moduleButtons.size(), guiX, modY, guiWidth, 20, getButtonText(m));
            button.currentY = modY;
            button.targetY = modY;
            moduleButtons.add(button);
            this.buttonList.add(button);

            modY += 22;
        }
    }

    private String getButtonText(Module m) {
        return m.getName();
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button instanceof ModuleButton) {
            Module m = ((ModuleButton) button).module;
            m.toggle();
            return;
        }

        if (button instanceof CategoryButton) {
            CategoryButton catButton = (CategoryButton) button;
            if (expandedCategory == catButton.category) {
                expandedCategory = null; // collapse
            } else {
                expandedCategory = catButton.category; // expand
            }
            initGui();
            return;
        }

        if (button.id == 999) {
            mc.displayGuiScreen(null);
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        ThemeColor theme = (ThemeColor) ModuleManager.getModuleByName("ThemeColor");
        int themeColor = 0x00FF00;
        if (theme != null && theme.isEnabled()) themeColor = theme.getColor();

        // Draw main header
        drawRect(guiX, guiY, guiX + guiWidth, guiY + guiHeight, 0xAA000000);
        drawString(mc.fontRendererObj, "ClickGUI", guiX + 5, guiY + 5, themeColor);

        // Draw category buttons
        for (CategoryButton b : categoryButtons) {
            boolean isExpanded = b.category == expandedCategory;
            int color = isExpanded ? themeColor : 0xFFAAAAAA;
            drawRect(b.xPosition, b.yPosition, b.xPosition + b.width, b.yPosition + b.height, color);
            drawCenteredString(mc.fontRendererObj, b.displayString, b.xPosition + b.width / 2, b.yPosition + 6, 0xFFFFFFFF);
        }

        // Draw module buttons
        for (ModuleButton b : moduleButtons) {
            int leftBarColor = b.module.isEnabled() ? 0xFF00FF00 : 0xFFFF0000;
            drawRect(b.xPosition, b.yPosition, b.xPosition + 5, b.yPosition + b.height, leftBarColor);
            drawRect(b.xPosition + 5, b.yPosition, b.xPosition + b.width, b.yPosition + b.height, 0xAA000000);
            drawString(mc.fontRendererObj, getButtonText(b.module), b.xPosition + 8, b.yPosition + 6, 0xFFFFFFFF);
        }

        // Draw sliders if GUI category is expanded
        if (expandedCategory == Category.GUI && theme != null && theme.isEnabled()) {
            int sliderY = moduleButtons.isEmpty() ? guiY + guiHeight + 5 :
                    moduleButtons.get(moduleButtons.size() - 1).yPosition + 22;
            theme.drawSliders(mc, guiX, sliderY);
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
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

            // Update category buttons positions
            int catX = guiX;
            for (CategoryButton b : categoryButtons) {
                b.xPosition = catX;
                b.yPosition = guiY - 25;
                catX += 75;
            }

            // Update module buttons relative to category
            int modY = guiY + guiHeight;
            for (ModuleButton b : moduleButtons) {
                b.xPosition = guiX;
                b.targetY = modY;
                modY += 22;
            }
        }

        ThemeColor theme = (ThemeColor) ModuleManager.getModuleByName("ThemeColor");
        if (theme != null && theme.isEnabled()) theme.handleMouseInput();
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    private static class ModuleButton extends GuiButton {
        public final Module module;
        public float currentY;
        public float targetY;

        public ModuleButton(Module module, int id, int x, int y, int width, int height, String text) {
            super(id, x, y, width, height, text);
            this.module = module;
            this.currentY = y;
            this.targetY = y;
        }
    }

    private static class CategoryButton extends GuiButton {
        public final Category category;

        public CategoryButton(Category category, int id, int x, int y, int width, int height, String text) {
            super(id, x, y, width, height, text);
            this.category = category;
        }
    }
}
