# Adding a map layer
1. Create a class that extends `RenderLayer`
   - This class must have a constructor that takes a `String` and calls `super(String)` with it. No other constructors will be called.
2. Call `Layers.registerLayer()` with that class and a unique key for it in client preinit
3. Add a lang entry for the button's tooltip and icons for displaying the button
   - The tooltip lang entry will also be used for the layer's entry in the controls list

For example:

```java
// FooRenderLayer.java
public class FooRenderLayer extends RenderLayer {
    public FooRenderLayer(String key) {
        super(key);
    }

    // implement render() and updateVisibleArea()
    // override other methods of RenderLayer as needed
}


// in client FMLPreInitializationEvent handler
Layer.registerLayer(FooRenderLayer.class, "bar");
```
In a lang file:
`visualores.button.bar=Toggle foo overlay`  
In `assets/`:
- `journeymap/theme/flat/icon/bar.png`
  - Should be a single square icon with padding
  - See [the included icons](src/main/resources/assets/journeymap/theme/flat/icon) and Journeymap's own icons for reference
- `visualores/textures/buttons/bar.png`
  - Should have two equal-sized square icons next to each other, left is deactivated state, right is activated state
  - See [the included icons](src/main/resources/assets/visualores/textures/buttons) for reference

# Adding a client cache
For saving files in VisualOres' client-side cache directory.
1. Create a class that implements `IClientCache` or either of its child interfaces
2. Call `ClientCacheManager.registerClientCache()` with an instance of that class in client preinit

# Other useful things
If you're writing a renderer that uses fluids' colors, use `DrawUtils#getFluidColor(Fluid)` to use the fluid color override list. No more white-colored water and lava!

If your integration requires VisualOres to be on both the server and client, call `VisualOres#addModRequiringServer(String)` to require that.