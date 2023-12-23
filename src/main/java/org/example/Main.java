package org.example;

import org.example.api.ApiGetter;
import org.example.model.Scan;
import org.example.model.ScanResponse;
import org.example.model.Ship;

public class Main {
    public static void main(String[] args) {
        ApiGetter apiGetter = new ApiGetter();
        ScanResponse scanResponse = apiGetter.scan();
        Scan scan = scanResponse.getScan();
        for (Ship myShip : scan.getMyShips()) {
            System.out.println(myShip.getId());
        }
    }
}
