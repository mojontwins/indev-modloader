package com.mojontwins.modloader;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URLClassLoader;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.TreeMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.imageio.ImageIO;

import com.mojontwins.modloader.registry.RegistrySet;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiNewLevel;
import net.minecraft.client.physics.AxisAlignedBB;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.RenderEngine;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.game.block.Block;
import net.minecraft.game.entity.Entity;
import net.minecraft.game.entity.EntityLiving;
import net.minecraft.game.item.Item;
import net.minecraft.game.item.ItemBlock;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.level.Spawner;
import net.minecraft.game.level.World;
import net.minecraft.game.level.generator.LevelGenerator;

public class ModLoader {
	public static boolean isInitialized;
	
	// A registry for IDs
	public static RegistrySet registrySet;
	
	// A translation table for loaded levels
	public static int[] translation = null;
	
	// A list of mods
	private static final LinkedList<BaseMod> modList = new LinkedList<BaseMod>();
	
	// A map for free / used terrain texture indexes
	private static final int [] terrainTextureIndexes = new int [] {
		1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
		1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
		1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0,
		1, 1, 1, 0, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
		1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
		1, 0, 0, 1, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
		0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
		0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
		0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
		0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
		0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
		0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
		0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
		0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
		0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
		1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0
	};
	
	// indexes the previous array
	public static int currentTerrainTextureIndex;
	
	// Used to get free block IDs when adding new blocks
	public static int currentFreeBlockId = 63;
	
	// A map for free / used item texture indexes
	private static final int [] itemTextureIndexes = new int [] {
		1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 1,
		1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 0, 0, 0, 0, 1,
		1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 1,
		1, 1, 1, 1, 1, 1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 1,
		1, 1, 1, 1, 1, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0,
		1, 1, 1, 1, 1, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0,
		1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
		1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
		1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
		0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
		0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
		0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
		0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
		0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
		0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
		0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
	};
	
	// indexes the previous array
	public static int currentItemTextureIndex;

	// Used to get free item IDs when adding new blocks
	public static int currentFreeItemId = 66;
	
	// Store here the texture overrides
	public static List<HashMap<String,Object>> overrides;
	
	// Store here the animated textures
	public static List<HashMap<String,Object>> animations;
	
	// Used to modify RenderPlayer.armorFilenamePrefix
	private static Field field_armorList = null;
	private static Field field_modifiers = null;
	
	// Store custom block renderers
	private static final Map<Integer,BaseMod> blockModels = new HashMap<Integer,BaseMod> ();
    private static final Map<Integer,Boolean> blockSpecialInv = new HashMap<Integer,Boolean> ();
    private static int nextBlockModelID = 1000;
    
    // Used to modify RenderManager.entityRenderMap
    private static HashMap<Class<?>,Render> entityRenderMap = new HashMap<Class<?>,Render>();
	
    // A sequencer for mob IDs used in Spawner
    private static int currentMobID = 1000;
    
    // A storage for mob names for the registry
    private static HashMap<String,Integer> mobNames = new HashMap<String,Integer>();
    
    // A storage for mob classes for the registry
    private static HashMap<String,Class<? extends Entity>> mobClasses = new HashMap<String,Class<? extends Entity>>();
    
    // A sequencer for level themes
    private static int currentThemeID = 4;
       
    // A HashMap to store level themes
    private static HashMap<Integer,ModLevelTheme> levelThemes = new HashMap<Integer,ModLevelTheme> ();
    
    // A TreeMap to store fluids!
    private static TreeMap<Integer,HashMap<String,Object>> customFluids = new TreeMap<Integer,HashMap<String,Object>> ();
    
    // A HashMap to store fuel burning times
    private static HashMap<Integer,Integer> burnTimes = new HashMap<Integer,Integer> ();
    
	public ModLoader () {
	}
	
	public static void init () {
		isInitialized = true;
		
		System.out.println ("ModLoader initializing ...");
		
		// Initialize currentTerrainTextureIndex
		currentTerrainTextureIndex = 0;
		
		// Initialize overrides
		overrides = new ArrayList<HashMap<String,Object>>();
		
		// Initialize animations
		animations = new ArrayList<HashMap<String,Object>>();
		
		try {	    
			// Make some fields accesible
			field_modifiers = (java.lang.reflect.Field.class).getDeclaredField("modifiers");
            field_modifiers.setAccessible(true);
            
		    field_armorList = (net.minecraft.client.renderer.entity.RenderPlayer.class).getDeclaredFields()[3];
		    field_modifiers.setInt(field_armorList, field_armorList.getModifiers() & 0xffffffef);
		    field_armorList.setAccessible(true);
		    			
	        // Get a path to the minecraft jar.			
	        File file;
	
	        try {
	            String path = URLDecoder
	            		.decode((com.mojontwins.modloader.ModLoader.class).getProtectionDomain().getCodeSource().getLocation().getPath(), "UTF-8")
	            		.replace("jar:", "").replace("file:/", "").replace("file:\\", "");
	
	            if (path.contains(".jar!")) {
	            	path = path.substring(0, path.lastIndexOf(".jar!") + ".jar".length());
	            }
	
	            path = (new File(path)).getAbsolutePath();
	            
	            System.out.println ("Minecraft jar path: " + path);
	            
	            file = new File(path);
	        } catch (Exception exception) {
	            throw new RuntimeException("Failed to resolve minecraft jar path!", exception);
	        }
	        
	        // Calculate the path to the `/mods/` directory, then create it if not present
	        File modDir = new File(Minecraft.mcDataDir, "/mods/");
	        modDir.mkdirs();
	        
	        // Load mods in the `/mods/` directory
	        readFromModFolder(modDir);
	        
	        // Load mods in the classpath (this includes the main minecraft.jar)
	        readFromClassPath(file);
	        
	        // Sort mods by priority / dependencies. Not for now
	        // sortModList();
	        
	        // Now run the `load` method of each mod
	        for (Iterator<BaseMod> iterator = modList.iterator(); iterator.hasNext();) {
	        	BaseMod basemod = (BaseMod)iterator.next();
	        	basemod.load ();
	        	System.out.println ("Mod Loaded: \": " + basemod.toString() + "\"");	        	
	        }
	        
	        for (Iterator<BaseMod> iterator = modList.iterator(); iterator.hasNext();) {
	        	BaseMod basemod = (BaseMod)iterator.next();
	        	basemod.modsLoaded ();       	
	        }
	        
	        // Now that everything is loaded, create the registry.      
	        registrySet = new RegistrySet();
	        
	        // Add blocks
	        for (int i = 0; i < Block.blocksList.length; i ++) {
	        	Block block = Block.blocksList[i];
	        	if (block != null) registrySet.put(block.name, i);
	        }
	        
	        // Add mobs
	        Iterator<String> mobsIterator = mobNames.keySet().iterator();
	        while (mobsIterator.hasNext()) {
	        	String mobName = mobsIterator.next();
	        	registrySet.put(mobName, mobNames.get(mobName));
	        }
	        
	        // Add themes
	        Iterator<Integer> themesIterator = levelThemes.keySet().iterator();
	        while (themesIterator.hasNext()) {
	        	int themeID = themesIterator.next();
	        	registrySet.put("theme." + levelThemes.get(themeID).themeName, themeID);
	        }
	        
	        System.out.println(registrySet.toString());
	        
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException ("Exception in ModLoader.init", e);			
		}
		
		System.out.println ("ModLoader initialized!");
	}
	
	/*
	 * Retrieves the first unused texture index for blocks
	 */
	public static int getFreeTerrainTextureIndex () {
		for (; currentTerrainTextureIndex < 256; currentTerrainTextureIndex ++) {
			if (terrainTextureIndexes [currentTerrainTextureIndex] == 0) {				
				terrainTextureIndexes [currentTerrainTextureIndex] = 1;
				return currentTerrainTextureIndex ++;
			}
		}
		
		return -1;
	}
	
	/* 
	 * Retrieves the first unused texture index for items
	 */
	public static int getFreeItemTextureIndex () {
		for (; currentItemTextureIndex < 256; currentItemTextureIndex ++) {
			if (itemTextureIndexes [currentItemTextureIndex] == 0) {				
				itemTextureIndexes [currentItemTextureIndex] = 1;
				return currentItemTextureIndex ++;
			}
		}
		
		return -1;
	}		
	
	/*
	 * Retrieves the next free block ID
	 */
    public static int getBlockId () throws Exception {
        if (currentFreeBlockId < 256)
            return currentFreeBlockId ++;
        else throw new Exception ("No more free item IDs.");
    }
    
    /*
     * Retrieves the next free item ID
     */
    
    public static int getItemId () throws Exception {
    	if (currentFreeItemId < 1024)
    		return currentFreeItemId ++;
    	else throw new Exception ("No more free item IDs.");
    }
    
    /*
     * Registers a new block
     */
	public static void registerBlock (Block block) throws Exception {
		registerBlock (block, null);
	}
	
	/*
	 * This is basicly Risugami's method
	 */
	public static void registerBlock (Block block, Class<?> class1) throws Exception {
		if (block == null) throw new IllegalArgumentException("block parameter cannot be null.");
		
		int i = block.blockID;
		ItemBlock itemblock = null;
		
        if (class1 != null) {
            itemblock = (ItemBlock)class1.getConstructor(new Class[] {
            	Integer.TYPE
            }).newInstance(new Object[] {
                Integer.valueOf(i - 256)
            });
        } else {
            itemblock = new ItemBlock(i - 256);
        }

        if (Block.blocksList[i] != null && Item.itemsList[i] == null) {
            Item.itemsList[i] = itemblock;
        }
	}	
	
	/*
	 * Registers a new kind of armor
	 */
    public static int addArmor(String s) throws Exception {
        // Gets a copy of the `armorFilenamePrefix` array in a list
    	String as[] = (String[]) field_armorList.get(null);
        List<String> list = Arrays.asList(as);
        ArrayList<String> arraylist = new ArrayList<String>();
        arraylist.addAll(list);

        // Make sure it's not been added yet
        if (!arraylist.contains(s)) {
            arraylist.add(s);
        }

        // Now return an index
        int i = arraylist.indexOf(s);
        
        // And substitute the original static array for the modified one
        field_armorList.set(null, ((Object)(arraylist.toArray(new String[0]))));
        
        System.out.println ("Added new armor type " + s + ", renderIndex " + i);
        
        return i;
    }

	/*
	 * Methods to override textures
	 */
	
	public static int addOverride (EnumTextureAtlases textureAtlas, String textureURI) {
		int textureIndex; 
		
		if (textureAtlas == EnumTextureAtlases.TERRAIN) {
			textureIndex = getFreeTerrainTextureIndex ();
		} else {
			textureIndex = getFreeItemTextureIndex ();
		}
		
		Boolean success = addOverride (textureAtlas, textureURI, textureIndex);
		
		return success ? textureIndex : -1;
	}
	
	public static boolean addOverride (EnumTextureAtlases textureAtlas, String textureURI, int textureIndex) {
		System.out.println ("Overriding " + textureAtlas + " with " + textureURI + " at index " + textureIndex);
		
		try {
			HashMap<String, Object> override = new HashMap<String, Object>();
			
			override.put("textureAtlas", textureAtlas);
			override.put("textureURI", textureURI);
			override.put("textureIndex", new Integer(textureIndex));
			
			overrides.add(override);
		} catch (Exception e) {
			return false;
		}
		return true;
	}
	
	/*
	 *  Methods to boolean animation overriding textures 
	 */
	
	public static int addAnimation (EnumTextureAtlases textureAtlas, String textureAtlasURI, int ticksPerFrame) {
		int textureIndex; 
		
		if (textureAtlas == EnumTextureAtlases.TERRAIN) {
			textureIndex = getFreeTerrainTextureIndex ();
		} else {
			textureIndex = getFreeItemTextureIndex ();
		}
		
		Boolean success = addAnimation (textureAtlas, textureAtlasURI, ticksPerFrame, textureIndex);
		
		return success ? textureIndex : -1;		
	}
	
	public static boolean addAnimation (EnumTextureAtlases textureAtlas, String textureAtlasURI, int ticksPerFrame, int textureIndex) {
		System.out.println ("Overriding " + textureAtlas + " with animation " + textureAtlasURI + " at index " + textureIndex);
		
		try {
			HashMap<String, Object> animation = new HashMap<String, Object>();
			
			animation.put("textureAtlas", textureAtlas);
			animation.put("textureAtlasURI", textureAtlasURI);
			animation.put("textureIndex", new Integer(textureIndex));
			animation.put("ticksPerFrame", ticksPerFrame);
			
			animations.add(animation);
		} catch (Exception e) {
			return false;
		}
		return true;
	}
	
	/*
	 * Creates the needed textureFXs
	 */
	public static void registerAllTextureOverrides (RenderEngine renderEngine) throws Exception {
		try {
			// Static textures
			for (Iterator<HashMap<String, Object>> iterator = overrides.iterator(); iterator.hasNext();) {
				Map<String, Object> thisEntry = iterator.next ();
				
				String textureURI = (String) thisEntry.get("textureURI");
				int textureIndex = (Integer) thisEntry.get("textureIndex");
				EnumTextureAtlases textureAtlas = (EnumTextureAtlases) thisEntry.get("textureAtlas");
				
				System.out.println ("Creating ModTextureStatic for texture " + textureIndex + " in " + textureAtlas + " from " + textureURI);
				
				BufferedImage bufferedImage = loadImage(renderEngine, textureURI);
				
				ModTextureStatic modTextureStatic = new ModTextureStatic (textureIndex, textureAtlas, bufferedImage);
				renderEngine.registerTextureFX(modTextureStatic);
			}
			
			// Animations
			for (Iterator<HashMap<String, Object>> iterator = animations.iterator(); iterator.hasNext();) {
				Map<String, Object> thisEntry = iterator.next ();
				
				String textureAtlasURI = (String) thisEntry.get("textureAtlasURI");
				int textureIndex = (Integer) thisEntry.get("textureIndex");
				EnumTextureAtlases textureAtlas = (EnumTextureAtlases) thisEntry.get("textureAtlas");
				int ticksPerFrame = (Integer) thisEntry.get("ticksPerFrame");
				
				System.out.println ("Creating ModTextureAnimated for texture " + textureIndex + " in " + textureAtlas + " from " + textureAtlasURI);
				
				ModTextureAnimated modTextureAnimated = new ModTextureAnimated (textureIndex, textureAtlas, textureAtlasURI, ticksPerFrame);
				renderEngine.registerTextureFX(modTextureAnimated);
			}
		} catch (Exception e) {
			System.out.println ("Exception @ registerAllTextureOverrides" + e);
			e.printStackTrace();
			throw e;
		}
	}
	
	public static BufferedImage loadImage (RenderEngine renderEngine, String textureURI) throws Exception {
		InputStream inputStream = ModLoader.class.getResourceAsStream (textureURI);
		if (inputStream == null) throw new Exception ("Image not found: " + textureURI);
		
		BufferedImage bufferedImage = ImageIO.read(inputStream);
		if (bufferedImage == null) throw new Exception ("Image corrupted: " + textureURI);
		
		return bufferedImage;
	}
    
	/*
	 * This is basicly Risugami's method
	 */
	private static void readFromModFolder (File modDir) 
	throws 
		NoSuchMethodException, 
		IllegalAccessException, 
		InvocationTargetException, 
		MalformedURLException,
		IOException,
    	ClassNotFoundException,
    	IllegalAccessException,
    	InstantiationException
	{
		ClassLoader classloader = (net.minecraft.client.Minecraft.class).getClassLoader();
		Method method = (java.net.URLClassLoader.class).getDeclaredMethod("addURL", new Class[]
		{
			java.net.URL.class
		});
		method.setAccessible(true);
		
		File afile[] = modDir.listFiles();
		Arrays.sort(afile);
		
        if (classloader instanceof URLClassLoader) {
            for (int i = 0; i < afile.length; i++) {
                File file = afile[i];

                if (file.isDirectory() || file.isFile() && (file.getName().endsWith(".jar") || file.getName().endsWith(".zip"))) {
                    method.invoke(classloader, new Object[] {
                    		file.toURI().toURL()
                    });
                }
            }
        }
        
        for (int j = 0; j < afile.length; j++) {
        	File file = afile[j];
        	if (file.isDirectory() || file.isFile() && (file.getName().endsWith(".jar") || file.getName().endsWith(".zip"))) {
        		System.out.println ("Adding mods from " + file.getCanonicalPath());
        		
        		if (file.isFile()) {
        			// Read from .zip file
        			System.out.println ("+ Zip found: " + file);
        			FileInputStream fileInputStream = new FileInputStream(file);
        			ZipInputStream zipInputStream = new ZipInputStream(fileInputStream);
        			
        			ZipEntry zipEntry;       			
        			while ((zipEntry = zipInputStream.getNextEntry()) != null) {
        				String s1 = zipEntry.getName();
        				if (!zipEntry.isDirectory() && s1.startsWith("mod_") && s1.endsWith(".class")) addMod(classloader, s1);
        			}
        			
        			zipInputStream.close();
        			fileInputStream.close();
        		} else if (file.isDirectory()) {
        			Package package1 = (com.mojontwins.modloader.ModLoader.class).getPackage();
        			
        			if (package1 != null) {
        				String s = package1.getName().replace('.', File.separatorChar);
        				file = new File(file, s);
        			}
        			
        			System.out.println ("+ Directory found: " + file);
        			File afile1[] = file.listFiles();
        			
        			if (afile1 != null) {
        				for (int k = 0; k < afile1.length; k++) {
        					String s2 = afile1[k].getName();        					
        					if (afile1[k].isFile() && s2.startsWith("mod_") && s2.endsWith(".class")) addMod(classloader, s2);
        				}
        			}
        		}
        	}
        }
	}

	/*
	 * This is basicly Risugami's method
	 */
    private static void readFromClassPath(File file) 
    throws 
    	FileNotFoundException, 
    	IOException,
    	ClassNotFoundException,
    	IllegalAccessException,
    	InstantiationException
    {
    	System.out.println ("Adding mods from " + file.getCanonicalPath());
        ClassLoader classloader = (com.mojontwins.modloader.ModLoader.class).getClassLoader();

        if (file.isFile() && (file.getName().endsWith(".jar") || file.getName().endsWith(".zip"))) {
            System.out.println ("+ Zip found: " + file);
            FileInputStream fileInputStream = new FileInputStream(file);
            ZipInputStream zipInputStream = new ZipInputStream(fileInputStream);

            ZipEntry zipEntry;
            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                String s1 = zipEntry.getName();
                if (!zipEntry.isDirectory() && s1.startsWith("mod_") && s1.endsWith(".class")) addMod(classloader, s1);
            }            

            zipInputStream.close();
            fileInputStream.close();
        } else if (file.isDirectory()) {
            Package package1 = (com.mojontwins.modloader.ModLoader.class).getPackage();

            if (package1 != null) {
                String s = package1.getName().replace('.', File.separatorChar);
                file = new File(file, s);
            }

            System.out.println("+ Directory found: " + file);
            File afile[] = file.listFiles();

            if (afile != null) {
                for (int i = 0; i < afile.length; i++) {
                    String s2 = afile[i].getName();

                    if (afile[i].isFile() && s2.startsWith("mod_") && s2.endsWith(".class")) addMod(classloader, s2);
                }
            }
        }
    }
    
	/*
	 * This is basicly Risugami's method
	 */
    private static void addMod(ClassLoader classloader, String s) 
    throws
    	ClassNotFoundException,
    	IllegalAccessException,
    	InstantiationException
    {
        try {
            String s1 = s.split("\\.")[0];

            if (s1.contains("$")) {
                return;
            }

            Package package1 = (com.mojontwins.modloader.ModLoader.class).getPackage();

            if (package1 != null) {
                s1 = (new StringBuilder(String.valueOf(String.valueOf(package1.getName())))).append(".").append(s1).toString();
            }

            @SuppressWarnings("rawtypes")
			Class class1 = classloader.loadClass(s1);

            if (!(com.mojontwins.modloader.BaseMod.class).isAssignableFrom(class1)) {
                return;
            }

            // setupProperties(class1);
            BaseMod basemod = (BaseMod)class1.newInstance();

            if (basemod != null) {
                modList.add(basemod);
                System.out.println("Mod Initialized: \"" + basemod.toString() + "\" from " + s);                
            }
        } catch (Throwable throwable) {
            System.out.println("Failed to load mod from \"" + s + "\"");            
            throw (throwable);
        }
    }    
    
    /*
     * Hooks
     */
    
    // This one runs after the player spawn point has been calculated and the house has been put into the world.
    public static void hookGenerateStructures (LevelGenerator levelGenerator, World world) {
    	for (Iterator<BaseMod> iterator = modList.iterator(); iterator.hasNext();) {
        	((BaseMod)iterator.next()).hookGenerateStructures(levelGenerator, world);
        }
    }
    
    // This one runs at the end of the `Planting` stage of level generation
    public static void hookPlanting (LevelGenerator levelGenerator, World world, Random rand) {
    	for (Iterator<BaseMod> iterator = modList.iterator(); iterator.hasNext();) {
        	((BaseMod)iterator.next()).hookPlanting(levelGenerator, world, rand);
        }
    }    
    
    // This one runs right before the game starts
    public static void hookGameStart (Minecraft minecraft) {
    	for (Iterator<BaseMod> iterator = modList.iterator(); iterator.hasNext();) {
        	((BaseMod)iterator.next()).hookGameStart(minecraft);
        }
	}
    
    // Called when block has been harvested
    public static boolean hookOnBlockHarvested (Minecraft minecraft, World world, int x, int y, int z, int blockID, int metadata) {
    	boolean res = false;
    	for (Iterator<BaseMod> iterator = modList.iterator(); iterator.hasNext();) {
        	res = res || ((BaseMod)iterator.next()).hookOnBlockHarvested(minecraft, world, x, y, z, blockID, metadata);
        }
    	return res;
    }
    
    // Called to recalculate player hit strength vs. entity
    public static int hookAttackStrengthModifier (EntityLiving entityLiving, Entity entityHit, int strength) {
    	int res = strength;
    	for (Iterator<BaseMod> iterator = modList.iterator(); iterator.hasNext();) {
        	res = ((BaseMod)iterator.next()).hookAttackStrengthModifier(entityLiving, entityHit, res);
        }
    	return res;
    }
    
    // Called to recalculate player hit strength vs. block
    public static float hookBlockHitStrengthModifier (EntityLiving entityLiving, Block block, float strength) {
    	float res = strength;
    	for (Iterator<BaseMod> iterator = modList.iterator(); iterator.hasNext();) {
        	res = ((BaseMod)iterator.next()).hookBlockHitStrengthModifier(entityLiving, block, res);
        }
    	return res;   	
    }
    
    // Called to calculate an entity speed modifier. Return 1.0F for no change! 
    public static float hookEntitySpeedModifier (EntityLiving entityLiving) {
    	float res = 1.0F;
    	for (Iterator<BaseMod> iterator = modList.iterator(); iterator.hasNext();) {
        	res *= ((BaseMod)iterator.next()).hookEntitySpeedModifier(entityLiving);
        }
    	return res;   	
    }
    
    /*
     * Recipes & Smelting
     */
    public static void addRecipe(ItemStack itemStack, Object obj []) {
    	ModCraftingManager.addRecipe(itemStack, obj);
    }
    
    public static void addSmelting(int input, int output) {
    	ModFurnaceRecipes.addSmeltingRecipe(input, output);
    }
    
    /*
     * Add fuel
     */
    public static void addFuel(int shiftedIndex, int time) {
    	burnTimes.put(shiftedIndex, time);
    }
    
    /*
     * Fuel hook
     */
    public static int getItemBurnTime(int shiftedIndex) {
    	Integer r = burnTimes.get(shiftedIndex);
    	if (r != null) return r.intValue(); else return 0;
    }
    
    /*
     * Registers your BaseMod instance as containing a custom block renderer.
     * You must then override two methods in your mod:
     * `renderInvBlock` to render the block in the inventory and
     * `renderWorldBlock` to render it in the world.
     * Set flag if the item renderer should render it as a regular block or not.
     */
    public static int getUniqueBlockModelID(BaseMod basemod, boolean flag) {
        int i = nextBlockModelID++;
        blockModels.put(Integer.valueOf(i), basemod);
        blockSpecialInv.put(Integer.valueOf(i), Boolean.valueOf(flag));
        return i;
    }
    
    /*
     * Check if `renderInvBlock` should be called to render this item in the inventory
     * or in your hand.
     */
    public static boolean renderBlockIsItemFull3D(int i) {
        if (!blockSpecialInv.containsKey(Integer.valueOf(i))) {
            return false;
        } else {
            return ((Boolean)blockSpecialInv.get(Integer.valueOf(i))).booleanValue();
        }
    }
    
    /*
     * Called from RenderItem.renderItemIntoGUI and ItemRenderer.renderItemInFirstPerson
     */
    public static void renderInvBlock(RenderBlocks renderblocks, Block block, int renderType) {
        BaseMod basemod = (BaseMod)blockModels.get(Integer.valueOf(renderType));

        if (basemod == null) {
            return;
        } else {
        	basemod.renderInvBlock(renderblocks, block, renderType);
        }
    }

    /*
     * Called from renderBlockByRenderType.
     */
    public static boolean renderWorldBlock(RenderBlocks renderblocks, World world, int x, int y, int z, Block block, int renderType) {
        BaseMod basemod = (BaseMod)blockModels.get(Integer.valueOf(renderType));

        if (basemod == null) {
            return false;
        } else {
            return basemod.renderWorldBlock(renderblocks, world, x, y, z, block, renderType);
        }
    }

    /*
     * Methods to customize the spawner
     */
    
    public static int spawnerSetMaxHostileMobs (int maxCreatures, World world) {
    	for (Iterator<BaseMod> iterator = modList.iterator(); iterator.hasNext();) {
    		maxCreatures = ((BaseMod)iterator.next()).spawnerSetMaxHostileMobs(maxCreatures, world);
        }
    	return maxCreatures;
    }
    
    public static int spawnerSetMaxNonHostileMobs (int maxCreatures, World world) {
    	for (Iterator<BaseMod> iterator = modList.iterator(); iterator.hasNext();) {
    		maxCreatures = ((BaseMod)iterator.next()).spawnerSetMaxNonHostileMobs(maxCreatures, world);
        }
    	return maxCreatures;
    }
    
    public static int spawnerSelectMonster (int entityID) {
    	for (Iterator<BaseMod> iterator = modList.iterator(); iterator.hasNext();) {
    		entityID = ((BaseMod)iterator.next()).spawnerSelectMonster(entityID);
        }
    	return entityID;
    }
    
    public static int spawnerSelectMonsterBasedOnPosition (int entityID, World world, int x, int y, int z) {
    	for (Iterator<BaseMod> iterator = modList.iterator(); iterator.hasNext();) {
    		entityID = ((BaseMod)iterator.next()).spawnerSelectMonsterBasedOnPosition(entityID, world, x, y, z);
        }
    	return entityID;
    }
    
    public static Object spawnMonster (int entityID, World world) {
    	for (Iterator<BaseMod> iterator = modList.iterator(); iterator.hasNext();) {
    		Object entity = ((BaseMod)iterator.next()).spawnMonster(entityID, world);
    		if (entity != null) return entity;
        }
    	return null;
    }
    
    public static int spawnerSelectAnimal (int entityID) {
    	for (Iterator<BaseMod> iterator = modList.iterator(); iterator.hasNext();) {
    		entityID = ((BaseMod)iterator.next()).spawnerSelectAnimal(entityID);
        }
    	return entityID;
    }
    
    public static int spawnerSelectAnimalBasedOnPosition (int entityID, World world, int x, int y, int z) {
    	for (Iterator<BaseMod> iterator = modList.iterator(); iterator.hasNext();) {
    		entityID = ((BaseMod)iterator.next()).spawnerSelectAnimalBasedOnPosition(entityID, world, x, y, z);
        }
    	return entityID;
    }
    
    public static Object spawnAnimal (int entityID, World world) {
    	for (Iterator<BaseMod> iterator = modList.iterator(); iterator.hasNext();) {
    		Object entity = ((BaseMod)iterator.next()).spawnAnimal(entityID, world);
    		if (entity != null) return entity;
        }
    	return null;
    }
    
    /*
     * Add a new entry to RenderManager.entityRenderMap
     */
	public static void addEntityRenderer (Class<?> entityClass, Render render) throws Exception {
    	entityRenderMap.put(entityClass, render);
    }
    
	/*
	 * Gets an entity render
	 */
	public static Render getEntityRender (Class<?> entityClass) {
		return entityRenderMap.get(entityClass);
	}
	
    /*
     * Simple sequencer
     */
    public static int getNewMobID(Class<? extends Entity> entityClass) {
    	String mobName = "Mob";
    	try {
	    	Entity entity = entityClass.getConstructor (World.class).newInstance (new World ());
	    	mobName = entity.getEntityString();
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    	mobNames.put(mobName, currentMobID);
    	mobClasses.put(mobName, entityClass);
    	return currentMobID ++;
    }
    
    /*
     * Register new monster mobs
     */    
    public static void registerMonsterEntity (int entityID, Class<? extends Entity> entityClass) {
    	Spawner.availableMonsterEntities.put(entityID, entityClass);
    }
    
    /*
     * Register new animal mobs
     */    
    public static void registerAnimalEntity (int entityID, Class<? extends Entity> entityClass) {
    	Spawner.availableAnimalEntities.put(entityID, entityClass);
    }
    
    /*
     * Remove monster mobs
     */
    public static void removeMonsterEntity (Class<? extends Entity> entityClass) {
    	Iterator<Entry<Integer, Class<? extends Entity>>> it = Spawner.availableMonsterEntities.entrySet().iterator();
    	while (it.hasNext()) {
    		Map.Entry<Integer, Class<? extends Entity>> entry = it.next ();
    		if (entityClass.equals(entry.getValue())) {
    			System.out.println ("Removing " + entityClass.toString() + " from monsters list");
    			it.remove ();
    		}
    	}
    }
    
    /*
     * Remove animal mobs
     */
    public static void removeAnimalEntity (Class<? extends Entity> entityClass) {
    	Iterator<Entry<Integer, Class<? extends Entity>>> it = Spawner.availableAnimalEntities.entrySet().iterator();
    	while (it.hasNext()) {
    		Map.Entry<Integer, Class<? extends Entity>> entry = it.next ();
    		if (entityClass.equals(entry.getValue())) {
    			System.out.println ("Removing " + entityClass.toString() + " from animals list");
    			it.remove ();
    		}
    	}
    }
    
    /*
     * Called before generating a new level to add custom mobs to the lists
     */
    public static void populateMobsHashMap (int levelType) {
    	for (Iterator<BaseMod> iterator = modList.iterator(); iterator.hasNext();) {
    		((BaseMod)iterator.next()).populateMobsHashMap(levelType);
        }
    }
    
    // World Theme support
    
    /*
     * Register a new theme
     */
    public static int registerWorldTheme (ModLevelTheme levelTheme) {
    	int themeID = currentThemeID ++;
    	levelThemes.put (themeID, levelTheme);
    	
    	return themeID;
    }
    
    /*
     * Add themes to the menu in GuiNewLevel
     */
    public static void addNewLevelMenuEntries (GuiNewLevel guiNewLevel) {
        Field fieldWorldTheme;
		try {
			fieldWorldTheme = guiNewLevel.getClass().getDeclaredField("worldTheme");		
			fieldWorldTheme.setAccessible(true);
        	String [] worldTheme = (String []) fieldWorldTheme.get (guiNewLevel);

	        // Modify or replace worldTheme. Cannot add to a static array, so:
	        List<String> list = Arrays.asList(worldTheme);
	        ArrayList<String> arraylist = new ArrayList<String>();
	        arraylist.addAll(list);
	
	        // Here: code to add all world themes defined in ModLoader.
	        Iterator<Integer> it = levelThemes.keySet().iterator();
	        while (it.hasNext()) {
	    		Integer themeID = it.next();
	    		ModLevelTheme levelTheme = levelThemes.get(themeID);
	    		arraylist.add(levelTheme.themeName);
	        }
	        
	        // And substitute the original static array for the modified one    
	        fieldWorldTheme.set (guiNewLevel, ((Object)(arraylist.toArray(new String[0]))));
		} catch (Exception e) {
			e.printStackTrace();
		} 
    }
    
    // Hooks for `LevelGenerator` and level themes
    
    /*
     *  Adjust water level
     */
    public static int waterLevelAdjust (LevelGenerator levelGanerator) {
    	ModLevelTheme levelTheme = levelThemes.get(levelGanerator.levelType);
    	if (levelTheme != null) {
    		return levelTheme.waterLevelAdjust;
    	}
    	return 0;
    }
    
	/*
	 * Adjust the floorlevel just before it is converted to int and written to the height map
	 */
	public static double adjustFloorLevel (LevelGenerator levelGenerator, double floorLevel) {
		ModLevelTheme levelTheme = levelThemes.get(levelGenerator.levelType);
		if (levelTheme != null) {
			floorLevel = levelTheme.adjustFloorLevel(levelGenerator, floorLevel);
		}
		return floorLevel;
	}
    
	/*
	 *  Adjust the (integer) height map, which is 0-centered at this stage
	 */
    public static void adjustHeightMap (LevelGenerator levelGenerator, int [] heightMap) {
    	ModLevelTheme levelTheme = levelThemes.get(levelGenerator.levelType);
    	if (levelTheme != null) {
    		levelTheme.adjustHeightMap (levelGenerator, heightMap);
    	}
    }
    
    // Soiling
    
    /*
     * This hook is called to override the blockID calculation for each column during the "soiling" stage.
     * *Must* return -1 on failure so the engine does its normal calculations. Note that the implementation
     * *HAS* to handle `floatinGen`.
     */
    public static int getSoilingBlockID (LevelGenerator levelGenerator, int y, int floorLevel, int fillLevel, int islandBottomLevel) {
    	ModLevelTheme levelTheme = levelThemes.get(levelGenerator.levelType);
    	if (levelTheme != null) {
    		return levelTheme.getSoilingBlockID(levelGenerator, y, floorLevel, fillLevel, islandBottomLevel);
    	}
        return -1;
    }
    
    // Growing
    
    /*
     * Adjust the beachLevel. Leave it untouched for default
     */
    public static int adjustBeachLevel (LevelGenerator levelGenerator, int beachLevel) {
    	ModLevelTheme levelTheme = levelThemes.get(levelGenerator.levelType);
    	if (levelTheme != null) {
    		beachLevel = levelTheme.adjustBeachLevel (levelGenerator, beachLevel);
    	}
    	return beachLevel;
    }
    
    /*
     * Decide if we should add sand return shouldGrow unchanged for default
     */
    public static boolean shouldGrow (LevelGenerator levelGenerator, double noiseValue, boolean shouldGrow) {
    	ModLevelTheme levelTheme = levelThemes.get(levelGenerator.levelType);
    	if (levelTheme != null) {
    		shouldGrow = levelTheme.shouldGrow(levelGenerator, noiseValue, shouldGrow);
    	}
    	return shouldGrow;
    }
    
	/*
	 * Called each iteration to know which block to add while growing.
	 * Return -1 for the default generation which is sand (grass for hell theme).
	 */
	public static int getGrowingBlockID (LevelGenerator levelGenerator) {
		ModLevelTheme levelTheme = levelThemes.get(levelGenerator.levelType);
    	if (levelTheme != null) {
    		return levelTheme.getGrowingBlockID(levelGenerator);
    	}
		return -1;
	}
	
	// Watering
	
	/*
	 * Use to select a custom BlockID for "water" (not much choice)
	 * Return -1 for the default generation which is Block.waterStill.BlockID;
	 */
	public static int getWateringBlockID (LevelGenerator levelGenerator, boolean inland) {
		ModLevelTheme levelTheme = levelThemes.get(levelGenerator.levelType);
    	if (levelTheme != null) {
    		return levelTheme.getWateringBlockID(levelGenerator, inland);
    	}
		return -1;
	}
	
	// Visuals
	
	/*
	 * Use this to modify any of these world values:
	 * `world.skyColor` - 0x99CCFF by default
	 * `world.fogColor` - 0xFFFFFF by default
	 * `world.cloudColor` - 0xFFFFFF by default
	 * `skylightSubtracted` Seems to be 15 by default. Skeletons & zombies only burn at day if it is > 7.
	 * `skyBrightness` 15 by default.
	 * `defaultFluid` - water or lava (for hell).
	 */
	public static void setVisuals (LevelGenerator levelGenerator, World world) {
		ModLevelTheme levelTheme = levelThemes.get(levelGenerator.levelType);
    	if (levelTheme != null) {
    		levelTheme.setVisuals(levelGenerator, world);
    	}
	}
	
	// Planting
	/*
	 * Do your planting and return true, 
	 * or return false to let the engine do its thing.
	 */
	public static boolean overridePlanting (LevelGenerator levelGenerator, World world) {
		ModLevelTheme levelTheme = levelThemes.get(levelGenerator.levelType);
    	if (levelTheme != null) {
    		return levelTheme.overridePlanting(levelGenerator, world);
    	}
    	return false;
    }
	
	// Custom fluids
	
	/*
	 * Registers a custom fluid to be used in the required hooks below
	 */
	public static void registerFluid (Block source, Block still, Block flowing, int damage, int fireTicks) {
		HashMap<String,Object> fluidEntry = new HashMap<String,Object> ();
		
		fluidEntry.put("source", source);
		fluidEntry.put("still", still);
		fluidEntry.put("flowing", flowing);
		fluidEntry.put("damage", damage);
		fluidEntry.put("fireTicks", fireTicks);
		
		customFluids.put(still.blockID, fluidEntry);
		customFluids.put(flowing.blockID, fluidEntry);
	}
	
	/*
	 * Called from `Entity.onEntityUpdate`
	 */
	public static boolean handleEntityVsFluidCollision(Entity entity, World world) {
		AxisAlignedBB aaBB = entity.boundingBox.expand(0.0F, -0.4F, 0.0F);
        int x0 = (int)aaBB.minX;
        int x1 = (int)aaBB.maxX + 1;
        int y0 = (int)aaBB.minY;
        int y1 = (int)aaBB.maxY + 1;
        int z0 = (int)aaBB.minZ;
        int z1 = (int)aaBB.maxZ + 1;

        for(int x = x0; x < x1; ++x) {
            for(int y = y0; y < y1; ++y) {
                for(int z = z0; z < z1; ++z) {
                	
                	HashMap<String,Object> fluidEntry = customFluids.get(world.getBlockId(x, y, z));
                	if (fluidEntry != null) {
                		entity.attackEntityFrom((Entity)null, (int) fluidEntry.get("damage"));
                		entity.fire = (int) fluidEntry.get("fireTicks");
                		return true;
                	}

                }
            }
        }
        
        return false;
	}
	
	/*
	 * Called from World.fluidFlowCheck
	 */
	public static int customFluidSource (int blockID) {
		HashMap<String,Object> fluidEntry = customFluids.get(blockID);
		if (fluidEntry != null) {
			return ((Block) fluidEntry.get ("source")).blockID;
		}
		return -9999;		
	}
	
	/*
	 * Called from LevelLoader.loadEntity
	 */
	public static Entity loadEntity (World world, String entityString) {
		Entity entity = null;
		Class<? extends Entity> mobClass = mobClasses.get(entityString);
		if (mobClass == null) {
			System.out.println ("Attempt to load non registered entity " + entityString);		
		} else {
			try {
				entity = mobClass.getConstructor (World.class).newInstance (world);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return entity;
	}
	
	/*
	 * Compares the current registry with the registry stored in the level file
	 * and translates the blocks array to the current block IDs.
	 */
	public static void translateBlocks (byte[] blocks) {
		for (int i = 0; i < blocks.length; i ++) {
			blocks [i] = (byte)(translation[blocks[i]] & 0xff);
		}
	}
}
