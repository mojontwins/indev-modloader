# indev-modloader

A modloader for Minecraft indev 20100223

# What's this

This document and repository will contain my efforts in creating a modloader for indev. This file will document the process and means to be didactic. I intend to make a release for every feature I add. The goal is being able to create rather complex indev mod using a mod_Name.java file. This modloader will modify whichever base classes are needed to make this possible, thus it will be released in form of a jar-drop mod.

This ModLoader is of course based on how Risugami's original ModLoader works, but it's not a port - albeit some methods are basicly the same.

I'm using [MCP-LTS](https://github.com/ModificationStation/1.7.3-LTS) to decompile and modify Minecraft Indev 20100223, so big thanks to all developers and contributors.

As you will notice, English is not my first language. That's why I'm using github to write the docs. Pull requests to fix my crappy writing are welcome!

# Roadmap

This is the roadmap which will be constantly changing.

* Create a basic ModBase class and make the system to automaticly run mod_Name classes.
* Use your mod class to add blocks.
* Use your mod class to add items.
* Use your mod class to add recipes of any kind.
* Use your mod class to add food.
* Use your mod class to add tile entities.
* Use your mod class to add entities.
* Use your mod class to add new kind of terrain generation.
* Use your mod class to add structures.

# 1. Creating a basic ModBase class

The basic ModBase class will be almost empty at this stage and will keep growing as I add hooks to the main Minecraft classes.

I start with adding a new package `com.mojontwins.modloader` to contain the main `ModLoader` and `BaseMod` classes, and eventually your own `mod_Name` classes. I start creating three empty classes:

* `ModLoader.java`.
* `BaseMod.java`.
* `mod_Example.java` which extends `BaseMod`.

Our first goal is getting Minecraft to initialize ModLoader and then get ModLoader to load any `mod_Name` classes in its package. Our first attempt is hooking a call to `ModLoader.init` right before the main loop at `Minecraft.java` is going to start.

Our first `com/mojontwins/modloader/ModLoader.java`:

```java
	package com.mojontwins.modloader;

	public class ModLoader {
		public static boolean isInitialized;
		
		public ModLoader () {
			
		}
		
		public static void init () {
			isInitialized = true;
			
			System.out.println ("ModLoader initialized!");
		}
	}
```

Hook at `net/minecraft/client/Minecraft.java` @ 316:

```java

	ModLoader.init ();

```

Run and we get 'ModLoader initialized!' in the console.

## Getting ModLoader to load and run `mod_Name` classes.

First of all we need to create basic abstract method `load` in `BaseMod.java`:

```java
    abstract class BaseMod {
        public abstract void load () throws Exception;
        
        public void modsLoaded () {
            
        }
    }
```

And then instantiate it in our `mod_Example.java` with a simple `System.out.println`:

```java
	package com.mojontwins.modloader;

	public class mod_Example extends BaseMod {
		public void load () throws Exception {
			System.out.println ("mod_Example load!");
		}
	}
```

The way we are going to load this class from `ModLoader`'s `init` is the same way Risugami's Modloader does it, with the fix **coffeenotfound** made for b1.7.3 in order to avoid the `URI is not hierarchical` exception the original ModLoader threw on modern setups. 

Before we make this, we'll have to make `Minecraft.java`'s `mcDataDir` static:

```java
    public static File mcDataDir;
```

Now this is our first `ModLoader`'s `init` method:

```java
    public static void init () {
        isInitialized = true;
        
        System.out.println ("ModLoader initializing ...");
        
        try {
            File file;
    
            // Get a path to the minecraft jar.
            try {
                String path = URLDecoder
                        .decode((com.mojontwins.modloader.ModLoader.class).getProtectionDomain().getCodeSource().getLocation().getPath(), "UTF-8")
                        .replace("jar:", "").replace("file:/", "").replace("file:\\", "");
    
                if (path.contains(".jar!")) {
                    path = path.substring(0, path.lastIndexOf(".jar!") + ".jar".length());
                }
    
                path = (new File(path)).getAbsolutePath();
                
                System.out.println ("Minecarft jar path: " + path);
                
                file = new File(path);
            } catch (Exception exception) {
                throw new RuntimeException("Failed to resolve minecraft jar path!", exception);
            }
            
            // Calculate the path to the `/mods/` directory, then create it if not present
            File modDir = new File(Minecraft.mcDataDir, "/mods/");
            modDir.mkdirs();
            
            // Load mods in the `/mods/` directory
            readFromModFolder(modDir);
            
            // Load mods in the classpath (this includes the main minecraft.jar)
            readFromClassPath(file);
            
            // Sort mods by priority / dependencies. Not for now
            // sortModList();
            
            // Now run the `load` method of each mod
            for (Iterator<BaseMod> iterator = modList.iterator(); iterator.hasNext();) {
                BaseMod basemod = (BaseMod)iterator.next();
                basemod.load ();
                System.out.println ("Mod Loaded: \": " + basemod.toString() + "\"");                
            }
            
            for (Iterator<BaseMod> iterator = modList.iterator(); iterator.hasNext();) {
                BaseMod basemod = (BaseMod)iterator.next();
                basemod.modsLoaded ();          
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException ("Exception in ModLoader.init", e);          
        }
        
        System.out.println ("ModLoader initialized!");
    }
```

For now, we are not adding the ability for mod's to read .cfg files, nor be ordered automaticly using priorities / dependencies. That will come later. We basicly copied the original Risugami's methods and refactored them a bit. Running the game we get this in the console, so we are set, for the moment.

```
    ModLoader initializing ...
    Minecarft jar path: D:\MCP\MCP-LTS\eclipse\Client\bin
    Adding mods from D:\MCP\MCP-LTS\eclipse\Client\bin
    Directory found.
    Mod Initialized: "com.mojontwins.modloader.mod_Example@715df06a" from mod_Example.class
    mod_Example load!
    Mod Loaded: ": com.mojontwins.modloader.mod_Example@715df06a"
    ModLoader initialized!
```

# Use your mod class to load new Blocks

First think I want to do is creating a system to get dynamic block IDs, as I don't like having to hard-code them. First of all I need to keep track of what block IDs are already taken by the base class, and which texture indexes from the texture atlas are in use, then provide methods to obtain IDs, texture indexes, and overriding textures.

Looking at the base `Block.java` it seems that blocks 1-63 are in use. That makes my life easier, as I just have to provide blockIDs from 64 onwards. Next task is checking which texture atlas indexes are in use and create a bitmap like the original Modloader uses, and then provide new indexes based on which indexes are free.

A first examination to `terrain.png` yields there apparently free texture indexes:

47, 51, 53, 81, 82, 84, 85, 96, 97, 98, 101-239, 250-255. 

Now I'll have to double check if any of those texture indexes are used for texture effects.

This is how I have mapped available / used texture indexes: 

```java
    private static final int [] terrainTextureIndexes = new int [] {
        1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
        1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
        1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0,
        1, 1, 1, 0, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
        1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
        1, 0, 0, 1, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
        0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0
    };
```

Now I need a method to retrieve the first terrain texture index available:

```java
    /*
     * Retrieves the first unused texture index
     */
    public static int getFreeTerrainTextureIndex () {
        int res = -1;
        
        for (int i = currentTerrainTextureIndex; i < 256; i ++) {
            if (terrainTextureIndexes [i] == 0) {
                res = i;
                terrainTextureIndexes [i] = 1;
                break;
            }
        }
        
        return res;
    }
```

A similar method to retrieve a free blockID is easier, as block IDs are free from 64 onwards. So I just have to keep a counter:

```java
    public static int currentFreeBlockId = 64;

    [...]

    public static int getBlockId () throws Exception {
        if (currentFreeBlockId < 256)
            return currentFreeBlockId ++;
        else throw new Exception ("No more free block IDs.");
    }
```

The next problem we must face is the fact that most block attributes modifier methods are protected. I don't want to edit the base `Block` class if I can avoid it, so I'm creating a `ModBlock` class which inherits from `Block` which re-exports all those methods so you can instantiate them from your mod. Maybe there's a better way but this is where my knowledge of Java ends.

Hell, they are marked as `final` which means I cannot override them! They are not final in the versions I know (b1.7.3, 1.2.5). I guess I'll have to edit the base class :-/ 

I guess I'll have to try a different approach with slightly renamed methods. I'm going to try with this class:

```java
    package com.mojontwins.modloader;

    import net.minecraft.game.block.Block;
    import net.minecraft.game.block.Material;

    public class ModBlock extends Block {

        protected ModBlock(int id, Material material) {
            super(id, material);
        }

        // Reimplement protected methods from Block as they are protected
        
        public ModBlock setBlockLightOpacity(int var1) {
            lightOpacity[this.blockID] = var1;
            return this;
        }
        
        public ModBlock setBlockLightValue(float var1) {
            lightValue[this.blockID] = (int)(15.0F * var1);
            return this;
        }
        
        public ModBlock setBlockResistance(float var1) {
            setResistance (var1 * 3.0F);
            return this;
        }
        
        public ModBlock setBlockHardness(float var1) {
            setHardness (var1);
            return this;
        }    
        
        public void setBlockTickOnLoad(boolean var1) {
            tickOnLoad[this.blockID] = var1;
        }

        public void setBounds(float var1, float var2, float var3, float var4, float var5, float var6) {
            this.minX = var1;
            this.minY = var2;
            this.minZ = var3;
            this.maxX = var4;
            this.maxY = var5;
            this.maxZ = var6;
        }   
    }
``` 

If this doesn't work I'll have to resort back to changing the base class.

So in your mod you basicly would create a new class which inherits from ModBlock, or even use ModBlock directly if you are fine with it. For the sake of completion, I'll do a new class and will try to create a block using that class, give it attributes. Next step will be registering such mod and writing code to add a texture to it.

Let's start by adding a new class:

```java
    package com.mojontwins.modloader;

    import net.minecraft.game.block.Material;

    public class BlockStoneBricks extends ModBlock {
        protected BlockStoneBricks(int id, Material material) {
            super(id, material);
        }
    }
```

And for now this seems to be OK:

```java
    package com.mojontwins.modloader;

    import net.minecraft.game.block.Material;

    public class mod_Example extends BaseMod {
        ModBlock blockStoneBricks;
        
        public void load () throws Exception {
            blockStoneBricks = new BlockStoneBricks(ModLoader.getBlockId (), Material.rock).setBlockHardness(1.5F).setBlockResistance(1.5F);
            System.out.println ("mod_Example load!");
        }
    }
```

Now it's time to add to ModLoader the means to register the new block in the system. That implies, after having been created calling the constructor (which ends up calling the `Block` constructor), adding it to `Item.itemsList`. Again, I'll be adapting how Risugami's ModLoader does this albeit in a (for the moment) simplified form.

```java
    public static void registerBlock (ModBlock block, Class<ItemBlock> class1) throws Exception {
        if (block == null) throw new IllegalArgumentException("block parameter cannot be null.");
        
        int i = block.blockID;
        ItemBlock itemblock = null;
        
        if (class1 != null) {
            itemblock = (ItemBlock)class1.getConstructor(new Class[] {
                Integer.TYPE
            }).newInstance(new Object[] {
                Integer.valueOf(i - 256)
            });
        } else {
            itemblock = new ItemBlock(i - 256);
        }

        if (Block.blocksList[i] != null && Item.itemsList[i] == null) {
            Item.itemsList[i] = itemblock;
        }
    }
```

And...

```java
    package com.mojontwins.modloader;

    import net.minecraft.game.block.Material;

    public class mod_Example extends BaseMod {
        ModBlock blockStoneBricks;
        
        public void load () throws Exception {
            blockStoneBricks = new BlockStoneBricks(ModLoader.getBlockId (), Material.rock).setBlockHardness(1.5F).setBlockResistance(1.5F);
            ModLoader.registerBlock(blockStoneBricks);
        }
    }
```

At least it doesn't crash when I run it ... yet :'-D

And here comes the hard part: overriding the texture. I say hard as I don't fully understand it yet. I think it is done, in 1.2.5, via a `TextureFX`. There are `TextureFX`s in Indev so I'll try and add it that way. Cross yer fingers.

The original AddOverride takes two parameters: the texture atlas you want to override (`/terrain.png` or `/gui/items.png`) and an URI to the new texture, which should be a 16x16 file. It then gets a unique texture index for it and then enqueues it to a list which is processed latter. Let's begin with this first part. Step by step is better.

I'll use an enum to tell the method which texture atlas I'm attacking. I like that way better.

```java
    package com.mojontwins.modloader;

    public enum EnumTextureAtlases {
        TERRAIN {
            public String toString () { return "/terrain.png"; }
        }, 
        ITEMS {
            public String toString () { return "/gui/items.png"; }
        }
    }
```

```java
    public static int addOverride (EnumTextureAtlases textureAtlas, String textureURI) {
        int textureIndex; 
        
        if (textureAtlas == EnumTextureAtlases.TERRAIN) {
            textureIndex = getFreeTerrainTextureIndex ();
        } else {
            // TODO
        }
        
        Boolean success = addOverride (textureAtlas, textureURI, textureIndex);
        
        return success ? textureIndex : -1;
    }
```

Before we go on, we'll need a list of hashes to store our overrides. Each override will contain three keys: textureAtlas, textureURI and textureIndex.

```java
    public static List<HashMap<String,Object>> overrides;
```

And this:

```java
    
    public static boolean addOverride (EnumTextureAtlases textureAtlas, String textureURI, int textureIndex) {
        System.out.println ("Overriding " + textureAtlas + " with " + textureURI + " at index " + textureIndex);
        
        try {
            HashMap<String, Object> override = new HashMap<String, Object>();
            
            override.put("textureAtlas", textureAtlas);
            override.put("textureURI", textureURI);
            override.put("textureIndex", new Integer(textureIndex));
            
            overrides.add(override);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
```

So let's try this (texture URI may change, I still don't quite understand how absolute paths work - will experiment later):

```java
    package com.mojontwins.modloader;

    import net.minecraft.game.block.Material;

    public class mod_Example extends BaseMod {
        ModBlock blockStoneBricks;
        
        public void load () throws Exception {
            blockStoneBricks = new BlockStoneBricks(ModLoader.getBlockId (), Material.rock).setBlockHardness(1.5F).setBlockResistance(1.5F);
            ModLoader.registerBlock(blockStoneBricks);
            blockStoneBricks.blockIndexInTexture = ModLoader.addOverride(EnumTextureAtlases.TERRAIN, "textures/block_stone_bricks.png");
        }
    }
```

And it compiles, and doesn't crash. Yet. Console output:

```
    ModLoader initializing ...
    Minecraft jar path: D:\Cosas\modloader-indev-wip\MCP-LTS\eclipse\Client\bin
    Adding mods from D:\Cosas\modloader-indev-wip\MCP-LTS\eclipse\Client\bin
    Directory found.
    Mod Initialized: "com.mojontwins.modloader.mod_Example@2c14142e" from mod_Example.class
    Overriding /terrain.png with /stone_bricks.png at index 47
    Mod Loaded: ": com.mojontwins.modloader.mod_Example@2c14142e"
    ModLoader initialized!
```

## Actually overriding the texture

All we have done is adding a HashMap to a List. Something has to be done, actually. In Risugami's ModLoader, the method in charge is `registerAllTextureOverrides`, which processes the list and does its magic. In the original ModLoader I'm using as a model (1.2.5), this is done in the `onTick` method which is called from EntityRendererProxy (via a base class edit). Maybe my approach is wrong, we'll see with time, but I guess I can just add a call after my `init` method has run, as all `mod_XXX` classes have been loaded and all `load` methods have been called, thus all overrides are in order. So let's take this path, for the moment. So a new addition to `Minecraft.java` (with a slight modification to what we had before):

```java
    try {
        ModLoader.init ();
        ModLoader.registerAllTextureOverrides (this.renderEngine);
    } catch (Exception e) {
        e.printStackTrace();
        this.shutdownMinecraftApplet();
        return;
    }
```

And the new ModLoader method

```java
    public static void registerAllTextureOverrides (RenderEngine renderEngine) throws Exception {
        for (Iterator<HashMap<String, Object>> iterator = overrides.iterator(); iterator.hasNext();) {
            HashMap<String, Object> thisEntry = iterator.next ();
            String textureURI = thisEntry.get("textureURI");
            BufferedImage bufferedimage = loadImage(renderEngine, textureURI);
            ModTextureStatic modTextureStatic = new ModTextureStatic (textureIndex, textureAtlas, bufferedImage);
            renderEngine.registerTextureFX(modTextureStatic);
        }
    }
```

Now I need to implement `loadImage` and the `ModTextureStatic` TextureFX.

```java
    public static BufferedImage loadImage (RenderEngine renderEngine, String textureURI) throws Exception {
        InputStream inputStream = ModLoader.class.getResourceAsStream (textureURI);
        if (inputStream == null) throw new Exception ("Image not found: " + textureURI);
        
        BufferedImage bufferedImage = ImageIO.read(inputStream);
        if (bufferedImage == null) throw new Exception ("Image corrupted: " + textureURI);
        
        return bufferedImage;
    }
```

If my knowledge in Java is correct, this makes the image path relative to where ModLoader resides, that is, `/com/mojontwins/modloader/`. Using an absolute path should work as well, but I'll have to make some tests with the system loading the mod from a .zip file in the future if I want to write proper documentation.

Now on to the TextureFX. TextureFXs just render stuff to a pixel array. In our case, we will copy the `BufferedImage` to the pixel array when instantiating the class. 

This is a very simple version which will only work with 16x16 pixel textures. Bigger textures would need a more fancy implementation like Risugami's. But for now this will suffice:

```java
    package com.mojontwins.modloader;

    import java.awt.image.BufferedImage;

    import net.minecraft.client.renderer.block.TextureFX;

    public class ModTextureStatic extends TextureFX {
        public ModTextureStatic(int textureIndex, EnumTextureAtlases textureAtlas, BufferedImage bufferedImage) {
            super(textureIndex);
            
            // Load the texture to the `imagedata` array
            int pixels [] = new int [256];
            bufferedImage.getRGB (0, 0, 16, 16, pixels, 0, 16);
            
            for (int i = 0; i < 256; i ++) {
                imageData [4 * i + 0] = (byte) ((pixels [i] >> 16) & 0xff);
                imageData [4 * i + 1] = (byte) ((pixels [i] >> 8) & 0xff);
                imageData [4 * i + 2] = (byte) (pixels [i] & 0xff);
                imageData [4 * i + 3] = (byte) ((pixels [i] >> 24) & 0xff);
            }       
        }

        public void onTick () {
            // Does nothing
        }
    }
```

So now we have the full set - but we still can't see anything. But running yields... no crashes! (yet!!).

To show this is working we'll add a hook I might remove or modify later. In `net.minecraft.game.level.generator.LevelGenerator`, method `generate`, we are going to add a call to a new method in `BaseMod` to paint some stuff to the level, right before the "planting" stage, @ around line 412:

```java
    [...]

    ModLoader.generateStructures (this, var6);

    [...]
```

Then, make ModLoader call the same method of all defined `BaseMod` instances:

```java
    public static void generateStructures (LevelGenerator levelGenerator, World world) {
        for (Iterator<BaseMod> iterator = modList.iterator(); iterator.hasNext();) {
            ((BaseMod)iterator.next()).generateStructures(levelGenerator, world);
        }
    }
```

Make our `BaseMod` a bit less barren:

```java
    package com.mojontwins.modloader;

    import net.minecraft.game.level.World;
    import net.minecraft.game.level.generator.LevelGenerator;

    abstract class BaseMod {
        public abstract void load () throws Exception;
        
        public void modsLoaded () { 
        }
        
        public void generateStructures (LevelGenerator levelGenerator, World world) {
        }
    }
```

And now add actual code to `mod_Example`. We are just going to add our new block on top of the spawn house...

```java
    public void generateStructures (LevelGenerator levelGenerator, World world) {
        world.setBlockWithNotify(world.xSpawn, world.ySpawn + 1, world.zSpawn - 3, blockStoneBricks.blockID);
    }
```

