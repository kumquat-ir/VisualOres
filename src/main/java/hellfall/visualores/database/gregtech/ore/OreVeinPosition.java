package hellfall.visualores.database.gregtech.ore;

import net.minecraft.client.resources.I18n;
import net.minecraft.nbt.NBTTagCompound;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class OreVeinPosition {
    public int x;
    public int z;
    public String depositname;
    public OreVeinInfo veinInfo;
    public boolean depleted;

    public OreVeinPosition(int x, int z, String depositname) {
        this.x = x;
        this.z = z;
        this.depositname = depositname;
        this.veinInfo = VeinInfoCache.getByName(depositname);
        this.depleted = false;
    }

    public NBTTagCompound toNBT(boolean saveDepleted) {
        NBTTagCompound result = new NBTTagCompound();
        result.setInteger("x", x);
        result.setInteger("z", z);
        result.setString("name", depositname);
        if (saveDepleted) result.setBoolean("depleted", depleted);
        return result;
    }

    public GridPos getGridPos() {
        return new GridPos(GridPos.blockToGridCoords(x), GridPos.blockToGridCoords(z));
    }

    public List<String> getTooltipStrings() {
        if (depleted) {
            List<String> tooltip = new ArrayList<>(veinInfo.tooltipStrings);
            tooltip.set(0, tooltip.get(0) + I18n.format("visualores.depleted"));
            return tooltip;
        }
        return veinInfo.tooltipStrings;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OreVeinPosition that = (OreVeinPosition) o;
        return x == that.x && z == that.z && depositname.equals(that.depositname);
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, z, depositname);
    }
}
