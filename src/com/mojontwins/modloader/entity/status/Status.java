package com.mojontwins.modloader.entity.status;

import net.minecraft.game.entity.EntityLiving;

public class Status {
	public String name;
	public static final Status statusTypes [] = new Status [256];
	public static int latestId = 0;
	public boolean isBadEffect;
	public int id;
	
	public Status(int id, boolean isBadEffect) {
		this.id = id;
		this.isBadEffect = isBadEffect;
		statusTypes [id] = this;
	}
	
	public static int getNewStatusId () {
		return (latestId < 256) ? latestId ++ : -1;		
	}

    public Status setName(String name) {
    	this.name = name;
    	return this;
    }
    
    /*
     * Override this method with your own to perform the effect
     */
    public void performEffect (EntityLiving entityLiving, int amplifier) {    	
    }
    
    /*
     *  Override this method with your own to decide if the effect is ready
     */
    public boolean isReady (int tick, int amplifier) {    	
    	return true;
    }
    
    /*
     *  Override this method with your own to decide if the effect is applicable to this entity
     */
    public boolean isApplicableTo (EntityLiving entityLiving) {
    	return true;
    }
}
