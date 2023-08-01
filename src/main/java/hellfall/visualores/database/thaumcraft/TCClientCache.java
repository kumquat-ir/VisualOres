package hellfall.visualores.database.thaumcraft;

import hellfall.visualores.database.IClientCachePerDimOnly;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.ChunkPos;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class TCClientCache implements IClientCachePerDimOnly {
    public static final TCClientCache instance = new TCClientCache();

    private final Int2ObjectMap<TCDimensionCache> cache = new Int2ObjectArrayMap<>();

    public void addChunk(short base, float aura, float flux) {
        int dim = Minecraft.getMinecraft().player.dimension;
        if (!cache.containsKey(dim)) {
            cache.put(dim, new TCDimensionCache());
        }
        cache.get(dim).addChunk(base, aura, flux);
    }

    public List<AuraFluxPosition> getVeinsInArea(int dim, int[] bounds) {
        if (cache.containsKey(dim)) {
            return cache.get(dim).getVeinsInArea(
                    new ChunkPos(bounds[0] >> 4, bounds[1] >> 4),
                    new ChunkPos((bounds[0] + bounds[2]) >> 4, (bounds[1] + bounds[3]) >> 4)
            );
        }
        return new ArrayList<>();
    }

    @Override
    public void setupCacheFiles() {
        addDimFiles("aura_");
    }

    @Override
    public void clear() {
        cache.clear();
    }

    @Override
    public Collection<Integer> getExistingDimensions(String prefix) {
        return cache.keySet();
    }

    @Override
    public NBTTagCompound saveDimFile(String prefix, int dim) {
        if (cache.containsKey(dim)) {
            return cache.get(dim).toNBT();
        }
        return null;
    }

    @Override
    public void readDimFile(String prefix, int dim, NBTTagCompound data) {
        if (!cache.containsKey(dim)) {
            cache.put(dim, new TCDimensionCache());
        }
        cache.get(dim).fromNBT(data);
    }
}
