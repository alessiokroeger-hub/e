# Silker Milker — Future Client / Dark Violet

Minecraft 26.3 · Fabric · Java 25

## Implemented in this source update
- Future Dark Violet ClickGUI with Overview, Combat, Visuals, HUD, Utility, Cosmetics and Settings pages
- Right Shift opens/closes the ClickGUI
- H toggles the HUD
- HUD options saved to `config/silker-milker.json`:
  - Coordinates
  - FPS
  - Attack cooldown percentage
  - Facing direction
  - Total armor damage
- Five accent presets and persistent accent color
- Client-side UI only; no combat automation or anti-cheat bypass

## UI placeholders / next development tasks
- Amethyst fully-grown cluster detection
- Container/world hint scanning
- Real custom hand swing animation
- Cosmetic item model overrides
- Editable RGB color picker
- More HUD widgets and profile management

The Amethyst Hints and Container Hints controls currently save their settings but do not scan the world yet. Cosmetic controls are prototypes, not finished animation/model implementations.

## Build environment
Requires JDK 25, Gradle, Minecraft 26.3, Fabric Loader 0.20.1,
Fabric API 0.166.0+26.3 and Fabric Loom 1.19-SNAPSHOT.

Run `gradle build`. Output should appear in `build/libs/`.

## Testing status
Not compiled or launched here. The available environment has Java 21 and no Gradle installation,
so a Minecraft 26.3 build cannot be verified in this session. Check mappings/API signatures
against the exact published 26.3 artifacts before distributing.
