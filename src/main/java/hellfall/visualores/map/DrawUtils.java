package hellfall.visualores.map;

import codechicken.lib.gui.GuiDraw;
import net.minecraft.client.renderer.GlStateManager;

import java.util.List;

public class DrawUtils {
    public static void drawSimpleTooltip(List<String> text, double x, double y, int fontColor, int bgColor) {
        if (text.isEmpty()) return;

        int maxTextWidth = 0;
        for (String str : text) {
            int strWidth = GuiDraw.fontRenderer.getStringWidth(str);
            if (strWidth > maxTextWidth) maxTextWidth = strWidth;
        }

        int boxWidth = maxTextWidth + 6;
        int boxHeight = text.size() * (GuiDraw.fontRenderer.FONT_HEIGHT + 2) + 6;

        double dx = x - (double) (int) x;
        double dy = y - (double) (int) y;

        GlStateManager.pushMatrix();

        GuiDraw.drawGradientRect((int) x, (int) y, boxWidth, boxHeight, bgColor, bgColor);
        GlStateManager.translate(dx, dy, 0);
        for (int i = 0; i < text.size(); i++) {
            GuiDraw.drawString(text.get(i), (int) x + 3, (int) y + 3 + i * (GuiDraw.fontRenderer.FONT_HEIGHT + 2), fontColor, false);
        }

        GlStateManager.popMatrix();
    }

    public static float[] floats(int rgb) {
        return new float[] { (float) (rgb >> 16 & 255) / 255.0F, (float) (rgb >> 8 & 255) / 255.0F,
                (float) (rgb & 255) / 255.0F };
    }
}
