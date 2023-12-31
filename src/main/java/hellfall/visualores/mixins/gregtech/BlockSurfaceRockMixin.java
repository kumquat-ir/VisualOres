package hellfall.visualores.mixins.gregtech;

import gregtech.common.blocks.BlockMaterialBase;
import gregtech.common.blocks.BlockSurfaceRock;
import hellfall.visualores.database.gregtech.ore.ServerCache;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockSurfaceRock.class)
public abstract class BlockSurfaceRockMixin extends BlockMaterialBase {
    public BlockSurfaceRockMixin(Material material) {
        super(material);
    }

    @Inject(method = "onBlockActivated", at = @At("HEAD"))
    private void visualores$onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ, CallbackInfoReturnable<Boolean> cir) {
        if (!worldIn.isRemote && playerIn instanceof EntityPlayerMP) {
            ServerCache.instance.prospectSurfaceRockMaterial(worldIn.provider.getDimension(), state.getValue(getVariantProperty()), pos, (EntityPlayerMP) playerIn);
        }
    }
}
