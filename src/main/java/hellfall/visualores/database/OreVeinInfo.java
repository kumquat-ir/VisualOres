package hellfall.visualores.database;

import gregtech.api.unification.material.Material;
import gregtech.api.unification.material.info.MaterialIconType;
import gregtech.api.worldgen.config.OreDepositDefinition;
import gregtech.api.worldgen.filler.LayeredBlockFiller;
import gregtech.api.worldgen.populator.SurfaceRockPopulator;
import gregtech.common.blocks.BlockOre;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.util.Collection;

public class OreVeinInfo {
    public Material surfaceRockMaterial;

    public ResourceLocation texture;
    public int color;

    public OreVeinInfo(OreDepositDefinition def) {
        if (def.getVeinPopulator() instanceof SurfaceRockPopulator) {
            this.surfaceRockMaterial = ((SurfaceRockPopulator) def.getVeinPopulator()).getMaterial();
        }

        if (FMLCommonHandler.instance().getSide().isClient()) {
            if (def.getBlockFiller() instanceof LayeredBlockFiller) {
                Collection<IBlockState> possibleStates = ((LayeredBlockFiller) def.getBlockFiller()).getPrimary().getPossibleResults();
                for (IBlockState state : possibleStates) {
                    if (state.getBlock() instanceof BlockOre) {
                        Material mat = ((BlockOre) state.getBlock()).material;
                        ResourceLocation shortenedLocation = MaterialIconType.ore.getBlockTexturePath(mat.getMaterialIconSet());
                        texture = new ResourceLocation(shortenedLocation.getNamespace(), "textures/" + shortenedLocation.getPath() + ".png");
                        color = mat.getMaterialRGB();
                    }
                }
            }
        }
    }
}
