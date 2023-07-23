package hellfall.visualores.lib.io.xol.enklume.nbt;

import java.io.IOException;
import java.nio.ByteBuffer;

public class NBTByteArray extends NBTNamed {

    int size;

    public byte[] data;

    @Override
    void feed(ByteBuffer is) throws IOException {
        super.feed(is);
        size = is.getInt();
        data = new byte[size];
        is.get(data);
    }
}
