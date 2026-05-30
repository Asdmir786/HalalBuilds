# How To Test HalalBuilds

This guide explains how to check whether HalalBuilds works on a real Paper server.

Use a test server or a copied world first. Do not test destructive commands like `cut`, `delete`, or large smart-foundation pastes on your main world until the smoke test passes.

## 1. Server Setup

Required:

- Paper `1.21.11`
- Java `21`
- FAWE installed
- HalalBuilds jar installed

Copy this jar into the server `plugins/` folder:

```text
build/libs/HalalBuilds-1.1.0.jar
```

Start the server.

## 2. Check Plugin Startup

In game or console, run:

```text
/plugins
```

Expected:

- `HalalBuilds` is listed as enabled.
- `FastAsyncWorldEdit` or FAWE is also enabled.

Check that this folder exists:

```text
plugins/HalalBuilds/
```

Expected folders:

```text
plugins/HalalBuilds/builds/
plugins/HalalBuilds/imports/
plugins/HalalBuilds/exports/
plugins/HalalBuilds/config.yml
```

If HalalBuilds does not load, check the console. The most likely reason is that FAWE is missing or incompatible.

## 3. Basic Command Check

Run:

```text
/hb list
```

Expected:

- If no builds exist yet, it says there are no saved builds.
- No console error appears.

Run:

```text
/hb wand
```

Expected:

- You receive a wooden axe selection wand.

## 4. Save A Small Build

Build or find a small test structure, such as a tiny house.

If you are testing after updating HalalBuilds, save a fresh test build instead of reusing an old schematic from a previous broken test. Old saved files may preserve old behavior.

Use the wand:

- Left-click one corner.
- Right-click the opposite corner.

Select tightly around the build. V1 saves the whole cuboid, including grass, dirt, and air inside the box.

Run:

```text
/hb save test_house
```

Expected:

- Chat says the build was saved.
- This folder exists:

```text
plugins/HalalBuilds/builds/test_house/
```

Expected files:

```text
test_house.schem
metadata.yml
```

Now run:

```text
/hb list
/hb info test_house
```

Expected:

- `test_house` appears in the list.
- Info shows dimensions, created time, world, tags, and notes.

## 5. Paste With Preview

Look at a flat target block where the build should be pasted.

Run:

```text
/hb paste test_house --preview
```

Expected:

- The build is not pasted yet.
- A particle outline appears around the pending placement.
- Chat shows a preview summary.
- The summary includes source, world, target coordinates, dimensions, rotation, paste mode, air behavior, entity behavior, skipped air, clearing count, and foundation count.

Confirm:

```text
/hb confirm
```

Expected:

- The build appears at the previewed target location.
- It does not paste somewhere else if you moved your camera before confirming.

Cancel test:

```text
/hb paste test_house --preview
/hb cancel
```

Expected:

- No paste happens after cancel.
- The particle outline stops refreshing after cancel.

Move test:

```text
/hb paste test_house --preview
/hb move forward 3
/hb move up 1
/hb confirm
```

Expected:

- The preview shifts before confirmation.
- Confirm pastes at the moved target.

## 6. Rotation Test

Use a build with an obvious front side.

First test the separate pending rotation command:

```text
/hb paste test_house --preview
/hb rotate 90
/hb confirm
```

Expected:

- `/hb rotate 90` updates the pending preview.
- The visual outline refreshes after rotation.
- The preview chat now says rotation `90`.
- `/hb confirm` pastes the rotated build at the same previewed target.

Then test inline rotation:

Run:

```text
/hb paste test_house --rotate 90 --preview
/hb confirm
```

Repeat:

```text
/hb paste test_house --rotate 180 --preview
/hb confirm
```

```text
/hb paste test_house --rotate 270 --preview
/hb confirm
```

Expected:

- The build rotates predictably.
- The footprint stays aligned.
- No command errors appear.

## 7. Clipboard Test

Select a small cuboid.

Run:

```text
/hb copy
```

Expected:

- Chat says the selection was copied.

Look at a target block.

Run:

```text
/hb paste clipboard --preview
/hb confirm
```

Expected:

- The copied selection pastes at the target.

## 7A. Air And Entity Flags

Test ignore-air:

```text
/hb paste test_house --ignore-air --preview
/hb confirm
```

Expected:

- Schematic air does not replace existing blocks.

Test paste-air:

```text
/hb paste test_house --paste-air --preview
/hb confirm
```

Expected:

- Schematic air is allowed to replace existing blocks.

Test entities:

```text
/hb save entity_test --entities
/hb paste entity_test --entities --preview
/hb confirm
```

Expected:

- Saved entities paste when the schematic contains supported entity data.

## 8. Cut And Undo Test

Use a disposable test structure.

Select it with the wand.

Run:

```text
/hb cut
```

Expected:

- The selection is copied to your HalalBuilds clipboard.
- The original selected blocks are removed.

Run:

```text
/hb undo
```

Expected:

- The removed blocks are restored when possible.

If undo fails, check console logs and note the exact selection size and command sequence.

## 9. Export Test

Run:

```text
/hb export test_house
```

Expected:

- This file exists:

```text
plugins/HalalBuilds/exports/test_house.schem
```

## 10. Import Test

Place a valid `.schem` file into:

```text
plugins/HalalBuilds/imports/
```

Example:

```text
plugins/HalalBuilds/imports/castle.schem
```

Run:

```text
/hb import castle.schem castle
```

Expected:

- Chat says the file was imported.
- `castle` appears in:

```text
/hb list
```

Paste it:

```text
/hb paste castle --preview
/hb confirm
```

Expected:

- Imported schematic pastes successfully.

Invalid import checks:

```text
/hb import ../server.properties
/hb import C:\temp\castle.schem
/hb import castle.txt
```

Expected:

- These are rejected.
- No file outside `plugins/HalalBuilds/imports/` is read.

## 11. Smart Foundation Test

Find a mountain, slope, cave opening, or uneven area.

Look at the target block.

Run:

```text
/hb paste test_house --mode smart_foundation --preview
```

Expected:

- Preview reports terrain blocks to clear and foundation blocks to place.
- If the operation is large, confirmation is required.

Confirm:

```text
/hb confirm
```

Expected:

- Terrain intersecting the structure is cleared.
- Empty unsupported space under the building footprint is filled with the configured foundation material.
- The result looks stable enough for a v1 smart-foundation paste.

## 12. Denylisted Block Test

Place a denylisted block in the target paste area, such as:

- chest
- barrel
- spawner
- bedrock
- command block

Run:

```text
/hb paste test_house --preview
```

Expected:

- The operation is blocked or reports denylisted blocks.
- The protected/denylisted block is not changed.

## 13. Permission Test

Use a non-op account.

Run:

```text
/hb list
/hb save test_house
/hb paste test_house
```

Expected:

- Commands are denied unless the player has the right permission.

With LuckPerms, admin access:

```text
/lp group admin permission set halalbuilds.admin true
```

Builder example:

```text
/lp group builder permission set halalbuilds.use true
/lp group builder permission set halalbuilds.wand true
/lp group builder permission set halalbuilds.save true
/lp group builder permission set halalbuilds.copy true
/lp group builder permission set halalbuilds.paste true
/lp group builder permission set halalbuilds.preview true
/lp group builder permission set halalbuilds.confirm true
/lp group builder permission set halalbuilds.undo true
```

Expected:

- Builder can use safe build workflow commands.
- Builder cannot use commands they were not granted.

## 14. What Counts As Pass

HalalBuilds v1.1.0 smoke test passes if:

- Plugin loads with FAWE.
- `/hb wand` works.
- Cuboid save works.
- List and info work.
- Preview and confirm paste at the same target.
- Visual preview appears, moves, rotates, and clears.
- Air/entity flags behave as expected.
- Rotation works.
- Clipboard paste works.
- Cut and undo work on a small test structure.
- Export writes a `.schem`.
- Import reads a `.schem`.
- Invalid import paths are rejected.
- Smart foundation works acceptably on uneven terrain.
- Permissions behave correctly.

## 15. What To Report If Something Breaks

Write down:

- Server version.
- Java version.
- FAWE version.
- HalalBuilds jar version.
- Exact command used.
- Exact chat error.
- Console error.
- Whether the build was saved, copied, pasted, imported, or exported.
- Target world and coordinates.
