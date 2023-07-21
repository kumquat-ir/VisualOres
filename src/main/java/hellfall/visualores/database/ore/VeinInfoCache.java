package hellfall.visualores.database.ore;

import gregtech.api.worldgen.config.OreDepositDefinition;
import gregtech.api.worldgen.config.WorldGenRegistry;

import java.util.HashMap;
import java.util.Map;

public class VeinInfoCache {
    private static Map<String, OreVeinInfo> cache;

    public static void init() {
        cache = new HashMap<>();
        for (OreDepositDefinition def : WorldGenRegistry.getOreDeposits()) {
            if (def.isVein()) {
                cache.put(def.getDepositName(), new OreVeinInfo(def));
            }
        }
    }

    public static OreVeinInfo getByName(String name) {
        return cache.get(name);
    }

    public static OreDepositDefinition getDefinitionByName(String name) {
        for (OreDepositDefinition def : WorldGenRegistry.getOreDeposits()) {
            if (def.getDepositName().equals(name)) {
                return def;
            }
        }
        return null;
    }
}
