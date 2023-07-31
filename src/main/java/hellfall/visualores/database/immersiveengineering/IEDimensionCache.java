package hellfall.visualores.database.immersiveengineering;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.ChunkPos;

import java.util.ArrayList;
import java.util.List;

public class IEDimensionCache {
    private final Object2ObjectMap<ChunkPos, ExcavatorVeinPosition> chunks = new Object2ObjectOpenHashMap<>();

    public void addVein(ItemStack stack, NBTTagCompound nbt) {
        int[] coords = nbt.getIntArray("coords");
        ChunkPos key = new ChunkPos(coords[1], coords[2]);
        chunks.put(key, new ExcavatorVeinPosition(stack, nbt));
    }

    public List<ExcavatorVeinPosition> getVeinsInArea(ChunkPos topLeft, ChunkPos bottomRight) {
        List<ExcavatorVeinPosition> result = new ArrayList<>();
        for (int i = topLeft.x; i <= bottomRight.x; i++) {
            for (int j = topLeft.z; j <= bottomRight.z; j++) {
                ChunkPos pos = new ChunkPos(i, j);
                if (chunks.containsKey(pos)) {
                    result.add(chunks.get(pos));
                }
            }
        }
        return result;
    }

    public void fromNBT(NBTTagCompound nbt) {
        for (String nbtKey : nbt.getKeySet()) {
            String[] splitpos = nbtKey.split(",");
            ChunkPos key = new ChunkPos(Integer.parseInt(splitpos[0]), Integer.parseInt(splitpos[1]));
            chunks.put(key, new ExcavatorVeinPosition(null, nbt.getCompoundTag(nbtKey)));
        }
    }

    public NBTTagCompound toNBT() {
        NBTTagCompound result = new NBTTagCompound();
        for (ChunkPos key : chunks.keySet()) {
            result.setTag(key.x + "," + key.z, chunks.get(key).toNBT());
        }
        return result;
    }
}
