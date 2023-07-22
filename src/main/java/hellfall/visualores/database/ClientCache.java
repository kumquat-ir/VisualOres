package hellfall.visualores.database;

import hellfall.visualores.Tags;
import hellfall.visualores.database.fluid.FluidCache;
import hellfall.visualores.database.fluid.UndergroundFluidPosition;
import hellfall.visualores.database.ore.DimensionCache;
import hellfall.visualores.database.ore.OreVeinPosition;
import hellfall.visualores.database.ore.WorldCache;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class ClientCache extends WorldCache {
    public static final File clientCacheDir = new File(Minecraft.getMinecraft().gameDir, Tags.MODID);
    public static final ClientCache instance = new ClientCache();
    private File worldFolder;

    private final FluidCache fluids = new FluidCache();

    public void init(String worldid) {
        final EntityPlayer player = Minecraft.getMinecraft().player;
        worldFolder = new File(clientCacheDir, player.getDisplayNameString() + "_" + player.getUniqueID() +
                File.separator + worldid);
        worldFolder.mkdirs();
        loadCache();
    }

    public void notifyNewVeins(int amount) {
        if (amount <= 0) return;
        Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(new TextComponentTranslation("visualores.newveins", amount));
    }

    public void addFluid(int dim, int fieldX, int fieldZ, String name, int yield, double percent) {
        fluids.addFluid(dim, fieldX, fieldZ, name, yield, percent);
    }

    public List<UndergroundFluidPosition> getFluidsInArea(int dim, int[] bounds) {
        return fluids.getFluidsInBounds(
                dim,
                new BlockPos(bounds[0], 0, bounds[1]),
                new BlockPos(bounds[0] + bounds[2], 0, bounds[1] + bounds[3])
        );
    }

    public void saveCache() {
        for (int dim : cache.keySet()) {
            File dimFile = new File(worldFolder, "DIM" + dim);
            try {
                CompressedStreamTools.writeCompressed(cache.get(dim).toNBT(true), new FileOutputStream(dimFile));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        File fluidFile = new File(worldFolder, "fluids");
        try {
            CompressedStreamTools.writeCompressed(fluids.toNBT(), new FileOutputStream(fluidFile));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void loadCache() {
        try {
            Files.walk(worldFolder.toPath(), 1).filter(Files::isRegularFile)
                    .filter(path -> path.getFileName().toString().startsWith("DIM"))
                    .forEach(this::loadDimFile);
            loadFluidFile(worldFolder.toPath().resolve("fluids"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadDimFile(Path path) {
        File dimFile = path.toFile();
        int dimid = Integer.parseInt(path.getFileName().toString().substring(3));
        if (!cache.containsKey(dimid)) {
            cache.put(dimid, new DimensionCache());
        }
        try {
            cache.get(dimid).fromNBT(CompressedStreamTools.readCompressed(new FileInputStream(dimFile)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadFluidFile(Path path) {
        File fluidFile = path.toFile();
        if (!fluidFile.exists()) return;
        try {
            fluids.fromNBT(CompressedStreamTools.readCompressed(new FileInputStream(fluidFile)));
        } catch (FileNotFoundException ignored) {
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void reset() {
        clear();
        try {
            Files.walk(worldFolder.toPath(), 2).filter(Files::isRegularFile)
//                    .filter(path -> path.getFileName().toString().startsWith("DIM")) // delete the entire cache
                    .forEach(file -> file.toFile().delete());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void clear() {
        super.clear();
        fluids.clear();
    }
}
