package com.mojontwins.modloader;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.game.level.World;
import net.minecraft.game.block.Block;

public class RenderSkeletonSkull {

	/*
	 * Texture lookup:
	 * ti_0 = modloader:block_skeleton_head
	 */
	public static boolean renderBlock (World world, Block block, int meta, float x, float y, float z, int ti_0) {
		if (meta == 3) {
			// Facing south (180 degrees)
			return renderMeta3(world, block, x, y, z, ti_0);
		}

		if (meta == 4) {
			// Facing west (90 degrees)
			return renderMeta4(world, block, x, y, z, ti_0);
		}

		if (meta == 5) {
			// Facing east (270 degrees)
			return renderMeta5(world, block, x, y, z, ti_0);
		}

		// Facing north (default)
		return renderMeta2(world, block, x, y, z, ti_0);
	}

	public static boolean renderMeta2 (World world, Block block, float x, float y, float z, int ti_0) {
		Tessellator tessellator = Tessellator.instance;
		float x1, y1, z1, x2, y2, z2;
		float u1, v1, u2, v2;

		// Texture #0
		float t0_u = (float) ((ti_0 & 0x0f) << 4) / 256.0F;
		float t0_v = (float) (ti_0 & 0xff0) / 256.0F;

		// Cube #0

		x1 = x + 0.2500F;
		y1 = y + 0.0000F;
		z1 = z + 0.2500F;
		x2 = x + 0.7500F;
		y2 = y + 0.5000F;
		z2 = z + 0.7500F;

		setLightValue(tessellator, world, block, x, y + 1, z, 1.0F);
		u1 = t0_u + 0.0000000F;
		v1 = t0_v + 0.0312500F;
		u2 = t0_u + 0.0312500F;
		v2 = t0_v + 0.0625000F;
		tessellator.addVertexWithUV(x2, y2, z2, u1, v1);
		tessellator.addVertexWithUV(x2, y2, z1, u1, v2);
		tessellator.addVertexWithUV(x1, y2, z1, u2, v2);
		tessellator.addVertexWithUV(x1, y2, z2, u2, v1);

		setLightValue(tessellator, world, block, x, y - 1, z, 0.5F);
		u1 = t0_u + 0.0000000F;
		v1 = t0_v + 0.0312500F;
		u2 = t0_u + 0.0312500F;
		v2 = t0_v + 0.0625000F;
		tessellator.addVertexWithUV(x1, y1, z2, u2, v1);
		tessellator.addVertexWithUV(x1, y1, z1, u2, v2);
		tessellator.addVertexWithUV(x2, y1, z1, u1, v2);
		tessellator.addVertexWithUV(x2, y1, z2, u1, v1);

		setLightValue(tessellator, world, block, x, y, z - 1, 0.8F);
		u1 = t0_u + 0.0312500F;
		v1 = t0_v + 0.0312500F;
		u2 = t0_u + 0.0625000F;
		v2 = t0_v + 0.0625000F;
		tessellator.addVertexWithUV(x1, y2, z1, u2, v1);
		tessellator.addVertexWithUV(x2, y2, z1, u1, v1);
		tessellator.addVertexWithUV(x2, y1, z1, u1, v2);
		tessellator.addVertexWithUV(x1, y1, z1, u2, v2);

		setLightValue(tessellator, world, block, x, y, z + 1, 0.8F);
		u1 = t0_u + 0.0000000F;
		v1 = t0_v + 0.0312500F;
		u2 = t0_u + 0.0312500F;
		v2 = t0_v + 0.0625000F;
		tessellator.addVertexWithUV(x1, y2, z2, u1, v1);
		tessellator.addVertexWithUV(x1, y1, z2, u1, v2);
		tessellator.addVertexWithUV(x2, y1, z2, u2, v2);
		tessellator.addVertexWithUV(x2, y2, z2, u2, v1);

		setLightValue(tessellator, world, block, x - 1, y, z, 0.6F);
		setLightValue(tessellator, world, block, x + 1, y, z, 0.6F);
		u1 = t0_u + 0.0000000F;
		v1 = t0_v + 0.0000000F;
		u2 = t0_u + 0.0312500F;
		v2 = t0_v + 0.0312500F;
		tessellator.addVertexWithUV(x2, y2, z1, u2, v1);
		tessellator.addVertexWithUV(x2, y2, z2, u1, v1);
		tessellator.addVertexWithUV(x2, y1, z2, u1, v2);
		tessellator.addVertexWithUV(x2, y1, z1, u2, v2);

		setLightValue(tessellator, world, block, x + 1, y, z, 0.6F);
		u1 = t0_u + 0.0312500F;
		v1 = t0_v + 0.0000000F;
		u2 = t0_u + 0.0625000F;
		v2 = t0_v + 0.0312500F;
		tessellator.addVertexWithUV(x1, y2, z1, u1, v1);
		tessellator.addVertexWithUV(x1, y1, z1, u1, v2);
		tessellator.addVertexWithUV(x1, y1, z2, u2, v2);
		tessellator.addVertexWithUV(x1, y2, z2, u2, v1);

		return true;
	}

	public static boolean renderMeta3 (World world, Block block, float x, float y, float z, int ti_0) {
		Tessellator tessellator = Tessellator.instance;
		float x1, y1, z1, x2, y2, z2;
		float u1, v1, u2, v2;

		// Texture #0
		float t0_u = (float) ((ti_0 & 0x0f) << 4) / 256.0F;
		float t0_v = (float) (ti_0 & 0xff0) / 256.0F;

		// Cube #0

		x1 = x + 0.7500F;
		y1 = y + 0.0000F;
		z1 = z + 0.7500F;
		x2 = x + 0.2500F;
		y2 = y + 0.5000F;
		z2 = z + 0.2500F;

		setLightValue(tessellator, world, block, x, y + 1, z, 1.0F);
		u1 = t0_u + 0.0000000F;
		v1 = t0_v + 0.0312500F;
		u2 = t0_u + 0.0312500F;
		v2 = t0_v + 0.0625000F;
		tessellator.addVertexWithUV(x2, y2, z2, u1, v1);
		tessellator.addVertexWithUV(x2, y2, z1, u1, v2);
		tessellator.addVertexWithUV(x1, y2, z1, u2, v2);
		tessellator.addVertexWithUV(x1, y2, z2, u2, v1);

		setLightValue(tessellator, world, block, x, y - 1, z, 0.5F);
		u1 = t0_u + 0.0000000F;
		v1 = t0_v + 0.0312500F;
		u2 = t0_u + 0.0312500F;
		v2 = t0_v + 0.0625000F;
		tessellator.addVertexWithUV(x1, y1, z2, u2, v1);
		tessellator.addVertexWithUV(x1, y1, z1, u2, v2);
		tessellator.addVertexWithUV(x2, y1, z1, u1, v2);
		tessellator.addVertexWithUV(x2, y1, z2, u1, v1);

		setLightValue(tessellator, world, block, x, y, z + 1, 0.8F);
		u1 = t0_u + 0.0312500F;
		v1 = t0_v + 0.0312500F;
		u2 = t0_u + 0.0625000F;
		v2 = t0_v + 0.0625000F;
		tessellator.addVertexWithUV(x1, y2, z1, u2, v1);
		tessellator.addVertexWithUV(x2, y2, z1, u1, v1);
		tessellator.addVertexWithUV(x2, y1, z1, u1, v2);
		tessellator.addVertexWithUV(x1, y1, z1, u2, v2);

		setLightValue(tessellator, world, block, x, y, z - 1, 0.8F);
		u1 = t0_u + 0.0000000F;
		v1 = t0_v + 0.0312500F;
		u2 = t0_u + 0.0312500F;
		v2 = t0_v + 0.0625000F;
		tessellator.addVertexWithUV(x1, y2, z2, u1, v1);
		tessellator.addVertexWithUV(x1, y1, z2, u1, v2);
		tessellator.addVertexWithUV(x2, y1, z2, u2, v2);
		tessellator.addVertexWithUV(x2, y2, z2, u2, v1);

		setLightValue(tessellator, world, block, x + 1, y, z, 0.6F);
		setLightValue(tessellator, world, block, x + 1, y, z, 0.6F);
		u1 = t0_u + 0.0000000F;
		v1 = t0_v + 0.0000000F;
		u2 = t0_u + 0.0312500F;
		v2 = t0_v + 0.0312500F;
		tessellator.addVertexWithUV(x2, y2, z1, u2, v1);
		tessellator.addVertexWithUV(x2, y2, z2, u1, v1);
		tessellator.addVertexWithUV(x2, y1, z2, u1, v2);
		tessellator.addVertexWithUV(x2, y1, z1, u2, v2);

		setLightValue(tessellator, world, block, x - 1, y, z, 0.6F);
		u1 = t0_u + 0.0312500F;
		v1 = t0_v + 0.0000000F;
		u2 = t0_u + 0.0625000F;
		v2 = t0_v + 0.0312500F;
		tessellator.addVertexWithUV(x1, y2, z1, u1, v1);
		tessellator.addVertexWithUV(x1, y1, z1, u1, v2);
		tessellator.addVertexWithUV(x1, y1, z2, u2, v2);
		tessellator.addVertexWithUV(x1, y2, z2, u2, v1);

		return true;
	}

	public static boolean renderMeta4 (World world, Block block, float x, float y, float z, int ti_0) {
		Tessellator tessellator = Tessellator.instance;
		float x1, y1, z1, x2, y2, z2;
		float u1, v1, u2, v2;

		// Texture #0
		float t0_u = (float) ((ti_0 & 0x0f) << 4) / 256.0F;
		float t0_v = (float) (ti_0 & 0xff0) / 256.0F;

		// Cube #0

		x1 = x + 0.7500F;
		y1 = y + 0.0000F;
		z1 = z + 0.7500F;
		x2 = x + 0.2500F;
		y2 = y + 0.5000F;
		z2 = z + 0.2500F;

		setLightValue(tessellator, world, block, x, y + 1, z, 1.0F);
		u1 = t0_u + 0.0000000F;
		v1 = t0_v + 0.0312500F;
		u2 = t0_u + 0.0312500F;
		v2 = t0_v + 0.0625000F;
		tessellator.addVertexWithUV(x2, y2, z2, u1, v1);
		tessellator.addVertexWithUV(x2, y2, z1, u2, v1);
		tessellator.addVertexWithUV(x1, y2, z1, u2, v2);
		tessellator.addVertexWithUV(x1, y2, z2, u1, v2);

		setLightValue(tessellator, world, block, x, y - 1, z, 0.5F);
		u1 = t0_u + 0.0000000F;
		v1 = t0_v + 0.0312500F;
		u2 = t0_u + 0.0312500F;
		v2 = t0_v + 0.0625000F;
		tessellator.addVertexWithUV(x1, y1, z2, u1, v2);
		tessellator.addVertexWithUV(x1, y1, z1, u2, v2);
		tessellator.addVertexWithUV(x2, y1, z1, u2, v1);
		tessellator.addVertexWithUV(x2, y1, z2, u1, v1);

		setLightValue(tessellator, world, block, x + 1, y, z, 0.6F);
		u1 = t0_u + 0.0000000F;
		v1 = t0_v + 0.0312500F;
		u2 = t0_u + 0.0312500F;
		v2 = t0_v + 0.0625000F;
		tessellator.addVertexWithUV(x1, y2, z1, u1, v1);
		tessellator.addVertexWithUV(x1, y1, z1, u1, v2);
		tessellator.addVertexWithUV(x1, y1, z2, u2, v2);
		tessellator.addVertexWithUV(x1, y2, z2, u2, v1);

		setLightValue(tessellator, world, block, x - 1, y, z, 0.6F);
		setLightValue(tessellator, world, block, x + 1, y, z, 0.6F);
		u1 = t0_u + 0.0312500F;
		v1 = t0_v + 0.0312500F;
		u2 = t0_u + 0.0625000F;
		v2 = t0_v + 0.0625000F;
		tessellator.addVertexWithUV(x2, y2, z1, u2, v1);
		tessellator.addVertexWithUV(x2, y2, z2, u1, v1);
		tessellator.addVertexWithUV(x2, y1, z2, u1, v2);
		tessellator.addVertexWithUV(x2, y1, z1, u2, v2);

		setLightValue(tessellator, world, block, x, y, z - 1, 0.8F);
		u1 = t0_u + 0.0312500F;
		v1 = t0_v + 0.0000000F;
		u2 = t0_u + 0.0625000F;
		v2 = t0_v + 0.0312500F;
		tessellator.addVertexWithUV(x1, y2, z1, u2, v1);
		tessellator.addVertexWithUV(x2, y2, z1, u1, v1);
		tessellator.addVertexWithUV(x2, y1, z1, u1, v2);
		tessellator.addVertexWithUV(x1, y1, z1, u2, v2);

		setLightValue(tessellator, world, block, x, y, z + 1, 0.8F);
		u1 = t0_u + 0.0000000F;
		v1 = t0_v + 0.0000000F;
		u2 = t0_u + 0.0312500F;
		v2 = t0_v + 0.0312500F;
		tessellator.addVertexWithUV(x1, y2, z2, u1, v1);
		tessellator.addVertexWithUV(x1, y1, z2, u1, v2);
		tessellator.addVertexWithUV(x2, y1, z2, u2, v2);
		tessellator.addVertexWithUV(x2, y2, z2, u2, v1);

		return true;
	}

	public static boolean renderMeta5 (World world, Block block, float x, float y, float z, int ti_0) {
		Tessellator tessellator = Tessellator.instance;
		float x1, y1, z1, x2, y2, z2;
		float u1, v1, u2, v2;

		// Texture #0
		float t0_u = (float) ((ti_0 & 0x0f) << 4) / 256.0F;
		float t0_v = (float) (ti_0 & 0xff0) / 256.0F;

		// Cube #0

		x1 = x + 0.7500F;
		y1 = y + 0.0000F;
		z1 = z + 0.7500F;
		x2 = x + 0.2500F;
		y2 = y + 0.5000F;
		z2 = z + 0.2500F;

		setLightValue(tessellator, world, block, x, y + 1, z, 1.0F);
		u1 = t0_u + 0.0000000F;
		v1 = t0_v + 0.0312500F;
		u2 = t0_u + 0.0312500F;
		v2 = t0_v + 0.0625000F;
		tessellator.addVertexWithUV(x2, y2, z2, u2, v1);
		tessellator.addVertexWithUV(x2, y2, z1, u1, v1);
		tessellator.addVertexWithUV(x1, y2, z1, u1, v2);
		tessellator.addVertexWithUV(x1, y2, z2, u2, v2);

		setLightValue(tessellator, world, block, x, y - 1, z, 0.5F);
		u1 = t0_u + 0.0000000F;
		v1 = t0_v + 0.0312500F;
		u2 = t0_u + 0.0312500F;
		v2 = t0_v + 0.0625000F;
		tessellator.addVertexWithUV(x1, y1, z2, u2, v2);
		tessellator.addVertexWithUV(x1, y1, z1, u1, v2);
		tessellator.addVertexWithUV(x2, y1, z1, u1, v1);
		tessellator.addVertexWithUV(x2, y1, z2, u2, v1);

		setLightValue(tessellator, world, block, x - 1, y, z, 0.6F);
		u1 = t0_u + 0.0312500F;
		v1 = t0_v + 0.0312500F;
		u2 = t0_u + 0.0625000F;
		v2 = t0_v + 0.0625000F;
		tessellator.addVertexWithUV(x1, y2, z1, u1, v1);
		tessellator.addVertexWithUV(x1, y1, z1, u1, v2);
		tessellator.addVertexWithUV(x1, y1, z2, u2, v2);
		tessellator.addVertexWithUV(x1, y2, z2, u2, v1);

		setLightValue(tessellator, world, block, x + 1, y, z, 0.6F);
		setLightValue(tessellator, world, block, x + 1, y, z, 0.6F);
		u1 = t0_u + 0.0000000F;
		v1 = t0_v + 0.0312500F;
		u2 = t0_u + 0.0312500F;
		v2 = t0_v + 0.0625000F;
		tessellator.addVertexWithUV(x2, y2, z1, u2, v1);
		tessellator.addVertexWithUV(x2, y2, z2, u1, v1);
		tessellator.addVertexWithUV(x2, y1, z2, u1, v2);
		tessellator.addVertexWithUV(x2, y1, z1, u2, v2);

		setLightValue(tessellator, world, block, x, y, z + 1, 0.8F);
		u1 = t0_u + 0.0000000F;
		v1 = t0_v + 0.0000000F;
		u2 = t0_u + 0.0312500F;
		v2 = t0_v + 0.0312500F;
		tessellator.addVertexWithUV(x1, y2, z1, u2, v1);
		tessellator.addVertexWithUV(x2, y2, z1, u1, v1);
		tessellator.addVertexWithUV(x2, y1, z1, u1, v2);
		tessellator.addVertexWithUV(x1, y1, z1, u2, v2);

		setLightValue(tessellator, world, block, x, y, z - 1, 0.8F);
		u1 = t0_u + 0.0312500F;
		v1 = t0_v + 0.0000000F;
		u2 = t0_u + 0.0625000F;
		v2 = t0_v + 0.0312500F;
		tessellator.addVertexWithUV(x1, y2, z2, u1, v1);
		tessellator.addVertexWithUV(x1, y1, z2, u1, v2);
		tessellator.addVertexWithUV(x2, y1, z2, u2, v2);
		tessellator.addVertexWithUV(x2, y2, z2, u2, v1);

		return true;
	}

	public static void setLightValue (Tessellator tessellator, World world, Block block, float x, float y, float z, float factor) {
		float f;
		if (world == null) f = factor; else f = block.getBlockBrightness(world, (int) x, (int) y, (int) z) * factor;
		if (Block.lightValue[block.blockID] > 0) f = factor;
		tessellator.setColorOpaque_F(f, f, f);
	}
}
