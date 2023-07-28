package hellfall.visualores.database.immersiveengineering;

import hellfall.visualores.database.IClientCachePerDimOnly;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.nbt.NBTTagCompound;

import java.util.Collection;

public class IEClientCache implements IClientCachePerDimOnly {
    public static final IEClientCache instance = new IEClientCache();

    private final Int2ObjectMap<IEDimensionCache> cache = new Int2ObjectArrayMap<>();

    @Override
    public void setupCacheFiles() {
        //... NetHandlerPlayClient.handleCollectItem()? or ParticleItemPickup constructor?
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
        return null;
    }

    @Override
    public void readDimFile(String prefix, int dim, NBTTagCompound data) {

    }
}
