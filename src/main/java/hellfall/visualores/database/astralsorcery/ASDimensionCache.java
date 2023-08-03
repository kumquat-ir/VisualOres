package hellfall.visualores.database.astralsorcery;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.ChunkPos;
import net.minecraftforge.fluids.Fluid;

import java.util.ArrayList;
import java.util.List;

public class ASDimensionCache {
    private final Object2ObjectMap<ChunkPos, StarfieldPosition> starfields = new Object2ObjectOpenHashMap<>();
    private final Object2ObjectMap<ChunkPos, NeromanticPosition> neromanticVeins = new Object2ObjectOpenHashMap<>();

    public void addStarfields() {
        // resonator searches 30 blocks away from the player
        // max distance from player a 3x3 chunk area will get is 31 (min is 16)
        ChunkPos center = new ChunkPos(Minecraft.getMinecraft().player.getPosition());
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                ChunkPos current = new ChunkPos(center.x + i, center.z + j);
                // starfields are determined entirely by world seed and are thus immutable
                starfields.putIfAbsent(current, new StarfieldPosition(current));
            }
        }
    }

    public List<StarfieldPosition> getStarfieldsInArea(ChunkPos topLeft, ChunkPos bottomRight) {
        List<StarfieldPosition> result = new ArrayList<>();
        for (int i = topLeft.x; i <= bottomRight.x; i++) {
            for (int j = topLeft.z; j <= bottomRight.z; j++) {
                ChunkPos pos = new ChunkPos(i, j);
                if (starfields.containsKey(pos)) {
                    result.add(starfields.get(pos));
                }
            }
        }
        return result;
    }

    public void setNeromanticFluid(ChunkPos pos, Fluid fluid) {
        neromanticVeins.put(pos, new NeromanticPosition(pos, fluid));
    }

    public List<NeromanticPosition> getNeromanticVeinsInArea(ChunkPos topLeft, ChunkPos bottomRight) {
        List<NeromanticPosition> result = new ArrayList<>();
        for (int i = topLeft.x; i <= bottomRight.x; i++) {
            for (int j = topLeft.z; j <= bottomRight.z; j++) {
                ChunkPos pos = new ChunkPos(i, j);
                if (neromanticVeins.containsKey(pos)) {
                    result.add(neromanticVeins.get(pos));
                }
            }
        }
        return result;
    }

    public NBTTagCompound toNBT(String prefix) {
        NBTTagCompound result = new NBTTagCompound();
        switch (prefix) {
            case "starfields_" -> {
                for (ChunkPos key : starfields.keySet()) {
                    result.setTag(key.x + "," + key.z, starfields.get(key).toNBT());
                }
            }
            case "neromantic_" -> {
                for (ChunkPos key : neromanticVeins.keySet()) {
                    result.setTag(key.x + "," + key.z, neromanticVeins.get(key).toNBT());
                }
            }
        }
        return result;
    }

    public void fromNBT(String prefix, NBTTagCompound nbt) {
        switch (prefix) {
            case "starfields_" -> {
                for (String nbtKey : nbt.getKeySet()) {
                    String[] splitpos = nbtKey.split(",");
                    ChunkPos key = new ChunkPos(Integer.parseInt(splitpos[0]), Integer.parseInt(splitpos[1]));
                    starfields.put(key, new StarfieldPosition(nbt.getCompoundTag(nbtKey), key.x, key.z));
                }
            }
            case "neromantic_" -> {
                for (String nbtKey : nbt.getKeySet()) {
                    String[] splitpos = nbtKey.split(",");
                    ChunkPos key = new ChunkPos(Integer.parseInt(splitpos[0]), Integer.parseInt(splitpos[1]));
                    neromanticVeins.put(key, new NeromanticPosition(nbt.getString(nbtKey), key.x, key.z));
                }
            }
        }
    }
}
