package hellfall.visualores.database.gregtech.ore;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DimensionCache {
    private final Map<GridPos, GridCache> cache = new HashMap<>();

    public boolean dirty;

    public boolean addVein(int x, int z, int gridX, int gridZ, String name) {
        GridPos key = new GridPos(gridX, gridZ);
        if (!cache.containsKey(key)) {
            cache.put(key, new GridCache());
        }
        boolean added = cache.get(key).addVein(x, z, name);
        dirty = added || dirty;
        return added;
    }

    public NBTTagCompound toNBT(boolean saveDepleted) {
        return toNBT(new NBTTagCompound(), saveDepleted);
    }

    public NBTTagCompound toNBT(NBTTagCompound nbt) {
        return toNBT(nbt, false);
    }

    public NBTTagCompound toNBT(NBTTagCompound nbt, boolean saveDepleted) {
        for (GridPos key : cache.keySet()) {
            nbt.setTag(key.x + "," + key.z, cache.get(key).toNBT(saveDepleted));
        }
        return nbt;
    }

    public void fromNBT(NBTTagCompound tag) {
        for (String gridpos : tag.getKeySet()) {
            String[] splitpos = gridpos.split(",");
            GridPos key = new GridPos(Integer.parseInt(splitpos[0]), Integer.parseInt(splitpos[1]));
            if (!cache.containsKey(key)) {
                cache.put(key, new GridCache());
            }
            cache.get(key).fromNBT(tag.getTagList(gridpos, 10));
        }
    }

    public List<OreVeinPosition> getNearbyVeins(BlockPos pos, int blockRadius) {
        return getVeinsInBounds(pos.add(-blockRadius, 0, -blockRadius), pos.add(blockRadius, 0, blockRadius));
    }

    public List<OreVeinPosition> getVeinsInBounds(BlockPos topLeftBlock, BlockPos bottomRightBlock) {
        GridPos topLeft = new GridPos(topLeftBlock);
        GridPos bottomRight = new GridPos(bottomRightBlock);
        List<OreVeinPosition> found = new ArrayList<>();
        for (int i = topLeft.x; i <= bottomRight.x; i++) {
            for (int j = topLeft.z; j <= bottomRight.z; j++) {
                GridPos curPos = new GridPos(i, j);
                if (cache.containsKey(curPos)) {
                    found.addAll(cache.get(curPos).getVeinsMatching(veinpos ->
                            veinpos.x >= topLeftBlock.getX() && veinpos.x <= bottomRightBlock.getX() &&
                                    veinpos.z >= topLeftBlock.getZ() && veinpos.z <= bottomRightBlock.getZ()
                    ));
                }
            }
        }
        return found;
    }

    public List<OreVeinPosition> getVeinsInChunk(ChunkPos pos) {
        GridPos gpos = new GridPos(pos);
        if (cache.containsKey(gpos)) {
            return cache.get(gpos).getVeinsMatching(veinpos -> pos.equals(new ChunkPos(veinpos.x >> 4, veinpos.z >> 4)));
        }
        return new ArrayList<>();
    }

    public void removeAllInChunk(ChunkPos pos) {
        GridPos gpos = new GridPos(pos);
        if (cache.containsKey(gpos)) {
            cache.get(gpos).removeVeinsMatching(veinpos -> pos.equals(new ChunkPos(veinpos.x >> 4, veinpos.z >> 4)));
        }
    }
}
