package com.mojontwins.modloader.registry;

import java.util.HashMap;

import com.mojang.nbt.NBTTagCompound;

public class RegistrySet {
	private String[] keys;
	private int[] values;
	private HashMap<String,Integer> lookup;
	private int curIndex = 0;

	private static final int initialLength = 20;
	private static final int expandFactor = 20;

	public RegistrySet() {
		keys = new String[initialLength];
		values = new int[initialLength];
		lookup = new HashMap<String, Integer>();
		curIndex = 0;
	}
	
	public RegistrySet(int length) {
		keys = new String[length];
		values = new int[length];
		lookup = new HashMap<String, Integer>();
		curIndex = 0;
	}
	
	private int lookupKey(String key) {
		Integer i = lookup.get(key);
		if (i != null) return i.intValue();
		return -1;
	}
	
	public void put(String key, int value) {
		int i = lookupKey(key);
		if (i == -1) {
			i = curIndex ++;
			lookup.put(key, i);
		}
		keys[i] = key;
		values[i] = value;
		if (curIndex == keys.length) expandArrays();
	}

	public void put(RegistryEntry registryEntry) {
		this.put(registryEntry.getKey(), registryEntry.getValue());
	}
	
	public int get(String key) throws Exception {
		int i = lookupKey(key);
		if (i == -1) throw new Exception();
		return values[i];
	}
	
	public RegistryEntry getEntry(String key) throws Exception {
		int i = lookupKey(key);
		if (i == -1) throw new Exception();
		return new RegistryEntry(keys[i], values[i]);
	}
	
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		nbt.setInteger("registryLength", curIndex);
		for (int i = 0; i < curIndex; i++) {
			try {
				nbt.setCompoundTag("registry_" + i, this.getEntry(keys[i]).writeToNBT(new NBTTagCompound()));
			} catch (Exception e) {
				System.out.println ("This should not happen");
				e.printStackTrace();
			}
		}
		return nbt;
	}
	
	public void readFromNBT(NBTTagCompound nbt) {
		int registryLength = nbt.getInteger("registryLength");
		for (int i = 0; i < registryLength; i ++) {
			RegistryEntry registryEntry = new RegistryEntry ();
			registryEntry.readFromNBT(nbt.getCompoundTag("registry_" + i));
			this.put(registryEntry);
		}
	}
	
	public int maxValue(int[] values) {
		int res = 0;
		for (int i = 0; i < values.length; i ++) {
			if (values[i] > res) res = values[i];
		}
		return res;
	}

	/*
	 * Generates a translation table which can be used to convert the
	 * data described by newRegistrySet to data described by this registrySet
	 */
	public int[] generateTranslationTable(RegistrySet newRegistrySet) {
		String[] newKeys = newRegistrySet.getKeys();
		int[] newValues = newRegistrySet.getValues();
		int[] translation = new int[maxValue(newValues) + 1];

		translation[0] = 0;
		for (int i = 1; i < translation.length; i ++) {
			translation[i] = -1;
		}
		
		for (int i = 0; i < newValues.length; i ++) {
			int j = lookupKey(newKeys[i]);
			if (j != -1) translation[newValues[i]] = values[j];
		}
		
		return translation;
	}
	
	// Make arrays bigger
	private void expandArrays() {
		int newLength = keys.length + expandFactor;
		
		String[] newKeys = new String[newLength];
		int[] newValues = new int[newLength];
	
		System.arraycopy(keys, 0, newKeys, 0, keys.length);
		System.arraycopy(values, 0, newValues, 0, values.length);
		
		keys = newKeys;
		values = newValues;
	}
	
	public String[] getKeys() {
		return keys;
	}

	public void setKeys(String[] keys) {
		this.keys = keys;
	}

	public int[] getValues() {
		return values;
	}

	public void setValues(int[] values) {
		this.values = values;
	}	
	
	public String toString() {
		String res = "";
		for (int i = 0; i < curIndex; i ++) {
			res += new RegistryEntry(keys[i], values[i]).toString() + "\n";
		}
		return res;
	}
}
