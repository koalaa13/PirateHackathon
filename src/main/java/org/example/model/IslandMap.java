package org.example.model;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class IslandMap {
    private final Tiles islandTiles;

    public static class Tiles {
        private final Set<Tile> tiles;

        public Tiles() {
            this.tiles = new HashSet<>();
        }

        public void addTile(long x, long y) {
            this.tiles.add(new Tile(x, y));
        }

        public void addTile(Tile tile) {
            this.tiles.add(tile);
        }

        public boolean contains(long x, long y) {
            return this.tiles.contains(new Tile(x, y));
        }

        public Iterator<Tile> iterateTiles() {
            return tiles.iterator();
        }
    }

    private final long height;

    private final long width;

    public IslandMap(long height, long width) {
        this.islandTiles = new Tiles();
        this.height = height;
        this.width = width;
    }

    public long getHeight() {
        return height;
    }

    public long getWidth() {
        return width;
    }

    public void addIslandTile(long x, long y) {
        this.islandTiles.addTile(x, y);
    }

    public boolean contains(long x, long y) {
        return this.islandTiles.contains(x, y);
    }

    public Tiles getRawTiles() {
        return islandTiles;
    }
}
