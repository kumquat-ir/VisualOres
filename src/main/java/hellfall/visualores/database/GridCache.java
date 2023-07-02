package hellfall.visualores.database;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class GridCache {
    private final List<OreVeinPosition> veins = new ArrayList<>();

    public void addVein(int x, int z, String name) {
        veins.add(new OreVeinPosition(x, z, name));
    }

    public NBTTagList toNBT() {
        NBTTagList result = new NBTTagList();
        for (OreVeinPosition pos : veins) {
            result.appendTag(pos.toNBT());
        }
        return result;
    }

    public void fromNBT(NBTTagList tag) {
        for (NBTBase veinposbase : tag.tagList) {
            NBTTagCompound veinpostag = (NBTTagCompound) veinposbase;
            OreVeinPosition veinpos = new OreVeinPosition(
                    veinpostag.getInteger("x"),
                    veinpostag.getInteger("z"),
                    veinpostag.getString("name")
            );
            if (!veins.contains(veinpos)) {
                veins.add(veinpos);
            }
        }
    }

    public List<OreVeinPosition> getVeinsMatching(Predicate<OreVeinPosition> predicate) {
        return veins.stream().filter(predicate).collect(Collectors.toList());
    }
}
