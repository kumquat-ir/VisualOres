package hellfall.visualores.database;

import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class WorldCache {
    protected final Map<Integer, DimensionCache> cache = new HashMap<>();

    public boolean addVein(int dim, int x, int z, int gridX, int gridZ, String name) {
        if (!cache.containsKey(dim)) {
            cache.put(dim, new DimensionCache());
        }
        return cache.get(dim).addVein(x, z, gridX, gridZ, name);
    }

    public List<OreVeinPosition> getNearbyVeins(int dim, BlockPos pos, int blockRadius) {
        if (cache.containsKey(dim)) {
            return cache.get(dim).getNearbyVeins(pos, blockRadius);
        }
        return new ArrayList<>();
    }

    public List<OreVeinPosition> getVeinsInArea(int dim, int[] bounds) {
        if (cache.containsKey(dim)) {
            return cache.get(dim).getVeinsInBounds(
                    new BlockPos(bounds[0], 0, bounds[1]),
                    new BlockPos(bounds[0] + bounds[2], 0, bounds[1] + bounds[3])
            );
        }
        return new ArrayList<>();
    }

    public void clear() {
        cache.clear();
    }
}
