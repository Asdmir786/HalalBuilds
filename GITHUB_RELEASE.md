# GitHub Release

Use this content for the first public release.

## Tag

```text
v1.0.1
```

## Title

```text
HalalBuilds v1.0.1 - First Stable Release
```

## Description

```markdown
# HalalBuilds v1.0.1

First stable release of HalalBuilds, a Paper plugin for saving, copying, cutting, pasting, importing, and exporting Minecraft builds with FAWE-powered schematic handling.

## Highlights

- Save selected WorldEdit/FAWE cuboid regions as named builds
- Copy, cut, and paste builds through `/hb`
- Paste saved builds or player clipboard builds
- Rotate pending pastes with `/hb rotate <0|90|180|270>`
- Preview risky or large pastes before confirming
- Confirm or cancel pending operations
- Smart foundation placement for uneven terrain
- Exact paste mode for raw schematic placement
- Export saved builds to Sponge `.schem`
- Import `.schem` files from the plugin imports folder
- Undo latest paste/cut operation when possible
- Safe file/name validation to prevent path traversal
- Configurable paste limits, denylisted blocks, foundation material, worlds, and import extensions
- LuckPerms-friendly permission nodes
- Runtime FAWE dependency check

## Requirements

- Paper 1.21.11
- Java 21
- FastAsyncWorldEdit / FAWE installed on the server

## Install

1. Download `HalalBuilds-1.0.1.jar` from this release.
2. Put it in your server `plugins/` folder.
3. Make sure FAWE is also installed.
4. Restart the server.
5. Configure permissions using LuckPerms or your preferred permissions plugin.

## Notes

HalalBuilds uses FAWE/WorldEdit internally as the schematic and block-editing engine, but server staff should use the `/hb` commands for the HalalBuilds workflow. Do not give normal builders broad `worldedit.*` or `fawe.*` permissions unless you also want them to access raw WorldEdit/FAWE commands.

## Known Limits

- Preview is currently chat-based, not a full visual hologram or outline preview.
- Selection is cuboid-based through WorldEdit/FAWE.
- WorldGuard/native building protection is planned for a later version.
- Inventory GUI and resource-pack workflows are future features, not part of v1.0.1.
```

## Asset To Upload

```text
build/libs/HalalBuilds-1.0.1.jar
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
