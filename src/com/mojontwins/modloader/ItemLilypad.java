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
