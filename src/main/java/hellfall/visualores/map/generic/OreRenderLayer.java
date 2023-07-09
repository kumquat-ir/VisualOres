package hellfall.visualores.map.generic;

import hellfall.visualores.VOConfig;
import hellfall.visualores.database.ClientCache;
import hellfall.visualores.database.OreVeinPosition;
import hellfall.visualores.map.DrawUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class OreRenderLayer extends RenderLayer {
    public static final int ORE_ICON_SIZE = 32;

    protected List<OreVeinPosition> visibleVeins;

    public OreRenderLayer(ButtonState.Button button) {
        super(button);
    }

    @Override
    public void render(double cameraX, double cameraZ, double scale) {
        double clampedScale = Math.max(scale, VOConfig.client.oreScaleStop);

        for (OreVeinPosition vein : visibleVeins) {
            GlStateManager.pushMatrix();

            // -> scale = pixels, origin = center of block vein is in
            GlStateManager.translate(vein.x - 0.5 - cameraX, vein.z - 0.5 - cameraZ, 0);
            GlStateManager.scale(1 / clampedScale, 1 / clampedScale, 1);

            float[] colors = DrawUtils.floats(vein.veinInfo.color);
            GlStateManager.color(1, 1, 1, 1);

            Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation("textures/blocks/stone.png"));
            Gui.drawModalRectWithCustomSizedTexture(-ORE_ICON_SIZE / 2, -ORE_ICON_SIZE / 2, 0, 0, ORE_ICON_SIZE, ORE_ICON_SIZE, ORE_ICON_SIZE, ORE_ICON_SIZE);

            Minecraft.getMinecraft().getTextureManager().bindTexture(vein.veinInfo.texture);
            GlStateManager.color(colors[0], colors[1], colors[2], 1);
            Gui.drawModalRectWithCustomSizedTexture(-ORE_ICON_SIZE / 2, -ORE_ICON_SIZE / 2, 0, 0, ORE_ICON_SIZE, ORE_ICON_SIZE, ORE_ICON_SIZE, ORE_ICON_SIZE);

            GlStateManager.popMatrix();
        }
    }

    @Override
    public void updateVisibleArea(int dimensionID, int[] visibleBounds) {
        visibleVeins = ClientCache.instance.getVeinsInArea(dimensionID, visibleBounds);
    }

    @Override
    public List<String> getTooltip(double mouseX, double mouseY, double cameraX, double cameraZ, double scale) {
        List<String> tooltip = new ArrayList<>();
        double clampedScale = Math.max(scale, VOConfig.client.oreScaleStop);
        double iconRadius = ORE_ICON_SIZE / 2.0 * (scale / clampedScale);
        Minecraft mc = Minecraft.getMinecraft();
        ScaledResolution res = new ScaledResolution(mc);
        mouseX = mouseX * res.getScaleFactor() - mc.displayWidth / 2.0;
        mouseY = mouseY * res.getScaleFactor() - mc.displayHeight / 2.0;
        for (OreVeinPosition vein : visibleVeins) {
            double scaledVeinX = (vein.x - 0.5 - cameraX) * scale;
            double scaledVeinZ = (vein.z - 0.5 - cameraZ) * scale;
            if (mouseX > scaledVeinX - iconRadius && mouseX < scaledVeinX + iconRadius &&
                mouseY > scaledVeinZ - iconRadius && mouseY < scaledVeinZ + iconRadius) {
                if (VOConfig.client.stackTooltips) {
                    tooltip.addAll(0, vein.veinInfo.tooltipStrings);
                }
                else {
                    tooltip = vein.veinInfo.tooltipStrings;
                }
            }
        }
        return tooltip;
    }
}
