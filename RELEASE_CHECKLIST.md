# Release Checklist

Use this checklist before publishing HalalBuilds `1.1.0`.

## Local Build

- [ ] Set Java to JDK `21`.
- [ ] Run `.\gradlew.bat clean build --no-daemon`.
- [ ] Confirm build succeeds.
- [ ] Confirm artifact exists:

```text
build/libs/HalalBuilds-1.1.0.jar
```

## Test Server Setup

- [ ] Create or use a test Paper `1.21.11` server.
- [ ] Run the server with Java `21`.
- [ ] Install FAWE.
- [ ] Install `HalalBuilds-1.1.0.jar`.
- [ ] Start the server.
- [ ] Confirm `/plugins` shows HalalBuilds enabled.
- [ ] Confirm `plugins/HalalBuilds/` is generated.

## Basic Command Smoke Test

Run as an operator or with `halalbuilds.admin`:

```text
/hb wand
/hb list
```

Expected:

- Wand command gives a wooden axe.
- List command works without errors.

## Save And Paste

- [ ] Select a small cuboid house with the wand.
- [ ] Run `/hb save test_house`.
- [ ] Run `/hb list`.
- [ ] Run `/hb info test_house`.
- [ ] Look at a flat target block.
- [ ] Run `/hb paste test_house --preview`.
- [ ] Confirm the preview target coordinates are correct.
- [ ] Confirm the particle preview appears.
- [ ] Run `/hb move forward 3`.
- [ ] Confirm the particle preview moves.
- [ ] Run `/hb confirm`.

Expected:

- Build saves.
- Build appears in list.
- Preview describes the correct target.
- Confirm pastes at the previewed target.

## Rotation

- [ ] Run `/hb paste test_house --rotate 90 --preview`.
- [ ] Run `/hb confirm`.
- [ ] Repeat for `180` and `270`.

Expected:

- Build rotates predictably.
- Alignment is acceptable.
- Visual preview refreshes after rotation.

## Air And Entity Controls

- [ ] Run `/hb paste test_house --ignore-air --preview`.
- [ ] Run `/hb confirm`.
- [ ] Run `/hb paste test_house --paste-air --preview`.
- [ ] Run `/hb confirm`.
- [ ] Save a test selection with `/hb save entity_test --entities`.
- [ ] Paste it with `/hb paste entity_test --entities --preview`.

Expected:

- Ignore-air does not replace existing blocks with schematic air.
- Paste-air allows schematic air to replace existing blocks.
- Entity flags do not throw command errors.

## Clipboard And Cut

- [ ] Select a small cuboid.
- [ ] Run `/hb copy`.
- [ ] Run `/hb paste clipboard --preview`.
- [ ] Run `/hb confirm`.
- [ ] Select a disposable test cuboid.
- [ ] Run `/hb cut`.
- [ ] Run `/hb undo`.

Expected:

- Clipboard paste works.
- Cut removes the selected region.
- Undo restores latest cut when possible.

## Import And Export

- [ ] Run `/hb export test_house`.
- [ ] Confirm `plugins/HalalBuilds/exports/test_house.schem` exists.
- [ ] Copy a known valid `.schem` into `plugins/HalalBuilds/imports/`.
- [ ] Run `/hb import filename.schem imported_test`.
- [ ] Run `/hb paste imported_test --preview`.
- [ ] Run `/hb confirm`.

Expected:

- Export writes a `.schem`.
- Import reads only from the imports folder.
- Imported build pastes successfully.

## Smart Foundation

- [ ] Paste a small build into a mountain or uneven slope with `/hb paste test_house --preview`.
- [ ] Confirm the preview reports terrain clearing/foundation counts.
- [ ] Run `/hb confirm`.
- [ ] Inspect the result.

Expected:

- Intersecting terrain is cleared.
- Foundation fills visible unsupported space under the footprint.
- Denylisted blocks stop the operation.

## Permission Smoke Test

- [ ] Test with a non-op player.
- [ ] Confirm commands are denied without permission.
- [ ] Grant a limited LuckPerms builder set.
- [ ] Confirm allowed commands work and denied commands remain denied.

## Publish

Only publish after the smoke test passes.

Recommended release files:

- `build/libs/HalalBuilds-1.1.0.jar`
- `README.md`
- `USER_GUIDE.md`
- `RELEASE_NOTES.md`
- `RELEASE_CHECKLIST.md`
