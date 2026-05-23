# Project Status

HalalBuilds is currently at **v1.0.1 release candidate**.

The plugin has been implemented and built locally. The remaining step before calling it a final public release is a live smoke test on a Paper `1.21.11` server with FAWE installed.

## Current Artifact

Release candidate jar:

```text
build/libs/HalalBuilds-1.0.1.jar
```

## What Has Been Done

- Gradle Kotlin DSL project scaffold.
- Gradle wrapper committed.
- Java `21` target.
- Paper `1.21.11` API target.
- `plugin.yml` metadata.
- Default `config.yml`.
- Runtime FAWE dependency check.
- Plugin data folders:
  - `plugins/HalalBuilds/builds/`
  - `plugins/HalalBuilds/imports/`
  - `plugins/HalalBuilds/exports/`
- Safe build name validation.
- Safe import/export path handling.
- Build metadata YAML files.
- Sponge `.schem` save/load/import/export.
- WorldEdit/FAWE cuboid selection support.
- Per-player temporary clipboard support.
- Pending preview/confirm/cancel operation flow.
- Best-effort undo support.
- Smart foundation planning.
- Exact paste mode.
- Rotation support for `0`, `90`, `180`, and `270` degrees.
- Unit tests for practical non-server logic.
- User guide, release notes, and release checklist.

## Implemented Commands

Command roots:

```text
/halalbuilds
/hb
/halalbuild
```

Implemented v1 commands:

```text
/hb wand
/hb save <name>
/hb copy
/hb cut
/hb paste <name|clipboard>
/hb paste <name|clipboard> --rotate <0|90|180|270>
/hb paste <name|clipboard> --preview
/hb paste <name|clipboard> --mode <smart_foundation|exact>
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

HalalBuilds v1 is command-based.

There is no custom graphical menu yet. Players interact through:

- WorldEdit/FAWE-style cuboid selection.
- `/hb` commands.
- Chat feedback.
- Tab completion.
- Chat preview summaries.

Basic workflow:

```text
/hb wand
select two cuboid corners
/hb save test_house
look at a target block
/hb paste test_house --preview
/hb confirm
```

Preview output should tell the player:

- source build or clipboard
- target world
- target coordinates
- dimensions
- volume
- rotation
- paste mode
- terrain blocks to clear
- foundation blocks to place
- denylisted blocks, if detected

## V1 Selection Behavior

V1 uses cuboid selection only.

The plugin saves what is inside the selected box:

- building blocks
- air
- grass
- dirt
- floors
- decorations
- any other selected blocks

For best results, select tightly around the build.

Clean scan behavior, such as automatically removing grass/dirt or detecting only connected building blocks, is planned for a later version.

## Smart Foundation Expectations

Smart foundation is the default paste mode.

It should:

- analyze the non-air structure footprint
- clear terrain that intersects the structure
- fill empty unsupported space under the footprint
- use the configured foundation material
- stop when denylisted blocks are detected, if configured
- require confirmation for large operations

It is still a v1 first-pass system and must be tested in real terrain before public production use.

## What Is Not In V1

These features are not implemented in v1:

- Inventory GUI.
- Visual particle/block outline preview.
- Resource pack custom icons.
- Clean scan or automatic terrain filtering while saving.
- Connected-building scan.
- WorldGuard integration.
- HalalBuilds-native protected regions.
- Build thumbnails.
- Web panel.

## Next Required Step

Run the live smoke test in:

```text
RELEASE_CHECKLIST.md
```

After that:

- If the smoke test passes, tag/publish `v1.0.1`.
- If the smoke test finds runtime bugs, fix those bugs and rebuild.
