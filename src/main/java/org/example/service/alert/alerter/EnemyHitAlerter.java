package org.example.service.alert.alerter;

import org.example.model.Scan;
import org.example.model.Ship;
import org.example.service.util.UtilService;

import java.util.ArrayList;
import java.util.List;

public class EnemyHitAlerter implements Alerter {
    private final UtilService utilService;

    public EnemyHitAlerter() {
        this.utilService = new UtilService();
    }

    @Override
    public List<String> getInfo(Scan oldScan, Scan newScan) {
        List<String> infos = new ArrayList<>();
        for (Ship oldShip : oldScan.getMyShips()) {
            Ship newShip = utilService.findShipById(newScan, oldShip.getId(), false);
            if (newShip != null) {
                long delta = newShip.getCannonShootSuccessCount() - oldShip.getCannonShootSuccessCount();
                if (delta > 0) {
                    infos.add(String.format("Корабль %d успешно попал в %d целей", oldShip.getId(), delta));
                }
            }
        }
        return infos;
    }
}
