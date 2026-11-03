# Bright Soul Lanterns

A Fabric mod for Minecraft: Java Edition 26.3 that makes soul lanterns emit light level **15**, the same as regular lanterns (vanilla is 10).

## Features

- Soul lanterns give off light level 15. The value is copied from the regular lantern, so the two always match.
- Soul lanterns you placed **before** installing the mod are relit automatically when their chunk loads. No need to break and replace them.
- Everything that reads light level sees the new value: lighting, mob spawning checks, and snow/ice melting.
- Chunks without soul lanterns are skipped, so the relight check costs almost nothing.

## Installation

1. Install [Fabric Loader](https://fabricmc.net/use/) for Minecraft 26.3.
2. Download [Fabric API](https://modrinth.com/mod/fabric-api) for 26.3.
3. Put both jars in your `mods` folder.

Install it on **both the server and every client**. In single-player, your own game is enough.

## Things to know

- Soul lanterns now melt nearby snow and ice, like regular lanterns. They still scare piglins.
- If you remove the mod, soul lanterns that were already relit keep their brighter light until you break and replace them.

## Building from source

Requires Java 25.

```
./gradlew build
```

The jar ends up in `build/libs/`.

## License

[MIT](LICENSE)
