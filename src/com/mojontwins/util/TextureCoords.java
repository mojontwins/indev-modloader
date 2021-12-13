package com.mojontwins.util;

public class TextureCoords {

	public TextureCoords() {
	}

	public static TupleInt index2AtlasCoords (int i) {
		return new TupleInt ((i & 0xf) << 4, i & 0xff0, 0, 0);
	}
	
	public static TupleFloat subIndex2TextureCoords (int i, int x, int y) {
		// i = 0 to 3, subtextures are arranged 2x2, 8.0F wide.
		// i    : 0 1 2 3
		// i & 1: 0 1 0 1
		// i >>1: 0 0 1 1
		float x1 = (float)x + (i & 1) * 8.0F;
		float x2 = x1 + 7.99F;
		float y1 = (float)y + (i >> 1) * 8.0F;
		float y2 = y1 + 7.99F;
		
		return new TupleFloat (x1 / 256.0F, y1 / 256.0F, x2 / 256.0F, y2 / 256.0F);
	}
	
	public static TupleInt orientationMeta2subIndexTuples (int meta) {
		if (meta == 2) {
			// Face to north
			return new TupleInt (3, 2, 0, 1);
		}
		
		if (meta == 3) {
			// Face to south
			return new TupleInt (2, 3, 1, 0);
		}
		
		if (meta == 4) {
			// Face to east
			return new TupleInt (1, 0, 3, 2);
		}
		
		if (meta == 5) {
			// Face to west
			return new TupleInt (0, 1, 2, 3);
		}
		
		// default
		return new TupleInt (3, 2, 0, 1);
	}
}
