package hellfall.visualores.database.immersiveengineering;

import blusunrize.immersiveengineering.common.util.ItemNBTHelper;
import hellfall.visualores.VisualOres;
import hellfall.visualores.database.IClientCachePerDimOnly;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.ChunkPos;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class IEClientCache implements IClientCachePerDimOnly {
    public static final IEClientCache instance = new IEClientCache();

    private final Int2ObjectMap<IEDimensionCache> cache = new Int2ObjectArrayMap<>();

    public void readCoresampleNBT(ItemStack stack) {
        NBTTagCompound nbt = ItemNBTHelper.getTag(stack);
        VisualOres.LOGGER.info(nbt);
        if (!nbt.hasKey("coords")) return;
        int dim = nbt.getIntArray("coords")[0];

        if (!cache.containsKey(dim)) {
            cache.put(dim, new IEDimensionCache());
        }
        cache.get(dim).addVein(stack, nbt);
    }

    public List<ExcavatorVeinPosition> getVeinsInArea(int dim, int[] bounds) {
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
        addDimFiles("excavator_");
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
            cache.put(dim, new IEDimensionCache());
        }
        cache.get(dim).fromNBT(data);
    }
}
