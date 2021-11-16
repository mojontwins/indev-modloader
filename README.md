# indev-modloader

A modloader for Minecraft Indev 20100223

# What's this

This document and repository will contain my efforts in creating a modloader for indev. This file will document the process and means to be didactic. I intend to make a release for every feature I add. The goal is being able to create rather complex indev mod using a mod_Name.java file. This modloader will modify whichever base classes are needed to make this possible, thus it will be released in form of a jar-drop mod.

**This ModLoader is based in Risugami's original ModLoader**, but it's not a *direct* port - albeit some methods are basicly the same.

My goal is keeping base class edit to the bare minimum. I'll be modifying Indev to make it more externally configurable. 

I'm using [Retro MCP](https://github.com/MCPHackers/RetroMCP) to decompile and modify Minecraft Indev 20100223, so big thanks to all developers and contributors.

![Retro MCP logo](https://repository-images.githubusercontent.com/417943142/cac3478c-1e9c-4987-98e2-a0223d6b8988)

As you will notice, English is not my first language. That's why I'm using github to write the docs. Pull requests to fix my crappy writing are welcome!

# I wanna fiddle with this before proper releases

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