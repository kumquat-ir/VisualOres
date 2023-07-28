package hellfall.visualores.mixins;

import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import org.jetbrains.annotations.Nullable;
import zone.rong.mixinbooter.IEarlyMixinLoader;

import java.lang.management.ManagementFactory;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@IFMLLoadingPlugin.MCVersion("1.12.2")
public class VOEarlyMixinLoader implements IFMLLoadingPlugin, IEarlyMixinLoader {
    @Override
    public String[] getASMTransformerClass() {
        return new String[0];
    }

    @Override
    public String getModContainerClass() {
        return null;
    }

    @Nullable
    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {}

    @Override
    public String getAccessTransformerClass() {
        return null;
    }

    @Override
    public List<String> getMixinConfigs() {
        return Collections.singletonList("mixins.visualores.immersiveengineering.json");
    }

    @Override
    public boolean shouldMixinConfigQueue(String mixinConfig) {
        // there is only one, we can ignore the config string param
        try {
            Class.forName("blusunrize.immersiveengineering.common.asm.IELoadingPlugin", false, this.getClass().getClassLoader());
            return true;
        } catch (ClassNotFoundException ignored) {
        }
        // forges jij loading doesnt work in the deobf dev environment for some reason... meaning that class never shows up! incredible!
        // so only enable if this is running in a deobf dev env for VisualOres itself, don't want to break addons' dev envs accidentally
        return ((boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment")) && isDevEnvForVO();
    }

    private boolean isDevEnvForVO() {
        // Yikes
        for (String arg : ManagementFactory.getRuntimeMXBean().getInputArguments()) {
            if (arg.equals("-ea:hellfall.visualores")) {
                return true;
            }
        }
        return false;
    }
}
