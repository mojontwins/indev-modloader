# indev-modloader

A modloader for Minecraft Indev 20100223

# What's this

This document and repository will contain my efforts in creating a modloader for indev. This file will document the process and means to be didactic. I intend to make a release for every feature I add. The goal is being able to create rather complex indev mod using a mod_Name.java file. This modloader will modify whichever base classes are needed to make this possible, thus it will be released in form of a jar-drop mod.

**This ModLoader is based in Risugami's original ModLoader**, but it's not a *direct* port - albeit some methods are basicly the same.

My goal is keeping base class edit to the bare minimum. I'll be modifying Indev to make it more externally configurable. 

I'm using [Retro MCP](https://github.com/MCPHackers/RetroMCP) to decompile and modify Minecraft Indev 20100223, so big thanks to all developers and contributors.

![Retro MCP logo](https://repository-images.githubusercontent.com/417943142/cac3478c-1e9c-4987-98e2-a0223d6b8988)

As you will notice, English is not my first language. That's why I'm using github to write the docs. Pull requests to fix my crappy writing are welcome!

# Installing Modloader for Indev

* Grab a copy of Minecraft in-20100223 `minecraft.jar`. Open with 7zip or other compressed file manager.
* Open the latest `modloader-indev-rXXXX.zip` file with 7zip or other compressed file manager.
* Drag & drop all files from modloader to `minecraft.jar`
* Delete `META-INF` inside `minecraft.jar`
* Close all files.

Your mods can go into a  `mods` folder or right into `minecraft.jar`. Mods have the classpath `com.mojontwins.modloader`. Check this git for lots of examples.

## Betacraft

Follow these steps if you want to try the example mods included in this repo using Betacraft.

* Open betacraft, create a new instance for Indev Modloader. Call it `Indev Modloader`.
* Configure the **in-20100223** version.
* Launch the game once. Close it once it's loaded.
* Go to `%appdata%/.betacraft/versions/`
* Copy (or rename) `in-20100223.jar` to `in-20100223-modloader.jar`
* Go to `%appdata%/.betacraft/versions/jsons/`
* Copy (or rename) `in-20100223.info` to `in-20100223-modloader.info`
* Open `in-20100223.jar` with 7zip or other compressed file manager.
* Open the latest `modloader-indev-rXXXX.zip` file with 7zip or other compressed file manager.
* Drag & drop all files from `modloader-indev-rXXXX.zip` to `in-20100223-modloader.jar`
* Delete `META-INF` inside `in-20100223-modloader.jar`
* Go to `%appdata%/.betacraft/versions/Indev Modloader` (or the name you gave to your new instance).
* Create a `mods` folder inside.
* Put `modloader-indev-examples.zip` from this repo inside the `mods` folder, *don't unpack it*.
* Launch Betacraft, select the `Indev Modloader` instance.
* Click on [Select Version] and select `in-20100223-modload`
* Click `Play`. Enjoy the bug fixes and the new level themes.

# I wanna fiddle with this

Here's what you need to do:

1. Install RetroMCP and configure it for Indev 20100223. Decompile the jar.
2. Now you have a `src` folder with the decompiled & deobfuscated source code of Minecraft Indev. Now copy the file minecraft.diff found in the `src` folder of this repo in your RetroMCP folder (at the same level as the `src` folder) and type

```
    patch --binary -p1 -u -i "minecraft.diff" -d "."
```

This should patch a few files from Minecraft.

3. Now get `src` folder in this repo and copy it to your RetroMCP folder. It should combine with the already existing, patched `src` folder.
4. Fire up Eclipse or whatever you are using.

# Diary

You can check the diary and read my babble about coding this thing: 

# Documentation

Not very good but at least it *exists*:

* [Reference](https://github.com/mojontwins/indev-modloader/blob/main/reference.md).
* [Development diary](https://github.com/mojontwins/indev-modloader/blob/main/diary.md).