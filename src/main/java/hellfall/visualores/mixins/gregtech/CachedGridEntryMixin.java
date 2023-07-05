package hellfall.visualores.mixins.gregtech;

import gregtech.api.worldgen.config.OreDepositDefinition;
import gregtech.api.worldgen.generator.CachedGridEntry;
import gregtech.api.worldgen.generator.GTWorldGenCapability;
import hellfall.visualores.VisualOres;
import hellfall.visualores.database.ServerCache;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = CachedGridEntry.class, remap = false)
public abstract class CachedGridEntryMixin {
    @Shadow private int veinCenterX;
    @Shadow private int veinCenterY;
    @Shadow private int veinCenterZ;
    @Shadow @Final private int gridX;
    @Shadow @Final private int gridZ;

    @Unique private int dimid;

    // ideally, this would be injected into the constructor right before triggerVeinsGeneration, but this mixin version cant do that
    // this method is called exactly once in the constructor, and nowhere else, so it works perfectly
    @Inject(method = "searchMasterOrNull", at = @At("HEAD"))
    private void injectInit(World world, CallbackInfoReturnable<GTWorldGenCapability> cir) {
        this.dimid = world.provider.getDimension();
    }

    @Inject(method = "doGenerateVein",
            at = @At(value = "INVOKE",
                    target = "Lgregtech/api/worldgen/shape/ShapeGenerator;generate(Ljava/util/Random;Lgregtech/api/worldgen/shape/IBlockGeneratorAccess;)V"
            )
    )
    private void injectGenerateVein(OreDepositDefinition definition, CallbackInfo ci) {
//        VisualOres.LOGGER.info("gen vein " + definition.getDepositName() + " at " + veinCenterX + "," + veinCenterY + "," + veinCenterZ);
//        VisualOres.LOGGER.info("in grid " + gridX + ", " + gridZ + ", dimension " + dimid);
        if (definition.isVein()) {
            ServerCache.instance.addVein(dimid, veinCenterX, veinCenterZ, gridX, gridZ, definition.getDepositName());
        }
    }
}
