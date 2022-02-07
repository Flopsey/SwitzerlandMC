package io.github.flopsey.switzerlandmc;

import org.bukkit.Material;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.WorldInfo;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class SwitzerlandChunkGenerator extends ChunkGenerator {

    HeightDataProvider heightDataProvider;

    public SwitzerlandChunkGenerator(HeightDataProvider heightDataProvider) {
        this.heightDataProvider = heightDataProvider;
    }

    @Override
    public void generateSurface(@NotNull WorldInfo worldInfo, @NotNull Random random, int chunkX, int chunkZ, @NotNull ChunkData chunkData) {
        for (int localX = 0; localX < 16; ++localX) {
            for (int localZ = 0; localZ < 16; ++localZ) {
                int y = minecraftHeight(worldInfo, heightDataProvider.getHeightAt(new Coordinates.MapCoords(new Coordinates.MinecraftCoords(16 * chunkX + localX, 16 * chunkZ + localZ))));
                chunkData.setRegion(localX, chunkData.getMinHeight(), localZ, localX + 1, y, localZ + 1, Material.STONE);
                chunkData.setBlock(localX, y, localZ, Material.GRASS_BLOCK);
            }
        }
    }

    static int minecraftHeight(@NotNull WorldInfo worldInfo, float height) {
        if (Float.isNaN(height)) {
            return worldInfo.getMinHeight() - 1;
        }
        return Math.round(height - 500);
    }
}
