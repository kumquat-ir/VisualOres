package hellfall.visualores.lib.io.xol.enklume.nbt;

import java.io.IOException;
import java.nio.ByteBuffer;

public class NBTShort extends NBTNamed {

    public short data;

    @Override
    void feed(ByteBuffer is) throws IOException {
        super.feed(is);
        data = is.getShort();
    }
}
