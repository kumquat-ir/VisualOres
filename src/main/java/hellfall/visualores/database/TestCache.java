package hellfall.visualores.database;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

public class TestCache {
    private final File cacheFile = new File(Minecraft.getMinecraft().gameDir, "vocache.dat");

    private final Map<Integer, DimensionCache> cache = new HashMap<>();

    public void addVein(int dim, int x, int z, int gridX, int gridZ, String name) {
        if (!cache.containsKey(dim)) {
            cache.put(dim, new DimensionCache());
        }
        cache.get(dim).addVein(x, z, gridX, gridZ, name);
    }

    public NBTTagCompound toNBT() {
        NBTTagCompound result = new NBTTagCompound();
        for (int key : cache.keySet()) {
            result.setTag(String.valueOf(key), cache.get(key).toNBT());
        }
        return result;
    }

    public void writeNBT() {
        try {
            CompressedStreamTools.writeCompressed(toNBT(), new FileOutputStream(cacheFile));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static class DimensionCache {
        private final Map<GridPos, GridCache> cache = new HashMap<>();

        public void addVein(int x, int z, int gridX, int gridZ, String name) {
            GridPos key = new GridPos(gridX, gridZ);
            if (!cache.containsKey(key)) {
                cache.put(key, new GridCache());
            }
            cache.get(key).addVein(x, z, name);
        }

        public NBTTagCompound toNBT() {
            NBTTagCompound result = new NBTTagCompound();
            for (GridPos key : cache.keySet()) {
                result.setTag(key.x + "," + key.z, cache.get(key).toNBT());
            }
            return result;
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
}
