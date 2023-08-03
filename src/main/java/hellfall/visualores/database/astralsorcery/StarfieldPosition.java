package hellfall.visualores.database.astralsorcery;

import hellfirepvp.astralsorcery.common.util.SkyCollectionHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;

public class StarfieldPosition {
    public int x;
    public int z;
    public final boolean[] low = new boolean[256];
    public final boolean[] high = new boolean[256];

    public StarfieldPosition(ChunkPos chunk) {
        this.x = chunk.x;
        this.z = chunk.z;
        World world = Minecraft.getMinecraft().world;
        BlockPos origin = chunk.getBlock(0, 0, 0);
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos(origin);
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                pos.setPos(origin.getX() + x, 0, origin.getZ() + z);
                // guaranteed to be present due to an earlier check before this ever gets called
                @SuppressWarnings("OptionalGetWithoutIsPresent")
                float amt = SkyCollectionHelper.getSkyNoiseDistributionClient(world, pos).get();
                if (amt >= 0.8F) {
                    high[x * 16 + z] = true;
                }
                else if (amt >= 0.4F) {
                    low[x * 16 + z] = true;
                }
            }
        }
    }

    public StarfieldPosition(NBTTagCompound nbt) {
        this.x = nbt.getInteger("x");
        this.z = nbt.getInteger("z");
        if (nbt.hasKey("empty")) {
            return;
        }
        int[] lowi = nbt.getIntArray("low");
        int[] highi = nbt.getIntArray("high");
        for (int i = 0; i < 256; i++) {
            low[i] = ((lowi[i / 32] >> (i % 32)) & 0b1) == 1;
            high[i] = ((highi[i / 32] >> (i % 32)) & 0b1) == 1;
        }
    }

    public NBTTagCompound toNBT() {
        NBTTagCompound result = new NBTTagCompound();
        boolean empty = true;
        int[] lowi = new int[8];
        int[] highi = new int[8];
        for (int i = 0; i < 256; i++) {
            if (low[i]) {
                lowi[i / 32] |= 1 << (i % 32);
                empty = false;
            }
            if (high[i]) {
                highi[i / 32] |= 1 << (i % 32);
                empty = false;
            }
        }
        result.setInteger("x", x);
        result.setInteger("z", z);
        if (!empty) {
            result.setIntArray("low", lowi);
            result.setIntArray("high", highi);
        }
        else {
            result.setBoolean("empty", true);
        }
        return result;
    }
}
