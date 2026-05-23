# Test Plan

This plan covers manual, automated, and semi-automated testing for HalalBuilds.

## Manual Test Environment

Required:

- Minecraft Java Edition client.
- Paper `1.21.11` test server.
- Java `21`.
- FAWE installed.
- WorldGuard installed for optional v2 protection integration tests.
- HalalBuilds installed.
- Operator account or permissions plugin configured.

Recommended test worlds:

- Flat world.
- Normal survival terrain.
- Mountain biome or custom mountain area.
- Area with caves or water below the surface.

## Startup Tests

### Plugin Loads With FAWE

Steps:

1. Install Paper `1.21.11`.
2. Install FAWE.
3. Install HalalBuilds.
4. Start the server.

Expected:

- HalalBuilds enables successfully.
- `plugins/HalalBuilds/` is created.
- `builds/`, `imports/`, and `exports/` folders are created.
- No startup errors appear in console.

### Plugin Handles Missing FAWE

Steps:

1. Remove FAWE.
2. Start the server.

Expected:

- HalalBuilds detects missing FAWE.
- If `disable-plugin-if-missing` is `true`, HalalBuilds disables itself.
- Console explains the missing dependency clearly.

## Save And Clipboard Tests

### Save Small House

Steps:

1. Select a small house with WorldEdit or the HalalBuilds wand.
2. Run `/hb save small_house`.
3. Run `/hb list`.
4. Run `/hb info small_house`.

Expected:

- Build saves successfully.
- Build appears in list.
- Metadata includes creator UUID, created time, dimensions, world, and origin offset.
- `.schem` and `metadata.yml` exist under `plugins/HalalBuilds/builds/small_house/`.

### Copy Selected Build

Steps:

1. Select a small building.
2. Run `/hb copy`.

Expected:

- Clipboard is created.
- Player receives dimensions or success message.

### Reject Incomplete Selection

Steps:

1. Clear or avoid creating a WorldEdit selection.
2. Run `/hb save invalid`.
3. Run `/hb copy`.

Expected:

- Commands fail cleanly.
- Player is told a complete selection is required.

### Reject Oversized Selection

Steps:

1. Set a low `max-selection-volume`.
2. Select a larger region.
3. Run `/hb save too_large`.

Expected:

- Save is rejected.
- Player sees the configured limit and actual volume where practical.

## Paste Tests

### Paste Saved Build On Flat Land

Steps:

1. Save `small_house`.
2. Look at a flat target location.
3. Run `/hb paste small_house`.

Expected:

- Build appears at target location.
- Alignment is predictable.
- No unexpected terrain clearing occurs beyond required paste behavior.

### Paste Clipboard

Steps:

1. Select a building.
2. Run `/hb copy`.
3. Move to another location.
4. Run `/hb paste clipboard`.

Expected:

- Clipboard build appears at target location.

### Paste With Rotation

Steps:

1. Save a directional build with a clear front side.
2. Run each command:
   - `/hb paste directional --rotate 0`
   - `/hb paste directional --rotate 90`
   - `/hb paste directional --rotate 180`
   - `/hb paste directional --rotate 270`

Expected:

- Rotation is correct for all four values.
- Footprint remains aligned.

### Reject Invalid Rotation

Steps:

1. Run `/hb paste small_house --rotate 45`.

Expected:

- Command is rejected.
- Player sees allowed values.

## Smart Foundation Tests

### Paste Into Mountain

Steps:

1. Save a house.
2. Aim at a mountain slope.
3. Run `/hb paste small_house --preview`.
4. Review preview.
5. Run `/hb confirm`.

Expected:

- Terrain intersecting the structure is cleared.
- Building interior is usable.
- Foundation is created beneath the footprint.
- Large terrain changes require confirmation.

### Paste Above Cave

Steps:

1. Find or create hollow space under the surface.
2. Paste a saved build above it.

Expected:

- Empty space beneath the building is filled with foundation blocks.
- Foundation does not exceed configured max depth.

### Paste Above Water

Steps:

1. Paste a building over shallow water or shoreline.

Expected:

- Foundation fills below the footprint according to config.
- Water under the footprint is handled consistently.

### Denylisted Block Protection

Steps:

1. Place a chest, spawner, or command block inside the affected paste area.
2. Run a smart foundation paste that would modify it.

Expected:

- Operation is blocked.
- Player is told denylisted blocks were detected.
- Protected blocks remain unchanged.

## Preview And Confirmation Tests

### Large Paste Requires Preview

Steps:

1. Set `max-paste-volume-before-preview` low.
2. Paste a build above that volume.

Expected:

- Operation does not execute immediately.
- Pending operation is created.
- Player is told to run `/hb confirm` or `/hb cancel`.

### Confirm Pending Operation

Steps:

1. Create a pending paste operation.
2. Run `/hb confirm`.

Expected:

- Operation executes.
- Pending operation is cleared.

### Cancel Pending Operation

Steps:

1. Create a pending paste operation.
2. Run `/hb cancel`.

Expected:

- Operation is canceled.
- No world changes occur.

## Cut Tests

### Cut And Paste Selection

Steps:

1. Select a small building.
2. Run `/hb cut`.
3. Confirm original region is removed.
4. Move to another location.
5. Run `/hb paste clipboard`.

Expected:

- Build is copied before removal.
- Original is removed safely.
- Clipboard paste works.

### Cut With Protected Block

Steps:

1. Include a denylisted block in the selection.
2. Run `/hb cut`.

Expected:

- Cut is blocked or protected block is preserved according to final implementation behavior.
- Player receives clear feedback.

## Import And Export Tests

### Export Build

Steps:

1. Save `small_house`.
2. Run `/hb export small_house`.

Expected:

- `plugins/HalalBuilds/exports/small_house.schem` is created.

### Import Build

Steps:

1. Place `castle.schem` in `plugins/HalalBuilds/imports/`.
2. Run `/hb import castle.schem castle`.
3. Run `/hb list`.
4. Run `/hb paste castle`.

Expected:

- Import succeeds.
- Imported build appears in list.
- Imported build pastes correctly.

### Reject Invalid Import Names

Steps:

Run commands such as:

```text
/hb import ../server.properties
/hb import C:\temp\castle.schem
/hb import castle.txt
```

Expected:

- All invalid imports are rejected.
- No files outside the imports folder are read.

## Permission Tests

### Commands Without Permission

Steps:

1. Use a non-op account with no HalalBuilds permissions.
2. Try each command.

Expected:

- Commands are denied.
- Player receives a clear permission message.

### Admin Permission

Steps:

1. Grant `halalbuilds.admin`.
2. Try all commands.

Expected:

- Admin can use all commands.

## Undo Tests

### Undo Paste

Steps:

1. Paste a saved build.
2. Run `/hb undo`.

Expected:

- Latest paste is reverted where possible.
- Player receives success message.

### Undo Cut

Steps:

1. Cut a selected build.
2. Run `/hb undo`.

Expected:

- Original region is restored where possible.
- Player receives clear feedback if undo data is unavailable.

## Planned V2 Protection Tests

### Native Protect Selection

Steps:

1. Select a small cuboid area.
2. Run `/hb protect test_house`.
3. Use another player without permission to break a block inside the area.
4. Use another player without permission to place a block inside the area.

Expected:

- Protection is created.
- Unauthorized break and place actions are blocked.
- Owner or admin can still edit according to config.

### Native Unprotect

Steps:

1. Protect a selected area.
2. Run `/hb unprotect test_house`.
3. Try breaking a block inside the former protected area.

Expected:

- Protection is removed.
- Normal server rules apply after removal.

### Paste With Native Protection

Steps:

1. Save `small_house`.
2. Run `/hb paste small_house --protect`.
3. Try editing the pasted house as an unauthorized player.

Expected:

- Pasted bounds are protected.
- Unauthorized edits are blocked.

### Respect Native Protection During Paste And Cut

Steps:

1. Create a protected region.
2. Try to paste through it as a player without admin permission.
3. Try to cut a selection intersecting it as a player without admin permission.

Expected:

- Operations are blocked.
- Protected blocks remain unchanged.

## Planned V2 WorldGuard Tests

### Respect Existing WorldGuard Region

Steps:

1. Install WorldGuard.
2. Create a protected WorldGuard region.
3. Try to paste through it as a player who cannot build there.

Expected:

- HalalBuilds blocks the paste.
- Existing WorldGuard protection is not bypassed.

### Create WorldGuard Region From Selection

Steps:

1. Select a cuboid area.
2. Run `/hb protect wg_house --worldguard`.
3. Inspect the WorldGuard region.

Expected:

- WorldGuard region is created.
- Configured flags are applied.
- Owner/member data is assigned according to config.

### Paste And Create WorldGuard Protection

Steps:

1. Save `small_house`.
2. Run `/hb paste small_house --protect wg_small_house`.
3. Inspect the generated WorldGuard region.

Expected:

- Pasted structure is protected by a WorldGuard region.
- Region bounds match the pasted structure bounds.
- Configured flags are applied.

### Missing WorldGuard Fallback

Steps:

1. Disable or remove WorldGuard.
2. Set protection backend to `worldguard`.
3. Start the server.
4. Run a protection command.

Expected:

- HalalBuilds does not crash.
- Player receives clear feedback that WorldGuard is unavailable or native fallback is used.

## Automated Tests

### Filename Validation

Test valid names:

- `house`
- `small_house`
- `castle-01`
- `Build123`

Test invalid names:

- `../house`
- `house/roof`
- `house\roof`
- `.`
- `..`
- Empty string.
- Absolute paths.
- Names exceeding configured length.

Expected:

- Valid names pass.
- Invalid names fail.

### Metadata Serialization

Test:

- Metadata writes to YAML.
- Metadata reads from YAML.
- UUIDs are preserved.
- Created time is preserved.
- Dimensions are preserved.
- Tags and notes are preserved.

Expected:

- Round trip produces equivalent metadata.

### Config Defaults

Test:

- Missing config uses defaults.
- Invalid paste mode falls back or fails safely.
- Invalid material is reported.
- Denylisted material names are parsed.
- Planned v2 protection defaults are parsed.
- Planned v2 WorldGuard settings are parsed without requiring WorldGuard at unit-test time.

Expected:

- Config service produces safe typed values.

### Command Parsing

Test:

- `/hb paste house`
- `/hb paste house --rotate 90`
- `/hb paste house --preview`
- `/hb paste house --rotate 180 --preview`
- Invalid flags.
- Missing arguments.

Expected:

- Valid commands parse correctly.
- Invalid commands return helpful usage errors.

## Integration Tests

If tooling allows, create a local Paper test server integration setup that:

- Starts a Paper server.
- Installs FAWE and HalalBuilds.
- Runs scripted commands.
- Verifies generated files.
- Verifies world changes using test coordinates.

Integration tests are optional at first but valuable before public releases.
