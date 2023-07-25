package hellfall.visualores.map.generic;

import hellfall.visualores.VOConfig;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

public class Layers {
    private static final Map<Class<? extends RenderLayer>, String> layerClasses = new HashMap<>();
    private static List<Class<? extends RenderLayer>> sortedLayers;

    public static void registerLayer(Class<? extends RenderLayer> clazz, String key) {
        if (Arrays.asList(VOConfig.client.ignoreLayers).contains(key)) return;
        layerClasses.put(clazz, key);
        new ButtonState.Button(key);
    }

    public static void addLayersTo(List<RenderLayer> layers) {
        if (sortedLayers == null) {
            sortedLayers = layerClasses.keySet().stream().sorted(
                    Comparator.comparingInt(l -> Arrays.asList(VOConfig.client.buttonOrder).indexOf(layerClasses.get(l)))
            ).collect(Collectors.toList());
        }
        for (Class<? extends RenderLayer> layer : sortedLayers) {
            try {
                layers.add(layer.getConstructor(String.class).newInstance(layerClasses.get(layer)));
            } catch (InstantiationException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static Collection<String> allKeys() {
        return layerClasses.values();
    }
}
