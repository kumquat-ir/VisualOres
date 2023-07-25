package hellfall.visualores.mixins.gregtech;

import gregtech.api.util.IBlockOre;
import gregtech.common.blocks.BlockOre;
import hellfall.visualores.database.gregtech.ore.OreVeinInfo;
import hellfall.visualores.database.gregtech.ore.ServerCache;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import javax.annotation.ParametersAreNonnullByDefault;

@Mixin(BlockOre.class)
public abstract class BlockOreMixin extends Block implements IBlockOre {

    @Unique private long activatedTime = 0;

    public BlockOreMixin(net.minecraft.block.material.Material materialIn) {
        super(materialIn);
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (playerIn instanceof EntityPlayerMP) {
            if (System.currentTimeMillis() > activatedTime + 1000) {
                activatedTime = System.currentTimeMillis();
                String oreMaterialName = OreVeinInfo.getBaseMaterialName(state);
                if (!oreMaterialName.isEmpty()) {
//                    VisualOres.LOGGER.info("in dim " + worldIn.provider.getDimension() + " at " + pos + " : " + oreMaterialName);
                    ServerCache.instance.prospectOreBlock(worldIn.provider.getDimension(), oreMaterialName, pos, (EntityPlayerMP) playerIn);
                }
            }
        }
        return super.onBlockActivated(worldIn, pos, state, playerIn, hand, facing, hitX, hitY, hitZ);
    }
}
