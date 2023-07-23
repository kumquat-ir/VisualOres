package hellfall.visualores.lib.io.xol.enklume.nbt;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class NBTNamed extends NBTag {

    private String tagName;
    boolean list = false;

    @Override
    void feed(ByteBuffer is) throws IOException {
        if (!list) {
            int nameSize = is.getShort();
            try {
                tagName = new String(is.array(), is.arrayOffset() + is.position(), nameSize, StandardCharsets.UTF_8);
                is.position(is.position() + nameSize);
                // System.out.println("read tag named :"+tagName);
            } catch (Exception e) {
                tagName = "<ERROR>";
                e.printStackTrace();
            }
        }
    }

    public String getName() {
        return tagName;
    }

    public void setNamedFromListIndex(int i) {
        tagName = Integer.toString(i);
        list = true;
    }
}
