package hellfall.visualores.database.gregtech;

import hellfall.visualores.database.IClientCache;
import hellfall.visualores.database.gregtech.fluid.FluidCache;
import hellfall.visualores.database.gregtech.fluid.UndergroundFluidPosition;
import hellfall.visualores.database.gregtech.ore.DimensionCache;
import hellfall.visualores.database.gregtech.ore.WorldCache;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;

import java.util.Collection;
import java.util.List;

public class GTClientCache extends WorldCache implements IClientCache {
    public static final GTClientCache instance = new GTClientCache();

    private final FluidCache fluids = new FluidCache();

    public void notifyNewVeins(int amount) {
        if (amount <= 0) return;
        Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(new TextComponentTranslation("visualores.newveins", amount));
    }

    public void addFluid(int dim, int fieldX, int fieldZ, String name, int yield, double percent) {
        fluids.addFluid(dim, fieldX, fieldZ, name, yield, percent);
    }

    public List<UndergroundFluidPosition> getFluidsInArea(int dim, int[] bounds) {
        return fluids.getFluidsInBounds(
                dim,
                new BlockPos(bounds[0], 0, bounds[1]),
                new BlockPos(bounds[0] + bounds[2], 0, bounds[1] + bounds[3])
        );
    }

    @Override
    public Collection<Integer> getExistingDimensions(String prefix) {
        return cache.keySet();
    }

    @Override
    public NBTTagCompound saveDimFile(String prefix, int dim) {
        if (!cache.containsKey(dim)) return null;
        return cache.get(dim).toNBT(true);
    }

    @Override
    public NBTTagCompound saveSingleFile(String name) {
        return fluids.toNBT();
    }

    @Override
    public void readDimFile(String prefix, int dim, NBTTagCompound data) {
        if (!cache.containsKey(dim)) {
            cache.put(dim, new DimensionCache());
        }
        cache.get(dim).fromNBT(data);
    }

    @Override
    public void readSingleFile(String name, NBTTagCompound data) {
        fluids.fromNBT(data);
    }

    @Override
    public void setupCacheFiles() {
        addDimFiles();
        addSingleFile("fluids");
    }

    @Override
    public void clear() {
        super.clear();
        fluids.clear();
    }
}
