package hellfall.visualores.database.gregtech.ore;

import hellfall.visualores.Tags;
import hellfall.visualores.VOConfig;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldSavedData;

import javax.annotation.Nonnull;

public class ServerCacheSaveData extends WorldSavedData {
    private static final String DATA_NAME = Tags.MODID;

    private DimensionCache backingCache;
    private NBTTagCompound toRead;

    @SuppressWarnings("unused") // this constructor is required to exist, but is never used directly
    public ServerCacheSaveData(String name) {
        super(name);
    }

    public ServerCacheSaveData() {
        super(DATA_NAME);
    }

    public static ServerCacheSaveData init(World world, DimensionCache backingCache) {
        MapStorage storage = world.getPerWorldStorage();
        ServerCacheSaveData instance = (ServerCacheSaveData) storage.getOrLoadData(ServerCacheSaveData.class, DATA_NAME);

        if (instance == null) {
            instance = new ServerCacheSaveData();
            storage.setData(DATA_NAME, instance);

            if (VOConfig.server.gregtech.doRetrogen) {
                OreCacheRetrogenerator.doRetrogen(world);
            }

            // always save the cache on initial load as a marker to not redo retrogen
            instance.markDirty();
        }

        instance.backingCache = backingCache;
        if (backingCache.dirty) {
            instance.markDirty();
        }
        if (instance.toRead != null) {
            backingCache.fromNBT(instance.toRead);
            instance.toRead = null;
        }

        return instance;
    }

    @Override
    public void readFromNBT(@Nonnull NBTTagCompound nbt) {
        if (backingCache != null) {
            backingCache.fromNBT(nbt);
        }
        else {
            toRead = nbt;
        }
    }

    @Override
    public @Nonnull NBTTagCompound writeToNBT(@Nonnull NBTTagCompound compound) {
        return backingCache.toNBT(compound);
    }
}
