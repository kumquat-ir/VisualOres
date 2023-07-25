package hellfall.visualores.database.gregtech.fluid;

import gregtech.api.worldgen.bedrockFluids.BedrockFluidVeinHandler;
import gregtech.api.worldgen.bedrockFluids.ChunkPosDimension;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FluidCache {
    private final Map<ChunkPosDimension, UndergroundFluidPosition> fluidCache = new HashMap<>();

    public void addFluid(int dim, int fieldX, int fieldZ, String fluid, int yield, double percent) {
        var fluidPos = new UndergroundFluidPosition(dim, fieldX, fieldZ, fluid, yield, percent);
        fluidCache.put(fluidPos.pos, fluidPos);
    }

    public List<UndergroundFluidPosition> getFluidsInBounds(int dim, BlockPos topLeft, BlockPos bottomRight) {
        //todo BedrockFluidVeinHandler.getVeinCoord()
        int[] topLeftField = {(topLeft.getX() >> 4) / BedrockFluidVeinHandler.VEIN_CHUNK_SIZE, (topLeft.getZ() >> 4) / BedrockFluidVeinHandler.VEIN_CHUNK_SIZE};
        int[] bottomRightField = {(bottomRight.getX() >> 4) / BedrockFluidVeinHandler.VEIN_CHUNK_SIZE, (bottomRight.getZ() >> 4) / BedrockFluidVeinHandler.VEIN_CHUNK_SIZE};
        List<UndergroundFluidPosition> found = new ArrayList<>();
        for (int i = topLeftField[0] - 1; i <= bottomRightField[0]; i++) {
            for (int j = topLeftField[1] - 1; j <= bottomRightField[1]; j++) {
                ChunkPosDimension currentPos = new ChunkPosDimension(dim, i, j);
                if (fluidCache.containsKey(currentPos)) {
                    found.add(fluidCache.get(currentPos));
                }
            }
        }
        return found;
    }

    public void fromNBT(NBTTagCompound nbt) {
        var fluidList = nbt.getTagList("fluids", 10);
        for (var fluidTagRaw : fluidList) {
            if (fluidTagRaw instanceof NBTTagCompound fluidTag) {
                var fluidPos = UndergroundFluidPosition.fromNBT(fluidTag);
                fluidCache.put(fluidPos.pos, fluidPos);
            }
        }
    }

    public NBTTagCompound toNBT() {
        var result = new NBTTagCompound();
        var fluidList = new NBTTagList();
        for (var fluidPos : fluidCache.values()) {
            fluidList.appendTag(fluidPos.toNBT());
        }
        result.setTag("fluids", fluidList);
        return result;
    }

    public void clear() {
        fluidCache.clear();
    }
}
