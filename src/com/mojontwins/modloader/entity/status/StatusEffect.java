package com.mojontwins.modloader.entity.status;

import net.minecraft.game.entity.EntityLiving;

public class StatusEffect {
	public int statusID; 	// Status ID of this effect.
	public int duration; 	// Time before the effect fades out.
	public int amplifier; 	// An effect amplifier. 
	
	public StatusEffect(int statusID, int duration, int amplifier) {
		this.statusID = statusID;
		this.duration = duration;
		this.amplifier = amplifier;
	}

	public StatusEffect(StatusEffect statusEffect) {
		this.statusID = statusEffect.statusID;
		this.duration = statusEffect.duration;
		this.amplifier = statusEffect.amplifier;
	}
	
	/*
	 * This method is to be called from EntityLiving's `onEntityUpdate`
	 * returns false when the effect has finished.
	 */
	public boolean onUpdate (EntityLiving entityLiving) {
		if (duration > 0) {
			Status status = Status.statusTypes [statusID];
			if (status.isReady(duration, amplifier)) {
				status.performEffect(entityLiving, amplifier);
			}
			duration --;
			return true;
		} return false;
	}
	
	public void combine (StatusEffect statusEffect) {
		// Update this with statusEffect's values.
		this.duration += statusEffect.duration;
		if (this.amplifier < statusEffect.amplifier) this.amplifier = statusEffect.amplifier;
	}
	
	public boolean equals (Object obj) {
		if (!(obj instanceof StatusEffect)) return false;
		
		StatusEffect statusEffect = (StatusEffect) obj;
		return statusEffect.statusID == this.statusID && statusEffect.duration == this.duration && statusEffect.amplifier == this.amplifier;
	}
}
