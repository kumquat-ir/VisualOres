# Adding a map layer
1. Create a class that extends `RenderLayer`
   - Create a static instance of `ButtonState.Button` in this class
   - The name of the button is used to determine the tooltip lang key and the icon
   - The `sort` parameter is used to determine how the button list is sorted
     - The ore vein button has `sort = 0`, and the underground fluid button has `sort = 1`
   - This class must have a constructor that takes no arguments and calls `super(Button)` with the button instance above
3. Call `RenderLayer.registerLayer()` with that class in client preinit

For example:

```java
// FooRenderLayer.java
public class FooRenderLayer {
    public static final ButtonState.Button FOO_BUTTON = new ButtonState.Button("foo", 2);
    
    public FooRenderLayer() {
        super(FOO_BUTTON);
    }

    // implement render() and updateVisibleArea()
    // if this layer should have tooltips, override getTooltip()
}


// in client FMLPreInitializationEvent handler
RenderLayer.registerLayer(FooRenderLayer.class);
```
In a lang file:
`visualores.button.foo=A tooltip!`  
In `assets/`:
- `journeymap/theme/flat/icon/foo.png`
  - Should be a single square icon with padding
- `visualores/textures/xaero/foo.png`
  - Should have two equal-sized square icons next to each other, left is deactivated state, right is activated state
  - See [the included icons](src/main/resources/assets/visualores/textures/xaero) for reference
