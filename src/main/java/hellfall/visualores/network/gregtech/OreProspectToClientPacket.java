package hellfall.visualores.network.gregtech;

import gregtech.api.network.IClientExecutor;
import gregtech.api.network.IPacket;
import hellfall.visualores.database.gregtech.GTClientCache;
import hellfall.visualores.database.gregtech.ore.GridPos;
import hellfall.visualores.database.gregtech.ore.OreVeinPosition;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.PacketBuffer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OreProspectToClientPacket implements IPacket, IClientExecutor {
    private final List<Integer> dimList;
    private final List<Integer> xList;
    private final List<Integer> zList;
    private final List<Integer> gridXList;
    private final List<Integer> gridZList;
    private final List<String> nameList;

    public OreProspectToClientPacket() {
        this.dimList = new ArrayList<>();
        this.xList = new ArrayList<>();
        this.zList = new ArrayList<>();
        this.gridXList = new ArrayList<>();
        this.gridZList = new ArrayList<>();
        this.nameList = new ArrayList<>();
    }

    public OreProspectToClientPacket(int dim, int x, int z, int gridX, int gridZ, String name) {
        this.dimList = Collections.singletonList(dim);
        this.xList = Collections.singletonList(x);
        this.zList = Collections.singletonList(z);
        this.gridXList = Collections.singletonList(gridX);
        this.gridZList = Collections.singletonList(gridZ);
        this.nameList = Collections.singletonList(name);
    }

    public OreProspectToClientPacket(List<Integer> dimList, List<Integer> xList, List<Integer> zList, List<Integer> gridXList, List<Integer> gridZList, List<String> nameList) {
        this.dimList = dimList;
        this.xList = xList;
        this.zList = zList;
        this.gridXList = gridXList;
        this.gridZList = gridZList;
        this.nameList = nameList;
    }

    public OreProspectToClientPacket(int dim, List<OreVeinPosition> positions) {
        this();
        for (OreVeinPosition position : positions) {
            dimList.add(dim);
            xList.add(position.x);
            zList.add(position.z);
            gridXList.add(GridPos.blockToGridCoords(position.x));
            gridZList.add(GridPos.blockToGridCoords(position.z));
            nameList.add(position.depositname);
        }
    }

    @Override
    public void executeClient(NetHandlerPlayClient netHandlerPlayClient) {
        int newVeins = 0;
        for (int i = 0; i < dimList.size(); i++) {
            if (GTClientCache.instance.addVein(dimList.get(i), xList.get(i), zList.get(i), gridXList.get(i), gridZList.get(i), nameList.get(i))) {
                newVeins++;
            }
        }
        GTClientCache.instance.notifyNewVeins(newVeins);
    }

    @Override
    public void encode(PacketBuffer packetBuffer) {
        packetBuffer.writeInt(dimList.size());
        for (int i = 0; i < dimList.size(); i++) {
            packetBuffer.writeInt(dimList.get(i));
            packetBuffer.writeInt(xList.get(i));
            packetBuffer.writeInt(zList.get(i));
            packetBuffer.writeInt(gridXList.get(i));
            packetBuffer.writeInt(gridZList.get(i));
            packetBuffer.writeString(nameList.get(i));
        }
    }

    @Override
    public void decode(PacketBuffer packetBuffer) {
        int size = packetBuffer.readInt();
        for (int i = 0; i < size; i++) {
            dimList.add(packetBuffer.readInt());
            xList.add(packetBuffer.readInt());
            zList.add(packetBuffer.readInt());
            gridXList.add(packetBuffer.readInt());
            gridZList.add(packetBuffer.readInt());
            nameList.add(packetBuffer.readString(Short.MAX_VALUE));
        }
    }
}
