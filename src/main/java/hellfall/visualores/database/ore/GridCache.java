package hellfall.visualores.database.ore;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class GridCache {
    private final List<OreVeinPosition> veins = new ArrayList<>();

    public boolean addVein(int x, int z, String name) {
        if (veins.contains(new OreVeinPosition(x, z, name))) return false;
        veins.add(new OreVeinPosition(x, z, name));
        return true;
    }

    public NBTTagList toNBT(boolean saveDepleted) {
        NBTTagList result = new NBTTagList();
        for (OreVeinPosition pos : veins) {
            result.appendTag(pos.toNBT(saveDepleted));
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
            if (veinpostag.hasKey("depleted")) {
                veinpos.depleted = veinpostag.getBoolean("depleted");
            }
            if (!veins.contains(veinpos)) {
                veins.add(veinpos);
            }
        }
    }

    public List<OreVeinPosition> getVeinsMatching(Predicate<OreVeinPosition> predicate) {
        return veins.stream().filter(predicate).collect(Collectors.toList());
    }

    public void removeVeinsMatching(Predicate<OreVeinPosition> predicate) {
        for (int i = 0; i < veins.size(); i++) {
            if (predicate.test(veins.get(i))) {
                veins.remove(i);
                i--;
            }
        }
    }
}
