package hellfall.visualores.database.gregtech.fluid;

import gregtech.api.fluids.MaterialFluid;
import gregtech.api.util.GTUtility;
import gregtech.api.util.LocalizationUtils;
import gregtech.api.worldgen.bedrockFluids.ChunkPosDimension;
import hellfall.visualores.VOConfig;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

import java.util.Objects;

public class UndergroundFluidPosition {
    public static Object2IntMap<String> colorOverrides = new Object2IntOpenHashMap<>();
    public ChunkPosDimension pos;
    public String fluid;
    public int yield;
    public double percent;

    public String name;
    public int color;

    public UndergroundFluidPosition(int dim, int fieldX, int fieldZ, String fluid, int yield, double percent) {
        this(new ChunkPosDimension(dim, fieldX, fieldZ), fluid, yield, percent);
    }

    public UndergroundFluidPosition(ChunkPosDimension pos, String fluid, int yield, double percent) {
        this.pos = pos;
        this.fluid = fluid;
        this.yield = yield;
        this.percent = percent;

        Fluid f = FluidRegistry.getFluid(fluid);
        this.name = LocalizationUtils.format(f.getUnlocalizedName());
        this.color = f.getColor();

        if (colorOverrides.containsKey(f.getName())) {
            color = GTUtility.convertRGBtoOpaqueRGBA_MC(colorOverrides.get(f.getName()));
        }
        else if (color == 0xFFFFFFFF && f instanceof MaterialFluid mf) {
            color = GTUtility.convertRGBtoOpaqueRGBA_MC(mf.getMaterial().getMaterialRGB());
        }
    }

    public NBTTagCompound toNBT() {
        NBTTagCompound result = pos.writeToNBT();
        NBTTagCompound info = new NBTTagCompound();
        info.setString("fluid", fluid);
        info.setInteger("yield", yield);
        info.setDouble("percent", percent);
        result.setTag("info", info);
        return result;
    }

    public static UndergroundFluidPosition fromNBT(NBTTagCompound nbt) {
        NBTTagCompound info = nbt.getCompoundTag("info");
        return new UndergroundFluidPosition(ChunkPosDimension.readFromNBT(nbt), info.getString("fluid"), info.getInteger("yield"), info.getDouble("percent"));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UndergroundFluidPosition that = (UndergroundFluidPosition) o;
        return yield == that.yield && Double.compare(that.percent, percent) == 0 && Objects.equals(pos, that.pos) && Objects.equals(fluid, that.fluid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pos, fluid, yield, percent);
    }

    public static void initColorOverrides() {
        for (String entry : VOConfig.client.gregtech.fluidColorOverrides) {
            String[] parts = entry.split("="); // eg. {"water", "#6B7AF7"}
            colorOverrides.put(parts[0], Integer.decode(parts[1]));
        }
    }
}
