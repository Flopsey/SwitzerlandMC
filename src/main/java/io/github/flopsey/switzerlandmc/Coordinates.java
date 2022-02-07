package io.github.flopsey.switzerlandmc;

import org.bukkit.Location;

public class Coordinates {

    public record MapCoords(int x, int y) {

        public MapCoords(MinecraftCoords minecraftCoords) {
            this(minecraftCoords.x, -minecraftCoords.z);
        }

    }

    public record MinecraftCoords(int x, int z) {

        public MinecraftCoords(MapCoords mapCoords) {
            this(mapCoords.x, -mapCoords.y);
        }

        public MinecraftCoords(Location location) {
            this(location.getBlockX(), location.getBlockZ());
        }

    }

}
