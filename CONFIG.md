# Configuration Plan

HalalBuilds should generate `plugins/HalalBuilds/config.yml` on first startup.

The configuration should be conservative by default. Large operations should require preview, protected blocks should be denylisted, and FAWE should be required.

## Planned `config.yml`

```yaml
# HalalBuilds configuration

limits:
  max-selection-volume: 250000
  max-paste-volume-before-preview: 75000
  max-terrain-changes-before-confirm: 10000
  max-foundation-depth: 64

paste:
  default-mode: smart_foundation
  paste-air-blocks: false
  clear-terrain-above-footprint: true
  require-preview-for-large-pastes: true
  pending-operation-timeout-seconds: 120

preview:
  enabled: true
  refresh-seconds: 2
  particle: END_ROD
  show-corners: true
  show-height-pillars: true
  show-facing-arrow: true

entities:
  save-entities: true
  paste-entities: true

foundation:
  material: STONE_BRICKS
  fill-under-footprint: true
  only-fill-under-non-air-blocks: true

protection:
  stop-on-denylisted-blocks: true
  denylisted-blocks:
    - CHEST
    - TRAPPED_CHEST
    - BARREL
    - SPAWNER
    - BEDROCK
    - COMMAND_BLOCK
    - CHAIN_COMMAND_BLOCK
    - REPEATING_COMMAND_BLOCK
    - STRUCTURE_BLOCK
    - JIGSAW
    - END_PORTAL_FRAME

structure-protection:
  enabled: true
  backend: native
  auto-protect-pastes: false
  protect-selection-command-enabled: true
  region-name-format: "hb_{player}_{name}"
  block-break: true
  block-place: true
  block-explosions: true
  pistons: true
  fire-spread: true
  allow-owner-edit: true
  allow-members-edit: true

worldguard:
  enabled: true
  respect-existing-regions: true
  allow-worldguard-region-creation: true
  prefer-worldguard-for-protected-pastes: false
  region-id-format: "hb_{player}_{name}"
  default-flags:
    block-break: deny
    block-place: deny
    creeper-explosion: deny
    other-explosion: deny
    fire-spread: deny

storage:
  allow-overwrite: false
  allowed-import-extensions:
    - .schem

dependency-check:
  require-fawe: true
  disable-plugin-if-missing: true

worlds:
  mode: allow_all
  enabled:
    - world
    - world_nether
    - world_the_end
  disabled: []

messages:
  prefix: "&a[HalalBuilds]&r "
```

## Settings

### `limits.max-selection-volume`

Maximum number of blocks allowed in a selection.

Purpose:

- Prevent accidental huge saves.
- Protect server memory and disk usage.

Recommended default:

```yaml
max-selection-volume: 250000
```

### `limits.max-paste-volume-before-preview`

Maximum schematic volume that can be pasted without preview.

Recommended default:

```yaml
max-paste-volume-before-preview: 75000
```

### `limits.max-terrain-changes-before-confirm`

Maximum estimated terrain clearing and foundation changes before confirmation is required.

Recommended default:

```yaml
max-terrain-changes-before-confirm: 10000
```

### `limits.max-foundation-depth`

Maximum depth that smart foundation mode may fill below a building footprint.

Recommended default:

```yaml
max-foundation-depth: 64
```

### `paste.default-mode`

Default paste mode.

Allowed values:

- `smart_foundation`
- `exact`

Recommended default:

```yaml
default-mode: smart_foundation
```

### `paste.paste-air-blocks`

Whether schematic air blocks should be pasted.

When `true`, interiors can be cleared correctly.

When `false`, air is ignored except for terrain clearing performed by smart foundation logic.

Recommended default:

```yaml
paste-air-blocks: false
```

### `preview.enabled`

Whether HalalBuilds should show particle outlines for pending paste previews.

Recommended default:

```yaml
enabled: true
```

### `preview.refresh-seconds`

How often the particle preview is redrawn for the player.

Recommended default:

```yaml
refresh-seconds: 2
```

### `preview.particle`

Particle used for the visual paste outline.

Recommended default:

```yaml
particle: END_ROD
```

### `preview.show-corners`

Whether preview corners should be emphasized with extra particles.

### `preview.show-height-pillars`

Whether the preview should draw vertical corner pillars up to the build height.

### `preview.show-facing-arrow`

Whether the preview should draw a direction arrow based on rotation.

### `entities.save-entities`

Default entity behavior when saving builds.

Recommended default:

```yaml
save-entities: true
```

### `entities.paste-entities`

Default entity behavior when pasting builds.

Recommended default:

```yaml
paste-entities: true
```

### `paste.clear-terrain-above-footprint`

Whether smart foundation mode should clear terrain that intersects or hangs into the structure footprint.

Recommended default:

```yaml
clear-terrain-above-footprint: true
```

### `paste.require-preview-for-large-pastes`

Whether large pastes must become pending operations before execution.

Recommended default:

```yaml
require-preview-for-large-pastes: true
```

### `paste.pending-operation-timeout-seconds`

How long a pending preview or confirmation remains valid.

Recommended default:

```yaml
pending-operation-timeout-seconds: 120
```

### `foundation.material`

Material used to fill empty space under structures.

Recommended default:

```yaml
material: STONE_BRICKS
```

### `foundation.fill-under-footprint`

Whether smart foundation mode should generate foundation blocks.

Recommended default:

```yaml
fill-under-footprint: true
```

### `foundation.only-fill-under-non-air-blocks`

Whether foundation should only be created under footprint columns that contain non-air schematic blocks.

Recommended default:

```yaml
only-fill-under-non-air-blocks: true
```

### `protection.stop-on-denylisted-blocks`

Whether operations should stop when denylisted blocks are detected.

Recommended default:

```yaml
stop-on-denylisted-blocks: true
```

### `protection.denylisted-blocks`

Material names that HalalBuilds should avoid changing.

Default list:

```yaml
denylisted-blocks:
  - CHEST
  - TRAPPED_CHEST
  - BARREL
  - SPAWNER
  - BEDROCK
  - COMMAND_BLOCK
  - CHAIN_COMMAND_BLOCK
  - REPEATING_COMMAND_BLOCK
  - STRUCTURE_BLOCK
  - JIGSAW
  - END_PORTAL_FRAME
```

### `structure-protection.enabled`

Planned v2 setting. Enables HalalBuilds-native structure protection.

Recommended default:

```yaml
enabled: true
```

### `structure-protection.backend`

Planned v2 setting. Selects the default protection backend.

Allowed values:

- `native`
- `worldguard`

Recommended default:

```yaml
backend: native
```

If `worldguard` is selected but WorldGuard is not installed, HalalBuilds should fall back to native protection or reject protection creation according to final implementation rules.

### `structure-protection.auto-protect-pastes`

Planned v2 setting. Automatically protects every pasted build.

Recommended default:

```yaml
auto-protect-pastes: false
```

### `structure-protection.protect-selection-command-enabled`

Planned v2 setting. Allows admins to protect a selected cuboid area with `/hb protect <regionName>`.

Recommended default:

```yaml
protect-selection-command-enabled: true
```

### `structure-protection.region-name-format`

Planned v2 setting. Format for generated native protection region names.

Supported placeholders should include:

- `{player}`
- `{uuid}`
- `{name}`
- `{world}`

Recommended default:

```yaml
region-name-format: "hb_{player}_{name}"
```

### `structure-protection.block-break`

Planned v2 setting. Blocks unauthorized block breaking inside native protected regions.

### `structure-protection.block-place`

Planned v2 setting. Blocks unauthorized block placing inside native protected regions.

### `structure-protection.block-explosions`

Planned v2 setting. Blocks explosion damage inside native protected regions.

### `structure-protection.pistons`

Planned v2 setting. Blocks piston movement into or out of native protected regions.

### `structure-protection.fire-spread`

Planned v2 setting. Blocks fire spread inside native protected regions.

### `structure-protection.allow-owner-edit`

Planned v2 setting. Allows the protection owner to edit their protected structure.

### `structure-protection.allow-members-edit`

Planned v2 setting. Allows protection members to edit the protected structure.

### `worldguard.enabled`

Planned v2 setting. Enables optional WorldGuard integration when WorldGuard is installed.

Recommended default:

```yaml
enabled: true
```

### `worldguard.respect-existing-regions`

Planned v2 setting. Makes HalalBuilds check existing WorldGuard regions before paste, cut, terrain clearing, and foundation operations.

Recommended default:

```yaml
respect-existing-regions: true
```

### `worldguard.allow-worldguard-region-creation`

Planned v2 setting. Allows HalalBuilds to create WorldGuard regions around protected structures.

Recommended default:

```yaml
allow-worldguard-region-creation: true
```

### `worldguard.prefer-worldguard-for-protected-pastes`

Planned v2 setting. Uses WorldGuard as the default backend for protected pasted builds when available.

Recommended default:

```yaml
prefer-worldguard-for-protected-pastes: false
```

### `worldguard.region-id-format`

Planned v2 setting. Format for generated WorldGuard region IDs.

Recommended default:

```yaml
region-id-format: "hb_{player}_{name}"
```

### `worldguard.default-flags`

Planned v2 setting. WorldGuard flags applied to plugin-created regions.

Recommended defaults:

```yaml
default-flags:
  block-break: deny
  block-place: deny
  creeper-explosion: deny
  other-explosion: deny
  fire-spread: deny
```

### `storage.allow-overwrite`

Whether players can overwrite existing saved builds.

Recommended default:

```yaml
allow-overwrite: false
```

### `storage.allowed-import-extensions`

Allowed import file extensions.

Recommended default:

```yaml
allowed-import-extensions:
  - .schem
```

### `dependency-check.require-fawe`

Whether FAWE is required.

Recommended default:

```yaml
require-fawe: true
```

### `dependency-check.disable-plugin-if-missing`

Whether HalalBuilds should disable itself if FAWE is missing.

Recommended default:

```yaml
disable-plugin-if-missing: true
```

### `worlds.mode`

Controls world enablement.

Allowed values:

- `allow_all`
- `allow_list`
- `deny_list`

Recommended default:

```yaml
mode: allow_all
```

### `worlds.enabled`

Worlds where HalalBuilds is allowed when `mode` is `allow_list`.

### `worlds.disabled`

Worlds where HalalBuilds is blocked when `mode` is `deny_list`.

### `messages.prefix`

Prefix used for chat messages.

Recommended default:

```yaml
prefix: "&a[HalalBuilds]&r "
```
