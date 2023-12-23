package org.example.game;

import org.example.api.ApiController;
import org.example.model.*;
import org.example.model.command.ShipCommand;
import org.example.model.command.ShipCommands;
import org.example.model.response.ScanResponse;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class Game {
    private final IslandMap islandMap;

    public Game(IslandMap islandMap) {
        this.islandMap = islandMap;
    }

    public boolean verify(boolean statement, String description) throws InterruptedException {
        if (!statement) {
            System.err.printf("Failed '%s'%n", description);
            Thread.sleep(Duration.ofSeconds(10).toMillis());
        }
        return statement;
    }

    private boolean isNear(Ship ship, Shoot shoot) {
        long diffX = ship.getX() - shoot.getX();
        long diffY = ship.getY() - shoot.getY();
        return diffX * diffX + diffY * diffY <= ship.getCannonRadius() * ship.getCannonRadius();
    }

    /**
     * Находим тайлы, выстрелив по которым можно в теории попасть во вражеский корабль
     * @param scan скан
     * @return лист возможных выстрелов
     */
    private List<Shoot> getPotentialShoots(Scan scan) {
        List<Shoot> potentialShoots = new ArrayList<>();
        for (Ship ship : scan.getEnemyShips()) {
            potentialShoots.add(new Shoot(ship.getX(), ship.getY()));
            potentialShoots.add(new Shoot(ship.getHeadX(), ship.getHeadY()));
            potentialShoots.add(new Shoot(ship.getForwardX(), ship.getForwardY()));
        }
        potentialShoots = potentialShoots.stream()
                .filter(shoot -> !islandMap.contains(shoot.getX(), shoot.getY()))
                .collect(Collectors.toList());
        return potentialShoots;
    }

    /**
     * Выбираем кто куда стреляет
     * @param scan скан
     * @param shipCommandList лист ShipCommand, в которых нужно заполнить информацию о выстрелах
     */
    private void fillShoots(Scan scan, List<ShipCommand> shipCommandList) {
        List<Shoot> potentialShoots = getPotentialShoots(scan);
        if (!potentialShoots.isEmpty()) {
            Random randomizer = new Random();
            for (int j = 0; j < scan.getMyShips().size(); ++j) {
                Ship ship = scan.getMyShips().get(j);
                if (ship.getCannonCooldownLeft() > 0) continue;
                int randomPos = randomizer.nextInt(potentialShoots.size());
                for (int i = 0; i < potentialShoots.size(); i++) {
                    Shoot shoot = potentialShoots.get((randomPos + i) % potentialShoots.size());
                    if (isNear(ship, shoot)) {
                        shipCommandList.get(j).setCannonShoot(shoot);
                        break;
                    }
                }
            }
        }
    }

    private ShipCommands makeShipCommands(Scan scan) {
        List<ShipCommand> shipCommandList = new ArrayList<>();
        ShipCommands shipCommands = new ShipCommands();
        shipCommands.setShips(shipCommandList);
        for (Ship ship : scan.getMyShips()) {
            shipCommandList.add(new ShipCommand(ship.getId()));
        }
        // choosing shoots
        fillShoots(scan, shipCommandList);

        return shipCommands;
    }

    public void play() throws InterruptedException {
        ApiController apiController = new ApiController();
        long lastTick = -1;
        while (true) {
            ScanResponse response = apiController.scan();
            if (!verify(response.isSuccess(), "success scan query")) continue;
            Scan scan = response.getScan();
            if (!verify(scan != null, "scan not null")) continue;
            if (scan.getTick() == lastTick) continue;
            ShipCommands shipCommands = makeShipCommands(scan);
            apiController.shipCommand(shipCommands);
            Thread.sleep(Duration.ofSeconds(1).toMillis());
        }
    }
}
