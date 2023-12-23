package org.example;

import org.example.model.IslandMap;
import org.example.parse.MapParser;
import org.example.visual.Visualizer;

import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        MapParser mapParser = new MapParser();
        IslandMap islandMap = mapParser.parseIslandMap();
        Visualizer visualizer = new Visualizer(islandMap, new ArrayList<>(), new ArrayList<>());
        visualizer.visualize(0, 0, 30);
    }
}
