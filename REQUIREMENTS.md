# HalalBuilds Requirements

## Summary

HalalBuilds is a Paper Minecraft Java Edition plugin for admins and builders. It provides a safe structure workflow for selecting, saving, copying, cutting, pasting, importing, exporting, rotating, previewing, and undoing custom builds.

The plugin must prioritize clean terrain placement, especially when placing buildings into mountains, slopes, caves, water, or uneven land.

## Target Stack

| Requirement | Value |
| --- | --- |
| Minecraft edition | Java Edition |
| Server platform | Paper `1.21.11` |
| Java version | `21` |
| Build system | Gradle Kotlin DSL |
| Language | Java |
| Package name | `com.halalbuilds` |
| Main class | `com.halalbuilds.HalalBuildsPlugin` |
| Plugin name | `HalalBuilds` |
| Runtime dependency | FAWE |
| Optional integration | WorldGuard |
| Schematic format | Sponge `.schem` |

## Functional Requirements

### Selection And Scanning

- Players with permission must be able to obtain a selection wand.
- The plugin must read a player's selected region through WorldEdit or FAWE APIs.
- The plugin must validate that selections are complete before copy, cut, or save operations.
- The plugin must reject selections that exceed the configured maximum volume.
- The plugin should report selection dimensions and volume when useful.
- V1 selection must be cuboid-based.
- V1 must preserve the selected cuboid content as-is, including grass, dirt, air, decorations, and other blocks inside the selected bounds.
- Automatic clean scanning, terrain filtering, and connected-building detection are planned future enhancements, not v1 requirements.

### Save Builds

- Players with permission must be able to save a selected region by name.
- Save names must be validated to prevent path traversal and unsafe filenames.
- Saved builds must be stored under `plugins/HalalBuilds/builds/`.
- Each saved build must include:
  - Sponge `.schem` file.
  - Metadata file.
- Existing saved builds may only be overwritten if configuration allows it or the player has an appropriate admin permission.

### Copy

- Players with permission must be able to copy the selected region into a temporary player clipboard.
- Clipboard data should include the schematic content and origin offset so paste alignment is predictable.
- Clipboard data should remain in memory and should not be treated as a saved build unless explicitly saved.

### Cut

- Players with permission must be able to cut a selected region.
- Cut must copy first.
- The original region should only be removed after a successful copy.
- The removal operation should be undoable where practical.
- Cut must respect protected block denylist behavior.

### Paste

- Players with permission must be able to paste:
  - A saved build by name.
  - The temporary player clipboard.
- Pasting must use the player's target location unless a future command option provides explicit coordinates.
- Pasting must support rotation values:
  - `0`
  - `90`
  - `180`
  - `270`
- Pasting must support:
  - Default smart foundation mode.
  - Exact mode for raw schematic placement.
- Pasting must obey configured air block behavior.
- Pasting must reject operations in disabled worlds.
- Pasting must require preview or confirmation for large or risky terrain changes.

### Preview And Confirmation

- High-risk or large operations must create a pending operation instead of immediately applying.
- Players must be able to confirm pending operations.
- Players must be able to cancel pending operations.
- Pending operations should expire after a configurable timeout in the implementation phase.
- Preview should communicate:
  - Build name or clipboard source.
  - Dimensions and volume.
  - Target world and location.
  - Rotation.
  - Paste mode.
  - Estimated terrain blocks cleared.
  - Estimated foundation blocks placed.
  - Any detected denylisted blocks.

### Undo

- Players with permission must be able to undo their latest paste or cut operation when possible.
- Undo should be best-effort and clearly report if an operation cannot be undone.
- Undo data should be bounded to avoid excessive memory usage.

### Import

- Server admins place import files under `plugins/HalalBuilds/imports/`.
- Players with permission must be able to import an allowed schematic file.
- Imports must only read from the controlled import folder.
- Import filenames must be validated to prevent path traversal.
- Allowed file extensions must be configurable.
- Imported builds should be saved into the normal `builds/` folder with metadata.

### Export

- Players with permission must be able to export saved builds.
- Exports must write to `plugins/HalalBuilds/exports/`.
- Export filenames must be validated.
- Exported files should use Sponge `.schem`.

### List, Info, Delete, Reload

- Players with permission must be able to list saved builds.
- Players with permission must be able to view metadata for a saved build.
- Players with permission must be able to delete saved builds.
- Admins must be able to reload configuration safely.

### Planned V2 Protection

HalalBuilds should include a protection system in a later version after the core structure workflow is stable.

Protection goals:

- Protect selected cuboid areas.
- Protect pasted structure bounds.
- Optionally auto-protect pasted builds.
- Prevent unauthorized breaking or placing inside protected structures.
- Track protection owner, members, region name, world, bounds, created time, and source build where practical.
- Allow admins to remove protection.
- Allow admins to inspect protection information.
- Support HalalBuilds-native protection even when no external protection plugin is installed.
- Support optional WorldGuard integration when WorldGuard is installed.

WorldGuard integration goals:

- Respect existing WorldGuard regions during paste, cut, terrain clearing, and foundation operations.
- Prevent HalalBuilds from bypassing region protection.
- Optionally create a WorldGuard cuboid region around a pasted build.
- Assign the player or configured admin group as owner/member where appropriate.
- Apply configurable default WorldGuard flags for protected structures.
- Keep WorldGuard optional so servers without it can still use HalalBuilds.

## Terrain Placement Requirements

### Default Mode

The default paste mode must be `smart_foundation`.

### Smart Foundation Behavior

When placing a structure into mountain or uneven terrain, the plugin should:

- Detect the structure footprint.
- Determine the occupied schematic bounds.
- Clear blocks that intersect the structure.
- Preserve air inside the structure if configured.
- Fill empty space under the building with configured foundation material.
- Avoid denylisted blocks such as:
  - Chests.
  - Spawners.
  - Bedrock.
  - Command blocks.
  - Protected blocks configured by the server owner.
- Require confirmation for large terrain changes.

### Exact Paste Mode

Exact mode must exist for admins who want raw schematic placement without smart terrain clearing or foundation generation.

## Storage Requirements

### Build Directory

```text
plugins/HalalBuilds/builds/
```

Each saved build should use a dedicated folder:

```text
plugins/HalalBuilds/builds/<build-name>/
  <build-name>.schem
  metadata.yml
```

### Import Directory

```text
plugins/HalalBuilds/imports/
```

### Export Directory

```text
plugins/HalalBuilds/exports/
```

## Metadata Requirements

Each saved build must include metadata with:

- Name.
- Creator UUID.
- Created time.
- Dimensions.
- Original world.
- Origin offset.
- Tags.
- Notes.

Recommended metadata format:

```yaml
name: example_house
creator_uuid: 00000000-0000-0000-0000-000000000000
created_at: "2026-05-19T00:00:00Z"
dimensions:
  width: 12
  height: 8
  length: 14
original_world: world
origin_offset:
  x: 0
  y: 0
  z: 0
tags: []
notes: ""
```

## Command Requirements

Required commands:

- `/halalbuilds wand`
- `/halalbuilds save <name>`
- `/halalbuilds copy`
- `/halalbuilds cut`
- `/halalbuilds paste <name|clipboard>`
- `/halalbuilds paste <name|clipboard> --rotate <0|90|180|270>`
- `/halalbuilds paste <name|clipboard> --preview`
- `/halalbuilds confirm`
- `/halalbuilds cancel`
- `/halalbuilds import <filename> [name]`
- `/halalbuilds export <name>`
- `/halalbuilds list`
- `/halalbuilds info <name>`
- `/halalbuilds delete <name>`
- `/halalbuilds undo`
- `/halalbuilds reload`

Planned v2 protection commands:

- `/halalbuilds protect <regionName>`
- `/halalbuilds protect <regionName> --worldguard`
- `/halalbuilds unprotect <regionName>`
- `/halalbuilds protection info <regionName>`
- `/halalbuilds protection list`
- `/halalbuilds paste <name|clipboard> --protect`
- `/halalbuilds paste <name|clipboard> --protect <regionName>`

Aliases:

- `/hb`
- `/halalbuild`

## Permission Requirements

Required permissions:

- `halalbuilds.use`
- `halalbuilds.wand`
- `halalbuilds.save`
- `halalbuilds.copy`
- `halalbuilds.cut`
- `halalbuilds.paste`
- `halalbuilds.preview`
- `halalbuilds.confirm`
- `halalbuilds.import`
- `halalbuilds.export`
- `halalbuilds.delete`
- `halalbuilds.undo`
- `halalbuilds.reload`
- `halalbuilds.protect`
- `halalbuilds.unprotect`
- `halalbuilds.protection.info`
- `halalbuilds.protection.list`
- `halalbuilds.protection.admin`
- `halalbuilds.admin`

## Configuration Requirements

The configuration must include:

- Max selection volume.
- Max paste volume before preview is required.
- Default paste mode.
- Foundation block material.
- Whether to paste air blocks.
- Whether to clear terrain above the footprint.
- Protected block denylist.
- Planned v2 protection settings.
- Optional WorldGuard integration settings.
- Allowed import file extensions.
- Whether players can overwrite existing saved builds.
- FAWE dependency check on startup.
- Per-world enable/disable list.

## Technical Requirements

- Use Paper API for lifecycle, commands, permissions, configuration, logging, and player interaction.
- Use FAWE and WorldEdit APIs for selections, clipboards, schematic reading/writing, and large block operations.
- Use Sponge `.schem` as the primary schematic format.
- Store HalalBuilds-specific data in plugin-owned metadata files.
- Keep large paste operations async through FAWE where safe.
- Validate all import/export/build names.
- Avoid direct access outside plugin-owned storage folders.
- Report useful failure messages to players and detailed errors to console logs.

## Planned Post-V1 Scan Improvements

Future versions may add advanced scan options:

- Trim empty outer air from saved builds.
- Ignore or remove common terrain blocks such as grass, dirt, stone, sand, or gravel from saved schematics.
- Save only connected structure blocks from a cuboid selection.
- Let admins choose block filters per save operation.
- Preview which blocks will be included before saving.
- Store scan mode in metadata.
