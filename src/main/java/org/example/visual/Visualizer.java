package org.example.visual;

import org.example.model.IslandMap;
import org.example.model.Ship;

import java.util.List;

public class Visualizer {
    private final IslandMap islandMap;

    private final IslandMap.Tiles myShipsTiles;

    private final IslandMap.Tiles enemyShipsTiles;

    public Visualizer(IslandMap islandMap, List<Ship> enemyShips, List<Ship> myShips) {
        this.islandMap = islandMap;
        this.myShipsTiles = new IslandMap.Tiles();
        this.enemyShipsTiles = new IslandMap.Tiles();
        myShips.forEach(s -> s.toTiles().forEach(myShipsTiles::addTile));
        enemyShips.forEach(s -> s.toTiles().forEach(enemyShipsTiles::addTile));
    }

    private static final char SEA_TILE = '*';
    private static final char MY_SHIP_TILE = 'M';

    private static final char ENEMY_SHIP_TILE = 'E';

    private static final char ISLAND_TILE = '@';

    private static final char ISLAND_COLLISION_TILE = 'X';

    private static final char SHIP_COLLISION_TILE = 'Y';

    private static final char EVERYTHING_COLLISION_TILE = 'P';

    // first bit == is island
    // second bit == is enemy ship
    // third bit == is my ship
    private char mapMaskToChar(int mask) {
        if (mask == 0) {
            return SEA_TILE;
        }
        if (mask == 1) {
            return ISLAND_TILE;
        }
        if (mask == 2) {
            return ENEMY_SHIP_TILE;
        }
        if (mask == 3) {
            return ISLAND_COLLISION_TILE;
        }
        if (mask == 4) {
            return MY_SHIP_TILE;
        }
        if (mask == 5) {
            return ISLAND_COLLISION_TILE;
        }
        if (mask == 6) {
            return SHIP_COLLISION_TILE;
        }
        if (mask == 7) {
            return EVERYTHING_COLLISION_TILE;
        }
        return SEA_TILE;
    }

    private int buildMask(boolean isIsland, boolean isEnemy, boolean isMy) {
        int mask = 0;
        if (isIsland) {
            mask |= 1;
        }
        if (isEnemy) {
            mask |= 2;
        }
        if (isMy) {
            mask |= 4;
        }
        return mask;
    }

    /**
     *
     * @param x Х координата центра квадрата визуализации
     * @param y Y координата центра квадрата визуализации
     * @param r радиус визуализации, т.е. отобразиться квадрат с противоположными углами [x - r; y - r] [x + r; y + r]
     */
    public void visualize(long x, long y, long r) {
        for (long curX = x - r; curX <= x + r; ++curX) {
            for (long curY = y - r; curY < y + r; ++curY) {
                int mask = buildMask(islandMap.contains(curX, curY),
                        enemyShipsTiles.contains(curX, curY),
                        myShipsTiles.contains(curX, curY));
                char c = mapMaskToChar(mask);
                System.out.print(c);
            }
            System.out.println();
        }
    }
}
