# GitHub Release

Use this content for the v1.1.0 release.

## Tag

```text
v1.1.0
```

## Title

```text
HalalBuilds v1.1.0 - Visual Preview Update
```

## Description

```markdown
# HalalBuilds v1.1.0

Visual preview and paste-control update for HalalBuilds, a Paper plugin for saving, copying, cutting, pasting, importing, and exporting Minecraft builds with FAWE-powered schematic handling.

## Highlights

- Visual particle outlines for pending paste previews
- Move pending previews with `/hb move <direction> <blocks>`
- Rotate pending previews with `/hb rotate <0|90|180|270>`
- Confirm or cancel pending previews cleanly
- Per-paste air controls with `--ignore-air` and `--paste-air`
- Per-save and per-paste entity controls with `--entities` and `--no-entities`
- Preview summaries now include skipped air and entity counts
- New preview and entity config sections
- New `halalbuilds.move` permission

## Requirements

- Paper 1.21.11
- Java 21
- FastAsyncWorldEdit / FAWE installed on the server

## Install

1. Download `HalalBuilds-1.1.0.jar` from this release.
2. Put it in your server `plugins/` folder.
3. Make sure FAWE is also installed.
4. Restart the server.
5. Configure permissions using LuckPerms or your preferred permissions plugin.

## Notes

HalalBuilds uses FAWE/WorldEdit internally as the schematic and block-editing engine, but server staff should use the `/hb` commands for the HalalBuilds workflow. Do not give normal builders broad `worldedit.*` or `fawe.*` permissions unless you also want them to access raw WorldEdit/FAWE commands.

## Known Limits

- Visual previews are particle outlines, not full ghost blocks.
- Selection is cuboid-based through WorldEdit/FAWE.
- WorldGuard/native building protection is planned for a later version.
- Inventory GUI and resource-pack workflows are future features, not part of v1.1.0.
- Million-block smart placement queue is planned for a later version.
```

## Asset To Upload

```text
build/libs/HalalBuilds-1.1.0.jar
```

## Repository Description

```text
Paper plugin for saving, copying, cutting, pasting, importing, and exporting Minecraft builds with FAWE-powered smart foundation placement.
```

## Topics

```text
minecraft
paper
paper-plugin
minecraft-plugin
java
java-21
fawe
worldedit
schematics
schem
building-tools
server-tools
halalbuilds
```
