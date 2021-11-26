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
	
	public int idDropped(int par1, Random rand, int par3) {
		// 1 in 4 chance of dropping a stick
        if (rand.nextInt(4) == 0) {
        	return Item.stick.shiftedIndex;
        } else return -1;
    }
}
