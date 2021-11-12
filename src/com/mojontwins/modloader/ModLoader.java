package com.mojontwins.modloader;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
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
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.imageio.ImageIO;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderEngine;
import net.minecraft.game.block.Block;
import net.minecraft.game.item.Item;
import net.minecraft.game.item.ItemBlock;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.level.World;
import net.minecraft.game.level.generator.LevelGenerator;

public class ModLoader {
	public static boolean isInitialized;
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
	public static int currentFreeBlockId = 64;
	
	// A map for free / used item texture indexes
	private static final int [] itemTextureIndexes = new int [] {
		1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0,
		1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 0, 0, 0, 0, 0,
		1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0,
		1, 1, 1, 1, 1, 1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0,
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
	
	public ModLoader () {
		
	}
	
	public static void init () {
		isInitialized = true;
		
		System.out.println ("ModLoader initializing ...");
		
		// Initialize currentTerrainTextureIndex
		currentTerrainTextureIndex = 0;
		
		// Initialize overrides
		overrides = new ArrayList<HashMap<String,Object>>();
		
		try {
	        File file;
	
	        // Get a path to the minecraft jar.
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
	public static void registerBlock (ModBlock block) throws Exception {
		registerBlock (block, null);
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
	
	public static void registerAllTextureOverrides (RenderEngine renderEngine) throws Exception {
		try {
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
	public static void registerBlock (ModBlock block, Class<ItemBlock> class1) throws Exception {
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
    
    /*
     * Recipes & Smelting
     */
    public static void addRecipe (ItemStack itemStack, Object obj []) {
    	ModCraftingManager.addRecipe(itemStack, obj);
    }
    
    public static void addSmelting (int input, int output) {
    	ModFurnaceRecipes.addSmeltingRecipe(input, output);
    }
}
