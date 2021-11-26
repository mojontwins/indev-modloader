        // Digs caves based upon a very strange set of random calculations
     
        this.guiLoading.displayLoadingString("Carving..");
        this.loadingBar();
        levelGenerator = this;

        int carvingIterations = this.width * this.depth * this.height / 256 / 64 << 1;

        for(int i = 0; i < carvingIterations; ++i) {
            levelGenerator.setNextPhase((float)i * 100.0F / (float)(carvingIterations - 1));
            float var60 = levelGenerator.rand.nextFloat() * (float)this.width;
            float var63 = levelGenerator.rand.nextFloat() * (float)this.height;
            float var62 = levelGenerator.rand.nextFloat() * (float)this.depth;
            var25 = (int)((levelGenerator.rand.nextFloat() + levelGenerator.rand.nextFloat()) * 200.0F);
            float var66 = levelGenerator.rand.nextFloat() * 3.1415927F * 2.0F;
            float var68 = 0.0F;
            float var71 = levelGenerator.rand.nextFloat() * 3.1415927F * 2.0F;
            float var70 = 0.0F;
            float var73 = levelGenerator.rand.nextFloat() * levelGenerator.rand.nextFloat();

            for(var31 = 0; var31 < var25; ++var31) {
                var60 += MathHelper.sin(var66) * MathHelper.cos(var71);
                var62 += MathHelper.cos(var66) * MathHelper.cos(var71);
                var63 += MathHelper.sin(var71);
                var66 += var68 * 0.2F;
                var68 = (var68 *= 0.9F) + (levelGenerator.rand.nextFloat() - levelGenerator.rand.nextFloat());
                var71 = (var71 + var70 * 0.5F) * 0.5F;
                var70 = (var70 *= 0.75F) + (levelGenerator.rand.nextFloat() - levelGenerator.rand.nextFloat());
                if (levelGenerator.rand.nextFloat() >= 0.25F) {
                    float var74 = var60 + (levelGenerator.rand.nextFloat() * 4.0F - 2.0F) * 0.2F;
                    float var33 = var63 + (levelGenerator.rand.nextFloat() * 4.0F - 2.0F) * 0.2F;
                    float var77 = var62 + (levelGenerator.rand.nextFloat() * 4.0F - 2.0F) * 0.2F;
                    float var75 = ((float)levelGenerator.height - var33) / (float)levelGenerator.height;
                    float var79 = 1.2F + (var75 * 3.5F + 1.0F) * var73;
                    float var80 = MathHelper.sin((float)var31 * 3.1415927F / (float)var25) * var79;

                    for(int x = (int)(var74 - var80); x <= (int)(var74 + var80); ++x) {
                        for(int y = (int)(var33 - var80); y <= (int)(var33 + var80); ++y) {
                            for(int z = (int)(var77 - var80); z <= (int)(var77 + var80); ++z) {
                                float var41 = (float)x - var74;
                                float var42 = (float)y - var33;
                                float var48 = (float)z - var77;
                                if (var41 * var41 + var42 * var42 * 2.0F + var48 * var48 < var80 * var80 && x > 0 && y > 0 && z > 0 && x < levelGenerator.width - 1 && y < levelGenerator.height - 1 && z < levelGenerator.depth - 1) {
                                    int idx = (y * levelGenerator.depth + z) * levelGenerator.width + x;
                                    if (levelGenerator.blocksByteArray[idx] == Block.stone.blockID) {
                                        levelGenerator.blocksByteArray[idx] = 0;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }