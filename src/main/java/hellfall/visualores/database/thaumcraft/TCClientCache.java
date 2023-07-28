package hellfall.visualores.database.thaumcraft;

import hellfall.visualores.database.IClientCachePerDimOnly;
import net.minecraft.nbt.NBTTagCompound;

import java.util.Collection;

public class TCClientCache implements IClientCachePerDimOnly {
    @Override
    public void setupCacheFiles() {
        // PacketAuraToClient onMessage()
        addDimFiles("aura_");
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
