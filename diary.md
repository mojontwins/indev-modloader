# Diary

# Roadmap

This is the roadmap which will be constantly changing.

* Create a basic ModBase class and make the system to automaticly run mod_Name classes.
* [x] Basic ModBase class and mod_XXX importing.
* [x] Use your mod class to add blocks.
* [X] Use your mod class to add items.
* [x] Use your mod class to add recipes of any kind.
* [ ] Use your mod to add armor
* [ ] Render blocks using custom renderers.
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

Hmmm - this seems way more hackeable than what I know: in 1.2.5 there's a Enum to contains all the values which can't be easily modified programatically. All methods are marked `final` which just sucks and *I'm removing that so we can customize everything!*

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

In minecraft 1.2.5 this is implemented with potions. Items have a `setPotionEffect` method to set a `potionEffect` attribute. Items here have an `onFoodEaten` method which call `EntityPlayer`'s `addPotionEffect` if any potion effect. This adds the potion to `activePotionsMap`.

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

TODO - adding some visual indicator for status effects!

### More status effects stuff - attack strength

There are two potions in 1.2.5, *weakness* and *damageBoost* which modify the strength when the player hits mobs. We can provide support for such kind of modifications with yet another hook, this time at `Minecraft.clickMouse` right after the attack strength has been calculated in `var19`. There's this:

```java
    int var19 = (var9 = (var11 = var10000.inventory).getStackInSlot(var11.currentItem)) != null ? Item.itemsList[var9.itemID].getDamageVsEntity() : 1;
```

We can follow this with:

```java
    // var19 : hit strength.
    // var14 : Entity being hit
    var19 = ModLoader.HookAttackStrengthModifier (this.thePlayer, var14, var19);
```

And add this new hook to `ModLoader`

```java
    public static int HookAttackStrengthModifier (EntityLiving entityLiving, Entity entityHit, int strength) {
        int res = strength;
        for (Iterator<BaseMod> iterator = modList.iterator(); iterator.hasNext();) {
            res = ((BaseMod)iterator.next()).HookAttackStrengthModifier(entityLiving, entityHit, res);
        }
        return res;
    }
```

then `BaseMod`

```java
    public int HookAttackStrengthModifier (EntityLiving entityLiving, Entity entityHit, int strength) {
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

Using the same kind of hook on var4 we can achieve the same effect as using 1.2.5 potions *digSpeed* and *digSlowdown*. Just adding this:

```java
    // var1 is playerEntity
    // var4 is original strength
    var4 = ModLoader.HookBlockHitStrengthModifier (var1, this, var4);
```

And the new hook in `Modloader`

```java
    public static float HookBlockHitStrengthModifier (EntityLiving entityLiving, Block block, float strength) {
        float res = strength;
        for (Iterator<BaseMod> iterator = modList.iterator(); iterator.hasNext();) {
            res = ((BaseMod)iterator.next()).HookBlockHitStrengthModifier(entityLiving, block, res);
        }
        return res;     
    }
```

And `Basemod`

```java
    public float HookBlockHitStrengthModifier (EntityLiving entityLiving, Block block, float strength) {
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

Some may not like this, that's why particle spawning (and color) will be controlled by your `Status` implementation. Anyways, I've added this new kind of particle to the base engine (adapted from 1.2.5):

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
