package org.example;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.game.Game;
import org.example.model.IslandMap;
import org.example.model.Scan;
import org.example.model.Ship;
import org.example.model.command.ShipCommands;
import org.example.parse.MapParser;
import org.example.visual.Visualizer;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws InterruptedException, JsonProcessingException {
        MapParser mapParser = new MapParser();
        IslandMap islandMap = mapParser.parseIslandMap();
        Ship myShip = new Ship();
        myShip.setId(1L);
        myShip.setX(20);
        myShip.setY(20);
        myShip.setSpeed(10);
        myShip.setMaxChangeSpeed(20);
        myShip.setCannonRadius(100);
        myShip.setSize(5);
        myShip.setDirection(Ship.Direction.north);
        List<Ship> myShips = new ArrayList<>();
        myShips.add(myShip);

        Ship enemyShip = new Ship();
        enemyShip.setId(2L);
        enemyShip.setX(30);
        enemyShip.setY(30);
        enemyShip.setSize(5);
        enemyShip.setDirection(Ship.Direction.east);
        List<Ship> enemyShips = new ArrayList<>();
        enemyShips.add(enemyShip);

        Scan scan = new Scan();
        scan.setMyShips(myShips);
        scan.setEnemyShips(enemyShips);

        ShipCommands shipCommands = new Game(islandMap).makeShipCommands(scan);
        System.out.println(new ObjectMapper().writeValueAsString(shipCommands));

//        Visualizer visualizer = new Visualizer(islandMap, enemyShips, myShips);
//        visualizer.visualize(0, 0, 60);
    }
}
