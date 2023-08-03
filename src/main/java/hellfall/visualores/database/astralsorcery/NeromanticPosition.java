package hellfall.visualores.database.astralsorcery;

import hellfall.visualores.map.DrawUtils;
import net.minecraft.client.resources.I18n;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.math.ChunkPos;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

import java.util.Collections;
import java.util.List;

public class NeromanticPosition {
    public int x;
    public int z;
    public int color;
    public String fluid;
    public List<String> tooltip;

    public NeromanticPosition(ChunkPos pos, Fluid fluid) {
        this.x = pos.x;
        this.z = pos.z;
        this.color = DrawUtils.getFluidColor(fluid);
        this.fluid = fluid.getName();
        this.tooltip = Collections.singletonList(I18n.format("visualores.astralsorcery.fluid", fluid.getLocalizedName(null)));
    }

    public NeromanticPosition(String fluid, int x, int z) {
        this.x = x;
        this.z = z;
        this.fluid = fluid;

        Fluid f = FluidRegistry.getFluid(fluid);
        this.color = DrawUtils.getFluidColor(f);
        this.tooltip = Collections.singletonList(I18n.format("visualores.astralsorcery.fluid", f.getLocalizedName(null)));
    }

    public NBTTagString toNBT() {
        return new NBTTagString(fluid);
    }
}
