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
        @Config.Name("Gregtech")
        public GTClientOptions gregtech = new GTClientOptions();

        @Config.Name("Journeymap")
        public JourneymapOptions jmap = new JourneymapOptions();

        @Config.Name("Xaero's Maps")
        public XaerosMapOptions xmap = new XaerosMapOptions();

        @Config.Comment("Whether to display tooltips for all overlays in the same location.")
        public boolean stackTooltips = true;

        @Config.Comment("Whether to enable rendering on minimaps")
        public boolean enableMinimapRendering = true;

        @Config.Comment("Whether to allow multiple overlays to render at the same time")
        public boolean allowMultipleLayers = false;

        @Config.Comment("Whether to reverse the normal order of buttons")
        public boolean reverseButtonOrder = false;

        @Config.Comment("What the normal order of buttons (and layers) should be")
        @Config.RequiresMcRestart
        public String[] buttonOrder = new String[]{
                "oreveins",
                "excavator",
                "undergroundfluid",
                "neromantic",
                "aura_flux",
                "starlight"
        };

        @Config.Comment("Layers to not register")
        @Config.RequiresMcRestart
        public String[] ignoreLayers = new String[0];

        @Config.Comment({"Fluid colors to override in the underground fluid overlay",
                "Format: fluid name=RGB color"})
        @Config.RequiresMcRestart
        public String[] fluidColorOverrides = new String[]{
                "water=#6B7AF7",
                "lava=#D14F0C"
        };

        public static class JourneymapOptions {
            @Config.Comment("Whether to put buttons on a separate toolbar on the right instead of the map type toolbar.")
            public boolean rightToolbar = true;
        }

        public static class XaerosMapOptions {
            @Config.Comment("Which part of the screen to anchor buttons to")
            public Anchor buttonAnchor = Anchor.BOTTOM_LEFT;

            @Config.Comment("Which direction the buttons will go")
            public Direction direction = Direction.HORIZONTAL;

            @Config.Comment("How horizontally far away from the anchor to place the buttons")
            public int xOffset = 20;

            @Config.Comment("How vertically far away from the anchor to place the buttons")
            public int yOffset = 0;

            public enum Anchor {
                TOP_LEFT, TOP_CENTER, TOP_RIGHT, RIGHT_CENTER, BOTTOM_RIGHT, BOTTOM_CENTER, BOTTOM_LEFT, LEFT_CENTER;

                public boolean isCentered() {
                    return this == TOP_CENTER || this == RIGHT_CENTER || this == BOTTOM_CENTER || this == LEFT_CENTER;
                }

                public Direction usualDirection() {
                    return switch (this) {
                        case TOP_CENTER, BOTTOM_CENTER -> Direction.HORIZONTAL;
                        case RIGHT_CENTER, LEFT_CENTER -> Direction.VERTICAL;
                        default -> null;
                    };
                }
            }

            public enum Direction {
                VERTICAL, HORIZONTAL
            }
        }

        public static class GTClientOptions {

            @Config.Comment("The map scale at which displayed ores will stop scaling.")
            @Config.RangeDouble(min = 0.1, max = 16)
            public double oreScaleStop = 1;

            @Config.Comment("The size, in pixels, of ore icons on the map")
            @Config.RangeInt(min = 4)
            public int oreIconSize = 32;

            @Config.Comment("The string prepending ore names in the ore vein tooltip")
            public String oreNamePrefix = "- ";
        }
    }

    public static class ServerOptions {
        @Config.Name("Gregtech")
        public GTServerOptions gregtech = new GTServerOptions();

        public static class GTServerOptions {
            @Config.Comment({"The radius, in blocks, that picking up a surface rock will search for veins in.",
                    "-1 to disable.", "Default: 24"})
            @Config.RangeInt(min = 1)
            public int surfaceRockProspectRange = 24;

            @Config.Comment({"The radius, in blocks, that clicking an ore block will search for veins in.",
                    "-1 to disable", "Default: 24"})
            @Config.RangeInt(min = 1)
            public int oreBlockProspectRange = 24;

            @Config.Comment({"Whether to cull cache entries in chunks that nothing generated in.",
                    "Prevents \"phantom veins\" from being recorded in, for example, the End void.",
                    "Can cause veins to not be recorded if they only generated blocks in chunks other than their centers. (i.e. at the edge of end islands)",
                    "Causes ore cache retrogen to take longer.",
                    "Default: true"})
            public boolean cullEmptyChunks = true;

        }
    }
}
