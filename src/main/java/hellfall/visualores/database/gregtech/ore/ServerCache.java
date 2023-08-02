package hellfall.visualores.database.gregtech.ore;

import gregtech.api.GregTechAPI;
import gregtech.api.unification.material.Material;
import hellfall.visualores.VOConfig;
import hellfall.visualores.network.gregtech.OreProspectToClientPacket;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServerCache extends WorldCache {
    public static final ServerCache instance = new ServerCache();

    private final Map<Integer, ServerCacheSaveData> saveData = new HashMap<>();

    public void maybeInitWorld(World world) {
        int dim = world.provider.getDimension();
        if (!cache.containsKey(dim)) {
            cache.put(dim, new DimensionCache());
        }
        if (!saveData.containsKey(dim)) {
            saveData.put(dim, ServerCacheSaveData.init(world, cache.get(dim)));
        }
    }

    public void invalidateWorld(World world) {
        int dim = world.provider.getDimension();
        cache.remove(dim);
        saveData.remove(dim);
    }

    @Override
    public boolean addVein(int dim, int x, int z, int gridX, int gridZ, String name) {
        boolean added = super.addVein(dim, x, z, gridX, gridZ, name);
        if (added && saveData.containsKey(dim)) {
            saveData.get(dim).markDirty();
        }
        return added;
    }

    @Override
    public void clear() {
        super.clear();
        saveData.clear();
    }

    public void prospectSurfaceRockMaterial(int dim, Material material, BlockPos pos, EntityPlayerMP player) {
        if (VOConfig.server.gregtech.surfaceRockProspectRange < 0) return;
        List<OreVeinPosition> nearbyVeins = getNearbyVeins(dim, pos, VOConfig.server.gregtech.surfaceRockProspectRange);
        List<OreVeinPosition> foundVeins = new ArrayList<>();
        for (OreVeinPosition nearbyVein : nearbyVeins) {
            if (material.equals(nearbyVein.veinInfo.surfaceRockMaterial)) {
                foundVeins.add(nearbyVein);
            }
        }
        GregTechAPI.networkHandler.sendTo(new OreProspectToClientPacket(dim, foundVeins), player);
    }

    public void prospectOreBlock(int dim, String oredictName, BlockPos pos, EntityPlayerMP player) {
        if (VOConfig.server.gregtech.oreBlockProspectRange < 0) return;
        List<OreVeinPosition> nearbyVeins = getNearbyVeins(dim, pos, VOConfig.server.gregtech.oreBlockProspectRange);
        List<OreVeinPosition> foundVeins = new ArrayList<>();
        for (OreVeinPosition nearbyVein : nearbyVeins) {
            if (nearbyVein.veinInfo.oreMaterialStrings.contains(oredictName)) {
                foundVeins.add(nearbyVein);
            }
        }
        GregTechAPI.networkHandler.sendTo(new OreProspectToClientPacket(dim, foundVeins), player);
    }

    public void prospectAllInChunk(int dim, ChunkPos pos, EntityPlayerMP player) {
        if (cache.containsKey(dim)) {
            GregTechAPI.networkHandler.sendTo(new OreProspectToClientPacket(dim, cache.get(dim).getVeinsInChunk(pos)), player);
        }
    }

    public void removeAllInChunk(int dim, ChunkPos pos) {
        if (cache.containsKey(dim)) {
            cache.get(dim).removeAllInChunk(pos);
        }
    }
}
