package com.mojontwins.modloader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URLClassLoader;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import net.minecraft.client.Minecraft;

public class ModLoader {
	public static boolean isInitialized;
	private static final LinkedList<BaseMod> modList = new LinkedList<BaseMod>();
		
	public ModLoader () {
		
	}
	
	public static void init () {
		isInitialized = true;
		
		System.out.println ("ModLoader initializing ...");
		
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
	            
	            System.out.println ("Minecarft jar path: " + path);
	            
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
        			System.out.println ("Zip found");
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
        			
        			System.out.println ("Directory found");
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
            System.out.println ("Zip found.");
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

            System.out.println("Directory found.");
            File afile[] = file.listFiles();

            if (afile != null) {
                for (int i = 0; i < afile.length; i++) {
                    String s2 = afile[i].getName();

                    if (afile[i].isFile() && s2.startsWith("mod_") && s2.endsWith(".class")) addMod(classloader, s2);
                }
            }
        }
    }
    

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
}
