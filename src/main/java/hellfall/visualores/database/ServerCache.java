package hellfall.visualores.database;

import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;

public class ServerCache {
    private final Map<Integer, DimensionCache> cache = new HashMap<>();
    private final Map<Integer, ServerCacheSaveData> saveData = new HashMap<>();

    public void maybeInitWorld(World world) {
        int dim = world.provider.getDimension();
        if (!cache.containsKey(dim)) {
            cache.put(dim, new DimensionCache());
        }
        if (!saveData.containsKey(dim)) {
            saveData.put(dim, ServerCacheSaveData.init(world, cache.get(dim)));
        }
    }

    public void invalidateWorld(World world) {
        int dim = world.provider.getDimension();
        cache.remove(dim);
        saveData.remove(dim);
    }

    public void addVein(int dim, int x, int z, int gridX, int gridZ, String name) {
        if (!cache.containsKey(dim)) {
            cache.put(dim, new DimensionCache());
        }
        cache.get(dim).addVein(x, z, gridX, gridZ, name);
        if (saveData.containsKey(dim)) {
            saveData.get(dim).markDirty();
        }
    }

    public void clear() {
        cache.clear();
        saveData.clear();
    }
}
