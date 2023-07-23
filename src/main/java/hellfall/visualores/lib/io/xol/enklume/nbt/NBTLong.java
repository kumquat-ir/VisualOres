package hellfall.visualores.lib.io.xol.enklume.nbt;

import java.io.IOException;
import java.nio.ByteBuffer;

public class NBTLong extends NBTNamed {

    public long data = 0;

    @Override
    void feed(ByteBuffer is) throws IOException {
        super.feed(is);
        data = is.getLong();
    }
}
