package com.mojontwins.modloader;

import java.util.Random;

import net.minecraft.game.block.Block;
import net.minecraft.game.level.World;

// Adapted from r1.2.5

public class WorldGenBigMushroom {
    private int mushroomType; // 0 for brown, 1 for green.

    public WorldGenBigMushroom(int mushroomType) {
        this.mushroomType = mushroomType;
    }

    public WorldGenBigMushroom() {
    	this (0);
    }

    public boolean generate(World world, Random rand, int x, int y, int z)
    {
        int mushroomBlockID = mushroomType == 1 ? mod_PoisonLand.blockBigMushroomGreen.blockID : mod_PoisonLand.blockBigMushroomBrown.blockID;

        int j = rand.nextInt(3) + 4;

        if (y < 1 || y + j + 1 >= world.height) {
            return false;
        }

        // Enough room ?

        for (int yy = y; yy <= y + 1 + j; yy++) {
            byte byte0 = 3;

            if (yy == y) {
                byte0 = 0;
            }

            for (int xx = x - byte0; xx <= x + byte0; xx++) {
                for (int zz = z - byte0; zz <= z + byte0; zz++) {
                    if (yy >= 0 && yy < world.height) {
                        int blockID = world.getBlockId(xx, yy, zz);

                        if (blockID != 0 && blockID != Block.leaves.blockID) {
                            return false;
                        }
                    } else {
                        return false;
                    }
                }
            }
        }

        // Dirt, grass or podzol below?
        int blockIDbelow = world.getBlockId(x, y - 1, z);

        if (blockIDbelow != Block.dirt.blockID && blockIDbelow != Block.grass.blockID && blockIDbelow != mod_PoisonLand.blockPodzol.blockID) {
            return false;
        }

        // Change for dirt
        world.setBlock(x, y - 1, z, Block.dirt.blockID);
        world.setBlockMetadata(x, y - 1, z, 0);
        
        // Draw the cap
        int i1 = y + j;

        if (mushroomType == 1) {
            i1 = (y + j) - 3;
        }

        for (int k1 = i1; k1 <= y + j; k1++) {
            int j2 = 1;

            if (k1 < y + j) {
                j2++;
            }

            if (mushroomType == 0) {
                j2 = 3;
            }

            for (int i3 = x - j2; i3 <= x + j2; i3++) {
                for (int j3 = z - j2; j3 <= z + j2; j3++) {
                    int k3 = 5;

                    if (i3 == x - j2) {
                        k3--;
                    }

                    if (i3 == x + j2) {
                        k3++;
                    }

                    if (j3 == z - j2) {
                        k3 -= 3;
                    }

                    if (j3 == z + j2) {
                        k3 += 3;
                    }

                    if (mushroomType == 0 || k1 < y + j) {
                        if ((i3 == x - j2 || i3 == x + j2) && (j3 == z - j2 || j3 == z + j2)) {
                            continue;
                        }

                        if ((i3 == x - (j2 - 1) && j3 == z - j2) || (i3 == x - j2 && j3 == z - (j2 - 1))) {
                            k3 = 1;
                        }

                        if ((i3 == x + (j2 - 1) && j3 == z - j2) || (i3 == x + j2 && j3 == z - (j2 - 1))) {
                            k3 = 3;
                        }

                        if ((i3 == x - (j2 - 1) && j3 == z + j2) || (i3 == x - j2 && j3 == z + (j2 - 1))) {
                            k3 = 7;
                        }

                        if ((i3 == x + (j2 - 1) && j3 == z + j2) || (i3 == x + j2 && j3 == z + (j2 - 1))) {
                            k3 = 9;
                        }
                    }

                    if (k3 == 5 && k1 < y + j) {
                        k3 = 0;
                    }

                    if ((k3 != 0 || y >= (y + j) - 1) && !Block.opaqueCubeLookup[world.getBlockId(i3, k1, j3)]) {
                        world.setBlockWithNotify(i3, k1, j3, mushroomBlockID);
                        world.setBlockMetadata(i3, k1, j3, k3);
                    }
                }
            }
        }

        // Trunk
        for (int l1 = 0; l1 < j; l1++) {
            int k2 = world.getBlockId(x, y + l1, z);

            if (!Block.opaqueCubeLookup[k2]) {
                world.setBlockWithNotify(x, y + l1, z, mushroomBlockID);
                world.setBlockMetadata(x, y + l1, z, 10);
            }
        }

        return true;
    }
}
