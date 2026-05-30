# HalalBuilds 1.1.0 Release Notes

HalalBuilds `1.1.0` is the visual preview and paste-control update.

## Added In 1.1.0

- Visual particle previews for pending paste operations.
- `/hb move <up|down|forward|back|left|right> <blocks>` to shift a pending preview before confirmation.
- `/hb rotate <0|90|180|270>` now refreshes the visual preview.
- `/hb cancel` and `/hb confirm` now clear the active visual preview.
- Paste air controls:
  - `/hb paste <name|clipboard> --ignore-air`
  - `/hb paste <name|clipboard> --paste-air`
- Entity controls:
  - `/hb save <name> --entities`
  - `/hb save <name> --no-entities`
  - `/hb paste <name|clipboard> --entities`
  - `/hb paste <name|clipboard> --no-entities`
- Preview summaries now include skipped air and entity counts.
- New `preview` config section for particle preview behavior.
- New `entities` config section for default save/paste entity behavior.
- New `halalbuilds.move` permission.

## Changed In 1.1.0

- Default `paste.paste-air-blocks` is now `false`.
- Normal pastes skip schematic air unless a player uses `--paste-air` or config changes the default.
- Saved/copy clipboard workflows use the configured entity-save default.

## Target

- Paper `1.21.11`
- Java `21`
- FAWE required at runtime
- Sponge `.schem` import/export

## Known Limits

- Visual previews are particle outlines, not full ghost blocks.
- Inventory GUI is not included yet.
- WorldGuard and HalalBuilds-native structure protection are planned future work.
- Million-block smart placement queue is planned for a later version.
- Existing builds saved without entities cannot paste entities retroactively; re-save with entities enabled if needed.

## Release Artifact

Expected jar after building:

```text
build/libs/HalalBuilds-1.1.0.jar
```
