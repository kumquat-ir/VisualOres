package hellfall.visualores.map;

import hellfall.visualores.VOConfig;
import hellfall.visualores.database.ClientCache;
import hellfall.visualores.database.OreVeinPosition;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

@SideOnly(Side.CLIENT)
public class GenericMapRenderer {
    protected int mouseX;
    protected int mouseY;

    protected int dimensionID;

    protected int[] visibleBounds = new int[4];
    protected List<OreVeinPosition> visibleVeins;

    public void updateMousePosition(int mouseX, int mouseY) {
        this.mouseX = mouseX;
        this.mouseY = mouseY;
    }

    public void updateVisibleArea(int dim, int x, int y, int w, int h) {
        if (dimensionID != dim || visibleBounds[0] != x || visibleBounds[1] != y || visibleBounds[2] != w || visibleBounds[3] != h) {
            dimensionID = dim;
            visibleBounds[0] = x;
            visibleBounds[1] = y;
            visibleBounds[2] = w;
            visibleBounds[3] = h;

            visibleVeins = ClientCache.instance.getVeinsInArea(dimensionID, visibleBounds);
        }
    }

    /**
     * EXPECTED GL STATE:
     * <br>
     * Coordinates: 1 unit = 1 block, positioned at block positions in world (ie. drawing at 5,12 draws on the block at 5,12)
     * @param cameraX X position of the center block of the view
     * @param cameraZ Z position of the center block of the view
     * @param scale Scale of the camera, such that scaling by <code>1/scale</code> results in 1 unit = 1 pixel
     */
    public void render(double cameraX, double cameraZ, double scale) {
        if (ButtonState.isEnabled("ORE_VEINS")) {
            double clampedScale = Math.max(scale, VOConfig.client.oreScaleStop);

            for (OreVeinPosition vein : visibleVeins) {
                GlStateManager.pushMatrix();

                GlStateManager.translate(vein.x - 0.5 - cameraX, vein.z - 0.5 - cameraZ, 0);
                GlStateManager.scale(1 / clampedScale, 1 / clampedScale, 1);
                float[] colors = floats(vein.veinInfo.color);
                GlStateManager.color(1, 1, 1, 1);

                // these are 16x16, we want them to render at 32x32
//                GlStateManager.scale(2, 2, 1);
                Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation("textures/blocks/stone.png"));
                Gui.drawModalRectWithCustomSizedTexture(-16, -16, 0, 0, 32, 32, 32, 32);

                Minecraft.getMinecraft().getTextureManager().bindTexture(vein.veinInfo.texture);
                GlStateManager.color(colors[0], colors[1], colors[2], 1);
                Gui.drawModalRectWithCustomSizedTexture(-16, -16, 0, 0, 32, 32, 32, 32);
//                GlStateManager.scale(0.5, 0.5, 1);

                GlStateManager.popMatrix();
            }
        }
    }

    public void mousePressed() {

    }

    public static float[] floats(int rgb) {
        return new float[] { (float) (rgb >> 16 & 255) / 255.0F, (float) (rgb >> 8 & 255) / 255.0F,
                (float) (rgb & 255) / 255.0F };
    }
}
