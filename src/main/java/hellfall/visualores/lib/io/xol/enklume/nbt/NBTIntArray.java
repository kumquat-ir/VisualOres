package hellfall.visualores.lib.io.xol.enklume.nbt;

import java.io.IOException;
import java.nio.ByteBuffer;

public class NBTIntArray extends NBTNamed {

    int size;
    public int[] data;

    @Override
    void feed(ByteBuffer is) throws IOException {
        super.feed(is);
        size = is.getInt();

        data = new int[size];
        for (int i = 0; i < size; i++) {
            data[i] = is.getInt();
        }
    }
}
