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
