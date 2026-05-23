# Admin Setup

This page explains how to install HalalBuilds cleanly so players and builders use the `/hb` workflow instead of getting confused by raw FAWE or WorldEdit commands.

## Recommended Setup

Install these plugins on the Paper server:

- HalalBuilds
- FastAsyncWorldEdit
- LuckPerms or another permission manager

FAWE is required because HalalBuilds uses the FAWE/WorldEdit API for selections, clipboards, schematic reading/writing, and high-volume block changes. HalalBuilds is the staff-facing workflow layer on top.

## What Players Should See

Normal players should not need any HalalBuilds, FAWE, or WorldEdit permissions.

Builders should use:

```text
/hb wand
/hb save <name>
/hb copy
/hb cut
/hb paste <name|clipboard> --preview
/hb rotate <0|90|180|270>
/hb confirm
/hb cancel
/hb undo
```

They should not need raw commands such as `//copy`, `//paste`, `//schem`, or other WorldEdit/FAWE editing commands unless you intentionally want them to have direct WorldEdit access.

## LuckPerms Example

Builder group:

```text
/lp group builder permission set halalbuilds.use true
/lp group builder permission set halalbuilds.wand true
/lp group builder permission set halalbuilds.save true
/lp group builder permission set halalbuilds.copy true
/lp group builder permission set halalbuilds.cut true
/lp group builder permission set halalbuilds.paste true
/lp group builder permission set halalbuilds.preview true
/lp group builder permission set halalbuilds.confirm true
/lp group builder permission set halalbuilds.undo true
```

Admin group:

```text
/lp group admin permission set halalbuilds.admin true
```

Optional import/export permissions:

```text
/lp group builder permission set halalbuilds.import true
/lp group builder permission set halalbuilds.export true
```

Dangerous/admin permissions:

```text
/lp group admin permission set halalbuilds.delete true
/lp group admin permission set halalbuilds.reload true
```

## Keeping FAWE In The Background

Do not grant broad WorldEdit or FAWE permissions to regular builders unless you want them to use raw WorldEdit/FAWE.

Avoid giving these to normal users:

```text
worldedit.*
fawe.*
worldedit.clipboard.*
worldedit.schematic.*
worldedit.region.*
```

Operators can bypass many permission checks. For a real test, use a non-op account with only the intended LuckPerms nodes.

## Can HalalBuilds Hide FAWE Completely?

Not completely, and it should not try to fake that.

FAWE is a real server plugin and dependency, so it can still appear in plugin lists, logs, dependency messages, and admin tooling. What HalalBuilds can do is make the normal builder workflow branded and simple:

- Use `/hb` commands for all build operations.
- Use HalalBuilds messages and docs.
- Give staff HalalBuilds permissions, not broad WorldEdit permissions.
- Keep FAWE installed as the backend editing engine.

That gives builders a clean HalalBuilds experience while keeping the reliable FAWE engine underneath.

## Common Confusion

If a builder sees WorldEdit or FAWE commands in tab completion, check whether they are op or have broad WorldEdit/FAWE permissions.

If `/hb wand` works but WorldEdit commands do not, that is expected for a HalalBuilds-only builder.

If `/hb paste <name> --preview` works and `/hb confirm` pastes, the HalalBuilds workflow is working.

If a saved build only pastes grass or one flat layer, the selected cuboid was only one block tall. Select from the bottom corner to the opposite top corner of the full building before saving.
