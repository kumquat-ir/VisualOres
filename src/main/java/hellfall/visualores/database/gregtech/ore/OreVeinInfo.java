package hellfall.visualores.database.gregtech.ore;

import codechicken.lib.texture.TextureUtils;
import gregtech.api.GregTechAPI;
import gregtech.api.unification.OreDictUnifier;
import gregtech.api.unification.material.Material;
import gregtech.api.unification.material.info.MaterialIconType;
import gregtech.api.unification.stack.MaterialStack;
import gregtech.api.util.FileUtility;
import gregtech.api.util.GTUtility;
import gregtech.api.util.GregFakePlayer;
import gregtech.api.util.LocalizationUtils;
import gregtech.api.util.world.DummyWorld;
import gregtech.api.worldgen.config.OreDepositDefinition;
import gregtech.api.worldgen.filler.BlockFiller;
import gregtech.api.worldgen.filler.FillerEntry;
import gregtech.api.worldgen.filler.LayeredBlockFiller;
import gregtech.api.worldgen.populator.SurfaceRockPopulator;
import gregtech.common.blocks.BlockOre;
import hellfall.visualores.VOConfig;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
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
                IBlockState state = (IBlockState) filler.getPossibleResults().toArray()[0];
                String matName = getBaseMaterialName(state);
                if (!matName.isEmpty() && state.getBlock() instanceof BlockOre) {
                    // gt ores need special handling due to ore variants
                    // we dont want "Red Sand Gold Ore" etc popping up in a tooltip
                    tooltipStrings.add(VOConfig.client.gregtech.oreNamePrefix + LocalizationUtils.format("item.material.oreprefix.ore",
                        GregTechAPI.materialManager.getMaterial(matName).getLocalizedName()));
                    continue;
                }
                ItemStack stack = getStackFromState(state);
                if (!stack.isEmpty()) {
                    // if we can get a stack from the blockstate, great, that makes it easy (and usually correct)
                    tooltipStrings.add(VOConfig.client.gregtech.oreNamePrefix + stack.getDisplayName());
                }
                else {
                    // otherwise, use this very error-prone method that only sometimes gets an actual translation
                    tooltipStrings.add(VOConfig.client.gregtech.oreNamePrefix + state.getBlock().getLocalizedName());
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

    /**
     * adapted from {@link gregtech.integration.jei.multiblock.MultiblockInfoRecipeWrapper#gatherStructureBlocks}
     */
    @SuppressWarnings("JavadocReference")
    private static ItemStack getStackFromState(IBlockState state) {
        // the horrible, horrible way to get an itemstack from a blockstate while minimizing errors

        ItemStack stack = ItemStack.EMPTY;
        BlockPos pos = new BlockPos(0, 0, 0);

        if (stack.isEmpty()) {
            // try the itemstack constructor
            stack = GTUtility.toItem(state);
        }
        if (stack.isEmpty()) {
            // add the first of the block's drops if the others didn't work
            NonNullList<ItemStack> list = NonNullList.create();
            state.getBlock().getDrops(list, DummyWorld.INSTANCE, pos, state, 0);
            if (!list.isEmpty()) {
                ItemStack is = list.get(0);
                if (!is.isEmpty()) {
                    stack = is;
                }
            }
        }
        if (stack.isEmpty()) {
            // if everything else doesn't work, try the not great getPickBlock() with some dummy values
            stack = state.getBlock().getPickBlock(state, new RayTraceResult(Vec3d.ZERO, EnumFacing.UP, pos), DummyWorld.INSTANCE, pos, new GregFakePlayer(DummyWorld.INSTANCE));
        }
        return stack;
    }
}
