package hellfall.visualores.database.gregtech.ore;

import gregtech.api.worldgen.generator.CachedGridEntry;
import gregtech.api.worldgen.generator.WorldGeneratorImpl;
import gregtech.common.blocks.BlockOre;
import hellfall.visualores.VOConfig;
import hellfall.visualores.VisualOres;
import hellfall.visualores.lib.io.xol.enklume.MinecraftChunk;
import hellfall.visualores.lib.io.xol.enklume.MinecraftRegion;
import hellfall.visualores.lib.io.xol.enklume.MinecraftWorld;
import it.unimi.dsi.fastutil.ints.Int2BooleanMap;
import it.unimi.dsi.fastutil.ints.Int2BooleanRBTreeMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraftforge.registries.GameData;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.zip.DataFormatException;

public class OreCacheRetrogenerator {
    public static final int CHUNKS_PER_REGION = 32;

    private static final Int2BooleanMap oreBlockIDs = new Int2BooleanRBTreeMap();

    public static void doRetrogen(World world) {
        VisualOres.LOGGER.info("Starting cache retrogen for dimension " + world.provider.getDimension());
        File worldFolder = world.getSaveHandler().getWorldDirectory();
        Map<GridPos, ChunkPos> generatedGridPositions = new Object2ObjectOpenHashMap<>();
        Set<ChunkPos> emptyChunks = new ObjectOpenHashSet<>();

        try {
            MinecraftWorld mcWorld = new MinecraftWorld(worldFolder);
            for (File regionFile : mcWorld.getAllRegionFiles(world.provider.getDimension())) {
                String[] parts = regionFile.getName().split("\\."); // {"r", regionX, regionZ, ".mca"}
                int regionChunkX = Integer.parseInt(parts[1]) * CHUNKS_PER_REGION;
                int regionChunkZ = Integer.parseInt(parts[2]) * CHUNKS_PER_REGION;
                MinecraftRegion mcRegion = new MinecraftRegion(regionFile);

                for (int x = 0; x < CHUNKS_PER_REGION; x++) {
                    for (int z = 0; z < CHUNKS_PER_REGION; z++) {
                        MinecraftChunk chunk = mcRegion.getChunk(x, z);
                        // the chunk is not yet generated if this is null
                        if (chunk.getRootTag() != null) {
                            generatedGridPositions.put(GridPos.fromChunkCoords(regionChunkX + x, regionChunkZ + z),
                                    new ChunkPos(regionChunkX + x, regionChunkZ + z));

                            if (VOConfig.server.gregtech.cullEmptyChunks) {
                                boolean foundOre = false;
                                for (int i = 0; i < 16; i++) outer: {
                                    if (chunk.blocks[i] == null) continue;
                                    for (int j = 0; j < chunk.blocks[i].length; j++) {
                                        int blockIDAdd = chunk.add[i] == null ? 0 : getNibble(chunk.add[i], j);
                                        int blockID = blockIDAdd << 12 | (chunk.blocks[i][j] & 0xFF) << 4 | getNibble(chunk.mData[i], j);
                                        if (isOreBlock(blockID)) {
                                            foundOre = true;
                                            break outer;
                                        }
                                    }
                                }
                                if (!foundOre) {
                                    emptyChunks.add(new ChunkPos(regionChunkX + x, regionChunkZ + z));
                                }
                            }
                        }
                    }

                }
            }
        } catch (IOException | DataFormatException e) {
            throw new RuntimeException(e);
        }
        if (generatedGridPositions.isEmpty()) {
            VisualOres.LOGGER.info("No chunks to retrogen in dimension " + world.provider.getDimension());
            return;
        }
        VisualOres.LOGGER.info("Retrogenerating " + generatedGridPositions.size() + " grid positions...");
        for (var entry : generatedGridPositions.entrySet()) {
            GridPos gridPos = entry.getKey();
            ChunkPos chunkPos = entry.getValue();

            // hey arch wtf is this code. why does it do this
            // (emulating weirdass behavior from WorldGeneratorImpl)
            int halfSizeX = (WorldGeneratorImpl.GRID_SIZE_X - 1) / 2;
            int halfSizeZ = (WorldGeneratorImpl.GRID_SIZE_Z - 1) / 2;
            for(int gridX = -halfSizeX; gridX <= halfSizeX; ++gridX) {
                for(int gridZ = -halfSizeZ; gridZ <= halfSizeZ; ++gridZ) {
                    // this doesnt actually generate anything in world
                    CachedGridEntry.getOrCreateEntry(world, gridPos.x + gridX, gridPos.z + gridZ, chunkPos.x, chunkPos.z);
                }
            }
        }
        VisualOres.LOGGER.info("Removing {} empty chunks", emptyChunks.size());
        for (var chunk : emptyChunks) {
            ServerCache.instance.removeAllInChunk(world.provider.getDimension(), chunk);
        }
    }

    private static boolean isOreBlock(int id) {
        if (!oreBlockIDs.containsKey(id)) {
            IBlockState state = GameData.getBlockStateIDMap().getByValue(id);
            if (state != null) {
                oreBlockIDs.put(id, state.getBlock() instanceof BlockOre);
            }
        }
        return oreBlockIDs.get(id);
    }

    private static int getNibble(byte[] arr, int index) {
        return (arr[index / 2] >> 4 * (index % 2)) & 0xF;
    }
}
