package io.github.flopsey.switzerlandmc;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.Objects;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class XYZHeightDataProvider implements HeightDataProvider {

    static final int TILE_SIZE = 1000;

    private final ConcurrentMap<Coordinates.MapCoords, HeightMapTile> cache = new ConcurrentHashMap<>();
    private final ReadWriteLock cacheLock = new ReentrantReadWriteLock();
    private final JavaPlugin plugin;
    private final String resourceName;

    public XYZHeightDataProvider(JavaPlugin plugin, String resourceName) {
        this.plugin = plugin;
        this.resourceName = resourceName;
    }

    @Override
    public float getHeightAt(Coordinates.MapCoords coords) {
        Coordinates.MapCoords tileCoordinates = new Coordinates.MapCoords(coords.x() / TILE_SIZE, coords.y() / TILE_SIZE);

        boolean cached;
        cacheLock.readLock().lock();
        try {
            cached = cache.containsKey(tileCoordinates);
        } finally {
            cacheLock.readLock().unlock();
        }
        if (!cached) {
            cacheLock.writeLock().lock();
            try {
                if (!cache.containsKey(tileCoordinates)) {
                    HeightMapTile tile = getTile(tileCoordinates);
                    cache.put(tileCoordinates, tile);
                }
            } finally {
                cacheLock.writeLock().unlock();
            }

        }

        cacheLock.readLock().lock();
        try {
            return cache.get(tileCoordinates).getHeight(coords);
        } finally {
            cacheLock.readLock().unlock();
        }
    }

    private HeightMapTile getTile(Coordinates.MapCoords tileCoords) {
        HeightMapTile tile = new HeightMapTile(tileCoords.x() * TILE_SIZE, tileCoords.x() * TILE_SIZE + TILE_SIZE, tileCoords.y() * TILE_SIZE, tileCoords.y() * TILE_SIZE + TILE_SIZE);
        float[][] cumulatedHeightValues = new float[TILE_SIZE][TILE_SIZE];
        short[][] heightValueCount = new short[TILE_SIZE][TILE_SIZE];
        try (Scanner scanner = new Scanner(Objects.requireNonNull(plugin.getResource(resourceName.formatted(tileCoords.x(), tileCoords.y()))))) {
            scanner.nextLine();  // Header line
            while (scanner.hasNextFloat()) {
                int x = (int) scanner.nextFloat() - tileCoords.x() * TILE_SIZE;
                int y = (int) scanner.nextFloat() - tileCoords.y() * TILE_SIZE;
                cumulatedHeightValues[x][y] += scanner.nextFloat();
                ++heightValueCount[x][y];
            }
            for (int x = 0; x < TILE_SIZE; ++x) {
                for (int y = 0; y < TILE_SIZE; ++y) {
                    tile.setHeight(new Coordinates.MapCoords(TILE_SIZE * tileCoords.x() + x, TILE_SIZE * tileCoords.y() + y), cumulatedHeightValues[x][y] / heightValueCount[x][y]);
                }
            }
        } catch (NullPointerException e) {
            Arrays.fill(tile.heightData, Float.NaN);
        }
        return tile;
    }

}
