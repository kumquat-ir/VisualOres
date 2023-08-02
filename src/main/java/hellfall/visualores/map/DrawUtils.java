package hellfall.visualores.map;

import codechicken.lib.gui.GuiDraw;
import gregtech.api.fluids.MaterialFluid;
import gregtech.api.util.GTUtility;
import hellfall.visualores.VOConfig;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.common.Loader;

import java.util.List;

public class DrawUtils {
    public static final Object2IntMap<String> colorOverrides = new Object2IntOpenHashMap<>();

    /**
     * Draws a tooltip on the screen.
     * @param screenW The width of the screen. Used for tooltip placement
     * @param screenH The height of the screen. Used for tooltip placement and truncation if too tall
     * @param fontColor ARGB hex color of the text
     * @param bgColor ARGB hex color of the background
     */
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

    /**
     * Draws a box with different colored sides and center.
     */
    public static void drawOverlayBox(int left, int top, int right, int bottom, int sideColor, int middleColor) {
        GuiDraw.drawGradientRectDirect(left, top, right - 1, top + 1, sideColor, sideColor); // top
        GuiDraw.drawGradientRectDirect(right - 1, top, right, bottom - 1, sideColor, sideColor); // right
        GuiDraw.drawGradientRectDirect(left, top + 1, left + 1, bottom, sideColor, sideColor); // left
        GuiDraw.drawGradientRectDirect(left + 1, bottom - 1, right, bottom, sideColor, sideColor); // bottom
        GuiDraw.drawGradientRectDirect(left + 1, top + 1, right - 1, bottom - 1, middleColor, middleColor); // middle
    }

    /**
     * Draws a box with different colored sides and center over a given chunk.
     */
    public static void drawOverlayBox(int chunkX, int chunkZ, int sideColor, int middleColor) {
        drawOverlayBox(chunkX * 16, chunkZ * 16, (chunkX + 1) * 16, (chunkZ + 1) * 16, sideColor, middleColor);
    }

    /**
     * Converts an (A)RGB integer color into an array of floats, for use in GL calls
     * @return float[]{R, G, B, A}
     */
    public static float[] floats(int argb) {
        return new float[] {
                (float) (argb >> 16 & 255) / 255.0F,
                (float) (argb >> 8 & 255) / 255.0F,
                (float) (argb & 255) / 255.0F,
                (float) (argb >> 24 & 255) / 255.0F
        };
    }

    /**
     * Get which block the mouse is hovering over.
     * <br>
     * The parameters passed to this should be the parameters of <code>updateHovered</code>
     * @return A {@link BlockPos} with x and z coordinates of the block the mouse is over, and a y coordinate of 0
     */
    public static BlockPos getMouseBlockPos(double mouseX, double mouseY, double cameraX, double cameraZ, double scale) {
        int mouseBlockX = (int) Math.floor((mouseX - Minecraft.getMinecraft().displayWidth / 2.0) / scale + cameraX);
        int mouseBlockZ = (int) Math.floor((mouseY - Minecraft.getMinecraft().displayHeight / 2.0) / scale + cameraZ);
        return new BlockPos(mouseBlockX, 0, mouseBlockZ);
    }

    /**
     * Get the color of a fluid, respecting the fluid color override list.
     * <br>
     * Will also apply a fix for incorrect GregTech fluid colors.
     */
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
