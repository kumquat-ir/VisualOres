package hellfall.visualores.lib.io.xol.enklume.nbt;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Clean code is for suckers anyway
 */
public abstract class NBTag {

    abstract void feed(ByteBuffer bytes) throws IOException;

    public static NBTag parseByteBuffer(ByteBuffer bytes) {
        bytes.order(ByteOrder.BIG_ENDIAN);
        try {
            int type = bytes.get();
            if (type == -1) return null;
            NBTag tag = create(type);
            tag.feed(bytes);
            return tag;
        } catch (IOException e) {
            return null;
        }
    }

    public static NBTag createNamedFromList(int t, int listIndex) {
        NBTag tag = create(Type.values()[t]);

        if (tag instanceof NBTNamed) {
            NBTNamed named = (NBTNamed) tag;
            named.setNamedFromListIndex(listIndex);

            return named;
        }

        System.out.println("Error: Type " + t + " (" + Type.values()[t].name() + ") can't be named.");

        return tag;
    }

    static Type lastType;

    public static NBTag create(int t) throws IOException {
        try {
            NBTag tag = create(Type.values()[t]);
            lastType = Type.values()[t];
            return tag;
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println(
                    "Out of bounds type exception: " + t
                            + " Last valid type was : "
                            + lastType
                            + ". Probably caused by a corrupt file. Will ignore this particular NBT entry!");
            throw new IOException("Corrupted File");
        }
    }

    private static NBTag create(Type t) {
        switch (t) {
            case TAG_END:
                return new NBTEnd();
            case TAG_COMPOUND:
                return new NBTCompound();
            case TAG_BYTE:
                return new NBTByte();
            case TAG_SHORT:
                return new NBTShort();
            case TAG_INT:
                return new NBTInt();
            case TAG_FLOAT:
                return new NBTFloat();
            case TAG_DOUBLE:
                return new NBTDouble();
            case TAG_STRING:
                return new NBTString();
            case TAG_LONG:
                return new NBTLong();
            case TAG_LIST:
                return new NBTList();
            case TAG_BYTE_ARRAY:
                return new NBTByteArray();
            case TAG_INT_ARRAY:
                return new NBTIntArray();
            default:
                System.out.println("Unknow type : " + t.name());
                break;
        }
        return null;
    }

    public enum Type {
        TAG_END,
        TAG_BYTE,
        TAG_SHORT,
        TAG_INT,
        TAG_LONG,
        TAG_FLOAT,
        TAG_DOUBLE,
        TAG_BYTE_ARRAY,
        TAG_STRING,
        TAG_LIST,
        TAG_COMPOUND,
        TAG_INT_ARRAY
    }
}
