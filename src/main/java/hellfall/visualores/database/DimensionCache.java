package hellfall.visualores.database;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DimensionCache {
    private final Map<GridPos, GridCache> cache = new HashMap<>();

    public boolean dirty;

    public void addVein(int x, int z, int gridX, int gridZ, String name) {
        GridPos key = new GridPos(gridX, gridZ);
        if (!cache.containsKey(key)) {
            cache.put(key, new GridCache());
        }
        cache.get(key).addVein(x, z, name);
        dirty = true;
    }

    public NBTTagCompound toNBT(NBTTagCompound nbt) {
        for (GridPos key : cache.keySet()) {
            nbt.setTag(key.x + "," + key.z, cache.get(key).toNBT());
        }
        return nbt;
    }

    public void fromNBT(NBTTagCompound tag) {
        for (String gridpos : tag.getKeySet()) {
            String[] splitpos = gridpos.split(",");
            GridPos key = new GridPos(Integer.parseInt(splitpos[0]), Integer.parseInt(splitpos[1]));
            if (!cache.containsKey(key)) {
                cache.put(key, new GridCache());
            }
            cache.get(key).fromNBT(tag.getTagList(gridpos, 10));
        }
    }

    private static class GridCache {
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
            for (NBTBase veinpos : tag.tagList) {
                NBTTagCompound veinpostag = (NBTTagCompound) veinpos;
                veins.add(new OreVeinPosition(
                        veinpostag.getInteger("x"),
                        veinpostag.getInteger("z"),
                        veinpostag.getString("name")
                ));
            }
        }

        private static class OreVeinPosition {
            public int x;
            public int z;
            public String depositname;

            public OreVeinPosition(int x, int z, String depositname) {
                this.x = x;
                this.z = z;
                this.depositname = depositname;
            }

            public NBTTagCompound toNBT() {
                NBTTagCompound result = new NBTTagCompound();
                result.setInteger("x", x);
                result.setInteger("z", z);
                result.setString("name", depositname);
                return result;
            }
        }
    }
}
