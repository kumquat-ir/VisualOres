package hellfall.visualores.database;

import hellfall.visualores.Tags;
import hellfall.visualores.VisualOres;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.CompressedStreamTools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ClientCache extends WorldCache {
    public static final File clientCacheDir = new File(Minecraft.getMinecraft().gameDir, Tags.MODID);
    public static final ClientCache instance = new ClientCache();
    private File worldFolder;

    public void init(String worldid) {
        final EntityPlayer player = Minecraft.getMinecraft().player;
        worldFolder = new File(clientCacheDir, player.getDisplayNameString() + "_" + player.getUniqueID() +
                File.separator + worldid);
        worldFolder.mkdirs();
        loadCache();
    }

    @Override
    public void addVein(int dim, int x, int z, int gridX, int gridZ, String name) {
        VisualOres.LOGGER.info("got more vein " + x + "," + z);
        super.addVein(dim, x, z, gridX, gridZ, name);
    }

    public void saveCache() {
        for (int dim : cache.keySet()) {
            File dimFile = new File(worldFolder, "DIM" + dim);
            try {
                CompressedStreamTools.writeCompressed(cache.get(dim).toNBT(), new FileOutputStream(dimFile));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void loadCache() {
        try {
            Files.walk(worldFolder.toPath(), 1).filter(Files::isRegularFile)
                    .filter(path -> path.getFileName().toString().startsWith("DIM"))
                    .forEach(this::loadDimFile);
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

    public void reset() {
        clear();
        try {
            Files.walk(worldFolder.toPath(), 1).filter(Files::isRegularFile)
                    .filter(path -> path.getFileName().toString().startsWith("DIM"))
                    .forEach(file -> file.toFile().delete());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
