package hellfall.visualores.lib.io.xol.enklume;

import hellfall.visualores.lib.io.xol.enklume.nbt.NBTByte;
import hellfall.visualores.lib.io.xol.enklume.nbt.NBTByteArray;
import hellfall.visualores.lib.io.xol.enklume.nbt.NBTCompound;
import hellfall.visualores.lib.io.xol.enklume.nbt.NBTag;

import java.nio.ByteBuffer;

public class MinecraftChunk {

    NBTCompound root = null;
    public int[] sectionsMap = new int[16];

    public final int x;
    public final int z;

    public byte[][] blocks = new byte[16][];
    public byte[][] mData = new byte[16][];
    public byte[][] add = new byte[16][];

    public MinecraftChunk(int x, int z) {
        this.x = x;
        this.z = z;
    }

    public MinecraftChunk(int x, int z, ByteBuffer byteArray) {
        this(x, z);
        root = (NBTCompound) NBTag.parseByteBuffer(byteArray);

        for (int i = 0; i < 16; i++) sectionsMap[i] = -1;
        for (int i = 0; i < 16; i++) {
            NBTCompound section = (NBTCompound) root.getTag("Level.Sections." + i);
            if (section != null) {
                int y = ((NBTByte) section.getTag("Y")).data;
                sectionsMap[y] = i;

                NBTag blocksNBT = root.getTag("Level.Sections." + i + ".Blocks");
                if (blocksNBT != null) blocks[i] = ((NBTByteArray) blocksNBT).data;
            }
        }

        for (int i = 0; i < 16; i++) {
            NBTag blocksNBT = root.getTag("Level.Sections." + i + ".Blocks");
            if (blocksNBT != null) blocks[i] = ((NBTByteArray) blocksNBT).data;
            NBTag mDataNBT = root.getTag("Level.Sections." + i + ".Data");
            if (mDataNBT != null) {
                mData[i] = ((NBTByteArray) mDataNBT).data;
            }
            NBTag addNBT = root.getTag("Level.Sections." + i + ".Add");
            if (addNBT != null) add[i] = ((NBTByteArray) addNBT).data;
        }
    }

    public NBTCompound getRootTag() {
        return root;
    }

    public int getBlockID(int x, int y, int z) {
        if (root == null) return 0;

        int i = sectionsMap[y / 16];
        if (y > 0 && y < 256) {
            if (i >= 0) {
                if (blocks[i] != null) {
                    y %= 16;
                    int index = y * 16 * 16 + z * 16 + x;
                    return blocks[i][index] & 0xFF;
                }
            }
        }

        if (i >= 0) {
            NBTag blocksNBT = root.getTag("Level.Sections." + i + ".Blocks");
            if (blocksNBT != null) {
                y = y % 16;
                int index = y * 16 * 16 + z * 16 + x;
                return ((NBTByteArray) blocksNBT).data[index] & 0xFF;
            }
        }

        return 0;
    }

    public int getBlockMeta(int x, int y, int z) {
        int i = sectionsMap[y / 16];
        if (y > 0 && y < 256) {
            if (i >= 0) {
                if (mData[i] != null) {
                    y %= 16;
                    int index = y * 16 * 16 + z * 16 + x;
                    byte unfilteredMeta = mData[i][index / 2];
                    // 4-bit nibbles, classic bullshit !
                    if (index % 2 != 0) {
                        return (unfilteredMeta >> 4) & 0xF;
                    } else {
                        return (unfilteredMeta) & 0xF;
                    }
                }
            }
        }
        return 0;
    }
}
