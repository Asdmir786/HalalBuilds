# Commands And Permissions

## Command Roots

Primary command:

```text
/halalbuilds
```

Aliases:

```text
/hb
/halalbuild
```

All player-facing commands should require `halalbuilds.use` unless the command is console-only or explicitly admin-only.

## Commands

| Command | Permission | Description |
| --- | --- | --- |
| `/halalbuilds wand` | `halalbuilds.wand` | Gives the player a selection wand or activates the selection tool. |
| `/halalbuilds save <name> [--entities\|--no-entities]` | `halalbuilds.save` | Saves the current WorldEdit/FAWE selection as a named build. |
| `/halalbuilds copy` | `halalbuilds.copy` | Copies the current selection into the player's temporary clipboard. |
| `/halalbuilds cut` | `halalbuilds.cut` | Copies the current selection, then safely removes the original blocks. |
| `/halalbuilds paste <name\|clipboard>` | `halalbuilds.paste` | Pastes a saved build or the player's clipboard at the target location. |
| `/halalbuilds paste <name\|clipboard> --rotate <0\|90\|180\|270>` | `halalbuilds.paste` | Pastes with rotation. |
| `/halalbuilds paste <name\|clipboard> --preview` | `halalbuilds.preview` | Creates a preview or pending operation before placement. |
| `/halalbuilds paste <name\|clipboard> --ignore-air` | `halalbuilds.paste` | Pastes while skipping schematic air blocks. |
| `/halalbuilds paste <name\|clipboard> --paste-air` | `halalbuilds.paste` | Pastes schematic air blocks too. |
| `/halalbuilds paste <name\|clipboard> --entities` | `halalbuilds.paste` | Pastes saved schematic entities when present. |
| `/halalbuilds paste <name\|clipboard> --no-entities` | `halalbuilds.paste` | Skips saved schematic entities. |
| `/halalbuilds rotate <0\|90\|180\|270>` | `halalbuilds.paste` | Changes the rotation of the player's pending preview. |
| `/halalbuilds move <up\|down\|forward\|back\|left\|right> <blocks>` | `halalbuilds.move` | Moves the player's pending preview before confirmation. |
| `/halalbuilds confirm` | `halalbuilds.confirm` | Confirms the player's pending operation. |
| `/halalbuilds cancel` | `halalbuilds.confirm` | Cancels the player's pending operation. |
| `/halalbuilds import <filename> [name]` | `halalbuilds.import` | Imports a schematic from the controlled imports folder. |
| `/halalbuilds export <name>` | `halalbuilds.export` | Exports a saved build to the controlled exports folder. |
| `/halalbuilds list` | `halalbuilds.use` | Lists saved builds. |
| `/halalbuilds info <name>` | `halalbuilds.use` | Shows metadata for a saved build. |
| `/halalbuilds delete <name>` | `halalbuilds.delete` | Deletes a saved build. |
| `/halalbuilds undo` | `halalbuilds.undo` | Undoes the latest paste or cut operation when possible. |
| `/halalbuilds reload` | `halalbuilds.reload` | Reloads plugin configuration. |

## Planned V2 Protection Commands

These commands are planned for the protection phase. They are documented now so the architecture can leave room for them.

| Command | Permission | Description |
| --- | --- | --- |
| `/halalbuilds protect <regionName>` | `halalbuilds.protect` | Protects the player's current cuboid selection using HalalBuilds-native protection by default. |
| `/halalbuilds protect <regionName> --worldguard` | `halalbuilds.protect` | Creates a WorldGuard region when WorldGuard is installed and integration is enabled. |
| `/halalbuilds unprotect <regionName>` | `halalbuilds.unprotect` | Removes a HalalBuilds protection region or plugin-owned WorldGuard region. |
| `/halalbuilds protection info <regionName>` | `halalbuilds.protection.info` | Shows owner, members, bounds, world, source build, and protection backend. |
| `/halalbuilds protection list` | `halalbuilds.protection.list` | Lists protected structures visible to the player. |
| `/halalbuilds paste <name\|clipboard> --protect` | `halalbuilds.protect` | Pastes a build and protects the pasted bounds. |
| `/halalbuilds paste <name\|clipboard> --protect <regionName>` | `halalbuilds.protect` | Pastes a build and protects it with a specific region name. |

## Examples

### Get A Wand

```text
/hb wand
```

### Save A Selected Build

```text
/hb save starter_house
```

### Copy And Paste A Selection

```text
/hb copy
/hb paste clipboard
```

### Cut And Paste A Selection

```text
/hb cut
/hb paste clipboard
```

### Paste A Saved Build

```text
/hb paste starter_house
```

### Paste With Rotation

```text
/hb paste starter_house --rotate 90
```

### Rotate A Pending Preview

```text
/hb paste starter_house --preview
/hb rotate 90
/hb move forward 3
/hb confirm
```

### Preview Before Pasting

```text
/hb paste starter_house --rotate 180 --preview
/hb confirm
```

### Cancel A Pending Operation

```text
/hb cancel
```

### Import A Schematic

Admin places:

```text
plugins/HalalBuilds/imports/castle.schem
```

Player runs:

```text
/hb import castle.schem castle
```

### Export A Build

```text
/hb export castle
```

Expected output path:

```text
plugins/HalalBuilds/exports/castle.schem
```

### View Build Info

```text
/hb info castle
```

### Delete A Build

```text
/hb delete castle
```

### Undo

```text
/hb undo
```

### Planned V2: Protect A Selected Area

```text
/hb protect spawn_house
```

### Planned V2: Paste And Protect

```text
/hb paste starter_house --protect
```

### Planned V2: Create A WorldGuard Region

```text
/hb protect starter_house_region --worldguard
```

## Permission Nodes

| Permission | Default Recommendation | Description |
| --- | --- | --- |
| `halalbuilds.use` | op | Base permission for using HalalBuilds commands. |
| `halalbuilds.wand` | op | Allows wand access. |
| `halalbuilds.save` | op | Allows saving selected builds. |
| `halalbuilds.copy` | op | Allows copying selected builds. |
| `halalbuilds.cut` | op | Allows cutting selected builds. |
| `halalbuilds.paste` | op | Allows pasting saved builds and clipboard builds. |
| `halalbuilds.move` | op | Allows moving pending paste previews. |
| `halalbuilds.preview` | op | Allows previewing placements. |
| `halalbuilds.confirm` | op | Allows confirming or canceling pending operations. |
| `halalbuilds.import` | op | Allows importing files from the imports folder. |
| `halalbuilds.export` | op | Allows exporting saved builds. |
| `halalbuilds.delete` | op | Allows deleting saved builds. |
| `halalbuilds.undo` | op | Allows undoing latest paste or cut operation. |
| `halalbuilds.reload` | op | Allows reloading configuration. |
| `halalbuilds.protect` | op | Allows protecting selected areas or pasted builds. |
| `halalbuilds.unprotect` | op | Allows removing protection from structures. |
| `halalbuilds.protection.info` | op | Allows viewing protection metadata. |
| `halalbuilds.protection.list` | op | Allows listing protected structures. |
| `halalbuilds.protection.admin` | op | Allows managing all HalalBuilds protection regions. |
| `halalbuilds.admin` | op | Grants full administrative access. |

## Suggested Permission Inheritance

`halalbuilds.admin` should include:

- `halalbuilds.use`
- `halalbuilds.wand`
- `halalbuilds.save`
- `halalbuilds.copy`
- `halalbuilds.cut`
- `halalbuilds.paste`
- `halalbuilds.move`
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

## Command Behavior Rules

- Commands that require a player location must reject console senders.
- Commands that use selections must reject incomplete selections.
- Commands that modify blocks must reject disabled worlds.
- Paste operations larger than the configured threshold must require preview and confirmation.
- Paste and cut operations must respect HalalBuilds-native protections.
- If WorldGuard is installed and integration is enabled, paste and cut operations must respect WorldGuard regions.
- Protection commands must validate that region names are safe and unique.
- Commands must return clear failure messages instead of silently failing.
- Import and export commands must never read or write outside plugin-owned folders.
