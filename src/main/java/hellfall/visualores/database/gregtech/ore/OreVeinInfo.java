package hellfall.visualores.database.gregtech.ore;

import codechicken.lib.texture.TextureUtils;
import gregtech.api.GregTechAPI;
import gregtech.api.unification.OreDictUnifier;
import gregtech.api.unification.material.Material;
import gregtech.api.unification.material.info.MaterialIconType;
import gregtech.api.unification.stack.MaterialStack;
import gregtech.api.util.FileUtility;
import gregtech.api.util.GTUtility;
import gregtech.api.util.LocalizationUtils;
import gregtech.api.worldgen.config.OreDepositDefinition;
import gregtech.api.worldgen.filler.BlockFiller;
import gregtech.api.worldgen.filler.FillerEntry;
import gregtech.api.worldgen.filler.LayeredBlockFiller;
import gregtech.api.worldgen.populator.SurfaceRockPopulator;
import gregtech.common.blocks.BlockOre;
import hellfall.visualores.VOConfig;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.util.*;

public class OreVeinInfo {
    public Material surfaceRockMaterial;
    public Set<String> oreMaterialStrings;

    public ResourceLocation texture;
    public int color;
    public TextureAtlasSprite tas;
    public List<String> tooltipStrings;

    public OreVeinInfo(OreDepositDefinition def) {
        oreMaterialStrings = new HashSet<>();
        if (def.getVeinPopulator() instanceof SurfaceRockPopulator) {
            this.surfaceRockMaterial = ((SurfaceRockPopulator) def.getVeinPopulator()).getMaterial();
        }
        for (FillerEntry filler : getAllFillers(def.getBlockFiller())) {
            for (IBlockState blockState : filler.getPossibleResults()) {
                String name = getBaseMaterialName(blockState);
                if (!name.isEmpty()) {
                    oreMaterialStrings.add(name);
                }
            }
        }

        if (FMLCommonHandler.instance().getSide().isClient()) {
            if (def.getBlockFiller() instanceof LayeredBlockFiller) {
                Collection<IBlockState> possiblePrimaryStates = ((LayeredBlockFiller) def.getBlockFiller()).getPrimary().getPossibleResults();
                for (IBlockState state : possiblePrimaryStates) {
                    if (state.getBlock() instanceof BlockOre) {
                        // gt ores need special handling due to iconset stuff
                        Material mat = ((BlockOre) state.getBlock()).material;
                        ResourceLocation shortenedLocation = MaterialIconType.ore.getBlockTexturePath(mat.getMaterialIconSet());
                        texture = new ResourceLocation(shortenedLocation.getNamespace(), "textures/" + shortenedLocation.getPath() + ".png");
                        color = mat.getMaterialRGB();
                        break;
                    }
                    else {
                        tas = TextureUtils.getSideIconsForBlock(state)[EnumFacing.NORTH.ordinal()];
                        if (tas != TextureUtils.getMissingSprite()) {
                            break;
                        }
                        // if the sprite is missing, see if theres a non-missing sprite to be found
                    }
                }
            }

            tooltipStrings = new ArrayList<>();
            if (def.getAssignedName() != null) {
                tooltipStrings.add(def.getAssignedName());
            }
            else {
                tooltipStrings.add(FileUtility.trimFileName(def.getDepositName()));
            }
            for (FillerEntry filler : getAllFillers(def.getBlockFiller())) {
                IBlockState blockState = (IBlockState) filler.getPossibleResults().toArray()[0];
                String matName = getBaseMaterialName(blockState);
                if (!matName.isEmpty() && blockState.getBlock() instanceof BlockOre) {
                    // gt ores need special handling again
                    tooltipStrings.add(VOConfig.client.gregtech.oreNamePrefix + LocalizationUtils.format("item.material.oreprefix.ore",
                        GregTechAPI.materialManager.getMaterial(matName).getLocalizedName()));
                }
                else {
                    tooltipStrings.add(VOConfig.client.gregtech.oreNamePrefix + blockState.getBlock().getLocalizedName());
                }
            }
        }
    }

    public static String getBaseMaterialName(IBlockState state) {
        MaterialStack stack = OreDictUnifier.getMaterial(GTUtility.toItem(state));
        if (stack == null) return "";
        return stack.material.getResourceLocation().toString();
    }

    private static FillerEntry[] getAllFillers(BlockFiller filler) {
        if (filler instanceof LayeredBlockFiller layeredFiller) {
            return new FillerEntry[]{
                    layeredFiller.getPrimary(),
                    layeredFiller.getSecondary(),
                    layeredFiller.getBetween(),
                    layeredFiller.getSporadic()
            };
        }
        return filler.getAllPossibleStates().toArray(new FillerEntry[0]);
    }
}
