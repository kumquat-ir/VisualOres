package hellfall.visualores.database;

import gregtech.api.GregTechAPI;
import gregtech.api.unification.material.Material;
import hellfall.visualores.VOConfig;
import hellfall.visualores.network.ProspectToClientPacket;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServerCache extends WorldCache {
    public static ServerCache instance = new ServerCache();

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
    public void addVein(int dim, int x, int z, int gridX, int gridZ, String name) {
        super.addVein(dim, x, z, gridX, gridZ, name);
        if (saveData.containsKey(dim)) {
            saveData.get(dim).markDirty();
        }
    }

    @Override
    public void clear() {
        super.clear();
        saveData.clear();
    }

    public void prospectSurfaceRockMaterial(int dim, Material material, BlockPos pos, EntityPlayerMP player) {
        List<OreVeinPosition> nearbyVeins = getNearbyVeins(dim, pos, VOConfig.server.surfaceRockProspectRange);
        List<OreVeinPosition> foundVeins = new ArrayList<>();
        for (OreVeinPosition nearbyVein : nearbyVeins) {
            if (material.equals(nearbyVein.veinInfo.surfaceRockMaterial)) {
                foundVeins.add(nearbyVein);
            }
        }
        GregTechAPI.networkHandler.sendTo(new ProspectToClientPacket(dim, foundVeins), player);
    }

    public void prospectOreBlock(int dim, String oredictName, BlockPos pos, EntityPlayerMP player) {
        List<OreVeinPosition> nearbyVeins = getNearbyVeins(dim, pos, VOConfig.server.oreBlockProspectRange);
        List<OreVeinPosition> foundVeins = new ArrayList<>();
        for (OreVeinPosition nearbyVein : nearbyVeins) {
            if (nearbyVein.veinInfo.oreMaterialStrings.contains(oredictName)) {
                foundVeins.add(nearbyVein);
            }
        }
        GregTechAPI.networkHandler.sendTo(new ProspectToClientPacket(dim, foundVeins), player);
    }

    public void prospectAllInChunk(int dim, ChunkPos pos, EntityPlayerMP player) {
        if (cache.containsKey(dim)) {
            GregTechAPI.networkHandler.sendTo(new ProspectToClientPacket(dim, cache.get(dim).getVeinsInChunk(pos)), player);
        }
    }

    public void removeAllInChunk(int dim, ChunkPos pos) {
        if (cache.containsKey(dim)) {
            cache.get(dim).removeAllInChunk(pos);
        }
    }
}
