package hellfall.visualores.database.astralsorcery;

import hellfall.visualores.database.IClientCachePerDimOnly;
import net.minecraft.nbt.NBTTagCompound;

import java.util.Collection;

public class ASClientCache implements IClientCachePerDimOnly {
    @Override
    public void setupCacheFiles() {
        // EntityFxFluidFountain constructor has a BlockPos(ish) and FluidStack
        addDimFiles("neromantic_");
        // SkyCollectionHelper.getSkyNoiseDistributionClient(World, BlockPos)
        addDimFiles("starfields_");
    }

    @Override
    public void clear() {

    }

    @Override
    public Collection<Integer> getExistingDimensions(String prefix) {
        return null;
    }

    @Override
    public NBTTagCompound saveDimFile(String prefix, int dim) {
        return null;
    }

    @Override
    public void readDimFile(String prefix, int dim, NBTTagCompound data) {

    }
}
