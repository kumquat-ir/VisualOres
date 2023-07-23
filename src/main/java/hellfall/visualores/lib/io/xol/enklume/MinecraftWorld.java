package hellfall.visualores.lib.io.xol.enklume;

import hellfall.visualores.lib.io.xol.enklume.nbt.NBTFile;
import hellfall.visualores.lib.io.xol.enklume.nbt.NBTFile.CompressionScheme;
import hellfall.visualores.lib.io.xol.enklume.nbt.NBTString;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.DataFormatException;

public class MinecraftWorld {

    public static final int OVERWORLD_ID = 0;
    public static final int NETHER_ID = -1;
    public static final int END_ID = 1;

    private final File folder;
    private final NBTFile nbtFile;

    private final String levelName;

    public MinecraftWorld(File folder) throws IOException {
        this.folder = folder;

        // Tries to read level.dat
        File levelDat = new File(this.folder.getAbsolutePath() + "/level.dat");
        assert levelDat.exists();

        nbtFile = new NBTFile(levelDat, CompressionScheme.GZIPPED);

        levelName = ((NBTString) nbtFile.getRoot().getTag("Data.LevelName")).getText();
    }

    public String getName() {
        return levelName;
    }

    public NBTFile getLevelDotDat() {
        return nbtFile;
    }

    public static int blockToRegionCoordinates(int blockCoordinates) {
        if (blockCoordinates >= 0) {
            return (int) Math.floor(blockCoordinates / 512f);
        }
        blockCoordinates = -blockCoordinates;
        return -(int) Math.floor(blockCoordinates / 512f) - 1;
    }

    public MinecraftRegion getRegion(int regionCoordinateX, int regionCoordinateZ)
            throws DataFormatException, IOException {
        return getRegion(0, regionCoordinateX, regionCoordinateZ);
    }

    public MinecraftRegion getRegion(int dimensionId, int regionCoordinateX, int regionCoordinateZ)
            throws DataFormatException, IOException {
        final String subfolder = dimensionId == 0 ? "" : "/DIM" + dimensionId;
        File regionFile = new File(
                folder.getAbsolutePath() + subfolder
                        + "/region/r."
                        + regionCoordinateX
                        + "."
                        + regionCoordinateZ
                        + ".mca");

        if (regionFile.exists()) {
            return new MinecraftRegion(regionFile);
        } else {
            return null;
        }
    }

    /**
     * Get a region file. Nothing more, nothing less.
     *
     * @param dimensionId       Dimension ID.
     * @param regionCoordinateX X coord of region. Uses region coords, NOT block or chunk coords.
     * @param regionCoordinateZ Z coord of region. Uses region coords, NOT block or chunk coords.
     * @return The region file.
     */
    public File getRegionFile(int dimensionId, int regionCoordinateX, int regionCoordinateZ)
            throws DataFormatException, IOException {

        // If 0 (overworld) no subfolder; otherwise use the appropriate subfolder
        String subfolder = dimensionId == 0 ? "" : "/DIM" + dimensionId;

        // Get the file. If it doesn't exist, will return null
        File regionFile = new File(
                this.folder.getAbsolutePath() + subfolder
                        + "/region/r."
                        + regionCoordinateX
                        + "."
                        + regionCoordinateZ
                        + ".mca");
        return regionFile.exists() ? regionFile : null;
    }

    /**
     * Get region files containing the regions within a rectangle, with corners startX,Z and endX,Z. These coords are
     * region coords, NOT block or chunk coords!
     *
     * @param dimID  ID of the dimension in question.
     * @param startX First corner X, must be less than or equal to endX.
     * @param startZ First corner Z, must be less than or equal to endZ.
     * @param endX   Last corner X.
     * @param endZ   Last corner Z.
     * @return List of region files selected.
     */
    public List<File> getSomeRegionFiles(int dimID, int startX, int startZ, int endX, int endZ)
            throws DataFormatException, IOException {

        // initialize
        List<File> files = new ArrayList<>();

        // l o o p
        // This should loop once per chunk between start and end
        // 1 10 = loops i = [0, 9]
        // -4 8 = loops i = [0, 11]
        for (int i = 0; i < endX - startX; ++i) {

            // a g a i n
            for (int j = 0; j < endZ - startZ; ++j) {

                // Get and add the file
                File file = this.getRegionFile(dimID, startX + i, startZ + j);
                if (file != null) {
                    files.add(file);
                }
            }
        }

        return files;
    }

    public List<File> getAllRegionFiles(int dimensionId) throws IOException {
        final String subfolder = dimensionId == 0 ? "" : "/DIM" + dimensionId;
        File regionFolder = new File(folder.getAbsolutePath() + subfolder + "/region");

        if (regionFolder.exists()) {
            return Files.walk(regionFolder.toPath(), 1).filter(path -> path.getFileName().toString().endsWith(".mca"))
                    .map(Path::toFile).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    public List<Integer> getDimensionIds() throws IOException {
        List<Integer> dimensionIds = Files.walk(this.folder.toPath(), 1).filter(Files::isDirectory)
                .map(path -> path.getFileName().toString()).filter(path -> path.startsWith("DIM"))
                .map(path -> path.substring(3)).filter(dimensionId -> isInteger(dimensionId))
                .map(dimensionId -> Integer.parseInt(dimensionId)).collect(Collectors.toList());
        dimensionIds.add(0);
        return dimensionIds;
    }

    private boolean isInteger(String input) {
        try {
            Integer.parseInt(input);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
