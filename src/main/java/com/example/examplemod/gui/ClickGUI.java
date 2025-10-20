package com.example.examplemod.gui;

import com.example.examplemod.modules.gui.ThemeColor;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.EnumChatFormatting;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

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
    private CustomButton closeButton;

    @Override
    public void initGui() {
        this.buttonList.clear();
        moduleButtons.clear();
        categoryButtons.clear();

        int catX = guiX;
        Category[] categories = Category.values();
        for (int i = 0; i < categories.length; i++) {
            CategoryButton catButton = new CategoryButton(categories[i], 1000 + i, catX, guiY, 70, 20, categories[i].name());
            categoryButtons.add(catButton);
            this.buttonList.add(catButton);
            catX += 75;
        }

        if (expandedCategory != null) {
            buildModuleButtonsForCategory(expandedCategory);
        }

        closeButton = new CustomButton(999, guiX, guiY + 160, guiWidth, 20,
                EnumChatFormatting.RED + "Close", 0xFF222222, 0xFF444444, 0xFFFFFFFF);
        this.buttonList.add(closeButton);
    }

    private void buildModuleButtonsForCategory(Category category) {
        CategoryButton catButton = null;
        for (CategoryButton b : categoryButtons) {
            if (b.category == category) {
                catButton = b;
                break;
            }
        }
        if (catButton == null) return;

        int modY = catButton.yPosition + catButton.height + 2;

        List<ModuleButton> buttonsForCategory = new ArrayList<ModuleButton>();
        int maxTextWidth = 0;

        for (Module m : ModuleManager.modules) {
            if (m.getCategory() != category) continue;

            int textWidth = mc.fontRendererObj.getStringWidth(m.getName()) + 16; // left bar + padding
            if (textWidth > maxTextWidth) maxTextWidth = textWidth;

            ModuleButton button = new ModuleButton(m, moduleButtons.size(), catButton.xPosition, modY, guiWidth, 18, m.getName());
            button.currentY = catButton.yPosition;
            button.targetY = modY;

            buttonsForCategory.add(button);
            moduleButtons.add(button);
            this.buttonList.add(button);
            modY += 18;
        }

        int rectWidth = Math.min(maxTextWidth, guiWidth);
        for (ModuleButton b : buttonsForCategory) {
            b.width = rectWidth;
        }
    }

    @Override
    protected void actionPerformed(net.minecraft.client.gui.GuiButton button) {
        if (button instanceof ModuleButton) {
            Module m = ((ModuleButton) button).module;
            m.toggle();
            return;
        }

        if (button instanceof CategoryButton) {
            CategoryButton catButton = (CategoryButton) button;
            if (expandedCategory == catButton.category) {
                expandedCategory = null;
            } else {
                expandedCategory = catButton.category;
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
        int themeColor = (theme != null && theme.isEnabled()) ? theme.getColor() : 0xFF00FF00;

        // Draw extended background rectangle behind all categories
        int firstX = guiX;
        int lastX = guiX + guiWidth;
        if (!categoryButtons.isEmpty()) {
            firstX = categoryButtons.get(0).xPosition;
            CategoryButton lastCat = categoryButtons.get(categoryButtons.size() - 1);
            lastX = lastCat.xPosition + lastCat.width;
        }
        drawRect(firstX - 5, guiY - 5, lastX + 5, guiY + guiHeight + 25, 0xAA000000);

        // Draw centered title above the rectangle
        String title = "ClickGUI";
        int textWidth = mc.fontRendererObj.getStringWidth(title);
        int centerX = (firstX + lastX) / 2 - textWidth / 2;
        drawString(mc.fontRendererObj, title, centerX, guiY - 15, themeColor);

        for (CategoryButton catButton : categoryButtons) {
            if (expandedCategory != catButton.category) continue;

            List<ModuleButton> buttonsInCategory = new ArrayList<ModuleButton>();
            int maxWidth = 0;
            for (ModuleButton mb : moduleButtons) {
                if (mb.module.getCategory() == catButton.category) {
                    buttonsInCategory.add(mb);
                    if (mb.width > maxWidth) maxWidth = mb.width;
                }
            }
            if (buttonsInCategory.isEmpty()) continue;

            int boxWidth = maxWidth;
            int boxX = catButton.xPosition + (catButton.width / 2) - (boxWidth / 2);
            int boxY = catButton.yPosition + catButton.height;
            int boxHeight = buttonsInCategory.size() * 18 + 4;

            drawRect(boxX, boxY, boxX + boxWidth, boxY + boxHeight, 0xAA111111);
            drawRect(boxX, boxY, boxX + boxWidth, boxY + 1, themeColor);

            for (ModuleButton mb : buttonsInCategory) {
                mb.xPosition = boxX;
            }
        }

        for (ModuleButton b : moduleButtons) {
            b.drawButton(mc, mouseX, mouseY);

            GL11.glPushMatrix();
            GL11.glScalef(0.8f, 0.8f, 1f);
            drawString(mc.fontRendererObj, b.module.getName(),
                    (int)((b.xPosition + 8)/0.8f),
                    (int)((b.yPosition + 3)/0.8f),
                    0xFFFFFFFF);
            GL11.glPopMatrix();
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

            int catX = guiX;
            for (CategoryButton b : categoryButtons) {
                b.xPosition = catX;
                b.yPosition = guiY;
                catX += 75;
            }

            if (expandedCategory != null) {
                CategoryButton catButton = null;
                for (CategoryButton b : categoryButtons) {
                    if (b.category == expandedCategory) {
                        catButton = b;
                        break;
                    }
                }
                if (catButton != null) {
                    int modY = catButton.yPosition + catButton.height + 2;
                    for (ModuleButton mb : moduleButtons) {
                        if (mb.module.getCategory() != expandedCategory) continue;
                        mb.xPosition = catButton.xPosition;
                        mb.targetY = modY;
                        modY += 18;
                    }
                }
            }

            closeButton.xPosition = guiX;
            closeButton.yPosition = guiY + 160;
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    private static class ModuleButton extends CustomButton {
        public final Module module;
        public float currentY;
        public float targetY;

        public ModuleButton(Module module, int id, int x, int y, int width, int height, String text) {
            super(id, x, y, width, height, text, 0xFF222222, 0xFF444444, 0xFFFFFFFF);
            this.module = module;
            this.currentY = y;
            this.targetY = y;
        }

        @Override
        public void drawButton(net.minecraft.client.Minecraft mc, int mouseX, int mouseY) {
            if (!this.visible) return;

            currentY += (targetY - currentY) * 0.2f;
            this.yPosition = (int) currentY;

            int leftBarColor = module.isEnabled() ? 0xFF00FF00 : 0xFFFF0000;
            drawRect(this.xPosition, this.yPosition, this.xPosition + 5, this.yPosition + this.height, leftBarColor);

            super.drawButton(mc, mouseX, mouseY);
        }
    }

    private static class CategoryButton extends CustomButton {
        public final Category category;

        public CategoryButton(Category category, int id, int x, int y, int width, int height, String text) {
            super(id, x, y, width, height, text, 0xFF333333, 0xFF555555, 0xFFFFFFFF);
            this.category = category;
        }
    }
}
