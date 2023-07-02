package hellfall.visualores.database;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DimensionCache {
    private final Map<GridPos, GridCache> cache = new HashMap<>();

    public boolean dirty;

    public void addVein(int x, int z, int gridX, int gridZ, String name) {
        GridPos key = new GridPos(gridX, gridZ);
        if (!cache.containsKey(key)) {
            cache.put(key, new GridCache());
        }
        cache.get(key).addVein(x, z, name);
        dirty = true;
    }

    public NBTTagCompound toNBT() {
        return toNBT(new NBTTagCompound());
    }

    public NBTTagCompound toNBT(NBTTagCompound nbt) {
        for (GridPos key : cache.keySet()) {
            nbt.setTag(key.x + "," + key.z, cache.get(key).toNBT());
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
        GridPos topLeft = new GridPos(pos.add(-blockRadius, 0, -blockRadius));
        GridPos bottomRight = new GridPos(pos.add(blockRadius, 0, blockRadius));
        List<OreVeinPosition> found = new ArrayList<>();
        for (int i = topLeft.x; i <= bottomRight.x; i++) {
            for (int j = topLeft.z; j <= bottomRight.z; j++) {
                GridPos curPos = new GridPos(i, j);
                if (cache.containsKey(curPos)) {
                    found.addAll(cache.get(curPos).getVeinsMatching(veinpos ->
                            veinpos.x >= pos.getX() - blockRadius && veinpos.x <= pos.getX() + blockRadius &&
                                    veinpos.z >= pos.getZ() - blockRadius && veinpos.z <= pos.getZ() + blockRadius
                    ));
                }
            }
        }
        return found;
    }
}
