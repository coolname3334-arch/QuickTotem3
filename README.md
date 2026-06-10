# QuickTotem

A client-side Fabric mod for Minecraft 26.1.

## What it does

When your **player inventory is open**, pressing **F** (the offhand swap key) will automatically equip the **totem of undying closest to your mouse cursor** into your offhand — but only if your offhand is currently empty.

If no totem is in your inventory, or your offhand already has an item, the key behaves normally.

## Requirements

- Minecraft 26.1
- Fabric Loader 0.19.3+
- Fabric API 0.145.1+26.1 (or newer)
- Java 25+

## Building

1. Ensure you have Java 25+ installed and set as your Gradle JVM.
2. If using IntelliJ IDEA, use version **2025.3 or above** (required for mixin support in 26.1).
3. Run:

```bash
./gradlew build
```

The compiled `.jar` will be in `build/libs/`.

## Installing

Drop the `.jar` into your `.minecraft/mods` folder alongside Fabric API.

## Notes

- This mod is **client-side only** and does not need to be installed on the server.
- The default key is **F**. You can rebind "Swap Item with Offhand" in Minecraft's controls settings and QuickTotem will respect your binding.
