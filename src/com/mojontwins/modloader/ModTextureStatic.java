package com.mojontwins.modloader;

import java.awt.image.BufferedImage;

import net.minecraft.client.renderer.block.TextureFX;

public class ModTextureStatic extends TextureFX {
	public ModTextureStatic(int textureIndex, EnumTextureAtlases textureAtlas, BufferedImage bufferedImage) {
		super(textureIndex);
		tileImage = textureAtlas == EnumTextureAtlases.ITEMS ? 1 : 0;
		
		System.out.println ("ModTextureStatic " + textureIndex + ", " + textureAtlas + ", tileImage = " + tileImage);
		
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
