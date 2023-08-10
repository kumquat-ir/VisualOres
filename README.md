# VisualOres
<a href="https://www.curseforge.com/minecraft/mc-mods/visualores"><img src="https://cf.way2muchnoise.eu/895539.svg?badge_style=for_the_badge" alt="CurseForge"></a>
<a href="https://modrinth.com/mod/visualores"><img src="https://img.shields.io/modrinth/dt/visualores?logo=modrinth&label=&suffix=%20&style=for-the-badge&color=2d2d2d&labelColor=5ca424&logoColor=1c1c1c" alt="Modrinth"></a>  
"[VisualProspecting](https://github.com/GTNewHorizons/VisualProspecting/) but for 1.12"

A mod that adds various overlays to map mods.

Requires CodeChickenLib and MixinBooter.

Supports Journeymap and Xaero's Minimap and World Map.

Overlays can be toggled by either a button on the world map or by pressing a keybind in-game (unbound by default - check the controls settings)  
The location and order of the buttons can be customized in the config:
- The order of the buttons can be changed either by a list or just reversing the order
  - Reversing the order does not require restarting minecraft, while modifying the list does not
  - Changes to the order list will also affect what order layers render in if multiple are enabled
- Journeymap users can put the buttons either next to the main map types or in their own toolbar on the right of the screen
- Xaero's World Map users can put the buttons anywhere!
  - The default location was chosen to not interfere with any other map controls

Prospection data can be shared to other players with `/vo share <player name>`. This requires VisualOres to be installed on the server and both clients.

Your client-side cache folder can be opened with `/vo openCacheFolder`, and all prospection data can be reset with `/vo resetClientCache`. Resetting the cache is not reversible!

## Current overlays

### [GregTech CEu](https://github.com/GregTechCEu/GregTech)
Requires GTCEu version 2.7.3  
Requires VisualOres to be installed on both the client and server

#### Ore vein overlay
![2023-07-27_12 25 40](https://github.com/kumquat-ir/VisualOres/assets/66188216/91cc7f81-a8f6-44b3-ad27-e15273f4b8a8)

Displays ore veins you have discovered. Pick up surface rocks, right-click ore blocks, or use an electric prospector on ore mode to discover veins.  

Press the "action key" (default: delete) while hovering over a vein to mark it as depleted (or not depleted).  
Double-click a vein to mark it as a waypoint, and again to unmark it.

VisualOres will attempt to generate ore vein locations for worlds that were generated before it was added, but it *will* generate some inaccurate vein positions!  
Veins may be missing, and veins that do not exist may be marked.  
(Still better than nothing, though. Most veins *should* be accurate.)  
(This uses a modified version of [Enklume](https://github.com/GTNewHorizons/Enklume) to parse the save data, located [here](https://github.com/kumquat-ir/VisualOres/tree/master/src/main/java/hellfall/visualores/lib/io/xol/enklume))

#### Underground fluid overlay
![2023-07-27_12 27 01](https://github.com/kumquat-ir/VisualOres/assets/66188216/810835ba-5437-4a7d-b9f3-4295e85c68d2)

Displays underground fluid fields you have discovered. Use an electric prospector on fluid mode to discover fields.  
(Yes, fields being shown as larger around x=0 and z=0 is correct. Underground fluid veins generated with GTCEu 2.7.2 and earlier have a bug with generation that causes this.)

### [Immersive Engineering](https://www.curseforge.com/minecraft/mc-mods/immersive-engineering)
Also contains integration with [Immersive Petroleum](https://www.curseforge.com/minecraft/mc-mods/immersive-petroleum)

#### Excavator vein overlay
![2023-08-02_23 18 53](https://github.com/kumquat-ir/VisualOres/assets/66188216/158d9247-7e32-48f4-984b-3a7be8971bc3)

Displays excavator veins you have discovered. Pick up a core sample to add its data to your map.  
Double click on a vein to toggle it as a waypoint.

### [Thaumcraft](https://www.curseforge.com/minecraft/mc-mods/thaumcraft)

#### Aura/flux level overlay
![2023-08-02_23 19 02](https://github.com/kumquat-ir/VisualOres/assets/66188216/78e0e29a-cc44-4599-bdd2-d843214f57ff)

Displays aura/flux levels you have seen in chunks. Hold a thaumometer to add the levels shown in the hud to your map.

### [Astral Sorcery](https://www.curseforge.com/minecraft/mc-mods/astral-sorcery)

#### Starlight level overlay
![2023-08-02_23 19 40](https://github.com/kumquat-ir/VisualOres/assets/66188216/53d114d3-673d-4783-95c3-11739606302b)

Displays starlight concentration levels. Hold a fosic resonator to add nearby starlight levels to your map.  
Lighter blue means higher concentration (the areas shown in-world with white sparkles).

#### Neromantic Prime fluid vein overlay
![2023-08-02_23 19 24](https://github.com/kumquat-ir/VisualOres/assets/66188216/d5279679-c3c0-4a44-8cc3-036af01e69b5)

Displays fluid veins you have discovered. Hold an ichosic resonator to add veins to your map as the particle effects spawn.  
(This means veins are only discovered when a particle effect spawns in their chunk, so it can take some time before the fluid in a specific chunk is discovered! Be patient.)