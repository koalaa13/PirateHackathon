package org.example.service.alert.alerter;

import org.example.model.Scan;
import org.example.model.Ship;
import org.example.service.util.UtilService;

import java.util.ArrayList;
import java.util.List;

public class AllyHpAlerter implements Alerter {
    private final UtilService utilService;

    public AllyHpAlerter() {
        this.utilService = new UtilService();
    }

    @Override
    public List<String> getInfo(Scan oldScan, Scan newScan) {
        List<String> infos = new ArrayList<>();
        for (Ship oldShip : oldScan.getMyShips()) {
            Ship newShip = utilService.findShipById(newScan, oldShip.getId(), false);
            if (newShip != null) {
                long oldHp = oldShip.getHp();
                long newHp = newShip.getHp();
                if (oldHp < newHp) {
                    infos.add(String.format("Корабль %d похилился на %dхп", oldShip.getId(), newHp - oldHp));
                } else if (oldHp > newHp) {
                    infos.add(String.format("Корабль %d подбили на %dхп", oldShip.getId(), oldHp - newHp));
                }
            }
        }
        return infos;
    }
}
