package hellfall.visualores.map.generic;

import codechicken.lib.gui.GuiDraw;
import gregtech.api.worldgen.bedrockFluids.BedrockFluidVeinHandler;
import hellfall.visualores.database.ClientCache;
import hellfall.visualores.database.fluid.UndergroundFluidPosition;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;

import java.util.ArrayList;
import java.util.List;

public class UndergroundFluidRenderLayer extends RenderLayer {
    public static ButtonState.Button UNDERGROUND_FLUIDS_BUTTON = new ButtonState.Button("undergroundfluid", 1);

    protected List<UndergroundFluidPosition> visibleFluids = new ArrayList<>();

    public UndergroundFluidRenderLayer() {
        super(UNDERGROUND_FLUIDS_BUTTON);
    }

    @Override
    public void render(double cameraX, double cameraZ, double scale) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(-cameraX, -cameraZ, 0);
        for (var fluidPos : visibleFluids) {
            int sideColor = (fluidPos.color & 0x00FFFFFF) + 0xDD000000;
            int midColor = (fluidPos.color & 0x00FFFFFF) + 0x77000000;

            int t = adjustForBadCoords(fluidPos.pos.z * BedrockFluidVeinHandler.VEIN_CHUNK_SIZE) * 16;
            int b = adjustForBadCoords((fluidPos.pos.z + 1) * BedrockFluidVeinHandler.VEIN_CHUNK_SIZE) * 16;
            int l = adjustForBadCoords(fluidPos.pos.x * BedrockFluidVeinHandler.VEIN_CHUNK_SIZE) * 16;
            int r = adjustForBadCoords((fluidPos.pos.x + 1) * BedrockFluidVeinHandler.VEIN_CHUNK_SIZE) * 16;

            GuiDraw.drawGradientRectDirect(l, t, r - 1, t + 1, sideColor, sideColor); // top
            GuiDraw.drawGradientRectDirect(r - 1, t, r, b - 1, sideColor, sideColor); // right
            GuiDraw.drawGradientRectDirect(l, t + 1, l + 1, b, sideColor, sideColor); // left
            GuiDraw.drawGradientRectDirect(l + 1, b - 1, r, b, sideColor, sideColor); // bottom
            GuiDraw.drawGradientRectDirect(l + 1, t + 1, r - 1, b - 1, midColor, midColor); // middle
        }
        GlStateManager.popMatrix();
    }

    @Override
    public void updateVisibleArea(int dimensionID, int[] visibleBounds) {
        visibleFluids = ClientCache.instance.getFluidsInArea(dimensionID, visibleBounds);
    }

    @Override
    public List<String> getTooltip(double mouseX, double mouseY, double cameraX, double cameraZ, double scale) {
        List<String> tooltip = new ArrayList<>();

        int mouseBlockX = (int) Math.floor((mouseX - Minecraft.getMinecraft().displayWidth / 2.0) / scale + cameraX);
        int mouseBlockZ = (int) Math.floor((mouseY - Minecraft.getMinecraft().displayHeight / 2.0) / scale + cameraZ);

        //todo BedrockFluidVeinHandler.getVeinCoord()
        int mouseFieldX = (mouseBlockX >> 4) / BedrockFluidVeinHandler.VEIN_CHUNK_SIZE;
        int mouseFieldZ = (mouseBlockZ >> 4) / BedrockFluidVeinHandler.VEIN_CHUNK_SIZE;

        for (var fluidPos : visibleFluids) {
            if (mouseFieldX == fluidPos.pos.x && mouseFieldZ == fluidPos.pos.z) {
                tooltip.add(I18n.format("terminal.prospector.fluid.info", fluidPos.name, fluidPos.yield, fluidPos.percent));
                break;
            }
        }

        return tooltip;
    }

    private int adjustForBadCoords(int chunkCoord) {
        // todo uncomment when gtceu updates
        // will need to add a packet for dual-side and some way to guess vein sizes around the origin if client-only
        if (/*BedrockFluidVeinHandler.saveDataVersion < 2*/ true) {
            if (chunkCoord <= 0) {
                chunkCoord = chunkCoord - 7;
            }
        }
        return chunkCoord;
    }
}
