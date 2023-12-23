package org.example.service.util;

import org.example.model.Scan;
import org.example.model.Ship;
import org.example.model.Shoot;

import java.util.List;

public class UtilService {
    public boolean isNear(Ship ship, Shoot shoot) {
        long diffX = ship.getX() - shoot.getX();
        long diffY = ship.getY() - shoot.getY();
        return diffX * diffX + diffY * diffY <= ship.getCannonRadius() * ship.getCannonRadius();
    }

    public Ship findShipById(Scan scan, long shipId, boolean enemy) {
        return findShipById(enemy ? scan.getEnemyShips() : scan.getMyShips(), shipId);
    }

    private Ship findShipById(List<Ship> ships, long shipId) {
        return ships.stream().filter(s -> s.getId() == shipId).findFirst().orElse(null);
    }
}
