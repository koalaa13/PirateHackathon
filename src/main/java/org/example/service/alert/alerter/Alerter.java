package org.example.service.alert.alerter;

import org.example.model.Scan;

import java.util.List;

public interface Alerter {
    List<String> getInfo(Scan oldScan, Scan newScan);
}
