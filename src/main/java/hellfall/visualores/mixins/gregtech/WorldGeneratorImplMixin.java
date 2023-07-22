package hellfall.visualores.mixins.gregtech;

import gregtech.api.worldgen.generator.CachedGridEntry;
import gregtech.api.worldgen.generator.WorldGeneratorImpl;
import hellfall.visualores.VOConfig;
import hellfall.visualores.database.ore.ServerCache;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(value = WorldGeneratorImpl.class, remap = false)
public abstract class WorldGeneratorImplMixin {
    @Unique private static boolean shouldCull = VOConfig.server.gregtech.cullEmptyChunks;

    @Redirect(method = "generateInternal", at = @At(value = "INVOKE", target = "Lgregtech/api/worldgen/generator/CachedGridEntry;populateChunk(Lnet/minecraft/world/World;IILjava/util/Random;)Z"))
    private static boolean visualores$injectGetChunksToCull(CachedGridEntry gridEntry, World world, int chunkX, int chunkZ, Random random) {
        boolean generatedAnything = gridEntry.populateChunk(world, chunkX, chunkZ, random);
        if (generatedAnything) {
            shouldCull = false;
        }
        return generatedAnything;
    }

    @Inject(method = "generateInternal", at = @At("TAIL"))
    private static void visualores$injectCullChunks(World world, int selfGridX, int selfGridZ, int chunkX, int chunkZ, Random random, CallbackInfo ci) {
        if (shouldCull) {
            ServerCache.instance.removeAllInChunk(world.provider.getDimension(), new ChunkPos(chunkX, chunkZ));
        }
        shouldCull = VOConfig.server.gregtech.cullEmptyChunks;
    }
}
