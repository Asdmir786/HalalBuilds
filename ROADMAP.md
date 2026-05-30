# HalalBuilds Roadmap

This document is the long-term product and engineering roadmap for HalalBuilds.

It explains where the plugin is now, what the next versions should do, why each feature matters, what should not be rushed, and how future systems should fit together without turning the project into a fragile pile of commands.

## Current Position

HalalBuilds is currently in the `1.1.x` line.

The plugin already has the foundation of a useful builder workflow:

- Paper plugin lifecycle.
- Java `21`.
- Paper `1.21.11`.
- FAWE/WorldEdit runtime dependency.
- Cuboid selection through WorldEdit/FAWE.
- Saved builds using Sponge `.schem`.
- Metadata files for saved builds.
- Import and export folders controlled by the plugin.
- Copy, cut, paste, preview, confirm, cancel, undo, rotate, and move workflows.
- Smart foundation placement.
- Exact placement mode.
- Visual particle previews.
- Per-command air and entity controls.
- LuckPerms-friendly permission nodes.
- GitHub release workflow.

The current user experience is still command-first. That is acceptable for v1, because v1 is meant to prove the core logic: save a build, preview it, place it, rotate it, move it, and keep server operations controlled.

Future versions should improve confidence, safety, scale, and ease of use.

## Product Direction

HalalBuilds should become a server staff building system, not just a thin wrapper around WorldEdit.

FAWE remains the backend engine. HalalBuilds should become the workflow layer:

- It should hide most raw FAWE complexity from builders.
- It should give admins predictable commands and permissions.
- It should prevent accidental destructive edits.
- It should make large structure placement manageable.
- It should make saved builds easy to browse, preview, place, protect, import, and export.

The plugin should keep working without a resource pack, GUI, or WorldGuard. Optional integrations can improve the experience, but the core system should stay dependable with only Paper and FAWE installed.

## Version Strategy

The roadmap should move in focused releases.

Do not put every idea into one giant version. Each release should have a clear theme and should be testable by itself.

Recommended path:

```text
v1.1.x = current visual preview line and polish
v1.2.0 = smart placement queue for huge builds
v1.3.0 = disk-backed undo and job recovery
v1.4.0 = inventory GUI build browser
v1.5.0 = clean save modes and structure filtering
v1.6.0 = native and WorldGuard protection
v2.0.0 = polished full builder platform
```

Until further notice, Javadoc/build polish that would otherwise be `v1.1.1` should be folded into the current `1.1.0` development line. Do not create a separate `1.1.1` release just for documentation or build-output cleanup unless runtime fixes require a new public patch.

## v1.1.x: Current Visual Preview Line

### Purpose

The `1.1.x` line is about making the existing command workflow easier to trust.

Before `1.1.0`, preview was mostly a chat summary. That was useful, but not enough for real building. A builder needs to see where the structure will go before confirming.

### Current Features

The current `1.1.0` scope includes:

- Particle preview outlines.
- Corner and height indicators.
- Facing arrow.
- `/hb move`.
- `/hb rotate` refreshing the preview.
- Confirm/cancel cleanup.
- `--ignore-air` and `--paste-air`.
- `--entities` and `--no-entities`.
- Preview summaries with skipped-air and entity counts.

### Polish Goals

The remaining polish in the `1.1.x` line should stay small:

- Fix runtime bugs found by live testing.
- Improve preview readability.
- Tune particle density if it is too noisy.
- Improve chat wording.
- Keep build output clean.
- Keep documentation accurate.

### Do Not Add Yet

Avoid adding these to `1.1.x` unless absolutely necessary:

- Inventory GUI.
- Protection.
- Large placement job queue.
- Disk-backed undo.
- Resource-pack UI.

Those are bigger systems and should each get their own release.

## v1.2.0: Smart Placement Queue

### Purpose

`v1.2.0` should solve the big-build problem.

The goal is not to pretend Minecraft can place millions of blocks for free. The goal is to turn huge pastes into controlled jobs that do not freeze the server, do not surprise admins, and can report progress clearly.

### Problem

A large schematic paste can be expensive because the server must:

- Read schematic data.
- Load or touch many chunks.
- Write block data.
- Update lighting.
- Save chunk data.
- Potentially spawn entities.
- Potentially update tile entities.
- Potentially clear terrain and create foundations.

Even with FAWE, a huge operation needs boundaries.

### Product Goal

The user should be able to run:

```text
/hb paste mega_castle --preview
/hb confirm
```

Then HalalBuilds should create a placement job instead of trying to do everything as one blind operation.

The user should see progress:

```text
HalalBuilds job #12
Blocks placed: 184000 / 1240000
Chunks done: 58 / 301
Skipped air: 2800000
Skipped unchanged: 42100
Entities queued: 12
Status: running
```

### Core Concepts

The smart placement queue should introduce these concepts:

- Placement job.
- Job ID.
- Job owner.
- Job state.
- Chunk work units.
- Batch size.
- Time budget.
- Progress reporting.
- Pause/resume/cancel.

### Proposed Job States

```text
PENDING
ANALYZING
RUNNING
PAUSED
CANCEL_REQUESTED
FAILED
COMPLETED
```

### Proposed Commands

```text
/hb jobs
/hb job <id>
/hb pause <id>
/hb resume <id>
/hb canceljob <id>
```

`canceljob` should be used instead of `cancel` so it does not conflict with pending preview cancellation.

### Placement Strategy

The first implementation should be conservative:

1. Load and rotate the clipboard.
2. Analyze non-air blocks.
3. Skip air if configured.
4. Optionally skip unchanged blocks.
5. Split work by chunk.
6. Sort chunks near the player first.
7. Paste each chunk or batch through FAWE.
8. Sleep/yield between batches.
9. Adjust batch size based on measured time.
10. Send progress updates.

### Chunk Awareness

Chunk-aware placement matters because Minecraft stores world data in chunks.

If HalalBuilds understands chunk boundaries, it can:

- Avoid random scatter writes.
- Report chunk progress.
- Preload or validate target chunks.
- Group work more predictably.
- Reduce user confusion when a large paste takes time.

### Skipping Work

Skipping unnecessary work is one of the biggest performance wins.

`v1.2.0` should support:

- Skip schematic air.
- Skip structure void.
- Skip unchanged blocks.
- Skip denied materials.
- Count skipped blocks in preview.

A schematic with a volume of 2,000,000 blocks may only have 300,000 real blocks. Ignoring air can make the difference between a practical paste and a painful paste.

### Adaptive Throttling

The queue should not use one fixed speed for every server.

Configuration should allow:

```yaml
placement-queue:
  enabled: true
  max-blocks-per-batch: 15000
  min-blocks-per-batch: 1000
  ticks-between-batches: 1
  target-ms-per-batch: 20
  pause-if-tps-below: 17.5
  cancel-if-tps-below: 12.0
  skip-unchanged-blocks: true
```

The queue should measure how long batches take and adapt:

- If batches are fast, increase batch size gradually.
- If batches are slow, reduce batch size.
- If TPS drops too far, pause.
- If the server is in danger, stop and report clearly.

### Preview Before Job

Large jobs should always require preview and confirmation.

The preview should show:

- Total volume.
- Actual non-air block count.
- Air skipped.
- Unchanged blocks estimate if possible.
- Entities to paste.
- Chunks touched.
- Terrain blocks to clear.
- Foundation blocks to place.
- Whether the paste will become a job.

### Permissions

Potential permissions:

```text
halalbuilds.jobs
halalbuilds.jobs.manage
halalbuilds.jobs.cancel
halalbuilds.limit.bypass
```

Normal builders should not be able to bypass large operation safeguards.

### Why Not Remove FAWE Limits

FAWE limits exist to protect the server.

HalalBuilds should not blindly remove them. Instead, it should:

- Add its own preflight safety checks.
- Use preview and confirmation.
- Break large work into jobs.
- Let admins intentionally raise FAWE limits if needed.
- Document the risks clearly.

## v1.3.0: Disk-Backed Undo And Recovery

### Purpose

`v1.3.0` should make large operations safer after `v1.2.0` introduces jobs.

Current undo is best-effort and memory-oriented. That is acceptable for small operations. It is not enough for massive builds.

### Problem

Huge undo snapshots can consume too much memory.

If a paste changes 1,000,000 blocks, storing all old block states in memory is risky.

### Product Goal

For large jobs, HalalBuilds should store undo data on disk:

```text
plugins/HalalBuilds/undo/<job-id>/
```

The undo should store changed block data compactly enough to survive normal operation.

### Features

`v1.3.0` should add:

- Disk-backed undo snapshots.
- Undo metadata.
- Undo expiration.
- Configurable max undo disk usage.
- Job recovery metadata.
- Better failure reporting.

### Proposed Config

```yaml
undo:
  enabled: true
  max-memory-blocks: 100000
  disk-backed-above-blocks: 100000
  max-disk-mb-per-job: 512
  expire-after-hours: 24
  keep-failed-job-data: true
```

### Proposed Commands

```text
/hb undo
/hb undo <jobId>
/hb undos
/hb clearundo <jobId>
```

### Recovery

If a server restarts during a large job, HalalBuilds should be able to report:

- Job was incomplete.
- Job can be resumed.
- Job can be canceled.
- Job cannot be recovered because data is missing.

Recovery does not need to be perfect in the first implementation, but the data model should leave room for it.

## v1.4.0: Inventory GUI Build Browser

### Purpose

`v1.4.0` should reduce command memorization.

Builders should be able to browse builds visually in a Minecraft inventory menu.

### Product Goal

Add:

```text
/hb menu
```

The GUI should allow:

- Browsing saved builds.
- Searching or paging through builds.
- Viewing build info.
- Previewing a selected build.
- Rotating preview.
- Moving preview.
- Confirming paste.
- Canceling preview.
- Exporting if permitted.
- Deleting if permitted.

### GUI Design

The first GUI should not require a resource pack.

Use vanilla items:

- Paper for info.
- Compass for preview.
- Arrow items for movement.
- Clock or rotate item for rotation.
- Lime concrete for confirm.
- Red concrete for cancel.
- Chest or barrel for saved builds.

### Permissions

The GUI should respect the same command permissions.

If a player lacks `halalbuilds.delete`, delete buttons should not appear or should be disabled.

### Why GUI Comes After Jobs

The GUI should be built on stable backend workflows.

If large placement jobs are not settled yet, GUI actions may need to be redesigned later. Doing the job queue first gives GUI buttons better backend commands to call.

## v1.5.0: Clean Save Modes

### Purpose

`v1.5.0` should solve the “I accidentally saved grass, dirt, mountain, or empty air” problem.

Current behavior saves exactly what is inside the selected cuboid. That is honest and predictable, but not always convenient.

### Product Goal

Add cleaner save options:

```text
/hb save house --trim-air
/hb save house --ignore grass_block,dirt,stone
/hb save house --structure-only
```

### Save Modes

Potential modes:

```text
exact
trim_air
ignore_materials
structure_only
```

### Exact

Current behavior.

Everything inside the cuboid is saved.

### Trim Air

Remove empty outer air from the schematic bounds.

This should not remove intentional interior air.

### Ignore Materials

Allow builders to exclude common terrain blocks:

```text
/hb save house --ignore grass_block,dirt,stone
```

This is useful when a build is sitting on terrain that should not become part of the saved schematic.

### Structure Only

This is the hardest option and should be treated carefully.

Possible strategy:

- Start from non-air blocks.
- Optionally ignore configured terrain blocks.
- Find connected components.
- Keep the largest connected structure.
- Preserve interior air only inside structure bounds.

This feature can easily surprise users, so it must have preview information and clear docs.

### Metadata

Saved metadata should record:

- Save mode.
- Ignored materials.
- Whether entities were saved.
- Whether air was trimmed.
- Original selection size.
- Final schematic size.

## v1.6.0: Native And WorldGuard Protection

### Purpose

`v1.6.0` should protect pasted or selected builds.

The user specifically wants a system where a selected square/cuboid or pasted structure can become protected so players cannot break it.

### Product Goal

Add both:

- HalalBuilds-native protection.
- Optional WorldGuard integration.

WorldGuard should be used when installed and configured, but HalalBuilds should still be able to protect regions without it.

### Commands

```text
/hb protect <name>
/hb unprotect <name>
/hb protection info <name>
/hb protection list
/hb paste <build> --protect
/hb paste <build> --protect <regionName>
```

### Native Protection

Native protection should listen to events:

- Block break.
- Block place.
- Explosion damage.
- Fire spread.
- Piston movement.
- Bucket use if needed.
- Entity griefing if needed.

It should store:

- Region name.
- Owner UUID.
- Members.
- World.
- Minimum point.
- Maximum point.
- Source build name if pasted.
- Created time.
- Protection flags.

### WorldGuard Integration

If WorldGuard is present:

- HalalBuilds can create WorldGuard regions.
- HalalBuilds can respect existing WorldGuard regions.
- HalalBuilds can refuse pastes that overlap protected regions unless the player has permission.

WorldGuard support should be optional. The plugin should not fail if WorldGuard is absent.

### Permissions

Potential permissions:

```text
halalbuilds.protect
halalbuilds.unprotect
halalbuilds.protection.info
halalbuilds.protection.list
halalbuilds.protection.admin
```

### Protection Risks

Protection can become complicated quickly.

Important details:

- Operators may bypass permissions.
- Region overlap rules must be clear.
- WorldGuard region names must be safe.
- Native regions need efficient lookup.
- Players should not be able to protect other players' builds without permission.

## v2.0.0: Full Builder Platform

### Purpose

`v2.0.0` should be the polished “this is not just a command plugin anymore” release.

It should combine stable versions of:

- Smart placement queue.
- Disk-backed undo.
- Inventory GUI.
- Clean save modes.
- Native protection.
- WorldGuard integration.
- Better visual previews.
- Better docs and release assets.

### Possible v2 Features

Potential v2 additions:

- Full inventory GUI.
- Better preview visuals with BlockDisplay entities.
- Optional resource-pack icons.
- Build categories/tags.
- Build search.
- Favorite builds.
- Import/export manager.
- Admin audit log.
- Better thumbnails or generated preview metadata.
- More detailed permission presets.

### Resource Pack Role

A resource pack should be optional.

It can improve:

- GUI icons.
- Confirm/cancel buttons.
- Build category icons.
- Custom preview markers.

It should not be required for core functionality.

### BlockDisplay Or Ghost Preview

Particles are good for v1.1.0.

For v2, ghost previews could be improved using display entities or other client-side techniques.

The plugin should avoid fake block previews unless cleanup is extremely reliable.

## Engineering Principles

### Keep FAWE As The Engine

HalalBuilds should not fight FAWE.

FAWE is good at large block operations and schematic compatibility. HalalBuilds should use it for the heavy world-editing work.

HalalBuilds should add:

- Workflow.
- Safety.
- Permissions.
- Storage.
- Preview.
- Job orchestration.
- Protection.

### Avoid Silent Destructive Behavior

Any operation that can clear lots of terrain should be visible before it happens.

Use preview and confirmation for:

- Large volume.
- Large terrain clearing.
- Large foundation fill.
- Denylisted block detection.
- Entity-heavy schematics.
- Protected area overlap.

### Prefer Conservative Defaults

Defaults should protect server owners.

Recommended defaults:

- FAWE required.
- Preview required for large pastes.
- Paste air disabled by default.
- Denylisted blocks protected.
- Overwrite disabled.
- Protection cautious.

### Keep Commands Stable

Existing commands should not change meaning casually.

If a command behavior changes, document it clearly and explain why.

### Make Every Major Feature Testable

Each release should update:

- README.
- USER_GUIDE.
- CONFIG.
- COMMANDS_AND_PERMISSIONS.
- HOW_TO_TEST.
- RELEASE_NOTES.
- RELEASE_CHECKLIST.

Each release should also add tests where practical.

## Testing Strategy

### Unit Tests

Continue testing:

- Command parsing.
- Name validation.
- Path validation.
- Config parsing.
- Metadata serialization.
- Rotation math.
- Schematic utility behavior where possible.

### Manual Tests

Manual tests are required for:

- FAWE runtime behavior.
- Particle preview visibility.
- Movement direction.
- Entity save/paste.
- Smart foundation terrain behavior.
- Protection event handling.
- GUI clicks.
- Large job behavior.

### Test Server

Every release should be tested on:

- Paper `1.21.11`.
- Java `21`.
- Matching FAWE version.
- Fresh config.
- Existing upgraded config.
- Non-op test player with LuckPerms.

## Release Rules

Before publishing a release:

1. Run a clean build.
2. Confirm jar exists.
3. Run unit tests.
4. Review docs for stale versions.
5. Check command docs match code.
6. Smoke-test on a real server if runtime behavior changed.
7. Tag release.
8. Upload jar.
9. Verify GitHub release asset exists.

## Current Priority

The next major engineering target should be:

```text
v1.2.0 - Smart Placement Queue
```

The reason is simple: once HalalBuilds can handle big placement jobs safely, every later feature becomes more useful.

The GUI can call job APIs.

Undo can store job snapshots.

Protection can attach to job results.

Clean save modes can feed better schematics into the placement queue.

So the queue is the next real backbone.
