package io.github.flopsey.switzerlandmc;

import org.bukkit.Material;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.WorldInfo;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class SwitzerlandChunkGenerator extends ChunkGenerator {

    HeightDataProvider swissALTI3D;
    HeightDataProvider swissSURFACE3D;

    public SwitzerlandChunkGenerator(HeightDataProvider swissALTI3D, HeightDataProvider swissSURFACE3D) {
        this.swissALTI3D = swissALTI3D;
        this.swissSURFACE3D = swissSURFACE3D;
    }

    static int minecraftHeight(float height) {
        return Math.round(height - 300);
    }

    @Override
    public void generateNoise(@NotNull WorldInfo worldInfo, @NotNull Random random, int chunkX, int chunkZ, @NotNull ChunkData chunkData) {
        for (int localX = 0; localX < 16; ++localX) {
            for (int localZ = 0; localZ < 16; ++localZ) {
                float height = swissALTI3D.getHeightAt(new Coordinates.MapCoords(new Coordinates.MinecraftCoords(16 * chunkX + localX, 16 * chunkZ + localZ)));
                if (Float.isNaN(height)) {
                    return;
                }
                int y = minecraftHeight(height);
                chunkData.setRegion(localX, chunkData.getMinHeight(), localZ, localX + 1, y, localZ + 1, Material.STONE);
                chunkData.setBlock(localX, y, localZ, Material.GRASS_BLOCK);
            }
        }
    }

    @Override
    public void generateSurface(@NotNull WorldInfo worldInfo, @NotNull Random random, int chunkX, int chunkZ, @NotNull ChunkData chunkData) {
        for (int localX = 0; localX < 16; ++localX) {
            for (int localZ = 0; localZ < 16; ++localZ) {
                int baseHeight = chunkData.getMaxHeight() - 1;
                while (chunkData.getBlockData(localX, baseHeight, localZ).getMaterial() == Material.AIR && baseHeight >= chunkData.getMinHeight()) {
                    baseHeight--;
                }
                float height = swissSURFACE3D.getHeightAt(new Coordinates.MapCoords(new Coordinates.MinecraftCoords(16 * chunkX + localX, 16 * chunkZ + localZ)));
                if (Float.isNaN(height)) {
                    return;
                }
                int y = minecraftHeight(height);
                chunkData.setRegion(localX, baseHeight + 1, localZ, localX + 1, y, localZ + 1, Material.LIGHT_GRAY_CONCRETE);
            }
        }
    }
}
