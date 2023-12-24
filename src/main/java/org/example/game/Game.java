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
import java.util.*;
import java.util.stream.Collectors;

import static java.lang.Math.*;

public class Game {
    private static class Pair<T, U> {
        public T first;
        public U second;

        Pair(T first, U second) {
            this.first = first;
            this.second = second;
        }
    }

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
    private List<Pair<Shoot, Long>> getPotentialShoots(Scan scan) {
        List<Pair<Shoot, Long>> potentialShoots = new ArrayList<>();
        List<Ship> sortedShipsViaHp = new ArrayList<>(scan.getEnemyShips());
        sortedShipsViaHp.sort((s1, s2) -> {
            if (s1.getHp() == s2.getHp()) {
                return -Integer.compare(s1.getSize(), s2.getSize());
            }
            return Integer.compare(s1.getHp(), s2.getHp());
        });
        for (Ship ship : sortedShipsViaHp) {
            potentialShoots.add(new Pair<>(new Shoot(ship.getForwardX(), ship.getForwardY()), (long) ship.getHp()));
        }
        potentialShoots = potentialShoots.stream()
                .filter(shoot -> !islandMap.contains(shoot.first.getX(), shoot.first.getY()))
                .collect(Collectors.toList());
        return potentialShoots;
    }

    /**
     * Выбираем кто куда стреляет
     * @param scan скан
     * @param shipCommandList лист ShipCommand, в которых нужно заполнить информацию о выстрелах
     */
    private void fillShoots(Scan scan, List<ShipCommand> shipCommandList) {
        List<Pair<Shoot, Long>> potentialShoots = getPotentialShoots(scan);
        if (!potentialShoots.isEmpty()) {
            for (int j = 0; j < scan.getMyShips().size(); ++j) {
                Ship ship = scan.getMyShips().get(j);
                if (ship.getCannonCooldownLeft() > 0) continue;
                for (int i = 0; i < potentialShoots.size(); i++) {
                    Shoot shoot = potentialShoots.get(i).first;
                    if (utilService.isNear(ship, shoot) && potentialShoots.get(i).second > 0) {
                        shipCommandList.get(j).setCannonShoot(shoot);
                        potentialShoots.set(i, new Pair<>(shoot, potentialShoots.get(i).second - 1));
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

        long speedChange = 0;
        if (dist <= MOVE_DESTINATION_RADIUS) {
            speedChange = -ship.getSpeed();
        } else {
            Ship.Direction direction = fillCommandToTurn(ship, shipCommand, targetX, targetY);

            boolean noIsland = true;
            for (int i = 0; i < 2 * ship.getMaxChangeSpeed() + ship.getSize(); i++) {
                long x = shipX + (long) direction.xDirection * i;
                long y = shipY + (long) direction.yDirection * i;
                if (islandMap.contains(x, y)) {
                    noIsland = false;
                    speedChange = -ship.getSpeed();
                    break;
                }
            }
            if (noIsland) {
                if (dist > 5 * ship.getMaxChangeSpeed()) {
                    speedChange = 2 * ship.getMaxChangeSpeed() - ship.getSpeed();
                } else {
                    speedChange = ship.getMaxChangeSpeed() - ship.getSpeed();
                }
            }
        }

        if (speedChange > ship.getMaxChangeSpeed()) speedChange = ship.getMaxChangeSpeed();
        if (speedChange < -ship.getMaxChangeSpeed()) speedChange = -ship.getMaxChangeSpeed();
        shipCommand.setChangeSpeed(speedChange);
    }

    public ShipCommands makeShipCommands(Scan scan, Map<Long, Long> moveX, Map<Long, Long> moveY) {
        ShipCommands shipCommands = buildShipCommands(scan);
        List<ShipCommand> shipCommandList = shipCommands.getShips();
        // TODO build strategy here
        fillShoots(scan, shipCommandList);
        Set<Ship.Cell> usedCells = new HashSet<>();
        for (ShipCommand shipCommand : shipCommandList) {
            Long shipId = shipCommand.getId();
            if (moveX.containsKey(shipId) && moveY.containsKey(shipId)) {
                Ship ship = utilService.findShipById(scan, shipId, false);
                fillCommandToMove(ship,
                        shipCommand,
                        moveX.get(shipId),
                        moveY.get(shipId)
                );
                boolean canMove = true;
                for (Ship.Cell cell : ship.getNextCells(shipCommand)) {
                    if (usedCells.contains(cell)) {
                        canMove = false;
                        break;
                    }
                }
                if (canMove) {
                    usedCells.addAll(ship.getNextCells(shipCommand));
                }
                if (!canMove) {
                    fillCommandToStop(ship, shipCommand);
                    shipCommand.setRotate(null);
                }
            }
        }
//        fillCommandToStopToEveryone(scan, shipCommandList);

        return shipCommands;
    }

    private void makeLongScanRequest(Scan scan) {
        Zone zone = scan.getZone();
        if (zone != null) {
            ApiController apiController = new ApiController();
            Random random = new Random();
            int diffX = random.nextInt((int) zone.getRadius() + 1);
            int diffY = random.nextInt((int) zone.getRadius() + 1);
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

        Map<Long, Long> moveX = new HashMap<>();
        Map<Long, Long> moveY = new HashMap<>();

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
            graphVisualizer.setZone(newScan.getZone());

            long selectedX = graphVisualizer.getSelectedX();
            long selectedY = graphVisualizer.getSelectedY();
            long selectedShip = fieldUI.getSelectedShip();

            if (selectedShip > 0 &&
                    newScan.getMyShips().stream().filter(ship -> ship.getId() == selectedShip).count() == 1) {
                moveX.put(selectedShip, selectedX);
                moveY.put(selectedShip, selectedY);
            }

            alertService.getAllInfos(oldScan, newScan).forEach(System.out::println);

            ShipCommands shipCommands = makeShipCommands(newScan, moveX, moveY);

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
