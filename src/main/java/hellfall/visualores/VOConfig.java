package hellfall.visualores;

import net.minecraftforge.common.config.Config;

@Config(modid = Tags.MODID)
public class VOConfig {
    @Config.Comment("Client-side config options")
    @Config.Name("Client Options")
    public static ClientOptions client = new ClientOptions();

    @Config.Comment("Server-side config options")
    @Config.Name("Server Options")
    public static ServerOptions server = new ServerOptions();

    public static class ClientOptions {
        @Config.Comment("The map scale at which displayed ores will stop scaling.")
        @Config.RangeDouble(min = 0.1, max = 16)
        public double oreScaleStop = 1;

//        public double textDisplayStop = 1;
    }

    public static class ServerOptions {
        @Config.Comment({"The range, in blocks, that picking up a surface rock will search for veins in.", "Default: 48"})
        @Config.RangeInt(min = 1)
        public int surfaceRockProspectRange = 48;

        @Config.Comment({"The range, in blocks, that clicking an ore block will search for veins in.", "Default: 48"})
        @Config.RangeInt(min = 1)
        public int oreBlockProspectRange = 48;

        @Config.Comment({"Whether to cull cache entries in chunks that nothing generated in.",
                "Prevents \"phantom veins\" from being recorded in, for example, the End void.",
                "Can cause veins to not be recorded if they only generated blocks in chunks other than their centers. (i.e. at the edge of end islands)"})
        public boolean cullEmptyChunks = true;
    }
}
