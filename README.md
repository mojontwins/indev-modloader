# indev-modloader

A modloader for Minecraft indev 20100223

# What's this

This document and repository will contain my efforts in creating a modloader for indev. This file will document the process and means to be didactic. I intend to make a release for every feature I add. The goal is being able to create rather complex indev mod using a mod_Name.java file. This modloader will modify whichever base classes are needed to make this possible, thus it will be released in form of a jar-drop mod.

**This ModLoader is based in Risugami's original ModLoader**, but it's not a *direct* port - albeit some methods are basicly the same.

I'm using [MCP-LTS](https://github.com/ModificationStation/1.7.3-LTS) to decompile and modify Minecraft Indev 20100223, so big thanks to all developers and contributors.

As you will notice, English is not my first language. That's why I'm using github to write the docs. Pull requests to fix my crappy writing are welcome!

# Roadmap

This is the roadmap which will be constantly changing.

* Create a basic ModBase class and make the system to automaticly run mod_Name classes.
* [x] Basic ModBase class and mod_XXX importing.
* [x] Use your mod class to add blocks.
* [ ] Use your mod class to add items.
* [x] Use your mod class to add recipes of any kind.
* [ ] Use your mod class to add food.
* [ ] Use your mod class to add tile entities.
* [ ] Use your mod class to add entities.
* [ ] Use your mod class to add new kind of terrain generation.
* [ ] Use your mod class to add structures.

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

A first examination to `terrain.png` yields these apparently free texture indexes:

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
        for (; currentTerrainTextureIndex < 256; currentTerrainTextureIndex ++) {
            if (terrainTextureIndexes [currentTerrainTextureIndex] == 0) {              
                terrainTextureIndexes [currentTerrainTextureIndex] = 1;
                return currentTerrainTextureIndex ++;
            }
        }
        
        return -1;
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

Hell, they are marked as `final` which means I cannot override them! They are not final in the versions I know (b1.7.3, 1.2.5). I guess I'll have to try a different approach with slightly renamed methods. I'm going to try with this class:

```java
    package com.mojontwins.modloader;

    import net.minecraft.game.block.Block;
    import net.minecraft.game.block.Material;

    public class ModBlock extends Block {
        public String name;

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
        
        public ModBlock setName(String name) {
            this.name = name;
            return this;
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
            blockStoneBricks = new BlockStoneBricks(ModLoader.getBlockId (), Material.rock).setBlockHardness(1.5F).setBlockResistance(1.5F).setName("block.stone_bricks");
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
            blockStoneBricks = new BlockStoneBricks(ModLoader.getBlockId (), Material.rock).setBlockHardness(1.5F).setBlockResistance(1.5F).setName("block.stone_bricks");
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
        try {
            for (Iterator<HashMap<String, Object>> iterator = overrides.iterator(); iterator.hasNext();) {
                Map<String, Object> thisEntry = iterator.next ();
                
                String textureURI = (String) thisEntry.get("textureURI");
                int textureIndex = (Integer) thisEntry.get("textureIndex");
                EnumTextureAtlases textureAtlas = (EnumTextureAtlases) thisEntry.get("textureAtlas");
                
                System.out.println ("Creating ModTextureStatic for texture " + textureIndex + " in " + textureAtlas + " from " + textureURI);
                
                BufferedImage bufferedImage = loadImage(renderEngine, textureURI);
                
                ModTextureStatic modTextureStatic = new ModTextureStatic (textureIndex, textureAtlas, bufferedImage);
                renderEngine.registerTextureFX(modTextureStatic);
            }
        } catch (Exception e) {
            System.out.println ("Exception @ registerAllTextureOverrides" + e);
            e.printStackTrace();
            throw e;
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

And it works!

TODO: I will later reimplement the Block ID system using some kind of registry that gets saved alognside worlds so IDs are reassigned when loading worlds.

# Crafting & smelting

We are modifying the roadmap so we can actually use our new blocks right away in the game. We have a new stone bricks block in our example. To get those in game, we have to smelt cobblestone to stone and then craft 4 bricks using 4 stones. 

The needed new `ModLoader` methods, `addRecipe` and `addSmelting`. 

## Crafting recipes

But there are some problems... First of all, `addRecipe` is not public, and the `CraftingManager` class is @#!! final so can't be extended, and the array `recipes` is private. I said I wanted to keep base classes edits to a bare minimum, so I'm going to give this a proper thinking.

Right now `GuiCrafting` creates a new `CraftingManager` instance, which adds all needed recipes to itself, and calls `findMatchingRecipe`. I have two alternatives:

1.- Making `CraftingManager` recipe list `static`, adding all recipes staticly, then change how `GuiCrafting` accesses it. 
2.- Making my own `ModCraftingManager` and patch `GuiCrafting` to use mine as well.

I find that 2 would result on way less base class edits (just one). 1 would mean changing `CraftingManager` almost completely, and fixing crappy Notch code is not my job (in this project, at least). So I'll take the second approach. 

The original `CraftingManager` is used this way from `GuiCrafting`: 

```java
    this.iInventory.setInventorySlotContents(0, CraftingManager.getInstance().findMatchingRecipe(var1));
```

Where `var1` is a 9 items array containing item IDs or -1 for empty slots. `findMatchingRecipe` returns an `ItemStack` which is then placed to the inventory at slot 0. I think I can still use the providede `CraftingRecipe` to implement my `ModCraftingManager` class.

First attempt:

```java
    package com.mojontwins.modloader;

    import java.util.ArrayList;
    import java.util.HashMap;
    import java.util.List;

    import net.minecraft.game.block.Block;
    import net.minecraft.game.item.Item;
    import net.minecraft.game.item.ItemStack;
    import net.minecraft.game.recipe.CraftingRecipe;

    public class ModCraftingManager {
        public static List<CraftingRecipe> recipes;
        
        static {
            recipes = new ArrayList<CraftingRecipe> ();
        }

        public ModCraftingManager() {
            // TODO Auto-generated constructor stub
        }

        public static void addRecipe(ItemStack itemStack, Object obj []) {

            /*
             * "obj" parameters are a sequence that goes this way:
             * - up to three strings OR one string array
             * - then a sequence of char, [item|block]
             */

            int paramIndex = 0;

            /* 
             * First task is concatenate everything to a string and determine the size of the recipe
             */

            String recipeString = "";        
            int recipeWidth = 0;
            int recipeHeight = 0;
            
            if (obj[0] instanceof String[]) {
                // Recipe comes in an array of String

                ++paramIndex;

                String[] recipeArray = (String[])obj[0];
                recipeHeight = recipeArray.length;

                // Concatenate recipe:
                for(int i = 0; i < recipeHeight; ++i) {
                    if (recipeWidth < recipeArray [i].length ()) recipeWidth = recipeArray [i].length ();
                    recipeString = recipeString + recipeArray [i];
                }
            } else {
                // Recipe comes in separate String parameters

                while(obj[paramIndex] instanceof String) {
                    String recipeRow = (String)obj[paramIndex++];
                    ++recipeHeight;
                    if (recipeWidth < recipeRow.length ()) recipeWidth = recipeRow.length();
                    recipeString = recipeString + recipeRow;
                }
            }

            // Now read the list of ingredients

            HashMap<Character,Integer> ingredients = new HashMap<Character,Integer>();
            
            for(; paramIndex < obj.length; paramIndex += 2) {
                Character recipeChar = (Character)obj[paramIndex];
                int blockOrItemID = 0;
                if (obj[paramIndex + 1] instanceof Item) {
                    blockOrItemID = ((Item)obj[paramIndex + 1]).shiftedIndex;
                } else if (obj[paramIndex + 1] instanceof Block) {
                    blockOrItemID = ((Block)obj[paramIndex + 1]).blockID;
                }

                ingredients.put(recipeChar, blockOrItemID);
            }

            // And create an integer array ready to use

            int[] recipeIds = new int[recipeWidth * recipeHeight];

            for(int i = 0; i < recipeWidth * recipeHeight; ++i) {
                char c = recipeString.charAt(i);
                if (ingredients.containsKey(c)) {
                    recipeIds[i] = (Integer)ingredients.get(c);
                } else {
                    recipeIds[i] = -1;
                }
            }

            recipes.add(new CraftingRecipe(recipeWidth, recipeHeight, recipeIds, itemStack));
        }
        
        public static ItemStack findMatchingRecipe(int[] recipeIds) {
            for(int i = 0; i < recipes.size(); ++i) {
                CraftingRecipe var3 = (CraftingRecipe)recipes.get(i);
                if (var3.matchRecipe(recipeIds)) {
                    return var3.createResult();
                }
            }

            return null;
        }
    }
```

So here comes our base class edit: in `GuiCrafting`, instead of this:

```java
    this.iInventory.setInventorySlotContents(0, CraftingManager.getInstance().findMatchingRecipe(var1));
```

We oughta do this:

```java
    ItemStack itemStack = CraftingManager.getInstance().findMatchingRecipe(var1);
    if (itemStack == null) {
        itemStack = ModCraftingManager.findMatchingRecipe(var1);
    }

    this.iInventory.setInventorySlotContents(0, itemStack);
``` 

So now we can add the new method to `ModLoader`:

```java
    public static void addRecipe (ItemStack itemStack, Object obj []) {
        ModCraftingManager.addRecipe(itemStack, obj);
    }
```

And this, to `mod_Example`:

```java
    ModLoader.addRecipe(new ItemStack(blockStoneBricks, 4), new Object [] {
        "XX", "XX",
        'X', Block.stone
    });
```

Which seems to compile and run without problems (can't test it yet until we have smelting on).

## Smelting recipes

Smelting stuff is embedded inside `TileEntityFurnace` in this version of Minecraft. So no `FurnaceRecipes` class like in 1.2.5, I'm afraid. After some study, I've discovered, in awe, that all there is is this method:

```java
    private static int smeltItem(int var0) {
        if (var0 == Block.oreIron.blockID) {
            return Item.ingotIron.shiftedIndex;
        } else if (var0 == Block.oreGold.blockID) {
            return Item.ingotGold.shiftedIndex;
        } else if (var0 == Block.oreDiamond.blockID) {
            return Item.diamond.shiftedIndex;
        } else if (var0 == Block.sand.blockID) {
            return Block.glass.blockID;
        } else if (var0 == Item.porkRaw.shiftedIndex) {
            return Item.porkCooked.shiftedIndex;
        } else {
            return var0 == Block.cobblestone.blockID ? Block.stone.blockID : -1;
        }
    }
```

Everything is hardcoded. Great (not). There's no way I can add more smelting recipes without editing the `TileEntityFurnace` base class. We can do something like this:

```java
    private static int smeltItem (int var0) {
        int result = ModFurnaceRecipes.smeltItem (var0); if (result != -1) return result;

        [...]
    }
```

And have this `ModFurnaceRecipes` class:
```java
    package com.mojontwins.modloader;

    import java.util.ArrayList;
    import java.util.List;

    public class ModFurnaceRecipes {
        public static List<ModSmeltingRecipe> recipes;
        
        static {
            recipes = new ArrayList<ModSmeltingRecipe> ();
        }
        
        public ModFurnaceRecipes() {
        }

        public static void addSmeltingRecipe (int input, int output) {
            recipes.add(new ModSmeltingRecipe(input, output));
        }
        
        public static int smeltItem (int input) {
            for (int i = 0; i < recipes.size (); i ++) {
                ModSmeltingRecipe recipe = (ModSmeltingRecipe) recipes.get(i);
                if (recipe.input == input) return recipe.output;
            }
            
            return -1;
        }
    }
```

Which needs this `ModSmeltingRecipe` class:

```java
    package com.mojontwins.modloader;

    public class ModSmeltingRecipe {
        public int input;
        public int output;
        
        public ModSmeltingRecipe(int input, int output) {
            this.input = input; this.output = output;
        }
    }
```

Add the `addSmelting` method to `ModLoader`

```java
    public static void addSmelting (int input, int output) {
        ModFurnaceRecipes.addSmeltingRecipe(input, output);
    }
```

And so we can add the new recipe to `mod_Example`:

```java
    ModLoader.addSmelting(Block.cobblestone.blockID, Block.stone.blockID);
```

## Another small hook

I'm adding another hook which is ran when the game is about to start so we can give the player some cobblestone to test our stuff quickly.

`ModLoader`:

```java
    // This one runs right before the game starts
    public static void hookGameStart (Minecraft minecraft) {
        for (Iterator<BaseMod> iterator = modList.iterator(); iterator.hasNext();) {
            ((BaseMod)iterator.next()).hookGameStart(minecraft);
        }
    }
```

`BaseMod`:

```java
    public void hookGameStart (Minecraft minecraft) {       
    }
```

`mod_Example`:

```java
    public void hookGameStart (Minecraft minecraft) {
        minecraft.thePlayer.inventory.setInventorySlotContents(0, new ItemStack(Block.stoneOvenIdle, 1));
        minecraft.thePlayer.inventory.setInventorySlotContents(1, new ItemStack(Block.workbench, 1));
        minecraft.thePlayer.inventory.setInventorySlotContents(2, new ItemStack(Item.coal, 64));
        minecraft.thePlayer.inventory.setInventorySlotContents(3, new ItemStack(Block.cobblestone, 64));
    }
```

And the hook goes right after the level has been created and set up in `Minecraft`, at the end of the `generateLevel` method:

```java
    ModLoader.hookGameStart(this);
```

That way we can quickly create an oven and a crafting table, smelt some cobblestone, and craft our stone bricks! And yes, it works like a charm!

# Items

The main problem with items is that this version of Minecraft only supports `TextureFX`s for the `terrain.png` atlas, and not for `icons/items.png`. So against my will I've modified once again the bases classes, this time half-porting 1.2.5's version of the `TextureFX` class and the `updateDynamicTextures` method in `RenderEngine`:

```java
    package net.minecraft.client.renderer.block;

    import org.lwjgl.opengl.GL11;

    import net.minecraft.client.renderer.RenderEngine;

    public class TextureFX {
        public byte[] imageData;
        public int iconIndex;
        public boolean anaglyphEnabled;
        public int textureId;
        public int tileImage;

        public TextureFX(int var1) {
            imageData = new byte[1024];
            anaglyphEnabled = false;
            textureId = 0;
            tileImage = 0;
            iconIndex = var1;
        }

        public void onTick() {
        }
        
        public void bindImage (RenderEngine renderEngine) {
            if (tileImage == 0) {
                GL11.glBindTexture(GL11.GL_TEXTURE_2D, renderEngine.getTexture("/terrain.png"));
            } else {
                GL11.glBindTexture(GL11.GL_TEXTURE_2D, renderEngine.getTexture("/gui/items.png"));
            }
        }
    }
```

```java
    public final void updateDynamicTextures() {
        // This method has been modified to allow for TextureFX in both atlases (originally just `terrain.png`)
        
        int i = -1;
        
        for(int var1 = 0; var1 < this.textureList.size(); ++var1) {
            TextureFX textureFX = (TextureFX)this.textureList.get(var1);
            textureFX.anaglyphEnabled = this.options.anaglyph;
            textureFX.onTick();
            this.imageData.clear();
            this.imageData.put(textureFX.imageData);
            this.imageData.position(0).limit(textureFX.imageData.length);
            
            if (textureFX.iconIndex != i) {
                textureFX.bindImage (this);
                i = textureFX.iconIndex;
            }
            
            GL11.glTexSubImage2D(GL11.GL_TEXTURE_2D, 0, textureFX.iconIndex % 16 << 4, textureFX.iconIndex / 16 << 4, 16, 16, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, this.imageData);
        }

        // I dunno what's this for but I will just leave it here...
        
        for(int var1 = 0; var1 < this.textureList.size(); ++var1) {
            TextureFX textureFX; 
            if ((textureFX = (TextureFX)this.textureList.get(var1)).textureId > 0) {
                this.imageData.clear();
                this.imageData.put(textureFX.imageData);
                this.imageData.position(0).limit(textureFX.imageData.length);
                GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureFX.textureId);
                GL11.glTexSubImage2D(GL11.GL_TEXTURE_2D, 0, 0, 0, 16, 16,  GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, this.imageData);
            }
        }
    }
```

And change `ModTextureStatic` to properly give `TextureFX`'s `iconIndex` a value:

```java
    tileImage = textureAtlas == EnumTextureAtlases.ITEMS ? 1 : 0;
```

Run and it still works, so we're fine for the moment.

So let's get on to it. First of all we have to create the array to serve free texture Ids for items. Looking at `/gui/items.png` we get this:

```java
    // A map for free / used item texture indexes
    private static final int [] itemTextureIndexes = new int [] {
        1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0,
        1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 0, 0, 0, 0, 0,
        1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0,
        1, 1, 1, 1, 1, 1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0,
        1, 1, 1, 1, 1, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0,
        1, 1, 1, 1, 1, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0,
        1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
    };
    
    // indexes the previous array
    public static int currentItemTextureIndex;
```

About item IDs, let's take a glance at the base `Item` class to design our sequencer. Let's remember that I plan to add a registry to store this kind of stuff in the world files so everything can be setup automaticly if needed, and also that you don't need to use the sequencers at all if you don't fancy them.

In this version, Item id's (not shifted indexes, those add 256 to the value for items) are sequentially used from 0 to 65 inclusive, so we make yet another stupid sequencer which can / should be replaced for some proper thing, as in blocks. But I will leave that for the future. So be it:

```java
    public static int getFreeItemTextureIndex () {
        for (; currentItemTextureIndex < 256; currentItemTextureIndex ++) {
            if (itemTextureIndexes [currentItemTextureIndex] == 0) {                
                itemTextureIndexes [currentItemTextureIndex] = 1;
                return currentItemTextureIndex ++;
            }
        }
        
        return -1;
    }   

    public static int getBlockId () throws Exception {
        if (currentFreeBlockId < 256)
            return currentFreeBlockId ++;
        else throw new Exception ("No more free item IDs.");
    }
```

So the next step is setting up the texture overriding process by completing our `addOverride` method:

```java
    public static int addOverride (EnumTextureAtlases textureAtlas, String textureURI) {
        int textureIndex; 
        
        if (textureAtlas == EnumTextureAtlases.TERRAIN) {
            textureIndex = getFreeTerrainTextureIndex ();
        } else {
            textureIndex = getFreeItemTextureIndex ();
        }
        
        Boolean success = addOverride (textureAtlas, textureURI, textureIndex);
        
        return success ? textureIndex : -1;
    }
```

Everything else should work alrighty. Now let's see how we could create our own items. As methods are public we can just inherit directly from Item... *but* planning ahead, I'll need item names for my future registry system. So let's add a `ModItem` class which extends `Item` to store what we need - and we'll then use `ModItem` to base our custom items.

```java
    package com.mojontwins.modloader;

    import net.minecraft.game.item.Item;

    public class ModItem extends Item {
        public String name;

        public ModItem(int var1) {
            super(var1);
        }
        
        public ModItem setMaxStackSize(int var1) {
            maxStackSize = var1;
            return this;
        }
        
        public ModItem setMaxDamage(int var1) {
            maxDamage = var1;
            return this;
        }
        
        public ModItem setName(String name) {
            this.name = name;
            return this;
        }
    }
```

Let's create a simple, useless and stupid item: a pebble. Using 9 pebbles you get a block of cobblestone (so we can test if everything works together). We are not even creating a custom class for it. In our `mod_Example`, we first create a new attribute for it:

```java
    public static ModItem itemPebble;
```

Then create the object in `load` and assign a texture:

```java
    itemPebble = new ModItem(ModLoader.getItemId()).setMaxStackSize(1);
    itemPebble.setIconIndex(ModLoader.addOverride(EnumTextureAtlases.ITEMS, "textures/item_pebble.png"));
```

Now add the crafting recipe:

```java
    ModLoader.addRecipe(new ItemStack(Block.cobblestone, 1), new Object [] {
        "XXX", "XXX", "XXX",
        'X', itemPebble
    });
```

Let's give us some useless pebbles so we can test:

```java
    minecraft.thePlayer.inventory.setInventorySlotContents(4, new ItemStack(itemPebble, 64));
```

And it works! Just put the new ugly pebbles in the crafting table to make cobblestone!
