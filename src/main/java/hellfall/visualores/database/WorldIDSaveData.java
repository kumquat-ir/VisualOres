package hellfall.visualores.database;

import hellfall.visualores.Tags;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldSavedData;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class WorldIDSaveData extends WorldSavedData {
    private static WorldIDSaveData instance;
    private static final String DATA_NAME = Tags.MODID + "_worldid";

    private String worldID;

    @SuppressWarnings("unused")
    public WorldIDSaveData(String name) {
        super(name);
    }

    public WorldIDSaveData() {
        super(DATA_NAME);
    }

    @SuppressWarnings("DataFlowIssue") // if things are null, just let it throw an error
    public static void init(World world) {
        MapStorage storage = world.getMapStorage();
        instance = (WorldIDSaveData) storage.getOrLoadData(WorldIDSaveData.class, DATA_NAME);

        if (instance == null) {
            instance = new WorldIDSaveData();
            storage.setData(DATA_NAME, instance);

            // this is here because it should NEVER change once generated
            instance.worldID = world.getMinecraftServer().getFolderName() + "_" + UUID.randomUUID();
            instance.markDirty();
        }
    }

    @Override
    public void readFromNBT(@NotNull NBTTagCompound nbt) {
        worldID = nbt.getString("id");
    }

    @Override
    public @NotNull NBTTagCompound writeToNBT(@NotNull NBTTagCompound compound) {
        compound.setString("id", worldID);
        return compound;
    }

    public static String getWorldID() {
        return instance.worldID;
    }
}
