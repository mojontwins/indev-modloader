# Utils

Utilites are command line based. to execute XXX.jar, just

```
	$ java -jar XXX.jar [parameters]
```

## Json2RenderBlocks.jar

This utility takes the JSON output of Workbench and generates a .java class you can use with your classic mod to add custom block renderers. By default it generate classes that can be used with *Indev Modloader*, but you can customize the output somehow.

```
	$ java -jar \Git\indev-modloader\utils\Json2RenderBlocks.jar
	Usage: java -jar Json2RenderBlocks.jar -i file.json -c ClassName [--genRotations] [--packageName com.mojontwins.modloader] [--tessellatorPackage net.minecraft.client.renderer.Tessellator]
```

* `-i file.json` is the path to the input file.
* `-c ClassName` is the class name for the generated output.
* `--genRotations` (optional) precalculates rotations based on metadata values 2, 3, 4 and 5 (same meaning as furnaces, for example).
* `--packageName` (optional) lets you specify a package name for the generated class.
* `--tessellatorPackage` (optional) lets you specify a classpath for the Tessellator class in your setup.

The utility writes its output to stdout so if you want them in a file you have to use `>` and redirect the output. Example:

```
	$ java -jar Json2RenderBlocks.jar -i \stuff\blocks\myblock.json -c RenderMyBlock > \RetroMCP\src\minecraft\com\mojontwins\modloader\RenderMyBlock.java
```

Will write the output to `\RetroMCP\src\minecraft\com\mojontwins\modloader\RenderMyBlock.java`.

Generated code will contain a `public static boolean renderBlock` method to render the block. Parameters are `int meta, float x, float y, float z,` with metadata and coordinates, and then a list of integer texture indexes used by the model. The converter generates a lookup so you can know what's what, like this:

```java
	/*
	 * Texture lookup:
	 * ti_0 = block_cauldron_xz
	 * ti_1 = block_cauldron_ns
	 * ti_2 = block_cauldron_w
	 * ti_3 = block_cauldron_e
	 * ti_4 = block_cauldron_ontents
	 */
	public static boolean renderBlock (int meta, float x, float y, float z, int ti_0, int ti_1, int ti_2, int ti_3, int ti_4) {
		// etc...
	}
```

`meta` is only useful if you used `--genRotations`, it will be ignored otherwise.

