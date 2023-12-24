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
import org.example.visual.FieldUI;
import org.example.visual.GraphVisualizer;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import static java.lang.Math.abs;
import static java.lang.Math.max;

public class Game {
    private final IslandMap islandMap;
    private FieldUI fieldUI;

    private final UtilService utilService;

    private final GraphVisualizer graphVisualizer;

    public Game(IslandMap islandMap, GraphVisualizer graphVisualizer, FieldUI fieldUI) {
        this.islandMap = islandMap;
        this.fieldUI = fieldUI;
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

    private void fillCommandNewDirection(Ship ship, ShipCommand shipCommand, Ship.Direction direction) {
        Ship.Direction curDirection = ship.getDirection();
        if (curDirection.xDirection == direction.xDirection || curDirection.yDirection == direction.yDirection) return;
        if (direction.xDirection == -curDirection.yDirection && direction.yDirection == curDirection.xDirection) {
            shipCommand.setRotate(90);
        } else {
            shipCommand.setRotate(-90);
        }
    }

    private Ship.Direction fillCommandToTurn(Ship ship, ShipCommand shipCommand, long targetX, long targetY) {
        long shipX = ship.getX();
        long shipY = ship.getY();

        long dx = targetX - shipX;
        long dy = targetY - shipY;

        Ship.Direction newDirection;
        if (abs(dx) > abs(dy)) {
            if (dx > 0) {
                newDirection = Ship.Direction.east;
            } else {
                newDirection = Ship.Direction.west;
            }
        } else {
            if (dy > 0) {
                newDirection = Ship.Direction.south;
            } else {
                newDirection = Ship.Direction.north;
            }
        }

        fillCommandNewDirection(ship, shipCommand, newDirection);
        return newDirection;
    }

    private static final long MOVE_DESTINATION_RADIUS = 30;

    private void fillCommandToMove(Ship ship, ShipCommand shipCommand, long targetX, long targetY) {
        long shipX = ship.getX();
        long shipY = ship.getY();
        long dx = targetX - shipX;
        long dy = targetY - shipY;
        long dist = max(abs(dx), abs(dy));

        if (dist <= MOVE_DESTINATION_RADIUS) {
            shipCommand.setChangeSpeed(-ship.getMaxChangeSpeed());
        } else {
            Ship.Direction direction = fillCommandToTurn(ship, shipCommand, targetX, targetY);

            for (int i = 0; i < 2 * ship.getMaxChangeSpeed() + ship.getSize(); i++) {
                long x = shipX + (long) direction.xDirection * i;
                long y = shipY + (long) direction.yDirection * i;
                if (islandMap.contains(x, y)) {
                    shipCommand.setChangeSpeed(-ship.getMaxChangeSpeed());
                    return;
                }
            }
            shipCommand.setChangeSpeed(ship.getMaxChangeSpeed() - ship.getSpeed());
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

    private void makeLongScanRequest(Scan scan) {
        Zone zone = scan.getZone();
        if (zone != null) {
            ApiController apiController = new ApiController();
            Random random = new Random();
            int diffX = random.nextInt((int) zone.getRadius());
            int diffY = random.nextInt((int) zone.getRadius());
            int signX = random.nextBoolean() ? 1 : -1;
            int signY = random.nextBoolean() ? 1 : -1;
            long x = zone.getX() + signX * diffX;
            long y = zone.getY() + signY * diffY;
            apiController.longScan(x, y);
        }
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

            if (newScan.getTick() % 16 == 0) {
                makeLongScanRequest(newScan);
            }

            long selectedX = graphVisualizer.getSelectedX();
            long selectedY = graphVisualizer.getSelectedY();
            long selectedShip = fieldUI.getSelectedShip();

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
