package hellfall.visualores.database;

import net.minecraft.nbt.NBTTagCompound;

/**
 * A client cache with only dimension-specific files.
 */
public interface IClientCachePerDimOnly extends IClientCache {
    @Override
    default NBTTagCompound saveSingleFile(String name) {
        return null;
    }

    @Override
    default void readSingleFile(String name, NBTTagCompound data) {}

    @Override
    default void addSingleFile(String name) {
        throw new IllegalStateException("Tried to add single file for dim-only cache!");
    }
}
