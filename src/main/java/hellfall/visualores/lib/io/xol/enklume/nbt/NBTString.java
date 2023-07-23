package hellfall.visualores.lib.io.xol.enklume.nbt;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class NBTString extends NBTNamed {

    public String data;

    @Override
    void feed(ByteBuffer is) throws IOException {
        super.feed(is);

        int size = is.getShort();

        try {
            data = new String(is.array(), is.arrayOffset() + is.position(), size, StandardCharsets.UTF_8);
            is.position(is.position() + size);
            // System.out.println("read tag named :"+tagName);
        } catch (Exception e) {
            data = "<ERROR>";
            e.printStackTrace();
        }
    }

    public String getText() {
        if (data == null) return "";
        return data;
    }
}
