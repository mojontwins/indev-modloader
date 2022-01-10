# Diary

# Roadmap

This is the roadmap which will be constantly changing.

* Create a basic ModBase class and make the system to automaticly run mod_Name classes.
* [x] Basic ModBase class and mod_XXX importing.
* [x] Use your mod class to add blocks.
* [x] Use your mod class to add items.
* [x] Use your mod class to add recipes of any kind.
* [x] Use your mod to add armor
* [x] Render blocks using custom renderers.
* [x] Use your mod class to add food.
* [x] Use your mod class to add tile entities.
* [x] Use your mod class to add entities.
* [x] Use your mod class to add new kind of terrain generation.
* [x] Use your mod class to add structures.
* [ ] Make clean ZIP with just ModLoader to modify the obfuscated jar.
* [ ] Make delta patch with just ModLoader to convert RetroMCP decompiled code to fixed Indev + ModLoader.
* [ ] Automate the two previous points so I don't have to do the work.

# TODOs

So I don't forget:

* [x] I will later reimplement the Block ID system using some kind of registry that gets saved alognside worlds so IDs are reassigned when loading worlds.
* [ ] Save / restore & translate level theme ID when saving a theme
* [ ] Save slots & using folders so I can
* [ ] Implement pseudo dimensions by binding several .mcmap's together.
* [x] Implement animations with local atlases via custom Texture FX
* [x] Custom fuel
* [x] Engine fix - Prevent eating food when right-clicking tile entities!
* [x] Engine fix - Correct bug that makes the indev house not spawn.
* [x] Engine fix - Arrows less harmful.
* [x] Engine addition - Shift to crouch - do not fall from ledges.
* [ ] Engine addition - Right click with sword to cover up.
* [ ] Theme based "generate structures".
* [ ] Custom "Raising / Soiling": Cave-like levels (supporting deep levels with 4 stacked sub-levels) to make the Nether like in early PE.

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

Hell, they are marked as `final` which means I cannot override them! They are not final in the versions I know (b1.7.3, r1.2.5). I guess I'll have to try a different approach with slightly renamed methods. I'm going to try with this class:

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

And here comes the hard part: overriding the texture. I say hard as I don't fully understand it yet. I think it is done, in r1.2.5, via a `TextureFX`. There are `TextureFX`s in Indev so I'll try and add it that way. Cross yer fingers.

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

All we have done is adding a HashMap to a List. Something has to be done, actually. In Risugami's ModLoader, the method in charge is `registerAllTextureOverrides`, which processes the list and does its magic. In the original ModLoader I'm using as a model (r1.2.5), this is done in the `onTick` method which is called from EntityRendererProxy (via a base class edit). Maybe my approach is wrong, we'll see with time, but I guess I can just add a call after my `init` method has run, as all `mod_XXX` classes have been loaded and all `load` methods have been called, thus all overrides are in order. So let's take this path, for the moment. So a new addition to `Minecraft.java` (with a slight modification to what we had before):

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
TODO: Implement animations with local atlases via custom Texture FX

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

Smelting stuff is embedded inside `TileEntityFurnace` in this version of Minecraft. So no `FurnaceRecipes` class like in r1.2.5, I'm afraid. After some study, I've discovered, in awe, that all there is is this method:

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

### Custom fuel 

TODO

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

The main problem with items is that this version of Minecraft only supports `TextureFX`s for the `terrain.png` atlas, and not for `icons/items.png`. So against my will I've modified once again the bases classes, this time half-porting r1.2.5's version of the `TextureFX` class and the `updateDynamicTextures` method in `RenderEngine`:

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
        1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 1,
        1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 0, 0, 0, 0, 1,
        1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 1,
        1, 1, 1, 1, 1, 1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 1,
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

Note that you still can override the methods that are called when you left or right click when the item is selected:

```java
    boolean onItemUse (ItemStack itemStack, World world, int x, int y, int z, int blockIDhit);
    boolean onItemRightClick (ItemStack itemStack, World, world, EntityPlayer entityPlayer);
```

If you redefine those just return true on success so the default action is not performed.

Let's create a simple, useless and stupid item: a pebble. Using 9 pebbles you get a block of cobblestone (so we can test if everything works together). We are not even creating a custom class for it. In our `mod_Example`, we first create a new attribute for it:

```java
    public static ModItem itemPebble;
```

Then create the object in `load` and assign a texture:

```java
    itemPebble = new ModItem(ModLoader.getItemId()).setMaxStackSize(64).setName("item.pebble");
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

## New tools

Tools are based on a material and a type. Upon those parameters, they can be used more or less effectively in the world & entities. As Indev is hardcodedfest, I'm expecting nasty stuff ahead. Let's examine how it's done and think about how can be expand on that in an easy way. Maybe we can also add special stuff like silk touch to golden tools? I love that feature in NSSS. But let's not get our hopes very high...

The classes `ItemAxe`, `ItemPickaxe` and `ItemSpade` all extend the base `ItemTool` class - *note how `ItemSword` and `ItemHoe` don't* - So let's start poking at that. This is the constructor:

```java
    public ItemTool(int var1, int var2, int var3, Block[] var4)
```

Where
    * `var1` is the ID (get's passed on to `super` which is `Item`).
    * `var2` seems to be a base damage value when hitting entities. Get's added to `var3` to make `damageVsEntity`.
    * `var3` seems to be the hardness, and is usually 0 for wood, 1 for rock, 2 for steel and 3 for diamond. It's used to calculate `maxDamage` or how many times you can use the tool ?
    * `var4` is an array of blocks which gets copied to `blocksEffectiveAgainst`.

`blocksEffectiveAgainst` is a list of blocks the tool is good at breaking. If the block being hit is in the list, the strength applied is `(var3 + 1) * 2`, this is, 2.0F for wood, 4.0F for stone, 6.0F for steel and 8.0F for diamond. It it's not, the strength is 1.0F.

Hmmm - this seems way more hackeable than what I know: in r1.2.5 there's a Enum to contains all the values which can't be easily modified programatically. All methods are marked `final` which just sucks and *I'm removing that so we can customize everything!*

Let's look at the actual tools which extend `ItemTool`:

### `ItemPickAxe`

The constructor has three parameters equivalent to the above `var1`, `var3` and `var4`. `var2` is set to `2`, so `damageVsEntity` happens to be `2 + var3`. The class has its own attribute `harvestLevel` which is also set to the value of `var3` (the 2nd parameter in the `ItemPickAxe` constructor) which is later used to calculate if the tool can harvest certain ores.

Sadly, the method `canHarvestBlock` is marked `final` which is just plain shyte. If I want to make my own tools, it would be great to be able to redefine this method. *So I'm removing the `final` modifier here as well*. Sorry but not sorry.

Finally, `blocksEffectiveAgainst` is set to everything rocky or stoney in the game.

Now we can extend `ItemPickAxe` to create our own pickaxes.

### `ItemAxe`

This one is much simpler. It sets `ItemTool`'s constructor `var2` to 3 so `damageVsEntity` is `3 + var3` - Axes are stronger than Pickaxes against mobs. Appart from that, `blocksEffectiveAgainst` contains everything that's made of wood.

### `ItemSpade`

Same as `ItemAxe`, but with `var2` set to 1 and affective agains grass, sand, dirt and gravel.

### `ItemSword`

As mentioned, is not considered a tool, at least in the class hyerarchy. And the reason why is "because", as it could have been implemented as a `ItemTool`. Maybe there's a obscure reason I can't understand. Maybe it's because `ItemTool` methods are `final`.  `ItemSword`'s are also `final`. *I'm removing all `final`s!*.

Swords just take two parameters: the ID and a `var2` parameter which is used for `maxDamage` (`32 << var2`) and `weaponDamage` (4 + (var2 << 1)). `weaponDamage` is used as a returning value for `getDamageVsEntity`, so the sword causes a damage of 6, 8, 10 or 12 depending on its material.

### `ItemHoe`

Hoes have code to plow land - that is, they override `onItemUse`.

### Creating new tools with ModLoader

So after I've removed all those `final`s we may be talking. Let's try and create a couple of steel tools: a steel pickaxe and a steel sword. We'll be using new classes which will extend base classes. We'll set all the custom values there. We'll also add the `name` attribute and the `setName` method for or future registry thing.

Tool duration and tool strength are very crudely configured from the constructor, so we will be overriding the values explictly by redefining some methods. Let's begin with our steel pickaxe.

I want the new steel pickaxe to be as strong as the iron pickaxe but 1.5 times as durable, and let's say 1.5 times faster too. Remember that the `ItemPickaxe` constructor can be expressed as:

```java
    ItemPickaxe (int itemID, int hardness);
```

and that `hardness` is 0 for wood and gold, 1 for stone, 2 for iron and 3 for diamond and is used to calculate several things:

* `harvestLevel`, the same value.
* `maxDamage`, equals `32 << hardness`, tool duration.
* `efficiencyOnProperMaterial` is `(hardness + 1) * 2`.
* `damageVsEntity` which is `2 + hardness` for pickaxes.

So we can extend from `ItemPickaxe`, call the `super` constructor with hardness = 2 (which means `iron`), and then recalculate `maxDamage` and `efficiencyOnProperMaterial`. 

* `maxDamage` for iron would be `32 << 2` = 256. 1.5 times more durable would be 384.
* `efficiencyOnProperMaterial` is `(2 + 1) * 2` = 6.0F, 1.5 times as fast would be 9.0F.

```java
    package com.mojontwins.modloader;

    import net.minecraft.game.item.ItemPickaxe;

    public class ItemSteelPickaxe extends ItemPickaxe {
        public String name;
        
        public ItemSteelPickaxe(int itemID) {
            super (itemID, 2);
            maxDamage = 384;
            efficiencyOnProperMaterial = 9.0F;
            maxStackSize = 1;
        }
        
        public ItemSteelPickaxe setName(String name) {
            this.name = name;
            return this;
        }   
    }
```

Our new steel sword should be 1.5 as durable as an iron sword and also 1.5 times more powerful. The `ItemSword` constructor can be expressed as

```java
    ItemSword (int itemID, int hardness);
```

Again, `hardness` is 0 for wood and gold, 1 for stone, 2 for iron and 3 for diamond and is used to calculate several things:

* `maxDamage` is `32 << hardness`, tool duration.
* `weaponDamage` is `4 + hardness * 2` (integer value).

That way we can extend from `ItemSword`, call the `super` constructor with hardness = 2 (in fact this doesn't matter in this case as we'll be overwriting everything!) and then recalculate `maxDamage` and `weaponDamage`:

* `maxDamage` for iron would be `32 << 2` = 256. 1.5 times more durable would be 384.
* `weaponDamage` would be `4 + 2 * 2` = 8. 1.5 times is 12.

```java 
    package com.mojontwins.modloader;

    import net.minecraft.game.item.ItemSword;

    public class ItemSteelSword extends ItemSword {
        public String name;
        
        public ItemSteelSword(int itemID) {
            super (itemID, 2);
            maxDamage = 384;
            weaponDamage = 12;
            maxStackSize = 1;
        }
        
        public ItemSteelSword setName(String name) {
            this.name = name;
            return this;
        }   
    }
```

Now add the items to mod_Example:

```java
    public static ItemSword itemSteelSword;
    public static ItemPickaxe itemSteelPickaxe;
```

```java
    itemSteelSword = new ItemSteelSword(ModLoader.getItemId()).setName("item.steel_sword");
    itemSteelSword.setIconIndex(ModLoader.addOverride(EnumTextureAtlases.ITEMS, "textures/item_steel_sword.png"));
    
    itemSteelPickaxe = new ItemSteelPickaxe(ModLoader.getItemId()).setName("item.steel_pickaxe");;
    itemSteelPickaxe.setIconIndex(ModLoader.addOverride(EnumTextureAtlases.ITEMS, "textures/item_steel_pickaxe.png"));
```

Give one of each to test

```java
    minecraft.thePlayer.inventory.setInventorySlotContents(5, new ItemStack(itemSteelSword, 1));
    minecraft.thePlayer.inventory.setInventorySlotContents(6, new ItemStack(itemSteelPickaxe, 1));
```

To make things more fun let's create a new Item: "Steel Ingot", and a smelting recipe in which you can smelt iron ingots to get steel ingots:

```java
    public static ModItem itemSteelIngot;
```

```java
    itemSteelIngot = new ModItem(ModLoader.getItemId()).setName("item.steel_ingot");
    itemSteelIngot.setIconIndex(ModLoader.addOverride(EnumTextureAtlases.ITEMS, "textures/item_steel_ingot.png"));
```

```java
    ModLoader.addSmelting(Item.ingotIron.shiftedIndex, itemSteelIngot.shiftedIndex);
```

And two crafting recipes to create the steel sword and the steel pickaxe:

```java
    ModLoader.addRecipe(new ItemStack(itemSteelSword,1), new Object [] {
        " # ", " # ", " X ",
        '#', itemSteelIngot,
        'X', Item.stick
    });

    ModLoader.addRecipe(new ItemStack(itemSteelPickaxe,1), new Object [] {
        "###", " X ", " X ",
        '#', itemSteelIngot,
        'X', Item.stick
    });
```

All I did in this section is writing a tutorial and modifying base classes. But I also tested everything (so far) is fairly robust.

### Silk touch tools?

Just for fun, I'm just intrigued to know if one could add stuff like this to items in this slightly modified Indev just by playing around with method overriding. The goal for this game: manage to subtitute the useless golden pickaxe for a silk touch pickaxe which just breaks the block but returns the original block untouched.

This is how Indev works:

* When you use your tool on a block and you break it, `PlayerControllerSP.sendBlockRemoved (int x, int y, int z)` is called.
* There, the block previously on (x, y, z) is retrieved and its `onBlockDestroyedByPlayer` is called, which does nothing except for blocks of class `BlockCrops`, `BlockFire` and `BlockTNT`.
* If the player has an item on its hand, such item's `onBlockDestroyed` method is called. This method actually damages tools and swords.
* If the call to `thePlayer.canHarvestBlock` returns true, the block's `dropBlockAsItem` is called.

`theplayer.canHarvestBlock` works as follows:

* If the block's material is not metal nor rock, it returns true. The block can always be harvested.
* If it is metal or rock, the held item's `canHarvestBlock` is called and its return value returned.

The `canHarvestBlock` from Items returns false, but is redefined by `ItemPickAxe` as we have seen.

SO

In our new golden pickaxe, first of all, `canHarvestBlock` should always return true. Then we would need to modify `PlayerControllerSP.sendBlockRemoved`, for example by adding a new hook.

This hool to `ModLoader`:

```java
    // Called when block has been harvested
    public static boolean hookOnBlockHarvested (Minecraft minecraft, World world, int x, int y, int z, int blockID, int metadata) {
        boolean res = false;
        for (Iterator<BaseMod> iterator = modList.iterator(); iterator.hasNext();) {
            res = res || ((BaseMod)iterator.next()).hookOnBlockHarvested(minecraft, world, x, y, z, blockID, metadata);
        }
        return res;
    }
```

`BaseMod`:

```java
    public boolean hookOnBlockHarvested (Minecraft minecraft, World world, int x, int y, int z, int blockID, int metadata) {
        return false;
    }    
```

And the modification to `PlayerControllerSP.sendBlockRemoved`:

```java
    if (var6 && this.mc.thePlayer.canHarvestBlock(Block.blocksList[var4])) {
        if (ModLoader.hookOnBlockHarvested (this.mc, this.mc.theWorld, var1, var2, var3, var4, var5) == false)  // This line
            Block.blocksList[var4].dropBlockAsItem(this.mc.theWorld, var1, var2, var3, var5);
    }
```

This is just the infrastructure. Now we have to:

* Create a class for our custom golden pickaxe.
* Hack into the main items list to *replace* the normal pickaxe with ours.
* Override `BaseMod`'s `hookOnBlockHarvested` to spawn the same block ID that's been destroyed. We'll copy some code from `Block.dropBlockAsItemWithChance`.

So this is our custom golden pickaxe:

```java
    package com.mojontwins.modloader;

    import net.minecraft.game.block.Block;
    import net.minecraft.game.item.ItemPickaxe;

    public class ItemSilkTouchGoldenPickaxe extends ItemPickaxe {
        public String name;

        public ItemSilkTouchGoldenPickaxe(int itemID) {     
            super(itemID, 1);
            
            // Make it faster than stone
            efficiencyOnProperMaterial = 9.0F;
            
            // Only can stack 1 per slot
            maxStackSize = 1;
        }

        // Override canHarvestBlock so we can harvest anything
        public boolean canHarvestBlock (Block var1) {
            return true;
        }
        
        // Override getStrVsBlock so it's always as efficient
        public float getStrVsBlock(Block var1) {
            return efficiencyOnProperMaterial;
        }
        
        public ItemSilkTouchGoldenPickaxe setName(String name) {
            this.name = name;
            return this;
        }   
    }
```

Let's define and instantiate it in our `mod_Example` class...

```java
    public static ItemPickaxe itemSilkTouchGoldenPickaxe;
```

```java
    itemSilkTouchGoldenPickaxe = new ItemSilkTouchGoldenPickaxe(ModLoader.getItemId()).setName("item.silk_touch_golden_pickaxe");
    itemSilkTouchGoldenPickaxe.setIconIndex(Item.pickaxeGold.getIconIndex());
```

Note how we are reusing the original golden pickaxe texture. And now there comes the hacky part:

```java
    // Substitute the original golden pickaxe:
    Item.pickaxeGold = itemSilkTouchGoldenPickaxe;
    Item.itemsList[Item.pickaxeGold.shiftedIndex] = itemSilkTouchGoldenPickaxe;
```

Now we'll add the actual hook code. For a first test we add this simple stub:

```java
    public boolean hookOnBlockHarvested (Minecraft minecraft, World world, int x, int y, int z, int blockID, int metadata) {
        System.out.println ("BIMMM!");
        return true;
    }   
```

Everytime you break a block it will be logged in the console, but nothing else will happen (well, the block is broken, but nothing spawns)

Now we give ourselves a golden pickaxe and test:

```java
    minecraft.thePlayer.inventory.setInventorySlotContents(7, new ItemStack(Item.pickaxeGold, 1));
```

It works, so let's do something in `hookOnBlockHarvested`: We detect if the tool used is the golden pickaxe and, if so, we spawn a new block item with the same blockID and return true; otherwise we return false and let the engine do its thing:

```java
    public boolean hookOnBlockHarvested (Minecraft minecraft, World world, int x, int y, int z, int blockID, int metadata) {
        ItemStack curItem = minecraft.thePlayer.inventory.getCurrentItem();
        if (curItem != null) {
            if (curItem.itemID == Item.pickaxeGold.shiftedIndex) {
                
                // This code is lifted from `Block.dropBlockAsItemWithChance`
                float px = world.random.nextFloat() * 0.7F + 0.15F;
                float py = world.random.nextFloat() * 0.7F + 0.15F;
                float pz = world.random.nextFloat() * 0.7F + 0.15F;
                EntityItem entityItem = new EntityItem(world, (float)x + px, (float)y + py, (float)z + pz, new ItemStack(blockID));
                entityItem.delayBeforeCanPickup = 10;
                world.spawnEntityInWorld(entityItem);
                
                return true;
            }
        }
        
        return false;
    }   
```

## New Armor

As we are dealing with items, and Armor pieces are items, let's see how we can add new armor.

In Indev, all armor items are instances of `ItemArmor`, which is a rather simple class. I've renamed the standard MCP identifiers to make it more understandable:

```java
    package net.minecraft.game.item;

    public class ItemArmor extends Item {
        private static final int[] damageReduceAmountArray = new int[]{3, 8, 6, 3};
        private static final int[] maxDamageArray = new int[]{11, 16, 15, 13};
        public final int armorType;
        public final int damageReduceAmount;
        public final int renderIndex;

        public ItemArmor(int itemID, int strength, int renderType, int type) {
            super(itemID);
            this.armorType = type;
            this.renderIndex = renderType;
            this.damageReduceAmount = damageReduceAmountArray[type];
            this.maxDamage = maxDamageArray[type] * 3 << strength;
            this.maxStackSize = 1;
        }
    }
```

* `strength` is used to calculate the amount of damage each armor piece resists before breaking. Each type of armor piece takes a fixed base amount of damage which is then multiplied by 3 raised to the power of 'strength'. Strength seems to be:
    * 0 for leather (named 'cloth'),
    * 1 for chain,
    * 2 for iron,
    * 3 for diamond and
    * 1 for gold

* `renderType` is used by the renderer to index (0-4) this array which is used to select which texture is used to render the armor pieces on the payer (defined and used in `RenderPlayer`:

```java
    private static final String[] armorFilenamePrefix = new String[]{"cloth", "chain", "iron", "diamond", "gold"};
```

* `type` is:
    * 0 Helmet
    * 1 Chest
    * 2 Leggins
    * 3 Boots

Looking at the code and understanding those values, we can deduce that:

* The amount of damage an armour piece takes is fixed and doesn't depend on the material, only on the type, and is defined by the array `damageReduceAmountArray`.
* The material only define how long the damage lasts before breaking.
* Gold and chain armor pieces are the same.

In order to add new kinds of armor, with completely customizable stats, and custom graphics, would need extending `ItemArmor` and assigning custom values to `damageReduceAmount` and `maxDamage`, plus adding new items to the `armorFilenamePrefix` array we can index with new `renderType` values. ModLoader should provide a method to add items to this array and retrieve the new item index so we can store it in our class.

Once we remove all the `final`s in `ItemArmor`, The new class for custom armor pieces could be:

```java
    package com.mojontwins.modloader;

    import net.minecraft.game.item.ItemArmor;

    public class ModItemArmor extends ItemArmor {
        public String name;

        public ModItemArmor (int itemID, int damageReduceAmount, int maxDamage, int renderType, int type) {
            super (itemID, 0, 0, type);     // 0, 0, because we are overwriting:
            this.renderIndex = renderType;
            this.damageReduceAmount = damageReduceAmount;
            this.maxDamage = maxDamage;
        }

        public ModItemArmor setName (String name) {
            this.name = name;
            return this;
        }
    }
```

The hard part would be being able to use our own texture for the model as the array containing the filename prefixes is static, final and private. The original Risugami's Modloader `arrArmor` does just that: adds the filename prefix and returns the index we want using some Java magic. We well be using Reflect to actually accessing the attributes of `RenderPlayer`. This needs some setup:

```java
    private static Field field_armorList = null;
    private static Field field_modifiers = null;
```

Then this, in init:

```java
    field_armorList = (net.minecraft.client.renderer.entity.RenderPlayer.class).getDeclaredFields()[3];
    field_modifiers.setInt(field_armorList, field_armorList.getModifiers() & 0xffffffef);
    field_armorList.setAccessible(true);
```

Let's pause for a bit and try to understand what this code does. Take a look at how `RenderPlayer` is defined:

```java
    public final class RenderPlayer extends RenderLiving {
        private ModelBiped modelBipedMain;
        private ModelBiped modelArmorChestplate;
        private ModelBiped modelArmor;
        private static final String[] armorFilenamePrefix = new String[]{"cloth", "chain", "iron", "diamond", "gold"};

        // etc

    }
```

Our goal is getting to add stuff to `armorFilenamePrefix`. If we count the attributes (starting at 0), then 0 is `modelBipedMain`, 1 is `modelArmorChestPlate`, 2 is `modelArmor` and 3 is `armorFilenamePrefix`. So the first line of code,

```java
    field_armorList = (net.minecraft.client.renderer.entity.RenderPlayer.class).getDeclaredFields()[3];
```

is selecting the declared field number '3' from `RenderPlayer`, this is, `armorFilenamePrefix`. The next line is changing the modifiers by resetting the modifier which equals 0x10 (& 0xffffffef will reset bit 4). Java field modifiers are:

|Flag Name|Value|Interpretation
|---------|-----|--------------
|ACC_PUBLIC|0x0001|Declared public; may be accessed from outside its package.
|ACC_PRIVATE|0x0002|Declared private; accessible only within the defining class.
|ACC_PROTECTED|0x0004|Declared protected; may be accessed within subclasses.
|ACC_STATIC|0x0008|Declared static.
|ACC_FINAL|0x0010|Declared final; must not be overridden (§5.4.5).
|ACC_SYNCHRONIZED|0x0020  Declared synchronized; invocation is wrapped by a monitor use.
|ACC_BRIDGE|0x0040|A bridge method, generated by the compiler.
|ACC_VARARGS|0x0080|Declared with variable number of arguments.
|ACC_NATIVE|0x0100|Declared native; implemented in a language other than Java.
|ACC_ABSTRACT|0x0400| Declared abstract; no implementation is provided.
|ACC_STRICT|0x0800|Declared strictfp; floating-point mode is FP-strict.
|ACC_SYNTHETIC|0x1000|Declared synthetic; not present in the source code.

So the second line of code is removing the `final` modifier from `armorFilenamePrefix`. The third and last line just makes the method accesible, overriding a possible `protected` or `private`.

Having all this setup, we can write an `addArmor` method which gets a String with a new filename prefix, adds it to `RenderPlayer.armorFilenamePrefix`, and retuns its index. This code is almost the same as Risugami's:

```java
    public static int addArmor(String s) throws Exception {
        
        // Gets a copy of the `armorFilenamePrefix` array in a list
        String as[] = (String[])field_armorList.get(null);
        List<String> list = Arrays.asList(as);
        ArrayList<String> arraylist = new ArrayList<String>();
        arraylist.addAll(list);

        // Make sure it's not been added yet
        if (!arraylist.contains(s)) {
            arraylist.add(s);
        }

        // Now return an index
        int i = arraylist.indexOf(s);
        
        // And substitute the original static array for the modified one
        field_armorList.set(null, ((Object)(arraylist.toArray(new String[0]))));
        
        return i;
    }
```

Using all this, we are going to add a full Steel Armor which is 1.5 the stats of the Iron armor. If you have followed me until this point you'll be able to work out the numbers. We've also added `steel_1.png` and `steel_2.png` with the new textures to an `/armor` folder in our project, and the new item textures to our textures folder.

```java
    public static ModItemArmor itemSteelHelmet;
    public static ModItemArmor itemSteelChest;
    public static ModItemArmor itemSteelLeggins;
    public static ModItemArmor itemSteelBoots;
```

```java 
    int steelRenderType = ModLoader.addArmor("steel");
    
    itemSteelHelmet = new ModItemArmor(ModLoader.getItemId(), 4, 149, steelRenderType, 0);
    itemSteelHelmet.setIconIndex(ModLoader.addOverride(EnumTextureAtlases.ITEMS, "textures/item_steel_helmet.png"));
    
    itemSteelChest = new ModItemArmor(ModLoader.getItemId(), 9, 216, steelRenderType, 1);
    itemSteelHelmet.setIconIndex(ModLoader.addOverride(EnumTextureAtlases.ITEMS, "textures/item_steel_chest.png"));
    
    itemSteelLeggins = new ModItemArmor(ModLoader.getItemId(), 6, 202, steelRenderType, 2);
    itemSteelHelmet.setIconIndex(ModLoader.addOverride(EnumTextureAtlases.ITEMS, "textures/item_steel_legs.png"));

    itemSteelBoots = new ModItemArmor(ModLoader.getItemId(), 3, 175, steelRenderType, 3);
    itemSteelBoots.setIconIndex(ModLoader.addOverride(EnumTextureAtlases.ITEMS, "textures/item_steel_boots.png")); 
```

and

```java
    ModLoader.addRecipe(new ItemStack(itemSteelHelmet, 1), new Object [] {
        "###", "# #", "   ",
        '#', itemSteelIngot
    });
        
    ModLoader.addRecipe(new ItemStack(itemSteelChest, 1), new Object [] {
        "# #", "###", "###",
        '#', itemSteelIngot
    });
    
    ModLoader.addRecipe(new ItemStack(itemSteelLeggins, 1), new Object [] {
        "###", "# #", "# #",
        '#', itemSteelIngot
    });
        
    ModLoader.addRecipe(new ItemStack(itemSteelBoots, 1), new Object [] {
        "# #", "# #",
        '#', itemSteelIngot
    });
```

## Food

Food items can be eaten and restore health. They extend `ItemFood` and call the constructor with `itemID, healAmount`. `healAmount` is expressed in half hearts. The base `ItemFood` class overrides the `onItemRightClick` method with this code:

```java
    public ItemStack onItemRightClick(ItemStack var1, World var2, EntityPlayer var3) {
        --var1.stackSize;
        var3.heal(this.healAmount);
        return var1;
    }    
```

`ItemSoup`, which extends `ItemFood`, in turns overrides it as well with this:

```java
    public ItemStack onItemRightClick(ItemStack var1, World var2, EntityPlayer var3) {
        super.onItemRightClick(var1, var2, var3);
        return new ItemStack(Item.bowlEmpty);
    }
```

So getting new food to Indev is pretty straightforward. If you can do with any of these implementations, just make a new food item using one of those classes. If you need further customization, extend from `ItemFood` and override `onItemRightClick`, modifying the `ItemStack` as needed. Note how you get the itemstack, the world, and the player entity. There's plenty of stuff you can do with those. For example, you could get poisoned if you ate raw chicken - but in Indev the player can't be poisoned.

It would be cool to add this status to the player entity and also have raw chicken or rotten flesh activate it in Indev. Let's see how we could get this to work.

In minecraft r1.2.5 this is implemented with potions. Items have a `setPotionEffect` method to set a `potionEffect` attribute. Items here have an `onFoodEaten` method which call `EntityPlayer`'s `addPotionEffect` if any potion effect. This adds the potion to `activePotionsMap`.

Each `EntityLiving` call to `updatePotionEffects` in `onEntityUpdate`. This method iterates the `activePotionsMap` and call each `potionEffect` `opUpdate` method which will return `false` if the effect has finished. This causes a call to `onFinishedPotionEffect`. 

`PottionEffect`'s `onUpdate` method does the magic: it decrease the duration of the effect. If duration > 0 it calls the `Potion`'s `isReady` method (which does some trickery with time ticks) and if it is, calls `Potion`'s `performEffect` which acts depending on the potion id. For the `hunger` effect, it just adds exhaustion to the player.

It would be great including something simmilar but avoiding potions - player's status. I could code all the needed classes and include them with modloader, then patch the base classes here and there, and have items which poison the player or give it extra abilities.

Yeah I added some stuff:

* `net.minecraft.game.entity.EntityLiving.Status` - extend from this class to make your status. We'll be using this to create the "poisoned" status. 
* `net.minecraft.game.entity.EntityLiving.StatusEffect` - the actual effect of the status, which controls the duration and an amplifier. You create an instance of this class and then add it to the entity.
* Additions to `EntityLiving` so you can add statuses. In each tick, the active `StatusEffect`s are iterated and run. This will decrease their duration, and then call the associated `Status`' `performEffect` method if `isReady` returns true. I've also added code so the status effects are saved alongside entities in the level.

We will eventually patch the main classes more and more to provide hooks that can be used for new, more complex statuses.

### Let's play

So let's add a *poisoned* status to our game...

```java 
    package com.mojontwins.modloader.entity.status;

    import net.minecraft.game.entity.Entity;
    import net.minecraft.game.entity.EntityLiving;
    import net.minecraft.game.entity.monster.EntityZombie;

    public class StatusPoisoned extends Status {
        public StatusPoisoned(int id, boolean isBadEffect) {
            super(id, true);
        }

        public void performEffect (EntityLiving entityLiving, int amplifier) {
            // Decrease half a heart
            if (entityLiving.health > 1) {
                entityLiving.attackEntityFrom((Entity)null, 1);
            }
        }
        
        public boolean isReady (int tick, int amplifier) {
            // Run every 5 ticks
            return (tick % 5) == 0;
        }
        
        public boolean isApplicableTo (EntityLiving entityLiving) {
            // Zombies can't be poisoned
            return !(entityLiving instanceof EntityZombie);
        }
    }
```

Instantiate it in `mod_Example.load`:

```java
    statusPoisoned = new StatusPoisoned(Status.getNewStatusId(), true);
```

Now we have to add a new `ItemFood` `ItemFoodRawChicken` with a custom `onItemRightClick` which activates the `statusPoisoned` status. Of course there will be absolutely no way to get Chicken food in this game... yet. But anyway.

```java
    package com.mojontwins.modloader;

    import com.mojontwins.modloader.entity.status.StatusEffect;

    import net.minecraft.game.entity.player.EntityPlayer;
    import net.minecraft.game.item.ItemFood;
    import net.minecraft.game.item.ItemStack;
    import net.minecraft.game.level.World;

    public class ItemFoodRawChicken extends ItemFood {
        public String name;

        public ItemFoodRawChicken(int itemID, int healAmount) {
            super(itemID, healAmount);
        }

        public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer entityPlayer) {
            itemStack.stackSize --;

            // Add status `poisoned` to player which lasts 50 ticks - that means a total of 5 hearts less
            entityPlayer.addStatusEffect(new StatusEffect(mod_Example.statusPoisoned.id, 50, 1));
            
            return itemStack;
        }
        
        public ItemFoodRawChicken setName (String name) {
            this.name = name;
            return this;
        }
    }

    package com.mojontwins.modloader;

    import net.minecraft.game.entity.player.EntityPlayer;
    import net.minecraft.game.item.ItemFood;
    import net.minecraft.game.item.ItemStack;
    import net.minecraft.game.level.World;

    public class ItemFoodCookedChicken extends ItemFood {
        public String name;

        public ItemFoodCookedChicken(int itemID, int healAmount) {
            super(itemID, healAmount);
        }

        public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer entityPlayer) {
            // Remove poisoned status
            entityPlayer.removeStatusEffect(mod_Example.statusPoisoned.id);
            
            return super.onItemRightClick(itemStack, world, entityPlayer);
        }
        
        public ItemFoodCookedChicken setName (String name) {
            this.name = name;
            return this;
        }
    }
```

Create the items in `mod_Example`, assign graphics, add a smelting recipe, and give ourselves some raw chicken to test.

```java
    [...]

    public static ItemFoodRawChicken itemFoodRawChicken;
    public static ItemFoodCookedChicken itemFoodCookedChicken;

    public void load () throws Exception {

        [...]

        // New food
        itemFoodRawChicken = new ItemFoodRawChicken(ModLoader.getItemId(), 0);
        itemFoodRawChicken.setIconIndex(ModLoader.addOverride(EnumTextureAtlases.ITEMS, "textures/item_chicken_raw.png"));
        
        itemFoodCookedChicken = new ItemFoodCookedChicken(ModLoader.getItemId(), 10);
        itemFoodCookedChicken.setIconIndex(ModLoader.addOverride(EnumTextureAtlases.ITEMS, "textures/item_chicken_cooked.png"));

        [...]

        ModLoader.addSmelting(itemFoodRawChicken.shiftedIndex, itemFoodCookedChicken.shiftedIndex);

    }

    public void hookGameStart (Minecraft minecraft) {
        minecraft.thePlayer.inventory.setInventorySlotContents(0, new ItemStack(Block.stoneOvenIdle, 1));
        minecraft.thePlayer.inventory.setInventorySlotContents(1, new ItemStack(Block.workbench, 1));
        minecraft.thePlayer.inventory.setInventorySlotContents(2, new ItemStack(Item.coal, 64));
        minecraft.thePlayer.inventory.setInventorySlotContents(3, new ItemStack(itemFoodRawChicken, 10));        

        [...]
    }

    [...]
``` 

TODO - Prevent eating food when right-clicking tile entities!

### More status effects stuff - attack strength

There are two potions in r1.2.5, *weakness* and *damageBoost* which modify the strength when the player hits mobs. We can provide support for such kind of modifications with yet another hook, this time at `Minecraft.clickMouse` right after the attack strength has been calculated in `var19`. There's this:

```java
    int var19 = (var9 = (var11 = var10000.inventory).getStackInSlot(var11.currentItem)) != null ? Item.itemsList[var9.itemID].getDamageVsEntity() : 1;
```

We can follow this with:

```java
    // var19 : hit strength.
    // var14 : Entity being hit
    var19 = ModLoader.hookAttackStrengthModifier (this.thePlayer, var14, var19);
```

And add this new hook to `ModLoader`

```java
    public static int hookAttackStrengthModifier (EntityLiving entityLiving, Entity entityHit, int strength) {
        int res = strength;
        for (Iterator<BaseMod> iterator = modList.iterator(); iterator.hasNext();) {
            res = ((BaseMod)iterator.next()).hookAttackStrengthModifier(entityLiving, entityHit, res);
        }
        return res;
    }
```

then `BaseMod`

```java
    public int hookAttackStrengthModifier (EntityLiving entityLiving, Entity entityHit, int strength) {
        return strength;
    }
```

TODO - Add example about using this alongside Status Effects

### Player vs. Block

The same way we can add hooks to modify how tools interact with blocks, in `Block.blockStrength` you have this bit:

```java
    [...]

    InventoryPlayer var2 = (var1 = var1).inventory;
    float var4 = 1.0F;
    if (var2.mainInventory[var2.currentItem] != null) {
        var4 = 1.0F * var2.mainInventory[var2.currentItem].getItem().getStrVsBlock(this);
    }

    [...]
```

Using the same kind of hook on var4 we can achieve the same effect as using r1.2.5 potions *digSpeed* and *digSlowdown*. Just adding this:

```java
    // var1 is playerEntity
    // var4 is original strength
    var4 = ModLoader.hookBlockHitStrengthModifier (var1, this, var4);
```

And the new hook in `Modloader`

```java
    public static float hookBlockHitStrengthModifier (EntityLiving entityLiving, Block block, float strength) {
        float res = strength;
        for (Iterator<BaseMod> iterator = modList.iterator(); iterator.hasNext();) {
            res = ((BaseMod)iterator.next()).hookBlockHitStrengthModifier(entityLiving, block, res);
        }
        return res;     
    }
```

And `Basemod`

```java
    public float hookBlockHitStrengthModifier (EntityLiving entityLiving, Block block, float strength) {
        return strength;
    }
```

TODO - Add example about using this alongside Status Effects

### Entity speed

In `EntityLiving.onLivingUpdate` @ around line 416

```java
    this.moveStrafing *= 0.98F;
    this.moveForward *= 0.98F;
    this.randomYawVelocity *= 0.9F;
    var3 = this.moveForward;
    var2 = this.moveStrafing;
    float var4;
``` 

I Change it for 

```java
    this.moveStrafing *= 0.98F;
    this.moveForward *= 0.98F;
    this.randomYawVelocity *= 0.9F;
    float f = ModLoader.hookEntitySpeedModifier (this, 1.0F);
    var3 = this.moveForward * f;
    var2 = this.moveStrafing * f;
```

Then add this in `ModLoader`

```java
    public static float hookEntitySpeedModifier (EntityLiving entityLiving, float speedModifier) {
        float res = speedModifier;
        for (Iterator<BaseMod> iterator = modList.iterator(); iterator.hasNext();) {
            res *= ((BaseMod)iterator.next()).hookEntitySpeedModifier(entityLiving, res);
        }
        return res;     
    }
```

and `BaseMod`

```java
    public float hookEntitySpeedModifier (EntityLiving entityLiving, float speedModifier) {
        return speedModifier;
    }
```

TODO - Add example about using this alongside Status Effects

### Particles

Some may not like this, that's why particle spawning (and color) will be controlled by your `Status` implementation. Anyways, I've added this new kind of particle to the base engine (adapted from r1.2.5):

```java
    package net.minecraft.client.particle;

    import net.minecraft.client.renderer.Tessellator;
    import net.minecraft.game.level.World;

    public class EntityStatusEffectFX extends EntityFX {

        public EntityStatusEffectFX(World world, float x, float y, float z, float vx, float vy, float vz) {
            super(world, x, y, z, vx, vy, vz);
            
            motionY *= 0.20000000298023224D;
            if (vx == 0.0D && vz == 0.0D) {
                motionX *= 0.10000000149011612D;
                motionZ *= 0.10000000149011612D;
            }
            particleScale *= 0.75F;
            particleMaxAge = (int)(8D / (Math.random() * 0.80000000000000004D + 0.20000000000000001D));
            noClip = false;
        }

        public void setParticleColor (float r, float g, float b) {
            particleRed = r;
            particleGreen = g;
            particleBlue = b;
        }
        
        public void renderParticle(Tessellator par1Tessellator, float par2, float par3, float par4, float par5, float par6, float par7) {
            float f = (((float)particleAge + par2) / (float)particleMaxAge) * 32F;
            if (f < 0.0F) f = 0.0F;
            if (f > 1.0F) f = 1.0F;
            
            super.renderParticle(par1Tessellator, par2, par3, par4, par5, par6, par7);
        }   
        
        public void onEntityUpdate() {
            prevPosX = posX;
            prevPosY = posY;
            prevPosZ = posZ;

            if (particleAge++ >= particleMaxAge) setEntityDead();

            particleTextureIndex = 128 + (7 - (particleAge * 8) / particleMaxAge);
            motionY += 0.0040000000000000001D;
            moveEntity(motionX, motionY, motionZ);

            if (posY == prevPosY) {
                motionX *= 1.1000000000000001D;
                motionZ *= 1.1000000000000001D;
            }

            motionX *= 0.95999997854232788D;
            motionY *= 0.95999997854232788D;
            motionZ *= 0.95999997854232788D;

            if (onGround) {
                motionX *= 0.69999998807907104D;
                motionZ *= 0.69999998807907104D;
            }
        }    
    }
```

And have hooked it to `RenderGlobal.spawnParticle`:

```java
    public final void spawnParticle(String var1, float var2, float var3, float var4, float var5, float var6, float var7) {
        float var8 = this.worldObj.playerEntity.posX - var2;
        float var9 = this.worldObj.playerEntity.posY - var3;
        float var10 = this.worldObj.playerEntity.posZ - var4;
        if (var8 * var8 + var9 * var9 + var10 * var10 <= 256.0F) {
            if (var1 == "bubble") {
                this.mc.effectRenderer.addEffect(new EntityBubbleFX(this.worldObj, var2, var3, var4, var5, var6, var7));
            } else if (var1 == "smoke") {
                this.mc.effectRenderer.addEffect(new EntitySmokeFX(this.worldObj, var2, var3, var4));
            } else if (var1 == "explode") {
                this.mc.effectRenderer.addEffect(new EntityExplodeFX(this.worldObj, var2, var3, var4, var5, var6, var7));
            } else if (var1 == "flame") {
                this.mc.effectRenderer.addEffect(new EntityFlameFX(this.worldObj, var2, var3, var4));
            } else if (var1 == "lava") {
                this.mc.effectRenderer.addEffect(new EntityLavaFX(this.worldObj, var2, var3, var4));
            } else if (var1 == "splash") {
                this.mc.effectRenderer.addEffect(new EntitySplashFX(this.worldObj, var2, var3, var4));
            } else if (var1 == "status_effect") {
                EntityStatusEffectFX entityFX = new EntityStatusEffectFX(this.worldObj, var2, var3, var4, 0, 0, 0);
                entityFX.setParticleColor(var5, var6, var7);
                this.mc.effectRenderer.addEffect(entityFX);             
            } else {
                if (var1 == "largesmoke") {
                    this.mc.effectRenderer.addEffect(new EntitySmokeFX(this.worldObj, var2, var3, var4, 2.5F));
                }

            }
        }
    }
```

Now we should add a particle colour to `Status` and the ability to turn off the feature.

```java
    public class Status {
        [...]

        public int particleColor = 0xFFFFFF;
        public boolean showParticles = true;

        [...]
    }
```

Now we have to spawn such particles in  `EntityLiving.updateStatusEffects`:

```java
    public void updateStatusEffects () {
        if (activeStatusEffectsMap.size() == 0) return; 

        Iterator<Integer> it = activeStatusEffectsMap.keySet().iterator ();
        
        // This will help me select one effect at random:
        int randomEffectCounter = 0;
        int randomEffectSelected = this.rand.nextInt(activeStatusEffectsMap.size());        
        
        while (it.hasNext()) {
            Integer statusID = it.next();
            StatusEffect statusEffect = (StatusEffect) activeStatusEffectsMap.get(statusID);
            
            // Create a particle? Not perfect but greedy solution which mostly works
            if (randomEffectCounter == randomEffectSelected) {
                Status status = Status.statusTypes[statusEffect.statusID];
                if (status.showParticles) {
                    int particleColour = status.particleColor;
                    worldObj.spawnParticle(
                            "status_effect",
                            posX + (this.rand.nextFloat() - 0.5F) * (float) width, 
                            (posY + this.rand.nextFloat() * (float) height) - (float) yOffset, 
                            posZ + (this.rand.nextFloat() - 0.5F) * (float) width,
                            (float)(particleColour >> 16 & 0xff) / 255F,
                            (float)(particleColour >> 8 & 0xff) / 255F,
                            (float)(particleColour & 0xff) / 255F
                           );
                }
            }
            randomEffectCounter ++;
            
            // Status effect will return false when duration has run out
            if (!statusEffect.onUpdate(this)) {
                it.remove ();
            }
        }
    }
```

And give our poison particles a colour in `mod_Example`:

```java
    statusPoisoned = new StatusPoisoned(Status.getNewStatusId(), true);
    statusPoisoned.particleColor = 0x70B433;
```

# Custom block renderers

Risugami's ModLoader hooks at `RenderBlocks.renderBlockByRenderType` so `ModLoader.renderWorldBlock` is called if the block's `getRenderType` returns a non already supported ID. I'm going to do the same thing as it's simple, easy and powerful.

Indev's `RenderBlocks` is less encapsulated and `renderBlockByRenderType` is a big method with lots of code inside. Anyways, being able to hook my `ModLoader` here should be as easy as... replacing the `return false` at the end for:

```java
    return ModLoader.renderWorldBlock(this, blockAccess, var2, var3, var4, var1, var5);
```

Also in `RenderBlocks`, at the end of `renderBlockOnInventory`, we need another hook:

```java
    ModLoader.renderInvBlock(this, var1, var3);
```

Modloader stores a `blockModels` HashMap. From your mod, you call a `getUniqueBlockModelID` which registers your mod in the HashMap associated with a new `renderType` which also returns. `renderWorldBlock` just iterates that HashMap and calls the registered mods' `renderWorldBlock` method. Such method should check the `renderType` and call the render function.

For the moment we add some stuff to `ModLoader` - almost the same thing as in Risugami's:

```java
    /*
     * Registers your BaseMod instance as containing a custom block renderer.
     * You must then override two methods in your mod:
     * `renderInvBlock` to render the block in the inventory and
     * `renderWorldBlock` to render it in the world.
     * Set flag if the item renderer should render it as a regular block or not.
     */
    public static int getUniqueBlockModelID(BaseMod basemod, boolean flag) {
        int i = nextBlockModelID++;
        blockModels.put(Integer.valueOf(i), basemod);
        blockSpecialInv.put(Integer.valueOf(i), Boolean.valueOf(flag));
        return i;
    }
    
    /*
     * Called from renderBlockAsItem.
     */
    public static void renderInvBlock(RenderBlocks renderblocks, Block block, int renderType) {
        BaseMod basemod = (BaseMod)blockModels.get(Integer.valueOf(renderType));

        if (basemod == null) {
            return;
        } else {
            basemod.renderInvBlock(renderblocks, block, renderType);
            return;
        }
    }

    /*
     * Called from renderBlockByRenderType.
     */
    public static boolean renderWorldBlock(RenderBlocks renderblocks, World world, int x, int y, int z, Block block, int renderType) {
        BaseMod basemod = (BaseMod)blockModels.get(Integer.valueOf(renderType));

        if (basemod == null) {
            return false;
        } else {
            return basemod.renderWorldBlock(renderblocks, world, x, y, z, block, renderType);
        }
    }
```

Once this is set up, we only need to actually implement a custom block renderer. To choose one which is pretty and simple, we'll use the lilypad renderer from r1.2.5 with some minor changes. The first thing would be creating a new class for our `BlockLilyPad`:

```java
    package com.mojontwins.modloader;

    import java.util.Random;

    import net.minecraft.client.physics.AxisAlignedBB;
    import net.minecraft.game.block.Block;
    import net.minecraft.game.block.Material;
    import net.minecraft.game.level.World;

    public class BlockLilypad extends ModBlock {

        public BlockLilypad(int id) {
            super(id, Material.plants);
            this.setTickOnLoad(true);
            this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.015625F, 1.0F);
        }

        public final boolean canPlaceBlockAt(World world, int x, int y, int z) {
            return this.canThisPlantGrowOnThisBlockID(world.getBlockId(x, y - 1, z));
        }

        protected boolean canThisPlantGrowOnThisBlockID(int blockID) {
            return blockID == Block.waterStill.blockID;
        }
        
        // Adapted from Flower:
        public final void onNeighborBlockChange(World world, int x, int y, int z, int blockID) {
            super.onNeighborBlockChange(world, x, y, z, blockID);
            this.checkFlowerChange(world, x, y, z);
        }

        public void updateTick(World world, int x, int y, int z, Random rand) {
            this.checkFlowerChange(world, x, y, z);
        }

        private void checkFlowerChange(World world, int x, int y, int z) {
            if (!this.canBlockStay(world, x, y, z)) {
                this.dropBlockAsItem(world, x, y, z, world.getBlockMetadata(x, y, z));
                world.setBlockWithNotify(x, y, z, 0);
            }

        }

        public boolean canBlockStay(World world, int x, int y, int z) {
            return this.canThisPlantGrowOnThisBlockID(world.getBlockId(x, y - 1, z));
        }
        
        public AxisAlignedBB getCollisionBoundingBoxFromPool(World par1World, int par2, int par3, int par4) {
            return new AxisAlignedBB ((float)par2 + minX, (float)par3 + minY, (float)par4 + minZ, (float)par2 + maxX, (float)par3 + maxY, (float)par4 + maxZ);
        }
        
        public final boolean isOpaqueCube() {
            return false;
        }

        public final boolean renderAsNormalBlock() {
            return false;
        }

        public int getRenderType() {
            return mod_Example.blockLilypadRenderID;
        }
    }
```

Note how `renderAsNormalBlock` returns `false`, and `getRenderType` refers to an ID we have yet to calculate... in `mod_Example`:

```java
    // Block lilypad with a custom renderer
    blockLilypad = new BlockLilypad (ModLoader.getBlockId()).setName("block.lilypad");
    ModLoader.registerBlock(blockLilypad);
    blockLilypad.blockIndexInTexture = ModLoader.addOverride (EnumTextureAtlases.TERRAIN, "textures/block_lilypad.png");
    blockLilypadRenderID = ModLoader.getUniqueBlockModelID(this, false);    
```

The actual rendering code should be located at `RenderWorldBlock` and `RenderInvBlock`.

```java
    public boolean renderWorldBlock(RenderBlocks renderblocks, World world, int x, int y, int z, Block block, int renderType) {
        Tessellator tessellator = Tessellator.instance;
        
        if (renderType == blockLilypadRenderID) {
            float b = block.getBlockBrightness(world, x, y, z);
            tessellator.setColorOpaque_F(b, b, b);
            
            return this.renderBlockLilypad (block, (float)x, (float)y, (float)z);
        }
        return false;
    }
    
    public boolean renderBlockLilypad(Block block, float par2, float par3, float par4)
    {
        Tessellator tessellator = Tessellator.instance;
        int i = block.blockIndexInTexture;

        int j = (i & 0xf) << 4;
        int k = i & 0xff0;
        
        float f = 0.015625F;
        
        float d = (float)j / 256F;
        float d1 = ((float)j + 15.99F) / 256F;
        float d2 = (float)k / 256F;
        float d3 = ((float)k + 15.99F) / 256F;
        
        long l = (long)(par2 * 0x2fc20f) ^ (long)par4 * 0x6ebfff5L ^ (long)par3;
        l = l * l * 0x285b825L + l * 11L;
        int i1 = (int)(l >> 16 & 3L);
        
        float f1 = (float)par2 + 0.5F;
        float f2 = (float)par4 + 0.5F;
        float f3 = (float)(i1 & 1) * 0.5F * (float)(1 - (i1 & 2));
        float f4 = (float)(i1 + 1 & 1) * 0.5F * (float)(1 - ((i1 + 1) & 2));

        tessellator.addVertexWithUV((f1 + f3) - f4, (float)par3 + f, f2 + f3 + f4, d, d2);
        tessellator.addVertexWithUV(f1 + f3 + f4, (float)par3 + f, (f2 - f3) + f4, d1, d2);
        tessellator.addVertexWithUV((f1 - f3) + f4, (float)par3 + f, f2 - f3 - f4, d1, d3);
        tessellator.addVertexWithUV(f1 - f3 - f4, (float)par3 + f, (f2 + f3) - f4, d, d3);
        tessellator.addVertexWithUV(f1 - f3 - f4, (float)par3 + f, (f2 + f3) - f4, d, d3);
        tessellator.addVertexWithUV((f1 - f3) + f4, (float)par3 + f, f2 - f3 - f4, d1, d3);
        tessellator.addVertexWithUV(f1 + f3 + f4, (float)par3 + f, (f2 - f3) + f4, d1, d2);
        tessellator.addVertexWithUV((f1 + f3) - f4, (float)par3 + f, f2 + f3 + f4, d, d2);

        return true;
    }
```

Lilypads have a gotcha: they should be able to be placed on still water by the player, but the main "check which block is under the mouse cursor" code won't work as water is not detected as a block you can interact with, just like air (thanks **Silver**, for the pointer).

When you create a block, they are automaticly assigned to a `ItemBlock` object which implements an `onItemUse` used to actually place the block in the world. For `BlockLilypad` we'll be using a custom item class, which we should instruct `ModLoader` to associate with `BlockLilypad` explicitly in our call to `registerBlock`.

*But* it's not *that* simple. For this to work, we need a `world.raytraceBlocks` which can detect water, and Indev's can't. In r1.2.5, `world.raytraceBlocks` has a special version which takes two booleans, which by default are `false, false`, but in the call which originates from `BlockLilypad` they end up being `true, false`. Also, this new version of `raytraceBlocks` seems to rely on a `Block.canCollideCheck` method which will return a call to `isCollidable` for all blocks but for some block classes, for example `BlockFluid`.

So let's go step by step. First thing, adding `Block.canCollideCheck` and overriding it in `BlockFluid`. 

In `Block`:

```java
    public boolean canCollideCheck (int metadata, boolean flag) {
        return isCollidable ();
    }
```

In `BlockFluid`:

```java
    public boolean canCollideCheck (int metadata, boolean flag) {
        return flag && metadata == 0;
    }
```

Now the task is trying to understand what both versions of raytraceBlocks are doing to enhance Indev's without completely rewriting it. 

Indev's version seems to iterate 20 times. Then it moves until it hits a block face then get's the block that's in there. if the block id is <= 0, the block is not collidable, or if the ray doesn't collide its bounding box, it keeps going. If not, it returns a `MovingObjectCollision` representing the collision with the bounding box.

r1.2.5's seems way more complicated. Before it starts moving, it gets the block in the current position and if (the second boolean parameter is false, OR the block is null OR it has a collision box) AND, also, the block id is > 0 AND `block.canCollideCheck` (Does this make sense?!  If the block is null, `block.canCollideCheck` will throw a `NullPointerException`. Hapily, that's after having checke that id > 0) - if this happens, then it calculates the collision with that block bounding box and if it's not null, it returns it. If not, it does a very similar loop as Indev's.

To help me understand what second flag does and what this piece of code is attempting to do I'll have to mentally trace the code. Let's see that, in fact, the second flag is false, which will make the whole parenthesis with ORs "true" without checking anything else. So the if, in practice, becomes this:

```
    if (k1 > 0 && block.canCollideCheck(i2, par3))
```

`par3`, the first flag, is `true` in the ItemWaterlily check. So if the block is water, this condition will be true and the block will be executed, so it will return the `MovingObjectCollision` agains that block. Note that this first check will return a collision if the block is collidable (solid) OR if it is water, via the special `canCollideCheck` which returns true if `par3` is true *BUT* the general call to this method, which happens with `par3` == false, will not.

So normally, water would be ignored, but if you call this with 'false, true', it won't.

The very same check is performed at the loop.

So the next step would be creating the new entry point for `rayTraceBlocks`, adding the first check, and modifying the already existing check. Then test if everything keeps working as normal, and if it does, do the `ItemLilypad` thing.

OK - I refactored the code, add the flags and the `canCollideCheck`s and it seems to work. Now let's add the missing stuff to place `Lilypads`.

In r1.2.5's `Item` you can find this method:


```java
    protected MovingObjectPosition getMovingObjectPositionFromPlayer(World par1World, EntityPlayer par2EntityPlayer, boolean par3)
    {
        float f = 1.0F;
        float f1 = par2EntityPlayer.prevRotationPitch + (par2EntityPlayer.rotationPitch - par2EntityPlayer.prevRotationPitch) * f;
        float f2 = par2EntityPlayer.prevRotationYaw + (par2EntityPlayer.rotationYaw - par2EntityPlayer.prevRotationYaw) * f;
        double d = par2EntityPlayer.prevPosX + (par2EntityPlayer.posX - par2EntityPlayer.prevPosX) * (double)f;
        double d1 = (par2EntityPlayer.prevPosY + (par2EntityPlayer.posY - par2EntityPlayer.prevPosY) * (double)f + 1.6200000000000001D) - (double)par2EntityPlayer.yOffset;
        double d2 = par2EntityPlayer.prevPosZ + (par2EntityPlayer.posZ - par2EntityPlayer.prevPosZ) * (double)f;
        Vec3D vec3d = Vec3D.createVector(d, d1, d2);
        float f3 = MathHelper.cos(-f2 * 0.01745329F - (float)Math.PI);
        float f4 = MathHelper.sin(-f2 * 0.01745329F - (float)Math.PI);
        float f5 = -MathHelper.cos(-f1 * 0.01745329F);
        float f6 = MathHelper.sin(-f1 * 0.01745329F);
        float f7 = f4 * f5;
        float f8 = f6;
        float f9 = f3 * f5;
        double d3 = 5D;
        Vec3D vec3d1 = vec3d.addVector((double)f7 * d3, (double)f8 * d3, (double)f9 * d3);
        MovingObjectPosition movingobjectposition = par1World.rayTraceBlocks_do_do(vec3d, vec3d1, par3, !par3);
        return movingobjectposition;
    }
```

which is called from the `ItemWaterlily` thingy. Let's port it blindfoldedly.

And the `ItemWaterlily`:

```java
    package com.mojontwins.modloader;

    import net.minecraft.client.physics.MovingObjectPosition;
    import net.minecraft.game.block.Material;
    import net.minecraft.game.entity.player.EntityPlayer;
    import net.minecraft.game.item.ItemBlock;
    import net.minecraft.game.item.ItemStack;
    import net.minecraft.game.level.World;

    public class ItemLilypad extends ItemBlock {
        public ItemLilypad (int itemID) {
            super (itemID);
        }
        
        public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer) {
            MovingObjectPosition movingobjectposition = getMovingObjectPositionFromPlayer(par2World, par3EntityPlayer, true);

            if (movingobjectposition == null) {
                return par1ItemStack;
            }

            if (movingobjectposition.typeOfHit == 0) {
                int i = movingobjectposition.blockX;
                int j = movingobjectposition.blockY;
                int k = movingobjectposition.blockZ;

                if (par2World.getBlockMaterial(i, j, k) == Material.water && par2World.getBlockMetadata(i, j, k) == 0 && par2World.getBlockId(i, j + 1, k) == 0) {
                    par2World.setBlockWithNotify(i, j + 1, k, mod_Example.blockLilypad.blockID);
                    par1ItemStack.stackSize--;
                }
            }

            return par1ItemStack;
        }   
    }
```

And finally, in `mod_Example`, 

```java
    // Block lilypad with a custom renderer
    blockLilypad = new BlockLilypad (ModLoader.getBlockId()).setName("block.lilypad");
    ModLoader.registerBlock(blockLilypad, ItemLilypad.class);
    blockLilypad.blockIndexInTexture = ModLoader.addOverride (EnumTextureAtlases.TERRAIN, "textures/block_lilypad.png");
    blockLilypadRenderID = ModLoader.getUniqueBlockModelID(this, false);    
```

I'll leave this for now while I solve another issue: the item is not being displayed correctly. A plain lump of grass is being displayed in the player's hand and in the inventory, which means that I'm clearly missing something. I don't have a complete example of using custom block renderers in ModLoader (this is, including the item rendering code), nor I don't fully understand how this all works in Indev. So I'll have to do some research and get some understanding, and then see how can I work it all out.

Right now I've done this:

```java 
    public void renderInvBlock(RenderBlocks renderblocks, Block block, int renderType) {
        Tessellator tessellator = Tessellator.instance;
        if (renderType == blockLilypadRenderID) {
            tessellator.startDrawingQuads();
            Tessellator.setNormal(0.0F, -1.0F, 0.0F);
            this.renderBlockLilypad(block, -0.5F, -0.5F, -0.5F);
            tessellator.draw();
        }
    }
```

`renderInvBlock` has been hooked as a default case for `RenderBlocks.renderBlockOnInventory`, but I'm not sure if it's being called *at all*. Exactly: it's not being rendered. So let me check this out more slowly...

`ItemRenderer.renderItemInFirstPerson` seems to be very simple - If the `itemID` < 256 (i.e. it's a block) and its `getRenderType ()` returns `0` it just calls `this.renderBlocksInstance.renderBlockOnInventory (Block.blocksList [this.itemToRender.itemID]);`. If it's not, it renders the "2D in 3D" style representation of the item using its base texture. My `LilyPad` item falls into this category - I just have to understand *why* texture index `0` is being rendered, rather than the correct one.

This seems to extract the texture index from the associated Item `getIconIndex`. When a block is registered, a related `ItemBlock` is created. In its constructor, we have a `this.setIconIndex(Block.blocksList[var1 + 256].getBlockTextureFromSide(2));`. And, by default, this should return `blockIndexInTexture` no matter what. So what's wrong? I'm missing something.

Yup, `this.itemToRender.getItem().getIconIndex()` is returning 0 for `ItemLilypad` and I think I know why: I'm registering the block *before* I assign an iconIndex. In this case this **does** matter, as we actually need the associated `ItemBlock`'s `iconIndex`. So I'll change the order and it works. I'll just have to remember to document this fact!

The 2D view in the inventory and the "2D in 3D" view in the player's hand looks good for lilypads, but won't look as good generally, so I need to find the way to actually make Indev to call `renderInvBlock`. I also need a way to easily fall back in the 2D / "2D in 3D" solution.

* As we have seen, the item in the player's hand is drawn by `ItemRenderer.renderItemInFirstPerson`, which calls `RenderBlocks.renderBlockOnInventory` if id < 256 and `renderType` == 0, or draws a "2D in 3D" representation otherwise.
* Items in the inventory guy are drawn by `RenderItem.renderItemIntoGUI` which works similarly, but draws a 2D rendition instead of the "2D in 3D".

My goal (and I'm writing this 'cause it's easier for me to visualize what I need to do if I verbalize it) is calling the custom rendering function if id < 256 and `renderType` != 0 and be able to fall back to the default 2D / "2D in 3D" default if I want.

So I'll try and patch Indev in `ItemRenderer.renderItemInFirstPerson` and `RenderItem.renderItemIntoGUI` to call our `renderInvBlock` and, if that returns `false`, fallback to the default. 

And here is where the `getUniqueBlockModelID` parameter `flag` comes into game!

```java
    public static boolean renderBlockIsItemFull3D(int i) {
        if (!blockSpecialInv.containsKey(Integer.valueOf(i))) {
            return false;
        } else {
            return ((Boolean)blockSpecialInv.get(Integer.valueOf(i))).booleanValue();
        }
    }
```

This method will return true if the custom block renderer was created with the flag set to true. We'll call this to decide if we must render our block using our own `renderInvBlock` method or with the default method (2D / "2D in 3D").

`RenderItem.renderIntemIntoGUI`

```java
    if (var2.itemID < 256 && 
        (
            (renderType = Block.blocksList[var2.itemID].getRenderType()) == 0 ||
            ModLoader.renderBlockIsItemFull3D(renderType)
        )
    ) {
        var9 = var2.itemID;
        RenderEngine.bindTexture(var1.getTexture("/terrain.png"));
        Block var8 = Block.blocksList[var9];
        GL11.glPushMatrix();
        GL11.glTranslatef((float)(var3 - 2), (float)(var4 + 3), 0.0F);
        GL11.glScalef(10.0F, 10.0F, 10.0F);
        GL11.glTranslatef(1.0F, 0.5F, 8.0F);
        GL11.glRotatef(210.0F, 1.0F, 0.0F, 0.0F);
        GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        if (renderType == 0) 
            this.renderBlocks.renderBlockOnInventory(var8);
        else
            ModLoader.renderInvBlock(this.renderBlocks, var8, renderType);
        GL11.glPopMatrix();
    [...]
    }
```

`ItemRenderer.renderItemInFirstPerson`

```java
    int renderType;
    if (this.itemToRender.itemID < 256 && 
        (
            (renderType = Block.blocksList[this.itemToRender.itemID].getRenderType()) == 0 ||
            ModLoader.renderBlockIsItemFull3D(renderType)
        )
    ) {
        GL11.glBindTexture(3553 /*GL_TEXTURE_2D*/, this.mc.renderEngine.getTexture("/terrain.png"));
        Block var8 = Block.blocksList[this.itemToRender.itemID];
        if (renderType == 0)
            this.renderBlocksInstance.renderBlockOnInventory(var8);
        else
            ModLoader.renderInvBlock(this.renderBlocksInstance, var8, renderType);
    } else {
        [...]
    }
```

Seems to be working just fine. Time to document all this.

### A new hook

Let's place the waterlilies during level generation. I've added another hook in the `planting` phase of level generation @ `LevelGenerator`. 

`MoadLoader`:

```java
    public static void hookPlanting (LevelGenerator levelGenerator, World world, Random rand) {
        for (Iterator<BaseMod> iterator = modList.iterator(); iterator.hasNext();) {
            ((BaseMod)iterator.next()).hookPlanting(levelGenerator, world, rand);
        }
    }   
```

`BaseMod`:

```java
    public void hookPlanting (LevelGenerator levelGenerator, World world, Random rand) {
    }  
```

Use this in `mod_Example` to grow waterlilies:

```java
    public void hookPlanting (LevelGenerator levelGenerator, World world, Random rand) {
        // Grow waterlilies
        int numWaterlilies = world.length * world.width / 16;
        for (int i = 0; i < numWaterlilies; i ++) {
            int x = rand.nextInt(world.width);
            int y = world.waterLevel - 1;
            int z = rand.nextInt(world.length);
            
            if (world.getBlockId(x, y, z) == Block.waterStill.blockID) {
                world.setBlockWithNotify(x, y + 1, z, blockLilypad.blockID);
            }
        }
    } 
```

# Tile entities

Little information is stored in the World about blocks: just blockID and some metadata. This will not suffice when you expect more of a block - think about chests or furnaces. Those need more stuff to them. Minecraft implements this using Tile Entities, which are special entitiles which are related to a block in the world. When you place a furnace block in the world, a related tile entity is spawned in the same coordinates. You interact with it through the associated block. The tile entity for the furnace is cooking or smelting while you do your thing, and if you want to put or extract objects you right-click the related object and are given access to a GUI which actually shows and modifies vaules in the tile entity.

We need to provide the means to manage tile entities with ModLoader. But before I even start checking what Risugami's does, we'll design a rather simple tile entity. So simple that it doesn't even have a gui.

## The silly box

The silly box is just a box where you can store an item. It behaves this way:

* Then the silly block is placed, the silly tile entity will be spawned.
* If you right click it, it will drop the object it is containing.
* If you have an item in your hand when doing so, such item will get inside the box.
* When harvested, it will drop the related block and the contents and the tile entity will be despawned.
* The top texture will be different than the sides / bottom and will change if the box is full.

So simple. So we need to create a class for this new block, and then implement the related tile entity, and once we have everything in place, I'll research how to implement it into the game.

```java
    package com.mojontwins.modloader;

    import java.util.Random;

    import net.minecraft.game.block.BlockContainer;
    import net.minecraft.game.block.Material;
    import net.minecraft.game.block.tileentity.TileEntity;
    import net.minecraft.game.entity.other.EntityItem;
    import net.minecraft.game.entity.player.EntityPlayer;
    import net.minecraft.game.level.World;

    public class BlockSillyBox extends BlockContainer {
        public int topTextureIndex;
        public String name;
        
        public BlockSillyBox(int id) {
            super(id, Material.rock);
            this.setHardness(1.5F);
            this.setResistance(20.0F);
        }

        public final int getBlockTextureFromSide(int side) {
            return side == 1 ? topTextureIndex : blockIndexInTexture;
        }
        
        public int idDropped(int var1, Random var2) {
            // No matter what, drop the box EMPTY
            return mod_Example.blockSillyBoxEmpty.blockID;
        }

        public final boolean blockActivated(World world, int x, int y, int z, EntityPlayer entityPlayer) {
            TileEntitySillyBox tileEntity = (TileEntitySillyBox)world.getBlockTileEntity(x, y, z);
            tileEntity.putItem(entityPlayer);
            return true;
        }
        
        protected final TileEntity getBlockEntity() {
            return new TileEntitySillyBox();
        }
        
        public BlockSillyBox setName(String name) {
            this.name = name;
            return this;
        }
    }
```

Explanations:

* The Block doesn't really know if it's full or not. That's why the associated `TileEntity` is for.
* This block extends `BlockContainer`. This will automate the creation and destruction of the related `TileEntity` when the block is added or removed from the world.
* The super class `BlockContainer` knows which `TileEntity` to instantiate 'cause we are telling exactly that in `getBlockEntity`.
* `blockActivated` is called when the user right-clicks the block. We get the associated tile entity (the only way, it seems, is asking the `world` object which `TileEntity` is at the block's coordinates), then we call one of its method (which we'll implement later).
* Resistance is 20.0F so this won't get destroyed by explosions. Save a valuable item in one of these boxes! (still useless, I know).
* `idDropped` always returns the "empty" instance.

Let's create two blocks in `mod_Example`: one to represent the box "full" and one to represent the box "empty". Note that this is only visual. The block is the same - the only thing which is different is the top texture.

```java
    blockSillyBoxEmpty = (BlockSillyBox) new BlockSillyBox (ModLoader.getBlockId(), false).setName("block.silly_block_empty");
    blockSillyBoxFull = (BlockSillyBox) new BlockSillyBox (ModLoader.getBlockId(), true).setName("block.silly_block_full");
    
    blockSillyBoxFull.blockIndexInTexture = blockSillyBoxEmpty.blockIndexInTexture = ModLoader.addOverride(EnumTextureAtlases.TERRAIN, "textures/block_box.png");
    blockSillyBoxFull.topTextureIndex = ModLoader.addOverride(EnumTextureAtlases.TERRAIN, "textures/block_box_top_full.png");
    blockSillyBoxEmpty.topTextureIndex = ModLoader.addOverride(EnumTextureAtlases.TERRAIN, "textures/block_box_top_empty.png");
    
    ModLoader.registerBlock(blockSillyBoxFull);
    ModLoader.registerBlock(blockSillyBoxEmpty);
```

Now lets create a very simple tile entity to associate with our new block. This will perform the logic we explained before and, depending on the state (it is full or empty) it will remove the existing block and place the correct one.

```java
    package com.mojontwins.modloader;

    import com.mojang.nbt.NBTTagCompound;

    import net.minecraft.game.block.tileentity.TileEntity;
    import net.minecraft.game.entity.other.EntityItem;
    import net.minecraft.game.entity.player.EntityPlayer;
    import net.minecraft.game.item.ItemStack;
    import net.minecraft.game.level.World;

    public class TileEntitySillyBox extends TileEntity {
        ItemStack contents = null;
        
        public TileEntitySillyBox() {
            // TODO Auto-generated constructor stub
        }

        public final void readFromNBT(NBTTagCompound var1) {
            super.readFromNBT(var1);
            this.contents = new ItemStack(var1);
        }

        public final void writeToNBT(NBTTagCompound var1) {
            super.writeToNBT(var1);
            contents.writeToNBT(var1);
        }
        
        public void updateEntity() {
        }
        
        public void putItem (EntityPlayer entityPlayer) {
            World world = this.worldObj;
            int x = this.xCoord, y = this.yCoord, z = this.zCoord;
            
            if (this.contents != null) {
                // Give what's inside
                
                float px = world.random.nextFloat() * 0.7F + 0.15F;
                float py = 1.0F;
                float pz = world.random.nextFloat() * 0.7F + 0.15F;
                EntityItem entityItem = new EntityItem(world, (float)x + px, (float)y + py, (float)z + pz, new ItemStack (this.contents.itemID, this.contents.stackSize, this.contents.itemDamage));
                entityItem.delayBeforeCanPickup = 10;
                world.spawnEntityInWorld(entityItem);
                this.contents = null;

            } else {
                // Get new item
            
                ItemStack itemStack = entityPlayer.inventory.getCurrentItem();
                if (itemStack != null) {
                    this.contents = new ItemStack (itemStack.itemID, itemStack.stackSize, itemStack.itemDamage);
                    entityPlayer.inventory.setInventorySlotContents(entityPlayer.inventory.currentItem, null);
                } else this.contents = null;

            }
            
            // Now update the block in the world. As the new block is placed, a new TileEntity will be generated.
            // We don't want this, so we preserve `this` and then reset it.
            TileEntity tileEntity = world.getBlockTileEntity(x, y, z);
            
            int blockID = contents == null ? mod_Example.blockSillyBoxEmpty.blockID : mod_Example.blockSillyBoxFull.blockID;
            world.setBlockWithNotify(x, y, z, blockID);
            world.setBlockTileEntity(x, y, z, tileEntity);
        }
        
        public void onTileEntityRemoved (World world, int x, int y, int z) {
            if (world.getBlockId(x, y, z) == 0) {
                float px = world.random.nextFloat() * 0.7F + 0.15F;
                float py = 1.0F;
                float pz = world.random.nextFloat() * 0.7F + 0.15F;
                EntityItem entityItem = new EntityItem(world, (float)x + px, (float)y + py, (float)z + pz, this.contents);
                entityItem.delayBeforeCanPickup = 10;   
                world.spawnEntityInWorld(entityItem);
            }
        }
    }
```

Note: `onTileEntityRemoved` was not present in the original. I've added this 'cause I find it quite handy. Gets called when the TileEntity is removed. Note that whenever the block is changed from blockSillyBoxEmpty to blockSillyBoxFull and vice-versa, `onBlockRemoval` is called, and `BlockContainer.onBlockRemoval` calls `world.removeTileEntity`. So we have to detect *explicitly* that the new block in place is the empty block. 

So it seems Indev is so simple that no TileEntity registering has to be done, just put your stuff. Time to put this to the reference docs.

## Entity experiments and research

I'll spend some time trying to understand how entities work in Indev. What do they need to work, how they are spawned, and how you can interact with them. First I will be examining mobs. I will try to understand what it takes to put my own move. The first one would be a very simple copy of Zombies - the Husks, which will not burn in the sun and will spawn instead of zombies if the floor is sand.

It seems that the bare minimum that an `Entity` that extends from `EntityMob` is:

```java
public class EntityBlah extends EntityMob {
    public EntityZombie(World var1) {
        super(var1);

        // This monster texture
        this.texture = "/mob/blah.png";

        // This monster base speed
        this.moveSpeed = 0.5F;

        // This monster attack strength
        this.attackStrength = 5;
    }

    public final void onLivingUpdate() {
        float var1;

        // Special stuff for this kind of mob. 
        // For example: burn in the sun
        if (this.worldObj.skylightSubtracted > 7 && (var1 = this.getEntityBrightness(1.0F)) > 0.5F && this.worldObj.canBlockSeeTheSky((int)this.posX, (int)this.posY, (int)this.posZ) && this.rand.nextFloat() * 30.0F < (var1 - 0.4F) * 2.0F) {
            this.fire = 300;
        }

        // General stuff for monsters (move around, etc)
        super.onLivingUpdate();
    }

    protected final String getEntityString() {
        // Give this monster a name
        return "Blah";
    }

    protected final int scoreValue() {
        // What the monster drops when killed
        return Item.feather.shiftedIndex;
    }
}
```

Now I'll try to understand how mobs spawn. We have the `Spawner` class, which has a `performSpawning` method. All monster & creature spawning is performed in this method, and absolutely everything is hardcoded. Let's break it down so I can decide the hooks which are needed to add your own `EntityLiving`s.

## The `performSpawning` class

First, the maximum posible amount of creatures in the world is calculated and stored in `var1`. First, it depends on the level size:

```java
    int var1 = this.worldObj.width * this.worldObj.length * this.worldObj.height * 20 / 64 / 64 / 64 / 2;
```

Then, depending on the difficulty level, this value is modified. For level 0, divided by 4. For level 1, multiplied by 3/4. For level 2, it stays untouched, and for level 3 it is multiplied by 1.5.

**HOOK**: So we have a good place to add a new hook: `var1 = ModLoader.spawnerSetMaxHostileMobs (var1, this.worldObj);` which, by default, shoud return `var1`.

Then it calculates `var2` = the world horizontal plane size divided by 4000 and `var3` = the amount of creatures already spawned. `var2` will be used to set a max number of *non-hostile* creatures, so:

**HOOK**: `var2 = ModLoader.spawnerSetMaxNonHostileMobs (var1, this.worldObj)`.

Next part will attempt to spawn a creature 4 times. It calculates a random location in the world `(var8, var9, var10)` and, most importantly, *it selects a random type of mob* in `var7` calculation a random value between 0 and 4.

**HOOK**: `var7 = ModLoader.spawnerSelectMonster (var7)`. In our mod, we would apply a random chance and substitute the value if suited, or simply return `var7`.

With `(x, y, z)` and type of creature select, it tries then twice. Resets the coodinate to `(x, y, z)` and then it iterates three times in which it moves the position a random amount (-5..5) for x and z and (-1..1) for y. If still inside the world, it makes sure that the distance to the player squared is < 1024.0F (around 32 blocks)

**HOOK**: Now we have a definitive (x, y, z) position and a type we may want to change the type, so `var7 = ModLoader.spawnerSelectMonsterBasedOnPosition (var7, this.worldObj, x, y, z)`.

At this point, the engine checks `var7` and upon it's value it creates a new `EntityXXX (world)`. 

**HOOK** in the `default` case, call `var23 = ModLoader.spawnMonster (var7, var22.worldObj)`, which should return `null` on failure.

*Note*: I've added a case for `var7 == 5` (which won't happen without modifications) to spawn a giant zombie.

The last thing in this set of loops is checking if the entity can stay. If checks if the block at current `(x, y, z)` and the block below it are opaque, then it calls the new entity's `canSpawnHere`, calculates a random angle, and then spawns the hostile entity in the world.

The next section attempts to spawn non hostile mobs.

First it gets the amount of animal entities in the world, and, as in with mosters, it iterates 4 times. For each time, it won't let the total amount of non hostile mobs surpass the max density value calculated above (in `var2`) Then it works the same way the hostile mobs spawner loop, so I'm placing the same hooks:

**HOOK** `var7 = ModLoader.spawnerSelectAnimal (var7); `

**HHOK** `var7 = ModLoader.spawnerSelectAnimalBasedOnPosition (var7, this.worldObj, x, y, z);`

**HOOK** `var23 = ModLoader.spawnAnimal (var7, var22.worldObj);` and, finally

The plan is adding these hooks and attempt to add some simple entities increasing in complexity and see if new hooks are needed.

## Husks

Husks are almost like plain zombies but spawn on sand and don't burn in the sun. They also use a custom texture.

```java
    package com.mojontwins.modloader;

    import net.minecraft.game.entity.monster.EntityMob;
    import net.minecraft.game.item.Item;
    import net.minecraft.game.level.World;

    public class EntityHusk extends EntityMob {
        public EntityHusk(World var1) {
            super(var1);
            this.texture = "/mob/husk.png";
            this.moveSpeed = 0.7F;
            this.attackStrength = 7;
        }

        public final void onLivingUpdate() {
            // Nothing special for the moment
            super.onLivingUpdate();
        }

        protected final String getEntityString() {
            return "Husk";
        }

        protected final int scoreValue() {
            return Item.feather.shiftedIndex;
        }
    }
```

Nothing much happening here.

## Rendering

Where do we tell how this new mob should be rendered? Time for some code surfing and reverse engineering. It seems that the players, the skeleton and the zombie are all rendered using the same base class: `RenderLiving`. And it's all there, in `RenderManager`:

```java
    @SuppressWarnings("unchecked")
    private RenderManager() {
        this.entityRenderMap.put(EntitySpider.class, new RenderSpider());
        this.entityRenderMap.put(EntityPig.class, new RenderLiving(new ModelPig(), 0.7F));
        this.entityRenderMap.put(EntitySheep.class, new RenderSheep(new ModelSheep(), new ModelSheepFur(), 0.7F));
        this.entityRenderMap.put(EntityCreeper.class, new RenderCreeper());
        this.entityRenderMap.put(EntitySkeleton.class, new RenderLiving(new ModelSkeleton(), 0.5F));
        this.entityRenderMap.put(EntityZombie.class, new RenderLiving(new ModelZombie(), 0.5F));
        this.entityRenderMap.put(EntityPlayer.class, new RenderPlayer());
        this.entityRenderMap.put(EntityGiantZombie.class, new RenderGiantZombie(new ModelZombie(), 0.5F, 6.0F));
        this.entityRenderMap.put(EntityLiving.class, new RenderLiving(new ModelBiped(), 0.5F));
        this.entityRenderMap.put(Entity.class, new RenderEntity());
        this.entityRenderMap.put(EntityPainting.class, new RenderPainting());
        this.entityRenderMap.put(EntityArrow.class, new RenderArrow());
        this.entityRenderMap.put(EntityItem.class, new RenderItem());
        this.entityRenderMap.put(EntityTNT.class, new RenderTNTPrimed());
        Iterator var1 = this.entityRenderMap.values().iterator();

        while(var1.hasNext()) {
            ((Render)var1.next()).setRenderManager(this);
        }
    }
```

So it seems that, to add new renderers, we'll have to hijack the private attribute `entityRenderMap` from `ModLoader`. Time to use reflection again. `entityRenderMap`'s keys are entity classes, and the values instances of different kinds of renderers. To add our Husk we'll have to add a new entry:

```java
    this.entityRenderMap.put(EntityHusk.class, new RenderLiving(new ModelZombie (), 0.5F));
``` 

But from ModLoader:

```java
    /*
     * Add a new entry to RenderManager.entityRenderMap
     */
    @SuppressWarnings("unchecked")
    public void addEntityRenderer (Class<?> entityClass, Render render) throws Exception {
        HashMap<Class<?>,Render> entityRenderMap = (HashMap<Class<?>,Render>) field_entityRenderMap.get(null);
        entityRenderMap.put(entityClass, render);
        field_entityRenderMap.set(null, entityRenderMap);
    }
```

But this doesn't work. It throws a null pointer exception. And my kung fu is not that strong...

Maybe attacking at `RenderManager.getEntityRenderObject` with a hook instead...

```java
    public final Render getEntityRenderObject(Entity var1) {
        Class var2 = var1.getClass();
        Render var3;
        var3 = ModLoader.getEntityRender(var2);
        if (var3 == null) var3 = (Render)this.entityRenderMap.get(var2);
        
        if ((var3) == null && var2 != Entity.class) {
            var3 = (Render)this.entityRenderMap.get(var2.getSuperclass());
            this.entityRenderMap.put(var2, var3);
        }

        return var3;
    }
```

So 

```java
    public static void addEntityRenderer (Class<?> entityClass, Render render) throws Exception {
        entityRenderMap.put(entityClass, render);
    }
```

And we need this:

```java
    public static Render getEntityRender (Class<?> entityClass) {
        return entityRenderMap.get(entityClass);
    }
```

We also need a little sequencer (starts in 1000):

```java
    /*
     * Simple sequencer
     */
    public static int getNewMobID() {
        return currentMobID ++;
    }
```

The last thing to to is creating an entity ID, adding the entity renderer, and then populating some hooks from our mod class.

```java
    // Add husks
    
    entityHuskMobID = ModLoader.getNewMobID();
    ModLoader.addEntityRenderer(EntityHusk.class, new RenderLiving(new ModelZombie (), 0.5F));
```

And

```java
    public int spawnerSelectMonsterBasedOnPosition (int entityID, World world, int x, int y, int z) {
        // If it's a Zombie and it's been placed on sand...
        if (entityID == 3 && world.getBlockId(x, y, z) == Block.sand.blockID || world.getBlockId(x, y + 1, z) == Block.sand.blockID) {
            // It's now a husk!
            entityID = entityHuskMobID; 
        }
        return entityID;
    }

    public Object spawnMonster (int entityID, World world) {
        if (entityID == entityHuskMobID) {
            return new EntityHusk(world);
        }
        return null;
    }
```

## Porting a slime

I got hold of `a1.1.1`'s slimes, which are the earliest slimes I could get hold of in deobfuscated form (they were introduced in a1.0.11). I'll take a look at the methods they expect to override to see what I can do with them. I'll also rename some variables for the sake of clarity in this doc.

### `EntitySlime`

```java
    public EntitySlime(World world) {
        super(world);
        this.texture = "/mob/slime.png";
        this.size = 1 << this.rand.nextInt(3);                  // Local attribute (1, 2, 4)
        this.yOffset = 0.0F;                                    // From Entity
        this.slimeJumpDelay = this.rand.nextInt(20) + 10;       // Local attribute
        this.setSlimeSize(this.size);                           // Local method
    }
```

```java
    // This method is local to this class
    public void setSlimeSize(int var1) {
        this.size = var1;                                       // Local attribute (1, 2, 4)
        this.setSize(0.6F * (float)var1, 0.6F * (float)var1);   // From Entity
        this.health = var1 * var1;                              // From EntityLiving (1, 4, 16)
        this.setPosition(this.posX, this.posY, this.posZ);      // From Entity
    }
```

```java
    // Nothing to comment here
    public void writeEntityToNBT(NBTTagCompound var1) {
        super.writeEntityToNBT(var1);
        var1.setInteger("Size", this.size - 1);
    }

    public void readEntityFromNBT(NBTTagCompound var1) {
        super.readEntityFromNBT(var1);
        this.size = var1.getInteger("Size") + 1;
    }
```

This one, I'll have to stop by:

```java
    public void onUpdate() {
        this.prevSquishFactor = this.squishFactor;
        boolean var1 = this.onGround;
        super.onUpdate();
        if (this.onGround && !var1) {
            for(int var2 = 0; var2 < this.size * 8; ++var2) {
                float var3 = this.rand.nextFloat() * 3.1415927F * 2.0F;
                float var4 = this.rand.nextFloat() * 0.5F + 0.5F;
                float var5 = MathHelper.sin(var3) * (float)this.size * 0.5F * var4;
                float var6 = MathHelper.cos(var3) * (float)this.size * 0.5F * var4;
                // I'll have to implement this new particle
                this.worldObj.spawnParticle("slime", this.posX + (double)var5, this.boundingBox.minY, this.posZ + (double)var6, 0.0D, 0.0D, 0.0D);
            }

            if (this.size > 2) {
                // This method exists at World
                this.worldObj.playSoundAtEntity(this, "mob.slime", this.getSoundVolume(), ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F) / 0.8F);
            }

            this.squishFactor = -0.5F;
        }

        this.squishFactor *= 0.6F;
    }
```

I have to tell if `onUpdate` is `onEntityUpdate` or `onLivingUpdate`. In a1.1.1, `EntityLiving.onUpdate` does call to `onLivingUpdate` and for what it does it seems to be `onEntityUpdate` So I'll have to rename this method. Same goes for this:

```java
   protected void updateEntityActionState() {
        EntityPlayer var1 = this.worldObj.getClosestPlayerToEntity(this, 16.0D);
        if (var1 != null) {
            this.faceEntity(var1, 10.0F);
        }

        if (this.onGround && this.slimeJumpDelay-- <= 0) {
            this.slimeJumpDelay = this.rand.nextInt(20) + 10;
            if (var1 != null) {
                this.slimeJumpDelay /= 3;
            }

            this.isJumping = true;
            if (this.size > 1) {
                this.worldObj.playSoundAtEntity(this, "mob.slime", this.getSoundVolume(), ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F) * 0.8F);
            }

            this.squishFactor = 1.0F;
            this.moveStrafing = 1.0F - this.rand.nextFloat() * 2.0F;
            this.moveForward = (float)(1 * this.size);
        } else {
            this.isJumping = false;
            if (this.onGround) {
                this.moveStrafing = this.moveForward = 0.0F;
            }
        }
    }
```

which seems to be `onLivingUpdate` - but there IS an `onLivingUpdate` as well. Maybe Indev's is split in two for Alpha? Will have to stop by a bit on this. Let's take a look at Indev's methods.

**Indev's `EntityLiving.onLivingUpdate` does this**

* ages the entity.
* Checks if it's too far away from then player and sets it to die, if so.
* Calls `updatePlayerActionState` which, IMHO, is a minaming.
* Detects "in Lava" or "in Water" and stores the state in flags `var8` and `var6`.
* Decreases all velocities a bit.
* *Calls my hook `entitySpeedModifier (this)` & Applies the modifier to `moveForward` (`var3`) and `moveStrafing` (`var2`)*. 
* Handles movement in water, in lava, or in air calling `moveFlying` and `moveEntity`
* Does some stuff to limb swing.
* checks collision with other entities and calls `applyEntityCollision` on the colliding enitity passing `this`.

**Indev's `EntityLiving.updatePlayerActionState` does this**

* Modifies `moveForward` and `moveStrafing` by random.
* Jumps at random.
* Turns around (Yaw), resets pitch.
* Detects "in Lava" or "in Water" and jumps at random.

**a1.1.1's `EntityLiving.onLivingUpdate` does this**

* Does some stuff to position and rotation.
* Calls `updateEntityActionState`.
* Detects "inLava" or "inWater" and performs jumping to get out.
* Decreases all velocities a bit.
* Calls `moveEntityWithHeading`.
* checks collision with other entities and calls `applyEntityCollision` on the colliding enitity passing `this`.


**a1.1.1's `EntityLiving.updateEntityActionState` does this**

* ages the entity.
* Checks if it's too far away from then player and sets it to die, if so.
* Selects a target to pursue (?)
* Detects "in Lava" or "in Water" and jumps at random.

SO. *together* both methods do basicly (!) the same thing, but the tasks seem to have been redistributed. So I'll have to rename `updateEntityActionState` to `updatePlayerActionState` and then add a custom `onLivingUpdate` which ports the tasks `a1.1.1`'s is doing to Indev (?).

```java
    public void setEntityDead() {
        if (this.size > 1 && this.health == 0) {
            for(int var1 = 0; var1 < 4; ++var1) {
                float var2 = ((float)(var1 % 2) - 0.5F) * (float)this.size / 4.0F;
                float var3 = ((float)(var1 / 2) - 0.5F) * (float)this.size / 4.0F;
                EntitySlime var4 = new EntitySlime(this.worldObj);
                var4.setSlimeSize(this.size / 2);
                // Change this by setPositionAndRotation
                var4.setLocationAndAngles(this.posX + (double)var2, this.posY + 0.5D, this.posZ + (double)var3, this.rand.nextFloat() * 360.0F, 0.0F);
                this.worldObj.spawnEntityInWorld(var4);
            }
        }

        super.setEntityDead();
    }
```

```java
    public void onCollideWithPlayer(EntityPlayer var1) {
        if (this.size > 1 && this.canEntityBeSeen(var1) && (double)this.getDistanceToEntity(var1) < 0.6D * (double)this.size && var1.attackEntityFrom(this, this.size)) {
            this.worldObj.playSoundAtEntity(this, "mob.slimeattack", 1.0F, (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
        }

    }
```

`canEntityBeSeen` doesn't seem to exist in Indev. This is the method (for `EntityLiving`). I've added it.

```java
    protected boolean canEntityBeSeen(Entity var1) {
        return this.worldObj.rayTraceBlocks(Vec3D.createVector(this.posX, this.posY + (double)this.getEyeHeight(), this.posZ), Vec3D.createVector(var1.posX, var1.posY + (double)var1.getEyeHeight(), var1.posZ)) == null;
    }
```

Finally:

```java
    protected String getHurtSound() {
        return "mob.slime";
    }

    protected String getDeathSound() {
        return "mob.slime";
    }

    protected int getDropItemId() {
        return this.size == 1 ? Item.slimeBall.shiftedIndex : 0;
    }

    public boolean getCanSpawnHere() {
        Chunk var1 = this.worldObj.getChunkFromBlockCoords(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY));
        return (this.size == 1 || this.worldObj.difficultySetting > 0) && this.rand.nextInt(10) == 0 && var1.getRandomWithSeed(987234911L).nextInt(10) == 0 && this.posY < 16.0D;
    }

    protected float getSoundVolume() {
        return 0.6F;
    }
```

`getCanSpawnHere` has to be redefined, of course. No chunks in Indev :-)

This leads me to another question: Sounds. Everything in indev sounds "UUGH", there's definitely support for it, it's just undefined for Zombies (for example). Where are they stored? Do I have to use a sound proxy? I'd rather have a big .jar, to be honest.

Oh my gosh - this has a resources renderer. I didn't know this was the case for Indev as well. Well, could I use my server? 

Anyways, let's add to the mix the new particle FX. How can I add particle FX from ModLoader? Let's leave this for now. I'd have to have a way to override the textures in this module and I'm not quite sure how to do it. Is that even possible in Risugami's ModLoader?

OK - let's do this. No textures for the moment. Port the Entity and the Render and see if it, at least, compiles. AND IT WORKS. Those are the modified classes:

```java
    package com.mojontwins.modloader;

    import com.mojang.nbt.NBTTagCompound;

    import net.minecraft.game.entity.Entity;
    import net.minecraft.game.entity.EntityLiving;
    import net.minecraft.game.entity.player.EntityPlayer;
    import net.minecraft.game.item.Item;
    import net.minecraft.game.level.World;
    import util.MathHelper;

    public class EntitySlime extends EntityLiving {
        public float squishFactor;
        public float prevSquishFactor;
        private int slimeJumpDelay = 0;
        public int size = 1;

        public EntitySlime(World var1) {
            super(var1);
            this.texture = "/mob/slime.png";
            this.size = 1 << this.rand.nextInt(3);
            this.yOffset = 0.0F;
            this.slimeJumpDelay = this.rand.nextInt(20) + 10;
            this.setSlimeSize(this.size);   
        }
        
        public void setSlimeSize(int var1) {
            this.size = var1;
            this.setSize(0.6F * (float)var1, 0.6F * (float)var1);
            this.health = var1 * var1;
            this.setPosition(this.posX, this.posY, this.posZ);
        }

        public void writeEntityToNBT(NBTTagCompound var1) {
            super.writeEntityToNBT(var1);
            var1.setInteger("Size", this.size - 1);
        }

        public void readEntityFromNBT(NBTTagCompound var1) {
            super.readEntityFromNBT(var1);
            this.size = var1.getInteger("Size") + 1;
        }

        public void onEntityUpdate() {
            this.prevSquishFactor = this.squishFactor;
            boolean var1 = this.onGround;
            super.onEntityUpdate();
            if (this.onGround && !var1) {
                for(int var2 = 0; var2 < this.size * 8; ++var2) {
                    float var3 = this.rand.nextFloat() * 3.1415927F * 2.0F;
                    float var4 = this.rand.nextFloat() * 0.5F + 0.5F;
                    float var5 = MathHelper.sin(var3) * (float)this.size * 0.5F * var4;
                    float var6 = MathHelper.cos(var3) * (float)this.size * 0.5F * var4;
                    // TODO - I'll have to implement this new particle (original "slime")
                    this.worldObj.spawnParticle("splash", this.posX + (float)var5, this.boundingBox.minY, this.posZ + (float)var6, 0.0F, 0.0F, 0.0F);
                }

                if (this.size > 2) {
                    // This method exists at World
                    this.worldObj.playSoundAtEntity(this, "mob.slime", 1.0F, ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F) / 0.8F);
                }

                this.squishFactor = -0.5F;
            }

            this.squishFactor *= 0.6F;
        }
        
        protected Entity findPlayerToAttack() {
            return this.worldObj.playerEntity.getDistanceSqToEntity(this) < 256.0F ? this.worldObj.playerEntity : null;
        }
        
        protected void updatePlayerActionState() {
            Entity var1 = this.findPlayerToAttack();
            if (var1 != null) {
                this.faceEntity(var1, 10.0F);
            }

            if (this.onGround && this.slimeJumpDelay-- <= 0) {
                this.slimeJumpDelay = this.rand.nextInt(20) + 10;
                if (var1 != null) {
                    this.slimeJumpDelay /= 3;
                }

                this.isJumping = true;
                if (this.size > 1) {
                    this.worldObj.playSoundAtEntity(this, "mob.slime", 1.0F, ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F) * 0.8F);
                }

                this.squishFactor = 1.0F;
                this.moveStrafing = 1.0F - this.rand.nextFloat() * 2.0F;
                this.moveForward = (float)(1 * this.size);
            } else {
                this.isJumping = false;
                if (this.onGround) {
                    this.moveStrafing = this.moveForward = 0.0F;
                }
            }
        }
        
        public void setEntityDead() {
            if (this.size > 1 && this.health == 0) {
                for(int var1 = 0; var1 < 4; ++var1) {
                    float var2 = ((float)(var1 % 2) - 0.5F) * (float)this.size / 4.0F;
                    float var3 = ((float)(var1 / 2) - 0.5F) * (float)this.size / 4.0F;
                    EntitySlime var4 = new EntitySlime(this.worldObj);
                    var4.setSlimeSize(this.size / 2);
                    // Change this by setPositionAndRotation
                    var4.setPositionAndRotation(this.posX + var2, this.posY + 0.5F, this.posZ + var3, this.rand.nextFloat() * 360.0F, 0.0F);
                    this.worldObj.spawnEntityInWorld(var4);
                }
            }

            super.setEntityDead();
        }
        
        public void onCollideWithPlayer(EntityPlayer var1) {
            float distance = (0.6F * this.size);
            if (this.size > 1 && this.canEntityBeSeen(var1) && (double)this.getDistanceSqToEntity(var1) < distance * distance && var1.attackEntityFrom(this, this.size)) {
                this.worldObj.playSoundAtEntity(this, "mob.slimeattack", 1.0F, (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
            }

        }   
        
        protected String getHurtSound() {
            return "mob.slime";
        }

        protected String getDeathSound() {
            return "mob.slime";
        }

        protected final int scoreValue() {
            // TODO - Add item slimeball
            return this.size == 1 ? Item.coal.shiftedIndex : 0;
        }

        public boolean getCanSpawnHere(float var1, float var2, float var3) {
            this.setPosition(var1, var2 + this.height / 2.0F, var3);
            return this.worldObj.checkIfAABBIsClear1(this.boundingBox) && this.worldObj.getCollidingBoundingBoxes(this.boundingBox).size() == 0
                && this.posY < this.worldObj.waterLevel;
        }

        protected float getSoundVolume() {
            return 0.6F;
        }
    }
```

```java
    package com.mojontwins.modloader;

    import org.lwjgl.opengl.GL11;

    import net.minecraft.client.model.ModelBase;
    import net.minecraft.client.renderer.entity.RenderLiving;
    import net.minecraft.game.entity.EntityLiving;

    public class RenderSlime extends RenderLiving {
        private ModelBase modelSlime;

        public RenderSlime(ModelBase var1, ModelBase var2, float var3) {
            super(var1, var3);
            this.modelSlime = var2;
        }

        protected boolean renderSlimePassModel(EntitySlime var1, int var2) {
            if (var2 == 0) {
                this.setRenderPassModel(this.modelSlime);
                GL11.glEnable(GL11.GL_NORMALIZE);
                GL11.glEnable(GL11.GL_BLEND);
                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                return true;
            } else {
                if (var2 == 1) {
                    GL11.glDisable(GL11.GL_BLEND);
                    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                }

                return false;
            }
        }
        
        protected void squishSlime(EntitySlime var1, float var2) {
            float var3 = (var1.prevSquishFactor + (var1.squishFactor - var1.prevSquishFactor) * var2) / ((float)var1.size * 0.5F + 1.0F);
            float var4 = 1.0F / (var3 + 1.0F);
            float var5 = (float)var1.size;
            GL11.glScalef(var4 * var5, 1.0F / var4 * var5, var4 * var5);
        }
        
        protected void preRenderCallback(EntityLiving var1, float var2) {
            this.squishSlime((EntitySlime)var1, var2);
        }
        
        protected boolean shouldRenderPass(EntityLiving var1, int var2) {
            return this.renderSlimePassModel((EntitySlime)var1, var2);
        }    
    }
```

```java
    package com.mojontwins.modloader;

    import org.lwjgl.opengl.GL11;

    import net.minecraft.client.model.ModelBase;
    import net.minecraft.client.renderer.entity.RenderLiving;
    import net.minecraft.game.entity.EntityLiving;

    public class RenderSlime extends RenderLiving {
        private ModelBase modelSlime;

        public RenderSlime(ModelBase var1, ModelBase var2, float var3) {
            super(var1, var3);
            this.modelSlime = var2;
        }

        protected boolean renderSlimePassModel(EntitySlime var1, int var2) {
            if (var2 == 0) {
                this.setRenderPassModel(this.modelSlime);
                GL11.glEnable(GL11.GL_NORMALIZE);
                GL11.glEnable(GL11.GL_BLEND);
                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                return true;
            } else {
                if (var2 == 1) {
                    GL11.glDisable(GL11.GL_BLEND);
                    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                }

                return false;
            }
        }
        
        protected void squishSlime(EntitySlime var1, float var2) {
            float var3 = (var1.prevSquishFactor + (var1.squishFactor - var1.prevSquishFactor) * var2) / ((float)var1.size * 0.5F + 1.0F);
            float var4 = 1.0F / (var3 + 1.0F);
            float var5 = (float)var1.size;
            GL11.glScalef(var4 * var5, 1.0F / var4 * var5, var4 * var5);
        }
        
        protected void preRenderCallback(EntityLiving var1, float var2) {
            this.squishSlime((EntitySlime)var1, var2);
        }
        
        protected boolean shouldRenderPass(EntityLiving var1, int var2) {
            return this.renderSlimePassModel((EntitySlime)var1, var2);
        }    
    }
```

### Auto select entities

The `Spawner` selects one of the pre-existing mob and animals by throwing a random. I could add a couple of lists to let the developers add their entities to such list automaticly:

`ModLoader.registerMonsterEntity (int entityID, Class monster)` and `ModLoader.registerAnimalEntity (int entityID, Class animal)` would let you add your own items to those lists. But first, I'll have to pre-fill the lists with "vanilla" entities and modify the Spawner to read from the list. Or HashMap.

It's done. If you register your entities, those will be auto-selected at random by the engine, just as it selects the pre-existing entities. If you don't, you'll have to use the hooks to select & spawn them.

# Trying to understand the world generator

`LevelGenerator` is pretty confusing, more so with obfuscated variables. I dunno if current RetroMCP has a better mapping - nah. I'm stuck with the varXX names. Will try to figure out how every stage of the process starts and how could I modify it via hooks or total substitution or whatever.

First of all, there's a set of attributes which define the characteristics of the generated level, which are set directly or indirectly from the menu selections when generating a new level. Those are:

* `width`, `height`, `length` - Level dimensions (in z, y, z axes, respectively).
* `waterLevel` - Sea level.
* `groundLevel` - Gotta figure this out.
* `islandGen` - Generate "island" level, surrounded by an endless ocean.
* `floatingGen` - Generate a "floating islands level". 
* `flatGen` - Generate a flat level.
* When all three are set to false, an "inland" level is created, surrounded by an endless plain.
* `levelType` contains the level "theme", which is: 0 - Normal, 1 - Hell, 2 - Paradise, 3 - Woods.

`levelType` seems to affect these stages:

* *Soiling* - The x-z plane is iterated. For each (x, z), a noise level is generated using `NoiseGenerateOctaves.generateNoise`. Then it is compared with a different value depending on `levelType` to set a boolean `var59`: 
    * For `levelType` 0 (normal), `var59` is true if the value is > -8.0D if `islandGen` or 8.0D otherwise.
    * For `levelType` 1 (hell) and 3 (paradise), true if value is > -8.0D.
    * For `levelType` 2 (paradise), `var59` true if that value is > -32.0D.
    * If the flag is set and the (somehow) calculated level for that coordinate is below a previously adjusted sea level, sand or grass (for Hell) is generated.

* *Watering* - 
    * If not `floatingGen`, under `waterLevel`
        * If `levelType` 1 select "lava"
        * else select "water"
    * Fill empty spaces with selected block.

* Decorating, selects `skyColor`, `fogColor`, `cloudColor`, `defaultFluid`, maybe `cloudHeight` or even adjusts `waterLevel` (set to -16 for `floatingGen`).

* *Planting* 
    * For al `levelType`s but 1, grows grass on dirt.
    * Grows extra trees if `levelType` is 3 (woods).
    * Uses a multiplicator of 100 to grow flowers, 1000 for `levelType` 2 (paradise).

Now on to the weird gibberish. Maybe if I start replacing `varXX`s with actual names? At least out of the code base so it doesn't get in the diff... Not that it really matters, tho'. 

## Understanding the generator

Let's break it down piece by piece. First thing we get is this:

```java
    int genPasses = 1;
    if (this.floatingGen) {
        genPasses = (var4 - 64) / 48 + 1;
    }

    this.phases = 13 + genPasses * 4;
```

I guess this has to do with the progress var. `var4` is the level heigth, which can be 64 for normal levels or 256 for "deep" levels. So `genPasses` will be 1 for all gens but for `floatingGen` which will make it `(var4 - 64) / 48 + 1;` which results on 1 for normal levels or 192 / 48 + 1 = 4 + 1 = 5 for "deep" levels. So "deep" floating islands have 5 generation "iterations". 

```java
    World world;
    (world = new World()).waterLevel = this.waterLevel;
    world.groundLevel = this.groundLevel;
    this.width = var2;
    this.depth = var3;
    this.height = var4;
    this.blocksByteArray = new byte[var2 * var3 * var4];
``` 

Some setup. Set `width` / `depth` / `height` with the parameters, create a blan `blocksByteArray` to contain the level, and create a new world. Note that, on the first call to `LevelGenerator.generate`, `waterLevel` and `groundLevel` are not explicitly set so they equal 0.

```java
    int genPass;
    LevelGenerator levelGenerator;

    for(genPass = 0; genPass < genPasses; ++genPass) {
        this.waterLevel = height - 32 - genPass * 48;
        this.groundLevel = this.waterLevel - 2;
            
        int[] heightMap;            
        int[] tempTempHeightMap;
```

The generator will now iterate 1 or 5 times (5 for "deep" floating islands). On each iteration, stages "Raising" to "Carving" will be executed. Prior to that, `waterLevel` is calculated as `height - 32 - genPass * 48`, and `groundLevel` two blocks below that, which results on: 

* Pass 0: `waterLevel` = **32** /  **192** ("deep"). `groundLevel` = **30** / **190**.
* Pass 1: `waterLevel` = 256 - 32 - 48 = **176**. `groundLevel` = **174**.
* Pass 2: `waterLevel` = 256 - 32 - 96 = **128**. `groundLevel` = **126**.
* Pass 3: `waterLevel` = 256 - 32 - 144 = **80**. `groundLevel` = **78**.
* Pass 4: `waterLevel` = 256 - 32 - 192 = **32**. `groundLevel` = **32**.

Note how `waterLevel` & `groundLevel` for deep floating islands is the same as the rest of the generators for the last pass (`waterlevel` = 32, `groundLevel` = 30).

### Raising / Eroding

The next part, "Raising / Eroding", iterates the x-z plane to create a basic heigth map using several noise generator. First it 'erodes' then it 'raises', but the way code is organized is quite cumbersome and I don't know if this is just the decompiler doing funny things with old bytecode. Most likely, as there is a "Raising" loop and a nested "Eroding" loop which will run after the variable used to iterate in the outer loop is out of bounds and then break out of both loops. 

Anyways. 

* The "Raising" part iterates on the x axis, from 0 to `this.width` - 1.
    * For each loop, it iterates on the z axis, from 0 to `this.depth` - 1.
        * It generates two noise values for the current (x-z) plane
        * Does some weird calculations with them
        * Calculates the max of them, divides by two, stores the result in `floorLevel`.
            * If `floatingGen`, more weird calculations are performed, and if the level is negative it's trimmed to 0
            * if not, it only flattens it a bit for negatives, multiplying by 0.8 the values.

* The "Eroding" part generates two new noise generators ngd1, ngd2, then iterates on the x axis, from 0 to `this.width` - 1.
    * For each loop, it iterates on the z axis, from 0 to `this.depth` - 1.
        * Generates a noise value based on ngd1.
        * Generates a variation value of 0 or 1 depending on a noise value from ngd2.
        * If the noise value from ngd1 > 2.0, it applies the variation to the current height: height = (height - variation) / 2 * 2 + variation

This calculation: `height = (height - variation) / 2 * 2 + variation` does something only if variation = 1, in which case:

    * if height is ODD, (height - 1 / 2 * 2) = height - 1. add 1 and height is not changed.
    * if height is EVEN, (height - 1 / 2 * 2) = height - 2. add 1 and height is 1 less.

So much effort for some little effect?

### Soiling

Now the height map is generated, the next pass will "fill" the blockArray with a basic set of blocks depending on the height map. It iterates the x-z plane, creates some weird numbers and a couple of noise values, and then:
    * Sets `floorLevel` to the value stored in `tempHeightMap` plus `levelGenerator.waterLevel`: this sets the vertical "center" of the height map at water level.
    * Sets `fillLevel` a bit above or below `floorLevel`, depending on the first noise value (which may be negative).
    * Adjusts the `heightMap` to the max between `floorLevel` and `fillLevel`. Now the `heightMap` is centered around water level and not 0.
    * If `heightMap` value for this cell is over the max map height - 2, it's trimmed to this value.
    * If `heightMap` value for this cell is 0 or negative, it's trimmed to 1.
    * A weird operation is generated to calculate a `islandBottomLevel` of the floating islands for `floatingGen`. If the calculated value is above the "imaginary" water level (as there is no water in floating islands) the value is set to `levelGenerator.height` which will later fill all of this column with air.
    * The curent column (x, z) is now iterated from 0 to `this.height`.
        * Default blockID = 0 (air)
        * If we are below `floorLevel`, blockID is DIRT.
        * If we are below `fillLevel`, blockID is STONE. 
        * If `floatingGen` and we are below `islandBottomLevel`, it is set to 0 (AIR).
        * If the block in the blocks array WAS 0, put the new blockID value. <- this makes generating several levels of islands possible.

### Growing

This section adds sand and gravel to the world. It sets a `beachLevel` (my invention, of course :D) just 1 block below `waterLevel`, or 2 blocks above if `levelType` is 2 (paradise). Then it iterates the x-z plane. Then calculates a boolean `var59` with a different condition depending on the niose generator `noiseGenO1` and `levelType`:

* For level types 1 or 3 (hell or woods), or the `islandGen` if level type is not 2 (paradise), if noise is > -8.
* For level type 2 (paradise), if noise > -32 (bigger chance).
* For level type 0 (normal), if not `islandGen`, if noise is > 8 (smaller chance).

It then gets the floor level for (x, z), and gets the `blockID` which is 1 over the floor level at (x, z). 

* If it is water or air and the floor level is below `beachLevel` and some generated noise in `noiseGenO2` for (x, z) is > 12.0, it places gravel.
* If it is air, then, if the floor level is below `beachLevel` and `var59` it places sand (dirt in `levelType` 1 - "hell").

### Carving

This seems to dig rock to make caves, but the code is too confusing. It seems to have it iterating for a number of times digging tunnels in random, varying angles. 

Note that the genPasses loop ends here. Once we pass this stage, all levels (for `floatingGen` "deep" levels) have been put to the block array.

### Ore generation

Generates ores in different quantities and from certain heights. Parameters to `populateOre` seem to be Block ID, density, chance, max. height.

```java
    int coalBlocks = this.populateOre(Block.oreCoal.blockID, 1000, 10, (height << 2) / 5);
    int irenBlocks = this.populateOre(Block.oreIron.blockID, 800, 8, height * 3 / 5);
    int goldBlocks = this.populateOre(Block.oreGold.blockID, 500, 6, (height << 1) / 5);
    int diamondBlocks = this.populateOre(Block.oreDiamond.blockID, 800, 2, height / 5);
    System.out.println("Coal: " + coalBlocks + ", Iron: " + irenBlocks + ", Gold: " + goldBlocks + ", Diamond: " + diamondBlocks);
```

### Melting

Lava is added.

Then cloud height is calculated, and groundLevel and waterLevel are adjusted as follows:

* Generally, `cloudHeight` is `height` + 2.
* if `floatingGen`, `groundLevel` is set to -128, `waterLevel` to -127, and `cloudHeight` to -16, all bellow the bottom of the map.
* if `islandGen`, `groundLevel` is set to `waterLevel` - 9, that is, 32 - 9 = 23.
* if `flatGen` or inland, `groundLevel` is `waterLevel` + 1, that is, 33, then `waterLevel` is adjusted to `groundLevel` - 16, becoming 17.

### Watering

Water (or lava, for `levelType` 2 "hell") is then added below `waterLevel` using floor fills.

### Level visuals

Based upon `levelType` (normal, hell, paradise or woods) several values are set:

* `world.skyColor` - 0x99CCFF by default
* `world.fogColor` - 0xFFFFFF by default
* `world.cloudColor` - 0xFFFFFF by default
* `skylightSubtracted` Seems to be 15 by default. Skeletons & zombies only burn at day if it is > 7.
* `skyBrightness` 15 by default.
* `defaultFluid` - water or lava (for hell).

### Assembling

This is called:

```java
    world.generate(width, height, depth, this.blocksByteArray, (byte[])null);
```

This method is also called when loading a level from disk. The two last parameters are passed `var3.getByteArray("Blocks")` and `var3.getByteArray("Data")`. Checking the `.mclevel` level format for Indev, we learn that:

* `Blocks` is a width * length * height bytes array of block IDs. (8 bits)
* `Data` is a width * length * height bytes array of block data (4 bit) and light value (next 4 bit).

This `generate` copies `width`, `height` and `depth` to the world, and copies the reference to `blocksByteArray` to its own `block` (note: it doesn't make a copy). Then there's this loop which iterates the whole byte array that I have to try and understand...

It seems to add lava, water, grass, dirt, bedrock or the default fluid to the limits of the block array, or something. I'm too dizzy now to understand this bit. The inner loop, which iterates on `y`, starts in 0, and if x / z are not in the borders it jumps to height - 2 directly. It's not that important, I don't think I need to touch this.

It then calculates an actual heightmap while it calculates the lights for the level, does some stuff, and returns.

From now on, all generation is performed on the world (not that it matters as the block array in the world is a reference to the block array in the generator).

## First customization idea: Themes

On a first iteration, having the ability to easily adding new world themes would be nice. Adding a new `LevelTheme` class with attributes and methods which would maintain a `HashMap` of themes one could easily access when `levelType` > 3. Adding new themes from ModLoader would be as easy as registering new `LevelTheme` instances in the `HashMap`, with the Key = the `levelType`. This should also add entries to the `GuiNewLevel` array of themes `worldTheme`, which happens to be private. Whether I make it accessible or I directly edit the base class to make it public, I have to get new values inside this array.

The main problem is that the array is not static and an instance of `GuiNewLevel` is created in three different places: in `GuiGameOver`, `GuiIngameMenu` and `GuiMainMenu`. To make this feasible I'm thinking on creating the object in `Minecraft` early on and then have a way to access it from those places. The other solution would be adding a hook after the object is created to add the new menu entries EVERY TIME which would be a bit of a chore.

That, or make the array static. But - I can't really access it if the object hasn't been instantiated or accessed once before the BaseMod "init" code is executed.

But, wouldn't instantiating a dummy object of this class assure that the static array is allocated and populated? Isn't that a bit "dirty"?

The other solution, as mentioned, would be - each of them three times:

```java
    GuiNewLevel guiNewLevel = new GuiNewLevel (this);
    ModLoader.addNewLevelMenuEntries (guiNewLevel);
    this.mc.displayGuiScreen (guiNewLevel);
```

This has to be added to three different spots buf I find this to be way more clean. In `addNewLevelMenuEntries` I would have to use reflection this way:

```java
    Field fieldWorldTheme = guiNewLevel.getClass().getDeclaredField(worldTheme);
    fieldWorldTheme.setAccessible(true);
    String [] worldTheme = (String []) fieldWorldTheme.get (guiNewLevel);

    // Modify or replace worldTheme. Cannot add to a static array, so:
    List<String> list = Arrays.asList(worldTheme);
    ArrayList<String> arraylist = new ArrayList<String>();
    arraylist.addAll(list);

    // Here: code to add all world themes defined in ModLoader.

    // And substitute the original static array for the modified one    
    fieldWorldTheme.set (guiNewLevel, ((Object)(arraylist.toArray(new String[0]))));
```

This should work. Now let's get on to the `ModLevelTheme` class, which should provide:

* A way to raise/lower the waterLevel for each pass

```java
    public int waterLevelAdjust = 0;                // in blocks; no change
```

* A way to select the blockID being used in the "Soiling" stage. I can add a hook before the values is decided. Return -1 for normal action:

```java
    int soilingBlockID (LevelGenerator levelGenerator, int y, int floorLevel, int fillLevel, int islandBottomLevel) {
        return -1;
    }
```

* A way to set `var59` and `newBlockID` for the "Growing" stage. I guess, one method and a public attribute:

```java
    public int growBlockID = Block.dirt.blockID;    // or whatever

    boolean shouldGrow (LevelGenerator levelGeneartor, double noiseValue) {
        return noiseValue > -8.0D;                  // or whatever;
    }
```

* A way to provide a `blockID` during the "Watering" stage.

```java 
    public int defaultFluidBlockID = Block.waterStill.blockID;
```

* A way to set visuals, like

```java

    public setVisuals (LevelGenerator levelGenerator, World world) {
        world.cloudColor = 2164736;
        world.fogColor = 1049600;
        world.skyColor = 1049600;
        world.skylightSubtracted = world.skyBrightness = 7;
        world.defaultFluid = Block.lavaMoving.blockID;
        if (levelGenerator.floatingGen) {
            world.cloudHeight = height + 2;
            levelGenerator.waterLevel = -16;
        }
    }
```

* A way to override default "Planting" stage completely. The `ModLoader` hook would return `false` if no match for `levelType` was found in the list of registered themes, `true` otherwise. And this method would be called to actually do the planting. If you don't need custom planting in your theme just put a `return false;` in this method or don't override the base class.

```java
    public boolean overridePlanting (LevelGenerator levelGenerator, World world) {

    }
```

Ok - let's write the base class.

```java
package com.mojontwins.modloader;

import net.minecraft.game.level.World;
import net.minecraft.game.level.generator.LevelGenerator;

public class ModLevelTheme {
    public int waterLevelAdjust = 0;                // in blocks; no change
    public String themeName = "";

    /*
     * Instantiate with the name which should appear in the "New Level" menu
     */
    public ModLevelTheme(String themeName) {
        this.themeName = themeName;
    }

    /*
     * Called each iteration to fill the empty block array with basic blocks
     * Return -1 for the default generation.
     */
    public int getSoilingBlockID (LevelGenerator levelGenerator, int y, int floorLevel, int fillLevel, int islandBottomLevel) {
        return -1;
    }
    
    /*
     * Normally beachLevel = levelGenerator.waterLevel -1. Leave unchanged or change it:
     */
    public int adjustBeachLevel (LevelGenerator levelGenerator, int beachLevel) {
        return beachLevel;
    }
    
    /*
     * Called each iteration decide if sand is to be added to the world.
     * return shouldGrow unchanged for the default behaviour, which is:
     * noiseValue > -8.0D for islandGen, or
     * noiseValue > 8.0D  for other gens.
     */
    boolean shouldGrow (LevelGenerator levelGeneartor, double noiseValue, boolean shouldGrow) {
        return shouldGrow; 
    }
    
    /*
     * Called each iteration to know which block to add while growing.
     * Return -1 for the default generation which is sand (grass for hell theme).
     */
    public int getGrowingBlockID (LevelGenerator levelGenerator) {
        return -1;
    }
    
    /*
     * Use to select a custom BlockID for "water" (not much choice)
     * Return -1 for the default generation which is Block.waterStill.BlockID;
     */
    public int getWateringBlockID (LevelGenerator levelGenerator) {
        return -1;
    }
    
    /*
     * Use this to modify any of these world values:
     * `world.skyColor` - 0x99CCFF by default
     * `world.fogColor` - 0xFFFFFF by default
     * `world.cloudColor` - 0xFFFFFF by default
     * `skylightSubtracted` Seems to be 15 by default. Skeletons & zombies only burn at day if it is > 7.
     * `skyBrightness` 15 by default.
     * `defaultFluid` - water or lava (for hell).
     */
    public void setVisuals (LevelGenerator levelGenerator, World world) {
    }
    
    /*
     * Do your planting and return true, 
     * or return false to let the engine do its thing.
     */
    public boolean overridePlanting (LevelGenerator levelGenerator, World world) {
        return false;
    }   
}

```

And the hooks in `ModLoader`.

```java
    /*
     * Register a new theme
     */
    public static int registerWorldTheme (ModLevelTheme levelTheme) {
        int themeID = currentThemeID ++;
        levelThemes.put (themeID, levelTheme);
        
        return themeID;
    }

    // etc...
```

So adding a new theme is a matter of extending `ModLevelTheme`, and then calling `registerWorldTheme` with an instance.

## The Desert Theme

To test this, I'm creating a Desert theme. But before I add the proper theme, I have to add Cacti and dead bushes, so I can make them spawn. I'll be adding a cactus generator as well. I don't know if I can make the cactus actually harm entities, but I'll try. Maybe I have to extend the engine, I don't know. Cacti are from Alpha 1.0.6. I can take the implementation from my uncompiled Alpha 1.1.1, should work. Mostly.

I will be adding the original 1.0.6. cacti which didn't require a custom renderer. The bounding box is set to be smaller tho', I can do this. The damage to entities is performed via `onEntityCollidedWithBlock`, which is not in Indev's `Block` - albeit there's a `onEntityWalking`. Let's check if it's the same thing...

Not. Both are called in `Entity.moveEntity`, but both have an `onEntityWalking`. Alpha has an extra block calling `onEntityCollideWithBlock` which I should replicate somehow.

This bit: 

```java
    int bbMinX = MathHelper.floor_double(this.boundingBox.minX);
    int bbMinY = MathHelper.floor_double(this.boundingBox.minY);
    int bbMinZ = MathHelper.floor_double(this.boundingBox.minZ);
    int bbMaxX = MathHelper.floor_double(this.boundingBox.maxX);
    int bbMaxY = MathHelper.floor_double(this.boundingBox.maxY);
    int bbMaxZ = MathHelper.floor_double(this.boundingBox.maxZ);

    for(int x = bbMinX; x <= bbMaxX; ++x) {
        for(int y = bbMinY; y <= bbMaxY; ++y) {
            for(int z = bbMinZ; z <= bbMaxZ; ++z) {
                int blockID = this.worldObj.getBlockId(x, y, z);
                if (blockID > 0) {
                    Block.blocksList[blockID].onEntityCollidedWithBlock(this.worldObj, x, y, z, this);
                }
            }
        }
    }
```

Let's add it :) And the default `onEntityCollidedWithBlock` to `Block`.

```java
    package com.mojontwins.modloader;

    import java.util.Random;

    import net.minecraft.client.physics.AxisAlignedBB;
    import net.minecraft.game.block.Block;
    import net.minecraft.game.block.Material;
    import net.minecraft.game.entity.Entity;
    import net.minecraft.game.level.World;

    public class BlockCactus extends ModBlock {
        
        public int bottomTextureIndex;
        public int topTextureIndex;

        public BlockCactus(int id, Material material) {
            super(id, material);
            this.setTickOnLoad(true);
        }

        public void updateTick(World var1, int var2, int var3, int var4, Random var5) {
            // Attempt to grow cactus
            if (var1.getBlockId(var2, var3 + 1, var4) == 0) {
                int var6;
                for(var6 = 1; var1.getBlockId(var2, var3 - var6, var4) == this.blockID; ++var6) {
                }

                // If not mex. height of 3 blocks...
                if (var6 < 3) {
                    int var7 = var1.getBlockMetadata(var2, var3, var4);
                    
                    // Can grow?
                    if (var7 == 15) {
                        var1.setBlockWithNotify(var2, var3 + 1, var4, this.blockID);
                        var1.setBlockMetadata(var2, var3, var4, 0);
                    } else {
                        var1.setBlockMetadata(var2, var3, var4, var7 + 1);
                    }
                }
            }
        }
        
        public AxisAlignedBB getCollisionBoundingBoxFromPool(World var1, int var2, int var3, int var4) {
            float var5 = 0.0625F;
            return new AxisAlignedBB (
                    (float)var2 + var5, var3, (float)var4 + var5, 
                    (float)(var2 + 1) - var5, (float)(var3 + 1), (float)(var4 + 1) - var5);
        }

        public AxisAlignedBB getSelectedBoundingBoxFromPool(World var1, int var2, int var3, int var4) {
            float var5 = 0.0625F;
            return new AxisAlignedBB (
                    (float)var2 + var5, var3, (float)var4 + var5, 
                    (float)(var2 + 1) - var5, (float)(var3 + 1), (float)(var4 + 1) - var5);
        }
        
        public int getBlockTextureFromSide(int var1) {
            if (var1 == 0) return this.bottomTextureIndex;
            if (var1 == 1) return this.topTextureIndex;
            return this.blockIndexInTexture; 
        }
        
        public boolean isOpaqueCube() {
            return false;
        }
        
        public boolean canPlaceBlockAt(World var1, int var2, int var3, int var4) {
            return !super.canPlaceBlockAt(var1, var2, var3, var4) ? false : this.canBlockStay(var1, var2, var3, var4);
        }
        
        public void onNeighborBlockChange(World var1, int var2, int var3, int var4, int var5) {
            if (!this.canBlockStay(var1, var2, var3, var4)) {
                this.dropBlockAsItem(var1, var2, var3, var4, var1.getBlockMetadata(var2, var3, var4));
                var1.setBlockWithNotify(var2, var3, var4, 0);
            }

        }

        public boolean canBlockStay(World var1, int var2, int var3, int var4) {
            if (var1.getBlockMaterial(var2 - 1, var3, var4).isSolid()) {
                return false;
            } else if (var1.getBlockMaterial(var2 + 1, var3, var4).isSolid()) {
                return false;
            } else if (var1.getBlockMaterial(var2, var3, var4 - 1).isSolid()) {
                return false;
            } else if (var1.getBlockMaterial(var2, var3, var4 + 1).isSolid()) {
                return false;
            } else {
                int var5 = var1.getBlockId(var2, var3 - 1, var4);
                return var5 == this.blockID || var5 == Block.sand.blockID;
            }
        }

        public void onEntityCollidedWithBlock(World var1, int var2, int var3, int var4, Entity var5) {
            var5.attackEntityFrom((Entity)null, 1);
        }
    }
```

And this cactus generator. Indev didn't have *World Generators*, but we can add our class and use it. Why not.

```java
    package com.mojontwins.modloader;

    import java.util.Random;

    import net.minecraft.game.level.World;

    public class WorldGenCactus {
        public WorldGenCactus() {
        }

        public boolean generate(World var1, Random var2, int var3, int var4, int var5) {
            for(int var6 = 0; var6 < 10; ++var6) {
                int var7 = var3 + var2.nextInt(8) - var2.nextInt(8);
                int var8 = var4 + var2.nextInt(4) - var2.nextInt(4);
                int var9 = var5 + var2.nextInt(8) - var2.nextInt(8);
                if (var1.getBlockId(var7, var8, var9) == 0) {
                    int var10 = 1 + var2.nextInt(var2.nextInt(3) + 1);

                    for(int var11 = 0; var11 < var10; ++var11) {
                        if (mod_DesertTheme.blockCactus.canBlockStay(var1, var7, var8 + var11, var9)) {
                            var1.setBlock(var7, var8 + var11, var9, mod_DesertTheme.blockCactus.blockID);
                        }
                    }
                }
            }

            return true;
        }
    }
```

Now the dead bush, which is just a ugly flower (or acts like one!) which can only grow on sand. Those were added on beta. As with lillypads, I'll have to manually add some stuff from `BlockFlower` as I want to extend `ModBlock`. No shears, so this is a really simple block: 

```java
    package com.mojontwins.modloader;

    import java.util.Random;

    import net.minecraft.client.physics.AxisAlignedBB;
    import net.minecraft.game.block.Block;
    import net.minecraft.game.block.Material;
    import net.minecraft.game.item.Item;
    import net.minecraft.game.level.World;

    public class BlockDeadBush extends ModBlock {

        public BlockDeadBush(int id) {
            super(id, Material.wood);

            float f = 0.4F;
            setBlockBounds(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, 0.8F, 0.5F + f);
        }

        public final boolean canPlaceBlockAt(World var1, int var2, int var3, int var4) {
            return this.canThisPlantGrowOnThisBlockID(var1.getBlockId(var2, var3 - 1, var4));
        }   
        
        protected boolean canThisPlantGrowOnThisBlockID(int par1) {
            return par1 == Block.sand.blockID;
        }
        
        public final void onNeighborBlockChange(World var1, int var2, int var3, int var4, int var5) {
            super.onNeighborBlockChange(var1, var2, var3, var4, var5);
            this.checkFlowerChange(var1, var2, var3, var4);
        }

        public void updateTick(World var1, int var2, int var3, int var4, Random var5) {
            this.checkFlowerChange(var1, var2, var3, var4);
        }

        private void checkFlowerChange(World var1, int var2, int var3, int var4) {
            if (!this.canBlockStay(var1, var2, var3, var4)) {
                this.dropBlockAsItem(var1, var2, var3, var4, var1.getBlockMetadata(var2, var3, var4));
                var1.setBlockWithNotify(var2, var3, var4, 0);
            }

        }
        
        public boolean canBlockStay(World var1, int var2, int var3, int var4) {
            return this.canThisPlantGrowOnThisBlockID(var1.getBlockId(var2, var3 - 1, var4));
        }
        
        public final AxisAlignedBB getCollisionBoundingBoxFromPool(int var1, int var2, int var3) {
            return null;
        }

        public final boolean isOpaqueCube() {
            return false;
        }

        public final boolean renderAsNormalBlock() {
            return false;
        }

        public int getRenderType() {
            return 1;
        }
        
        public int idDropped(int par1, Random rand, int par3) {
            // 1 in 4 chance of dropping a stick
            if (rand.nextInt(4) == 0) {
                return Item.stick.shiftedIndex;
            } else return -1;
        }
    }
```

With this on, I just need my theme class which extends `ModLevelTheme`. I just need some of the hooks for what I want:

```java
    package com.mojontwins.modloader;

    import net.minecraft.game.block.Block;
    import net.minecraft.game.block.BlockFlower;
    import net.minecraft.game.level.World;
    import net.minecraft.game.level.generator.LevelGenerator;

    public class ThemeDesert extends ModLevelTheme {

        public ThemeDesert(String themeName) {
            super(themeName);
        }

        /*
         * This method is called just before converting the double floor level to int
         * and store it in the heighmap. You can further adjust it.
         */
        /*
        public double adjustFloorLevel (LevelGenerator levelGenerator, double floorLevel) {
            return floorLevel > 0.0D ? floorLevel * 4.0D : floorLevel;
        }
        */
        // (Changed my mind - wont use this)

        /*
         * In the "Soiling" stage, the heightmap and a couple of noise generators are used to fill
         * the blocks array with block, one column at a time. This method is called for each "y"
         * in each column to select which block ID to put to the block array. For our desert,
         * we'll be filling with sand from the top `floorLevel` to `fillLevel` (note that `fillLevel`
         * may go over `floorLevel` sometimes), and from `fillLevel` down with stone. If the
         * level generator is `floatingGen`, the bottom (`islandBottomLevel` & below) is filled with zeroes.
         */
        public int getSoilingBlockID (LevelGenerator levelGenerator, int y, int floorLevel, int fillLevel, int islandBottomLevel) {
            int blockID = 0;
            if (y <= floorLevel) {
                blockID = Block.sand.blockID;
            }

            if (y <= fillLevel) {
                blockID = Block.stone.blockID;
            }

            if (levelGenerator.floatingGen && y < islandBottomLevel) {
                blockID = 0;
            }
            
            return blockID;
        }
        
        /*
         * During the "Growing" stage the generator originally generated sand in some places
         * We are generating dirt instead.
         */
        public int getGrowingBlockID (LevelGenerator levelGenerator) {
            return Block.dirt.blockID;
        }
        
        /*
         * Set yellowish sandy shades for the sky, fog and clouds. Set the general light
         * quite bright.
         */
        public void setVisuals (LevelGenerator levelGenerator, World world) {
            world.skyColor = 0xCEBFA1;
            world.fogColor = 0xE2E1A6;
            world.cloudColor = 0xFFFED4;
            world.skylightSubtracted = 15;
            world.skyBrightness = 16;
        }
        
        /*
         * The main "planting" sections grows trees, flowers and mushrooms. We are
         * overriding that and growing cacti and dead bushes. We are adding mushrooms
         * as well, but below the water level, so they appear in caves underground.
         */
        public boolean overridePlanting (LevelGenerator levelGenerator, World world) {
            int totalBlocks = world.width * world.length * world.height;
            
            // Spawn cacti
            int cacti = totalBlocks / 500;
            WorldGenCactus worldGenCactus = new WorldGenCactus ();
            for (int i = 0; i < cacti; i ++) {
                int x = levelGenerator.rand.nextInt(world.width);
                int y = levelGenerator.rand.nextInt(world.height);
                int z = levelGenerator.rand.nextInt(world.length);
                worldGenCactus.generate(world, levelGenerator.rand, x,  y,  z);
            }
            
            // Spawn dead bushes
            int deadBushes = totalBlocks / 50;
            for (int i = 0; i < deadBushes; i ++) {
                int x = levelGenerator.rand.nextInt(world.width);
                int y = levelGenerator.rand.nextInt(world.height);
                int z = levelGenerator.rand.nextInt(world.length);
                if (((BlockDeadBush)mod_DesertTheme.blockDeadBush).canBlockStay(world, x, y, z)) {
                    world.setBlock(x, y, z, mod_DesertTheme.blockDeadBush.blockID);
                }
            }
            
            // Grow shrooms underground
            int mushrooms = totalBlocks / 4000;

            for(int i = 0; i < mushrooms; ++i) {
                int x0 = levelGenerator.rand.nextInt(world.width);
                int y0 = levelGenerator.rand.nextInt(world.waterLevel);
                int z0 = levelGenerator.rand.nextInt(world.length);
                
                BlockFlower blockMushroom = levelGenerator.rand.nextBoolean() ? Block.mushroomBrown : Block.mushroomRed;

                for(int j = 0; j < 10; ++j) {
                    int x = x0;
                    int y = y0;
                    int z = z0;

                    for(int k = 0; k < 10; ++k) {
                        x += levelGenerator.rand.nextInt(4) - levelGenerator.rand.nextInt(4);
                        y += levelGenerator.rand.nextInt(2) - levelGenerator.rand.nextInt(2);
                        z += levelGenerator.rand.nextInt(4) - levelGenerator.rand.nextInt(4);
                        if (x >= 0 && y >= 0 && z > 0 && x < world.width && y < world.length && z < world.height && world.getBlockId(x, y, z) == 0 && blockMushroom.canBlockStay(world, x, y, z)) {
                            world.setBlockWithNotify(x, y, z, blockMushroom.blockID);
                        }
                    }
                }
            }
            
            return true;
        }
    }
```

TODO: Smelt cacti for food. 

### Intermision: Level theme and spawner

I need to devise a good way of limiting which mobs will spawn depending on the active level theme, as in by default all mobs can be spawned, but you can stop some existing mobs from spawning in your theme.

Luckily, I'm now storing `levelType` in the world object.

I'm rehashing how spawning lists work. Now you should add your mobs in a special method in your mod - which will be called everytime a level is about to be created, so you can control if you are populating the maps or not.

Have to document this urgently.

## The Poison level

Ideas:

* Default fluid is a new "poison" fluid which inflicts the "posioned" effect when touched by entities.
* You can fill bottles with poison and throw them to the enemies <- interesting, replicate arrows or snowballs in later versions.
* Soil is podzol.
* Glowing huge mushrooms and small mushrooms.
* Smelt mushrooms blocks for poison. Or add a new tile entity to distill poison.
* Use sand to make glass, and glass to make bottles.
* New skeleton variation.
* Make tall mesas:

This seems to make nice tall mesas: 

```java
    public double adjustFloorLevel (LevelGenerator levelGenerator, double floorLevel) {
        if (floorLevel > 8.0D) floorLevel += 16.0D;
        return floorLevel;
    }
```

Study how to change (or parametrize) the cave generator for bigger caves.

### The podzol block

I'm gonna replicate early vanilla mycellium behaviour for the soil, but using podzol textures.

```java
    package com.mojontwins.modloader;

    import java.util.Random;

    import net.minecraft.game.block.Block;
    import net.minecraft.game.block.Material;
    import net.minecraft.game.level.World;

    public class BlockPodzol extends ModBlock {
        public int bottomTextureIndex;
        public int topTextureIndex;
        
        public BlockPodzol(int id) {
            super(id, Material.ground);
            this.setTickOnLoad(true);
        }

        public int getBlockTextureFromSide (int var1) {
            if (var1 == 0) return this.bottomTextureIndex;
            if (var1 == 1) return this.topTextureIndex;
            return this.blockIndexInTexture;        
        }

         public void updateTick(World world, int x, int y, int z, Random par5Random) {
            if (world.getBlockLightValue(x, y + 1, z) < 4 && Block.lightOpacity[world.getBlockId(x, y + 1, z)] > 2) {
                world.setBlockWithNotify(x, y, z, Block.dirt.blockID);
            } else if (world.getBlockLightValue(x, y + 1, z) >= 9) {
                for (int i = 0; i < 4; i++) {
                    int xx = (x + par5Random.nextInt(3)) - 1;
                    int yy = (y + par5Random.nextInt(5)) - 3;
                    int zz = (z + par5Random.nextInt(3)) - 1;
                    int belowBlockID = world.getBlockId(xx, yy + 1, zz);

                    if (world.getBlockId(xx, yy, zz) == Block.dirt.blockID && world.getBlockLightValue(xx, yy + 1, zz) >= 4 && Block.lightOpacity[belowBlockID] <= 2) {
                        world.setBlockWithNotify(xx, yy, zz, blockID);
                    }
                }
            }
        }

        public int idDropped(int par1, Random par2Random) {
            return Block.dirt.idDropped(0, par2Random);
        }
    }
```

and the setup in the mod class:

```java
    blockPodzol = new BlockPodzol(ModLoader.getBlockId()).setBlockHardness(0.25F).setName("block.podzol");
    blockPodzol.stepSound = Block.soundGrassFootstep;
    blockPodzol.blockIndexInTexture = ModLoader.addOverride(EnumTextureAtlases.TERRAIN, "textures/block_podzol_size.png");
    ((BlockPodzol)blockPodzol).topTextureIndex = ModLoader.addOverride(EnumTextureAtlases.TERRAIN, "textures/block_podzol_top.png");
    ((BlockPodzol)blockPodzol).bottomTextureIndex = Block.dirt.blockIndexInTexture;
    ModLoader.registerBlock(blockPodzol);
```

### Giant mushrooms

And now the blocks used to build the giant mushrooms. For the cap blocks, metadata contains the combination of "inner" and "outter" textures in all six faces. Because I won't be reinventing the wheel, I'm using the code in r1.2.5. I'll be even using a simplified `WorldGenGiantMushroom`.  The same block class is used for the trunk, with metadata = 10.

```java
    package com.mojontwins.modloader;

    import java.util.Random;

    import net.minecraft.game.block.Material;

    public class BlockBigMushroom extends ModBlock {
        public int mushroomType;
        
        public int textureStem;
        public int textureCap;
        
        public BlockBigMushroom(int id, int type) {
            super(id, Material.wood);
            mushroomType = type;
        }

        public int getBlockTextureFromSideAndMetadata(int par1, int par2)
        {
            // meta = 10: stem, par > 1: sides
            if (par2 == 10 && par1 > 1) {
                return textureStem;
            }

            // Bottom
            if (par2 >= 1 && par2 <= 9 && par1 == 1) {
                return textureCap;
            }

            // Side 2
            if (par2 >= 1 && par2 <= 3 && par1 == 2) {
                return textureCap;
            }

            // Side 3
            if (par2 >= 7 && par2 <= 9 && par1 == 3) {
                return textureCap;
            }

            // Side 4
            if ((par2 == 1 || par2 == 4 || par2 == 7) && par1 == 4) {
                return textureCap;
            }

            // Side 5
            if ((par2 == 3 || par2 == 6 || par2 == 9) && par1 == 5) {
                return textureCap;
            }

            // Whole
            if (par2 == 14) {
                return textureCap;
            }

            // All trunk
            if (par2 == 15) {
                return textureStem;
            }
            
            // Inside
            return blockIndexInTexture;
        }
        
        public int quantityDropped(Random par1Random) {
            int i = par1Random.nextInt(10) - 7;
            if (i < 0) i = 0;
            return i;
        }

        public int idDropped(int par1, Random par2Random, int par3) {
            return blockID;
        }
    }
```

And the setup in the mod class:

```java
    blockBigMushroomGreen = new BlockBigMushroom(ModLoader.getBlockId(), 1).setBlockHardness(0.25F).setName("block.big_mushroom_green");
    blockBigMushroomGreen.setBlockLightValue(0.875F);
    blockBigMushroomGreen.stepSound = Block.soundWoodFootstep;
    blockBigMushroomGreen.blockIndexInTexture = ModLoader.addOverride(EnumTextureAtlases.TERRAIN, "textures/block_mushroom_inside.png");
    ((BlockBigMushroom)blockBigMushroomGreen).textureCap = ModLoader.addOverride(EnumTextureAtlases.TERRAIN, "textures/block_mushroom_green.png");
    ((BlockBigMushroom)blockBigMushroomGreen).textureStem = ModLoader.addOverride(EnumTextureAtlases.TERRAIN, "textures/block_mushroom_trunk.png");
    ModLoader.registerBlock(blockBigMushroomGreen);
    
    blockBigMushroomBrown = new BlockBigMushroom(ModLoader.getBlockId(), 0).setBlockHardness(0.25F).setName("block.big_mushroom_brown");
    blockBigMushroomBrown.stepSound = Block.soundWoodFootstep;
    blockBigMushroomBrown.blockIndexInTexture = blockBigMushroomGreen.blockIndexInTexture;
    ((BlockBigMushroom)blockBigMushroomBrown).textureCap = ModLoader.addOverride(EnumTextureAtlases.TERRAIN, "textures/block_mushroom_brown.png");
    ((BlockBigMushroom)blockBigMushroomBrown).textureStem = ((BlockBigMushroom)blockBigMushroomGreen).textureStem;  
    ModLoader.registerBlock(blockBigMushroomBrown);
```

Next: the new fluid. Harms without burning, flows as water. Is yellowish green. No use inside a bottle.

I guess I should make the texture FX first... I might be taking the chance to use an animated texture using a local texture atlas and add this to modLoader. Let's find something in google. Yay.

The animated texture FX should load the texture atlas (16x16 frames stitched vertically) and calculate automaticly the number of frames & setup everything. Maybe get a ticks per frame in the constructor. Something like this:

```java
    package com.mojontwins.modloader;

    import java.awt.image.BufferedImage;
    import java.io.InputStream;

    import javax.imageio.ImageIO;

    import net.minecraft.client.renderer.block.TextureFX;

    public class ModTextureAnimated extends TextureFX {
        private int animationFrames;
        private int animationCounter;
        private int ticksPerFrame;
        private int ticksCounter;
        private int rawAnimationData [];
        private byte animationData [][];
        
        public ModTextureAnimated(int textureIndex, EnumTextureAtlases textureAtlas, String textureAtlasURI, int ticksPerFrame) {
            super(textureIndex);
            this.tileImage = textureAtlas == EnumTextureAtlases.ITEMS ? 1 : 0;
            this.ticksPerFrame = ticksPerFrame;
            
            try {
                // Load texture
                InputStream inputStream = this.getClass().getResourceAsStream(textureAtlasURI);
                BufferedImage bufferedimage = ImageIO.read(inputStream);
                
                // Calculate number of frames
                animationFrames = bufferedimage.getHeight() / 16;
                
                // Allocate rawAnimationData & animationData
                rawAnimationData = new int [256 * animationFrames];
                animationData = new byte [animationFrames][1024];
                
                // Extract pixels from bufferedimage
                bufferedimage.getRGB(0, 0, bufferedimage.getWidth(), bufferedimage.getHeight(), rawAnimationData, 0, 16);
                
                // Load the texture to the `animationData` array in the correct format
                for (int i = 0; i < animationFrames; i ++) 
                    for (int j = 0; j < 256; j ++) {
                        int idx = i * 256 + j;
                        animationData [i][4 * j + 0] = (byte) (rawAnimationData[idx] >> 16 & 0xff);
                        animationData [i][4 * j + 1] = (byte) (rawAnimationData[idx] >> 8 & 0xff); 
                        animationData [i][4 * j + 2] = (byte) (rawAnimationData[idx] & 0xff); 
                        animationData [i][4 * j + 3] = (byte) (rawAnimationData[idx] >> 24 & 0xff); 
                    }
                
                System.out.println ("ModTextureAnimated " + textureIndex + ", " + textureAtlas + ", textureAtlasURI = " + textureAtlasURI + " (" + animationFrames + " frames)");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void onTick () {
            imageData = animationData [animationCounter];
            ticksCounter ++;
            if (ticksPerFrame == ticksCounter) {
                ticksCounter = 0;           
                animationCounter ++;
                if (animationCounter == animationFrames) animationCounter = 0;
            }
        }
    }
```

Now I have to integrate this in the fashion of `addOverride`, some sort of `addAnimation` which does its magic. As with normal, fixed texture overrides, animations need to be associated to a texture index in one of the atlases, so...

I have to test this fast. Let's add seaweeds. Another plant. Gosh, I can only think of plants.

```java
    package com.mojontwins.modloader;

    import java.util.Random;

    import net.minecraft.client.physics.AxisAlignedBB;
    import net.minecraft.game.block.Block;
    import net.minecraft.game.block.Material;
    import net.minecraft.game.level.World;

    public class BlockSeaWeed extends ModBlock {

        public BlockSeaWeed(int id) {
            super(id, Material.water);
            this.setTickOnLoad(true);
        }

        public boolean canPlaceBlockAt(World world, int x, int y, int z) {
            return world.getBlockId(x, y, z) == Block.waterStill.blockID 
                    && world.getBlockId(x, y + 1, z) == Block.waterStill.blockID 
                    && canThisPlantGrowOnThisBlockID(world.getBlockId(x, y - 1, z));
        }
        
        protected boolean canThisPlantGrowOnThisBlockID(int par1) {
            return par1 == blockID || par1 == Block.dirt.blockID 
                    || par1 == Block.sand.blockID
                    || par1 == Block.stone.blockID;
        }    
        
        public void onNeighborBlockChange(World world, int x, int y, int z, int neighborBlockID) {
            if (!canBlockStay(world, x, y, z)) {
                dropBlockAsItem(world, x, y, z, world.getBlockMetadata(x, y, z));
                world.setBlockWithNotify(x, y, z, Block.waterStill.blockID);
            }
        }
        
        public void updateTick(World world, int x, int y, int z, Random rand) {
            if (rand.nextInt (4) == 0) {
                if (world.getBlockId(x, y + 1, z) == Block.waterStill.blockID && world.getBlockId(x, y + 2, z) != 0) {
                    world.setBlockWithNotify(x, y + 1, z, blockID);
                }
            }
        }
        
        public boolean canBlockStay(World world, int x, int y, int z) {
            return canThisPlantGrowOnThisBlockID(world.getBlockId(x, y - 1, z));
        }
        
        public AxisAlignedBB getCollisionBoundingBoxFromPool(World par1World, int par2, int par3, int i) {
            return null;
        }
        
        public boolean isOpaqueCube() {
            return false;
        }
        
        public boolean renderAsNormalBlock() {
            return false;
        }
        
        public int getRenderType() {
            return 1;
        }
    }
```

And we create the new block in the mod class like this:

```java
    // Seaweeds with animated textures
    blockSeaWeed = new BlockSeaWeed(ModLoader.getItemId()).setBlockHardness(0.2F).setName("block.sea_weed");
    blockSeaWeed.blockIndexInTexture = ModLoader.addAnimation(EnumTextureAtlases.TERRAIN, "textures/block_seaweed.png", 1);
    ModLoader.registerBlock(blockSeaWeed);      
```

Finally, we generate some. I'm adding a new method to `World` which will come up VERY handy!

```java
    public int getSeaBed(int x, int z) {
        // Start at water level downwards
        int y = this.waterLevel;
        
        if (this.getBlockId(x, y, z) != Block.waterStill.blockID) return 0;
        
        while (y > 0) {
            y --;
            if (this.getBlockId(x, y, z) != Block.waterStill.blockID) break;
        }
        
        return y;
    }
```

As I won't be able to test this until I get home, let's document the bit and add the example.

Before I add the new fluid, I've ported the world generator for big mushrooms, adapted from r1.2.5 to my codebase:

```java
    package com.mojontwins.modloader;

    import java.util.Random;

    import net.minecraft.game.block.Block;
    import net.minecraft.game.level.World;

    // Adapted from r1.2.5

    public class WorldGenBigMushroom {
        private int mushroomType; // 0 for brown, 1 for green.

        public WorldGenBigMushroom(int mushroomType) {
            this.mushroomType = mushroomType;
        }

        public WorldGenBigMushroom() {
            this (0);
        }

        public boolean generate(World world, Random rand, int x, int y, int z)
        {
            int mushroomBlockID = mushroomType == 1 ? mod_PoisonLand.blockBigMushroomGreen.blockID : mod_PoisonLand.blockBigMushroomBrown.blockID;

            int j = rand.nextInt(3) + 4;

            if (y < 1 || y + j + 1 >= world.height) {
                return false;
            }

            // Enough room ?

            for (int yy = y; yy <= y + 1 + j; yy++) {
                byte byte0 = 3;

                if (yy == y) {
                    byte0 = 0;
                }

                for (int xx = x - byte0; xx <= x + byte0; xx++) {
                    for (int zz = z - byte0; zz <= z + byte0; zz++) {
                        if (yy >= 0 && yy < world.height) {
                            int blockID = world.getBlockId(xx, yy, zz);

                            if (blockID != 0 && blockID != Block.leaves.blockID) {
                                return false;
                            }
                        } else {
                            return false;
                        }
                    }
                }
            }

            // Dirt, grass or podzol below?
            int blockIDbelow = world.getBlockId(x, y - 1, z);

            if (blockIDbelow != Block.dirt.blockID && blockIDbelow != Block.grass.blockID && blockIDbelow != mod_PoisonLand.blockPodzol.blockID) {
                return false;
            }

            // Change for dirt
            world.setBlock(x, y - 1, z, Block.dirt.blockID);
            world.setBlockMetadata(x, y - 1, z, 0);
            
            // Draw the cap
            int i1 = y + j;

            if (mushroomType == 1) {
                i1 = (y + j) - 3;
            }

            for (int k1 = i1; k1 <= y + j; k1++) {
                int j2 = 1;

                if (k1 < y + j) {
                    j2++;
                }

                if (mushroomType == 0) {
                    j2 = 3;
                }

                for (int i3 = x - j2; i3 <= x + j2; i3++) {
                    for (int j3 = z - j2; j3 <= z + j2; j3++) {
                        int k3 = 5;

                        if (i3 == x - j2) {
                            k3--;
                        }

                        if (i3 == x + j2) {
                            k3++;
                        }

                        if (j3 == z - j2) {
                            k3 -= 3;
                        }

                        if (j3 == z + j2) {
                            k3 += 3;
                        }

                        if (mushroomType == 0 || k1 < y + j) {
                            if ((i3 == x - j2 || i3 == x + j2) && (j3 == z - j2 || j3 == z + j2)) {
                                continue;
                            }

                            if ((i3 == x - (j2 - 1) && j3 == z - j2) || (i3 == x - j2 && j3 == z - (j2 - 1))) {
                                k3 = 1;
                            }

                            if ((i3 == x + (j2 - 1) && j3 == z - j2) || (i3 == x + j2 && j3 == z - (j2 - 1))) {
                                k3 = 3;
                            }

                            if ((i3 == x - (j2 - 1) && j3 == z + j2) || (i3 == x - j2 && j3 == z + (j2 - 1))) {
                                k3 = 7;
                            }

                            if ((i3 == x + (j2 - 1) && j3 == z + j2) || (i3 == x + j2 && j3 == z + (j2 - 1))) {
                                k3 = 9;
                            }
                        }

                        if (k3 == 5 && k1 < y + j) {
                            k3 = 0;
                        }

                        if ((k3 != 0 || y >= (y + j) - 1) && !Block.opaqueCubeLookup[world.getBlockId(i3, k1, j3)]) {
                            world.setBlockWithNotify(i3, k1, j3, mushroomBlockID);
                            world.setBlockMetadata(i3, k1, j3, k3);
                        }
                    }
                }
            }

            // Trunk
            for (int l1 = 0; l1 < j; l1++) {
                int k2 = world.getBlockId(x, y + l1, z);

                if (!Block.opaqueCubeLookup[k2]) {
                    world.setBlockWithNotify(x, y + l1, z, mushroomBlockID);
                    world.setBlockMetadata(x, y + l1, z, 10);
                }
            }

            return true;
        }
    }
```

### Acid ocean

Ok - now on to Indev fluids again. Lava seems to be more simple than water. Nah, in fact is the same. On Indev you need three kinds of blocks: source, still and flowing using classes `BlockFluidSource`, `BlockStationary` and `BlockFlowing`, respectively. These methods have the hardcoded stuff needed for water and lava, so if I want to add my own fluid I'll have to replicate the entire classes.

`lavaSource` and `waterSource` are used in `World.fluidFlowCheck`. If I add new fluids I need a hook in there, so maybe it would be a good idea to make custom fluids a thing in ModLoader.

`*Still` and `*Moving` also appear in `ItemBlock.onItemUse` in the check which allows you to place new blocks in the world, so this shit is getting more and more complicated.

Appart from that that's apparently it, so let's thing on a new custom fluid system which nobody will use but which I can extract dopamine from.

I can be usin a `TreeMap` to store a list of fluids. Each element in the list would be a `HashMap` containing three elements "source", "still" and "moving", pointing to the right block object. So creating your fluid would need registering it on this map. A couple of hooks to modloader would use this map to provide the info the engine needs. Now let's examine the block classes.

* `BlockFluidSource` - it seems that I could just use this, directly. The constructor is `BlockFluidSource (blockID, movingFluidBlockID)`, with `movingFluidBlockID` the ID of the fluid it represents, in `moving` form.

* `BlockFlowing` extends `BlockFluid` has a lot of `lava` and `water` hardcoded shit:

    *  `BlockFlowing`: `blockIndexInTexture` is hardcoded. Setting `movingId` and `stillId` assumes sequantial IDs. Water flowing to lava to make stone is hardcoded. Water extinguishing fires is hardcoded. Lava spreading fire is hardcoded. Selecting the renderBlockPass (0 for solid, 1 for translucid) is hardcoded.
    *  `BlockFluid`: `blockIndexInTexture` is hardcoded. Setting `movingId` and `stillId` assumes sequantial IDs - duplicated code, also :-/. Clean up the constructor in `blockFlowing` and everything is set up here! `getBlockTextureFromSide` can be overriden. 

A complete rehash of `BlockFlowing` and `BlockFluid` is needed if I want to reuse them. A number of properties which `lava` and `water` preset and use them for the checks rather than the actual material:

* `blockIndexInTexture` calculation (duplicated in `BlockFlowing` and the super class `BlockFluid`).
* `stoneComponentLava` and `stoneComponentWater`, booleans.
* `exitinguishesFire`, boolean.
* `setsOnFire`, boolean.
* `renderBlockPass`, integer 0 or 1.

I can try and change one at a time and see if I don't break this completely :)

Or as `Material.lava` and `Material.water` are used most of the time, I could just leave those in and fix only the harcoded references to block IDs. That way you can create your own fluids of `Material.lava` and `Material.water` and if you need you can override the methods you need.

* In `BlockFlowing.extinguishFireLava` you have this:

```java
    if (var0.getBlockId(var1, var2, var3) != Block.lavaMoving.blockID && var0.getBlockId(var1, var2, var3) != Block.lavaStill.blockID)
```

which is in fact checking for lava so you could just:

```java
    if (Block.blocksList[var0.getBlockId(var1, var2, var3)].material == Material.lava)
```

So with this simple fix and using `Material`s you can do stuff. 

* `BlockStationary` is fine as it is.

So to add your new fluid you would just need a class extending `BlockFlowing`, plus an instance of `BlockFluidSource` and `BlockStationary`. As for the name needed for the future registry for IDs, I could just automate things and if a name attribute is not present in the class I could just use `.getClass ().toString ()` as the name tag.

Yup, but what happens with collision? This is also hardcoded in `Entity`:

```java
    if (this.handleLavaMovement()) {
        this.attackEntityFrom((Entity)null, 10);
        this.fire = 600;
    }
```

I could leave this as is but for some reason I don't like the idea of not being able to control this. Maybe another hook? Like

```java
    if (ModLoader.handleCustomFluidMovement (this.boundingBox.expand(0.0F, -0.4F, 0.0F))) {
    } else {
        this.attackEntityFrom((Entity)null, 10);
        this.fire = 600;        
    }
```

`this.boundingBox.expand(0.0F, -0.4F, 0.0F)` returns an `AxisAlignedBB`. Do your shit and return false or whatever. In `handleCustomMovement` I could call public final boolean handleMaterialAcceleration(AxisAlignedBB var1, Material var2) {} and then override. Have my HashMap describing fluids include a "damageValue" (which can be negative!) and "fireTicks" which can be 0.

Phew. 

Let's this rest for a bit.

What would be the best way to index our TreeMap? How should it be accessed? I think that adding it twice, indexing by the "still" id and by the "flowing" id would be the best solution.

Anyways, I implemented it:

```java
    // Blocks used for the new fluid
    
    int blockAcidFlowingID = ModLoader.getBlockId();
    int blockAcidStillID = ModLoader.getBlockId();
    
    blockAcidFlowing = (BlockAcidFlowing) new BlockAcidFlowing(blockAcidFlowingID, blockAcidStillID).setName("block.poison_flowing");
    blockAcidFlowing.blockIndexInTexture = ModLoader.addAnimation(EnumTextureAtlases.TERRAIN, "textures/block_acidwater.png", 1);
    ModLoader.registerBlock(blockAcidFlowing);
    
    blockAcidStill = (BlockStationary) new BlockStationary(blockAcidFlowingID, blockAcidStillID, Material.water).setName("block.poison_still");
    blockAcidStill.blockIndexInTexture = blockAcidFlowing.blockIndexInTexture;
    ModLoader.registerBlock(blockAcidStill);
    
    blockAcidSource = (BlockFluidSource) new BlockFluidSource(ModLoader.getBlockId(), blockAcidFlowingID).setName("block.poison_source");
    blockAcidSource.material = Material.water;
    blockAcidSource.blockIndexInTexture = blockAcidFlowing.blockIndexInTexture;
    ModLoader.registerBlock(blockAcidFlowing);
    
    // Register the new fluid
    
    ModLoader.registerFluid(blockAcidSource, blockAcidStill, blockAcidFlowing, 1, 0);
```

The level theme is almost finished (at least on its first incarnation):

```java
    package com.mojontwins.modloader;

    import net.minecraft.game.block.Block;
    import net.minecraft.game.level.World;
    import net.minecraft.game.level.generator.LevelGenerator;

    public class ThemePoisonLand extends ModLevelTheme {
        public int waterLevelAdjust = 2; 
        
        public ThemePoisonLand(String themeName) {
            super(themeName);
        }

        public double adjustFloorLevel (LevelGenerator levelGenerator, double floorLevel) {
            return floorLevel > 8.0F ? floorLevel + 12 : (floorLevel < 0.0F ? floorLevel * 2 : floorLevel);
        }

        public int getSoilingBlockID (LevelGenerator levelGenerator, int y, int floorLevel, int fillLevel, int islandBottomLevel) {
            int blockID = 0;
            
            if (y == floorLevel) {
                blockID = mod_PoisonLand.blockPodzol.blockID;
            }
            
            if (y < floorLevel) {
                blockID = Block.dirt.blockID;
            }

            if (y <= fillLevel) {
                blockID = Block.stone.blockID;
            }

            if (levelGenerator.floatingGen && y < islandBottomLevel) {
                blockID = 0;
            }
            
            return blockID;
        }
        
        public int getWateringBlockID (LevelGenerator levelGenerator) {
            return mod_PoisonLand.blockAcidStill.blockID;
        }
        
        public void setVisuals (LevelGenerator levelGenerator, World world) {
            world.skyColor = 0x0B0C33;
            world.fogColor = 0x3AB14E;
            world.cloudColor = 0x4D4FA0;
            world.skylightSubtracted = 6;
            world.skyBrightness = 7;
            world.defaultFluid = mod_PoisonLand.blockAcidFlowing.blockID;
            world.groundLevel = world.waterLevel - 2;
        }   
        
        public boolean overridePlanting (LevelGenerator levelGenerator, World world) {
            levelGenerator.populateFlowersAndMushrooms(world, Block.mushroomBrown, 500);
            
            int totalBlocks = world.width * world.length * world.height;
            
            // Spawn big mushrooms
            int bigMushrooms = totalBlocks / 5000;
            for (int i = 0; i < bigMushrooms; i ++) {
                int x = levelGenerator.rand.nextInt(world.width);
                int z = levelGenerator.rand.nextInt(world.length);
                int y = world.getHighestGround(x, z) + 1;
                if (y < world.waterLevel + 16) {
                    (new WorldGenBigMushroom (levelGenerator.rand.nextInt(2))).generate(world, levelGenerator.rand, x,  y,  z);
                }
            }
            
            // Spawn trees on high mesas
            int trees = totalBlocks / 10000;

            for(int i = 0; i < trees; ++i) {
                int x0 = levelGenerator.rand.nextInt(world.width);
                int z0 = levelGenerator.rand.nextInt(world.length);
                int y0 = world.getHighestGround(x0, z0);

                int x = x0;
                int y = y0;
                int z = z0;

                if (y0 > world.waterLevel + 16) for(int j = 0; j < 5; ++j) {
                    
                    x += levelGenerator.rand.nextInt(12) - levelGenerator.rand.nextInt(12);
                    z += levelGenerator.rand.nextInt(12) - levelGenerator.rand.nextInt(12);
                    
                   if (world.getBlockId(x, y, z) != 0) {
                        world.setBlock(x, y, z, Block.dirt.blockID);
                        world.growTrees(x, y + 1, z);
                    }
                }
            }
            
            return true;
        }
    }
```

### Bottles

Bottles are items which can be filled with water, acid or poison. I'll be adding a new `ItemBottle` class which can be instantiated with `contents`, just like I implemented the bucket before. Bottles are stackable so it won't be as simple, but not very complicated. I'll be implementing the "fill with water" logic first, then I'll figure out how to make a throwable bottle (which will be a custom entity). So this is our first version: Bottle can only be filled.

```java
    package com.mojontwins.modloader;

    import net.minecraft.client.physics.MovingObjectPosition;
    import net.minecraft.game.block.Block;
    import net.minecraft.game.entity.player.EntityPlayer;
    import net.minecraft.game.item.ItemStack;
    import net.minecraft.game.level.World;

    public class ItemBottle extends ModItem {
        // What's inside the bottle?
        int contents; 
        
        public ItemBottle(int var1, int contents) {
            super(var1);
            this.contents = contents;
            this.maxStackSize = 1;
        }

        public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer entityPlayer) {
            // First we detect if we hit something
            MovingObjectPosition movingobjectposition = getMovingObjectPositionFromPlayer(world, entityPlayer, true);

            if (movingobjectposition != null && movingobjectposition.typeOfHit == 0) {
                int x = movingobjectposition.blockX;
                int y = movingobjectposition.blockY;
                int z = movingobjectposition.blockZ;

                int blockID = world.getBlockId(x, y, z);

                if (this.contents == 0) {
                    if (blockID == Block.waterStill.blockID) {
                        world.playSoundAtPlayer((float)x + 0.5F, (float)y + 0.5F, (float)z + 0.5F, "random.splash", world.random.nextFloat() * 0.25F + 0.75F,  world.random.nextFloat() + 0.5F);
                        
                        // Substitute the hit block with air
                        world.setBlockWithNotify(x, y, z, 0);
                        
                        // Replace this item with a filled bottle
                        return new ItemStack (mod_PoisonLand.itemBottleWater);
                    } 

                    if (blockID == mod_PoisonLand.blockAcidStill.blockID ) {
                        world.playSoundAtPlayer((float)x + 0.5F, (float)y + 0.5F, (float)z + 0.5F, "random.splash", world.random.nextFloat() * 0.25F + 0.75F,  world.random.nextFloat() + 0.5F);
                        
                        // Substitute the hit block with air
                        world.setBlockWithNotify(x, y, z, 0);
                        
                        // Replace this item with a filled bottle
                        return new ItemStack (mod_PoisonLand.itemBottleAcid);                   
                    }
                }
            }

            return itemStack;
        }   
    }
```

### Trowing bottles.

I guess I can take the code for arrows and replicate it somewhat. I'm more interested on what to do with the renderer. What does r1.2.5 do? Also, maybe arrows are TOO complicated for what I need? I don't need states nor recollection. I just need to throw a bottle which breaks on collision and maybe adds a status effect.

`EntityPotion` in r1.2.5 is not yet 100% deobfuscated, but I think I can work it out. It extends `EntityThrowable`, which is NOT a luxury I have in Indev. So whether I'm thinking on implementing a `ModEntityThrowable` which I can reuse.

So I'll pick stuff from it for my own, simple, Indev style `ModEntityThrowableSimple`.

The idea is extending minimally this class to create your own throwables, as in this:

```java
    package com.mojontwins.modloader;

    import net.minecraft.client.physics.MovingObjectPosition;
    import net.minecraft.game.entity.EntityLiving;
    import net.minecraft.game.item.Item;
    import net.minecraft.game.level.World;

    public class EntityThrowableBottle extends ModEntitlyThrowableSimple {
        public Item itemBottle;

        public EntityThrowableBottle(World world, EntityLiving owner, float speedMultiplier, Item itemBottle) {
            super(world, owner, speedMultiplier);
            this.itemBottle = itemBottle;
        }

        protected String getEntityString() {
            return "throwable_bottle";
        }
        
        public void onImpact(MovingObjectPosition movingObjectPosition) {
            // TODO: Do stuff depending on contents.
        }
    }
```

We need a renderer. I'll be adding a `ModRenderThrowableSimple` we can use and reuse, like this:


```java
    package com.mojontwins.modloader;

    import net.minecraft.game.entity.Entity;

    public class RenderThrowableBottle extends ModRenderThrowableSimple {
        public RenderThrowableBottle() {
        }
        
        /*
         * `ModRenderThrowableSimple` calls this method to get the icon index to draw.
         */
        public int getItemIconIndex(Entity entity) {
            return ((EntityThrowableBottle) entity).itemBottle.getIconIndex();
        }
    }
```

So I guess this *should* suffice (in our mod class):

```java
    // Associate a renderer to our EntityThrowableBottles:

    ModLoader.addEntityRenderer(EntityThrowableBottle.class, new RenderThrowableBottle());
```

Now on to business - actually throwing the bottle when right clicking - creating an entity and emptying the `ItemStack`.

Am I being too smart?

```java
    public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer entityPlayer) {
        // First we detect if we hit something
        MovingObjectPosition movingobjectposition = getMovingObjectPositionFromPlayer(world, entityPlayer, true);

        if (movingobjectposition != null && movingobjectposition.typeOfHit == 0) {
            int x = movingobjectposition.blockX;
            int y = movingobjectposition.blockY;
            int z = movingobjectposition.blockZ;

            int blockID = world.getBlockId(x, y, z);

            if (this.contents == 0) {
                if (blockID == Block.waterStill.blockID) {
                    world.playSoundAtPlayer((float)x + 0.5F, (float)y + 0.5F, (float)z + 0.5F, "random.splash", world.random.nextFloat() * 0.25F + 0.75F,  world.random.nextFloat() + 0.5F);
                    
                    // Substitute the hit block with air
                    world.setBlockWithNotify(x, y, z, 0);
                    
                    // Replace this item with a filled bottle
                    return new ItemStack (mod_PoisonLand.itemBottleWater);
                } 

                if (blockID == mod_PoisonLand.blockAcidStill.blockID ) {
                    world.playSoundAtPlayer((float)x + 0.5F, (float)y + 0.5F, (float)z + 0.5F, "random.splash", world.random.nextFloat() * 0.25F + 0.75F,  world.random.nextFloat() + 0.5F);
                    
                    // Substitute the hit block with air
                    world.setBlockWithNotify(x, y, z, 0);
                    
                    // Replace this item with a filled bottle
                    return new ItemStack (mod_PoisonLand.itemBottleAcid);                   
                }
            }
        }
        
        // If we reach this point: throw the bottle!

        world.playSoundAtEntity(entityPlayer, "random.bow", 0.5F, 0.4F / (Item.rand.nextFloat() * 0.4F + 0.8F));
        world.spawnEntityInWorld(new EntityThrowableBottle(world, entityPlayer, 0.5F + Item.rand.nextFloat() * 0.5F, itemStack.getItem()));
        itemStack.stackSize--;
    
        return itemStack;
    }     
```

If this works, next thing would be actually doing something to entities. If the bottle is empty or full of water we could strike for a couple of hearts (damage 4). If it's full of acid it could be damage 10 and if it's full of posion it's damage 100, in advance for the almighty diamond skeletons I'm planning to add and which would be kinda the bosses for this world theme (still thinking of them). One could play to get, I dunno, 3 special items dropped by such mobs, or something like that.

```java
    package com.mojontwins.modloader;

    import net.minecraft.client.physics.MovingObjectPosition;
    import net.minecraft.game.entity.Entity;
    import net.minecraft.game.entity.EntityLiving;
    import net.minecraft.game.item.Item;
    import net.minecraft.game.level.World;

    public class EntityThrowableBottle extends ModEntitlyThrowableSimple {
        public Item itemBottle;

        public EntityThrowableBottle(World world, EntityLiving owner, float speedMultiplier, Item itemBottle) {
            super(world, owner, speedMultiplier);
            this.itemBottle = itemBottle;
        }

        protected String getEntityString() {
            return "throwable_bottle";
        }
        
        public void onImpact(MovingObjectPosition movingObjectPosition) {
            // Glass
            this.worldObj.playSoundAtPlayer(this.posX, this.posY, this.posZ, "random.glass", 1.0F, 1.0F);
            
            int itemID = itemBottle.shiftedIndex;
            int damage = 0;
            
            if (itemID == mod_PoisonLand.itemBottleEmpty.shiftedIndex) {
                damage = 2;
            } else if (itemID == mod_PoisonLand.itemBottleWater.shiftedIndex) {
                damage = 4;
            } else if (itemID == mod_PoisonLand.itemBottleAcid.shiftedIndex) {
                damage = 10;
            } else if (itemID == mod_PoisonLand.itemBottlePoison.shiftedIndex) {
                damage = 100;
            }
            
            // Splash
            if (itemID != mod_PoisonLand.itemBottleEmpty.shiftedIndex) {
                int splashes = 4 + this.rand.nextInt(4);
                for (int i = 0; i < splashes; i++) {
                    this.worldObj.spawnParticle("splash", this.posX + rand.nextFloat()-0.5F, this.posY + rand.nextFloat()-0.5F, this.posZ + rand.nextFloat()-0.5F, 0.0F, 0.0F, 0.0F);
                }
            }
            
            // Hit entity
            Entity entity = movingObjectPosition.entityHit;
            if (entity != null) entity.attackEntityFrom(null, damage);
        }
    }
```

Oh - I got it. I'll add a special brand of skeletons which are just skeletons but drop skeleton heads (with a chance).  Then you can summon diamond skeletons by placing two diamond blocks on top of each other and a skeleton head on top. I'll have to borrow a block renederer for skeleton heads (which will be blocks). It's a 1.4.2 feature, will be too complicated to port. I could just make my own renderer. Size would be 0.5Fx0.5Fx0.5F, bottom-centered (at 0.25F, 0, 0.25F). Place using the angle trick, store in metadata. And that's it. Comes next, for example.

I keep delaying the GUI riden tile entity to make poison.

After some fiddling, I managed to make the custom block renderer by hand, complete with rotations. I am also in the process of making a converter which takes the JSONs https://www.blockbench.net/ writes and turns them into java classes I can use with Indev ModLoader. As it is a bit buggy (have to test it and make sure it rotates all the textures properly!) I'm delaying its documentation.

Ok, so now that I have a block for the distiller, it is time to create the associated GUI and tile entity.

### A skeleton replica

Nope, I can't design the GUI rn, so let's move on to another thing I have to do: the Skeleton replica which drops its head by chance 1:8 (for example). I'll use a slightly modified skin for it, just to make it a bit more interesting. As we did with husks, just inherit from the base class and refine:

```java
    package com.mojontwins.modloader;

    import net.minecraft.game.entity.monster.EntitySkeleton;
    import net.minecraft.game.item.Item;
    import net.minecraft.game.level.World;

    public class EntityPoisonSkeleton extends EntitySkeleton {

        public EntityPoisonSkeleton(World var1) {
            super(var1);
            this.texture = "/mob/poison_skeleton.png";
        }

        protected String getEntityString() {
            return "PoisonSkeleton";
        }
        
        protected int scoreValue() {
            return (rand.nextInt(8) == 0) ? mod_PoisonLand.blockSkullHeadRenderID : Item.arrow.shiftedIndex;
        }    
    }
```

Register in our mod class:

```java
    entityPoisonSkeletonMobID = ModLoader.getNewMobID();
    ModLoader.addEntityRenderer(EntityPoisonSkeleton.class, new RenderLiving(new ModelSkeleton (), 0.5F));
```

And replace skeletons with this:

```java
    public void populateMobsHashMap (int levelType) {   
        // Add poison skeletons instead of skeletons
        if (levelType == poisonLandThemeID) {
            System.out.println ("Replacing skeletons with poison skeletons!");
            ModLoader.removeMonsterEntity(EntitySkeleton.class);
            ModLoader.registerMonsterEntity (entityPoisonSkeletonMobID, EntityPoisonSkeleton.class);
        }
    }
```

### Invocations

Now on to the effect of placing the skull on top of two diamond blocks.

BEWARE! I just made `Minecraft.effectsRenderer` static! Test to check if I have broken something! Nay, it works.

### The cauldron

I've been thinking and I don't really need a tile entity to make poison, just a changing block will suffice. I'll add a cauldron object (using a custom block model) which can be filled with nothing, water, acid or poison. Interacting with it with a bottle will do all the fiddling, replacing the bottle item and the block placed in the world by the correct instance.

Right now, `BlockCauldron` is just a normal block with supports different angles and a custom model. It has attributes for 4 different texture indexes used to render the cauldron, and uses `itemIndexInTexture` for the contents, so I just have to check its value to know which kind of cauldron we are interacting with.

```java
    // Breed your poison with a custom tile renderer
    
    int cauldronTXZ = ModLoader.addOverride(EnumTextureAtlases.TERRAIN, "textures/block_cauldron_xz.png");
    int cauldronTNS = ModLoader.addOverride(EnumTextureAtlases.TERRAIN, "textures/block_cauldron_ns.png");
    int cauldronTW = ModLoader.addOverride(EnumTextureAtlases.TERRAIN, "textures/block_cauldron_w.png");
    int cauldronTE = ModLoader.addOverride(EnumTextureAtlases.TERRAIN, "textures/block_cauldron_e.png");
    
    blockCauldronEmpty = new BlockCauldron(ModLoader.getBlockId()).setBlockHardness(1.0F).setName("block.cauldron.empty");
    ((BlockCauldron) blockCauldronEmpty).tXZ = cauldronTXZ;
    ((BlockCauldron) blockCauldronEmpty).tNS = cauldronTNS;
    ((BlockCauldron) blockCauldronEmpty).tW = cauldronTW;
    ((BlockCauldron) blockCauldronEmpty).tE = cauldronTE;
    blockCauldronEmpty.blockIndexInTexture = ModLoader.addOverride(EnumTextureAtlases.TERRAIN, "textures/block_empty.png");
    ModLoader.registerBlock(blockCauldronEmpty);

    blockCauldronWater = new BlockCauldron(ModLoader.getBlockId()).setBlockHardness(1.0F).setName("block.cauldron.water");
    ((BlockCauldron) blockCauldronWater).tXZ = cauldronTXZ;
    ((BlockCauldron) blockCauldronWater).tNS = cauldronTNS;
    ((BlockCauldron) blockCauldronWater).tW = cauldronTW;
    ((BlockCauldron) blockCauldronWater).tE = cauldronTE;
    blockCauldronWater.blockIndexInTexture = Block.waterMoving.blockIndexInTexture;
    ModLoader.registerBlock(blockCauldronWater);

    blockCauldronAcid = new BlockCauldron(ModLoader.getBlockId()).setBlockHardness(1.0F).setName("block.cauldron.acid");
    ((BlockCauldron) blockCauldronAcid).tXZ = cauldronTXZ;
    ((BlockCauldron) blockCauldronAcid).tNS = cauldronTNS;
    ((BlockCauldron) blockCauldronAcid).tW = cauldronTW;
    ((BlockCauldron) blockCauldronAcid).tE = cauldronTE;
    blockCauldronAcid.blockIndexInTexture = blockAcidFlowing.blockIndexInTexture;
    ModLoader.registerBlock(blockCauldronAcid);

    blockCauldronPoison = new BlockCauldron(ModLoader.getBlockId()).setBlockHardness(1.0F).setName("block.cauldron.poison");
    ((BlockCauldron) blockCauldronPoison).tXZ = cauldronTXZ;
    ((BlockCauldron) blockCauldronPoison).tNS = cauldronTNS;
    ((BlockCauldron) blockCauldronPoison).tW = cauldronTW;
    ((BlockCauldron) blockCauldronPoison).tE = cauldronTE;
    blockCauldronPoison.blockIndexInTexture = ModLoader.addOverride(EnumTextureAtlases.TERRAIN, "textures/block_poison.png");
    ModLoader.registerBlock(blockCauldronPoison);

    // We'll use the same custom block renderer for all the cauldron instances
    blockCauldronRenderID = ModLoader.getUniqueBlockModelID(this, true);
```

The cauldron's custom renderer was generated with the `Json2RenderBlocks.jar` converter and it was set up in BlockBench so the last texture used is that of the contents. As mentioned, such contents is represented by `blockIndexInTexture`, so here's how we integrate the custom renderer for cauldrons:

```java
    public void renderInvBlock(RenderBlocks renderblocks, Block block, int renderType) {
        Tessellator tessellator = Tessellator.instance;

        [...]
        
        if (renderType == blockCauldronRenderID) {
            tessellator.startDrawingQuads();
            Tessellator.setNormal(0.0F, -1.0F, 0.0F);
            RenderCauldron.renderBlock(0, -0.5F, -0.5F, -0.5F, ((BlockCauldron) block).tXZ, ((BlockCauldron) block).tNS, ((BlockCauldron) block).tW, ((BlockCauldron) block).tE, block.blockIndexInTexture);
            tessellator.draw();
        }
    }

    public boolean renderWorldBlock(RenderBlocks renderblocks, World world, int x, int y, int z, Block block, int renderType) {
        Tessellator tessellator = Tessellator.instance;
        
        [...]

        if (renderType == blockCauldronRenderID) {
            float b = block.getBlockBrightness(world, x, y, z);
            tessellator.setColorOpaque_F(b, b, b);
            
            return RenderCauldron.renderBlock(world.getBlockMetadata(x, y, z), x, y, z, ((BlockCauldron) block).tXZ, ((BlockCauldron) block).tNS, ((BlockCauldron) block).tW, ((BlockCauldron) block).tE, block.blockIndexInTexture);    
        }
        
        return false;
    }   
```

To create the cauldron new logic, we'll expand on what we have in the `onItemRightClick` method of `ItemBottle`, so if the block hit with the bottle is of class `BlockCauldron`, special stuff is done:

* If the cauldron is empty, and the bottle is not empty, it will get filled with the content of the bottle, and the bottle will be replaced with the empty bottle.
* If the cauldron is not empty, and the bottle is empty, it will be emptied and the bottle filled with its contents.

We'll leave further interaction (i.e. throwing in mushrooms) for later. This is the way to check if we hit a cauldron, whatever type of cauldron it is:

```java       
    // Let's check if we hit a cauldron
    if (blockID > 0 && Block.blocksList[blockID] != null && Block.blocksList[blockID] instanceof BlockCauldron) {
        
        // Empty cauldron, not empty bottle:
        if (blockID == mod_PoisonLand.blockCauldronEmpty.blockID && this.contents != 0) {
            // Fill cauldron
            int newBlockID = 0;
            
            if (this.contents == Block.waterStill.blockID) {
                newBlockID = mod_PoisonLand.blockCauldronWater.blockID;
            } else if (this.contents == mod_PoisonLand.blockPoison.blockID) {
                newBlockID = mod_PoisonLand.blockCauldronPoison.blockID;
            } else if (this.contents == mod_PoisonLand.blockAcidFlowing.blockID) {
                newBlockID = mod_PoisonLand.blockCauldronAcid.blockID;
            }
                                
            world.setBlockAndMetadataWithNotify(x, y, z, newBlockID, meta);
            
            // Empty bottle
            return new ItemStack (mod_PoisonLand.itemBottleEmpty);
        }
        
        // Empty bottle, not empty cauldron:
        if (blockID != mod_PoisonLand.blockCauldronEmpty.blockID && this.contents == 0) {
            // Empty cauldron
            int cauldronContents = Block.blocksList[blockID].blockIndexInTexture;
            world.setBlockAndMetadataWithNotify(x, y, z, mod_PoisonLand.blockCauldronEmpty.blockID, meta);
            
            // Fill bottle
            if (cauldronContents == Block.waterMoving.blockIndexInTexture) {
                itemStack = new ItemStack (mod_PoisonLand.itemBottleWater);
            } else if (cauldronContents == mod_PoisonLand.blockAcidFlowing.blockIndexInTexture) {
                itemStack = new ItemStack (mod_PoisonLand.itemBottleAcid);
            } else if (cauldronContents == mod_PoisonLand.blockPoison.blockIndexInTexture) {
                itemStack = new ItemStack (mod_PoisonLand.itemBottlePoison);
            }
            
            return itemStack;
        }
        
        // Case else:
        return itemStack;
    }
```

Nothing really special, just basic itemStack / world block fiddling. Note how metadata is preserved for cauldrons as it represents the cauldron angle.

Next would be adding mushrooms to the mix. Acid + glowing mushrooms should become poison. Water + brown mushrooms should become edible soup. That implies adding edible bottles of soup, and adding water to the world somehow. 

Adding water should be as simple as placing water sources in the world. The problem is HOW. During the watering stage, the level generator fill all holes below water level with the default fluid, which is poison. I guess it's time to take a glance at the generator again and think of a way to add extra fluids which are different to the default fluid.

First thing is understanding how the `floodFill` method in `LevelGenerator` works. 

```java
    private long floodFill(int var1, int var2, int var3, int var4, int var5) {
```

* `var1, var2, var3` is `x, y, z`.
* `var4` seems to be *block to substitute*. 
* `var5` seems to be *block to fill with*.

The returned value seems to be a count of blocks replaced, or something similar. 

So `(..., 0, blockID)` would be filling empty space with `blockID`. 

During the watering phase, `liquidThemeSpawner` is called. It iteratems the amount of blocks divided by 1000 times. Each times it selects a completely random coordinate. If that coordinate in the blocks array is empty, it launches a flood fill in that coordinate which substitutes empty blocks with ID 255 (non existing hopefully). If the count of blocks returned is LESS than 640, it launches a new flood fill to change 255 with the default fluid block ID, otherwise it launches the new flood fill to restore the empty block (0).

I think I could rewrite my theme hooks so there's a different handle for the surrounding sea and the inland ponds. Now I have a simple variable. Change this for two proper methods so I can use rand and stuff.

Or better still, do this:

```java
    public int getWateringBlockID (LevelGenerator levelGenerator, boolean inland) {}
```

You can check `inland` and do what you will.

So this allows me to have some water inland (with luck, will adjust later if too rare): 

```java
    public int getWateringBlockID (LevelGenerator levelGenerator, boolean inland) {
        return inland && levelGenerator.rand.nextBoolean() ? Block.waterStill.blockID : mod_PoisonLand.blockAcidStill.blockID;
    }
```

So the next thing is adding the actual item for soup, and a soup block, and the ability for the cauldron to contain soup. Then, think on a way to use mushroom blocks on the cauldron, and check whether brown / glowing mushroom blocks are properly differentiated.

Then on the brewin'!

To add the kind of interactivity I need to mushroom blocks I need to create a custom `ItemBlock` with the required method. The `ItemBlock` should store the mushroom type as the blocks do.

```java
    package com.mojontwins.modloader;

    import net.minecraft.client.physics.MovingObjectPosition;
    import net.minecraft.game.block.Block;
    import net.minecraft.game.entity.player.EntityPlayer;
    import net.minecraft.game.item.ItemBlock;
    import net.minecraft.game.item.ItemStack;
    import net.minecraft.game.level.World;

    public class ItemBigMushroom extends ItemBlock {
        public int mushroomType = 0;
        
        public ItemBigMushroom(int var1) {
            super(var1);
            Block block = Block.blocksList[blockID];
            if (block instanceof BlockBigMushroom) {
                mushroomType = ((BlockBigMushroom) block).mushroomType;
            }
        }

        public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer entityPlayer) {
            MovingObjectPosition movingobjectposition = getMovingObjectPositionFromPlayer(world, entityPlayer, true);

            if (movingobjectposition != null && movingobjectposition.typeOfHit == 0) {
                int x = movingobjectposition.blockX;
                int y = movingobjectposition.blockY;
                int z = movingobjectposition.blockZ;

                int blockID = world.getBlockId(x, y, z);
                int meta = world.getBlockMetadata(x, y, z);  
                
                // Let's check if we hit a cauldron
                if (blockID > 0 && Block.blocksList[blockID] != null && Block.blocksList[blockID] instanceof BlockCauldron) {
                    // TODO
                }
            }

            return itemStack;
        }   
    }
```

And change the way we defined Mushroom blocks in the mod class:

```java
    [...]
    ModLoader.registerBlock(blockBigMushroomGreen, ItemBigMushroom.class);

    [...]
    ModLoader.registerBlock(blockBigMushroomBrown, ItemBigMushroom.class);
```

We also create a block to represent soup and one for goo. When you use the glowing mushroom on acid there's a change of 75% to get goo instead of poison.

So those are the recipes:

* Water + brown mushroom = Soup.
* Water + green mushroom = Acid.
* Acid + brown mushroon = Goo.
* Acid + green mushroom = 50% chance goo / poison.
* Poison + mushroom = Goo.
* Soup + mushroom = Goo.
* Goo + mushroom = Goo.

An empty cauldron would not accept a mushroom block:

```java
    public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer entityPlayer) {
        MovingObjectPosition movingobjectposition = getMovingObjectPositionFromPlayer(world, entityPlayer, true);

        if (movingobjectposition != null && movingobjectposition.typeOfHit == 0) {
            int x = movingobjectposition.blockX;
            int y = movingobjectposition.blockY;
            int z = movingobjectposition.blockZ;

            int blockID = world.getBlockId(x, y, z);
            int meta = world.getBlockMetadata(x, y, z);  
            
            // Let's check if we hit a cauldron
            if (blockID > 0 && Block.blocksList[blockID] != null && Block.blocksList[blockID] instanceof BlockCauldron) {
                // Empty cauldron: do nothing
                if (blockID == mod_PoisonLand.blockCauldronEmpty.blockID) {
                    return itemStack;
                }
                
                int newBlockID = blockID; 
                
                // Cauldron is not empty.
                int cauldronContents = Block.blocksList[blockID].blockIndexInTexture;
                                
                if (cauldronContents == Block.waterMoving.blockIndexInTexture) {
                    // Water, + brown = soup; + green = acid
                    newBlockID = this.mushroomType == 0 ? mod_PoisonLand.blockCauldronSoup.blockID : mod_PoisonLand.blockCauldronAcid.blockID;
                } else if (cauldronContents == mod_PoisonLand.blockAcidFlowing.blockIndexInTexture) {
                    // Acid, + brown = goo; + green = 50 % chance goo / poison
                    newBlockID = this.mushroomType == 0 || rand.nextBoolean() ? mod_PoisonLand.blockCauldronGoo.blockID : mod_PoisonLand.blockCauldronPoison.blockID;
                } else {
                    // Poison + ? = goo, 
                    // Soup + ? = goo,
                    // Goo + ? = goo.
                    newBlockID = mod_PoisonLand.blockCauldronGoo.blockID;
                }
                
                // Replace cauldron
                world.setBlockAndMetadataWithNotify(x, y, z, newBlockID, meta);
                    
                // Decrease stack
                itemStack.stackSize --;
            }
        }

        return itemStack;
    }
```

### The Diamond Skeleton

I want diamond skeleton not to be just reskinned skeletons. I want them bigger, by a factor of 1.5F (so 3 blocks tall!). I'm going to use the same method that is used to make giant zombies bigger: extend the `EntityLiving` class and add a  `preRenderCallback` to scale the model:

I'm not scalling it very much so it's not weird that it has the same collision as normal skeletons.

```java
    package com.mojontwins.modloader;

    import org.lwjgl.opengl.GL11;

    import net.minecraft.client.model.ModelBase;
    import net.minecraft.client.renderer.entity.RenderLiving;
    import net.minecraft.game.entity.EntityLiving;

    public class RenderDiamondSkeleton extends RenderLiving {
        float scale = 1.5F;
        
        public RenderDiamondSkeleton(ModelBase var1, float var2) {
            super(var1, var2);
        }

        protected final void preRenderCallback(EntityLiving var1, float var2) {
            GL11.glScalef(this.scale, this.scale, this.scale);
        }
    }
```

And this in the mod class:

```java
    entityDiamondSkeletonMobID = ModLoader.getNewMobID();
    ModLoader.addEntityRenderer(EntityDiamondSkeleton.class, new RenderDiamondSkeleton(new ModelSkeleton (), 0.7F));
```

The only thing remaining is actually spawning the entity when a skull is put on two diamond blocks:

```java
    public void onBlockPlaced(World world, int x, int y, int z, int side) {
        if (this.blockID != mod_PoisonLand.blockSkullHead.blockID) return;
        
        // Detect if it's on top of two diamond blocks
        // And there's 1 block surrounding to each direction
        
        for (int i = x - 1; i <= x + 1; i ++) {
            for (int j = y - 2; j <= y; j ++) {
                for (int k = z - 1; k <= z + 1; k ++)  {
                    int blockID = world.getBlockId(i, j, k);
                    if (j < y && i == x && k == z) {
                        if (blockID != Block.blockDiamond.blockID) return;
                    } else if (i != x || k != z) {
                        if (blockID != 0) return;
                    }                   
                }
            }
        }
        
        // If we get to this point the condition is fulfilled.
        
        // Destroy the blocks
        for (int j = y -2; j <= y; j ++) {
            Minecraft.effectRenderer.addBlockDestroyEffects(x, j, z);
            world.setBlockWithNotify(x, y, z, 0);
        }
        
        // Add the entity
        Entity entityDiamondSkeleton = new EntityDiamondSkeleton (world);
        entityDiamondSkeleton.setPositionAndRotation (x, y, z, 0.0F, 0.0F);
        world.playSoundAtEntity(entityDiamondSkeleton, "random.explode", 0.5F, 1.0F);
        world.spawnEntityInWorld(entityDiamondSkeleton);
        
    }
```

Last thing to add is the prize item this Diamond Skull drops.

# Fuel

Few things are left to add (at least, from my list, and not counting the alternate world generator stuff I seem to keep in mind). One of them is the ability to add new fuel. It will be achieved by using a rather simple registry (item <-> time) and patching this method in `TileEntityFurnace`:

```java
    private static int getItemBurnTime(ItemStack var0) {
        if (var0 == null) {
            return 0;
        } else {
            int var1;
            if ((var1 = var0.getItem().shiftedIndex) < 256 && Block.blocksList[var1].material == Material.wood) {
                return 300;
            } else if (var1 == Item.stick.shiftedIndex) {
                return 100;
            } else {
                return var1 == Item.coal.shiftedIndex ? 1600 : 0;
            }
        }
    }
```

For example:

```java
    private static int getItemBurnTime(ItemStack var0) {
        if (var0 == null) {
            return 0;
        } else {
            int var1;
            if ((var1 = var0.getItem().shiftedIndex) < 256 && Block.blocksList[var1].material == Material.wood) {
                return 300;
            } else if (var1 == Item.stick.shiftedIndex) {
                return 100;
            } else {
                return var1 == Item.coal.shiftedIndex ? 1600 : ModLoader.getItemBurnTime(var1);
            }
        }
    }
```

And having modloader return 0 by default or the stored value for the passed `shiftedIndex` in `var1` if it exists, than have a `registerFuel` or something to populate the registry:

```java
    /*
     * Add fuel
     */
    public static void addFuel(int shiftedIndex, int time) {
        burnTimes.put(shiftedIndex, time);
    }
    
    /*
     * Fuel hook
     */
    public static int getItemBurnTime(int shiftedIndex) {
        Integer r = burnTimes.get(shiftedIndex);
        if (r != null) return r.intValue(); else return 0;
    }
```

# Custom raising / soiling

I'll proof-of-concept this by creating a cave-type level generator. Main problem is lighting which will be nil. Dunny if there's such a thing as "min light level", if not I'd have to add it in, so nether-like worlds are possible.

The main goal is replacing raising/soiling completely at will in your theme, or be able to use the default vanilla code. First thing is thinking the cleanest way to do so.

I also have to provide a way to let the developer choose the spawn point, overriding the (fixed) vanilla code.


