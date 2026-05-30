# Project Status

HalalBuilds is currently at **v1.1.0 release candidate**.

The plugin has been upgraded from the v1.0.1 command workflow to include visual particle previews, movable pending placements, air controls, and entity toggles.

## Current Artifact

Release candidate jar:

```text
build/libs/HalalBuilds-1.1.0.jar
```

## What Has Been Done

- Gradle Kotlin DSL project scaffold.
- Java `21` target.
- Paper `1.21.11` API target.
- Runtime FAWE dependency check.
- Sponge `.schem` save/load/import/export.
- WorldEdit/FAWE cuboid selection support.
- Per-player temporary clipboard support.
- Pending preview/confirm/cancel operation flow.
- Visual particle previews for pending placements.
- Pending preview movement with `/hb move`.
- Pending preview rotation with `/hb rotate`.
- Paste-air controls with `--ignore-air` and `--paste-air`.
- Entity save/paste controls with `--entities` and `--no-entities`.
- Best-effort undo support.
- Smart foundation planning.
- Exact paste mode.
- Unit tests for practical non-server logic.
- User guide, release notes, and release checklist.

## Implemented Commands

```text
/hb wand
/hb save <name> [--entities|--no-entities]
/hb copy
/hb cut
/hb paste <name|clipboard>
/hb paste <name|clipboard> --rotate <0|90|180|270>
/hb paste <name|clipboard> --preview
/hb paste <name|clipboard> --mode <smart_foundation|exact>
/hb paste <name|clipboard> --ignore-air
/hb paste <name|clipboard> --paste-air
/hb paste <name|clipboard> --entities
/hb paste <name|clipboard> --no-entities
/hb rotate <0|90|180|270>
/hb move <up|down|forward|back|left|right> <blocks>
/hb confirm
/hb cancel
/hb import <filename> [name]
/hb export <name>
/hb list
/hb info <name>
/hb delete <name>
/hb undo
/hb reload
```

## What To Expect In Game

Basic workflow:

```text
/hb wand
select two cuboid corners
/hb save test_house
look at a target block
/hb paste test_house --preview
/hb rotate 90
/hb move forward 3
/hb confirm
```

Preview output should show:

- a particle outline in the world
- source build or clipboard
- target world and coordinates
- dimensions and volume
- rotation and paste mode
- air and entity behavior
- skipped air count
- terrain/foundation counts
- denylisted blocks, if detected

## Not In 1.1.0

- Inventory GUI.
- Full ghost-block visual preview.
- Resource pack custom icons.
- Clean scan or automatic terrain filtering while saving.
- Connected-building scan.
- WorldGuard integration.
- HalalBuilds-native protected regions.
- Million-block smart placement queue.

## Next Required Step

Run the live smoke test in:

```text
RELEASE_CHECKLIST.md
```

After that:

- If the smoke test passes, tag/publish `v1.1.0`.
- If the smoke test finds runtime bugs, fix those bugs and rebuild.
