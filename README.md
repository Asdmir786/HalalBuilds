# HalalBuilds

[![Build](https://github.com/Asdmir786/HalalBuilds/actions/workflows/build.yml/badge.svg)](https://github.com/Asdmir786/HalalBuilds/actions/workflows/build.yml)
![Java 21](https://img.shields.io/badge/Java-21-orange)
![Paper 1.21.11](https://img.shields.io/badge/Paper-1.21.11-blue)
![FAWE Required](https://img.shields.io/badge/FAWE-required-green)
![Version](https://img.shields.io/badge/version-1.1.0-brightgreen)

HalalBuilds is a Paper Minecraft Java Edition plugin for admins and builders who want a safer structure workflow on top of FAWE/WorldEdit.

It lets staff save cuboid selections as named builds, copy/cut/paste structures, import/export Sponge `.schem` files, rotate placements, preview risky operations, and paste into uneven terrain with smart foundation support.

**Best for:** Minecraft server owners, Paper admins, builders, survival networks, SMP staff, and creative teams that need reusable structure saves with safer terrain placement.

## Status

Current release: **1.1.0**

HalalBuilds v1.1.0 adds visual particle previews, movable pending placements, paste-air controls, and entity toggles. Inventory GUI, clean scan modes, and WorldGuard/native protection are planned future work.

## Target Stack

| Item | Target |
| --- | --- |
| Minecraft | Java Edition |
| Server | Paper `1.21.11` |
| Java | `21` |
| Build tool | Gradle Kotlin DSL |
| Language | Java |
| Runtime dependency | FAWE |
| Optional future integration | WorldGuard |
| Schematic format | Sponge `.schem` |
| Main class | `com.halalbuilds.HalalBuildsPlugin` |

## Features

- Cuboid selection workflow using WorldEdit/FAWE selections.
- Save selected builds by name.
- Copy selected builds into a temporary player clipboard.
- Cut selected builds by copying first, then removing the original.
- Paste saved builds or clipboard builds.
- Rotate pending previews or paste commands by `0`, `90`, `180`, or `270` degrees.
- Visual particle preview plus chat preview for pending operations.
- Move pending previews up, down, forward, back, left, or right before confirming.
- Ignore-air or paste-air controls per paste.
- Save/paste entity controls per command.
- Smart foundation mode for uneven terrain.
- Exact paste mode for raw schematic placement.
- Import `.schem` files from a controlled imports folder.
- Export saved builds to a controlled exports folder.
- Best-effort undo for the latest paste/cut.
- Safe name and path validation.
- LuckPerms-compatible permission nodes.

## Install

1. Run Paper `1.21.11` with Java `21`.
2. Install FAWE.
3. Download `HalalBuilds-1.1.0.jar` from GitHub Releases.
4. Place `HalalBuilds-1.1.0.jar` in the server `plugins/` folder.
5. Start the server once.
6. Edit `plugins/HalalBuilds/config.yml` if needed.
7. Restart or run:

```text
/hb reload
```

Generated folders:

```text
plugins/HalalBuilds/
  builds/
  imports/
  exports/
  config.yml
```

## Quick Start

Select the full cuboid around a build with the wand:

```text
/hb wand
```

Save it:

```text
/hb save starter_house
```

Preview, rotate, and confirm a paste:

```text
/hb paste starter_house --preview
/hb rotate 90
/hb move forward 3
/hb confirm
```

Use exact mode when you do not want smart foundation blocks:

```text
/hb paste starter_house --mode exact --preview
/hb confirm
```

## Important V1 Behavior

HalalBuilds v1 saves exactly what is inside the selected cuboid.

If the selection is only one block tall, it will save only a flat layer. Select from the bottom corner to the opposite top corner to capture a full building.

If a build was saved with an older test jar and pastes as air or appears to do nothing, delete and re-save it with the current release.

## Commands

Main command:

```text
/halalbuilds
```

Aliases:

```text
/hb
/halalbuild
```

Common commands:

```text
/hb wand
/hb save <name>
/hb copy
/hb cut
/hb paste <name|clipboard>
/hb paste <name|clipboard> --preview
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

See [COMMANDS_AND_PERMISSIONS.md](COMMANDS_AND_PERMISSIONS.md) for the complete command and permission reference.

## Documentation

- [USER_GUIDE.md](USER_GUIDE.md) - in-game usage guide.
- [ADMIN_SETUP.md](ADMIN_SETUP.md) - FAWE, LuckPerms, and staff permission setup.
- [HOW_TO_TEST.md](HOW_TO_TEST.md) - step-by-step smoke test guide.
- [COMMANDS_AND_PERMISSIONS.md](COMMANDS_AND_PERMISSIONS.md) - commands, aliases, permissions, and examples.
- [CONFIG.md](CONFIG.md) - configuration reference.
- [RELEASE_NOTES.md](RELEASE_NOTES.md) - release notes and known limits.
- [GITHUB_RELEASE.md](GITHUB_RELEASE.md) - release title, tag, description, and upload checklist.
- [RELEASE_CHECKLIST.md](RELEASE_CHECKLIST.md) - final release checklist.
- [PROJECT_STATUS.md](PROJECT_STATUS.md) - current project status.
- [ROADMAP.md](ROADMAP.md) - detailed version roadmap and future architecture plan.
- [REQUIREMENTS.md](REQUIREMENTS.md) - product requirements.
- [TECHNICAL_PLAN.md](TECHNICAL_PLAN.md) - architecture and algorithms.
- [TEST_PLAN.md](TEST_PLAN.md) - manual and automated test plan.

## License

HalalBuilds is released under the [MIT License](LICENSE).

## Build

Windows PowerShell:

```powershell
$env:JAVA_HOME = (Resolve-Path '.tools\jdks\jdk-21.0.11+10').Path
$env:PATH = "$env:JAVA_HOME\bin;" + $env:PATH
.\gradlew.bat clean build --no-daemon
```

Release jar:

```text
build/libs/HalalBuilds-1.1.0.jar
```

## Roadmap

Planned future work:

- Inventory GUI build browser.
- Smart placement queue for huge builds.
- Resource-pack enhanced icons.
- Clean scan modes for trimming air and filtering terrain blocks.
- WorldGuard integration.
- HalalBuilds-native protected regions.
