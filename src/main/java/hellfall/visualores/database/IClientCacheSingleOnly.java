package hellfall.visualores.database;

import net.minecraft.nbt.NBTTagCompound;

import java.util.Collection;

/**
 * A client cache with only non-dimension-specific files.
 */
public interface IClientCacheSingleOnly extends IClientCache {
    @Override
    default Collection<Integer> getExistingDimensions(String prefix) {
        return null;
    }

    @Override
    default NBTTagCompound saveDimFile(String prefix, int dim) {
        return null;
    }

    @Override
    default void readDimFile(String prefix, int dim, NBTTagCompound data) {}

    @Override
    default void addDimFiles(String prefix) {
        throw new IllegalStateException("Tried to add dim files for single-only cache!");
    }
}
