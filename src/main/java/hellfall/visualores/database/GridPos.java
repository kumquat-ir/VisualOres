package hellfall.visualores.database;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;

import java.util.Objects;

public class GridPos {
    public int x;
    public int z;

    public GridPos(int x, int z) {
        this.x = x;
        this.z = z;
    }

    public GridPos(ChunkPos chunk) {
        this.x = chunk.x / 3;
        this.z = chunk.z / 3;
    }

    public ChunkPos getChunk(int x, int z) {
        return new ChunkPos(this.x * 3 + x, this.z * 3 + z);
    }

    public BlockPos getBlock(int x, int y, int z) {
        return new BlockPos(this.x * 3 * 16 + x, y, this.z * 3 * 16 + z);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GridPos gridPos = (GridPos) o;
        return x == gridPos.x && z == gridPos.z;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, z);
    }
}
