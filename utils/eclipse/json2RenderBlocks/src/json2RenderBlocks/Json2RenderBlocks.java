package json2RenderBlocks;

import com.mojontwins.util.CommandLineParserSimple;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.json.simple.JSONArray;
import org.json.simple.JSONValue;

public class Json2RenderBlocks {
	public static void main(String[] args) {
		String jsonContent = null;
		Map<?, ?> jsonObj = null;
		
		Map<String, List<String>> params = CommandLineParserSimple.getOptions(args);
		
		String fileIn = CommandLineParserSimple.getSingleValue(params, "i");
		String className = CommandLineParserSimple.getSingleValue(params, "c");
		
		if (fileIn == null || "".equals(fileIn) || className == null || "".equals(className)) {
			System.out.println("Usage: \njava -jar Json2RenderBlocks.jar -i file.json -c ClassName \n\t[--genRotations] \n\t[--packageName com.mojontwins.modloader] \n\t[--tessellatorClasspath net.minecraft.client.renderer.Tessellator] \n\t[--worldClasspath net.minecraft.game.level.World]\n\t[--blockClasspath net.minecraft.game.block.Block]");
			System.exit(-1);
		} 
		
		boolean genRotations = CommandLineParserSimple.optionExists(params, "-genRotations");
		
		String packageName = CommandLineParserSimple.getSingleValue(params, "-packageName");
		if (packageName == null)
			packageName = "com.mojontwins.modloader"; 
		
		String tessellatorClasspath = CommandLineParserSimple.getSingleValue(params, "-tessellatorClasspath");
		if (tessellatorClasspath == null)
			tessellatorClasspath = "net.minecraft.client.renderer.Tessellator"; 
		
		String worldClasspath = CommandLineParserSimple.getSingleValue(params, "-worldClasspath");
		if (worldClasspath == null)
			worldClasspath = "net.minecraft.game.level.World"; 
		
		String blockClasspath = CommandLineParserSimple.getSingleValue(params, "-blockClasspath");
		if (blockClasspath == null)
			blockClasspath = "net.minecraft.game.block.Block"; 
		
		try {
			jsonContent = new String(Files.readAllBytes(Paths.get(fileIn, new String[0])), StandardCharsets.UTF_8);
		} catch (IOException e) {
			System.out.println("Error reading " + fileIn + ": " + e.toString());
			System.exit(-1);
		} 
		
		if ((jsonObj = (Map<?, ?>)JSONValue.parse(jsonContent)) == null) {
			System.out.println("JSON parser returned null (bad JSON?)");
			System.exit(-1);
		} 
		
		Map<?, ?> textures = (Map<?, ?>)jsonObj.get("textures");
		int textureIdx = 0;
		
		if (textures == null) {
			System.out.println("JSON parser couldn't find textures (not a Minecraft block JSON?)");
			System.exit(-1);
		} 
		
		while (textures.get(textureIdx) != null)
			textureIdx++; 
		
		if (textureIdx == 0) {
			System.out.println("JSON parser couldn't find textures (not a Minecraft block JSON?)");
			System.exit(-1);
		} 
		
		JSONArray elements = (JSONArray)jsonObj.get("elements");
		if (elements == null) {
			System.out.println("JSON parser couldn't find elements (not a Minecraft block JSON?)");
			System.exit(-1);
		} 
		
		System.out.println("package " + packageName + ";\n");
		System.out.println("import " + tessellatorClasspath + ";");
		System.out.println("import " + worldClasspath + ";");
		System.out.println("import " + blockClasspath + ";");
		System.out.println("");
		System.out.println("public class " + className + " {\n");
		System.out.println("\t/*\n\t * Texture lookup:");
		
		int i;
		for (i = 0; i < textureIdx; i++)
			System.out.println("\t * ti_" + i + " = " + textures.get(i)); 
		System.out.println("\t */");
		System.out.print("\tpublic static boolean renderBlock (World world, Block block, int meta, float x, float y, float z, ");
		
		for (i = 0; i < textureIdx; i++) {
			System.out.print("int ti_" + i);
			if (i < textureIdx - 1)
				System.out.print(", "); 
		} 
		
		System.out.println(") {");
		
		if (genRotations) {
			System.out.println("\t\tif (meta == 3) {");
			System.out.println("\t\t\t// Facing south (180 degrees)");
			System.out.print("\t\t\treturn renderMeta3(world, block, x, y, z, ");
		
			for (i = 0; i < textureIdx; i++) {
				System.out.print("ti_" + i);
				if (i < textureIdx - 1)
					System.out.print(", "); 
			} 
			
			System.out.println(");");
			System.out.println("\t\t}\n");
			System.out.println("\t\tif (meta == 4) {");
			System.out.println("\t\t\t// Facing west (90 degrees)");
			System.out.print("\t\t\treturn renderMeta4(world, block, x, y, z, ");
			
			for (i = 0; i < textureIdx; i++) {
				System.out.print("ti_" + i);
				if (i < textureIdx - 1)
					System.out.print(", "); 
			} 
			
			System.out.println(");");
			System.out.println("\t\t}\n");
			System.out.println("\t\tif (meta == 5) {");
			System.out.println("\t\t\t// Facing east (270 degrees)");
			System.out.print("\t\t\treturn renderMeta5(world, block, x, y, z, ");
			
			for (i = 0; i < textureIdx; i++) {
				System.out.print("ti_" + i);
				if (i < textureIdx - 1)
					System.out.print(", "); 
			} 
			
			System.out.println(");");
			System.out.println("\t\t}\n");
			System.out.println("\t\t// Facing north (default)");
			System.out.print("\t\treturn renderMeta2(world, block, x, y, z, ");
			
			for (i = 0; i < textureIdx; i++) {
				System.out.print("ti_" + i);
				if (i < textureIdx - 1)
					System.out.print(", "); 
			} 
			
			System.out.println(");");
		} else {
			exportGeneralHeaderless(textureIdx, elements);
		} 
		
		System.out.println("\t}");
		
		if (genRotations) {
			System.out.println("");
			exportFacingNorth(textureIdx, elements);
			System.out.println("");
			exportFacingSouth(textureIdx, elements);
			System.out.println("");
			exportFacingWest(textureIdx, elements);
			System.out.println("");
			exportFacingEast(textureIdx, elements);
		} 
		
		System.out.println("\n\tpublic static void setLightValue (Tessellator tessellator, World world, Block block, float x, float y, float z, float factor) {");
		System.out.println("\t\tfloat f;");
		System.out.println("\t\tif (world == null) f = factor; else f = block.getBlockBrightness(world, (int) x, (int) y, (int) z) * factor;");
		System.out.println("\t\tif (Block.lightValue[block.blockID] > 0) f = factor;");
		System.out.println("\t\ttessellator.setColorOpaque_F(f, f, f);");
		System.out.println("\t}");
		System.out.println("}");
	}
	
	public static void exportFacingSouth(int textureIdx, JSONArray elements) {
		System.out.print("\tpublic static boolean renderMeta3 (World world, Block block, float x, float y, float z, ");
		
		int i;
		for (i = 0; i < textureIdx; i++) {
			System.out.print("int ti_" + i);
			if (i < textureIdx - 1)
				System.out.print(", "); 
		} 
		
		System.out.println(") {");
		System.out.println("\t\tTessellator tessellator = Tessellator.instance;");
		System.out.println("\t\tfloat x1, y1, z1, x2, y2, z2;");
		System.out.println("\t\tfloat u1, v1, u2, v2;\n");
		
		for (i = 0; i < textureIdx; i++) {
			System.out.println("\t\t// Texture #" + i);
			System.out.println("\t\tfloat t" + i + "_u = (float) ((ti_" + i + " & 0x0f) << 4) / 256.0F;");
			System.out.println("\t\tfloat t" + i + "_v = (float) (ti_" + i + " & 0xff0) / 256.0F;");
			System.out.println("");
		} 
		
		int cubeNumber = 0;
		
		for (Object elementObj : elements) {
			System.out.println("\t\t// Cube #" + cubeNumber++ + "\n");
			
			Map<?, ?> element = (Map<?, ?>)elementObj;
			
			JSONArray arrFrom = (JSONArray)element.get("from");
			JSONArray arrTo = (JSONArray)element.get("to");
			
			float x1 = (float)((Long)arrFrom.get(0)).longValue() / 16.0F;
			float y1 = (float)((Long)arrFrom.get(1)).longValue() / 16.0F;
			float z1 = (float)((Long)arrFrom.get(2)).longValue() / 16.0F;
			float x2 = (float)((Long)arrTo.get(0)).longValue() / 16.0F;
			float y2 = (float)((Long)arrTo.get(1)).longValue() / 16.0F;
			float z2 = (float)((Long)arrTo.get(2)).longValue() / 16.0F;
			
			System.out.println("\t\tx1 = x + " + f2s(1.0F - x1) + ";");
			System.out.println("\t\ty1 = y + " + f2s(y1) + ";");
			System.out.println("\t\tz1 = z + " + f2s(1.0F - z1) + ";");
			System.out.println("\t\tx2 = x + " + f2s(1.0F - x2) + ";");
			System.out.println("\t\ty2 = y + " + f2s(y2) + ";");
			System.out.println("\t\tz2 = z + " + f2s(1.0F - z2) + ";");
			System.out.println("");
			
			Map<?, ?> faces = (Map<?, ?>)element.get("faces");
			if (faces == null) {
				System.out.println("JSON parser couldn't find faces (not a Minecraft block JSON?)");
				System.exit(-1);
			} 
			
			Map<?, ?> face = (Map<?, ?>)faces.get("up");
			
			String texture = (String)face.get("texture");
			JSONArray uv = (JSONArray)face.get("uv");
			
			int textureIndex;
			if (face != null && (textureIndex = texture2index(texture)) >= 0) {
				System.out.println("\t\tsetLightValue(tessellator, world, block, x, y + 1, z, 1.0F);");
				System.out.println("\t\tu1 = t" + textureIndex + "_u + " + f2s2(((Long)uv.get(0)).floatValue() / 256.0F) + ";");
				System.out.println("\t\tv1 = t" + textureIndex + "_v + " + f2s2(((Long)uv.get(1)).floatValue() / 256.0F) + ";");
				System.out.println("\t\tu2 = t" + textureIndex + "_u + " + f2s2(((Long)uv.get(2)).floatValue() / 256.0F) + ";");
				System.out.println("\t\tv2 = t" + textureIndex + "_v + " + f2s2(((Long)uv.get(3)).floatValue() / 256.0F) + ";");
				System.out.println("\t\ttessellator.addVertexWithUV(x2, y2, z2, u1, v1);");
				System.out.println("\t\ttessellator.addVertexWithUV(x2, y2, z1, u1, v2);");
				System.out.println("\t\ttessellator.addVertexWithUV(x1, y2, z1, u2, v2);");
				System.out.println("\t\ttessellator.addVertexWithUV(x1, y2, z2, u2, v1);");
				System.out.println("");
			} 
			
			face = (Map<?, ?>)faces.get("down");
			texture = (String)face.get("texture");
			uv = (JSONArray)face.get("uv");
			
			if (face != null && (textureIndex = texture2index(texture)) >= 0) {
				System.out.println("\t\tsetLightValue(tessellator, world, block, x, y - 1, z, 0.5F);");
				System.out.println("\t\tu1 = t" + textureIndex + "_u + " + f2s2(((Long)uv.get(0)).floatValue() / 256.0F) + ";");
				System.out.println("\t\tv1 = t" + textureIndex + "_v + " + f2s2(((Long)uv.get(1)).floatValue() / 256.0F) + ";");
				System.out.println("\t\tu2 = t" + textureIndex + "_u + " + f2s2(((Long)uv.get(2)).floatValue() / 256.0F) + ";");
				System.out.println("\t\tv2 = t" + textureIndex + "_v + " + f2s2(((Long)uv.get(3)).floatValue() / 256.0F) + ";");
				System.out.println("\t\ttessellator.addVertexWithUV(x1, y1, z2, u2, v1);");
				System.out.println("\t\ttessellator.addVertexWithUV(x1, y1, z1, u2, v2);");
				System.out.println("\t\ttessellator.addVertexWithUV(x2, y1, z1, u1, v2);");
				System.out.println("\t\ttessellator.addVertexWithUV(x2, y1, z2, u1, v1);");
				System.out.println("");
			} 
			
			face = (Map<?, ?>)faces.get("north");
			System.out.println("\t\tsetLightValue(tessellator, world, block, x, y, z + 1, 0.8F);");
			if (face != null)
				generateNorthFace(face, 1); 
			
			face = (Map<?, ?>)faces.get("south");
			System.out.println("\t\tsetLightValue(tessellator, world, block, x, y, z - 1, 0.8F);");
			if (face != null)
				generateSouthFace(face, 2); 
			
			face = (Map<?, ?>)faces.get("east");
			System.out.println("\t\tsetLightValue(tessellator, world, block, x - 1, y, z, 0.6F);");
			if (face != null)
				generateEastFace(face, 2); 
			
			face = (Map<?, ?>)faces.get("west");
			System.out.println("\t\tsetLightValue(tessellator, world, block, x + 1, y, z, 0.6F);");
			if (face != null)
				generateWestFace(face, 1); 
		} 
		System.out.println("\t\treturn true;");
		System.out.println("\t}");
	}
	
	public static void exportFacingNorth(int textureIdx, JSONArray elements) {
		System.out.print("\tpublic static boolean renderMeta2 (World world, Block block, float x, float y, float z, ");
		
		for (int i = 0; i < textureIdx; i++) {
			System.out.print("int ti_" + i);
			if (i < textureIdx - 1)
				System.out.print(", "); 
		} 
		
		System.out.println(") {");
		exportGeneralHeaderless(textureIdx, elements);
		System.out.println("\t}");
	}
	
	public static void exportGeneralHeaderless(int textureIdx, JSONArray elements) {
		System.out.println("\t\tTessellator tessellator = Tessellator.instance;");
		System.out.println("\t\tfloat x1, y1, z1, x2, y2, z2;");
		System.out.println("\t\tfloat u1, v1, u2, v2;\n");
		
		for (int i = 0; i < textureIdx; i++) {
			System.out.println("\t\t// Texture #" + i);
			System.out.println("\t\tfloat t" + i + "_u = (float) ((ti_" + i + " & 0x0f) << 4) / 256.0F;");
			System.out.println("\t\tfloat t" + i + "_v = (float) (ti_" + i + " & 0xff0) / 256.0F;");
			System.out.println("");
		} 
		
		int cubeNumber = 0;
		for (Object elementObj : elements) {
			System.out.println("\t\t// Cube #" + cubeNumber++ + "\n");
		
			Map<?, ?> element = (Map<?, ?>)elementObj;
			
			JSONArray arrFrom = (JSONArray)element.get("from");
			JSONArray arrTo = (JSONArray)element.get("to");
			
			float x1 = (float)((Long)arrFrom.get(0)).longValue() / 16.0F;
			float y1 = (float)((Long)arrFrom.get(1)).longValue() / 16.0F;
			float z1 = (float)((Long)arrFrom.get(2)).longValue() / 16.0F;
			float x2 = (float)((Long)arrTo.get(0)).longValue() / 16.0F;
			float y2 = (float)((Long)arrTo.get(1)).longValue() / 16.0F;
			float z2 = (float)((Long)arrTo.get(2)).longValue() / 16.0F;
			
			System.out.println("\t\tx1 = x + " + f2s(x1) + ";");
			System.out.println("\t\ty1 = y + " + f2s(y1) + ";");
			System.out.println("\t\tz1 = z + " + f2s(z1) + ";");
			System.out.println("\t\tx2 = x + " + f2s(x2) + ";");
			System.out.println("\t\ty2 = y + " + f2s(y2) + ";");
			System.out.println("\t\tz2 = z + " + f2s(z2) + ";");
			System.out.println("");
			
			Map<?, ?> faces = (Map<?, ?>)element.get("faces");
			if (faces == null) {
				System.out.println("JSON parser couldn't find faces (not a Minecraft block JSON?)");
				System.exit(-1);
			} 
			
			Map<?, ?> face = (Map<?, ?>)faces.get("up");
			
			String texture = (String)face.get("texture");
			JSONArray uv = (JSONArray)face.get("uv");
			
			int textureIndex;
			if (face != null && (textureIndex = texture2index(texture)) >= 0) {
				System.out.println("\t\tsetLightValue(tessellator, world, block, x, y + 1, z, 1.0F);");
				System.out.println("\t\tu1 = t" + textureIndex + "_u + " + f2s2(((Long)uv.get(0)).floatValue() / 256.0F) + ";");
				System.out.println("\t\tv1 = t" + textureIndex + "_v + " + f2s2(((Long)uv.get(1)).floatValue() / 256.0F) + ";");
				System.out.println("\t\tu2 = t" + textureIndex + "_u + " + f2s2(((Long)uv.get(2)).floatValue() / 256.0F) + ";");
				System.out.println("\t\tv2 = t" + textureIndex + "_v + " + f2s2(((Long)uv.get(3)).floatValue() / 256.0F) + ";");
				System.out.println("\t\ttessellator.addVertexWithUV(x2, y2, z2, u1, v1);");
				System.out.println("\t\ttessellator.addVertexWithUV(x2, y2, z1, u1, v2);");
				System.out.println("\t\ttessellator.addVertexWithUV(x1, y2, z1, u2, v2);");
				System.out.println("\t\ttessellator.addVertexWithUV(x1, y2, z2, u2, v1);");
				System.out.println("");
			} 
			
			face = (Map<?, ?>)faces.get("down");
			texture = (String)face.get("texture");
			uv = (JSONArray)face.get("uv");
			if (face != null && (textureIndex = texture2index(texture)) >= 0) {
				System.out.println("\t\tsetLightValue(tessellator, world, block, x, y - 1, z, 0.5F);");
				System.out.println("\t\tu1 = t" + textureIndex + "_u + " + f2s2(((Long)uv.get(0)).floatValue() / 256.0F) + ";");
				System.out.println("\t\tv1 = t" + textureIndex + "_v + " + f2s2(((Long)uv.get(1)).floatValue() / 256.0F) + ";");
				System.out.println("\t\tu2 = t" + textureIndex + "_u + " + f2s2(((Long)uv.get(2)).floatValue() / 256.0F) + ";");
				System.out.println("\t\tv2 = t" + textureIndex + "_v + " + f2s2(((Long)uv.get(3)).floatValue() / 256.0F) + ";");
				System.out.println("\t\ttessellator.addVertexWithUV(x1, y1, z2, u2, v1);");
				System.out.println("\t\ttessellator.addVertexWithUV(x1, y1, z1, u2, v2);");
				System.out.println("\t\ttessellator.addVertexWithUV(x2, y1, z1, u1, v2);");
				System.out.println("\t\ttessellator.addVertexWithUV(x2, y1, z2, u1, v1);");
				System.out.println("");
			} 
			
			face = (Map<?, ?>)faces.get("north");
			System.out.println("\t\tsetLightValue(tessellator, world, block, x, y, z - 1, 0.8F);");
			if (face != null)
				generateNorthFace(face, 1); 
			
			face = (Map<?, ?>)faces.get("south");
			System.out.println("\t\tsetLightValue(tessellator, world, block, x, y, z + 1, 0.8F);");
			if (face != null)
				generateSouthFace(face, 2); 
			
			face = (Map<?, ?>)faces.get("east");
			System.out.println("\t\tsetLightValue(tessellator, world, block, x - 1, y, z, 0.6F);");
			if (face != null)
				generateEastFace(face, 2); 
			
			face = (Map<?, ?>)faces.get("west");
			System.out.println("\t\tsetLightValue(tessellator, world, block, x + 1, y, z, 0.6F);");
			if (face != null)
				generateWestFace(face, 1); 
		} 
		System.out.println("\t\treturn true;");
	}
	
	public static void exportFacingWest(int textureIdx, JSONArray elements) {
		System.out.print("\tpublic static boolean renderMeta4 (World world, Block block, float x, float y, float z, ");
		int i;
		
		for (i = 0; i < textureIdx; i++) {
			System.out.print("int ti_" + i);
			if (i < textureIdx - 1)
				System.out.print(", "); 
		} 
		
		System.out.println(") {");
		System.out.println("\t\tTessellator tessellator = Tessellator.instance;");
		System.out.println("\t\tfloat x1, y1, z1, x2, y2, z2;");
		System.out.println("\t\tfloat u1, v1, u2, v2;\n");
		
		for (i = 0; i < textureIdx; i++) {
			System.out.println("\t\t// Texture #" + i);
			System.out.println("\t\tfloat t" + i + "_u = (float) ((ti_" + i + " & 0x0f) << 4) / 256.0F;");
			System.out.println("\t\tfloat t" + i + "_v = (float) (ti_" + i + " & 0xff0) / 256.0F;");
			System.out.println("");
		} 
		
		int cubeNumber = 0;
		for (Object elementObj : elements) {
			System.out.println("\t\t// Cube #" + cubeNumber++ + "\n");
		
			Map<?, ?> element = (Map<?, ?>)elementObj;
			
			JSONArray arrFrom = (JSONArray)element.get("from");
			JSONArray arrTo = (JSONArray)element.get("to");
			
			float x1 = (float)((Long)arrFrom.get(0)).longValue() / 16.0F;
			float y1 = (float)((Long)arrFrom.get(1)).longValue() / 16.0F;
			float z1 = (float)((Long)arrFrom.get(2)).longValue() / 16.0F;
			float x2 = (float)((Long)arrTo.get(0)).longValue() / 16.0F;
			float y2 = (float)((Long)arrTo.get(1)).longValue() / 16.0F;
			float z2 = (float)((Long)arrTo.get(2)).longValue() / 16.0F;
			
			System.out.println("\t\tx1 = x + " + f2s(z2) + ";");
			System.out.println("\t\ty1 = y + " + f2s(y1) + ";");
			System.out.println("\t\tz1 = z + " + f2s(1.0F - x1) + ";");
			System.out.println("\t\tx2 = x + " + f2s(z1) + ";");
			System.out.println("\t\ty2 = y + " + f2s(y2) + ";");
			System.out.println("\t\tz2 = z + " + f2s(1.0F - x2) + ";");
			System.out.println("");
			
			Map<?, ?> faces = (Map<?, ?>)element.get("faces");
			if (faces == null) {
				System.out.println("JSON parser couldn't find faces (not a Minecraft block JSON?)");
				System.exit(-1);
			} 
			
			Map<?, ?> face = (Map<?, ?>)faces.get("up");
			String texture = (String)face.get("texture");
			JSONArray uv = (JSONArray)face.get("uv");
			int textureIndex;
			if (face != null && (textureIndex = texture2index(texture)) >= 0) {
				System.out.println("\t\tsetLightValue(tessellator, world, block, x, y + 1, z, 1.0F);");
				System.out.println("\t\tu1 = t" + textureIndex + "_u + " + f2s2(((Long)uv.get(0)).floatValue() / 256.0F) + ";");
				System.out.println("\t\tv1 = t" + textureIndex + "_v + " + f2s2(((Long)uv.get(1)).floatValue() / 256.0F) + ";");
				System.out.println("\t\tu2 = t" + textureIndex + "_u + " + f2s2(((Long)uv.get(2)).floatValue() / 256.0F) + ";");
				System.out.println("\t\tv2 = t" + textureIndex + "_v + " + f2s2(((Long)uv.get(3)).floatValue() / 256.0F) + ";");
				System.out.println("\t\ttessellator.addVertexWithUV(x2, y2, z2, u1, v1);");
				System.out.println("\t\ttessellator.addVertexWithUV(x2, y2, z1, u2, v1);");
				System.out.println("\t\ttessellator.addVertexWithUV(x1, y2, z1, u2, v2);");
				System.out.println("\t\ttessellator.addVertexWithUV(x1, y2, z2, u1, v2);");
				System.out.println("");
			} 
			
			face = (Map<?, ?>)faces.get("down");
			texture = (String)face.get("texture");
			uv = (JSONArray)face.get("uv");
			if (face != null && (textureIndex = texture2index(texture)) >= 0) {
				System.out.println("\t\tsetLightValue(tessellator, world, block, x, y - 1, z, 0.5F);");
				System.out.println("\t\tu1 = t" + textureIndex + "_u + " + f2s2(((Long)uv.get(0)).floatValue() / 256.0F) + ";");
				System.out.println("\t\tv1 = t" + textureIndex + "_v + " + f2s2(((Long)uv.get(1)).floatValue() / 256.0F) + ";");
				System.out.println("\t\tu2 = t" + textureIndex + "_u + " + f2s2(((Long)uv.get(2)).floatValue() / 256.0F) + ";");
				System.out.println("\t\tv2 = t" + textureIndex + "_v + " + f2s2(((Long)uv.get(3)).floatValue() / 256.0F) + ";");
				System.out.println("\t\ttessellator.addVertexWithUV(x1, y1, z2, u1, v2);");
				System.out.println("\t\ttessellator.addVertexWithUV(x1, y1, z1, u2, v2);");
				System.out.println("\t\ttessellator.addVertexWithUV(x2, y1, z1, u2, v1);");
				System.out.println("\t\ttessellator.addVertexWithUV(x2, y1, z2, u1, v1);");
				System.out.println("");
			} 

			face = (Map<?, ?>)faces.get("south");
			System.out.println("\t\tsetLightValue(tessellator, world, block, x + 1, y, z, 0.6F);");
			if (face != null)
				generateWestFace(face, 1); 
			
			face = (Map<?, ?>)faces.get("north");
			System.out.println("\t\tsetLightValue(tessellator, world, block, x - 1, y, z, 0.6F);");
			if (face != null)
				generateEastFace(face, 2); 
			
			face = (Map<?, ?>)faces.get("west");
			System.out.println("\t\tsetLightValue(tessellator, world, block, x, y, z + 1, 0.8F);");
			if (face != null)
				generateNorthFace(face, 1); 
			
			face = (Map<?, ?>)faces.get("east");
			System.out.println("\t\tsetLightValue(tessellator, world, block, x, y, z - 1, 0.8F);");
			if (face != null)
				generateSouthFace(face, 2); 
		} 
		System.out.println("\t\treturn true;");
		System.out.println("\t}");
	}
	
	public static void exportFacingEast(int textureIdx, JSONArray elements) {
		System.out.print("\tpublic static boolean renderMeta5 (World world, Block block, float x, float y, float z, ");
		
		int i;
		for (i = 0; i < textureIdx; i++) {
			System.out.print("int ti_" + i);
			if (i < textureIdx - 1)
				System.out.print(", "); 
		} 
		
		System.out.println(") {");
		System.out.println("\t\tTessellator tessellator = Tessellator.instance;");
		System.out.println("\t\tfloat x1, y1, z1, x2, y2, z2;");
		System.out.println("\t\tfloat u1, v1, u2, v2;\n");
		
		for (i = 0; i < textureIdx; i++) {
			System.out.println("\t\t// Texture #" + i);
			System.out.println("\t\tfloat t" + i + "_u = (float) ((ti_" + i + " & 0x0f) << 4) / 256.0F;");
			System.out.println("\t\tfloat t" + i + "_v = (float) (ti_" + i + " & 0xff0) / 256.0F;");
			System.out.println("");
		} 
		
		int cubeNumber = 0;
		for (Object elementObj : elements) {
			System.out.println("\t\t// Cube #" + cubeNumber++ + "\n");
			
			Map<?, ?> element = (Map<?, ?>)elementObj;
			
			JSONArray arrFrom = (JSONArray)element.get("from");
			JSONArray arrTo = (JSONArray)element.get("to");
			
			float x1 = (float)((Long)arrFrom.get(0)).longValue() / 16.0F;
			float y1 = (float)((Long)arrFrom.get(1)).longValue() / 16.0F;
			float z1 = (float)((Long)arrFrom.get(2)).longValue() / 16.0F;
			float x2 = (float)((Long)arrTo.get(0)).longValue() / 16.0F;
			float y2 = (float)((Long)arrTo.get(1)).longValue() / 16.0F;
			float z2 = (float)((Long)arrTo.get(2)).longValue() / 16.0F;
			
			System.out.println("\t\tx1 = x + " + f2s(1.0F - z1) + ";");
			System.out.println("\t\ty1 = y + " + f2s(y1) + ";");
			System.out.println("\t\tz1 = z + " + f2s(x2) + ";");
			System.out.println("\t\tx2 = x + " + f2s(1.0F - z2) + ";");
			System.out.println("\t\ty2 = y + " + f2s(y2) + ";");
			System.out.println("\t\tz2 = z + " + f2s(x1) + ";");
			System.out.println("");
			
			Map<?, ?> faces = (Map<?, ?>)element.get("faces");
			
			if (faces == null) {
				System.out.println("JSON parser couldn't find faces (not a Minecraft block JSON?)");
				System.exit(-1);
			} 
			
			Map<?, ?> face = (Map<?, ?>)faces.get("up");
			String texture = (String)face.get("texture");
			JSONArray uv = (JSONArray)face.get("uv");
			int textureIndex;
			if (face != null && (textureIndex = texture2index(texture)) >= 0) {
				System.out.println("\t\tsetLightValue(tessellator, world, block, x, y + 1, z, 1.0F);");
				System.out.println("\t\tu1 = t" + textureIndex + "_u + " + f2s2(((Long)uv.get(0)).floatValue() / 256.0F) + ";");
				System.out.println("\t\tv1 = t" + textureIndex + "_v + " + f2s2(((Long)uv.get(1)).floatValue() / 256.0F) + ";");
				System.out.println("\t\tu2 = t" + textureIndex + "_u + " + f2s2(((Long)uv.get(2)).floatValue() / 256.0F) + ";");
				System.out.println("\t\tv2 = t" + textureIndex + "_v + " + f2s2(((Long)uv.get(3)).floatValue() / 256.0F) + ";");
				System.out.println("\t\ttessellator.addVertexWithUV(x2, y2, z2, u2, v1);");
				System.out.println("\t\ttessellator.addVertexWithUV(x2, y2, z1, u1, v1);");
				System.out.println("\t\ttessellator.addVertexWithUV(x1, y2, z1, u1, v2);");
				System.out.println("\t\ttessellator.addVertexWithUV(x1, y2, z2, u2, v2);");
				System.out.println("");
			} 
			
			face = (Map<?, ?>)faces.get("down");
			texture = (String)face.get("texture");
			uv = (JSONArray)face.get("uv");
			if (face != null && (textureIndex = texture2index(texture)) >= 0) {
				System.out.println("\t\tsetLightValue(tessellator, world, block, x, y - 1, z, 0.5F);");
				System.out.println("\t\tu1 = t" + textureIndex + "_u + " + f2s2(((Long)uv.get(0)).floatValue() / 256.0F) + ";");
				System.out.println("\t\tv1 = t" + textureIndex + "_v + " + f2s2(((Long)uv.get(1)).floatValue() / 256.0F) + ";");
				System.out.println("\t\tu2 = t" + textureIndex + "_u + " + f2s2(((Long)uv.get(2)).floatValue() / 256.0F) + ";");
				System.out.println("\t\tv2 = t" + textureIndex + "_v + " + f2s2(((Long)uv.get(3)).floatValue() / 256.0F) + ";");
				System.out.println("\t\ttessellator.addVertexWithUV(x1, y1, z2, u2, v2);");
				System.out.println("\t\ttessellator.addVertexWithUV(x1, y1, z1, u1, v2);");
				System.out.println("\t\ttessellator.addVertexWithUV(x2, y1, z1, u1, v1);");
				System.out.println("\t\ttessellator.addVertexWithUV(x2, y1, z2, u2, v1);");
				System.out.println("");
			} 
			
			face = (Map<?, ?>)faces.get("north");
			System.out.println("\t\tsetLightValue(tessellator, world, block, x + 1, y, z, 0.6F);");
			if (face != null)
				generateWestFace(face, 1); 
			
			face = (Map<?, ?>)faces.get("south");
			System.out.println("\t\tsetLightValue(tessellator, world, block, x - 1, y, z, 0.6F);");
			if (face != null)
				generateEastFace(face, 2); 
			
			face = (Map<?, ?>)faces.get("east");
			System.out.println("\t\tsetLightValue(tessellator, world, block, x, y, z + 1, 0.8F);");
			if (face != null)
				generateNorthFace(face, 1); 
			
			face = (Map<?, ?>)faces.get("west");
			System.out.println("\t\tsetLightValue(tessellator, world, block, x, y, z - 1, 0.8F);");
			if (face != null)
				generateSouthFace(face, 2); 
		} 
		
		System.out.println("\t\treturn true;");
		System.out.println("\t}");
	}
	
	public static String f2s(float f) {
		return String.valueOf(String.format(Locale.US, "%.4f", new Object[] { Float.valueOf(f) })) + "F";
	}
	
	public static String f2s2(float f) {
		return String.valueOf(String.format(Locale.US, "%.7f", new Object[] { Float.valueOf(f) })) + "F";
	}
	
	public static int texture2index(String texture) {
		if (texture == null || "".equals(texture))
			return -1; 
		
		if ("#missing".equals(texture))
			return -1; 
		
		int res = -1;
		
		try {
			res = Integer.parseInt(texture.substring(1));
		} catch (Exception exception) {}
		
		return res;
	}
	
	public static void generateNorthFace(Map<?, ?> face, int zI) {
		String texture = (String)face.get("texture");
		JSONArray uv = (JSONArray)face.get("uv");
		
		int textureIndex;
		if ((textureIndex = texture2index(texture)) >= 0) {
			System.out.println("\t\tu1 = t" + textureIndex + "_u + " + f2s2(((Long)uv.get(0)).floatValue() / 256.0F) + ";");
			System.out.println("\t\tv1 = t" + textureIndex + "_v + " + f2s2(((Long)uv.get(1)).floatValue() / 256.0F) + ";");
			System.out.println("\t\tu2 = t" + textureIndex + "_u + " + f2s2(((Long)uv.get(2)).floatValue() / 256.0F) + ";");
			System.out.println("\t\tv2 = t" + textureIndex + "_v + " + f2s2(((Long)uv.get(3)).floatValue() / 256.0F) + ";");
			System.out.println("\t\ttessellator.addVertexWithUV(x1, y2, z" + zI + ", u2, v1);");
			System.out.println("\t\ttessellator.addVertexWithUV(x2, y2, z" + zI + ", u1, v1);");
			System.out.println("\t\ttessellator.addVertexWithUV(x2, y1, z" + zI + ", u1, v2);");
			System.out.println("\t\ttessellator.addVertexWithUV(x1, y1, z" + zI + ", u2, v2);");
			System.out.println("");
		} 
	}
	
	public static void generateSouthFace(Map<?, ?> face, int zI) {
		String texture = (String)face.get("texture");
		JSONArray uv = (JSONArray)face.get("uv");
		
		int textureIndex;
		if ((textureIndex = texture2index(texture)) >= 0) {
			System.out.println("\t\tu1 = t" + textureIndex + "_u + " + f2s2(((Long)uv.get(0)).floatValue() / 256.0F) + ";");
			System.out.println("\t\tv1 = t" + textureIndex + "_v + " + f2s2(((Long)uv.get(1)).floatValue() / 256.0F) + ";");
			System.out.println("\t\tu2 = t" + textureIndex + "_u + " + f2s2(((Long)uv.get(2)).floatValue() / 256.0F) + ";");
			System.out.println("\t\tv2 = t" + textureIndex + "_v + " + f2s2(((Long)uv.get(3)).floatValue() / 256.0F) + ";");
			System.out.println("\t\ttessellator.addVertexWithUV(x1, y2, z" + zI + ", u1, v1);");
			System.out.println("\t\ttessellator.addVertexWithUV(x1, y1, z" + zI + ", u1, v2);");
			System.out.println("\t\ttessellator.addVertexWithUV(x2, y1, z" + zI + ", u2, v2);");
			System.out.println("\t\ttessellator.addVertexWithUV(x2, y2, z" + zI + ", u2, v1);");
			System.out.println("");
		} 
	}
	
	public static void generateEastFace(Map<?, ?> face, int xI) {
		String texture = (String)face.get("texture");
		JSONArray uv = (JSONArray)face.get("uv");
		
		int textureIndex;
		if ((textureIndex = texture2index(texture)) >= 0) {
			System.out.println("\t\tu1 = t" + textureIndex + "_u + " + f2s2(((Long)uv.get(0)).floatValue() / 256.0F) + ";");
			System.out.println("\t\tv1 = t" + textureIndex + "_v + " + f2s2(((Long)uv.get(1)).floatValue() / 256.0F) + ";");
			System.out.println("\t\tu2 = t" + textureIndex + "_u + " + f2s2(((Long)uv.get(2)).floatValue() / 256.0F) + ";");
			System.out.println("\t\tv2 = t" + textureIndex + "_v + " + f2s2(((Long)uv.get(3)).floatValue() / 256.0F) + ";");
			System.out.println("\t\ttessellator.addVertexWithUV(x" + xI + ", y2, z1, u2, v1);");
			System.out.println("\t\ttessellator.addVertexWithUV(x" + xI + ", y2, z2, u1, v1);");
			System.out.println("\t\ttessellator.addVertexWithUV(x" + xI + ", y1, z2, u1, v2);");
			System.out.println("\t\ttessellator.addVertexWithUV(x" + xI + ", y1, z1, u2, v2);");
			System.out.println("");
		} 
	}
	
	public static void generateWestFace(Map<?, ?> face, int xI) {
		String texture = (String)face.get("texture");
		JSONArray uv = (JSONArray)face.get("uv");
		
		int textureIndex;
		if ((textureIndex = texture2index(texture)) >= 0) {
			System.out.println("\t\tu1 = t" + textureIndex + "_u + " + f2s2(((Long)uv.get(0)).floatValue() / 256.0F) + ";");
			System.out.println("\t\tv1 = t" + textureIndex + "_v + " + f2s2(((Long)uv.get(1)).floatValue() / 256.0F) + ";");
			System.out.println("\t\tu2 = t" + textureIndex + "_u + " + f2s2(((Long)uv.get(2)).floatValue() / 256.0F) + ";");
			System.out.println("\t\tv2 = t" + textureIndex + "_v + " + f2s2(((Long)uv.get(3)).floatValue() / 256.0F) + ";");
			System.out.println("\t\ttessellator.addVertexWithUV(x" + xI + ", y2, z1, u1, v1);");
			System.out.println("\t\ttessellator.addVertexWithUV(x" + xI + ", y1, z1, u1, v2);");
			System.out.println("\t\ttessellator.addVertexWithUV(x" + xI + ", y1, z2, u2, v2);");
			System.out.println("\t\ttessellator.addVertexWithUV(x" + xI + ", y2, z2, u2, v1);");
			System.out.println("");
		} 
	}
}
