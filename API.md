# Adding a map layer

1. Create a static instance of `ButtonState.Button` somewhere
   - The name of the button is used to determine the tooltip lang key and the icon
   - The `sort` parameter is used to determine how the button list is sorted
     - The ore vein button has `sort = 0`, and the underground fluid button has `sort = 1`
2. Create a class that extends `RenderLayer`
   - This class must have a constructor that takes no arguments and calls `super(Button)` with the button instance above
3. Call `RenderLayer.registerLayer()` with that class in client preinit

For example:
```java
// somewhere
public static final ButtonState.Button FOO_BUTTON = new ButtonState.Button("foo", 2);


// FooRenderLayer.java
public class FooRenderLayer {
    public FooRenderLayer() {
        super(FOO_BUTTON);
    }

    // implement render() and updateVisibleArea()
    // if this layer should have tooltips, override getTooltip()
}


// in client FMLPreInitializationEvent handler
RenderLayer.registerLayer(FooRenderLayer.class);
```
