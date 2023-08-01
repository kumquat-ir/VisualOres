package hellfall.visualores.database.thaumcraft;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.ChunkPos;

import java.util.ArrayList;
import java.util.List;

public class TCDimensionCache {
    private final Object2ObjectMap<ChunkPos, AuraFluxPosition> chunks = new Object2ObjectOpenHashMap<>();

    public void addChunk(short base, float aura, float flux) {
        // no way to get what position player is at on the server, but this should be fine if there isnt horrendous lag
        ChunkPos key = new ChunkPos(Minecraft.getMinecraft().player.getPosition());
        chunks.put(key, new AuraFluxPosition(base, aura, flux, key.x, key.z));
    }

    public List<AuraFluxPosition> getVeinsInArea(ChunkPos topLeft, ChunkPos bottomRight) {
        List<AuraFluxPosition> result = new ArrayList<>();
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
            NBTTagCompound chunkNBT = nbt.getCompoundTag(nbtKey);
            chunks.put(key, new AuraFluxPosition(
                    chunkNBT.getShort("base"),
                    chunkNBT.getFloat("aura"),
                    chunkNBT.getFloat("flux"),
                    key.x,
                    key.z
            ));
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
