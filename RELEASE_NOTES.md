# HalalBuilds 1.0.1 Release Notes

HalalBuilds `1.0.1` is the first stable v1 release.

## Fixed In 1.0.1

- Fixed saved/copy clipboard placement so selected cuboids are copied into the normalized schematic origin correctly.
- This fixes a bug where `/hb confirm` could execute through FAWE but appear to paste nothing because the saved schematic content was offset incorrectly.
- Added `/hb rotate <0|90|180|270>` to rotate an existing pending preview before confirmation.
- Improved confirmation feedback so `/hb confirm` reports the world, target coordinates, and rotation used.

Important upgrade note:

- If a build was saved with an older test jar and pastes as air or appears to do nothing, delete that saved build and save it again with `1.0.1`.

## Target

- Paper `1.21.11`
- Java `21`
- FAWE required at runtime
- Sponge `.schem` import/export

## Initial V1 Features

- `/halalbuilds` root command with `/hb` and `/halalbuild` aliases.
- Cuboid selection workflow using WorldEdit/FAWE selections.
- Save selected builds to `plugins/HalalBuilds/builds/`.
- Copy selections to a temporary per-player clipboard.
- Cut selections by copying first, then removing the original.
- Paste saved builds or clipboard builds.
- Paste rotation support for `0`, `90`, `180`, and `270` degrees.
- Smart foundation paste mode.
- Exact paste mode.
- Preview, confirm, and cancel flow for risky or large placements.
- Import `.schem` files from `plugins/HalalBuilds/imports/`.
- Export saved builds to `plugins/HalalBuilds/exports/`.
- List, info, delete, undo, and reload commands.
- Safe name and path validation for stored builds, imports, and exports.
- Metadata files for saved builds.
- Typed configuration loading.
- Runtime FAWE dependency check.
- Unit tests for validation, storage, config, metadata, command parsing, and schematic utilities.

## Known Limits

- V1 is command-based and does not include an inventory GUI.
- Preview is a chat summary, not a particle or block outline preview.
- V1 saves selected cuboids as-is, including grass, dirt, air, and other blocks inside the selected bounds.
- Clean scan/terrain filtering is planned for a later version.
- WorldGuard and HalalBuilds-native structure protection are documented for v2 but not implemented in v1.
- Final confidence requires live Paper + FAWE smoke testing on a real server.

## Release Artifact

Expected jar after building:

```text
build/libs/HalalBuilds-1.0.1.jar
```
