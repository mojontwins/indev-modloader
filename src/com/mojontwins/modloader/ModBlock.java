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
