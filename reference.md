# General stuff

To create a mod using ModLoader, you have to create a new class called `mod_YourNameHere` in the package `com.mojontwins.modloader` which extends `BaseMod`, then place all your initialization stuff in a `load` method. To load resources from within the .jar or .zip files, take in account that the base directory if that of `ModLoader`, that is, `com/mojontwins/modloader` so you may use paths relative to that, such as those in the examples contained in this document.

```java
    package com.mojontwins.modloader;

    public class mod_Example extends BaseMod {
        public void load () throws Exception {
            // Your stuff here
        }
    }
```

## New blocks

There's several ways to add new blocks, but this one is the standard for ModLoader.

### Simple blocks

1. Create a new class for your block which extends `ModBlock`:

```java
    package com.mojontwins.modloader;

    import net.minecraft.game.block.Material;

    public class BlockStoneBricks extends ModBlock {
        protected BlockStoneBricks(int id, Material material) {
            super(id, material);
        }
    }
```

2. Create a `static` attribute in your mod class:

```java
    public static ModBlock blockStoneBricks;
```

3. Create the object, assign a texture, and register your new block in the `load` method of your mod class:

```java
    blockStoneBricks = new BlockStoneBricks(ModLoader.getBlockId (), Material.rock).setBlockHardness(1.5F).setBlockResistance(1.5F).setName("block.stone_bricks");
    blockStoneBricks.blockIndexInTexture = ModLoader.addOverride(EnumTextureAtlases.TERRAIN, "textures/block_stone_bricks.png");
    ModLoader.registerBlock(blockStoneBricks);  
```

Note how your new block ID is generated automaticly by the `ModLoader.getBlockID ()` sequencer, but you can use a fixed blockID if you please. Also, `setName` is used to give the block a name for the registry. The second line uses `ModLoader` to load a new texture and assign it to your block. The third line registers the block and creates the associated `ItemBlock` the engine needs to work.

### Block properties

You define basic block properties by calling these methods:

```java
    public ModBlock setBlockLightOpacity(int var1); 
    public ModBlock setBlockLightValue(float var1);
    public ModBlock setBlockResistance(float var1);
    public ModBlock setBlockHardness(float var1);
    public void setBlockTickOnLoad(boolean var1);
    public void setBounds(float var1, float var2, float var3, float var4, float var5, float var6);
```

You can also access the `Block` attributes directly in the constructor of the block class:

```java
    public int blockIndexInTexture;
    public final int blockID;
    public float minX;
    public float minY;
    public float minZ;
    public float maxX;
    public float maxY;
    public float maxZ;
    public StepSound stepSound;
    public float blockParticleGravity;
    public final Material material;
```

For hardness and resistance you must call `setResistance (float resistance)` and `setHardness (float resistance)`.

### Blocks with different textures per face

To do this you first need to call `ModLoader.addOverride` for each texture you need, then store the returned ids in your class, somehow. Then you must override one of these methods in your class depending on your needs:

```java
    public int getBlockTextureFromSideAndMetadata(int var1, int var2) {
        return this.getBlockTextureFromSide(var1);
    }

    public int getBlockTextureFromSide(int var1) {
        return this.blockIndexInTexture;
    }
```

`var1` contains the ID of the side being rendererd:

* 0 - bottom
* 1 - top
* 2,3,4,5 - sides, with 2-3 and 4-5 opposing.

### Blocks returning different blocks/items when harvested

By default, your blocks will return the same block when harvested. This is because the main `Block` class implements these two methods:

```java
    public int idDropped(int var1, Random var2) {
        return this.blockID;
    }

    public int quantityDropped(Random var1) {
        return 1;
    }
```

To make your block act differently, just override these methods in your block class. `idDropped` returns the id of the block of item which will drop upon harvesting your block, and `quantityDropped` returns the quantity. You even get a `Random` object if you need to ramdomize it.

### Animated textures

To use an animated texture, you must call `addAnimation` rather than `addOverride` when assigning to the block's `blockIndexInTexture` or any custom texture index attribute. Parameters are the same plus an extra `ticksPerFrame` value at the end, which defines how many ticks each animation frame stays. The `textureAtlasURI` must be a png file with all the animation frames stitched vertically. 

```java
    public static int addAnimation (EnumTextureAtlases textureAtlas, String textureAtlasURI, int ticksPerFrame);
```

### Blocks with a custom ItemBlock class

By default, `ModLoader` registers blocks by creating an `ItemBlock` instance, but you can use your own `Item` class instead. Just pass its `.class` attribute when registering your block:

```java
    ModLoader.registerBlock(blockLilypad, ItemLilypad.class);
```

For the complete lilypad example check *Advanced blocks*

### Advanced blocks

We'll be creating an object to represent lilypads, complete with custom block rendering, custom associated item class (so we can place lilypads in water), custom boundaries (so we can walk on top of lilypads) and advanced block handling (as lilypads can only grow on water).

First of all, the `BlockLilypad` class:

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

This class contains some stuff from `BlockPlant` which is useful:

* When the user attempts to place a `BlockLilypad` in the world, `canPlaceBlockAt` is played to check if it's in the right place.
* `canBlockStay` is an auxiliary function which just checks if the lilypad can stay - this is, it's still being placed over still water.
* `checkFlowerChange` is another auxiliary function. It calls `canBlockStay`. It that returns false, it removes the lilypad from the world and drops it as an item. This would happen for instance if the world changed and still water is no longer below the lilypad.
* `updateTick` will be called from time to time, as `this.setTickOnLoad(true);` is in the constructor. This will, in turn, call `checkFlowerChange`. 
* `getCollisionBoundingBoxFromPool` is needed 'cause we've defined a customised bounding box for this block in the constructor with `this.setBlockBounds`. This call creates a small, thin bounding box in the lower part of the box containing the block. The coordinates range from 0.0 (left, front, bottom) to 1.0 (right, back, top).
* `isOpaqueCube` returns false as the bounding box and block model don't take the whole box.
* `renderAsNormalBlock` also returns false as we'll be using a custom renderer.
* Finally, `getRenderType` returns the value of an attribute in our mod class we haven't defined yet.

Normally you can't place blocks over water 'cause water is not detected by the ray caster which finds which block is under the mouse cursor. So we need to code a special case for when the player right-clicks with a lilypad selected. This means that we have to use a custom `ItemBlock` object to be associated with our `BlockLilypad` which implements the special case:

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

This overrides completely how Minecraft behaves normally. It will do its own thing when you right-click with a Lilypad in the player's hand. It will ask the engine what's under the cursor. If it happens to be still water and there's air on top of it, it will place a Lillypad in the world.

In our mod class, we add the block as follows - note the call to `registerBlock`:

```java
    public static ModBlock blockLilypad;
    public static int blockLilypadRenderID;

    [...]

    blockLilypad = new BlockLilypad (ModLoader.getBlockId()).setName("block.lilypad");
    blockLilypad.blockIndexInTexture = ModLoader.addOverride (EnumTextureAtlases.TERRAIN, "textures/block_lilypad.png");
    ModLoader.registerBlock(blockLilypad, ItemLilypad.class);
    blockLilypadRenderID = ModLoader.getUniqueBlockModelID(this, false);     
```

Note also how we load and assign a texture to the block *before* we register it - this is because registering the block creates the related item, and its constructor just copies the block's `blockIndexInTexture` to the new item's `iconIndex`, so it must have a proper value beforehand.

The last line asks `ModLoader` for a new, unique render ID for the custom renderer we are just going to add. Note how `BlockLilypad.getRenderType` returns exactly this value: `mod_Example.blockLilypadRenderID`.

`getUniqueBlockModelID` takes two parameter: the `BaseMod` instance (i.e. `this`), and a boolean telling the engine that the item should be rendered in full-3D when it's being drawn into the inventory or the player's hand. By default, if you pass a `false`, the engine will draw the texture in 2D for the inventory and an extruded "2D in 3D" version of it when it's on the player's hand. If you pass `true`, you'll have to write a proper renderer for it in `renderInvBlock`.  The default behaviour will suffice for lilypads.

So what's left is overriding `BaseMod.renderWorldBlock` and write a proper renderer. This one has been adapted from r1.2.5:

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

As mentioned, `renderInvBlock` will not be used unless you pass `true` to `getUniqueBlockModelID`. In such case, this general implementation works most of the time, as the methods which draw the block in the inventory / hand set the origin of coordinates in the center of where it should appear, and the *measures* are 1.0Fx1.0Fx1.0F. (not needed for lillypads, but useful for stuff like fences). The `this.renderBlah` method would be the very same method called from `renderWorldBlock`:

```java
    public void renderInvBlock(RenderBlocks renderblocks, Block block, int renderType) {
        Tessellator tessellator = Tessellator.instance;

        if (renderType == blockBlahRenderID) {
            tessellator.startDrawingQuads();
            Tessellator.setNormal(0.0F, -1.0F, 0.0F);
            this.renderBlockBlah(block, -0.5F, -0.5F, -0.5F);
            tessellator.draw();
        }
    }
```

## New Items

Items are way simpler to put into your game. Just create an object of the class `ModItem`, or a class extending `ModItem`, and give it some properties & attributes. As with blocks, you can use the built-in sequencer for Item IDs or you can use your own magic numbers. Also, don't forget to give your stuff names so it gets properly registered. 

Tools are based on a material and a type. Upon those parameters, they can be used more or less effectively in the world and entities.

```java
    public static ModItem itemPebble;

    [...]

    itemPebble = new ModItem(ModLoader.getItemId()).setMaxStackSize(64).setName("item.pebble");
    itemPebble.setIconIndex(ModLoader.addOverride(EnumTextureAtlases.ITEMS, "textures/item_pebble.png"));
```

### Tools & weapons

Tools and weapons are special items. You can extend the existing ItemPickaxe, ItemSword, ItemAxe, ItemHoe or ItemSpade, or even create your own. 

The classes `ItemAxe`, `ItemPickaxe` and `ItemSpade` all extend the base `ItemTool` class - *note how `ItemSword` and `ItemHoe` don't*. Here's the constructor:

```java
    public ItemTool(int var1, int var2, int var3, Block[] var4)
```

Where
    * `var1` is the ID (get's passed on to `super` which is `Item`).
    * `var2` seems to be a base damage value when hitting entities. Get's added to `var3` to make `damageVsEntity`.
    * `var3` seems to be the hardness, and is usually 0 for wood, 1 for rock, 2 for steel and 3 for diamond. It's used to calculate `maxDamage` or how many times you can use the tool ?
    * `var4` is an array of blocks which gets copied to `blocksEffectiveAgainst`.

`blocksEffectiveAgainst` is a list of blocks the tool is good at breaking. If the block being hit is in the list, the strength applied is `(var3 + 1) * 2`, this is, 2.0F for wood, 4.0F for stone, 6.0F for steel and 8.0F for diamond. It it's not, the strength is 1.0F.

#### `ItemPickAxe`

The constructor has three parameters equivalent to the above `var1`, `var3` and `var4`. `var2` is set to `2`, so `damageVsEntity` happens to be `2 + var3`. The class has its own attribute `harvestLevel` which is also set to the value of `var3` (the 2nd parameter in the `ItemPickAxe` constructor) which is later used to calculate if the tool can harvest certain ores.

Sadly, the method `canHarvestBlock` is marked `final` which is just plain shyte. If I want to make my own tools, it would be great to be able to redefine this method. *So I'm removing the `final` modifier here as well*. Sorry but not sorry.

Finally, `blocksEffectiveAgainst` is set to everything rocky or stoney in the game.

Now we can extend `ItemPickAxe` to create our own pickaxes.

#### `ItemAxe`

This one is much simpler. It sets `ItemTool`'s constructor `var2` to 3 so `damageVsEntity` is `3 + var3` - Axes are stronger than Pickaxes against mobs. Appart from that, `blocksEffectiveAgainst` contains everything that's made of wood.

#### `ItemSpade`

Same as `ItemAxe`, but with `var2` set to 1 and affective agains grass, sand, dirt and gravel.

#### `ItemSword`

As mentioned, is not considered a tool, at least in the class hyerarchy. And the reason why is "because", as it could have been implemented as a `ItemTool`. Maybe there's a obscure reason I can't understand. Maybe it's because `ItemTool` methods are `final`.  `ItemSword`'s are also `final`. *I'm removing all `final`s!*.

Swords just take two parameters: the ID and a `var2` parameter which is used for `maxDamage` (`32 << var2`) and `weaponDamage` (4 + (var2 << 1)). `weaponDamage` is used as a returning value for `getDamageVsEntity`, so the sword causes a damage of 6, 8, 10 or 12 depending on its material.

#### `ItemHoe`

Hoes have code to plow land - that is, they override `onItemUse`.

#### Creating new tools 

Let's try and create a couple of steel tools: a steel pickaxe and a steel sword. We'll be using new classes which will extend base classes. We'll set all the custom values there.

Tool duration and tool strength are very crudely configured from the original constructors, so we will be overriding the values explictly. Let's begin with our steel pickaxe. I want the new steel pickaxe to be as strong as the iron pickaxe but 1.5 times as durable, and let's say 1.5 times faster too. Remember that the `ItemPickaxe` constructor can be expressed as:

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
    
    itemSteelPickaxe = new ItemSteelPickaxe(ModLoader.getItemId()).setName("item.steel_pickaxe");
    itemSteelPickaxe.setIconIndex(ModLoader.addOverride(EnumTextureAtlases.ITEMS, "textures/item_steel_pickaxe.png"));
```

All that's left is being able to craft these new tools. Check *Smelting and Crafting* below.

### Armor

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

You should know that: 

* The amount of damage an armour piece takes is fixed and doesn't depend on the material, only on the type, and is defined by the array `damageReduceAmountArray`.
* The material only defines how long it takes the armor piece to break.
* Gold and chain armor pieces are the same.

In order to add new kinds of armor, with completely customizable stats, and custom graphics, would need extending `ItemArmor` and assigning custom values to `damageReduceAmount` and `maxDamage`, plus adding new items to the `armorFilenamePrefix` array we can index with new `renderType` values. ModLoader provides a method to add items to this array and retrieve the new item index so we can store it in our mod class.

To make your life easier we have added the `ModItemArmor` class:

```java
    myArmor = new ModItemArmor (itemID, damageReduceAmount, maxDamage, type);
```

Where `type` would be your new material ID. To get a new material ID you have to call `ModLoader.AddArmor`: 

```java
    public static int addArmor(String s) throws Exception;
```

Where `s` is a filename prefix. Minecraft will fetch the armor textures from two png files in the `/armor` directory: `<material>_1.png` and `<material>_2.png`. You can use the existing armor textures as templates when drawing your own.

As an example, we are going to add a full Steel Armor which is 1.5 the stats of the Iron armor. If you have followed me until this point you'll be able to work out the numbers. We've also added `steel_1.png` and `steel_2.png` with the new textures to an `/armor` folder in our project, and the new item textures to our textures folder.

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

### Food

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

So getting new food to Indev is pretty straightforward. If you can do with any of these implementations, just make a new food item using one of those classes. If you need further customization, extend from `ItemFood` and override `onItemRightClick`, modifying the `ItemStack` as needed. Note how you get the itemstack, the world, and the player entity. 

You can use food to give the player status effects.

# Smelting and crafting

## Crafting recipes

To add crafting recipes you use the same method as if you were editing base classes, but calling `ModLoader.addRecipe` instead. `addRecipe` takes an `ItemStack` representing the results and an object representing the actual recipe, like this:

```java
    ModLoader.addRecipe(new ItemStack(blockStoneBricks, 4), new Object [] {
        "XX", "XX",
        'X', Block.stone
    });
```

The recipe object starts wit one to three strings. All strings should be the same length. You can pad with spaces if needed. Different arbitrary characters in the strings represent items or blocks. After the strings, there should be a list of pairs (char, block/item) which give actual meaning to the arbitrary characters in the strings. For example:

```java
    ModLoader.addRecipe(new ItemStack(Block.cobblestone, 1), new Object [] {
        "XXX", "XXX", "XXX",
        'X', itemPebble
    });

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

As you see, you can use your own blocks/items.

## Smelting recipes

Smelting recipes are even more simple:

```java
    ModLoader.addSmelting(Block.cobblestone.blockID, Block.stone.blockID);
```

Where the first parameter is the input, and the second parameter is the output.

### Custom fuel

TODO

# Playing around

## Substituting an existing standard item AKA silk touch golden pickaxe

Just for fun, Let's subtitute the useless golden pickaxe for a silk touch pickaxe which just breaks the block but returns the original block untouched.

This is how Indev works:

* When you use your tool on a block and you break it, `PlayerControllerSP.sendBlockRemoved (int x, int y, int z)` is called.
* There, the block previously on (x, y, z) is retrieved and its `onBlockDestroyedByPlayer` is called, which does nothing except for blocks of class `BlockCrops`, `BlockFire` and `BlockTNT`.
* If the player has an item on its hand, such item's `onBlockDestroyed` method is called. This method actually damages tools and swords.
* If the call to `thePlayer.canHarvestBlock` returns true, the block's `dropBlockAsItem` is called.

`theplayer.canHarvestBlock` works as follows:

* If the block's material is not metal nor rock, it returns true. The block can always be harvested.
* If it is metal or rock, the held item's `canHarvestBlock` is called and its return value returned.

The `canHarvestBlock` from Items returns false, but is redefined by `ItemPickAxe` as we have seen. So for our new golden pickaxe, first of all, `canHarvestBlock` should always return true. Then we would need to modify `PlayerControllerSP.sendBlockRemoved`, using the hook `hookOnBlockHarvested`. Se below for a detailed list of hooks and further explanations.

Now we have to:

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

# Status effects

I've added status for `EntityLiving` objects and status effects. They work almost the same way as 1.0.0+ potions - but without potions. You can create as many statuses as you need, and inflict them to any `EntityLiving` instance in your game - most notably the `EntityPlayerSP`. 

To create a new status you have to create a new class which extends the `Status` class and then create an instance of such object. For example, let's create a status to represent poison, which will make the affected entity lose health over time:

```java
    package com.mojontwins.modloader.entity.status;

    import net.minecraft.game.entity.Entity;
    import net.minecraft.game.entity.EntityLiving;
    import net.minecraft.game.entity.monster.EntityZombie;

    public class StatusPoisoned extends Status {

        /*
         * Constructor. needs a Status ID (which we'll be getting from a sequencer)
         * and a isBadEffect bookean (not used for the moment)
         */
        public StatusPoisoned(int id, boolean isBadEffect) {
            super(id, true);
        }

        /*
         * So we can give this object a name, for the sequencer
         */
        public StatusPoisoned setName(String name) {
            this.name = name;
            return this;
        }
        
        /*
         * This method actually performs the effect this status represents
         */
        public void performEffect (EntityLiving entityLiving, int amplifier) {
            // Decrease half a heart
            if (entityLiving.health > 1) {
                entityLiving.attackEntityFrom((Entity)null, 1);
            }
        }
        
        /*
         * the effect will only be performed if this returns true
         */     
        public boolean isReady (int tick, int amplifier) {
            // Run every 5 ticks
            return (tick % 5) == 0;
        }

        /*
         * This status will only applied if this method returns true
         */
        public boolean isApplicableTo (EntityLiving entityLiving) {
            // Zombies can't be poisoned
            return !(entityLiving instanceof EntityZombie);
        }
    }
```

Once the new class has been added you need to create an object of that class in your `mod_XXX` (for example, `mod_Example`):

```java
    statusPoisoned = new StatusPoisoned(Status.getNewStatusId(), true).setName("status.poisoned");
```

By default, active statuses will spawn white particles. If you want the particles to be of a different colour, add

```java
    statusPoisoned.particleColor = 0x70B433;
```

or, if you don't want particles at all, add

```java
    statusPoisoned.showParticles = false;
```

Status are applied to `EntityLiving`s via `StatusEffect`s. For instance, we've created a new `ItemFoodRawChicken` which will inflict `StatusPoisoned` upon being eaten:

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
```

This class overrides `ItemFood`'s default `onItemRightClick` with its own, which reduces the `ItemStack` size and inflicts the `StatusPoisoned` status. Note that it uses your instance in `mod_Example`.

There are other kinds of statuses which you only need to just wear out over time and which you can use alongside the defined code hooks to modify how the player or other `EntityLiving` behaves. For example, we can add a status which makes the player move faster for 100 ticks. As this doesn't need to override `Status`' methods, you can use the `Status` class directly in your `mod_XXXX`:

```java
    statusHyperSpeed = new Status (Status.getNewStatusId, false).setName("status.hyperSpeed");
    statusHyperSpeed.particleColor = 0x0000FF;
```

And activate the status effect when eating, for example, a Blue Pill

```java
    package com.mojontwins.modloader;

    import com.mojontwins.modloader.entity.status.StatusEffect;

    import net.minecraft.game.entity.player.EntityPlayer;
    import net.minecraft.game.item.ItemFood;
    import net.minecraft.game.item.ItemStack;
    import net.minecraft.game.level.World;

    public class ItemFoodBluePill extends ItemFood {
        public String name;

        public ItemFoodBluePill(int itemID, int healAmount) {
            super(itemID, healAmount);
        }

        public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer entityPlayer) {
            itemStack.stackSize --;

            // Add status `hyperSpeed` to player which lasts 100 ticks
            entityPlayer.addStatusEffect(new StatusEffect(mod_Example.statusHyperSpeed.id, 100, 1));
            
            return itemStack;
        }
        
        public ItemFoodBluePill setName (String name) {
            this.name = name;
            return this;
        }
    }
```

Then make the status work by implementing the hook `hookEntitySpeedModifier` in your mod:

```java
    public float hookEntitySpeedModifier (EntityLiving entityLiving) {
        if (entityLiving.isStatusActive (statusHyperSpeed)) {
            return 2.0F;
        }

        return 1.0F;
    }
```

# Tile Entities

Little information is stored in the World about blocks: just blockID and some metadata. This will not suffice when you expect more of a block - think about chests or furnaces. Those need more stuff to them. Minecraft implements this using Tile Entities, which are special entitiles which are related to a block in the world. When you place a furnace block in the world, a related tile entity is spawned in the same coordinates. You interact with it through the associated block. The tile entity for the furnace is cooking or smelting while you do your thing, and if you want to put or extract objects you right-click the related object and are given access to a GUI which actually shows and modifies vaules in the tile entity.

Blocks related to Tile Entities extend `BlockContainer`. This will automate the creation and destruction of the related `TileEntity` when the block is added or removed from the world. 

## Very simple example - The silly box

The silly box is just a box where you can store an item. It behaves this way:

* Then the silly block is placed, the silly tile entity will be spawned.
* If you right click it and it's not empty, it will drop the object it is containing.
* If you right click it and it is empty, it will take the object in your hand (if any).
* When harvested, it will drop the related block and the contents and the tile entity will be despawned.
* The top texture will be different than the sides / bottom and will change if the box is full.

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
            if (world.getBlockId(x, y, z) == 0 && this.contents != null) {
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

Things of importance:

* `readFromNBT` and `saveToNBT` read or save a copy of this tile entity to the level file. If this puzzles you, you have a ton of examples in the game code about saving/retrieving different data types.
* `putItem` is a custom method (not inherited from `TileEntity`) and its our entry point. Remember that this is the method we call from the block. It just performs the logic we described earlier.
* At the end of `putItem`, the block is replaced (if needed) with a new instance: empty to full, full to empty. This is actually a removal and a creation of a `BlockSillyBox` block, so the associated Tile Entity will be removed, then created. If we want persistence, we get a copy of the tile entity before modifying the world, and restore it afterwards.
* `onTileEntityRemoved` is called when the tile entity is removed. Beware! The tile entity is removed when you or an explosion destroys the world, but it will be removed whenever the `BlockSillyBox` gets updated. So if we want to do something special when the related block has been destroyed, we have to check if the current block at the coordinates, at this stage, is "0" (empty). If so, we drop the contained item.

## A more complex tile entity (with a GUI)

There used to be a mod which created a special block to make clay (this was around the beta period and clay was very rare). It used time, water, dirt and the sun to create clay. This has given me an idea of creating a special furnace or whatever you may call this in which you add dirt, a bucket of water, and coal, and it makes clay. Coal will exhaust and water will eventually run out.

We'll need a custom GUI for this. We'll take the existing furnace GUI as a template to create ours. 

# Animal & Monster Entities

The way Vanilla Indev works is by selecting a random number from 0 to the number of different mobs of each kind minus one, then, if possible, spawn the selected mob in the world.

In ModLoader, I've added a collection of hooks and a registering system so you can add your own mobs to be selected at random alongside the rest of the mobs, or control when or how they are selected if you prefer.

To add a new mod you need at least one new class: the one which describes your `Entity`. You can check at the examples included in this repository or base yours on already existing entities, for example an `EntityHusk` based on `EntityZombies`. Sometimes you'll need a custom renderer, extending from `Render`, and a model class, extending `BaseModel`. 

## Add a new monster or animal and let the engine select it.

* Add your `Entity`. 
* Add your `Render` and `BaseModel` if needed.
* Get a new entity ID from ModLoader:

```java
    entityTestID = ModLoader.getNewMobID();
```

* Configure a renderer for your Entity:

```java
    ModLoader.addEntityRenderer(EntityTest.class, new RenderTest(new ModelTest (), 0.5F));
```

* Register your entity. You have to do this in a special method in your mod class called `populateMobsHashMap`. This method is called to let you populate the default monster and animal lists (or even modify them!) after the world theme (normal, hell, paradise, woods, or your own) has been selected.

```java
    ModLoader.registerMonsterEntity (entityTestID, EntitTest.class);
```

(replace with `registerAnimalEntity` to register a new animal).

## Add a new monster or animal and control how it is spawned

To have complete control, perform all the steps mentioned above but the last: don't register your mob, so the engine never selects it automaticly. To select and then spawn your unregistered mobs, you have these hooks (for monsters; replace *Monster* by *Animal* in the method name for animals):

```java
    /*
     * Called by the creature spawner. Must return entityId
     */
    public int spawnerSelectMonster (int entityID) {
        return entityID;
    }
    
    /*
     * Called by the creature spawner. Must return entityId
     */
    public int spawnerSelectMonsterBasedOnPosition (int entityID, World world, int x, int y, int z) {
        return entityID;
    }

    /*
     * Called by the creature spawner. Return a new entity object based on entityID
     */
    public Object spawnMonster (int entityID, World world) {
        return null;
    }
```

To understand these hooks better, it's very useful to understand how Indev works:

1.- First select an entity ID at random. `spawnerSelectMonster` is called at this point, and the selected ID is passed. *You can modify this number and return it to change the selected entity*.
2.- Then it iterates a number of times finding a good spot. It selects a coordinate (x, y, z) in the world. `spawnerSelectMonsterBasedOnPosition` is called then. It is pased the selected ID again, a reference to the world, and the coordinates. Based on the coordinates and/or what's in the world, *You can modify this number and return it to change the selected entity*.
3.- Finally, it creates a new object of the correct `Entity` class based on the selected entity ID. If the ID is out of bounds (i.e. not registered), it will fail and call `spawnMonster`. If the passed `entityID` is satisfactory, *you* must create the entity object using your custom entity class and the `world` parameter you get.
4.- Then, the `getCanSpawnHere` method of your entity class will be called, and if that returns true and other conditions are met, the entity is spawned.

## Remove existing monster or animal entries from the list

In case you don't want a certain type of mob to appear in your theme / mod, just remove it from within your mod's `populateMobsHashMap`:

```java
    removeMonsterEntity(EntitySkeleton.class);
```

or 

```java
    removeAnimalEntity(EntityPig.class);
```

## Example: The Husk

Husks are almost like plain zombies but spawn on sand and don't burn in the sun. They also use a custom texture. So we start by getting the `EntityZombie` class, and putting all the code in there in our new `EntityHusk` class, changing what's necessary:

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
        
        public boolean getCanSpawnHere(float var1, float var2, float var3) {
            this.setPosition(var1, var2 + this.height / 2.0F, var3);
            return this.worldObj.checkIfAABBIsClear1(this.boundingBox) && this.worldObj.getCollidingBoundingBoxes(this.boundingBox).size() == 0 && !this.worldObj.getIsAnyLiquid(this.boundingBox);
        }
        
        protected String getLivingSound() {
            return "mob.zombie1";
        }

        protected String getHurtSound() {
            return "mob.zombiehurt1";
        }

        protected String getDeathSound() {
            return "mob.zombiedeath";
        }
    }
```

Changes are:

* `this.texture` has been altered. I've created a new texture and put it in `src/mob/husk.png`. It uses the `zombie.png` texture as a template.
* `this.moveSpeed` and `this.attackStrength` have been increased. Husks are stronger.
* `getEntityString` returns `Husk` rather than `Zombie`.
* I haven't modified `scoreValue`, but it's supposed to be return the ID of the item it drops.
* `onLivingUpdate` just calls the `super` here. There was a check to set the entity on fire I've removed.
* `getCanSpawnHere` checked if the light was dim enough then called the super. I've navigated to the super and pasted the code here, as Husks don't need darkess to spawn.

For rendering, we'll use the same renderer as the Zombie, which happens to be `RenderLiving` with a `ModelZombie` model. So next step is getting an ID for the new entity and configuring a renderer in our mod class:

```java
    entityHuskMobID = ModLoader.getNewMobID();
    ModLoader.addEntityRenderer(EntityHusk.class, new RenderLiving(new ModelZombie (), 0.5F));
```

We don't want Husks to be spawned naturally. What we want to do is that Zombies which would spawn on sand become Husks. So we **don't register** this mob, and use the hooks instead.

We'll let the engine select the monster and decide a set of coordinates. *Then* we'll check if the coordinates are on a sand block and the selected monster is a Zombie to change it for a husk. So we must add code to the `spawnerSelectMonsterBasedOnPosition` hook in our mod class:

```java
    public int spawnerSelectMonsterBasedOnPosition (int entityID, World world, int x, int y, int z) {
        // If it's a Zombie and it's been placed on sand...
        if (entityID == 3 && (world.getBlockId(x, y, z) == Block.sand.blockID || world.getBlockId(x, y - 1, z) == Block.sand.blockID)) {
            System.out.println ("Zombie @ " + x + ", " + z + " is now a Husk!");
            // It's now a husk!
            entityID = entityHuskMobID; 
        }
        return entityID;
    }
```

`3` is the entityID of Zombies (you can check `net.minecraft.game.level.Spawner` for a complete list). If the block at the selected spawn point is sand or the block below it is sand, change the ID for that of our Husk. After this change, if `entityID` gets modified and it gets the value of `entityHuskMobID`, when the engine attempts to create the new Entity class it will fail as this ID is not registered. So it will call `spawnMonster` where we are in charge of creating the `Entity`:

```java
    public Object spawnMonster (int entityID, World world) {        
        if (entityID == entityHuskMobID) return new EntityHusk(world);
        
        return null;
    }
```

# Hooks

I've added a number of hooks - modifications to the base classes to call methods from `BaseMod` (which you can override in your mod) to modify several game variables or add stuff. These are the hooks you can use from you mod.

If you are thinking on a mod and need to do a base class edit just drop me an issue and I'll add a hook for you so you don't have to play with the base classes. This will help me making Indev Modloader more powerful.

## `hookGenerateStructures`

This hook is called during level generation, in `LevelGenerator.generate`, right after the player has been spawn in the world and the basic house has been created around it. You can use this to add stuff to the level, which at this point is almost complete.

```java
    /* 
     * Called while generating the world.
     * runs after the player spawn point has been calculated and the house has been generated.
     */
    public void hookGenerateStructures (LevelGenerator levelGenerator, World world) {
    }
```

## `hookPlanting`

This hook is called at the end of the *planting* stage of level generation. You can plant stuff during this phase.

```java
    public void hookPlanting (LevelGenerator levelGenerator, World world, Random rand) {
    }  
```

For example, you can grow lilypads on water:

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

## `hookGameStart`

This hook is called right before a new game is about to start: the level has been generated and everything is in place. I've used this in the example to place stuff in the player's inventory so I can test things quickly:

```java
    /*
     * Called right before the game starts
     */
    public void hookGameStart (Minecraft minecraft) {       
    }
```

To add stuff to the player inventory you can use this method call:

```java
    minecraft.thePlayer.inventory.setInventorySlotContents(0, new ItemStack(itemOrBlock, quantity));
```

Examples:

```java
    minecraft.thePlayer.inventory.setInventorySlotContents(0, new ItemStack(Block.stoneOvenIdle, 1));
    minecraft.thePlayer.inventory.setInventorySlotContents(1, new ItemStack(Block.workbench, 1));
    minecraft.thePlayer.inventory.setInventorySlotContents(2, new ItemStack(Item.coal, 64));
    minecraft.thePlayer.inventory.setInventorySlotContents(3, new ItemStack(itemFoodRawChicken, 10));
    minecraft.thePlayer.inventory.setInventorySlotContents(9, new ItemStack(Block.cobblestone, 64));
    minecraft.thePlayer.inventory.setInventorySlotContents(10, new ItemStack(itemPebble, 64));
    minecraft.thePlayer.inventory.setInventorySlotContents(11, new ItemStack(itemSteelSword, 1));
    minecraft.thePlayer.inventory.setInventorySlotContents(12, new ItemStack(itemSteelPickaxe, 1));
    minecraft.thePlayer.inventory.setInventorySlotContents(13, new ItemStack(Item.pickaxeGold, 1));
    minecraft.thePlayer.inventory.setInventorySlotContents(14, new ItemStack(itemSteelIngot, 64));
```

## `hookOnBlockHarvested`

This hook is called whenever the player harvests a block (for example, when you used the pickaxe on a cobblestone block and it has just broken) and is used to override the default action which is a call to the object's `dropBlockAsItem` method. To override the default action do your thing and then return `true`. If your thing *fails to happen* and you want the default action to be carried out then return `false`.

```java 
    /*
     *  Called when block has been harvested
     */
    public boolean hookOnBlockHarvested (Minecraft minecraft, World world, int x, int y, int z, int blockID, int metadata) {
        return false;
    }
```

I've used this to create a golden pickaxe with silk touch. Once the item has been created you give it the special ability by adding this code to your `hookOnBlockHarvested`:

```java
    public boolean hookOnBlockHarvested (Minecraft minecraft, World world, int x, int y, int z, int blockID, int metadata) {

        // Get the item being held by the player:
        ItemStack curItem = minecraft.thePlayer.inventory.getCurrentItem();
        
        // If it's not null
        if (curItem != null) {

            // and happens to be our special golden pickaxe
            if (curItem.itemID == Item.pickaxeGold.shiftedIndex) {
                
                // Spawn a new EntityItem representing the same block that has been harvested!
                // This code is lifted from `Block.dropBlockAsItemWithChance`
                float px = world.random.nextFloat() * 0.7F + 0.15F;
                float py = world.random.nextFloat() * 0.7F + 0.15F;
                float pz = world.random.nextFloat() * 0.7F + 0.15F;

                // this is: the same block --------------------------------------------------------------------------------+
                //                                                                                                         |
                EntityItem entityItem = new EntityItem(world, (float)x + px, (float)y + py, (float)z + pz, new ItemStack(blockID));
                entityItem.delayBeforeCanPickup = 10;
                world.spawnEntityInWorld(entityItem);
                
                // Return true to override the default action
                return true;
            }
        }
        
        // Generally, return false for the default action is performed
        return false;
    } 
```

Check the diary for more info about this example.

## `HookAttackStrengthModifier`

Modifies the player's attack strength. It receives an `EntityLiving` representing the player (to be expanded to any EntityLiving in the future), an `Entity` representing the entity being hit, and `strength` with the original strength value.

```java 
    /*
     *  Called to recalculate player hit strength vs. entity
     */
    public int hookAttackStrengthModifier (EntityLiving entityLiving, Entity entityHit, int strength) {
        return strength;
    }
```

You can use this to make the player stronger or weaker under certain circumstances, for instance you can check if the player or the hit entity has a certain `StatusEffect` and apply a modification upon its type, like this:

```java
    public int hookAttackStrengthModifier (EntityLiving entityLiving, Entity entityHit, int strength) {
        if (entityLiving.isStatusActive (statusWeak)) {
            strength = strength >> 1;
        }

        if (entityHit.isStatusActive (statusWeak) {
            strength = strength << 1;
        }

        return strength;
    }
```

Check the documentation section about status effects.

## `HookBlockHitStrengthModifier`

Modifies the player's strength when hitting a block. The original value depends on the tool and material. It receives an `EntityLiving` representing the player, a `Block` representing the block being hit, and the orginal `strength` as calculated by the engine. 

```java 
    /*
     *  Called to recalculate player hit strength vs. block
     */
    public float hookBlockHitStrengthModifier (EntityLiving entityLiving, Block block, float strength) {
        return strength;
    }
```

You can use this to make the player mine faster or slower under certain circumstances, depending on the block or maybe a certain `StatusEffect`:

```java
    public float hookBlockHitStrengthModifier (EntityLiving entityLiving, Block block, float strength) {
        if (entityLiving.isStatusActive (statusMineFaster)) {
            strength = strength * 1.5F;
        }

        return strength;
    }
```

Check the documentation section about status effects.

## `hookEntitySpeedModifier`

Can be used to modify the speed of a living entity (i.e. the player). It receives an `entityLiving` representing the entity to be modified, and a `speedModifier` which is `1.0F` for no change. The value you return will be multiplied with the base speed of the living entity.

```java 
    /*
     *  Called to calculate an entity speed modifier. Return 1.0F for no change! 
     */
    public float hookEntitySpeedModifier (EntityLiving entityLiving) {
        return 1.0F;
    }
```

For example:

```java
    public float hookEntitySpeedModifier (EntityLiving entityLiving) {
        if (entityLiving.isStatusActive (statusHyperSpeed)) {
            return 2.0F;
        }

        return 1.0F;
    }
```

# World generation

## Level Themes

Vanilla Indev offers four level themes: normal, hell, paradise and woods, which define some basic properties of level generation. With ModLoader you can easily add your own themes to the menu, with their own properties. 

To better understand how themes work, a basic explanation of how the level generator works is needed:

1. Initial setup is performed.

2. As a special case, if you select "floating islands" and a "deep" shape, the generation steps will be iterated 5 times, each at a different height, and the results combined in a single blocks array.

3. Water level is calculated. Initially at level 32 for normal levels (which are 64 blocks high), or 192 for "deep" levels, (which are 256 blocks high). For "floating islands" + "deep" levels, this value changes on each iteration: 192, 176, 128, 80 and 32.

4. **Raising**: The basic height map is calculated: for each (x, z) coordinate, a floor level is calculated based upon a couple of noise generators and some weird math.

5. **Eroding**: The height map is modified. Using again a couple of noise generators it "erodes" the surface by 1 block at some places.

6. **Soiling**: Using the height map, the block array is filled with actual blocks. The level stored in the height map becomes `floorLevel`, and based upon it and using again the noise generators, a `fillLevel` is calculated, generally below `floorLevel`, but sometimes surparsing it. If floating islands are being generated, some extra math is used to calculate a `islandBottomLevel`. Then, the block array is filled with dirt from `floorLevel` to `fillLevel` (note that this segment may not exist) and with stone from `floorLevel` down - up to `islandBottomLevel` for floating islands.

7. **Growing**: This section adds sand and gravel to the world. It sets a `beachLevel` just 1 block below `waterLevel`, or 2 blocks above if `levelType` is 2 (paradise). If the height map goes below this level, and using a chance controlled by a noise generator which depends on the level theme, it fills with sand.  It sometimes replaces water with gravel on shallow ponds.

8. **Carving**: A digger which runs for a distance varying angles in the process carves the world to make caves.

The iterator which runs the above process five times for "deep" floating islands ends here. The remaining steps are performed over the block array which is now populated with 5 floors of floating islands.

9. Ore generation: After caves are created, ore is added to the world in several quantities by replacing stone. 

10. **Melting**: Lava ponds are added.

11. At this point, the cloud height is set, and water and ground level are adjusted as follows: 

* Generally, `cloudHeight` is `height` + 2.
* if `floatingGen`, `groundLevel` is set to -128, `waterLevel` to -127, and `cloudHeight` to -16, all bellow the bottom of the map.
* if `islandGen`, `groundLevel` is set to `waterLevel` - 9, that is, 32 - 9 = 23.
* if `flatGen` or inland, `groundLevel` is `waterLevel` + 1, that is, 33, then `waterLevel` is adjusted to `groundLevel` - 16, becoming 17.

12. **Watering**: Water (or lava, for `levelType` 2 "hell") is then added below `waterLevel` using floor fills.

13. Level visuals: Based upon `levelType` (normal, hell, paradise or woods) several values are set:

* `world.skyColor` - 0x99CCFF by default
* `world.fogColor` - 0xFFFFFF by default
* `world.cloudColor` - 0xFFFFFF by default
* `skylightSubtracted` Seems to be 15 by default. Skeletons & zombies only burn at day if it is > 7.
* `skyBrightness` 15 by default.
* `defaultFluid` - water or lava (for hell).

14. **Assembling**: Sets up the world object. Copies the block array to the world. Sets up special bordes for several generators. Adds bedrock. Sets up the light levels.

15. **Building**: Finds a spawn and generates the Indev house around it. We also added a call to `hookGenerateStructures (levelGenerator, world)` to easily add structures from our mods.

16. **Planting**: Grass is grown on exposed dirt. Trees are planted (times 50 for the "wood" theme!). Flowers are added (using a multiplier of 1000 for the "paradise" theme, 100 otherwise) and mushrooms are grown (multiplier 50).

17. **Lighting**: Light is updated.

(A more detailed & technical breakdown can be found in the diary)

Note how the some customization is performed by selecting the world theme. We have expanded on this letting you create your own themes using a set of hooks and values.

## The `ModLevelTheme` class

Level themes are added by registering instances of classes which extend the `ModLevelTheme` class. Your themes are then added to the "New Level" menu and can be selected alongside the existing "normal", "hell", "paradise" and "woods" themes.

Extending the `ModLevelTheme` class with an empty class or registering a instance of `ModLevelTheme` directly would result on a copy of the "normal" theme. Overriding the provided attributes and methods and adding your code to them you can create your own themes:

### Adjust the water level

```java
    public int waterLevelAdjust = 0;                // in blocks; no change
```

In step 3, water level is calculated. The value you assign to this variable is added to the calculated water level for each step of generation (remember that the five first generation steps are performed five times for "deep" floating islands). Raise the water level using a positive value, or lower it using a negative value, measured in blocks.

### Adjust the floor level

```java
    public double adjustFloorLevel (LevelGenerator levelGenerator, double floorLevel) {
        return floorLevel;
    }
```

This hook is called during the **Raising** stage, just before the `floorLevel` calculated for each (x, z) cell in the horizontal plane is about to be converted to `int` and written to the height map. You can leave the level unchanged or modify it. Examples:

```java
    // Steeper hills
    return floorLevel < 0.0D ? floorLevel : floorLevel * 4;
```

```java
    // Deeper oceans and pools
    return floorLevel < 0.0D ? floorLevel * 4 : floorLevel;
```

```java
    // Bumpier overall
    return floorLevel * 2;
```

```java
    // Nice high mesas
    return floorLevel > 8.0D ? floorLevel += 16.0D : floorLevel;
```

etc.

### Adjust the calculated height map

```java
    public void adjustHeightMap (LevelGenerator levelGenerator, int [] heightMap) {
    }
```

After the **Eroding** stage, you can further modify the adjusted `heightmap` (which is an `int [levelGenerator.width * levelGenerator.depth]`).

### Do your own "soiling"

```java
    public int getSoilingBlockID (LevelGenerator levelGenerator, int y, int floorLevel, int fillLevel, int islandBottomLevel) {
        return -1;
    }
```

Let's you override the default behaviour as described above for the **Soiling** stage. Return which `blockID` to write to the block array based upon the current `y` and based upon the calculated `fllorLevel`, `filllevel` and `islandBottomLevel`. You can start by replicating the existing code and then tweaking it:

```java
    public int getSoilingBlockID (LevelGenerator levelGenerator, int y, int floorLevel, int fillLevel, int islandBottomLevel) {
        int blockID = 0;
        if (y <= floorLevel) {
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
```

Note that ores are only generated by replacing stone blocks, so be careful with this.

### Customising the Growing stage

I'd recommend reading what's in the diary about this stage and trying to understand the code. Once you have, you can tweak the behaviour with these three methods:

```java
    
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
```

### Make your ocean

In the **Watering** a series of flood fills are performed: first, randomly, if the filled space contains less than 640 blocks. Then, in the four edges of the horizontal plane set at `waterLevel` to create the surrounding ocean (if appliable). You can select which `blockID` to use. Returning `-1` will select `Block.waterstill.blockID`. `inland` is set to `true` during the initial fills (which usually fill little ponds), and `false` during the later fills (those which create oceans).

```java
    public int getWateringBlockID (LevelGenerator levelGenerator, boolean inland) {
        return -1;
    }
```

### Configure the level visuals

```java
    public void setVisuals (LevelGenerator levelGenerator, World world) {
    }
```

Use this to modify any of these world values:

* `world.skyColor` - 0x99CCFF by default
* `world.fogColor` - 0xFFFFFF by default
* `world.cloudColor` - 0xFFFFFF by default
* `world.skylightSubtracted` Seems to be 15 by default. Skeletons & zombies only burn at day if it is > 7.
* `world.skyBrightness` 15 by default.
* `world.defaultFluid` - water or lava (for hell).

### Do your own planting

```java
    public boolean overridePlanting (LevelGenerator levelGenerator, World world) {
        return false;
    }  
```

If you return `true`, the **planting** stage will be overriden. Add your custom plants here. 

## Registering your new theme

To register a new theme, create your theme class extending `ModLevelTheme`, and then call `ModLoader.RegisterTheme` with a new object of your theme class. The String used in the constructor will be the name of your theme shown in the new level menu:

```java
    int yourThemeID;

    [...]

    yourThemeID = ModLoader.registerWorldTheme(new ThemeYourTheme("Your Theme"));
```

# Simple example: generate seaweeds!

Oceans are boring. Let's add seaweeds with animated textures. Start by adding the new block class. Note that we are using `renderIndex` 1, and that we are attempting to grow our seaweeds at random, with a 1:4 chance:

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
                if (world.getBlockId(x, y + 1, z) == Block.waterStill.blockID && world.getBlockId(x, y + 2, z) == Block.waterStill.blockID ) {
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

In your mod class, create the block, assign an animation as texture, and register it:

```java
    // Seaweeds with animated textures
    blockSeaWeed = new BlockSeaWeed(ModLoader.getItemId()).setBlockHardness(0.2F).setName("block.sea_weed");
    blockSeaWeed.blockIndexInTexture = ModLoader.addAnimation(EnumTextureAtlases.TERRAIN, "textures/block_seaweed.png", 1);
    ModLoader.registerBlock(blockSeaWeed);
```

And finally, add a simple generator to the `hookPlanting` method.

```java
    public void hookPlanting (LevelGenerator levelGenerator, World world, Random rand) {
        int worldArea = world.length * world.width;
        
        // Grow seaweeds
        int numSeaWeeds = worldArea / 4;
        for (int i = 0; i < numSeaWeeds; i ++) {
            int x = rand.nextInt(world.width);
            int z = rand.nextInt(world.length);
            int y = world.getSeaBed(x, z);
            
            if (y > 0) {
                int height = 4 + rand.nextInt(4);
                for (int j = 0; j < height && y < world.waterLevel - 2; j ++) {
                    y ++;
                    if (world.getBlockId(x, y, z) == Block.waterStill.blockID) {
                        world.setBlockWithNotify(x, y, z, blockSeaWeed.blockID);
                    }
                }
            }           
        }
    } 
```

# Full example: The Desert theme

The Desert theme replaces dirt with sand, trees with cacti and flowers with dead bushes. No trees mean that the only wood you can get should come from our spawning house. You can get sticks from dead bushes as well. Zombies will be turned to Husks if they are spawned on sand. 

But before we add the proper theme, we have to add cacti and dead bushes for the planting stage. We'll be adding a cactus generator as well. Cacti are taken from Alpha 1.0.6, where they appeared first and didn't require a custom renderer. We *could* add the custom renderer, but I want to keep with the "classic" feel as much as possible.

`BlockCactus` is your usual plan, with methods to tell wether it can be placed and / or grown, and methods to make the plant collapse if it's cut below the top. At this point, you should be familiar with all that's going on here:

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

        public BlockCactus(int id) {
            super(id, Material.plants);
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

The generator class just uses cacti blocks to grow cacti on sand. WorldGen classes weren't a thing in Indev, but anyways:

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
                        if (((BlockCactus)mod_DesertTheme.blockCactus).canBlockStay(var1, var7, var8 + var11, var9)) {
                            var1.setBlock(var7, var8 + var11, var9, mod_DesertTheme.blockCactus.blockID);
                        }
                    }
                }
            }

            return true;
        }
    }
```

Dead bushes are also plants. I'll be using the old dead bush texture (it was later replaced) and the usual "cross" rendered used by flowers and mushrooms, which happens to be `renderType` 1.

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
            return var1.getBlockId(var2, var3, var4) == 0 && this.canThisPlantGrowOnThisBlockID(var1.getBlockId(var2, var3 - 1, var4));
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
        
        public int idDropped(int par1, Random rand) {
            // 1 in 4 chance of dropping a stick
            if (rand.nextInt(4) == 0) {
                return Item.stick.shiftedIndex;
            } else return -1;
        }
    }
```

Once we have our classes in place, let's create the theme class, extending `ModLevelTheme`. Note that this is a rather simple theme definition and not all methods have been overriden. Pay special attention to the last method, `overridePlanting`, where we spawn dead bushes and call the cacti generator:

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

The last step to make all this happen is creating our mod class. In `mod_DeserTheme` I instantiate the block classes and register the new blocks, configure husks, add the hooks needed to make them spawn, and finally I register the new theme so it's a thing and it's selectable in the new level menú:

```java
    package com.mojontwins.modloader;

    import net.minecraft.client.model.ModelZombie;
    import net.minecraft.client.renderer.entity.RenderLiving;
    import net.minecraft.game.block.Block;
    import net.minecraft.game.level.World;

    public class mod_DesertTheme extends BaseMod {
        public static ModBlock blockCactus;
        public static ModBlock blockDeadBush;
        
        public static int desertThemeID;
        public static int entityHuskMobID;
        
        public mod_DesertTheme() {
        }

        @Override
        public void load() throws Exception {
            
            // Add some extra blocks
            
            blockCactus = new BlockCactus(ModLoader.getBlockId ()).setBlockHardness(0.4F).setName("block.cactus");
            blockCactus.blockIndexInTexture = ModLoader.addOverride(EnumTextureAtlases.TERRAIN, "textures/block_cactus.png");
            ((BlockCactus)blockCactus).bottomTextureIndex = ModLoader.addOverride(EnumTextureAtlases.TERRAIN, "textures/block_cactus_bottom.png");
            ((BlockCactus)blockCactus).topTextureIndex = ModLoader.addOverride(EnumTextureAtlases.TERRAIN, "textures/block_cactus_top.png");
            ModLoader.registerBlock(blockCactus);
            
            blockDeadBush = new BlockDeadBush(ModLoader.getBlockId()).setBlockHardness(0.1F).setName("block.dead_bush");
            blockDeadBush.blockIndexInTexture = ModLoader.addOverride(EnumTextureAtlases.TERRAIN, "textures/block_dead_bush.png");
            ModLoader.registerBlock(blockDeadBush);
            
            // Add the level theme
            
            desertThemeID = ModLoader.registerWorldTheme(new ThemeDesert("Desert"));
            
            // Add husks
            
            entityHuskMobID = ModLoader.getNewMobID();
            ModLoader.addEntityRenderer(EntityHusk.class, new RenderLiving(new ModelZombie (), 0.5F));
            // Note how husks are NOT registered as monsters as we don't want the engine to auto-select them.

        }
        
        public int spawnerSelectMonsterBasedOnPosition (int entityID, World world, int x, int y, int z) {
            // If it's a Zombie and it's been placed on sand...
            if (entityID == 3 && (world.getBlockId(x, y, z) == Block.sand.blockID || world.getBlockId(x, y - 1, z) == Block.sand.blockID)) {
                // It's now a husk!
                entityID = entityHuskMobID; 
            }
            return entityID;
        }

        public Object spawnMonster (int entityID, World world) {        
            if (entityID == entityHuskMobID) return new EntityHusk(world);
            
            return null;
        }
    }
```

# Full example: Clay stuff

We'll try and add a new mod which adds clay, means of getting clay, buckets you can fill with water and drop elsewhere, a new tile entity to make clay with sand/dirt, water, and time, etc.

We need a custom class `BlockClay` as when you break clay it drops 4 clay balls:

```java
    package com.mojontwins.modloader;

    import java.util.Random;

    import net.minecraft.game.block.Material;

    public class BlockClay extends ModBlock {

        public BlockClay(int id, Material material) {
            super(id, material);
        }

        public int quantityDropped(Random var1) {
            return 4;
        }

        public int idDropped(int var1, Random var2) {
            return mod_ClayStuff.itemClayBall.shiftedIndex;
        }   
    }
```

And our first, bare-bones `mod_ClayStuff` mod:

```java
    package com.mojontwins.modloader;

    import net.minecraft.game.block.Material;
    import net.minecraft.game.item.ItemStack;

    public class mod_ClayStuff extends BaseMod {

        public static ModBlock blockClay;
        public static ModItem itemClayBall;
        public static ModItem itemBucketEmpty;
        public static ModItem itemBucketWater;
        
        @Override
        public void load() throws Exception {
            blockClay = new BlockClay(ModLoader.getBlockId(), Material.ground).setBlockHardness(0.5F).setName("block.clay");
            blockClay.blockIndexInTexture = ModLoader.addOverride(EnumTextureAtlases.TERRAIN, "textures/block_clay.png");
            ModLoader.registerBlock(blockClay);
            
            itemClayBall = new ModItem(ModLoader.getItemId()).setMaxStackSize(64).setName("item.clay_ball");
            itemClayBall.setIconIndex(ModLoader.addOverride(EnumTextureAtlases.ITEMS, "textures/item_clay_ball.png"));
            
            ModLoader.addRecipe(new ItemStack(blockClay), new Object [] {
                "XX", "XX",
                'X', itemClayBall
            });
        }

    }
```

Next thing to add is the empty bucket. The empty bucket can be used on water, will extract the water block, and become a water bucket, which we'll handle later. Of course, as items and blocks can't hold a state, we'll use the same class which can be instanced in different ways to represent the different states of the "same" water bucket.

```java
    package com.mojontwins.modloader;

    import net.minecraft.client.physics.MovingObjectPosition;
    import net.minecraft.game.block.Block;
    import net.minecraft.game.block.Material;
    import net.minecraft.game.entity.player.EntityPlayer;
    import net.minecraft.game.item.ItemStack;
    import net.minecraft.game.level.World;

    public class ItemBucket extends ModItem {
        // What's inside the bucket?
        public int contents = 0;    
        
        public ItemBucket(int var1, int contents) {
            super(var1);
            this.contents = contents;
        }

        public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer entityPlayer) {
            // First we detect if we hit water
            MovingObjectPosition movingobjectposition = getMovingObjectPositionFromPlayer(world, entityPlayer, true);

            if (movingobjectposition == null) {
                return itemStack;
            }

            if (movingobjectposition.typeOfHit == 0) {
                int x = movingobjectposition.blockX;
                int y = movingobjectposition.blockY;
                int z = movingobjectposition.blockZ;
                int sideHit = movingobjectposition.sideHit;

                if (world.getBlockMaterial(x, y, z) == Material.water && contents == 0) {
                    // This is an empty bucket hitting water
                    
                    // Substitute the hit block with air
                    world.setBlockWithNotify(x, y, z, 0);
                    
                    // Replace this item with a water bucket
                    return new ItemStack (mod_ClayStuff.itemBucketWater);
                } else if (Block.blocksList[world.getBlockId(x, y, z)].isOpaqueCube() && contents == Block.waterStill.blockID) {
                    // This bucket is full of water

                    // Abuse ItemBlock.onItemUse, which puts a block in the world
                    ItemStack itemStackWaterMoving = new ItemStack (Block.waterMoving);
                    (itemStackWaterMoving).getItem().onItemUse (itemStackWaterMoving, world, x, y, z, sideHit);
                    
                    // Replace this item with an empty bucket
                    return new ItemStack (mod_ClayStuff.itemBucketEmpty);
                }
            }

            return itemStack;
        }   
    }
```

If you create an object of class `ItemBucket` it will represent an empty bucket or a bucket with water based on the second parameter in the constructor, which will get 0 for "empty" and `Block.waterStill.blockId` for "filled with water". This value is stored in `contents`. The bucket is used when the player right-clicks somewhere in the world with the bucket equiped. `onItemRightClick` is called then.

First of all we get the coordinates and the side of the block in the world being hit in a `MovingObjectPosition` object. If it is null, we are not hitting anything (the block under the mouse cursor is far away), so return the `itemStack` untouched. If not, we check the type of hit, which is 0 for blocks and 1 for entities. 

If the block hit's material is water, and *this* is an empty bucket (`contents` is 0), then we *fill the bucket*: remove the block hit by the bucket, and return a new ItemStack containing the bucket with water.

If the block hit is opaque, and *this* is a bucket with water (`contents` is `Block.waterStill.blockID`), then we have to put water in the world but beware: if we used `world.setBlockWithNotify` with `(x, y, z)` as in the previous branch we would be substituting the block hit with the water block, and this is not what we want. We could use `sideHit` to adjust the `(x, y, z)` coordinates so the new block stuck to the side of the block being hit, but such calculation is already present in the Minecraft code: when you right click in the world with a block equiped, `ItemBlock.onItemUse` is called and puts the block in the world, if possible. So we make a new itemstack with `Block.waterMoving`, and then call `onItemUse` on `getItem()`. Finally, we return a new ItemStack containing the empty bucket.

Now what's left is creating the objects in the mod class:

```java 
    itemBucketEmpty = new ItemBucket(ModLoader.getItemId(), 0).setMaxStackSize(1).setName("item.bucket_empty");
    itemBucketEmpty.setIconIndex(ModLoader.addOverride(EnumTextureAtlases.ITEMS, "textures/item_bucket_empty.png"));
    
    itemBucketWater = new ItemBucket(ModLoader.getItemId(), Block.waterStill.blockID).setMaxStackSize(1).setName("item.bucket_water");
    itemBucketWater.setIconIndex(ModLoader.addOverride(EnumTextureAtlases.ITEMS, "textures/item_bucket_water.png"));
```

# Full Example: Add slimes, and slime balls, and a slime bucket!

