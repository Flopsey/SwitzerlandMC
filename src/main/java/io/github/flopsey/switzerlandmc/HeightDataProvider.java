package io.github.flopsey.switzerlandmc;

public interface HeightDataProvider {

    float getHeightAt(Coordinates.MapCoords coords);

    class HeightMapTile {

        int minX;
        int maxX;
        int minY;
        int maxY;
        float[] heightData;

        public HeightMapTile(int minX, int maxX, int minY, int maxY) {
            this.minX = minX;
            this.maxX = maxX;
            this.minY = minY;
            this.maxY = maxY;
            this.heightData = new float[(maxX - minX) * (maxY - minY)];
        }

        public void setHeight(Coordinates.MapCoords coords, float height) {
            checkBounds(coords);
            heightData[index(coords)] = height;
        }

        public float getHeight(Coordinates.MapCoords coords) {
            try {
                checkBounds(coords);
            } catch (IndexOutOfBoundsException e) {
                return Float.NaN;
            }
            return heightData[index(coords)];
        }

        private void checkBounds(Coordinates.MapCoords coords) {
            if (coords.x() < minX || coords.x() >= maxX) {
                throw new IndexOutOfBoundsException("x coordinate %d out of bounds for range %d to %d".formatted(coords.x(), minX, maxX));
            }
            if (coords.y() < minY || coords.y() >= maxY) {
                throw new IndexOutOfBoundsException("y coordinate %d out of bounds for range %d to %d".formatted(coords.y(), minY, maxY));
            }
        }

        private int index(Coordinates.MapCoords coords) {
            return (maxY - minY) * (coords.x() - minX) + (coords.y() - minY);
        }

    }

}
