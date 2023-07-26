package hellfall.visualores.map;

import codechicken.lib.gui.GuiDraw;
import gregtech.api.fluids.MaterialFluid;
import gregtech.api.util.GTUtility;
import hellfall.visualores.VOConfig;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.common.Loader;

import java.util.List;

public class DrawUtils {
    public static Object2IntMap<String> colorOverrides = new Object2IntOpenHashMap<>();

    public static void drawSimpleTooltip(List<String> text, double x, double y, int screenW, int screenH, int fontColor, int bgColor) {
        if (text.isEmpty()) return;

        int boxHeight = text.size() * (GuiDraw.fontRenderer.FONT_HEIGHT + 2) + 6;

        // if box is taller than screen, truncate list of lines and add a line with how many were truncated
        if (boxHeight > screenH) {
            int maxLines = (screenH - 6) / (GuiDraw.fontRenderer.FONT_HEIGHT + 2);
            int oldsize = text.size();
            text = text.subList(0, maxLines - 1);
            text.add(I18n.format("visualores.tooltipoverflow", oldsize - maxLines + 1));
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

    public static int getFluidColor(Fluid fluid) {
        int color = fluid.getColor();
        if (colorOverrides.containsKey(fluid.getName())) {
            // make full opacity
            color = colorOverrides.get(fluid.getName()) | 0xFF000000;
        }
        else if (color == 0xFFFFFFFF && Loader.isModLoaded("gregtech")) {
            color = gtMaterialColor(fluid);
        }
        return color;
    }

    private static int gtMaterialColor(Fluid fluid) {
        if (fluid instanceof MaterialFluid materialFluid) {
            return GTUtility.convertRGBtoOpaqueRGBA_MC(materialFluid.getMaterial().getMaterialRGB());
        }
        return fluid.getColor();
    }

    public static void initColorOverrides() {
        for (String entry : VOConfig.client.fluidColorOverrides) {
            String[] parts = entry.split("="); // eg. {"water", "#6B7AF7"}
            colorOverrides.put(parts[0], Integer.decode(parts[1]));
        }
    }
}
