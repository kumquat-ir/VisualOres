package hellfall.visualores.lib.io.xol.enklume.nbt;

import java.io.IOException;
import java.nio.ByteBuffer;

public class NBTInt extends NBTNamed {

    public int data;

    @Override
    void feed(ByteBuffer is) throws IOException {
        super.feed(is);
        data = is.getInt();
    }

    public int getData() {
        return data;
    }
}
