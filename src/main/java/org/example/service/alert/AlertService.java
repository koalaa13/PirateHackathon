package org.example.service.alert;

import org.example.model.Scan;
import org.example.service.alert.alerter.Alerter;
import org.example.service.alert.alerter.EnemyHitAlerter;
import org.example.service.alert.alerter.EnemyScanAlerter;
import org.example.service.alert.alerter.AllyHpAlerter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class AlertService {
    private final List<Alerter> alerters;

    public AlertService() {
        this.alerters = new ArrayList<>();
        this.alerters.add(new AllyHpAlerter());
        this.alerters.add(new EnemyHitAlerter());
        this.alerters.add(new EnemyScanAlerter());
    }

    public List<String> getAllInfos(Scan oldScan, Scan newScan) {
        if (oldScan == null || newScan == null) {
            return Collections.emptyList();
        }
        return this.alerters.stream().flatMap(a -> a.getInfo(oldScan, newScan).stream()).collect(Collectors.toList());
    }
}
