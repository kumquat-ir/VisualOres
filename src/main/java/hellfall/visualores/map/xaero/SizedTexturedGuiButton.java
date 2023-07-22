package hellfall.visualores.map.xaero;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import xaero.map.gui.CursorBox;
import xaero.map.gui.GuiTexturedButton;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class SizedTexturedGuiButton extends GuiTexturedButton {

    public SizedTexturedGuiButton(int x, int y, int w, int h, int textureX, int textureY, int textureW, int textureH, ResourceLocation texture, Consumer<GuiButton> action, Supplier<CursorBox> tooltip) {
        super(x, y, w, h, textureX, textureY, textureW, textureH, texture, action, tooltip);
    }

    @Override
    public void drawButton(Minecraft minecraft, int mouseX, int mouseY, float partialTicks) {
        Minecraft.getMinecraft().getTextureManager().bindTexture(this.texture);
        int iconX = x + width / 2 - textureW / 2;
        int iconY = y + height / 2 - textureH / 2;
        if (this.enabled) {
            if (mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height) {
                iconY--;
                GlStateManager.color(0.9F, 0.9F, 0.9F);
            } else {
                GlStateManager.color(0.9882F, 0.9882F, 0.9882F, 1.0F);
            }
        } else {
            GlStateManager.color(0.25F, 0.25F, 0.25F);
        }

        Gui.drawModalRectWithCustomSizedTexture(iconX, iconY, textureX, textureY, textureW, textureH, 32, 16);
    }
}
