# HalalBuilds User Guide

This guide explains how admins and builders use HalalBuilds v1.1.0 in-game.

HalalBuilds v1.1.0 is command-based with visual particle previews. It does not include a graphical build browser yet. Selection is done through WorldEdit/FAWE, and build management is done with `/halalbuilds` commands.

## Requirements

Server requirements:

- Paper `1.21.11`
- Java `21`
- FAWE installed
- HalalBuilds installed

Recommended:

- LuckPerms or another permissions plugin
- A test world before using the plugin on important builds

## Installation

1. Stop the server.
2. Install FAWE in the server `plugins/` folder.
3. Copy `HalalBuilds-1.1.0.jar` into the server `plugins/` folder.
4. Start the server.
5. Confirm the plugin creates:

```text
plugins/HalalBuilds/
  builds/
  imports/
  exports/
  config.yml
```

6. Edit `plugins/HalalBuilds/config.yml` if needed.
7. Restart the server or run:

```text
/hb reload
```

## Permissions

Operators can use HalalBuilds by default.

With LuckPerms, give admins full access:

```text
/lp group admin permission set halalbuilds.admin true
```

Recommended builder permissions:

```text
/lp group builder permission set halalbuilds.use true
/lp group builder permission set halalbuilds.wand true
/lp group builder permission set halalbuilds.save true
/lp group builder permission set halalbuilds.copy true
/lp group builder permission set halalbuilds.paste true
/lp group builder permission set halalbuilds.preview true
/lp group builder permission set halalbuilds.move true
/lp group builder permission set halalbuilds.confirm true
/lp group builder permission set halalbuilds.undo true
```

Trusted builder extras:

```text
/lp group trusted-builder permission set halalbuilds.cut true
/lp group trusted-builder permission set halalbuilds.import true
/lp group trusted-builder permission set halalbuilds.export true
```

Owner/admin extras:

```text
/lp group owner permission set halalbuilds.delete true
/lp group owner permission set halalbuilds.reload true
```

## Basic Workflow

## V1 Selection Behavior

HalalBuilds v1 uses cuboid selection.

That means the selected build is everything inside the box between position 1 and position 2:

- building blocks
- interior air, if air pasting is enabled
- grass
- dirt
- floor blocks
- decorations
- anything else inside the selected cuboid

For best results, select tightly around the building. If you include grass, dirt, or extra terrain inside the cuboid, that extra material may be saved with the build.

Future versions may add clean-scan options to trim empty air, ignore grass/dirt, remove selected terrain blocks from saved schematics, or detect only connected structure blocks. That is not part of v1.

### 1. Get A Selection Wand

```text
/hb wand
```

This gives a wooden axe. Use it like a WorldEdit selection wand:

- Left-click one corner.
- Right-click the opposite corner.

### 2. Save A Build

After selecting a complete cuboid around the structure:

```text
/hb save starter_house
```

Save without entities:

```text
/hb save starter_house --no-entities
```

Saved builds are stored under:

```text
plugins/HalalBuilds/builds/starter_house/
```

If a build was saved with an older test jar and pastes as air or appears to do nothing, delete it and save it again with the current jar.

### 3. List Saved Builds

```text
/hb list
```

### 4. View Build Info

```text
/hb info starter_house
```

### 5. Paste A Build

Look at the target block where the build should be placed, then run:

```text
/hb paste starter_house
```

HalalBuilds places the build above the block you are looking at. If you are not looking at a block within range, it uses your current block location.

By default in v1.1.0, schematic air is ignored during paste. Use `--paste-air` when you want schematic air to replace existing blocks.

## Copy And Clipboard

Copy stores a temporary per-player clipboard.

```text
/hb copy
```

Paste the clipboard:

```text
/hb paste clipboard
```

Clipboard data is not permanent. It is cleared when the plugin disables or the server restarts.

## Cut

Cut copies the selection first, then removes the original selected blocks.

```text
/hb cut
```

Then paste it elsewhere:

```text
/hb paste clipboard
```

Use cut carefully. Test in a copy of the world first until the plugin has been proven on your server.

## Rotation

Supported rotations:

- `0`
- `90`
- `180`
- `270`

Example:

```text
/hb paste starter_house --rotate 90
```

You can also rotate a pending preview before confirming it:

```text
/hb paste starter_house --preview
/hb rotate 90
/hb confirm
```

`/hb rotate` only changes a pending preview. If you do not have a pending preview, create one first with `/hb paste <name|clipboard> --preview`.

## Paste Modes

### Smart Foundation

Smart foundation is the default mode.

It is designed for terrain such as:

- mountains
- slopes
- cave openings
- water edges
- uneven land

It tries to:

- detect the structure footprint
- clear intersecting terrain
- fill empty space under the building with foundation blocks
- stop if denylisted blocks are detected
- require confirmation for large terrain changes

Example:

```text
/hb paste starter_house --mode smart_foundation
```

### Exact

Exact mode is raw schematic placement.

Use it when you want the schematic placed without smart foundation terrain logic:

```text
/hb paste starter_house --mode exact
```

## Preview And Confirmation

Use preview before risky placements:

```text
/hb paste starter_house --preview
```

HalalBuilds will show a visual particle outline and a chat summary with:

- source build or clipboard
- dimensions
- volume
- world
- target coordinates
- rotation
- paste mode
- air behavior
- entity behavior
- skipped air count
- entity count
- terrain blocks to clear
- foundation blocks to place
- denylisted blocks, if found

Confirm:

```text
/hb confirm
```

When confirmation succeeds, HalalBuilds should send a chat message with the confirmed world, target coordinates, and rotation.

Cancel:

```text
/hb cancel
```

Move a pending visual preview before confirmation:

```text
/hb move forward 3
/hb move up 1
/hb move left 2
```

Rotate and move can be combined:

```text
/hb paste starter_house --preview
/hb rotate 90
/hb move forward 3
/hb confirm
```

## Air And Entity Controls

Ignore schematic air:

```text
/hb paste starter_house --ignore-air
```

Paste schematic air:

```text
/hb paste starter_house --paste-air
```

Paste without entities:

```text
/hb paste starter_house --no-entities
```

Paste with entities:

```text
/hb paste starter_house --entities
```

## Import Schematics

Admins place `.schem` files here:

```text
plugins/HalalBuilds/imports/
```

Then run:

```text
/hb import castle.schem castle
```

The imported build becomes a normal saved HalalBuilds build.

## Export Schematics

Export a saved build:

```text
/hb export castle
```

The exported file is written to:

```text
plugins/HalalBuilds/exports/castle.schem
```

## Undo

Undo the latest paste or cut when possible:

```text
/hb undo
```

Undo is best-effort. Very large operations may have practical limits, so important server work should still be tested carefully.

## Delete A Build

```text
/hb delete starter_house
```

This deletes the saved build from HalalBuilds storage. It does not remove already pasted copies from the world.

## Safety Notes

- Always test on a copy of the world first.
- Keep `stop-on-denylisted-blocks` enabled.
- Use `/hb paste <name> --preview` for mountain or large placements.
- Do not import files with untrusted names.
- Keep FAWE updated with the server version.
- Use LuckPerms to limit destructive commands like cut, delete, import, export, and reload.

## Current UI Status

HalalBuilds v1.1.0 has no custom graphical inventory UI.

Current interaction style:

- WorldEdit/FAWE selection wand for selecting regions.
- Commands for actions.
- Chat messages for feedback.
- Tab completion for saved build names.
- Visual particle previews for pending placements.
- Chat preview summaries for risky placements.

Planned future UI options:

- Inventory build browser.
- Clickable build actions.
- Full ghost-block placement previews.
- Optional resource-pack enhanced icons and menu textures.
- Optional web/GitHub documentation screenshots.
