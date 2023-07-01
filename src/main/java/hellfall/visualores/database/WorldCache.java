//package hellfall.visualores.database;
//
//import net.minecraft.nbt.CompressedStreamTools;
//import net.minecraft.nbt.NBTTagCompound;
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.util.HashMap;
//import java.util.Map;
//
//public abstract class WorldCache {
//    protected final Map<Integer, DimensionCache> cache = new HashMap<>();
//
//    protected abstract File getStorageDir();
//
//    public void addVein(int dim, int x, int z, int gridX, int gridZ, String name) {
//        if (!cache.containsKey(dim)) {
//            cache.put(dim, new DimensionCache());
//        }
//        cache.get(dim).addVein(x, z, gridX, gridZ, name);
//    }
//
//    public NBTTagCompound toNBT() {
//        NBTTagCompound result = new NBTTagCompound();
//        for (int key : cache.keySet()) {
//            result.setTag(String.valueOf(key), cache.get(key).toNBT());
//        }
//        return result;
//    }
//
//    public void writeNBT() {
//        try {
//            CompressedStreamTools.writeCompressed(toNBT(), new FileOutputStream(new File(getStorageDir(), "vocache.dat")));
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    public void readNBT() {
//        try {
//            NBTTagCompound nbt = CompressedStreamTools.readCompressed(new FileInputStream(new File(getStorageDir(), "vocache.dat")));
//            for (String dimkey : nbt.getKeySet()) {
//                int dim = Integer.parseInt(dimkey);
//                if (!cache.containsKey(dim)) {
//                    cache.put(dim, new DimensionCache());
//                }
//                cache.get(dim).fromNBT(nbt.getCompoundTag(dimkey));
//            }
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }
//}
