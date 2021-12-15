package com.mojontwins.modloader;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.game.level.World;
import net.minecraft.game.block.Block;

public class RenderCauldron {

	/*
	 * Texture lookup:
	 * ti_0 = block_cauldron_xz
	 * ti_1 = block_cauldron_ns
	 * ti_2 = block_cauldron_w
	 * ti_3 = block_cauldron_e
	 * ti_4 = block_cauldron_ontents
	 */
	public static boolean renderBlock (World world, Block block, int meta, float x, float y, float z, int ti_0, int ti_1, int ti_2, int ti_3, int ti_4) {
		if (meta == 3) {
			// Facing south (180 degrees)
			return renderMeta3(world, block, x, y, z, ti_0, ti_1, ti_2, ti_3, ti_4);
		}

		if (meta == 4) {
			// Facing west (90 degrees)
			return renderMeta4(world, block, x, y, z, ti_0, ti_1, ti_2, ti_3, ti_4);
		}

		if (meta == 5) {
			// Facing east (270 degrees)
			return renderMeta5(world, block, x, y, z, ti_0, ti_1, ti_2, ti_3, ti_4);
		}

		// Facing north (default)
		return renderMeta2(world, block, x, y, z, ti_0, ti_1, ti_2, ti_3, ti_4);
	}

	public static boolean renderMeta2 (World world, Block block, float x, float y, float z, int ti_0, int ti_1, int ti_2, int ti_3, int ti_4) {
		Tessellator tessellator = Tessellator.instance;
		float x1, y1, z1, x2, y2, z2;
		float u1, v1, u2, v2;

		// Texture #0
		float t0_u = (float) ((ti_0 & 0x0f) << 4) / 256.0F;
		float t0_v = (float) (ti_0 & 0xff0) / 256.0F;

		// Texture #1
		float t1_u = (float) ((ti_1 & 0x0f) << 4) / 256.0F;
		float t1_v = (float) (ti_1 & 0xff0) / 256.0F;

		// Texture #2
		float t2_u = (float) ((ti_2 & 0x0f) << 4) / 256.0F;
		float t2_v = (float) (ti_2 & 0xff0) / 256.0F;

		// Texture #3
		float t3_u = (float) ((ti_3 & 0x0f) << 4) / 256.0F;
		float t3_v = (float) (ti_3 & 0xff0) / 256.0F;

		// Texture #4
		float t4_u = (float) ((ti_4 & 0x0f) << 4) / 256.0F;
		float t4_v = (float) (ti_4 & 0xff0) / 256.0F;

		// Cube #0

		x1 = x + 0.1250F;
		y1 = y + 0.0000F;
		z1 = z + 0.1250F;
		x2 = x + 0.8750F;
		y2 = y + 0.0625F;
		z2 = z + 0.8750F;

		setLightValue(tessellator, world, block, x, y + 1, z, 1.0F);
		u1 = t0_u + 0.0078125F;
		v1 = t0_v + 0.0078125F;
		u2 = t0_u + 0.0546875F;
		v2 = t0_v + 0.0546875F;
		tessellator.addVertexWithUV(x2, y2, z2, u1, v1);
		tessellator.addVertexWithUV(x2, y2, z1, u1, v2);
		tessellator.addVertexWithUV(x1, y2, z1, u2, v2);
		tessellator.addVertexWithUV(x1, y2, z2, u2, v1);

		setLightValue(tessellator, world, block, x, y - 1, z, 0.5F);
		u1 = t0_u + 0.0078125F;
		v1 = t0_v + 0.0078125F;
		u2 = t0_u + 0.0546875F;
		v2 = t0_v + 0.0546875F;
		tessellator.addVertexWithUV(x1, y1, z2, u2, v1);
		tessellator.addVertexWithUV(x1, y1, z1, u2, v2);
		tessellator.addVertexWithUV(x2, y1, z1, u1, v2);
		tessellator.addVertexWithUV(x2, y1, z2, u1, v1);

		setLightValue(tessellator, world, block, x, y, z - 1, 0.8F);
		u1 = t0_u + 0.0078125F;
		v1 = t0_v + 0.0078125F;
		u2 = t0_u + 0.0546875F;
		v2 = t0_v + 0.0117188F;
		tessellator.addVertexWithUV(x1, y2, z1, u2, v1);
		tessellator.addVertexWithUV(x2, y2, z1, u1, v1);
		tessellator.addVertexWithUV(x2, y1, z1, u1, v2);
		tessellator.addVertexWithUV(x1, y1, z1, u2, v2);

		setLightValue(tessellator, world, block, x, y, z + 1, 0.8F);
		u1 = t0_u + 0.0078125F;
		v1 = t0_v + 0.0078125F;
		u2 = t0_u + 0.0546875F;
		v2 = t0_v + 0.0117188F;
		tessellator.addVertexWithUV(x1, y2, z2, u1, v1);
		tessellator.addVertexWithUV(x1, y1, z2, u1, v2);
		tessellator.addVertexWithUV(x2, y1, z2, u2, v2);
		tessellator.addVertexWithUV(x2, y2, z2, u2, v1);

		setLightValue(tessellator, world, block, x - 1, y, z, 0.6F);
		u1 = t0_u + 0.0078125F;
		v1 = t0_v + 0.0078125F;
		u2 = t0_u + 0.0546875F;
		v2 = t0_v + 0.0117188F;
		tessellator.addVertexWithUV(x2, y2, z1, u2, v1);
		tessellator.addVertexWithUV(x2, y2, z2, u1, v1);
		tessellator.addVertexWithUV(x2, y1, z2, u1, v2);
		tessellator.addVertexWithUV(x2, y1, z1, u2, v2);

		setLightValue(tessellator, world, block, x + 1, y, z, 0.6F);
		u1 = t0_u + 0.0078125F;
		v1 = t0_v + 0.0078125F;
		u2 = t0_u + 0.0546875F;
		v2 = t0_v + 0.0117188F;
		tessellator.addVertexWithUV(x1, y2, z1, u1, v1);
		tessellator.addVertexWithUV(x1, y1, z1, u1, v2);
		tessellator.addVertexWithUV(x1, y1, z2, u2, v2);
		tessellator.addVertexWithUV(x1, y2, z2, u2, v1);

		// Cube #1

		x1 = x + 0.0625F;
		y1 = y + 0.0625F;
		z1 = z + 0.0625F;
		x2 = x + 0.9375F;
		y2 = y + 0.1250F;
		z2 = z + 0.9375F;

		setLightValue(tessellator, world, block, x, y + 1, z, 1.0F);
		u1 = t0_u + 0.0078125F;
		v1 = t0_v + 0.0078125F;
		u2 = t0_u + 0.0546875F;
		v2 = t0_v + 0.0546875F;
		tessellator.addVertexWithUV(x2, y2, z2, u1, v1);
		tessellator.addVertexWithUV(x2, y2, z1, u1, v2);
		tessellator.addVertexWithUV(x1, y2, z1, u2, v2);
		tessellator.addVertexWithUV(x1, y2, z2, u2, v1);

		setLightValue(tessellator, world, block, x, y - 1, z, 0.5F);
		u1 = t0_u + 0.0078125F;
		v1 = t0_v + 0.0078125F;
		u2 = t0_u + 0.0546875F;
		v2 = t0_v + 0.0546875F;
		tessellator.addVertexWithUV(x1, y1, z2, u2, v1);
		tessellator.addVertexWithUV(x1, y1, z1, u2, v2);
		tessellator.addVertexWithUV(x2, y1, z1, u1, v2);
		tessellator.addVertexWithUV(x2, y1, z2, u1, v1);

		setLightValue(tessellator, world, block, x, y, z - 1, 0.8F);
		u1 = t0_u + 0.0078125F;
		v1 = t0_v + 0.0078125F;
		u2 = t0_u + 0.0546875F;
		v2 = t0_v + 0.0117188F;
		tessellator.addVertexWithUV(x1, y2, z1, u2, v1);
		tessellator.addVertexWithUV(x2, y2, z1, u1, v1);
		tessellator.addVertexWithUV(x2, y1, z1, u1, v2);
		tessellator.addVertexWithUV(x1, y1, z1, u2, v2);

		setLightValue(tessellator, world, block, x, y, z + 1, 0.8F);
		u1 = t0_u + 0.0078125F;
		v1 = t0_v + 0.0078125F;
		u2 = t0_u + 0.0546875F;
		v2 = t0_v + 0.0117188F;
		tessellator.addVertexWithUV(x1, y2, z2, u1, v1);
		tessellator.addVertexWithUV(x1, y1, z2, u1, v2);
		tessellator.addVertexWithUV(x2, y1, z2, u2, v2);
		tessellator.addVertexWithUV(x2, y2, z2, u2, v1);

		setLightValue(tessellator, world, block, x - 1, y, z, 0.6F);
		u1 = t0_u + 0.0078125F;
		v1 = t0_v + 0.0078125F;
		u2 = t0_u + 0.0546875F;
		v2 = t0_v + 0.0117188F;
		tessellator.addVertexWithUV(x2, y2, z1, u2, v1);
		tessellator.addVertexWithUV(x2, y2, z2, u1, v1);
		tessellator.addVertexWithUV(x2, y1, z2, u1, v2);
		tessellator.addVertexWithUV(x2, y1, z1, u2, v2);

		setLightValue(tessellator, world, block, x + 1, y, z, 0.6F);
		u1 = t0_u + 0.0078125F;
		v1 = t0_v + 0.0078125F;
		u2 = t0_u + 0.0546875F;
		v2 = t0_v + 0.0117188F;
		tessellator.addVertexWithUV(x1, y2, z1, u1, v1);
		tessellator.addVertexWithUV(x1, y1, z1, u1, v2);
		tessellator.addVertexWithUV(x1, y1, z2, u2, v2);
		tessellator.addVertexWithUV(x1, y2, z2, u2, v1);

		// Cube #2

		x1 = x + 0.0625F;
		y1 = y + 0.1250F;
		z1 = z + 0.0000F;
		x2 = x + 0.9375F;
		y2 = y + 1.0000F;
		z2 = z + 0.1250F;

		setLightValue(tessellator, world, block, x, y + 1, z, 1.0F);
		u1 = t0_u + 0.0039063F;
		v1 = t0_v + 0.0546875F;
		u2 = t0_u + 0.0585938F;
		v2 = t0_v + 0.0625000F;
		tessellator.addVertexWithUV(x2, y2, z2, u1, v1);
		tessellator.addVertexWithUV(x2, y2, z1, u1, v2);
		tessellator.addVertexWithUV(x1, y2, z1, u2, v2);
		tessellator.addVertexWithUV(x1, y2, z2, u2, v1);

		setLightValue(tessellator, world, block, x, y - 1, z, 0.5F);
		u1 = t0_u + 0.0039063F;
		v1 = t0_v + 0.0000000F;
		u2 = t0_u + 0.0585938F;
		v2 = t0_v + 0.0078125F;
		tessellator.addVertexWithUV(x1, y1, z2, u2, v1);
		tessellator.addVertexWithUV(x1, y1, z1, u2, v2);
		tessellator.addVertexWithUV(x2, y1, z1, u1, v2);
		tessellator.addVertexWithUV(x2, y1, z2, u1, v1);

		setLightValue(tessellator, world, block, x, y, z - 1, 0.8F);
		u1 = t1_u + 0.0039063F;
		v1 = t1_v + 0.0000000F;
		u2 = t1_u + 0.0585938F;
		v2 = t1_v + 0.0546875F;
		tessellator.addVertexWithUV(x1, y2, z1, u2, v1);
		tessellator.addVertexWithUV(x2, y2, z1, u1, v1);
		tessellator.addVertexWithUV(x2, y1, z1, u1, v2);
		tessellator.addVertexWithUV(x1, y1, z1, u2, v2);

		setLightValue(tessellator, world, block, x, y, z + 1, 0.8F);
		u1 = t0_u + 0.0039063F;
		v1 = t0_v + 0.0039063F;
		u2 = t0_u + 0.0585938F;
		v2 = t0_v + 0.0585938F;
		tessellator.addVertexWithUV(x1, y2, z2, u1, v1);
		tessellator.addVertexWithUV(x1, y1, z2, u1, v2);
		tessellator.addVertexWithUV(x2, y1, z2, u2, v2);
		tessellator.addVertexWithUV(x2, y2, z2, u2, v1);

		setLightValue(tessellator, world, block, x - 1, y, z, 0.6F);
		u1 = t1_u + 0.0000000F;
		v1 = t1_v + 0.0000000F;
		u2 = t1_u + 0.0078125F;
		v2 = t1_v + 0.0546875F;
		tessellator.addVertexWithUV(x2, y2, z1, u2, v1);
		tessellator.addVertexWithUV(x2, y2, z2, u1, v1);
		tessellator.addVertexWithUV(x2, y1, z2, u1, v2);
		tessellator.addVertexWithUV(x2, y1, z1, u2, v2);

		setLightValue(tessellator, world, block, x + 1, y, z, 0.6F);
		u1 = t1_u + 0.0546875F;
		v1 = t1_v + 0.0000000F;
		u2 = t1_u + 0.0625000F;
		v2 = t1_v + 0.0546875F;
		tessellator.addVertexWithUV(x1, y2, z1, u1, v1);
		tessellator.addVertexWithUV(x1, y1, z1, u1, v2);
		tessellator.addVertexWithUV(x1, y1, z2, u2, v2);
		tessellator.addVertexWithUV(x1, y2, z2, u2, v1);

		// Cube #3

		x1 = x + 0.0625F;
		y1 = y + 0.1250F;
		z1 = z + 0.8750F;
		x2 = x + 0.9375F;
		y2 = y + 1.0000F;
		z2 = z + 1.0000F;

		setLightValue(tessellator, world, block, x, y + 1, z, 1.0F);
		u1 = t0_u + 0.0039063F;
		v1 = t0_v + 0.0546875F;
		u2 = t0_u + 0.0585938F;
		v2 = t0_v + 0.0625000F;
		tessellator.addVertexWithUV(x2, y2, z2, u1, v1);
		tessellator.addVertexWithUV(x2, y2, z1, u1, v2);
		tessellator.addVertexWithUV(x1, y2, z1, u2, v2);
		tessellator.addVertexWithUV(x1, y2, z2, u2, v1);

		setLightValue(tessellator, world, block, x, y - 1, z, 0.5F);
		u1 = t0_u + 0.0039063F;
		v1 = t0_v + 0.0546875F;
		u2 = t0_u + 0.0585938F;
		v2 = t0_v + 0.0625000F;
		tessellator.addVertexWithUV(x1, y1, z2, u2, v1);
		tessellator.addVertexWithUV(x1, y1, z1, u2, v2);
		tessellator.addVertexWithUV(x2, y1, z1, u1, v2);
		tessellator.addVertexWithUV(x2, y1, z2, u1, v1);

		setLightValue(tessellator, world, block, x, y, z - 1, 0.8F);
		u1 = t0_u + 0.0039063F;
		v1 = t0_v + 0.0039063F;
		u2 = t0_u + 0.0585938F;
		v2 = t0_v + 0.0585938F;
		tessellator.addVertexWithUV(x1, y2, z1, u2, v1);
		tessellator.addVertexWithUV(x2, y2, z1, u1, v1);
		tessellator.addVertexWithUV(x2, y1, z1, u1, v2);
		tessellator.addVertexWithUV(x1, y1, z1, u2, v2);

		setLightValue(tessellator, world, block, x, y, z + 1, 0.8F);
		u1 = t1_u + 0.0585938F;
		v1 = t1_v + 0.0000000F;
		u2 = t1_u + 0.0039063F;
		v2 = t1_v + 0.0546875F;
		tessellator.addVertexWithUV(x1, y2, z2, u1, v1);
		tessellator.addVertexWithUV(x1, y1, z2, u1, v2);
		tessellator.addVertexWithUV(x2, y1, z2, u2, v2);
		tessellator.addVertexWithUV(x2, y2, z2, u2, v1);

		setLightValue(tessellator, world, block, x - 1, y, z, 0.6F);
		u1 = t1_u + 0.0078125F;
		v1 = t1_v + 0.0000000F;
		u2 = t1_u + 0.0000000F;
		v2 = t1_v + 0.0546875F;
		tessellator.addVertexWithUV(x2, y2, z1, u2, v1);
		tessellator.addVertexWithUV(x2, y2, z2, u1, v1);
		tessellator.addVertexWithUV(x2, y1, z2, u1, v2);
		tessellator.addVertexWithUV(x2, y1, z1, u2, v2);

		setLightValue(tessellator, world, block, x + 1, y, z, 0.6F);
		u1 = t1_u + 0.0625000F;
		v1 = t1_v + 0.0000000F;
		u2 = t1_u + 0.0546875F;
		v2 = t1_v + 0.0546875F;
		tessellator.addVertexWithUV(x1, y2, z1, u1, v1);
		tessellator.addVertexWithUV(x1, y1, z1, u1, v2);
		tessellator.addVertexWithUV(x1, y1, z2, u2, v2);
		tessellator.addVertexWithUV(x1, y2, z2, u2, v1);

		// Cube #4

		x1 = x + 0.0000F;
		y1 = y + 0.1250F;
		z1 = z + 0.0625F;
		x2 = x + 0.1250F;
		y2 = y + 1.0000F;
		z2 = z + 0.9375F;

		setLightValue(tessellator, world, block, x, y + 1, z, 1.0F);
		u1 = t0_u + 0.0000000F;
		v1 = t0_v + 0.0039063F;
		u2 = t0_u + 0.0078125F;
		v2 = t0_v + 0.0585938F;
		tessellator.addVertexWithUV(x2, y2, z2, u1, v1);
		tessellator.addVertexWithUV(x2, y2, z1, u1, v2);
		tessellator.addVertexWithUV(x1, y2, z1, u2, v2);
		tessellator.addVertexWithUV(x1, y2, z2, u2, v1);

		setLightValue(tessellator, world, block, x, y - 1, z, 0.5F);
		u1 = t0_u + 0.0000000F;
		v1 = t0_v + 0.0039063F;
		u2 = t0_u + 0.0078125F;
		v2 = t0_v + 0.0585938F;
		tessellator.addVertexWithUV(x1, y1, z2, u2, v1);
		tessellator.addVertexWithUV(x1, y1, z1, u2, v2);
		tessellator.addVertexWithUV(x2, y1, z1, u1, v2);
		tessellator.addVertexWithUV(x2, y1, z2, u1, v1);

		setLightValue(tessellator, world, block, x, y, z - 1, 0.8F);
		u1 = t2_u + 0.0546875F;
		v1 = t2_v + 0.0000000F;
		u2 = t2_u + 0.0625000F;
		v2 = t2_v + 0.0546875F;
		tessellator.addVertexWithUV(x1, y2, z1, u2, v1);
		tessellator.addVertexWithUV(x2, y2, z1, u1, v1);
		tessellator.addVertexWithUV(x2, y1, z1, u1, v2);
		tessellator.addVertexWithUV(x1, y1, z1, u2, v2);

		setLightValue(tessellator, world, block, x, y, z + 1, 0.8F);
		u1 = t2_u + 0.0000000F;
		v1 = t2_v + 0.0000000F;
		u2 = t2_u + 0.0078125F;
		v2 = t2_v + 0.0546875F;
		tessellator.addVertexWithUV(x1, y2, z2, u1, v1);
		tessellator.addVertexWithUV(x1, y1, z2, u1, v2);
		tessellator.addVertexWithUV(x2, y1, z2, u2, v2);
		tessellator.addVertexWithUV(x2, y2, z2, u2, v1);

		setLightValue(tessellator, world, block, x - 1, y, z, 0.6F);
		u1 = t0_u + 0.0039063F;
		v1 = t0_v + 0.0039063F;
		u2 = t0_u + 0.0585938F;
		v2 = t0_v + 0.0585938F;
		tessellator.addVertexWithUV(x2, y2, z1, u2, v1);
		tessellator.addVertexWithUV(x2, y2, z2, u1, v1);
		tessellator.addVertexWithUV(x2, y1, z2, u1, v2);
		tessellator.addVertexWithUV(x2, y1, z1, u2, v2);

		setLightValue(tessellator, world, block, x + 1, y, z, 0.6F);
		u1 = t2_u + 0.0039063F;
		v1 = t2_v + 0.0000000F;
		u2 = t2_u + 0.0585938F;
		v2 = t2_v + 0.0546875F;
		tessellator.addVertexWithUV(x1, y2, z1, u1, v1);
		tessellator.addVertexWithUV(x1, y1, z1, u1, v2);
		tessellator.addVertexWithUV(x1, y1, z2, u2, v2);
		tessellator.addVertexWithUV(x1, y2, z2, u2, v1);

		// Cube #5

		x1 = x + 0.8750F;
		y1 = y + 0.1250F;
		z1 = z + 0.0625F;
		x2 = x + 1.0000F;
		y2 = y + 1.0000F;
		z2 = z + 0.9375F;

		setLightValue(tessellator, world, block, x, y + 1, z, 1.0F);
		u1 = t0_u + 0.0546875F;
		v1 = t0_v + 0.0039063F;
		u2 = t0_u + 0.0625000F;
		v2 = t0_v + 0.0585938F;
		tessellator.addVertexWithUV(x2, y2, z2, u1, v1);
		tessellator.addVertexWithUV(x2, y2, z1, u1, v2);
		tessellator.addVertexWithUV(x1, y2, z1, u2, v2);
		tessellator.addVertexWithUV(x1, y2, z2, u2, v1);

		setLightValue(tessellator, world, block, x, y - 1, z, 0.5F);
		u1 = t0_u + 0.0546875F;
		v1 = t0_v + 0.0039063F;
		u2 = t0_u + 0.0625000F;
		v2 = t0_v + 0.0585938F;
		tessellator.addVertexWithUV(x1, y1, z2, u2, v1);
		tessellator.addVertexWithUV(x1, y1, z1, u2, v2);
		tessellator.addVertexWithUV(x2, y1, z1, u1, v2);
		tessellator.addVertexWithUV(x2, y1, z2, u1, v1);

		setLightValue(tessellator, world, block, x, y, z - 1, 0.8F);
		u1 = t3_u + 0.0546875F;
		v1 = t3_v + 0.0000000F;
		u2 = t3_u + 0.0625000F;
		v2 = t3_v + 0.0546875F;
		tessellator.addVertexWithUV(x1, y2, z1, u2, v1);
		tessellator.addVertexWithUV(x2, y2, z1, u1, v1);
		tessellator.addVertexWithUV(x2, y1, z1, u1, v2);
		tessellator.addVertexWithUV(x1, y1, z1, u2, v2);

		setLightValue(tessellator, world, block, x, y, z + 1, 0.8F);
		u1 = t3_u + 0.0000000F;
		v1 = t3_v + 0.0000000F;
		u2 = t3_u + 0.0078125F;
		v2 = t3_v + 0.0546875F;
		tessellator.addVertexWithUV(x1, y2, z2, u1, v1);
		tessellator.addVertexWithUV(x1, y1, z2, u1, v2);
		tessellator.addVertexWithUV(x2, y1, z2, u2, v2);
		tessellator.addVertexWithUV(x2, y2, z2, u2, v1);

		setLightValue(tessellator, world, block, x - 1, y, z, 0.6F);
		u1 = t3_u + 0.0039063F;
		v1 = t3_v + 0.0000000F;
		u2 = t3_u + 0.0585938F;
		v2 = t3_v + 0.0546875F;
		tessellator.addVertexWithUV(x2, y2, z1, u2, v1);
		tessellator.addVertexWithUV(x2, y2, z2, u1, v1);
		tessellator.addVertexWithUV(x2, y1, z2, u1, v2);
		tessellator.addVertexWithUV(x2, y1, z1, u2, v2);

		setLightValue(tessellator, world, block, x + 1, y, z, 0.6F);
		u1 = t0_u + 0.0039063F;
		v1 = t0_v + 0.0039063F;
		u2 = t0_u + 0.0585938F;
		v2 = t0_v + 0.0585938F;
		tessellator.addVertexWithUV(x1, y2, z1, u1, v1);
		tessellator.addVertexWithUV(x1, y1, z1, u1, v2);
		tessellator.addVertexWithUV(x1, y1, z2, u2, v2);
		tessellator.addVertexWithUV(x1, y2, z2, u2, v1);

		// Cube #6

		x1 = x + 0.1250F;
		y1 = y + 0.1250F;
		z1 = z + 0.1250F;
		x2 = x + 0.8750F;
		y2 = y + 0.8125F;
		z2 = z + 0.8750F;

		setLightValue(tessellator, world, block, x, y + 1, z, 1.0F);
		u1 = t4_u + 0.0078125F;
		v1 = t4_v + 0.0078125F;
		u2 = t4_u + 0.0546875F;
		v2 = t4_v + 0.0546875F;
		tessellator.addVertexWithUV(x2, y2, z2, u1, v1);
		tessellator.addVertexWithUV(x2, y2, z1, u1, v2);
		tessellator.addVertexWithUV(x1, y2, z1, u2, v2);
		tessellator.addVertexWithUV(x1, y2, z2, u2, v1);

		setLightValue(tessellator, world, block, x, y - 1, z, 0.5F);
		u1 = t4_u + 0.0078125F;
		v1 = t4_v + 0.0078125F;
		u2 = t4_u + 0.0546875F;
		v2 = t4_v + 0.0546875F;
		tessellator.addVertexWithUV(x1, y1, z2, u2, v1);
		tessellator.addVertexWithUV(x1, y1, z1, u2, v2);
		tessellator.addVertexWithUV(x2, y1, z1, u1, v2);
		tessellator.addVertexWithUV(x2, y1, z2, u1, v1);

		setLightValue(tessellator, world, block, x, y, z - 1, 0.8F);
		u1 = t4_u + 0.0078125F;
		v1 = t4_v + 0.0078125F;
		u2 = t4_u + 0.0546875F;
		v2 = t4_v + 0.0507813F;
		tessellator.addVertexWithUV(x1, y2, z1, u2, v1);
		tessellator.addVertexWithUV(x2, y2, z1, u1, v1);
		tessellator.addVertexWithUV(x2, y1, z1, u1, v2);
		tessellator.addVertexWithUV(x1, y1, z1, u2, v2);

		setLightValue(tessellator, world, block, x, y, z + 1, 0.8F);
		u1 = t4_u + 0.0078125F;
		v1 = t4_v + 0.0078125F;
		u2 = t4_u + 0.0507813F;
		v2 = t4_v + 0.0546875F;
		tessellator.addVertexWithUV(x1, y2, z2, u1, v1);
		tessellator.addVertexWithUV(x1, y1, z2, u1, v2);
		tessellator.addVertexWithUV(x2, y1, z2, u2, v2);
		tessellator.addVertexWithUV(x2, y2, z2, u2, v1);

		setLightValue(tessellator, world, block, x - 1, y, z, 0.6F);
		u1 = t4_u + 0.0078125F;
		v1 = t4_v + 0.0078125F;
		u2 = t4_u + 0.0546875F;
		v2 = t4_v + 0.0507813F;
		tessellator.addVertexWithUV(x2, y2, z1, u2, v1);
		tessellator.addVertexWithUV(x2, y2, z2, u1, v1);
		tessellator.addVertexWithUV(x2, y1, z2, u1, v2);
		tessellator.addVertexWithUV(x2, y1, z1, u2, v2);

		setLightValue(tessellator, world, block, x + 1, y, z, 0.6F);
		u1 = t4_u + 0.0078125F;
		v1 = t4_v + 0.0078125F;
		u2 = t4_u + 0.0546875F;
		v2 = t4_v + 0.0507813F;
		tessellator.addVertexWithUV(x1, y2, z1, u1, v1);
		tessellator.addVertexWithUV(x1, y1, z1, u1, v2);
		tessellator.addVertexWithUV(x1, y1, z2, u2, v2);
		tessellator.addVertexWithUV(x1, y2, z2, u2, v1);

		return true;
	}

	public static boolean renderMeta3 (World world, Block block, float x, float y, float z, int ti_0, int ti_1, int ti_2, int ti_3, int ti_4) {
		Tessellator tessellator = Tessellator.instance;
		float x1, y1, z1, x2, y2, z2;
		float u1, v1, u2, v2;

		// Texture #0
		float t0_u = (float) ((ti_0 & 0x0f) << 4) / 256.0F;
		float t0_v = (float) (ti_0 & 0xff0) / 256.0F;

		// Texture #1
		float t1_u = (float) ((ti_1 & 0x0f) << 4) / 256.0F;
		float t1_v = (float) (ti_1 & 0xff0) / 256.0F;

		// Texture #2
		float t2_u = (float) ((ti_2 & 0x0f) << 4) / 256.0F;
		float t2_v = (float) (ti_2 & 0xff0) / 256.0F;

		// Texture #3
		float t3_u = (float) ((ti_3 & 0x0f) << 4) / 256.0F;
		float t3_v = (float) (ti_3 & 0xff0) / 256.0F;

		// Texture #4
		float t4_u = (float) ((ti_4 & 0x0f) << 4) / 256.0F;
		float t4_v = (float) (ti_4 & 0xff0) / 256.0F;

		// Cube #0

		x1 = x + 0.8750F;
		y1 = y + 0.0000F;
		z1 = z + 0.8750F;
		x2 = x + 0.1250F;
		y2 = y + 0.0625F;
		z2 = z + 0.1250F;

		setLightValue(tessellator, world, block, x, y + 1, z, 1.0F);
		u1 = t0_u + 0.0078125F;
		v1 = t0_v + 0.0078125F;
		u2 = t0_u + 0.0546875F;
		v2 = t0_v + 0.0546875F;
		tessellator.addVertexWithUV(x2, y2, z2, u1, v1);
		tessellator.addVertexWithUV(x2, y2, z1, u1, v2);
		tessellator.addVertexWithUV(x1, y2, z1, u2, v2);
		tessellator.addVertexWithUV(x1, y2, z2, u2, v1);

		setLightValue(tessellator, world, block, x, y - 1, z, 0.5F);
		u1 = t0_u + 0.0078125F;
		v1 = t0_v + 0.0078125F;
		u2 = t0_u + 0.0546875F;
		v2 = t0_v + 0.0546875F;
		tessellator.addVertexWithUV(x1, y1, z2, u2, v1);
		tessellator.addVertexWithUV(x1, y1, z1, u2, v2);
		tessellator.addVertexWithUV(x2, y1, z1, u1, v2);
		tessellator.addVertexWithUV(x2, y1, z2, u1, v1);

		setLightValue(tessellator, world, block, x, y, z + 1, 0.8F);
		u1 = t0_u + 0.0078125F;
		v1 = t0_v + 0.0078125F;
		u2 = t0_u + 0.0546875F;
		v2 = t0_v + 0.0117188F;
		tessellator.addVertexWithUV(x1, y2, z1, u2, v1);
		tessellator.addVertexWithUV(x2, y2, z1, u1, v1);
		tessellator.addVertexWithUV(x2, y1, z1, u1, v2);
		tessellator.addVertexWithUV(x1, y1, z1, u2, v2);

		setLightValue(tessellator, world, block, x, y, z - 1, 0.8F);
		u1 = t0_u + 0.0078125F;
		v1 = t0_v + 0.0078125F;
		u2 = t0_u + 0.0546875F;
		v2 = t0_v + 0.0117188F;
		tessellator.addVertexWithUV(x1, y2, z2, u1, v1);
		tessellator.addVertexWithUV(x1, y1, z2, u1, v2);
		tessellator.addVertexWithUV(x2, y1, z2, u2, v2);
		tessellator.addVertexWithUV(x2, y2, z2, u2, v1);

		setLightValue(tessellator, world, block, x - 1, y, z, 0.6F);
		u1 = t0_u + 0.0078125F;
		v1 = t0_v + 0.0078125F;
		u2 = t0_u + 0.0546875F;
		v2 = t0_v + 0.0117188F;
		tessellator.addVertexWithUV(x2, y2, z1, u2, v1);
		tessellator.addVertexWithUV(x2, y2, z2, u1, v1);
		tessellator.addVertexWithUV(x2, y1, z2, u1, v2);
		tessellator.addVertexWithUV(x2, y1, z1, u2, v2);

		setLightValue(tessellator, world, block, x + 1, y, z, 0.6F);
		u1 = t0_u + 0.0078125F;
		v1 = t0_v + 0.0078125F;
		u2 = t0_u + 0.0546875F;
		v2 = t0_v + 0.0117188F;
		tessellator.addVertexWithUV(x1, y2, z1, u1, v1);
		tessellator.addVertexWithUV(x1, y1, z1, u1, v2);
		tessellator.addVertexWithUV(x1, y1, z2, u2, v2);
		tessellator.addVertexWithUV(x1, y2, z2, u2, v1);

		// Cube #1

		x1 = x + 0.9375F;
		y1 = y + 0.0625F;
		z1 = z + 0.9375F;
		x2 = x + 0.0625F;
		y2 = y + 0.1250F;
		z2 = z + 0.0625F;

		setLightValue(tessellator, world, block, x, y + 1, z, 1.0F);
		u1 = t0_u + 0.0078125F;
		v1 = t0_v + 0.0078125F;
		u2 = t0_u + 0.0546875F;
		v2 = t0_v + 0.0546875F;
		tessellator.addVertexWithUV(x2, y2, z2, u1, v1);
		tessellator.addVertexWithUV(x2, y2, z1, u1, v2);
		tessellator.addVertexWithUV(x1, y2, z1, u2, v2);
		tessellator.addVertexWithUV(x1, y2, z2, u2, v1);

		setLightValue(tessellator, world, block, x, y - 1, z, 0.5F);
		u1 = t0_u + 0.0078125F;
		v1 = t0_v + 0.0078125F;
		u2 = t0_u + 0.0546875F;
		v2 = t0_v + 0.0546875F;
		tessellator.addVertexWithUV(x1, y1, z2, u2, v1);
		tessellator.addVertexWithUV(x1, y1, z1, u2, v2);
		tessellator.addVertexWithUV(x2, y1, z1, u1, v2);
		tessellator.addVertexWithUV(x2, y1, z2, u1, v1);

		setLightValue(tessellator, world, block, x, y, z + 1, 0.8F);
		u1 = t0_u + 0.0078125F;
		v1 = t0_v + 0.0078125F;
		u2 = t0_u + 0.0546875F;
		v2 = t0_v + 0.0117188F;
		tessellator.addVertexWithUV(x1, y2, z1, u2, v1);
		tessellator.addVertexWithUV(x2, y2, z1, u1, v1);
		tessellator.addVertexWithUV(x2, y1, z1, u1, v2);
		tessellator.addVertexWithUV(x1, y1, z1, u2, v2);

		setLightValue(tessellator, world, block, x, y, z - 1, 0.8F);
		u1 = t0_u + 0.0078125F;
		v1 = t0_v + 0.0078125F;
		u2 = t0_u + 0.0546875F;
		v2 = t0_v + 0.0117188F;
		tessellator.addVertexWithUV(x1, y2, z2, u1, v1);
		tessellator.addVertexWithUV(x1, y1, z2, u1, v2);
		tessellator.addVertexWithUV(x2, y1, z2, u2, v2);
		tessellator.addVertexWithUV(x2, y2, z2, u2, v1);

		setLightValue(tessellator, world, block, x - 1, y, z, 0.6F);
		u1 = t0_u + 0.0078125F;
		v1 = t0_v + 0.0078125F;
		u2 = t0_u + 0.0546875F;
		v2 = t0_v + 0.0117188F;
		tessellator.addVertexWithUV(x2, y2, z1, u2, v1);
		tessellator.addVertexWithUV(x2, y2, z2, u1, v1);
		tessellator.addVertexWithUV(x2, y1, z2, u1, v2);
		tessellator.addVertexWithUV(x2, y1, z1, u2, v2);

		setLightValue(tessellator, world, block, x + 1, y, z, 0.6F);
		u1 = t0_u + 0.0078125F;
		v1 = t0_v + 0.0078125F;
		u2 = t0_u + 0.0546875F;
		v2 = t0_v + 0.0117188F;
		tessellator.addVertexWithUV(x1, y2, z1, u1, v1);
		tessellator.addVertexWithUV(x1, y1, z1, u1, v2);
		tessellator.addVertexWithUV(x1, y1, z2, u2, v2);
		tessellator.addVertexWithUV(x1, y2, z2, u2, v1);

		// Cube #2

		x1 = x + 0.9375F;
		y1 = y + 0.1250F;
		z1 = z + 1.0000F;
		x2 = x + 0.0625F;
		y2 = y + 1.0000F;
		z2 = z + 0.8750F;

		setLightValue(tessellator, world, block, x, y + 1, z, 1.0F);
		u1 = t0_u + 0.0039063F;
		v1 = t0_v + 0.0546875F;
		u2 = t0_u + 0.0585938F;
		v2 = t0_v + 0.0625000F;
		tessellator.addVertexWithUV(x2, y2, z2, u1, v1);
		tessellator.addVertexWithUV(x2, y2, z1, u1, v2);
		tessellator.addVertexWithUV(x1, y2, z1, u2, v2);
		tessellator.addVertexWithUV(x1, y2, z2, u2, v1);

		setLightValue(tessellator, world, block, x, y - 1, z, 0.5F);
		u1 = t0_u + 0.0039063F;
		v1 = t0_v + 0.0000000F;
		u2 = t0_u + 0.0585938F;
		v2 = t0_v + 0.0078125F;
		tessellator.addVertexWithUV(x1, y1, z2, u2, v1);
		tessellator.addVertexWithUV(x1, y1, z1, u2, v2);
		tessellator.addVertexWithUV(x2, y1, z1, u1, v2);
		tessellator.addVertexWithUV(x2, y1, z2, u1, v1);

		setLightValue(tessellator, world, block, x, y, z + 1, 0.8F);
		u1 = t1_u + 0.0039063F;
		v1 = t1_v + 0.0000000F;
		u2 = t1_u + 0.0585938F;
		v2 = t1_v + 0.0546875F;
		tessellator.addVertexWithUV(x1, y2, z1, u2, v1);
		tessellator.addVertexWithUV(x2, y2, z1, u1, v1);
		tessellator.addVertexWithUV(x2, y1, z1, u1, v2);
		tessellator.addVertexWithUV(x1, y1, z1, u2, v2);

		setLightValue(tessellator, world, block, x, y, z - 1, 0.8F);
		u1 = t0_u + 0.0039063F;
		v1 = t0_v + 0.0039063F;
		u2 = t0_u + 0.0585938F;
		v2 = t0_v + 0.0585938F;
		tessellator.addVertexWithUV(x1, y2, z2, u1, v1);
		tessellator.addVertexWithUV(x1, y1, z2, u1, v2);
		tessellator.addVertexWithUV(x2, y1, z2, u2, v2);
		tessellator.addVertexWithUV(x2, y2, z2, u2, v1);

		setLightValue(tessellator, world, block, x - 1, y, z, 0.6F);
		u1 = t1_u + 0.0000000F;
		v1 = t1_v + 0.0000000F;
		u2 = t1_u + 0.0078125F;
		v2 = t1_v + 0.0546875F;
		tessellator.addVertexWithUV(x2, y2, z1, u2, v1);
		tessellator.addVertexWithUV(x2, y2, z2, u1, v1);
		tessellator.addVertexWithUV(x2, y1, z2, u1, v2);
		tessellator.addVertexWithUV(x2, y1, z1, u2, v2);

		setLightValue(tessellator, world, block, x + 1, y, z, 0.6F);
		u1 = t1_u + 0.0546875F;
		v1 = t1_v + 0.0000000F;
		u2 = t1_u + 0.0625000F;
		v2 = t1_v + 0.0546875F;
		tessellator.addVertexWithUV(x1, y2, z1, u1, v1);
		tessellator.addVertexWithUV(x1, y1, z1, u1, v2);
		tessellator.addVertexWithUV(x1, y1, z2, u2, v2);
		tessellator.addVertexWithUV(x1, y2, z2, u2, v1);

		// Cube #3

		x1 = x + 0.9375F;
		y1 = y + 0.1250F;
		z1 = z + 0.1250F;
		x2 = x + 0.0625F;
		y2 = y + 1.0000F;
		z2 = z + 0.0000F;

		setLightValue(tessellator, world, block, x, y + 1, z, 1.0F);
		u1 = t0_u + 0.0039063F;
		v1 = t0_v + 0.0546875F;
		u2 = t0_u + 0.0585938F;
		v2 = t0_v + 0.0625000F;
		tessellator.addVertexWithUV(x2, y2, z2, u1, v1);
		tessellator.addVertexWithUV(x2, y2, z1, u1, v2);
		tessellator.addVertexWithUV(x1, y2, z1, u2, v2);
		tessellator.addVertexWithUV(x1, y2, z2, u2, v1);

		setLightValue(tessellator, world, block, x, y - 1, z, 0.5F);
		u1 = t0_u + 0.0039063F;
		v1 = t0_v + 0.0546875F;
		u2 = t0_u + 0.0585938F;
		v2 = t0_v + 0.0625000F;
		tessellator.addVertexWithUV(x1, y1, z2, u2, v1);
		tessellator.addVertexWithUV(x1, y1, z1, u2, v2);
		tessellator.addVertexWithUV(x2, y1, z1, u1, v2);
		tessellator.addVertexWithUV(x2, y1, z2, u1, v1);

		setLightValue(tessellator, world, block, x, y, z + 1, 0.8F);
		u1 = t0_u + 0.0039063F;
		v1 = t0_v + 0.0039063F;
		u2 = t0_u + 0.0585938F;
		v2 = t0_v + 0.0585938F;
		tessellator.addVertexWithUV(x1, y2, z1, u2, v1);
		tessellator.addVertexWithUV(x2, y2, z1, u1, v1);
		tessellator.addVertexWithUV(x2, y1, z1, u1, v2);
		tessellator.addVertexWithUV(x1, y1, z1, u2, v2);

		setLightValue(tessellator, world, block, x, y, z - 1, 0.8F);
		u1 = t1_u + 0.0585938F;
		v1 = t1_v + 0.0000000F;
		u2 = t1_u + 0.0039063F;
		v2 = t1_v + 0.0546875F;
		tessellator.addVertexWithUV(x1, y2, z2, u1, v1);
		tessellator.addVertexWithUV(x1, y1, z2, u1, v2);
		tessellator.addVertexWithUV(x2, y1, z2, u2, v2);
		tessellator.addVertexWithUV(x2, y2, z2, u2, v1);

		setLightValue(tessellator, world, block, x - 1, y, z, 0.6F);
		u1 = t1_u + 0.0078125F;
		v1 = t1_v + 0.0000000F;
		u2 = t1_u + 0.0000000F;
		v2 = t1_v + 0.0546875F;
		tessellator.addVertexWithUV(x2, y2, z1, u2, v1);
		tessellator.addVertexWithUV(x2, y2, z2, u1, v1);
		tessellator.addVertexWithUV(x2, y1, z2, u1, v2);
		tessellator.addVertexWithUV(x2, y1, z1, u2, v2);

		setLightValue(tessellator, world, block, x + 1, y, z, 0.6F);
		u1 = t1_u + 0.0625000F;
		v1 = t1_v + 0.0000000F;
		u2 = t1_u + 0.0546875F;
		v2 = t1_v + 0.0546875F;
		tessellator.addVertexWithUV(x1, y2, z1, u1, v1);
		tessellator.addVertexWithUV(x1, y1, z1, u1, v2);
		tessellator.addVertexWithUV(x1, y1, z2, u2, v2);
		tessellator.addVertexWithUV(x1, y2, z2, u2, v1);

		// Cube #4

		x1 = x + 1.0000F;
		y1 = y + 0.1250F;
		z1 = z + 0.9375F;
		x2 = x + 0.8750F;
		y2 = y + 1.0000F;
		z2 = z + 0.0625F;

		setLightValue(tessellator, world, block, x, y + 1, z, 1.0F);
		u1 = t0_u + 0.0000000F;
		v1 = t0_v + 0.0039063F;
		u2 = t0_u + 0.0078125F;
		v2 = t0_v + 0.0585938F;
		tessellator.addVertexWithUV(x2, y2, z2, u1, v1);
		tessellator.addVertexWithUV(x2, y2, z1, u1, v2);
		tessellator.addVertexWithUV(x1, y2, z1, u2, v2);
		tessellator.addVertexWithUV(x1, y2, z2, u2, v1);

		setLightValue(tessellator, world, block, x, y - 1, z, 0.5F);
		u1 = t0_u + 0.0000000F;
		v1 = t0_v + 0.0039063F;
		u2 = t0_u + 0.0078125F;
		v2 = t0_v + 0.0585938F;
		tessellator.addVertexWithUV(x1, y1, z2, u2, v1);
		tessellator.addVertexWithUV(x1, y1, z1, u2, v2);
		tessellator.addVertexWithUV(x2, y1, z1, u1, v2);
		tessellator.addVertexWithUV(x2, y1, z2, u1, v1);

		setLightValue(tessellator, world, block, x, y, z + 1, 0.8F);
		u1 = t2_u + 0.0546875F;
		v1 = t2_v + 0.0000000F;
		u2 = t2_u + 0.0625000F;
		v2 = t2_v + 0.0546875F;
		tessellator.addVertexWithUV(x1, y2, z1, u2, v1);
		tessellator.addVertexWithUV(x2, y2, z1, u1, v1);
		tessellator.addVertexWithUV(x2, y1, z1, u1, v2);
		tessellator.addVertexWithUV(x1, y1, z1, u2, v2);

		setLightValue(tessellator, world, block, x, y, z - 1, 0.8F);
		u1 = t2_u + 0.0000000F;
		v1 = t2_v + 0.0000000F;
		u2 = t2_u + 0.0078125F;
		v2 = t2_v + 0.0546875F;
		tessellator.addVertexWithUV(x1, y2, z2, u1, v1);
		tessellator.addVertexWithUV(x1, y1, z2, u1, v2);
		tessellator.addVertexWithUV(x2, y1, z2, u2, v2);
		tessellator.addVertexWithUV(x2, y2, z2, u2, v1);

		setLightValue(tessellator, world, block, x - 1, y, z, 0.6F);
		u1 = t0_u + 0.0039063F;
		v1 = t0_v + 0.0039063F;
		u2 = t0_u + 0.0585938F;
		v2 = t0_v + 0.0585938F;
		tessellator.addVertexWithUV(x2, y2, z1, u2, v1);
		tessellator.addVertexWithUV(x2, y2, z2, u1, v1);
		tessellator.addVertexWithUV(x2, y1, z2, u1, v2);
		tessellator.addVertexWithUV(x2, y1, z1, u2, v2);

		setLightValue(tessellator, world, block, x + 1, y, z, 0.6F);
		u1 = t2_u + 0.0039063F;
		v1 = t2_v + 0.0000000F;
		u2 = t2_u + 0.0585938F;
		v2 = t2_v + 0.0546875F;
		tessellator.addVertexWithUV(x1, y2, z1, u1, v1);
		tessellator.addVertexWithUV(x1, y1, z1, u1, v2);
		tessellator.addVertexWithUV(x1, y1, z2, u2, v2);
		tessellator.addVertexWithUV(x1, y2, z2, u2, v1);

		// Cube #5

		x1 = x + 0.1250F;
		y1 = y + 0.1250F;
		z1 = z + 0.9375F;
		x2 = x + 0.0000F;
		y2 = y + 1.0000F;
		z2 = z + 0.0625F;

		setLightValue(tessellator, world, block, x, y + 1, z, 1.0F);
		u1 = t0_u + 0.0546875F;
		v1 = t0_v + 0.0039063F;
		u2 = t0_u + 0.0625000F;
		v2 = t0_v + 0.0585938F;
		tessellator.addVertexWithUV(x2, y2, z2, u1, v1);
		tessellator.addVertexWithUV(x2, y2, z1, u1, v2);
		tessellator.addVertexWithUV(x1, y2, z1, u2, v2);
		tessellator.addVertexWithUV(x1, y2, z2, u2, v1);

		setLightValue(tessellator, world, block, x, y - 1, z, 0.5F);
		u1 = t0_u + 0.0546875F;
		v1 = t0_v + 0.0039063F;
		u2 = t0_u + 0.0625000F;
		v2 = t0_v + 0.0585938F;
		tessellator.addVertexWithUV(x1, y1, z2, u2, v1);
		tessellator.addVertexWithUV(x1, y1, z1, u2, v2);
		tessellator.addVertexWithUV(x2, y1, z1, u1, v2);
		tessellator.addVertexWithUV(x2, y1, z2, u1, v1);

		setLightValue(tessellator, world, block, x, y, z + 1, 0.8F);
		u1 = t3_u + 0.0546875F;
		v1 = t3_v + 0.0000000F;
		u2 = t3_u + 0.0625000F;
		v2 = t3_v + 0.0546875F;
		tessellator.addVertexWithUV(x1, y2, z1, u2, v1);
		tessellator.addVertexWithUV(x2, y2, z1, u1, v1);
		tessellator.addVertexWithUV(x2, y1, z1, u1, v2);
		tessellator.addVertexWithUV(x1, y1, z1, u2, v2);

		setLightValue(tessellator, world, block, x, y, z - 1, 0.8F);
		u1 = t3_u + 0.0000000F;
		v1 = t3_v + 0.0000000F;
		u2 = t3_u + 0.0078125F;
		v2 = t3_v + 0.0546875F;
		tessellator.addVertexWithUV(x1, y2, z2, u1, v1);
		tessellator.addVertexWithUV(x1, y1, z2, u1, v2);
		tessellator.addVertexWithUV(x2, y1, z2, u2, v2);
		tessellator.addVertexWithUV(x2, y2, z2, u2, v1);

		setLightValue(tessellator, world, block, x - 1, y, z, 0.6F);
		u1 = t3_u + 0.0039063F;
		v1 = t3_v + 0.0000000F;
		u2 = t3_u + 0.0585938F;
		v2 = t3_v + 0.0546875F;
		tessellator.addVertexWithUV(x2, y2, z1, u2, v1);
		tessellator.addVertexWithUV(x2, y2, z2, u1, v1);
		tessellator.addVertexWithUV(x2, y1, z2, u1, v2);
		tessellator.addVertexWithUV(x2, y1, z1, u2, v2);

		setLightValue(tessellator, world, block, x + 1, y, z, 0.6F);
		u1 = t0_u + 0.0039063F;
		v1 = t0_v + 0.0039063F;
		u2 = t0_u + 0.0585938F;
		v2 = t0_v + 0.0585938F;
		tessellator.addVertexWithUV(x1, y2, z1, u1, v1);
		tessellator.addVertexWithUV(x1, y1, z1, u1, v2);
		tessellator.addVertexWithUV(x1, y1, z2, u2, v2);
		tessellator.addVertexWithUV(x1, y2, z2, u2, v1);

		// Cube #6

		x1 = x + 0.8750F;
		y1 = y + 0.1250F;
		z1 = z + 0.8750F;
		x2 = x + 0.1250F;
		y2 = y + 0.8125F;
		z2 = z + 0.1250F;

		setLightValue(tessellator, world, block, x, y + 1, z, 1.0F);
		u1 = t4_u + 0.0078125F;
		v1 = t4_v + 0.0078125F;
		u2 = t4_u + 0.0546875F;
		v2 = t4_v + 0.0546875F;
		tessellator.addVertexWithUV(x2, y2, z2, u1, v1);
		tessellator.addVertexWithUV(x2, y2, z1, u1, v2);
		tessellator.addVertexWithUV(x1, y2, z1, u2, v2);
		tessellator.addVertexWithUV(x1, y2, z2, u2, v1);

		setLightValue(tessellator, world, block, x, y - 1, z, 0.5F);
		u1 = t4_u + 0.0078125F;
		v1 = t4_v + 0.0078125F;
		u2 = t4_u + 0.0546875F;
		v2 = t4_v + 0.0546875F;
		tessellator.addVertexWithUV(x1, y1, z2, u2, v1);
		tessellator.addVertexWithUV(x1, y1, z1, u2, v2);
		tessellator.addVertexWithUV(x2, y1, z1, u1, v2);
		tessellator.addVertexWithUV(x2, y1, z2, u1, v1);

		setLightValue(tessellator, world, block, x, y, z + 1, 0.8F);
		u1 = t4_u + 0.0078125F;
		v1 = t4_v + 0.0078125F;
		u2 = t4_u + 0.0546875F;
		v2 = t4_v + 0.0507813F;
		tessellator.addVertexWithUV(x1, y2, z1, u2, v1);
		tessellator.addVertexWithUV(x2, y2, z1, u1, v1);
		tessellator.addVertexWithUV(x2, y1, z1, u1, v2);
		tessellator.addVertexWithUV(x1, y1, z1, u2, v2);

		setLightValue(tessellator, world, block, x, y, z - 1, 0.8F);
		u1 = t4_u + 0.0078125F;
		v1 = t4_v + 0.0078125F;
		u2 = t4_u + 0.0507813F;
		v2 = t4_v + 0.0546875F;
		tessellator.addVertexWithUV(x1, y2, z2, u1, v1);
		tessellator.addVertexWithUV(x1, y1, z2, u1, v2);
		tessellator.addVertexWithUV(x2, y1, z2, u2, v2);
		tessellator.addVertexWithUV(x2, y2, z2, u2, v1);

		setLightValue(tessellator, world, block, x - 1, y, z, 0.6F);
		u1 = t4_u + 0.0078125F;
		v1 = t4_v + 0.0078125F;
		u2 = t4_u + 0.0546875F;
		v2 = t4_v + 0.0507813F;
		tessellator.addVertexWithUV(x2, y2, z1, u2, v1);
		tessellator.addVertexWithUV(x2, y2, z2, u1, v1);
		tessellator.addVertexWithUV(x2, y1, z2, u1, v2);
		tessellator.addVertexWithUV(x2, y1, z1, u2, v2);

		setLightValue(tessellator, world, block, x + 1, y, z, 0.6F);
		u1 = t4_u + 0.0078125F;
		v1 = t4_v + 0.0078125F;
		u2 = t4_u + 0.0546875F;
		v2 = t4_v + 0.0507813F;
		tessellator.addVertexWithUV(x1, y2, z1, u1, v1);
		tessellator.addVertexWithUV(x1, y1, z1, u1, v2);
		tessellator.addVertexWithUV(x1, y1, z2, u2, v2);
		tessellator.addVertexWithUV(x1, y2, z2, u2, v1);

		return true;
	}

	public static boolean renderMeta4 (World world, Block block, float x, float y, float z, int ti_0, int ti_1, int ti_2, int ti_3, int ti_4) {
		Tessellator tessellator = Tessellator.instance;
		float x1, y1, z1, x2, y2, z2;
		float u1, v1, u2, v2;

		// Texture #0
		float t0_u = (float) ((ti_0 & 0x0f) << 4) / 256.0F;
		float t0_v = (float) (ti_0 & 0xff0) / 256.0F;

		// Texture #1
		float t1_u = (float) ((ti_1 & 0x0f) << 4) / 256.0F;
		float t1_v = (float) (ti_1 & 0xff0) / 256.0F;

		// Texture #2
		float t2_u = (float) ((ti_2 & 0x0f) << 4) / 256.0F;
		float t2_v = (float) (ti_2 & 0xff0) / 256.0F;

		// Texture #3
		float t3_u = (float) ((ti_3 & 0x0f) << 4) / 256.0F;
		float t3_v = (float) (ti_3 & 0xff0) / 256.0F;

		// Texture #4
		float t4_u = (float) ((ti_4 & 0x0f) << 4) / 256.0F;
		float t4_v = (float) (ti_4 & 0xff0) / 256.0F;

		// Cube #0

		x1 = x + 0.8750F;
		y1 = y + 0.0000F;
		z1 = z + 0.8750F;
		x2 = x + 0.1250F;
		y2 = y + 0.0625F;
		z2 = z + 0.1250F;

		setLightValue(tessellator, world, block, x, y + 1, z, 1.0F);
		u1 = t0_u + 0.0078125F;
		v1 = t0_v + 0.0078125F;
		u2 = t0_u + 0.0546875F;
		v2 = t0_v + 0.0546875F;
		tessellator.addVertexWithUV(x2, y2, z2, u1, v1);
		tessellator.addVertexWithUV(x2, y2, z1, u2, v1);
		tessellator.addVertexWithUV(x1, y2, z1, u2, v2);
		tessellator.addVertexWithUV(x1, y2, z2, u1, v2);

		setLightValue(tessellator, world, block, x, y - 1, z, 0.5F);
		u1 = t0_u + 0.0078125F;
		v1 = t0_v + 0.0078125F;
		u2 = t0_u + 0.0546875F;
		v2 = t0_v + 0.0546875F;
		tessellator.addVertexWithUV(x1, y1, z2, u1, v2);
		tessellator.addVertexWithUV(x1, y1, z1, u2, v2);
		tessellator.addVertexWithUV(x2, y1, z1, u2, v1);
		tessellator.addVertexWithUV(x2, y1, z2, u1, v1);

		setLightValue(tessellator, world, block, x + 1, y, z, 0.6F);
		u1 = t0_u + 0.0078125F;
		v1 = t0_v + 0.0078125F;
		u2 = t0_u + 0.0546875F;
		v2 = t0_v + 0.0117188F;
		tessellator.addVertexWithUV(x1, y2, z1, u1, v1);
		tessellator.addVertexWithUV(x1, y1, z1, u1, v2);
		tessellator.addVertexWithUV(x1, y1, z2, u2, v2);
		tessellator.addVertexWithUV(x1, y2, z2, u2, v1);

		setLightValue(tessellator, world, block, x - 1, y, z, 0.6F);
		u1 = t0_u + 0.0078125F;
		v1 = t0_v + 0.0078125F;
		u2 = t0_u + 0.0546875F;
		v2 = t0_v + 0.0117188F;
		tessellator.addVertexWithUV(x2, y2, z1, u2, v1);
		tessellator.addVertexWithUV(x2, y2, z2, u1, v1);
		tessellator.addVertexWithUV(x2, y1, z2, u1, v2);
		tessellator.addVertexWithUV(x2, y1, z1, u2, v2);

		setLightValue(tessellator, world, block, x, y, z + 1, 0.8F);
		u1 = t0_u + 0.0078125F;
		v1 = t0_v + 0.0078125F;
		u2 = t0_u + 0.0546875F;
		v2 = t0_v + 0.0117188F;
		tessellator.addVertexWithUV(x1, y2, z1, u2, v1);
		tessellator.addVertexWithUV(x2, y2, z1, u1, v1);
		tessellator.addVertexWithUV(x2, y1, z1, u1, v2);
		tessellator.addVertexWithUV(x1, y1, z1, u2, v2);

		setLightValue(tessellator, world, block, x, y, z - 1, 0.8F);
		u1 = t0_u + 0.0078125F;
		v1 = t0_v + 0.0078125F;
		u2 = t0_u + 0.0546875F;
		v2 = t0_v + 0.0117188F;
		tessellator.addVertexWithUV(x1, y2, z2, u1, v1);
		tessellator.addVertexWithUV(x1, y1, z2, u1, v2);
		tessellator.addVertexWithUV(x2, y1, z2, u2, v2);
		tessellator.addVertexWithUV(x2, y2, z2, u2, v1);

		// Cube #1

		x1 = x + 0.9375F;
		y1 = y + 0.0625F;
		z1 = z + 0.9375F;
		x2 = x + 0.0625F;
		y2 = y + 0.1250F;
		z2 = z + 0.0625F;

		setLightValue(tessellator, world, block, x, y + 1, z, 1.0F);
		u1 = t0_u + 0.0078125F;
		v1 = t0_v + 0.0078125F;
		u2 = t0_u + 0.0546875F;
		v2 = t0_v + 0.0546875F;
		tessellator.addVertexWithUV(x2, y2, z2, u1, v1);
		tessellator.addVertexWithUV(x2, y2, z1, u2, v1);
		tessellator.addVertexWithUV(x1, y2, z1, u2, v2);
		tessellator.addVertexWithUV(x1, y2, z2, u1, v2);

		setLightValue(tessellator, world, block, x, y - 1, z, 0.5F);
		u1 = t0_u + 0.0078125F;
		v1 = t0_v + 0.0078125F;
		u2 = t0_u + 0.0546875F;
		v2 = t0_v + 0.0546875F;
		tessellator.addVertexWithUV(x1, y1, z2, u1, v2);
		tessellator.addVertexWithUV(x1, y1, z1, u2, v2);
		tessellator.addVertexWithUV(x2, y1, z1, u2, v1);
		tessellator.addVertexWithUV(x2, y1, z2, u1, v1);

		setLightValue(tessellator, world, block, x + 1, y, z, 0.6F);
		u1 = t0_u + 0.0078125F;
		v1 = t0_v + 0.0078125F;
		u2 = t0_u + 0.0546875F;
		v2 = t0_v + 0.0117188F;
		tessellator.addVertexWithUV(x1, y2, z1, u1, v1);
		tessellator.addVertexWithUV(x1, y1, z1, u1, v2);
		tessellator.addVertexWithUV(x1, y1, z2, u2, v2);
		tessellator.addVertexWithUV(x1, y2, z2, u2, v1);

		setLightValue(tessellator, world, block, x - 1, y, z, 0.6F);
		u1 = t0_u + 0.0078125F;
		v1 = t0_v + 0.0078125F;
		u2 = t0_u + 0.0546875F;
		v2 = t0_v + 0.0117188F;
		tessellator.addVertexWithUV(x2, y2, z1, u2, v1);
		tessellator.addVertexWithUV(x2, y2, z2, u1, v1);
		tessellator.addVertexWithUV(x2, y1, z2, u1, v2);
		tessellator.addVertexWithUV(x2, y1, z1, u2, v2);

		setLightValue(tessellator, world, block, x, y, z + 1, 0.8F);
		u1 = t0_u + 0.0078125F;
		v1 = t0_v + 0.0078125F;
		u2 = t0_u + 0.0546875F;
		v2 = t0_v + 0.0117188F;
		tessellator.addVertexWithUV(x1, y2, z1, u2, v1);
		tessellator.addVertexWithUV(x2, y2, z1, u1, v1);
		tessellator.addVertexWithUV(x2, y1, z1, u1, v2);
		tessellator.addVertexWithUV(x1, y1, z1, u2, v2);

		setLightValue(tessellator, world, block, x, y, z - 1, 0.8F);
		u1 = t0_u + 0.0078125F;
		v1 = t0_v + 0.0078125F;
		u2 = t0_u + 0.0546875F;
		v2 = t0_v + 0.0117188F;
		tessellator.addVertexWithUV(x1, y2, z2, u1, v1);
		tessellator.addVertexWithUV(x1, y1, z2, u1, v2);
		tessellator.addVertexWithUV(x2, y1, z2, u2, v2);
		tessellator.addVertexWithUV(x2, y2, z2, u2, v1);

		// Cube #2

		x1 = x + 0.1250F;
		y1 = y + 0.1250F;
		z1 = z + 0.9375F;
		x2 = x + 0.0000F;
		y2 = y + 1.0000F;
		z2 = z + 0.0625F;

		setLightValue(tessellator, world, block, x, y + 1, z, 1.0F);
		u1 = t0_u + 0.0039063F;
		v1 = t0_v + 0.0546875F;
		u2 = t0_u + 0.0585938F;
		v2 = t0_v + 0.0625000F;
		tessellator.addVertexWithUV(x2, y2, z2, u1, v1);
		tessellator.addVertexWithUV(x2, y2, z1, u2, v1);
		tessellator.addVertexWithUV(x1, y2, z1, u2, v2);
		tessellator.addVertexWithUV(x1, y2, z2, u1, v2);

		setLightValue(tessellator, world, block, x, y - 1, z, 0.5F);
		u1 = t0_u + 0.0039063F;
		v1 = t0_v + 0.0000000F;
		u2 = t0_u + 0.0585938F;
		v2 = t0_v + 0.0078125F;
		tessellator.addVertexWithUV(x1, y1, z2, u1, v2);
		tessellator.addVertexWithUV(x1, y1, z1, u2, v2);
		tessellator.addVertexWithUV(x2, y1, z1, u2, v1);
		tessellator.addVertexWithUV(x2, y1, z2, u1, v1);

		setLightValue(tessellator, world, block, x + 1, y, z, 0.6F);
		u1 = t0_u + 0.0039063F;
		v1 = t0_v + 0.0039063F;
		u2 = t0_u + 0.0585938F;
		v2 = t0_v + 0.0585938F;
		tessellator.addVertexWithUV(x1, y2, z1, u1, v1);
		tessellator.addVertexWithUV(x1, y1, z1, u1, v2);
		tessellator.addVertexWithUV(x1, y1, z2, u2, v2);
		tessellator.addVertexWithUV(x1, y2, z2, u2, v1);

		setLightValue(tessellator, world, block, x - 1, y, z, 0.6F);
		u1 = t1_u + 0.0039063F;
		v1 = t1_v + 0.0000000F;
		u2 = t1_u + 0.0585938F;
		v2 = t1_v + 0.0546875F;
		tessellator.addVertexWithUV(x2, y2, z1, u2, v1);
		tessellator.addVertexWithUV(x2, y2, z2, u1, v1);
		tessellator.addVertexWithUV(x2, y1, z2, u1, v2);
		tessellator.addVertexWithUV(x2, y1, z1, u2, v2);

		setLightValue(tessellator, world, block, x, y, z + 1, 0.8F);
		u1 = t1_u + 0.0546875F;
		v1 = t1_v + 0.0000000F;
		u2 = t1_u + 0.0625000F;
		v2 = t1_v + 0.0546875F;
		tessellator.addVertexWithUV(x1, y2, z1, u2, v1);
		tessellator.addVertexWithUV(x2, y2, z1, u1, v1);
		tessellator.addVertexWithUV(x2, y1, z1, u1, v2);
		tessellator.addVertexWithUV(x1, y1, z1, u2, v2);

		setLightValue(tessellator, world, block, x, y, z - 1, 0.8F);
		u1 = t1_u + 0.0000000F;
		v1 = t1_v + 0.0000000F;
		u2 = t1_u + 0.0078125F;
		v2 = t1_v + 0.0546875F;
		tessellator.addVertexWithUV(x1, y2, z2, u1, v1);
		tessellator.addVertexWithUV(x1, y1, z2, u1, v2);
		tessellator.addVertexWithUV(x2, y1, z2, u2, v2);
		tessellator.addVertexWithUV(x2, y2, z2, u2, v1);

		// Cube #3

		x1 = x + 1.0000F;
		y1 = y + 0.1250F;
		z1 = z + 0.9375F;
		x2 = x + 0.8750F;
		y2 = y + 1.0000F;
		z2 = z + 0.0625F;

		setLightValue(tessellator, world, block, x, y + 1, z, 1.0F);
		u1 = t0_u + 0.0039063F;
		v1 = t0_v + 0.0546875F;
		u2 = t0_u + 0.0585938F;
		v2 = t0_v + 0.0625000F;
		tessellator.addVertexWithUV(x2, y2, z2, u1, v1);
		tessellator.addVertexWithUV(x2, y2, z1, u2, v1);
		tessellator.addVertexWithUV(x1, y2, z1, u2, v2);
		tessellator.addVertexWithUV(x1, y2, z2, u1, v2);

		setLightValue(tessellator, world, block, x, y - 1, z, 0.5F);
		u1 = t0_u + 0.0039063F;
		v1 = t0_v + 0.0546875F;
		u2 = t0_u + 0.0585938F;
		v2 = t0_v + 0.0625000F;
		tessellator.addVertexWithUV(x1, y1, z2, u1, v2);
		tessellator.addVertexWithUV(x1, y1, z1, u2, v2);
		tessellator.addVertexWithUV(x2, y1, z1, u2, v1);
		tessellator.addVertexWithUV(x2, y1, z2, u1, v1);

		setLightValue(tessellator, world, block, x + 1, y, z, 0.6F);
		u1 = t1_u + 0.0585938F;
		v1 = t1_v + 0.0000000F;
		u2 = t1_u + 0.0039063F;
		v2 = t1_v + 0.0546875F;
		tessellator.addVertexWithUV(x1, y2, z1, u1, v1);
		tessellator.addVertexWithUV(x1, y1, z1, u1, v2);
		tessellator.addVertexWithUV(x1, y1, z2, u2, v2);
		tessellator.addVertexWithUV(x1, y2, z2, u2, v1);

		setLightValue(tessellator, world, block, x - 1, y, z, 0.6F);
		u1 = t0_u + 0.0039063F;
		v1 = t0_v + 0.0039063F;
		u2 = t0_u + 0.0585938F;
		v2 = t0_v + 0.0585938F;
		tessellator.addVertexWithUV(x2, y2, z1, u2, v1);
		tessellator.addVertexWithUV(x2, y2, z2, u1, v1);
		tessellator.addVertexWithUV(x2, y1, z2, u1, v2);
		tessellator.addVertexWithUV(x2, y1, z1, u2, v2);

		setLightValue(tessellator, world, block, x, y, z + 1, 0.8F);
		u1 = t1_u + 0.0625000F;
		v1 = t1_v + 0.0000000F;
		u2 = t1_u + 0.0546875F;
		v2 = t1_v + 0.0546875F;
		tessellator.addVertexWithUV(x1, y2, z1, u2, v1);
		tessellator.addVertexWithUV(x2, y2, z1, u1, v1);
		tessellator.addVertexWithUV(x2, y1, z1, u1, v2);
		tessellator.addVertexWithUV(x1, y1, z1, u2, v2);

		setLightValue(tessellator, world, block, x, y, z - 1, 0.8F);
		u1 = t1_u + 0.0078125F;
		v1 = t1_v + 0.0000000F;
		u2 = t1_u + 0.0000000F;
		v2 = t1_v + 0.0546875F;
		tessellator.addVertexWithUV(x1, y2, z2, u1, v1);
		tessellator.addVertexWithUV(x1, y1, z2, u1, v2);
		tessellator.addVertexWithUV(x2, y1, z2, u2, v2);
		tessellator.addVertexWithUV(x2, y2, z2, u2, v1);

		// Cube #4

		x1 = x + 0.9375F;
		y1 = y + 0.1250F;
		z1 = z + 1.0000F;
		x2 = x + 0.0625F;
		y2 = y + 1.0000F;
		z2 = z + 0.8750F;

		setLightValue(tessellator, world, block, x, y + 1, z, 1.0F);
		u1 = t0_u + 0.0000000F;
		v1 = t0_v + 0.0039063F;
		u2 = t0_u + 0.0078125F;
		v2 = t0_v + 0.0585938F;
		tessellator.addVertexWithUV(x2, y2, z2, u1, v1);
		tessellator.addVertexWithUV(x2, y2, z1, u2, v1);
		tessellator.addVertexWithUV(x1, y2, z1, u2, v2);
		tessellator.addVertexWithUV(x1, y2, z2, u1, v2);

		setLightValue(tessellator, world, block, x, y - 1, z, 0.5F);
		u1 = t0_u + 0.0000000F;
		v1 = t0_v + 0.0039063F;
		u2 = t0_u + 0.0078125F;
		v2 = t0_v + 0.0585938F;
		tessellator.addVertexWithUV(x1, y1, z2, u1, v2);
		tessellator.addVertexWithUV(x1, y1, z1, u2, v2);
		tessellator.addVertexWithUV(x2, y1, z1, u2, v1);
		tessellator.addVertexWithUV(x2, y1, z2, u1, v1);

		setLightValue(tessellator, world, block, x + 1, y, z, 0.6F);
		u1 = t2_u + 0.0000000F;
		v1 = t2_v + 0.0000000F;
		u2 = t2_u + 0.0078125F;
		v2 = t2_v + 0.0546875F;
		tessellator.addVertexWithUV(x1, y2, z1, u1, v1);
		tessellator.addVertexWithUV(x1, y1, z1, u1, v2);
		tessellator.addVertexWithUV(x1, y1, z2, u2, v2);
		tessellator.addVertexWithUV(x1, y2, z2, u2, v1);

		setLightValue(tessellator, world, block, x - 1, y, z, 0.6F);
		u1 = t2_u + 0.0546875F;
		v1 = t2_v + 0.0000000F;
		u2 = t2_u + 0.0625000F;
		v2 = t2_v + 0.0546875F;
		tessellator.addVertexWithUV(x2, y2, z1, u2, v1);
		tessellator.addVertexWithUV(x2, y2, z2, u1, v1);
		tessellator.addVertexWithUV(x2, y1, z2, u1, v2);
		tessellator.addVertexWithUV(x2, y1, z1, u2, v2);

		setLightValue(tessellator, world, block, x, y, z + 1, 0.8F);
		u1 = t2_u + 0.0039063F;
		v1 = t2_v + 0.0000000F;
		u2 = t2_u + 0.0585938F;
		v2 = t2_v + 0.0546875F;
		tessellator.addVertexWithUV(x1, y2, z1, u2, v1);
		tessellator.addVertexWithUV(x2, y2, z1, u1, v1);
		tessellator.addVertexWithUV(x2, y1, z1, u1, v2);
		tessellator.addVertexWithUV(x1, y1, z1, u2, v2);

		setLightValue(tessellator, world, block, x, y, z - 1, 0.8F);
		u1 = t0_u + 0.0039063F;
		v1 = t0_v + 0.0039063F;
		u2 = t0_u + 0.0585938F;
		v2 = t0_v + 0.0585938F;
		tessellator.addVertexWithUV(x1, y2, z2, u1, v1);
		tessellator.addVertexWithUV(x1, y1, z2, u1, v2);
		tessellator.addVertexWithUV(x2, y1, z2, u2, v2);
		tessellator.addVertexWithUV(x2, y2, z2, u2, v1);

		// Cube #5

		x1 = x + 0.9375F;
		y1 = y + 0.1250F;
		z1 = z + 0.1250F;
		x2 = x + 0.0625F;
		y2 = y + 1.0000F;
		z2 = z + 0.0000F;

		setLightValue(tessellator, world, block, x, y + 1, z, 1.0F);
		u1 = t0_u + 0.0546875F;
		v1 = t0_v + 0.0039063F;
		u2 = t0_u + 0.0625000F;
		v2 = t0_v + 0.0585938F;
		tessellator.addVertexWithUV(x2, y2, z2, u1, v1);
		tessellator.addVertexWithUV(x2, y2, z1, u2, v1);
		tessellator.addVertexWithUV(x1, y2, z1, u2, v2);
		tessellator.addVertexWithUV(x1, y2, z2, u1, v2);

		setLightValue(tessellator, world, block, x, y - 1, z, 0.5F);
		u1 = t0_u + 0.0546875F;
		v1 = t0_v + 0.0039063F;
		u2 = t0_u + 0.0625000F;
		v2 = t0_v + 0.0585938F;
		tessellator.addVertexWithUV(x1, y1, z2, u1, v2);
		tessellator.addVertexWithUV(x1, y1, z1, u2, v2);
		tessellator.addVertexWithUV(x2, y1, z1, u2, v1);
		tessellator.addVertexWithUV(x2, y1, z2, u1, v1);

		setLightValue(tessellator, world, block, x + 1, y, z, 0.6F);
		u1 = t3_u + 0.0000000F;
		v1 = t3_v + 0.0000000F;
		u2 = t3_u + 0.0078125F;
		v2 = t3_v + 0.0546875F;
		tessellator.addVertexWithUV(x1, y2, z1, u1, v1);
		tessellator.addVertexWithUV(x1, y1, z1, u1, v2);
		tessellator.addVertexWithUV(x1, y1, z2, u2, v2);
		tessellator.addVertexWithUV(x1, y2, z2, u2, v1);

		setLightValue(tessellator, world, block, x - 1, y, z, 0.6F);
		u1 = t3_u + 0.0546875F;
		v1 = t3_v + 0.0000000F;
		u2 = t3_u + 0.0625000F;
		v2 = t3_v + 0.0546875F;
		tessellator.addVertexWithUV(x2, y2, z1, u2, v1);
		tessellator.addVertexWithUV(x2, y2, z2, u1, v1);
		tessellator.addVertexWithUV(x2, y1, z2, u1, v2);
		tessellator.addVertexWithUV(x2, y1, z1, u2, v2);

		setLightValue(tessellator, world, block, x, y, z + 1, 0.8F);
		u1 = t0_u + 0.0039063F;
		v1 = t0_v + 0.0039063F;
		u2 = t0_u + 0.0585938F;
		v2 = t0_v + 0.0585938F;
		tessellator.addVertexWithUV(x1, y2, z1, u2, v1);
		tessellator.addVertexWithUV(x2, y2, z1, u1, v1);
		tessellator.addVertexWithUV(x2, y1, z1, u1, v2);
		tessellator.addVertexWithUV(x1, y1, z1, u2, v2);

		setLightValue(tessellator, world, block, x, y, z - 1, 0.8F);
		u1 = t3_u + 0.0039063F;
		v1 = t3_v + 0.0000000F;
		u2 = t3_u + 0.0585938F;
		v2 = t3_v + 0.0546875F;
		tessellator.addVertexWithUV(x1, y2, z2, u1, v1);
		tessellator.addVertexWithUV(x1, y1, z2, u1, v2);
		tessellator.addVertexWithUV(x2, y1, z2, u2, v2);
		tessellator.addVertexWithUV(x2, y2, z2, u2, v1);

		// Cube #6

		x1 = x + 0.8750F;
		y1 = y + 0.1250F;
		z1 = z + 0.8750F;
		x2 = x + 0.1250F;
		y2 = y + 0.8125F;
		z2 = z + 0.1250F;

		setLightValue(tessellator, world, block, x, y + 1, z, 1.0F);
		u1 = t4_u + 0.0078125F;
		v1 = t4_v + 0.0078125F;
		u2 = t4_u + 0.0546875F;
		v2 = t4_v + 0.0546875F;
		tessellator.addVertexWithUV(x2, y2, z2, u1, v1);
		tessellator.addVertexWithUV(x2, y2, z1, u2, v1);
		tessellator.addVertexWithUV(x1, y2, z1, u2, v2);
		tessellator.addVertexWithUV(x1, y2, z2, u1, v2);

		setLightValue(tessellator, world, block, x, y - 1, z, 0.5F);
		u1 = t4_u + 0.0078125F;
		v1 = t4_v + 0.0078125F;
		u2 = t4_u + 0.0546875F;
		v2 = t4_v + 0.0546875F;
		tessellator.addVertexWithUV(x1, y1, z2, u1, v2);
		tessellator.addVertexWithUV(x1, y1, z1, u2, v2);
		tessellator.addVertexWithUV(x2, y1, z1, u2, v1);
		tessellator.addVertexWithUV(x2, y1, z2, u1, v1);

		setLightValue(tessellator, world, block, x + 1, y, z, 0.6F);
		u1 = t4_u + 0.0078125F;
		v1 = t4_v + 0.0078125F;
		u2 = t4_u + 0.0507813F;
		v2 = t4_v + 0.0546875F;
		tessellator.addVertexWithUV(x1, y2, z1, u1, v1);
		tessellator.addVertexWithUV(x1, y1, z1, u1, v2);
		tessellator.addVertexWithUV(x1, y1, z2, u2, v2);
		tessellator.addVertexWithUV(x1, y2, z2, u2, v1);

		setLightValue(tessellator, world, block, x - 1, y, z, 0.6F);
		u1 = t4_u + 0.0078125F;
		v1 = t4_v + 0.0078125F;
		u2 = t4_u + 0.0546875F;
		v2 = t4_v + 0.0507813F;
		tessellator.addVertexWithUV(x2, y2, z1, u2, v1);
		tessellator.addVertexWithUV(x2, y2, z2, u1, v1);
		tessellator.addVertexWithUV(x2, y1, z2, u1, v2);
		tessellator.addVertexWithUV(x2, y1, z1, u2, v2);

		setLightValue(tessellator, world, block, x, y, z + 1, 0.8F);
		u1 = t4_u + 0.0078125F;
		v1 = t4_v + 0.0078125F;
		u2 = t4_u + 0.0546875F;
		v2 = t4_v + 0.0507813F;
		tessellator.addVertexWithUV(x1, y2, z1, u2, v1);
		tessellator.addVertexWithUV(x2, y2, z1, u1, v1);
		tessellator.addVertexWithUV(x2, y1, z1, u1, v2);
		tessellator.addVertexWithUV(x1, y1, z1, u2, v2);

		setLightValue(tessellator, world, block, x, y, z - 1, 0.8F);
		u1 = t4_u + 0.0078125F;
		v1 = t4_v + 0.0078125F;
		u2 = t4_u + 0.0546875F;
		v2 = t4_v + 0.0507813F;
		tessellator.addVertexWithUV(x1, y2, z2, u1, v1);
		tessellator.addVertexWithUV(x1, y1, z2, u1, v2);
		tessellator.addVertexWithUV(x2, y1, z2, u2, v2);
		tessellator.addVertexWithUV(x2, y2, z2, u2, v1);

		return true;
	}

	public static boolean renderMeta5 (World world, Block block, float x, float y, float z, int ti_0, int ti_1, int ti_2, int ti_3, int ti_4) {
		Tessellator tessellator = Tessellator.instance;
		float x1, y1, z1, x2, y2, z2;
		float u1, v1, u2, v2;

		// Texture #0
		float t0_u = (float) ((ti_0 & 0x0f) << 4) / 256.0F;
		float t0_v = (float) (ti_0 & 0xff0) / 256.0F;

		// Texture #1
		float t1_u = (float) ((ti_1 & 0x0f) << 4) / 256.0F;
		float t1_v = (float) (ti_1 & 0xff0) / 256.0F;

		// Texture #2
		float t2_u = (float) ((ti_2 & 0x0f) << 4) / 256.0F;
		float t2_v = (float) (ti_2 & 0xff0) / 256.0F;

		// Texture #3
		float t3_u = (float) ((ti_3 & 0x0f) << 4) / 256.0F;
		float t3_v = (float) (ti_3 & 0xff0) / 256.0F;

		// Texture #4
		float t4_u = (float) ((ti_4 & 0x0f) << 4) / 256.0F;
		float t4_v = (float) (ti_4 & 0xff0) / 256.0F;

		// Cube #0

		x1 = x + 0.8750F;
		y1 = y + 0.0000F;
		z1 = z + 0.8750F;
		x2 = x + 0.1250F;
		y2 = y + 0.0625F;
		z2 = z + 0.1250F;

		setLightValue(tessellator, world, block, x, y + 1, z, 1.0F);
		u1 = t0_u + 0.0078125F;
		v1 = t0_v + 0.0078125F;
		u2 = t0_u + 0.0546875F;
		v2 = t0_v + 0.0546875F;
		tessellator.addVertexWithUV(x2, y2, z2, u2, v1);
		tessellator.addVertexWithUV(x2, y2, z1, u1, v1);
		tessellator.addVertexWithUV(x1, y2, z1, u1, v2);
		tessellator.addVertexWithUV(x1, y2, z2, u2, v2);

		setLightValue(tessellator, world, block, x, y - 1, z, 0.5F);
		u1 = t0_u + 0.0078125F;
		v1 = t0_v + 0.0078125F;
		u2 = t0_u + 0.0546875F;
		v2 = t0_v + 0.0546875F;
		tessellator.addVertexWithUV(x1, y1, z2, u2, v2);
		tessellator.addVertexWithUV(x1, y1, z1, u1, v2);
		tessellator.addVertexWithUV(x2, y1, z1, u1, v1);
		tessellator.addVertexWithUV(x2, y1, z2, u2, v1);

		setLightValue(tessellator, world, block, x + 1, y, z, 0.6F);
		u1 = t0_u + 0.0078125F;
		v1 = t0_v + 0.0078125F;
		u2 = t0_u + 0.0546875F;
		v2 = t0_v + 0.0117188F;
		tessellator.addVertexWithUV(x1, y2, z1, u1, v1);
		tessellator.addVertexWithUV(x1, y1, z1, u1, v2);
		tessellator.addVertexWithUV(x1, y1, z2, u2, v2);
		tessellator.addVertexWithUV(x1, y2, z2, u2, v1);

		setLightValue(tessellator, world, block, x - 1, y, z, 0.6F);
		u1 = t0_u + 0.0078125F;
		v1 = t0_v + 0.0078125F;
		u2 = t0_u + 0.0546875F;
		v2 = t0_v + 0.0117188F;
		tessellator.addVertexWithUV(x2, y2, z1, u2, v1);
		tessellator.addVertexWithUV(x2, y2, z2, u1, v1);
		tessellator.addVertexWithUV(x2, y1, z2, u1, v2);
		tessellator.addVertexWithUV(x2, y1, z1, u2, v2);

		setLightValue(tessellator, world, block, x, y, z + 1, 0.8F);
		u1 = t0_u + 0.0078125F;
		v1 = t0_v + 0.0078125F;
		u2 = t0_u + 0.0546875F;
		v2 = t0_v + 0.0117188F;
		tessellator.addVertexWithUV(x1, y2, z1, u2, v1);
		tessellator.addVertexWithUV(x2, y2, z1, u1, v1);
		tessellator.addVertexWithUV(x2, y1, z1, u1, v2);
		tessellator.addVertexWithUV(x1, y1, z1, u2, v2);

		setLightValue(tessellator, world, block, x, y, z - 1, 0.8F);
		u1 = t0_u + 0.0078125F;
		v1 = t0_v + 0.0078125F;
		u2 = t0_u + 0.0546875F;
		v2 = t0_v + 0.0117188F;
		tessellator.addVertexWithUV(x1, y2, z2, u1, v1);
		tessellator.addVertexWithUV(x1, y1, z2, u1, v2);
		tessellator.addVertexWithUV(x2, y1, z2, u2, v2);
		tessellator.addVertexWithUV(x2, y2, z2, u2, v1);

		// Cube #1

		x1 = x + 0.9375F;
		y1 = y + 0.0625F;
		z1 = z + 0.9375F;
		x2 = x + 0.0625F;
		y2 = y + 0.1250F;
		z2 = z + 0.0625F;

		setLightValue(tessellator, world, block, x, y + 1, z, 1.0F);
		u1 = t0_u + 0.0078125F;
		v1 = t0_v + 0.0078125F;
		u2 = t0_u + 0.0546875F;
		v2 = t0_v + 0.0546875F;
		tessellator.addVertexWithUV(x2, y2, z2, u2, v1);
		tessellator.addVertexWithUV(x2, y2, z1, u1, v1);
		tessellator.addVertexWithUV(x1, y2, z1, u1, v2);
		tessellator.addVertexWithUV(x1, y2, z2, u2, v2);

		setLightValue(tessellator, world, block, x, y - 1, z, 0.5F);
		u1 = t0_u + 0.0078125F;
		v1 = t0_v + 0.0078125F;
		u2 = t0_u + 0.0546875F;
		v2 = t0_v + 0.0546875F;
		tessellator.addVertexWithUV(x1, y1, z2, u2, v2);
		tessellator.addVertexWithUV(x1, y1, z1, u1, v2);
		tessellator.addVertexWithUV(x2, y1, z1, u1, v1);
		tessellator.addVertexWithUV(x2, y1, z2, u2, v1);

		setLightValue(tessellator, world, block, x + 1, y, z, 0.6F);
		u1 = t0_u + 0.0078125F;
		v1 = t0_v + 0.0078125F;
		u2 = t0_u + 0.0546875F;
		v2 = t0_v + 0.0117188F;
		tessellator.addVertexWithUV(x1, y2, z1, u1, v1);
		tessellator.addVertexWithUV(x1, y1, z1, u1, v2);
		tessellator.addVertexWithUV(x1, y1, z2, u2, v2);
		tessellator.addVertexWithUV(x1, y2, z2, u2, v1);

		setLightValue(tessellator, world, block, x - 1, y, z, 0.6F);
		u1 = t0_u + 0.0078125F;
		v1 = t0_v + 0.0078125F;
		u2 = t0_u + 0.0546875F;
		v2 = t0_v + 0.0117188F;
		tessellator.addVertexWithUV(x2, y2, z1, u2, v1);
		tessellator.addVertexWithUV(x2, y2, z2, u1, v1);
		tessellator.addVertexWithUV(x2, y1, z2, u1, v2);
		tessellator.addVertexWithUV(x2, y1, z1, u2, v2);

		setLightValue(tessellator, world, block, x, y, z + 1, 0.8F);
		u1 = t0_u + 0.0078125F;
		v1 = t0_v + 0.0078125F;
		u2 = t0_u + 0.0546875F;
		v2 = t0_v + 0.0117188F;
		tessellator.addVertexWithUV(x1, y2, z1, u2, v1);
		tessellator.addVertexWithUV(x2, y2, z1, u1, v1);
		tessellator.addVertexWithUV(x2, y1, z1, u1, v2);
		tessellator.addVertexWithUV(x1, y1, z1, u2, v2);

		setLightValue(tessellator, world, block, x, y, z - 1, 0.8F);
		u1 = t0_u + 0.0078125F;
		v1 = t0_v + 0.0078125F;
		u2 = t0_u + 0.0546875F;
		v2 = t0_v + 0.0117188F;
		tessellator.addVertexWithUV(x1, y2, z2, u1, v1);
		tessellator.addVertexWithUV(x1, y1, z2, u1, v2);
		tessellator.addVertexWithUV(x2, y1, z2, u2, v2);
		tessellator.addVertexWithUV(x2, y2, z2, u2, v1);

		// Cube #2

		x1 = x + 1.0000F;
		y1 = y + 0.1250F;
		z1 = z + 0.9375F;
		x2 = x + 0.8750F;
		y2 = y + 1.0000F;
		z2 = z + 0.0625F;

		setLightValue(tessellator, world, block, x, y + 1, z, 1.0F);
		u1 = t0_u + 0.0039063F;
		v1 = t0_v + 0.0546875F;
		u2 = t0_u + 0.0585938F;
		v2 = t0_v + 0.0625000F;
		tessellator.addVertexWithUV(x2, y2, z2, u2, v1);
		tessellator.addVertexWithUV(x2, y2, z1, u1, v1);
		tessellator.addVertexWithUV(x1, y2, z1, u1, v2);
		tessellator.addVertexWithUV(x1, y2, z2, u2, v2);

		setLightValue(tessellator, world, block, x, y - 1, z, 0.5F);
		u1 = t0_u + 0.0039063F;
		v1 = t0_v + 0.0000000F;
		u2 = t0_u + 0.0585938F;
		v2 = t0_v + 0.0078125F;
		tessellator.addVertexWithUV(x1, y1, z2, u2, v2);
		tessellator.addVertexWithUV(x1, y1, z1, u1, v2);
		tessellator.addVertexWithUV(x2, y1, z1, u1, v1);
		tessellator.addVertexWithUV(x2, y1, z2, u2, v1);

		setLightValue(tessellator, world, block, x + 1, y, z, 0.6F);
		u1 = t1_u + 0.0039063F;
		v1 = t1_v + 0.0000000F;
		u2 = t1_u + 0.0585938F;
		v2 = t1_v + 0.0546875F;
		tessellator.addVertexWithUV(x1, y2, z1, u1, v1);
		tessellator.addVertexWithUV(x1, y1, z1, u1, v2);
		tessellator.addVertexWithUV(x1, y1, z2, u2, v2);
		tessellator.addVertexWithUV(x1, y2, z2, u2, v1);

		setLightValue(tessellator, world, block, x - 1, y, z, 0.6F);
		u1 = t0_u + 0.0039063F;
		v1 = t0_v + 0.0039063F;
		u2 = t0_u + 0.0585938F;
		v2 = t0_v + 0.0585938F;
		tessellator.addVertexWithUV(x2, y2, z1, u2, v1);
		tessellator.addVertexWithUV(x2, y2, z2, u1, v1);
		tessellator.addVertexWithUV(x2, y1, z2, u1, v2);
		tessellator.addVertexWithUV(x2, y1, z1, u2, v2);

		setLightValue(tessellator, world, block, x, y, z + 1, 0.8F);
		u1 = t1_u + 0.0000000F;
		v1 = t1_v + 0.0000000F;
		u2 = t1_u + 0.0078125F;
		v2 = t1_v + 0.0546875F;
		tessellator.addVertexWithUV(x1, y2, z1, u2, v1);
		tessellator.addVertexWithUV(x2, y2, z1, u1, v1);
		tessellator.addVertexWithUV(x2, y1, z1, u1, v2);
		tessellator.addVertexWithUV(x1, y1, z1, u2, v2);

		setLightValue(tessellator, world, block, x, y, z - 1, 0.8F);
		u1 = t1_u + 0.0546875F;
		v1 = t1_v + 0.0000000F;
		u2 = t1_u + 0.0625000F;
		v2 = t1_v + 0.0546875F;
		tessellator.addVertexWithUV(x1, y2, z2, u1, v1);
		tessellator.addVertexWithUV(x1, y1, z2, u1, v2);
		tessellator.addVertexWithUV(x2, y1, z2, u2, v2);
		tessellator.addVertexWithUV(x2, y2, z2, u2, v1);

		// Cube #3

		x1 = x + 0.1250F;
		y1 = y + 0.1250F;
		z1 = z + 0.9375F;
		x2 = x + 0.0000F;
		y2 = y + 1.0000F;
		z2 = z + 0.0625F;

		setLightValue(tessellator, world, block, x, y + 1, z, 1.0F);
		u1 = t0_u + 0.0039063F;
		v1 = t0_v + 0.0546875F;
		u2 = t0_u + 0.0585938F;
		v2 = t0_v + 0.0625000F;
		tessellator.addVertexWithUV(x2, y2, z2, u2, v1);
		tessellator.addVertexWithUV(x2, y2, z1, u1, v1);
		tessellator.addVertexWithUV(x1, y2, z1, u1, v2);
		tessellator.addVertexWithUV(x1, y2, z2, u2, v2);

		setLightValue(tessellator, world, block, x, y - 1, z, 0.5F);
		u1 = t0_u + 0.0039063F;
		v1 = t0_v + 0.0546875F;
		u2 = t0_u + 0.0585938F;
		v2 = t0_v + 0.0625000F;
		tessellator.addVertexWithUV(x1, y1, z2, u2, v2);
		tessellator.addVertexWithUV(x1, y1, z1, u1, v2);
		tessellator.addVertexWithUV(x2, y1, z1, u1, v1);
		tessellator.addVertexWithUV(x2, y1, z2, u2, v1);

		setLightValue(tessellator, world, block, x + 1, y, z, 0.6F);
		u1 = t0_u + 0.0039063F;
		v1 = t0_v + 0.0039063F;
		u2 = t0_u + 0.0585938F;
		v2 = t0_v + 0.0585938F;
		tessellator.addVertexWithUV(x1, y2, z1, u1, v1);
		tessellator.addVertexWithUV(x1, y1, z1, u1, v2);
		tessellator.addVertexWithUV(x1, y1, z2, u2, v2);
		tessellator.addVertexWithUV(x1, y2, z2, u2, v1);

		setLightValue(tessellator, world, block, x - 1, y, z, 0.6F);
		u1 = t1_u + 0.0585938F;
		v1 = t1_v + 0.0000000F;
		u2 = t1_u + 0.0039063F;
		v2 = t1_v + 0.0546875F;
		tessellator.addVertexWithUV(x2, y2, z1, u2, v1);
		tessellator.addVertexWithUV(x2, y2, z2, u1, v1);
		tessellator.addVertexWithUV(x2, y1, z2, u1, v2);
		tessellator.addVertexWithUV(x2, y1, z1, u2, v2);

		setLightValue(tessellator, world, block, x, y, z + 1, 0.8F);
		u1 = t1_u + 0.0078125F;
		v1 = t1_v + 0.0000000F;
		u2 = t1_u + 0.0000000F;
		v2 = t1_v + 0.0546875F;
		tessellator.addVertexWithUV(x1, y2, z1, u2, v1);
		tessellator.addVertexWithUV(x2, y2, z1, u1, v1);
		tessellator.addVertexWithUV(x2, y1, z1, u1, v2);
		tessellator.addVertexWithUV(x1, y1, z1, u2, v2);

		setLightValue(tessellator, world, block, x, y, z - 1, 0.8F);
		u1 = t1_u + 0.0625000F;
		v1 = t1_v + 0.0000000F;
		u2 = t1_u + 0.0546875F;
		v2 = t1_v + 0.0546875F;
		tessellator.addVertexWithUV(x1, y2, z2, u1, v1);
		tessellator.addVertexWithUV(x1, y1, z2, u1, v2);
		tessellator.addVertexWithUV(x2, y1, z2, u2, v2);
		tessellator.addVertexWithUV(x2, y2, z2, u2, v1);

		// Cube #4

		x1 = x + 0.9375F;
		y1 = y + 0.1250F;
		z1 = z + 0.1250F;
		x2 = x + 0.0625F;
		y2 = y + 1.0000F;
		z2 = z + 0.0000F;

		setLightValue(tessellator, world, block, x, y + 1, z, 1.0F);
		u1 = t0_u + 0.0000000F;
		v1 = t0_v + 0.0039063F;
		u2 = t0_u + 0.0078125F;
		v2 = t0_v + 0.0585938F;
		tessellator.addVertexWithUV(x2, y2, z2, u2, v1);
		tessellator.addVertexWithUV(x2, y2, z1, u1, v1);
		tessellator.addVertexWithUV(x1, y2, z1, u1, v2);
		tessellator.addVertexWithUV(x1, y2, z2, u2, v2);

		setLightValue(tessellator, world, block, x, y - 1, z, 0.5F);
		u1 = t0_u + 0.0000000F;
		v1 = t0_v + 0.0039063F;
		u2 = t0_u + 0.0078125F;
		v2 = t0_v + 0.0585938F;
		tessellator.addVertexWithUV(x1, y1, z2, u2, v2);
		tessellator.addVertexWithUV(x1, y1, z1, u1, v2);
		tessellator.addVertexWithUV(x2, y1, z1, u1, v1);
		tessellator.addVertexWithUV(x2, y1, z2, u2, v1);

		setLightValue(tessellator, world, block, x + 1, y, z, 0.6F);
		u1 = t2_u + 0.0546875F;
		v1 = t2_v + 0.0000000F;
		u2 = t2_u + 0.0625000F;
		v2 = t2_v + 0.0546875F;
		tessellator.addVertexWithUV(x1, y2, z1, u1, v1);
		tessellator.addVertexWithUV(x1, y1, z1, u1, v2);
		tessellator.addVertexWithUV(x1, y1, z2, u2, v2);
		tessellator.addVertexWithUV(x1, y2, z2, u2, v1);

		setLightValue(tessellator, world, block, x - 1, y, z, 0.6F);
		u1 = t2_u + 0.0000000F;
		v1 = t2_v + 0.0000000F;
		u2 = t2_u + 0.0078125F;
		v2 = t2_v + 0.0546875F;
		tessellator.addVertexWithUV(x2, y2, z1, u2, v1);
		tessellator.addVertexWithUV(x2, y2, z2, u1, v1);
		tessellator.addVertexWithUV(x2, y1, z2, u1, v2);
		tessellator.addVertexWithUV(x2, y1, z1, u2, v2);

		setLightValue(tessellator, world, block, x, y, z + 1, 0.8F);
		u1 = t0_u + 0.0039063F;
		v1 = t0_v + 0.0039063F;
		u2 = t0_u + 0.0585938F;
		v2 = t0_v + 0.0585938F;
		tessellator.addVertexWithUV(x1, y2, z1, u2, v1);
		tessellator.addVertexWithUV(x2, y2, z1, u1, v1);
		tessellator.addVertexWithUV(x2, y1, z1, u1, v2);
		tessellator.addVertexWithUV(x1, y1, z1, u2, v2);

		setLightValue(tessellator, world, block, x, y, z - 1, 0.8F);
		u1 = t2_u + 0.0039063F;
		v1 = t2_v + 0.0000000F;
		u2 = t2_u + 0.0585938F;
		v2 = t2_v + 0.0546875F;
		tessellator.addVertexWithUV(x1, y2, z2, u1, v1);
		tessellator.addVertexWithUV(x1, y1, z2, u1, v2);
		tessellator.addVertexWithUV(x2, y1, z2, u2, v2);
		tessellator.addVertexWithUV(x2, y2, z2, u2, v1);

		// Cube #5

		x1 = x + 0.9375F;
		y1 = y + 0.1250F;
		z1 = z + 1.0000F;
		x2 = x + 0.0625F;
		y2 = y + 1.0000F;
		z2 = z + 0.8750F;

		setLightValue(tessellator, world, block, x, y + 1, z, 1.0F);
		u1 = t0_u + 0.0546875F;
		v1 = t0_v + 0.0039063F;
		u2 = t0_u + 0.0625000F;
		v2 = t0_v + 0.0585938F;
		tessellator.addVertexWithUV(x2, y2, z2, u2, v1);
		tessellator.addVertexWithUV(x2, y2, z1, u1, v1);
		tessellator.addVertexWithUV(x1, y2, z1, u1, v2);
		tessellator.addVertexWithUV(x1, y2, z2, u2, v2);

		setLightValue(tessellator, world, block, x, y - 1, z, 0.5F);
		u1 = t0_u + 0.0546875F;
		v1 = t0_v + 0.0039063F;
		u2 = t0_u + 0.0625000F;
		v2 = t0_v + 0.0585938F;
		tessellator.addVertexWithUV(x1, y1, z2, u2, v2);
		tessellator.addVertexWithUV(x1, y1, z1, u1, v2);
		tessellator.addVertexWithUV(x2, y1, z1, u1, v1);
		tessellator.addVertexWithUV(x2, y1, z2, u2, v1);

		setLightValue(tessellator, world, block, x + 1, y, z, 0.6F);
		u1 = t3_u + 0.0546875F;
		v1 = t3_v + 0.0000000F;
		u2 = t3_u + 0.0625000F;
		v2 = t3_v + 0.0546875F;
		tessellator.addVertexWithUV(x1, y2, z1, u1, v1);
		tessellator.addVertexWithUV(x1, y1, z1, u1, v2);
		tessellator.addVertexWithUV(x1, y1, z2, u2, v2);
		tessellator.addVertexWithUV(x1, y2, z2, u2, v1);

		setLightValue(tessellator, world, block, x - 1, y, z, 0.6F);
		u1 = t3_u + 0.0000000F;
		v1 = t3_v + 0.0000000F;
		u2 = t3_u + 0.0078125F;
		v2 = t3_v + 0.0546875F;
		tessellator.addVertexWithUV(x2, y2, z1, u2, v1);
		tessellator.addVertexWithUV(x2, y2, z2, u1, v1);
		tessellator.addVertexWithUV(x2, y1, z2, u1, v2);
		tessellator.addVertexWithUV(x2, y1, z1, u2, v2);

		setLightValue(tessellator, world, block, x, y, z + 1, 0.8F);
		u1 = t3_u + 0.0039063F;
		v1 = t3_v + 0.0000000F;
		u2 = t3_u + 0.0585938F;
		v2 = t3_v + 0.0546875F;
		tessellator.addVertexWithUV(x1, y2, z1, u2, v1);
		tessellator.addVertexWithUV(x2, y2, z1, u1, v1);
		tessellator.addVertexWithUV(x2, y1, z1, u1, v2);
		tessellator.addVertexWithUV(x1, y1, z1, u2, v2);

		setLightValue(tessellator, world, block, x, y, z - 1, 0.8F);
		u1 = t0_u + 0.0039063F;
		v1 = t0_v + 0.0039063F;
		u2 = t0_u + 0.0585938F;
		v2 = t0_v + 0.0585938F;
		tessellator.addVertexWithUV(x1, y2, z2, u1, v1);
		tessellator.addVertexWithUV(x1, y1, z2, u1, v2);
		tessellator.addVertexWithUV(x2, y1, z2, u2, v2);
		tessellator.addVertexWithUV(x2, y2, z2, u2, v1);

		// Cube #6

		x1 = x + 0.8750F;
		y1 = y + 0.1250F;
		z1 = z + 0.8750F;
		x2 = x + 0.1250F;
		y2 = y + 0.8125F;
		z2 = z + 0.1250F;

		setLightValue(tessellator, world, block, x, y + 1, z, 1.0F);
		u1 = t4_u + 0.0078125F;
		v1 = t4_v + 0.0078125F;
		u2 = t4_u + 0.0546875F;
		v2 = t4_v + 0.0546875F;
		tessellator.addVertexWithUV(x2, y2, z2, u2, v1);
		tessellator.addVertexWithUV(x2, y2, z1, u1, v1);
		tessellator.addVertexWithUV(x1, y2, z1, u1, v2);
		tessellator.addVertexWithUV(x1, y2, z2, u2, v2);

		setLightValue(tessellator, world, block, x, y - 1, z, 0.5F);
		u1 = t4_u + 0.0078125F;
		v1 = t4_v + 0.0078125F;
		u2 = t4_u + 0.0546875F;
		v2 = t4_v + 0.0546875F;
		tessellator.addVertexWithUV(x1, y1, z2, u2, v2);
		tessellator.addVertexWithUV(x1, y1, z1, u1, v2);
		tessellator.addVertexWithUV(x2, y1, z1, u1, v1);
		tessellator.addVertexWithUV(x2, y1, z2, u2, v1);

		setLightValue(tessellator, world, block, x + 1, y, z, 0.6F);
		u1 = t4_u + 0.0078125F;
		v1 = t4_v + 0.0078125F;
		u2 = t4_u + 0.0546875F;
		v2 = t4_v + 0.0507813F;
		tessellator.addVertexWithUV(x1, y2, z1, u1, v1);
		tessellator.addVertexWithUV(x1, y1, z1, u1, v2);
		tessellator.addVertexWithUV(x1, y1, z2, u2, v2);
		tessellator.addVertexWithUV(x1, y2, z2, u2, v1);

		setLightValue(tessellator, world, block, x - 1, y, z, 0.6F);
		u1 = t4_u + 0.0078125F;
		v1 = t4_v + 0.0078125F;
		u2 = t4_u + 0.0507813F;
		v2 = t4_v + 0.0546875F;
		tessellator.addVertexWithUV(x2, y2, z1, u2, v1);
		tessellator.addVertexWithUV(x2, y2, z2, u1, v1);
		tessellator.addVertexWithUV(x2, y1, z2, u1, v2);
		tessellator.addVertexWithUV(x2, y1, z1, u2, v2);

		setLightValue(tessellator, world, block, x, y, z + 1, 0.8F);
		u1 = t4_u + 0.0078125F;
		v1 = t4_v + 0.0078125F;
		u2 = t4_u + 0.0546875F;
		v2 = t4_v + 0.0507813F;
		tessellator.addVertexWithUV(x1, y2, z1, u2, v1);
		tessellator.addVertexWithUV(x2, y2, z1, u1, v1);
		tessellator.addVertexWithUV(x2, y1, z1, u1, v2);
		tessellator.addVertexWithUV(x1, y1, z1, u2, v2);

		setLightValue(tessellator, world, block, x, y, z - 1, 0.8F);
		u1 = t4_u + 0.0078125F;
		v1 = t4_v + 0.0078125F;
		u2 = t4_u + 0.0546875F;
		v2 = t4_v + 0.0507813F;
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
