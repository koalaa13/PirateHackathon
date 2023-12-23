package org.example.parse;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import org.example.model.IslandMap;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class MapParser {
    private final ObjectMapper objectMapper;

    private static final String WIDTH_KEY = "width";

    private static final String HEIGHT_KEY = "height";

    private static final String ISLAND_KEY = "islands";

    private static final String START_KEY = "start";

    private static final String MAP_KEY = "map";

    private static final String DEFAULT_FILENAME = "island_map.json";


    private final ObjectReader mapReader;

    private final ObjectReader startReader;

    public MapParser() {
        this.objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        this.mapReader = objectMapper.readerFor(new TypeReference<List<List<Integer>>>() {
        });

        this.startReader = objectMapper.readerFor(new TypeReference<List<Integer>>() {
        });
    }

    public IslandMap parseIslandMap() {
        return parseIslandMap(DEFAULT_FILENAME);
    }

    public IslandMap parseIslandMap(String fileName) {
        IslandMap islandMap = null;
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        try (InputStream is = classLoader.getResourceAsStream(fileName)) {
            JsonNode jsonNode = objectMapper.readTree(is);
            long width = jsonNode.get(WIDTH_KEY).asLong();
            long height = jsonNode.get(HEIGHT_KEY).asLong();

            islandMap = new IslandMap(height, width);
            JsonNode islands = jsonNode.get(ISLAND_KEY);
            for (JsonNode island : islands) {
                long x = ((List<Integer>) startReader.readValue(island.get(START_KEY))).get(0);
                long y = ((List<Integer>) startReader.readValue(island.get(START_KEY))).get(1);
                List<List<Integer>> map = mapReader.readValue(island.get(MAP_KEY));
                for (int i = 0; i < map.size(); ++i) {
                    for (int j = 0; j < map.get(i).size(); ++j) {
                        if (map.get(i).get(j) == 1) {
                            islandMap.addIslandTile(x + j, y + i);
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.err.println(e);
        }
        if (islandMap != null) {
            // frame in borders of map
            for (int i = -1; i <= islandMap.getHeight(); ++i) {
                islandMap.addIslandTile(-1, i);
                islandMap.addIslandTile(islandMap.getWidth(), i);
            }
            for (int i = -1; i <= islandMap.getWidth(); ++i) {
                islandMap.addIslandTile(i, -1);
                islandMap.addIslandTile(i, islandMap.getHeight());
            }
        }
        return islandMap;
    }
}
