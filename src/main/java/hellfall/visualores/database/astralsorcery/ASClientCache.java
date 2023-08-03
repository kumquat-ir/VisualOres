package hellfall.visualores.database.astralsorcery;

import hellfall.visualores.database.IClientCachePerDimOnly;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.ChunkPos;
import net.minecraftforge.fluids.Fluid;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ASClientCache implements IClientCachePerDimOnly {
    public static final ASClientCache instance = new ASClientCache();

    private final Int2ObjectMap<ASDimensionCache> cache = new Int2ObjectArrayMap<>();

    public void addStarfields() {
        int dim = Minecraft.getMinecraft().world.provider.getDimension();
        if (!cache.containsKey(dim)) {
            cache.put(dim, new ASDimensionCache());
        }
        cache.get(dim).addStarfields();
    }

    public List<StarfieldPosition> getStarfieldsInBounds(int dim, int[] bounds) {
        if (cache.containsKey(dim)) {
            return cache.get(dim).getStarfieldsInArea(
                    new ChunkPos(bounds[0] >> 4, bounds[1] >> 4),
                    new ChunkPos((bounds[0] + bounds[2]) >> 4, (bounds[1] + bounds[3]) >> 4)
            );
        }
        return new ArrayList<>();
    }

    public void setNeromanticFluid(int dim, ChunkPos pos, Fluid fluid) {
        if (!cache.containsKey(dim)) {
            cache.put(dim, new ASDimensionCache());
        }
        cache.get(dim).setNeromanticFluid(pos, fluid);
    }

    public List<NeromanticPosition> getNeromanticVeinsInBounds(int dim, int[] bounds) {
        if (cache.containsKey(dim)) {
            return cache.get(dim).getNeromanticVeinsInArea(
                    new ChunkPos(bounds[0] >> 4, bounds[1] >> 4),
                    new ChunkPos((bounds[0] + bounds[2]) >> 4, (bounds[1] + bounds[3]) >> 4)
            );
        }
        return new ArrayList<>();
    }

    @Override
    public void setupCacheFiles() {
        addDimFiles("starfields_");
        addDimFiles("neromantic_");
    }

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
            return cache.get(dim).toNBT(prefix);
        }
        return null;
    }

    @Override
    public void readDimFile(String prefix, int dim, NBTTagCompound data) {
        if (!cache.containsKey(dim)) {
            cache.put(dim, new ASDimensionCache());
        }
        cache.get(dim).fromNBT(prefix, data);
    }
}
