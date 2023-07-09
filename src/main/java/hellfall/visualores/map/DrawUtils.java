package hellfall.visualores.map;

import codechicken.lib.gui.GuiDraw;
import gregtech.api.util.LocalizationUtils;
import net.minecraft.client.renderer.GlStateManager;

import java.util.List;

public class DrawUtils {
    public static void drawSimpleTooltip(List<String> text, double x, double y, int screenW, int screenH, int fontColor, int bgColor) {
        if (text.isEmpty()) return;

        int boxHeight = text.size() * (GuiDraw.fontRenderer.FONT_HEIGHT + 2) + 6;

        // if box is taller than screen, truncate list of lines and add a line with how many were truncated
        if (boxHeight > screenH) {
            int maxLines = (screenH - 6) / (GuiDraw.fontRenderer.FONT_HEIGHT + 2);
            int oldsize = text.size();
            text = text.subList(0, maxLines - 1);
            text.add(LocalizationUtils.format("visualores.tooltipoverflow", oldsize - maxLines + 1));
            boxHeight = text.size() * (GuiDraw.fontRenderer.FONT_HEIGHT + 2) + 6;
        }

        int maxTextWidth = 0;
        for (String str : text) {
            int strWidth = GuiDraw.fontRenderer.getStringWidth(str);
            if (strWidth > maxTextWidth) maxTextWidth = strWidth;
        }

        int boxWidth = maxTextWidth + 6;

        // mirror box if it intersects screen edge
        // shift box left/up if no space to mirror
        if (x + boxWidth > screenW) {
            if (x - boxWidth < 0) {
                x = Math.max(0, screenW - boxWidth);
            }
            else {
                x -= boxWidth;
            }
        }
        if (y + boxHeight > screenH) {
            if (y - boxHeight < 0) {
                y = Math.max(0, screenH - boxHeight);
            }
            else {
                y -= boxHeight;
            }
        }

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
