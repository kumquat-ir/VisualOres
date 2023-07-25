package hellfall.visualores.map.generic;

import codechicken.lib.gui.GuiDraw;
import hellfall.visualores.VOConfig;
import hellfall.visualores.database.ClientCache;
import hellfall.visualores.database.ore.OreVeinPosition;
import hellfall.visualores.map.DrawUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OreRenderLayer extends RenderLayer {
    protected static final ResourceLocation STONE = new ResourceLocation("textures/blocks/stone.png");
    protected static final ResourceLocation DEPLETED = new ResourceLocation("visualores", "textures/depleted.png");
    protected List<OreVeinPosition> visibleVeins = new ArrayList<>();
    protected List<OreVeinPosition> hoveredVeins = new ArrayList<>();

    // should be shared between all renderer instances
    protected static OreVeinPosition waypointVein;

    public OreRenderLayer(String key) {
        super(key);
    }

    @Override
    public void render(double cameraX, double cameraZ, double scale) {
        double clampedScale = Math.max(scale, VOConfig.client.gregtech.oreScaleStop);
        int iconSize = VOConfig.client.gregtech.oreIconSize;

        for (OreVeinPosition vein : visibleVeins) {
            GlStateManager.pushMatrix();

            // -> scale = pixels, origin = center of block vein is in
            GlStateManager.translate(vein.x - 0.5 - cameraX, vein.z - 0.5 - cameraZ, 0);
            GlStateManager.scale(1 / clampedScale, 1 / clampedScale, 1);

            float[] colors = DrawUtils.floats(vein.veinInfo.color);
            GlStateManager.color(1, 1, 1, 1);

            Minecraft.getMinecraft().getTextureManager().bindTexture(STONE);
            Gui.drawModalRectWithCustomSizedTexture(-iconSize / 2, -iconSize / 2, 0, 0, iconSize, iconSize, iconSize, iconSize);

            Minecraft.getMinecraft().getTextureManager().bindTexture(vein.veinInfo.texture);
            GlStateManager.color(colors[0], colors[1], colors[2], 1);
            Gui.drawModalRectWithCustomSizedTexture(-iconSize / 2, -iconSize / 2, 0, 0, iconSize, iconSize, iconSize, iconSize);

            GlStateManager.color(1, 1, 1, 1);
            if (vein.depleted) {
                GuiDraw.drawRect(-iconSize / 2, -iconSize / 2, iconSize, iconSize, 0x96000000);
                Minecraft.getMinecraft().getTextureManager().bindTexture(DEPLETED);
                Gui.drawModalRectWithCustomSizedTexture(-iconSize / 2, -iconSize / 2, 0, 0, iconSize, iconSize, iconSize, iconSize);
            }

            if (vein == waypointVein) {
                int thickness = iconSize / 8;
                int color = 0xFFFFD700;

                GuiDraw.drawRect(-thickness - iconSize / 2, -thickness - iconSize / 2, thickness + iconSize, thickness, color);
                GuiDraw.drawRect(iconSize / 2, -thickness - iconSize / 2, thickness, thickness + iconSize, color);
                GuiDraw.drawRect(-thickness - iconSize / 2, -iconSize / 2, thickness, thickness + iconSize, color);
                GuiDraw.drawRect(-iconSize / 2, iconSize / 2, thickness + iconSize, thickness, color);
            }

            GlStateManager.popMatrix();
        }
    }

    @Override
    public void updateVisibleArea(int dimensionID, int[] visibleBounds) {
        visibleVeins = ClientCache.instance.getVeinsInArea(dimensionID, visibleBounds);
    }

    @Override
    public void updateHovered(double mouseX, double mouseY, double cameraX, double cameraZ, double scale) {
        hoveredVeins.clear();
        double clampedScale = Math.max(scale, VOConfig.client.gregtech.oreScaleStop);
        double iconRadius = VOConfig.client.gregtech.oreIconSize / 2.0 * (scale / clampedScale);
        Minecraft mc = Minecraft.getMinecraft();
        mouseX = mouseX - mc.displayWidth / 2.0;
        mouseY = mouseY - mc.displayHeight / 2.0;
        for (OreVeinPosition vein : visibleVeins) {
            double scaledVeinX = (vein.x - 0.5 - cameraX) * scale;
            double scaledVeinZ = (vein.z - 0.5 - cameraZ) * scale;
            if (mouseX > scaledVeinX - iconRadius && mouseX < scaledVeinX + iconRadius &&
                    mouseY > scaledVeinZ - iconRadius && mouseY < scaledVeinZ + iconRadius) {
                hoveredVeins.add(vein);
            }
        }
        // topmost vein first
        Collections.reverse(hoveredVeins);
    }

    @Override
    public List<String> getTooltip() {
        List<String> tooltip = new ArrayList<>();
        if (VOConfig.client.stackTooltips) {
            for (OreVeinPosition vein : hoveredVeins) {
                tooltip.addAll(vein.getTooltipStrings());
            }
        }
        else if (!hoveredVeins.isEmpty()){
            tooltip = hoveredVeins.get(0).getTooltipStrings();
        }
        return tooltip;
    }

    @Override
    public boolean onActionKey() {
        if (hoveredVeins.isEmpty()) return false;
        hoveredVeins.get(0).depleted = !hoveredVeins.get(0).depleted;
        return true;
    }

    @Override
    public boolean onDoubleClick() {
        if (hoveredVeins.isEmpty()) return false;
        OreVeinPosition vein = hoveredVeins.get(0);
        waypointVein = toggleWaypoint(vein.getTooltipStrings().get(0), null, vein.x, 64, vein.z) ? vein : null;
        return true;
    }
}
