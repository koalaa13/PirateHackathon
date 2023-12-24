package org.example;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.api.ApiController;
import org.example.game.Game;
import org.example.model.IslandMap;
import org.example.model.Scan;
import org.example.model.Ship;
import org.example.model.command.ShipCommands;
import org.example.parse.MapParser;
import org.example.visual.FieldUI;
import org.example.visual.GraphVisualizer;
import org.example.visual.Visualizer;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws InterruptedException, JsonProcessingException {
        ApiController apiController = new ApiController();
        apiController.downloadMapFile(apiController.getMapUrl());

        MapParser mapParser = new MapParser();
        IslandMap islandMap = mapParser.parseIslandMap();
        GraphVisualizer graphVisualizer = new GraphVisualizer(islandMap);
        FieldUI fieldUI = new FieldUI();
        new Game(islandMap, graphVisualizer, fieldUI).play();

//        Visualizer visualizer = new Visualizer(islandMap, enemyShips, myShips);
//        visualizer.visualize(0, 0, 60);
    }
}
