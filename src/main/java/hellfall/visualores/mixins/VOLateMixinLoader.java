package hellfall.visualores.mixins;

import com.google.common.collect.ImmutableList;
import net.minecraftforge.fml.common.Loader;
import zone.rong.mixinbooter.ILateMixinLoader;

import java.util.List;
import java.util.stream.Collectors;

public class VOLateMixinLoader implements ILateMixinLoader {
    private static final List<String> mixinInfixes = ImmutableList.of("journeymap", "xaerominimap", "xaeroworldmap", "gregtech", "astralsorcery", "thaumcraft");

    @Override
    public List<String> getMixinConfigs() {
        return mixinInfixes.stream().map(infix -> "mixins.visualores." + infix + ".json").collect(Collectors.toList());
    }

    @Override
    public boolean shouldMixinConfigQueue(String mixinConfig) {
        String[] parts = mixinConfig.split("\\.");
        return parts.length != 4 || Loader.isModLoaded(parts[2]);
    }
}
