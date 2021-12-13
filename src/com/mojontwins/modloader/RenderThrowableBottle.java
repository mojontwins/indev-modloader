package com.mojontwins.modloader;

import net.minecraft.game.entity.Entity;

public class RenderThrowableBottle extends ModRenderThrowableSimple {
	public RenderThrowableBottle() {
	}
	
	/*
	 * `ModRenderThrowableSimple` calls this method to get the icon index to draw.
	 */
	public int getItemIconIndex(Entity entity) {
		return ((EntityThrowableBottle) entity).itemBottle.getIconIndex();
	}
}
