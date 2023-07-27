# VisualOres
"[VisualProspecting](https://github.com/GTNewHorizons/VisualProspecting/) but for 1.12"

A mod that adds various overlays to map mods.

Supports Journeymap and Xaero's Minimap and World Map.

Overlays can be toggled by either a button on the world map or by pressing a keybind in-game (unbound by default - check the controls settings)  
The location and order of the buttons can be customized in the config:
- The order of the buttons can be changed either by a list or just reversing the order
  - Reversing the order does not require restarting minecraft, while modifying the list does
  - Changes to the order list will also affect what order layers render in if multiple are enabled
- Journeymap users can put the buttons either next to the main map types or in their own toolbar on the right of the screen
- Xaero's World Map users can put the buttons anywhere!
  - The default location was chosen to not interfere with any other map controls

## Current overlays

### [GregTech CEu](https://github.com/GregTechCEu/GregTech)
Requires GTCEU version 2.7.2  
Requires VisualOres to be installed on both the client and server

#### Ore vein overlay
![2023-07-27_12 25 40](https://github.com/kumquat-ir/VisualOres/assets/66188216/91cc7f81-a8f6-44b3-ad27-e15273f4b8a8)

Displays ore veins you have discovered. Pick up surface rocks, right-click ore blocks, or use an electric prospector on ore mode to discover veins.  

Press the "action key" (default: delete) while hovering over a vein to mark it as depleted (or not depleted).  
Double-click a vein to mark it as a waypoint, and again to unmark it.

VisualOres will attempt to generate ore vein locations for worlds that were generated before it was added, but it *will* generate some inaccurate vein positions!  
Veins may be missing, and veins that do not exist may be marked.  
(Still better than nothing, though. Most veins *should* be accurate.)

#### Underground fluid overlay

![2023-07-27_12 27 01](https://github.com/kumquat-ir/VisualOres/assets/66188216/810835ba-5437-4a7d-b9f3-4295e85c68d2)

Displays underground fluid fields you have discovered. Use an electric prospector on fluid mode to discover fields.  
(Yes, the weird shapes of fields are correct. GTCEU 2.7.2 and earlier have a bug with fluid vein generation.)
