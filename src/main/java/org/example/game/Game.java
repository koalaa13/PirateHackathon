package org.example.game;

import org.example.api.ApiGetter;
import org.example.model.IslandMap;
import org.example.model.Scan;
import org.example.model.ScanResponse;

import java.time.Duration;

public class Game {
    private final IslandMap islandMap;

    public Game(IslandMap islandMap) {
        this.islandMap = islandMap;
    }

    public boolean verify(boolean statement, String description) throws InterruptedException {
        if (!statement) {
            System.err.printf("Failed '%s'%n", description);
            Thread.sleep(Duration.ofSeconds(10));
        }
        return statement;
    }

    public void play() throws InterruptedException {
        ApiGetter apiGetter = new ApiGetter();
        long lastTick = -1;
        while (true) {
            ScanResponse response = apiGetter.scan();
            if (!verify(response.isSuccess(), "success scan query")) continue;
            Scan scan = response.getScan();
            if (!verify(scan != null, "scan not null")) continue;
            if (scan.getTick() == lastTick) continue;
        }
    }
}
