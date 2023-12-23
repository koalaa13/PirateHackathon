package org.example.service.alert.alerter;

import org.example.model.Scan;
import org.example.model.Ship;
import org.example.service.util.UtilService;

import java.util.ArrayList;
import java.util.List;

public class EnemyScanAlerter implements Alerter {
    private final UtilService utilService;

    public EnemyScanAlerter() {
        this.utilService = new UtilService();
    }

    @Override
    public List<String> getInfo(Scan oldScan, Scan newScan) {
        List<String> infos = new ArrayList<>();

        for (Ship oldEnemy : oldScan.getEnemyShips()) {
            Ship newEnemy = utilService.findShipById(newScan, oldEnemy.getId(), true);
            if (newEnemy == null) {
                infos.add(String.format("На горизонте исчез враг id %d\nx %d\ny %d\nsize %d\nspeed %d\ndirection %s",
                        oldEnemy.getId(),
                        oldEnemy.getX(),
                        oldEnemy.getY(),
                        oldEnemy.getSize(),
                        oldEnemy.getSpeed(),
                        oldEnemy.getDirection()));
            }
        }

        for (Ship newEnemy : newScan.getEnemyShips()) {
            Ship oldEnemy = utilService.findShipById(oldScan, newEnemy.getId(), true);
            if (oldEnemy == null) {
                infos.add(String.format("На горизонте появился враг id %d\nx %d\ny %d\nsize %d\nspeed %d\ndirection %s",
                        newEnemy.getId(),
                        newEnemy.getX(),
                        newEnemy.getY(),
                        newEnemy.getSize(),
                        newEnemy.getSpeed(),
                        newEnemy.getDirection()));
            }
        }

        return infos;
    }
}
