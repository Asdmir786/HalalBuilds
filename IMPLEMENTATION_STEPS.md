# Implementation Steps

This file defines the recommended build order for HalalBuilds. The goal is to reach a safe, testable plugin in phases instead of building every feature at once.

## Phase 1: Project Setup

1. Initialize Gradle Kotlin DSL project.
2. Configure Java `21`.
3. Add Paper `1.21.11` API dependency.
4. Add FAWE and WorldEdit compile dependencies.
5. Configure plugin metadata generation.
6. Create package `com.halalbuilds`.
7. Create main class `com.halalbuilds.HalalBuildsPlugin`.
8. Create default `config.yml`.
9. Add command registration for `/halalbuilds`, `/hb`, and `/halalbuild`.

Acceptance criteria:

- Project builds successfully.
- Plugin loads on Paper with FAWE installed.
- Plugin refuses to enable or warns clearly when FAWE is missing, depending on config.

## Phase 2: Configuration And Storage

1. Implement typed config service.
2. Validate config values on startup.
3. Create plugin folders:
   - `builds/`
   - `imports/`
   - `exports/`
4. Implement safe name validation.
5. Implement safe path resolution.
6. Implement metadata model.
7. Implement metadata serialization and deserialization.

Acceptance criteria:

- Config defaults load correctly.
- Invalid names are rejected.
- Path traversal attempts are rejected.
- Metadata can be saved and loaded.

## Phase 3: Command Framework

1. Implement command dispatcher.
2. Implement permission checks.
3. Implement player-only command checks.
4. Implement help output for invalid usage.
5. Add tab completion where practical.

Acceptance criteria:

- Every planned command is registered.
- Missing permissions return clear messages.
- Console cannot run player-only commands.

## Phase 4: Selection And Save

1. Implement WorldEdit/FAWE selection lookup.
2. Validate complete selections.
3. Validate max selection volume.
4. Convert selected regions to clipboards.
5. Write Sponge `.schem` files.
6. Write metadata files.
7. Implement `/hb save <name>`.
8. Implement `/hb list`.
9. Implement `/hb info <name>`.
10. Implement `/hb delete <name>`.

Acceptance criteria:

- A selected house can be saved.
- Saved builds appear in list.
- Info displays metadata.
- Delete removes the build folder safely.
- V1 saves the selected cuboid as-is, including grass, dirt, and other blocks inside the selection.

## Phase 5: Copy And Clipboard

1. Implement per-player clipboard storage.
2. Implement `/hb copy`.
3. Store dimensions and origin offset.
4. Add clipboard status messages.

Acceptance criteria:

- A selected region can be copied.
- Clipboard can be detected as available or missing.

## Phase 6: Exact Paste

1. Implement saved build loading.
2. Implement clipboard source loading.
3. Resolve player target location.
4. Implement exact paste mode.
5. Implement `paste-air-blocks`.
6. Implement rotation support.
7. Implement `/hb paste <name|clipboard>`.
8. Implement `/hb paste <name|clipboard> --rotate <0|90|180|270>`.

Acceptance criteria:

- Saved builds paste correctly.
- Clipboard pastes correctly.
- Rotation values align as expected.
- Invalid rotation values are rejected.

## Phase 7: Preview And Confirmation

1. Implement placement estimate model.
2. Implement pending operation storage per player.
3. Implement preview flag parsing.
4. Enforce preview for large pastes.
5. Implement `/hb confirm`.
6. Implement `/hb cancel`.
7. Add timeout cleanup for pending operations.

Acceptance criteria:

- Large operations require confirmation.
- Preview shows meaningful placement details.
- Confirm executes the operation.
- Cancel removes the pending operation.

## Phase 8: Smart Foundation

1. Compute rotated schematic bounds.
2. Detect occupied footprint.
3. Scan terrain inside affected bounds.
4. Detect denylisted blocks.
5. Compute intersecting terrain clearing.
6. Compute foundation fill operations.
7. Enforce foundation depth limit.
8. Estimate terrain change count.
9. Integrate smart foundation mode into paste flow.

Acceptance criteria:

- Pasting into a mountain clears intersecting terrain.
- Pasting above caves or water fills foundation beneath the structure.
- Denylisted blocks block the operation.
- Large terrain changes require confirmation.

## Phase 9: Cut

1. Implement copy-first cut flow.
2. Validate selection.
3. Create clipboard.
4. Remove original region safely.
5. Respect denylisted blocks.
6. Store undo data where practical.
7. Implement `/hb cut`.

Acceptance criteria:

- Cut copies the selected build.
- Original region is removed only after successful copy.
- Protected blocks are not removed.

## Phase 10: Import And Export

1. Implement import filename validation.
2. Read `.schem` files only from `imports/`.
3. Save imported schematic as a normal build.
4. Write import metadata.
5. Implement `/hb import <filename> [name]`.
6. Implement `/hb export <name>`.
7. Write exported `.schem` files to `exports/`.

Acceptance criteria:

- Valid `.schem` files import successfully.
- Invalid names and path traversal are rejected.
- Saved builds export successfully.

## Phase 11: Undo

1. Define undo operation model.
2. Store latest undo state per player.
3. Support undo after paste.
4. Support undo after cut where practical.
5. Bound undo memory usage.
6. Implement `/hb undo`.

Acceptance criteria:

- Latest paste can be undone.
- Latest cut can be undone when snapshot data exists.
- Player receives clear feedback when undo is unavailable.

## Phase 12: Testing And Polish

1. Add unit tests for filename validation.
2. Add unit tests for metadata serialization.
3. Add unit tests for config defaults.
4. Add command parsing tests where practical.
5. Run manual server tests.
6. Improve messages.
7. Add README release instructions.
8. Prepare first release artifact.

Acceptance criteria:

- Unit tests pass.
- Manual test plan passes.
- GitHub documentation is complete.
- Plugin jar is ready for server testing.

## Phase 13: Planned V2 Protection Foundation

1. Add protection models for region name, world, cuboid bounds, owner UUID, members, backend, source build, and created time.
2. Add native protection storage.
3. Implement protected region lookup by location and bounds intersection.
4. Add event listeners for block break and block place.
5. Add optional listeners for explosions, fire spread, and piston movement.
6. Add `/hb protect <regionName>`.
7. Add `/hb unprotect <regionName>`.
8. Add `/hb protection info <regionName>`.
9. Add `/hb protection list`.
10. Add protection checks to paste, cut, terrain clearing, and foundation planning.

Acceptance criteria:

- Selected cuboid areas can be protected without WorldGuard.
- Unauthorized players cannot break or place inside native protected regions.
- Admins can inspect and remove protection regions.
- Paste and cut operations respect native protected regions.

## Phase 14: Planned V2 WorldGuard Integration

1. Detect WorldGuard on startup.
2. Load WorldGuard hook only when WorldGuard is installed and enabled in config.
3. Check existing WorldGuard regions before paste, cut, terrain clearing, and foundation operations.
4. Add `/hb protect <regionName> --worldguard`.
5. Add `/hb paste <name|clipboard> --protect`.
6. Add `/hb paste <name|clipboard> --protect <regionName>`.
7. Create WorldGuard cuboid regions around pasted structure bounds.
8. Apply configured default WorldGuard flags.
9. Assign region owners and members.
10. Store HalalBuilds metadata for plugin-created WorldGuard regions.

Acceptance criteria:

- HalalBuilds works normally without WorldGuard installed.
- Existing WorldGuard regions are respected when integration is enabled.
- Protected pastes can create WorldGuard regions.
- Plugin-created WorldGuard regions can be inspected and removed through HalalBuilds commands.

## Phase 15: Planned Post-V1 Clean Scan Modes

1. Add save flags for scan modes.
2. Add optional trim-empty-air mode.
3. Add optional terrain block filter mode.
4. Add configurable ignored materials such as grass, dirt, stone, sand, and gravel.
5. Add connected-block scan mode with max volume safeguards.
6. Store scan mode and ignored materials in metadata.
7. Add preview summary for included and ignored blocks before saving.

Acceptance criteria:

- Admins can save a tighter schematic from a cuboid selection.
- Terrain filtering is optional and never surprises users by default.
- Existing v1 cuboid save behavior remains available.
