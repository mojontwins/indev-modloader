package com.mojontwins.modloader;

import net.minecraft.game.item.ItemArmor;

public class ModItemArmor extends ItemArmor {
    public String name;

    public ModItemArmor (int itemID, int damageReduceAmount, int maxDamage, int renderType, int type) {
        super (itemID, 0, 0, type);     // 0, 0, because we are overwriting:
        this.renderIndex = renderType;
        this.damageReduceAmount = damageReduceAmount;
        this.maxDamage = maxDamage;
    }

    public ModItemArmor setName (String name) {
        this.name = name;
        return this;
    }
}
