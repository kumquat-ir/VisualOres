package hellfall.visualores.map.layers.gregtech;

import gregtech.api.worldgen.bedrockFluids.BedrockFluidVeinHandler;
import hellfall.visualores.database.gregtech.GTClientCache;
import hellfall.visualores.database.gregtech.fluid.UndergroundFluidPosition;
import hellfall.visualores.map.DrawUtils;
import hellfall.visualores.map.layers.RenderLayer;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UndergroundFluidRenderLayer extends RenderLayer {
    protected List<UndergroundFluidPosition> visibleFluids = new ArrayList<>();
    protected UndergroundFluidPosition hovered;

    public UndergroundFluidRenderLayer(String key) {
        super(key);
    }

    @Override
    public void render(double cameraX, double cameraZ, double scale) {
        for (var fluidPos : visibleFluids) {
            int sideColor = (fluidPos.color & 0x00FFFFFF) + 0xDD000000;
            int midColor = (fluidPos.color & 0x00FFFFFF) + 0x77000000;

            int t = adjustForBadCoords(fluidPos.pos.z * BedrockFluidVeinHandler.VEIN_CHUNK_SIZE) * 16;
            int b = adjustForBadCoords((fluidPos.pos.z + 1) * BedrockFluidVeinHandler.VEIN_CHUNK_SIZE) * 16;
            int l = adjustForBadCoords(fluidPos.pos.x * BedrockFluidVeinHandler.VEIN_CHUNK_SIZE) * 16;
            int r = adjustForBadCoords((fluidPos.pos.x + 1) * BedrockFluidVeinHandler.VEIN_CHUNK_SIZE) * 16;

            DrawUtils.drawOverlayBox(l, t, r, b, sideColor, midColor);
        }
    }

    @Override
    public void updateVisibleArea(int dimensionID, int[] visibleBounds) {
        visibleFluids = GTClientCache.instance.getFluidsInArea(dimensionID, visibleBounds);
    }

    @Override
    public void updateHovered(double mouseX, double mouseY, double cameraX, double cameraZ, double scale) {
        BlockPos mouseBlock = DrawUtils.getMouseBlockPos(mouseX, mouseY, cameraX, cameraZ, scale);

        //todo BedrockFluidVeinHandler.getVeinCoord()
        int mouseFieldX = (mouseBlock.getX() >> 4) / BedrockFluidVeinHandler.VEIN_CHUNK_SIZE;
        int mouseFieldZ = (mouseBlock.getZ() >> 4) / BedrockFluidVeinHandler.VEIN_CHUNK_SIZE;

        hovered = null;
        for (var fluidPos : visibleFluids) {
            if (mouseFieldX == fluidPos.pos.x && mouseFieldZ == fluidPos.pos.z) {
                hovered = fluidPos;
                break;
            }
        }
    }

    @Override
    public List<String> getTooltip() {
        if (hovered == null) return null;
        return Collections.singletonList(I18n.format("terminal.prospector.fluid.info", hovered.name, hovered.yield, hovered.percent));
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
