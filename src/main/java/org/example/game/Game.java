package org.example.game;

import org.example.api.ApiGetter;
import org.example.model.*;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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

    private boolean isNear(Ship ship, Shoot shoot) {
        long diffX = ship.getX() - shoot.getX();
        long diffY = ship.getY() - shoot.getY();
        return Math.sqrt(diffX * diffX + diffY * diffY) <= ship.getCannonRadius();
    }

    private ShipCommands makeShipCommands(Scan scan) {
        List<Shoot> potentialShoots = new ArrayList<>();
        for (Ship ship : scan.getEnemyShips()) {
            potentialShoots.add(new Shoot(ship.getX(), ship.getY()));
            potentialShoots.add(new Shoot(ship.getHeadX(), ship.getHeadY()));
            potentialShoots.add(new Shoot(ship.getForwardX(), ship.getForwardY()));
        }
        // TODO: Filter out island coords
        List<ShipCommand> shipCommandList = new ArrayList<>();
        ShipCommands shipCommands = new ShipCommands();
        shipCommands.setShipCommands(shipCommandList);
        if (!potentialShoots.isEmpty()) {
            Random randomizer = new Random();
            for (Ship ship : scan.getMyShips()) {
                if (ship.getCannonCooldownLeft() > 0) continue;
                ShipCommand shipCommand = new ShipCommand();
                shipCommand.setId(ship.getId());
                int randomPos = randomizer.nextInt(potentialShoots.size());
                for (int i = 0; i < potentialShoots.size(); i++) {
                    Shoot shoot = potentialShoots.get((randomPos + i) % potentialShoots.size());
                    if (isNear(ship, shoot)) {
                        shipCommand.setCannonShoot(shoot);
                        break;
                    }
                }
                if (shipCommand.getCannonShoot() != null) {
                    shipCommandList.add(shipCommand);
                }
            }
        }
        return shipCommands;
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
            ShipCommands shipCommands = makeShipCommands(scan);
            apiGetter.shipCommand(shipCommands);
            Thread.sleep(Duration.ofSeconds(1));
        }
    }
}
