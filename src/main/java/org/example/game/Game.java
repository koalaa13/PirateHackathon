package org.example.game;

import org.example.api.ApiController;
import org.example.model.*;
import org.example.model.command.ShipCommand;
import org.example.model.command.ShipCommands;
import org.example.model.response.DefaultApiResponse;
import org.example.model.response.Error;
import org.example.model.response.ScanResponse;
import org.example.service.alert.AlertService;
import org.example.service.util.UtilService;
import org.example.visual.GraphVisualizer;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class Game {
    private final IslandMap islandMap;

    private final UtilService utilService;

    private final GraphVisualizer graphVisualizer;

    public Game(IslandMap islandMap, GraphVisualizer graphVisualizer) {
        this.islandMap = islandMap;
        this.utilService = new UtilService();
        this.graphVisualizer = graphVisualizer;
    }

    public boolean verify(boolean statement, String description) throws InterruptedException {
        if (!statement) {
            System.err.printf("Failed '%s'%n", description);
            Thread.sleep(Duration.ofSeconds(10).toMillis());
        }
        return statement;
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
                    if (utilService.isNear(ship, shoot)) {
                        shipCommandList.get(j).setCannonShoot(shoot);
                        break;
                    }
                }
            }
        }
    }

    private ShipCommands buildShipCommands(Scan scan) {
        List<ShipCommand> shipCommandList = new ArrayList<>();
        ShipCommands shipCommands = new ShipCommands();
        shipCommands.setShips(shipCommandList);
        for (Ship ship : scan.getMyShips()) {
            shipCommandList.add(new ShipCommand(ship.getId()));
        }
        return shipCommands;
    }

    private void fillCommandToStop(Ship ship, ShipCommand shipCommand) {
        if (ship == null) {
            return;
        }
        long maxChange = ship.getMaxChangeSpeed();
        long curSpeed = ship.getSpeed();
        long delta = -Math.min(maxChange, curSpeed);
        if (shipCommand.getId() == ship.getId()) {
            shipCommand.setChangeSpeed(delta);
        }
    }

    private void fillCommandToStopToEveryone(Scan scan, List<ShipCommand> shipCommandList) {
        for (ShipCommand shipCommand : shipCommandList) {
            long shipId = shipCommand.getId();
            Ship ship = utilService.findShipById(scan, shipId, false);
            fillCommandToStop(ship, shipCommand);
        }
    }

    public ShipCommands makeShipCommands(Scan scan) {
        ShipCommands shipCommands = buildShipCommands(scan);
        List<ShipCommand> shipCommandList = shipCommands.getShips();
        // TODO build strategy here
        fillShoots(scan, shipCommandList);
//        fillCommandToStopToEveryone(scan, shipCommandList);

        return shipCommands;
    }

    public void play() throws InterruptedException {
        ApiController apiController = new ApiController();
        AlertService alertService = new AlertService();

        Scan oldScan = null;
        while (true) {
            ScanResponse response = apiController.scan();
            if (!verify(response.isSuccess(), "success scan query")) continue;
            Scan newScan = response.getScan();
            if (!verify(newScan != null, "scan not null")) continue;
            if (oldScan != null && newScan.getTick() == oldScan.getTick()) continue;

            long selectedX = graphVisualizer.getSelectedX();
            long selectedY = graphVisualizer.getSelectedY();

            alertService.getAllInfos(oldScan, newScan).forEach(System.out::println);

            ShipCommands shipCommands = makeShipCommands(newScan);
            DefaultApiResponse shipCommandsResponse = apiController.shipCommand(shipCommands);
            if (shipCommandsResponse.getErrors() != null && !shipCommandsResponse.getErrors().isEmpty()) {
                shipCommandsResponse.getErrors().stream().map(Error::getMessage).forEach(System.out::println);
            }

            graphVisualizer.setMyShips(newScan.getMyShips());
            graphVisualizer.setEnemyShips(newScan.getEnemyShips());

            graphVisualizer.updateGraph();

            oldScan = newScan;
            Thread.sleep(Duration.ofSeconds(1).toMillis());
        }
    }
}
