package com.mojontwins.modloader;

import net.minecraft.client.renderer.Tessellator;

public class RenderDistiller {
	public static boolean renderBlock (int meta, float x, float y, float z, int ti_0, int ti_1) {
		if (meta == 2) {
			// Facing south (180 degrees)
			return renderMeta2(x, y, z, ti_0, ti_1);
		}

		if (meta == 4) {
			// Facing west (90 degrees)
			return renderMeta4(x, y, z, ti_0, ti_1);
		}

		if (meta == 5) {
			// Facing east (270 degrees)
			return renderMeta5(x, y, z, ti_0, ti_1);
		}

		// Facing north (default)
		return renderMeta3(x, y, z, ti_0, ti_1);
	}

	public static boolean renderMeta3 (float x, float y, float z, int ti_0, int ti_1) {
		Tessellator tessellator = Tessellator.instance;
		float x1, y1, z1, x2, y2, z2;
		float u1, v1, u2, v2;

		// Texture #0
		float t0_u = (float) ((ti_0 & 0x0f) << 4) / 256.0F;
		float t0_v = (float) (ti_0 & 0xff0) / 256.0F;

		// Texture #1
		float t1_u = (float) ((ti_1 & 0x0f) << 4) / 256.0F;
		float t1_v = (float) (ti_1 & 0xff0) / 256.0F;

		// Cube #0

		x1 = x + 0.0000F;
		y1 = y + 0.0000F;
		z1 = z + 0.0000F;
		x2 = x + 1.0000F;
		y2 = y + 0.3125F;
		z2 = z + 1.0000F;

		// Top face

		u1 = t1_u + 0.0000000F;
		v1 = t1_v + 0.0000000F;
		u2 = t1_u + 0.0625000F;
		v2 = t1_v + 0.0625000F;
		tessellator.addVertexWithUV(x2, y2, z2, u1, v1);
		tessellator.addVertexWithUV(x2, y2, z1, u1, v2);
		tessellator.addVertexWithUV(x1, y2, z1, u2, v2);
		tessellator.addVertexWithUV(x1, y2, z2, u2, v1);

		// Bottom face

		u1 = t1_u + 0.0000000F;
		v1 = t1_v + 0.0000000F;
		u2 = t1_u + 0.0039063F;
		v2 = t1_v + 0.0039063F;
		tessellator.addVertexWithUV(x1, y1, z2, u2, v1);
		tessellator.addVertexWithUV(x1, y1, z1, u2, v2);
		tessellator.addVertexWithUV(x2, y1, z1, u1, v2);
		tessellator.addVertexWithUV(x2, y1, z2, u1, v1);

		u1 = t0_u + 0.0000000F;
		v1 = t0_v + 0.0429688F;
		u2 = t0_u + 0.0625000F;
		v2 = t0_v + 0.0625000F;
		tessellator.addVertexWithUV(x1, y2, z1, u2, v1);
		tessellator.addVertexWithUV(x2, y2, z1, u1, v1);
		tessellator.addVertexWithUV(x2, y1, z1, u1, v2);
		tessellator.addVertexWithUV(x1, y1, z1, u2, v2);

		u1 = t0_u + 0.0000000F;
		v1 = t0_v + 0.0429688F;
		u2 = t0_u + 0.0625000F;
		v2 = t0_v + 0.0625000F;
		tessellator.addVertexWithUV(x1, y2, z2, u1, v1);
		tessellator.addVertexWithUV(x1, y1, z2, u1, v2);
		tessellator.addVertexWithUV(x2, y1, z2, u2, v2);
		tessellator.addVertexWithUV(x2, y2, z2, u2, v1);

		u1 = t0_u + 0.0000000F;
		v1 = t0_v + 0.0429688F;
		u2 = t0_u + 0.0625000F;
		v2 = t0_v + 0.0625000F;
		tessellator.addVertexWithUV(x2, y2, z1, u2, v1);
		tessellator.addVertexWithUV(x2, y2, z2, u1, v1);
		tessellator.addVertexWithUV(x2, y1, z2, u1, v2);
		tessellator.addVertexWithUV(x2, y1, z1, u2, v2);

		u1 = t0_u + 0.0000000F;
		v1 = t0_v + 0.0429688F;
		u2 = t0_u + 0.0625000F;
		v2 = t0_v + 0.0625000F;
		tessellator.addVertexWithUV(x1, y2, z1, u1, v1);
		tessellator.addVertexWithUV(x1, y1, z1, u1, v2);
		tessellator.addVertexWithUV(x1, y1, z2, u2, v2);
		tessellator.addVertexWithUV(x1, y2, z2, u2, v1);

		// Cube #1

		x1 = x + 0.5000F;
		y1 = y + 0.3125F;
		z1 = z + 0.0000F;
		x2 = x + 0.5000F;
		y2 = y + 1.0625F;
		z2 = z + 1.0000F;

		u1 = t0_u + 0.0625000F;
		v1 = t0_v + 0.0000000F;
		u2 = t0_u + 0.0000000F;
		v2 = t0_v + 0.0429688F;
		tessellator.addVertexWithUV(x2, y2, z1, u2, v1);
		tessellator.addVertexWithUV(x2, y2, z2, u1, v1);
		tessellator.addVertexWithUV(x2, y1, z2, u1, v2);
		tessellator.addVertexWithUV(x2, y1, z1, u2, v2);

		u1 = t0_u + 0.0000000F;
		v1 = t0_v + 0.0000000F;
		u2 = t0_u + 0.0625000F;
		v2 = t0_v + 0.0429688F;
		tessellator.addVertexWithUV(x1, y2, z1, u1, v1);
		tessellator.addVertexWithUV(x1, y1, z1, u1, v2);
		tessellator.addVertexWithUV(x1, y1, z2, u2, v2);
		tessellator.addVertexWithUV(x1, y2, z2, u2, v1);

		// Cube #2

		x1 = x + 0.2500F;
		y1 = y + 0.3125F;
		z1 = z + 0.2500F;
		x2 = x + 0.7500F;
		y2 = y + 0.5000F;
		z2 = z + 0.2500F;

		u1 = t0_u + 0.0000000F;
		v1 = t0_v + 0.0312500F;
		u2 = t0_u + 0.0312500F;
		v2 = t0_v + 0.0429688F;
		tessellator.addVertexWithUV(x1, y2, z1, u2, v1);
		tessellator.addVertexWithUV(x2, y2, z1, u1, v1);
		tessellator.addVertexWithUV(x2, y1, z1, u1, v2);
		tessellator.addVertexWithUV(x1, y1, z1, u2, v2);

		u1 = t0_u + 0.0000000F;
		v1 = t0_v + 0.0312500F;
		u2 = t0_u + 0.0312500F;
		v2 = t0_v + 0.0429688F;
		tessellator.addVertexWithUV(x1, y2, z2, u1, v1);
		tessellator.addVertexWithUV(x1, y1, z2, u1, v2);
		tessellator.addVertexWithUV(x2, y1, z2, u2, v2);
		tessellator.addVertexWithUV(x2, y2, z2, u2, v1);

		// Cube #3

		x1 = x + 0.4375F;
		y1 = y + 0.3125F;
		z1 = z + 0.6250F;
		x2 = x + 0.5625F;
		y2 = y + 0.7500F;
		z2 = z + 0.7500F;

		// Top face

		u1 = t0_u + 0.0390625F;
		v1 = t0_v + 0.0156250F;
		u2 = t0_u + 0.0468750F;
		v2 = t0_v + 0.0234375F;
		tessellator.addVertexWithUV(x2, y2, z2, u1, v1);
		tessellator.addVertexWithUV(x2, y2, z1, u1, v2);
		tessellator.addVertexWithUV(x1, y2, z1, u2, v2);
		tessellator.addVertexWithUV(x1, y2, z2, u2, v1);

		u1 = t0_u + 0.0390625F;
		v1 = t0_v + 0.0156250F;
		u2 = t0_u + 0.0468750F;
		v2 = t0_v + 0.0429688F;
		tessellator.addVertexWithUV(x1, y2, z1, u2, v1);
		tessellator.addVertexWithUV(x2, y2, z1, u1, v1);
		tessellator.addVertexWithUV(x2, y1, z1, u1, v2);
		tessellator.addVertexWithUV(x1, y1, z1, u2, v2);

		u1 = t0_u + 0.0390625F;
		v1 = t0_v + 0.0156250F;
		u2 = t0_u + 0.0468750F;
		v2 = t0_v + 0.0429688F;
		tessellator.addVertexWithUV(x1, y2, z2, u1, v1);
		tessellator.addVertexWithUV(x1, y1, z2, u1, v2);
		tessellator.addVertexWithUV(x2, y1, z2, u2, v2);
		tessellator.addVertexWithUV(x2, y2, z2, u2, v1);

		u1 = t0_u + 0.0390625F;
		v1 = t0_v + 0.0156250F;
		u2 = t0_u + 0.0468750F;
		v2 = t0_v + 0.0429688F;
		tessellator.addVertexWithUV(x2, y2, z1, u2, v1);
		tessellator.addVertexWithUV(x2, y2, z2, u1, v1);
		tessellator.addVertexWithUV(x2, y1, z2, u1, v2);
		tessellator.addVertexWithUV(x2, y1, z1, u2, v2);

		u1 = t0_u + 0.0390625F;
		v1 = t0_v + 0.0156250F;
		u2 = t0_u + 0.0468750F;
		v2 = t0_v + 0.0429688F;
		tessellator.addVertexWithUV(x1, y2, z1, u1, v1);
		tessellator.addVertexWithUV(x1, y1, z1, u1, v2);
		tessellator.addVertexWithUV(x1, y1, z2, u2, v2);
		tessellator.addVertexWithUV(x1, y2, z2, u2, v1);

		return true;
	}
	public static boolean renderMeta2 (float x, float y, float z, int ti_0, int ti_1) {
		Tessellator tessellator = Tessellator.instance;
		float x1, y1, z1, x2, y2, z2;
		float u1, v1, u2, v2;

		// Texture #0
		float t0_u = (float) ((ti_0 & 0x0f) << 4) / 256.0F;
		float t0_v = (float) (ti_0 & 0xff0) / 256.0F;

		// Texture #1
		float t1_u = (float) ((ti_1 & 0x0f) << 4) / 256.0F;
		float t1_v = (float) (ti_1 & 0xff0) / 256.0F;

		// Cube #0

		x1 = x + 1.0000F;
		y1 = y + 0.0000F;
		z1 = z + 1.0000F;
		x2 = x + 0.0000F;
		y2 = y + 0.3125F;
		z2 = z + 0.0000F;

		// Top face

		u1 = t1_u + 0.0000000F;
		v1 = t1_v + 0.0000000F;
		u2 = t1_u + 0.0625000F;
		v2 = t1_v + 0.0625000F;
		tessellator.addVertexWithUV(x2, y2, z2, u1, v1);
		tessellator.addVertexWithUV(x2, y2, z1, u1, v2);
		tessellator.addVertexWithUV(x1, y2, z1, u2, v2);
		tessellator.addVertexWithUV(x1, y2, z2, u2, v1);

		// Bottom face

		u1 = t1_u + 0.0000000F;
		v1 = t1_v + 0.0000000F;
		u2 = t1_u + 0.0039063F;
		v2 = t1_v + 0.0039063F;
		tessellator.addVertexWithUV(x1, y1, z2, u2, v1);
		tessellator.addVertexWithUV(x1, y1, z1, u2, v2);
		tessellator.addVertexWithUV(x2, y1, z1, u1, v2);
		tessellator.addVertexWithUV(x2, y1, z2, u1, v1);

		u1 = t0_u + 0.0000000F;
		v1 = t0_v + 0.0429688F;
		u2 = t0_u + 0.0625000F;
		v2 = t0_v + 0.0625000F;
		tessellator.addVertexWithUV(x1, y2, z1, u2, v1);
		tessellator.addVertexWithUV(x2, y2, z1, u1, v1);
		tessellator.addVertexWithUV(x2, y1, z1, u1, v2);
		tessellator.addVertexWithUV(x1, y1, z1, u2, v2);

		u1 = t0_u + 0.0000000F;
		v1 = t0_v + 0.0429688F;
		u2 = t0_u + 0.0625000F;
		v2 = t0_v + 0.0625000F;
		tessellator.addVertexWithUV(x1, y2, z2, u1, v1);
		tessellator.addVertexWithUV(x1, y1, z2, u1, v2);
		tessellator.addVertexWithUV(x2, y1, z2, u2, v2);
		tessellator.addVertexWithUV(x2, y2, z2, u2, v1);

		u1 = t0_u + 0.0000000F;
		v1 = t0_v + 0.0429688F;
		u2 = t0_u + 0.0625000F;
		v2 = t0_v + 0.0625000F;
		tessellator.addVertexWithUV(x2, y2, z1, u2, v1);
		tessellator.addVertexWithUV(x2, y2, z2, u1, v1);
		tessellator.addVertexWithUV(x2, y1, z2, u1, v2);
		tessellator.addVertexWithUV(x2, y1, z1, u2, v2);

		u1 = t0_u + 0.0000000F;
		v1 = t0_v + 0.0429688F;
		u2 = t0_u + 0.0625000F;
		v2 = t0_v + 0.0625000F;
		tessellator.addVertexWithUV(x1, y2, z1, u1, v1);
		tessellator.addVertexWithUV(x1, y1, z1, u1, v2);
		tessellator.addVertexWithUV(x1, y1, z2, u2, v2);
		tessellator.addVertexWithUV(x1, y2, z2, u2, v1);

		// Cube #1

		x1 = x + 0.5000F;
		y1 = y + 0.3125F;
		z1 = z + 1.0000F;
		x2 = x + 0.5000F;
		y2 = y + 1.0625F;
		z2 = z + 0.0000F;

		u1 = t0_u + 0.0625000F;
		v1 = t0_v + 0.0000000F;
		u2 = t0_u + 0.0000000F;
		v2 = t0_v + 0.0429688F;
		tessellator.addVertexWithUV(x2, y2, z1, u2, v1);
		tessellator.addVertexWithUV(x2, y2, z2, u1, v1);
		tessellator.addVertexWithUV(x2, y1, z2, u1, v2);
		tessellator.addVertexWithUV(x2, y1, z1, u2, v2);

		u1 = t0_u + 0.0000000F;
		v1 = t0_v + 0.0000000F;
		u2 = t0_u + 0.0625000F;
		v2 = t0_v + 0.0429688F;
		tessellator.addVertexWithUV(x1, y2, z1, u1, v1);
		tessellator.addVertexWithUV(x1, y1, z1, u1, v2);
		tessellator.addVertexWithUV(x1, y1, z2, u2, v2);
		tessellator.addVertexWithUV(x1, y2, z2, u2, v1);

		// Cube #2

		x1 = x + 0.7500F;
		y1 = y + 0.3125F;
		z1 = z + 0.7500F;
		x2 = x + 0.2500F;
		y2 = y + 0.5000F;
		z2 = z + 0.7500F;

		u1 = t0_u + 0.0000000F;
		v1 = t0_v + 0.0312500F;
		u2 = t0_u + 0.0312500F;
		v2 = t0_v + 0.0429688F;
		tessellator.addVertexWithUV(x1, y2, z1, u2, v1);
		tessellator.addVertexWithUV(x2, y2, z1, u1, v1);
		tessellator.addVertexWithUV(x2, y1, z1, u1, v2);
		tessellator.addVertexWithUV(x1, y1, z1, u2, v2);

		u1 = t0_u + 0.0000000F;
		v1 = t0_v + 0.0312500F;
		u2 = t0_u + 0.0312500F;
		v2 = t0_v + 0.0429688F;
		tessellator.addVertexWithUV(x1, y2, z2, u1, v1);
		tessellator.addVertexWithUV(x1, y1, z2, u1, v2);
		tessellator.addVertexWithUV(x2, y1, z2, u2, v2);
		tessellator.addVertexWithUV(x2, y2, z2, u2, v1);

		// Cube #3

		x1 = x + 0.5625F;
		y1 = y + 0.3125F;
		z1 = z + 0.3750F;
		x2 = x + 0.4375F;
		y2 = y + 0.7500F;
		z2 = z + 0.2500F;

		// Top face

		u1 = t0_u + 0.0390625F;
		v1 = t0_v + 0.0156250F;
		u2 = t0_u + 0.0468750F;
		v2 = t0_v + 0.0234375F;
		tessellator.addVertexWithUV(x2, y2, z2, u1, v1);
		tessellator.addVertexWithUV(x2, y2, z1, u1, v2);
		tessellator.addVertexWithUV(x1, y2, z1, u2, v2);
		tessellator.addVertexWithUV(x1, y2, z2, u2, v1);

		u1 = t0_u + 0.0390625F;
		v1 = t0_v + 0.0156250F;
		u2 = t0_u + 0.0468750F;
		v2 = t0_v + 0.0429688F;
		tessellator.addVertexWithUV(x1, y2, z1, u2, v1);
		tessellator.addVertexWithUV(x2, y2, z1, u1, v1);
		tessellator.addVertexWithUV(x2, y1, z1, u1, v2);
		tessellator.addVertexWithUV(x1, y1, z1, u2, v2);

		u1 = t0_u + 0.0390625F;
		v1 = t0_v + 0.0156250F;
		u2 = t0_u + 0.0468750F;
		v2 = t0_v + 0.0429688F;
		tessellator.addVertexWithUV(x1, y2, z2, u1, v1);
		tessellator.addVertexWithUV(x1, y1, z2, u1, v2);
		tessellator.addVertexWithUV(x2, y1, z2, u2, v2);
		tessellator.addVertexWithUV(x2, y2, z2, u2, v1);

		u1 = t0_u + 0.0390625F;
		v1 = t0_v + 0.0156250F;
		u2 = t0_u + 0.0468750F;
		v2 = t0_v + 0.0429688F;
		tessellator.addVertexWithUV(x2, y2, z1, u2, v1);
		tessellator.addVertexWithUV(x2, y2, z2, u1, v1);
		tessellator.addVertexWithUV(x2, y1, z2, u1, v2);
		tessellator.addVertexWithUV(x2, y1, z1, u2, v2);

		u1 = t0_u + 0.0390625F;
		v1 = t0_v + 0.0156250F;
		u2 = t0_u + 0.0468750F;
		v2 = t0_v + 0.0429688F;
		tessellator.addVertexWithUV(x1, y2, z1, u1, v1);
		tessellator.addVertexWithUV(x1, y1, z1, u1, v2);
		tessellator.addVertexWithUV(x1, y1, z2, u2, v2);
		tessellator.addVertexWithUV(x1, y2, z2, u2, v1);

		return true;
	}
	public static boolean renderMeta4 (float x, float y, float z, int ti_0, int ti_1) {
		Tessellator tessellator = Tessellator.instance;
		float x1, y1, z1, x2, y2, z2;
		float u1, v1, u2, v2;

		// Texture #0
		float t0_u = (float) ((ti_0 & 0x0f) << 4) / 256.0F;
		float t0_v = (float) (ti_0 & 0xff0) / 256.0F;

		// Texture #1
		float t1_u = (float) ((ti_1 & 0x0f) << 4) / 256.0F;
		float t1_v = (float) (ti_1 & 0xff0) / 256.0F;

		// Cube #0

		x1 = x + 1.0000F;
		y1 = y + 0.0000F;
		z1 = z + 1.0000F;
		x2 = x + 0.0000F;
		y2 = y + 0.3125F;
		z2 = z + 0.0000F;

		// Top face

		u1 = t1_u + 0.0000000F;
		v1 = t1_v + 0.0000000F;
		u2 = t1_u + 0.0625000F;
		v2 = t1_v + 0.0625000F;
		tessellator.addVertexWithUV(x2, y2, z2, u1, v1);
		tessellator.addVertexWithUV(x2, y2, z1, u2, v1);
		tessellator.addVertexWithUV(x1, y2, z1, u2, v2);
		tessellator.addVertexWithUV(x1, y2, z2, u1, v2);

		// Bottom face

		u1 = t1_u + 0.0000000F;
		v1 = t1_v + 0.0000000F;
		u2 = t1_u + 0.0039063F;
		v2 = t1_v + 0.0039063F;
		tessellator.addVertexWithUV(x1, y1, z2, u1, v2);
		tessellator.addVertexWithUV(x1, y1, z1, u2, v2);
		tessellator.addVertexWithUV(x2, y1, z1, u2, v1);
		tessellator.addVertexWithUV(x2, y1, z2, u1, v1);

		u1 = t0_u + 0.0000000F;
		v1 = t0_v + 0.0429688F;
		u2 = t0_u + 0.0625000F;
		v2 = t0_v + 0.0625000F;
		tessellator.addVertexWithUV(x1, y2, z1, u1, v1);
		tessellator.addVertexWithUV(x1, y1, z1, u1, v2);
		tessellator.addVertexWithUV(x1, y1, z2, u2, v2);
		tessellator.addVertexWithUV(x1, y2, z2, u2, v1);

		u1 = t0_u + 0.0000000F;
		v1 = t0_v + 0.0429688F;
		u2 = t0_u + 0.0625000F;
		v2 = t0_v + 0.0625000F;
		tessellator.addVertexWithUV(x2, y2, z1, u2, v1);
		tessellator.addVertexWithUV(x2, y2, z2, u1, v1);
		tessellator.addVertexWithUV(x2, y1, z2, u1, v2);
		tessellator.addVertexWithUV(x2, y1, z1, u2, v2);

		u1 = t0_u + 0.0000000F;
		v1 = t0_v + 0.0429688F;
		u2 = t0_u + 0.0625000F;
		v2 = t0_v + 0.0625000F;
		tessellator.addVertexWithUV(x1, y2, z1, u2, v1);
		tessellator.addVertexWithUV(x2, y2, z1, u1, v1);
		tessellator.addVertexWithUV(x2, y1, z1, u1, v2);
		tessellator.addVertexWithUV(x1, y1, z1, u2, v2);

		u1 = t0_u + 0.0000000F;
		v1 = t0_v + 0.0429688F;
		u2 = t0_u + 0.0625000F;
		v2 = t0_v + 0.0625000F;
		tessellator.addVertexWithUV(x1, y2, z2, u1, v1);
		tessellator.addVertexWithUV(x1, y1, z2, u1, v2);
		tessellator.addVertexWithUV(x2, y1, z2, u2, v2);
		tessellator.addVertexWithUV(x2, y2, z2, u2, v1);

		// Cube #1

		x1 = x + 1.0000F;
		y1 = y + 0.3125F;
		z1 = z + 0.5000F;
		x2 = x + 0.0000F;
		y2 = y + 1.0625F;
		z2 = z + 0.5000F;

		u1 = t0_u + 0.0000000F;
		v1 = t0_v + 0.0000000F;
		u2 = t0_u + 0.0625000F;
		v2 = t0_v + 0.0429688F;
		tessellator.addVertexWithUV(x1, y2, z1, u2, v1);
		tessellator.addVertexWithUV(x2, y2, z1, u1, v1);
		tessellator.addVertexWithUV(x2, y1, z1, u1, v2);
		tessellator.addVertexWithUV(x1, y1, z1, u2, v2);

		u1 = t0_u + 0.0625000F;
		v1 = t0_v + 0.0000000F;
		u2 = t0_u + 0.0000000F;
		v2 = t0_v + 0.0429688F;
		tessellator.addVertexWithUV(x1, y2, z2, u1, v1);
		tessellator.addVertexWithUV(x1, y1, z2, u1, v2);
		tessellator.addVertexWithUV(x2, y1, z2, u2, v2);
		tessellator.addVertexWithUV(x2, y2, z2, u2, v1);

		// Cube #2

		x1 = x + 0.2500F;
		y1 = y + 0.3125F;
		z1 = z + 0.7500F;
		x2 = x + 0.2500F;
		y2 = y + 0.5000F;
		z2 = z + 0.2500F;

		u1 = t0_u + 0.0000000F;
		v1 = t0_v + 0.0312500F;
		u2 = t0_u + 0.0312500F;
		v2 = t0_v + 0.0429688F;
		tessellator.addVertexWithUV(x1, y2, z1, u1, v1);
		tessellator.addVertexWithUV(x1, y1, z1, u1, v2);
		tessellator.addVertexWithUV(x1, y1, z2, u2, v2);
		tessellator.addVertexWithUV(x1, y2, z2, u2, v1);

		u1 = t0_u + 0.0000000F;
		v1 = t0_v + 0.0312500F;
		u2 = t0_u + 0.0312500F;
		v2 = t0_v + 0.0429688F;
		tessellator.addVertexWithUV(x2, y2, z1, u2, v1);
		tessellator.addVertexWithUV(x2, y2, z2, u1, v1);
		tessellator.addVertexWithUV(x2, y1, z2, u1, v2);
		tessellator.addVertexWithUV(x2, y1, z1, u2, v2);

		// Cube #3

		x1 = x + 0.7500F;
		y1 = y + 0.3125F;
		z1 = z + 0.5625F;
		x2 = x + 0.6250F;
		y2 = y + 0.7500F;
		z2 = z + 0.4375F;

		// Top face

		u1 = t0_u + 0.0390625F;
		v1 = t0_v + 0.0156250F;
		u2 = t0_u + 0.0468750F;
		v2 = t0_v + 0.0234375F;
		tessellator.addVertexWithUV(x2, y2, z2, u1, v1);
		tessellator.addVertexWithUV(x2, y2, z1, u2, v1);
		tessellator.addVertexWithUV(x1, y2, z1, u2, v2);
		tessellator.addVertexWithUV(x1, y2, z2, u1, v2);

		u1 = t0_u + 0.0390625F;
		v1 = t0_v + 0.0156250F;
		u2 = t0_u + 0.0468750F;
		v2 = t0_v + 0.0429688F;
		tessellator.addVertexWithUV(x1, y2, z1, u1, v1);
		tessellator.addVertexWithUV(x1, y1, z1, u1, v2);
		tessellator.addVertexWithUV(x1, y1, z2, u2, v2);
		tessellator.addVertexWithUV(x1, y2, z2, u2, v1);

		u1 = t0_u + 0.0390625F;
		v1 = t0_v + 0.0156250F;
		u2 = t0_u + 0.0468750F;
		v2 = t0_v + 0.0429688F;
		tessellator.addVertexWithUV(x2, y2, z1, u2, v1);
		tessellator.addVertexWithUV(x2, y2, z2, u1, v1);
		tessellator.addVertexWithUV(x2, y1, z2, u1, v2);
		tessellator.addVertexWithUV(x2, y1, z1, u2, v2);

		u1 = t0_u + 0.0390625F;
		v1 = t0_v + 0.0156250F;
		u2 = t0_u + 0.0468750F;
		v2 = t0_v + 0.0429688F;
		tessellator.addVertexWithUV(x1, y2, z1, u2, v1);
		tessellator.addVertexWithUV(x2, y2, z1, u1, v1);
		tessellator.addVertexWithUV(x2, y1, z1, u1, v2);
		tessellator.addVertexWithUV(x1, y1, z1, u2, v2);

		u1 = t0_u + 0.0390625F;
		v1 = t0_v + 0.0156250F;
		u2 = t0_u + 0.0468750F;
		v2 = t0_v + 0.0429688F;
		tessellator.addVertexWithUV(x1, y2, z2, u1, v1);
		tessellator.addVertexWithUV(x1, y1, z2, u1, v2);
		tessellator.addVertexWithUV(x2, y1, z2, u2, v2);
		tessellator.addVertexWithUV(x2, y2, z2, u2, v1);

		return true;
	}
	public static boolean renderMeta5 (float x, float y, float z, int ti_0, int ti_1) {
		Tessellator tessellator = Tessellator.instance;
		float x1, y1, z1, x2, y2, z2;
		float u1, v1, u2, v2;

		// Texture #0
		float t0_u = (float) ((ti_0 & 0x0f) << 4) / 256.0F;
		float t0_v = (float) (ti_0 & 0xff0) / 256.0F;

		// Texture #1
		float t1_u = (float) ((ti_1 & 0x0f) << 4) / 256.0F;
		float t1_v = (float) (ti_1 & 0xff0) / 256.0F;

		// Cube #0

		x1 = x + 1.0000F;
		y1 = y + 0.0000F;
		z1 = z + 1.0000F;
		x2 = x + 0.0000F;
		y2 = y + 0.3125F;
		z2 = z + 0.0000F;

		// Top face

		u1 = t1_u + 0.0000000F;
		v1 = t1_v + 0.0000000F;
		u2 = t1_u + 0.0625000F;
		v2 = t1_v + 0.0625000F;
		tessellator.addVertexWithUV(x2, y2, z2, u2, v1);
		tessellator.addVertexWithUV(x2, y2, z1, u1, v1);
		tessellator.addVertexWithUV(x1, y2, z1, u1, v2);
		tessellator.addVertexWithUV(x1, y2, z2, u2, v2);

		// Bottom face

		u1 = t1_u + 0.0000000F;
		v1 = t1_v + 0.0000000F;
		u2 = t1_u + 0.0039063F;
		v2 = t1_v + 0.0039063F;
		tessellator.addVertexWithUV(x1, y1, z2, u2, v2);
		tessellator.addVertexWithUV(x1, y1, z1, u1, v2);
		tessellator.addVertexWithUV(x2, y1, z1, u1, v1);
		tessellator.addVertexWithUV(x2, y1, z2, u2, v1);

		u1 = t0_u + 0.0000000F;
		v1 = t0_v + 0.0429688F;
		u2 = t0_u + 0.0625000F;
		v2 = t0_v + 0.0625000F;
		tessellator.addVertexWithUV(x1, y2, z1, u1, v1);
		tessellator.addVertexWithUV(x1, y1, z1, u1, v2);
		tessellator.addVertexWithUV(x1, y1, z2, u2, v2);
		tessellator.addVertexWithUV(x1, y2, z2, u2, v1);

		u1 = t0_u + 0.0000000F;
		v1 = t0_v + 0.0429688F;
		u2 = t0_u + 0.0625000F;
		v2 = t0_v + 0.0625000F;
		tessellator.addVertexWithUV(x2, y2, z1, u2, v1);
		tessellator.addVertexWithUV(x2, y2, z2, u1, v1);
		tessellator.addVertexWithUV(x2, y1, z2, u1, v2);
		tessellator.addVertexWithUV(x2, y1, z1, u2, v2);

		u1 = t0_u + 0.0000000F;
		v1 = t0_v + 0.0429688F;
		u2 = t0_u + 0.0625000F;
		v2 = t0_v + 0.0625000F;
		tessellator.addVertexWithUV(x1, y2, z1, u2, v1);
		tessellator.addVertexWithUV(x2, y2, z1, u1, v1);
		tessellator.addVertexWithUV(x2, y1, z1, u1, v2);
		tessellator.addVertexWithUV(x1, y1, z1, u2, v2);

		u1 = t0_u + 0.0000000F;
		v1 = t0_v + 0.0429688F;
		u2 = t0_u + 0.0625000F;
		v2 = t0_v + 0.0625000F;
		tessellator.addVertexWithUV(x1, y2, z2, u1, v1);
		tessellator.addVertexWithUV(x1, y1, z2, u1, v2);
		tessellator.addVertexWithUV(x2, y1, z2, u2, v2);
		tessellator.addVertexWithUV(x2, y2, z2, u2, v1);

		// Cube #1

		x1 = x + 1.0000F;
		y1 = y + 0.3125F;
		z1 = z + 0.5000F;
		x2 = x + 0.0000F;
		y2 = y + 1.0625F;
		z2 = z + 0.5000F;

		u1 = t0_u + 0.0625000F;
		v1 = t0_v + 0.0000000F;
		u2 = t0_u + 0.0000000F;
		v2 = t0_v + 0.0429688F;
		tessellator.addVertexWithUV(x1, y2, z1, u2, v1);
		tessellator.addVertexWithUV(x2, y2, z1, u1, v1);
		tessellator.addVertexWithUV(x2, y1, z1, u1, v2);
		tessellator.addVertexWithUV(x1, y1, z1, u2, v2);

		u1 = t0_u + 0.0000000F;
		v1 = t0_v + 0.0000000F;
		u2 = t0_u + 0.0625000F;
		v2 = t0_v + 0.0429688F;
		tessellator.addVertexWithUV(x1, y2, z2, u1, v1);
		tessellator.addVertexWithUV(x1, y1, z2, u1, v2);
		tessellator.addVertexWithUV(x2, y1, z2, u2, v2);
		tessellator.addVertexWithUV(x2, y2, z2, u2, v1);

		// Cube #2

		x1 = x + 0.7500F;
		y1 = y + 0.3125F;
		z1 = z + 0.7500F;
		x2 = x + 0.7500F;
		y2 = y + 0.5000F;
		z2 = z + 0.2500F;

		u1 = t0_u + 0.0000000F;
		v1 = t0_v + 0.0312500F;
		u2 = t0_u + 0.0312500F;
		v2 = t0_v + 0.0429688F;
		tessellator.addVertexWithUV(x1, y2, z1, u1, v1);
		tessellator.addVertexWithUV(x1, y1, z1, u1, v2);
		tessellator.addVertexWithUV(x1, y1, z2, u2, v2);
		tessellator.addVertexWithUV(x1, y2, z2, u2, v1);

		u1 = t0_u + 0.0000000F;
		v1 = t0_v + 0.0312500F;
		u2 = t0_u + 0.0312500F;
		v2 = t0_v + 0.0429688F;
		tessellator.addVertexWithUV(x2, y2, z1, u2, v1);
		tessellator.addVertexWithUV(x2, y2, z2, u1, v1);
		tessellator.addVertexWithUV(x2, y1, z2, u1, v2);
		tessellator.addVertexWithUV(x2, y1, z1, u2, v2);

		// Cube #3

		x1 = x + 0.3750F;
		y1 = y + 0.3125F;
		z1 = z + 0.5625F;
		x2 = x + 0.2500F;
		y2 = y + 0.7500F;
		z2 = z + 0.4375F;

		// Top face

		u1 = t0_u + 0.0390625F;
		v1 = t0_v + 0.0156250F;
		u2 = t0_u + 0.0468750F;
		v2 = t0_v + 0.0234375F;
		tessellator.addVertexWithUV(x2, y2, z2, u2, v1);
		tessellator.addVertexWithUV(x2, y2, z1, u1, v1);
		tessellator.addVertexWithUV(x1, y2, z1, u1, v2);
		tessellator.addVertexWithUV(x1, y2, z2, u2, v2);

		u1 = t0_u + 0.0390625F;
		v1 = t0_v + 0.0156250F;
		u2 = t0_u + 0.0468750F;
		v2 = t0_v + 0.0429688F;
		tessellator.addVertexWithUV(x1, y2, z1, u1, v1);
		tessellator.addVertexWithUV(x1, y1, z1, u1, v2);
		tessellator.addVertexWithUV(x1, y1, z2, u2, v2);
		tessellator.addVertexWithUV(x1, y2, z2, u2, v1);

		u1 = t0_u + 0.0390625F;
		v1 = t0_v + 0.0156250F;
		u2 = t0_u + 0.0468750F;
		v2 = t0_v + 0.0429688F;
		tessellator.addVertexWithUV(x2, y2, z1, u2, v1);
		tessellator.addVertexWithUV(x2, y2, z2, u1, v1);
		tessellator.addVertexWithUV(x2, y1, z2, u1, v2);
		tessellator.addVertexWithUV(x2, y1, z1, u2, v2);

		u1 = t0_u + 0.0390625F;
		v1 = t0_v + 0.0156250F;
		u2 = t0_u + 0.0468750F;
		v2 = t0_v + 0.0429688F;
		tessellator.addVertexWithUV(x1, y2, z1, u2, v1);
		tessellator.addVertexWithUV(x2, y2, z1, u1, v1);
		tessellator.addVertexWithUV(x2, y1, z1, u1, v2);
		tessellator.addVertexWithUV(x1, y1, z1, u2, v2);

		u1 = t0_u + 0.0390625F;
		v1 = t0_v + 0.0156250F;
		u2 = t0_u + 0.0468750F;
		v2 = t0_v + 0.0429688F;
		tessellator.addVertexWithUV(x1, y2, z2, u1, v1);
		tessellator.addVertexWithUV(x1, y1, z2, u1, v2);
		tessellator.addVertexWithUV(x2, y1, z2, u2, v2);
		tessellator.addVertexWithUV(x2, y2, z2, u2, v1);

		return true;
	}
}
