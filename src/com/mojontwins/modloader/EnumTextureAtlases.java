package com.mojontwins.modloader;

public enum EnumTextureAtlases {
	TERRAIN {
		public String toString () { return "/terrain.png"; }
	}, 
	ITEMS {
		public String toString () { return "/gui/items.png"; }
	}
}
