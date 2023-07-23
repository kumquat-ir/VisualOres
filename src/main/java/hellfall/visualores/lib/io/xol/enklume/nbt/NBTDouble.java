package hellfall.visualores.lib.io.xol.enklume.nbt;

import java.io.IOException;
import java.nio.ByteBuffer;

public class NBTDouble extends NBTNamed {

    public double data = 0;

    @Override
    void feed(ByteBuffer is) throws IOException {
        super.feed(is);
        data = is.getDouble();
    }

    public double getData() {
        return data;
    }
}
