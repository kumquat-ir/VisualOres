package hellfall.visualores.database.ore;

import net.minecraft.nbt.NBTTagCompound;

import java.util.Objects;

public class OreVeinPosition {
    public int x;
    public int z;
    public String depositname;
    public OreVeinInfo veinInfo;

    public OreVeinPosition(int x, int z, String depositname) {
        this.x = x;
        this.z = z;
        this.depositname = depositname;
        this.veinInfo = VeinInfoCache.getByName(depositname);
    }

    public NBTTagCompound toNBT() {
        NBTTagCompound result = new NBTTagCompound();
        result.setInteger("x", x);
        result.setInteger("z", z);
        result.setString("name", depositname);
        return result;
    }

    public GridPos getGridPos() {
        return new GridPos(GridPos.blockToGridCoords(x), GridPos.blockToGridCoords(z));
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
