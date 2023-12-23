package org.example.model;

import java.util.List;

public class Scan {
    private List<Ship> myShips;

    private List<Ship> enemyShips;

    private Zone zone;

    private long tick;

    public long getTick() {
        return tick;
    }

    public void setTick(long tick) {
        this.tick = tick;
    }

    public List<Ship> getMyShips() {
        return myShips;
    }

    public void setMyShips(List<Ship> myShips) {
        this.myShips = myShips;
    }

    public List<Ship> getEnemyShips() {
        return enemyShips;
    }

    public void setEnemyShips(List<Ship> enemyShips) {
        this.enemyShips = enemyShips;
    }

    public Zone getZone() {
        return zone;
    }

    public void setZone(Zone zone) {
        this.zone = zone;
    }
}