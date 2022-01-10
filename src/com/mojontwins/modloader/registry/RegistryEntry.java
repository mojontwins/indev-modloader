package com.mojontwins.modloader.registry;

import com.mojang.nbt.NBTTagCompound;

public class RegistryEntry {
	private String key;
	private int value;

	public RegistryEntry() {
	}

	public RegistryEntry(String key, int value) {
		this.setKey(key); 
		this.setValue(value);
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		nbt.setString("key", key);
		nbt.setInteger("value", value);
		return nbt;
	}
	
	public void readFromNBT(NBTTagCompound nbt) {
		this.key = nbt.getString("key");
		this.value = nbt.getInteger("value");
	}
	
	public int compareTo(RegistryEntry registryEntry) {
		return this.key.compareTo(registryEntry.key);
	}
	
	public boolean equals(RegistryEntry registryEntry) {
		return this.key.equals(registryEntry.key);
	}
	
	public String toString() {
		return "{\"" + key + "\" : " + value + "}";
	}
}
