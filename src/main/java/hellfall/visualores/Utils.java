package hellfall.visualores;

import gregtech.api.worldgen.config.OreDepositDefinition;
import gregtech.api.worldgen.config.WorldGenRegistry;

public class Utils {
    public static OreDepositDefinition getDefinitionByName(String name) {
        for (OreDepositDefinition def : WorldGenRegistry.getOreDeposits()) {
            if (def.getDepositName().equals(name)) {
                return def;
            }
        }
        return null;
    }
}
