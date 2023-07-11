package hellfall.visualores.database;

import gregtech.api.GregTechAPI;
import gregtech.api.unification.OreDictUnifier;
import gregtech.api.unification.material.Material;
import gregtech.api.unification.material.info.MaterialIconType;
import gregtech.api.unification.stack.MaterialStack;
import gregtech.api.util.FileUtility;
import gregtech.api.util.GTUtility;
import gregtech.api.util.LocalizationUtils;
import gregtech.api.worldgen.config.OreDepositDefinition;
import gregtech.api.worldgen.filler.FillerEntry;
import gregtech.api.worldgen.filler.LayeredBlockFiller;
import gregtech.api.worldgen.populator.SurfaceRockPopulator;
import gregtech.common.blocks.BlockOre;
import hellfall.visualores.VOConfig;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class OreVeinInfo {
    public Material surfaceRockMaterial;
    public List<String> oreMaterialStrings;

    public ResourceLocation texture;
    public int color;
    public List<String> tooltipStrings;

    public OreVeinInfo(OreDepositDefinition def) {
        oreMaterialStrings = new ArrayList<>();
        if (def.getVeinPopulator() instanceof SurfaceRockPopulator) {
            this.surfaceRockMaterial = ((SurfaceRockPopulator) def.getVeinPopulator()).getMaterial();
        }
        if (def.getBlockFiller() instanceof LayeredBlockFiller) {
            for (FillerEntry filler : new FillerEntry[]{
                    ((LayeredBlockFiller) def.getBlockFiller()).getPrimary(),
                    ((LayeredBlockFiller) def.getBlockFiller()).getSecondary(),
                    ((LayeredBlockFiller) def.getBlockFiller()).getBetween(),
                    ((LayeredBlockFiller) def.getBlockFiller()).getSporadic()
            }) {
                IBlockState blockState = (IBlockState) filler.getPossibleResults().toArray()[0];
                oreMaterialStrings.add(getBaseMaterialName(blockState));
            }
        }

        if (FMLCommonHandler.instance().getSide().isClient()) {
            if (def.getBlockFiller() instanceof LayeredBlockFiller) {
                Collection<IBlockState> possiblePrimaryStates = ((LayeredBlockFiller) def.getBlockFiller()).getPrimary().getPossibleResults();
                for (IBlockState state : possiblePrimaryStates) {
                    if (state.getBlock() instanceof BlockOre) {
                        Material mat = ((BlockOre) state.getBlock()).material;
                        ResourceLocation shortenedLocation = MaterialIconType.ore.getBlockTexturePath(mat.getMaterialIconSet());
                        texture = new ResourceLocation(shortenedLocation.getNamespace(), "textures/" + shortenedLocation.getPath() + ".png");
                        color = mat.getMaterialRGB();
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
            for (String mat : oreMaterialStrings) {
                // the material name is guaranteed to come from a material that exists
                tooltipStrings.add(VOConfig.client.oreNamePrefix + LocalizationUtils.format("item.material.oreprefix.ore",
                        GregTechAPI.materialManager.getMaterial(mat).getLocalizedName()));
            }
        }
    }

    public static String getBaseMaterialName(IBlockState state) {
        MaterialStack stack = OreDictUnifier.getMaterial(GTUtility.toItem(state));
        if (stack == null) return "";
        return stack.material.toString();
    }
}
