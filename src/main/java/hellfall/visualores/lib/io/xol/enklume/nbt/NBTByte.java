package hellfall.visualores.lib.io.xol.enklume.nbt;

import java.io.IOException;
import java.nio.ByteBuffer;

public class NBTByte extends NBTNamed {

    public byte data;

    @Override
    void feed(ByteBuffer is) throws IOException {
        super.feed(is);
        data = is.get();
    }
}
