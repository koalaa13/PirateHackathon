package org.example.api;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.example.model.command.LongScanCommand;
import org.example.model.response.ScanResponse;
import org.example.model.command.ShipCommands;
import org.example.model.response.DefaultApiResponse;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.Buffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ApiController {
    private static final String API_URL = "https://datsblack.datsteam.dev/api";

    private static final String API_AUTH_HEADER = "X-API-Key";

    private static final String API_KEY = "e8af3a6f-6d95-4d2b-b864-521ab93af2be";

    private final ObjectMapper objectMapper;

    public ApiController() {
        this.objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public ScanResponse scan() {
        ScanResponse scanResponse = null;
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(API_URL + "/scan");
            request.setHeader(API_AUTH_HEADER, API_KEY);
            scanResponse = client.execute(request, response ->
                    objectMapper.readValue(response.getEntity().getContent(), ScanResponse.class)
            );
        } catch (IOException e) {
            System.err.println(e);
        }
        return scanResponse;
    }

    public DefaultApiResponse shipCommand(ShipCommands query) {
        DefaultApiResponse defaultApiResponse = null;
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            final String json = objectMapper.writeValueAsString(query);
            final StringEntity entity = new StringEntity(json);
            HttpPost request = new HttpPost(API_URL + "/shipCommand");
            request.setEntity(entity);
            request.setHeader(API_AUTH_HEADER, API_KEY);
            request.setHeader("Accept", "application/json");
            request.setHeader("Content-type", "application/json");

            defaultApiResponse = client.execute(request, response ->
                    objectMapper.readValue(response.getEntity().getContent(), DefaultApiResponse.class)
            );
        } catch (IOException e) {
            System.err.println(e);
        }
        return defaultApiResponse;
    }

    public DefaultApiResponse longScan(long x, long y) {
        DefaultApiResponse defaultApiResponse = null;
        LongScanCommand longScanCommand = new LongScanCommand(x, y);
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            final String json = objectMapper.writeValueAsString(longScanCommand);
            final StringEntity entity = new StringEntity(json);
            HttpPost request = new HttpPost(API_URL + "/longScan");
            request.setEntity(entity);
            request.setHeader(API_AUTH_HEADER, API_KEY);
            request.setHeader("Accept", "application/json");
            request.setHeader("Content-type", "application/json");

            defaultApiResponse = client.execute(request,
                    response -> objectMapper.readValue(response.getEntity().getContent(), DefaultApiResponse.class)
            );
        } catch (IOException e) {
            System.err.println(e);
        }
        return defaultApiResponse;
    }

    public String getMapUrl() {
        String link = null;
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(API_URL + "/map");
            request.setHeader(API_AUTH_HEADER, API_KEY);

            link = client.execute(request, response -> objectMapper.readTree(response.getEntity().getContent())).get("mapUrl").asText();
        } catch (IOException e) {
            System.err.println(e);
        }
        return link;
    }

    public void downloadMapFile(String link) {
        try {
            URL url = new URL(link);
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
            BufferedWriter writer = Files.newBufferedWriter(Paths.get("src/main/resources/island_map.json"), StandardCharsets.UTF_8);

            String inputLine;
            while ((inputLine = reader.readLine()) != null) {
                writer.write(inputLine);
                writer.newLine();
            }
            reader.close();
            writer.close();
        } catch (IOException e) {
            System.err.println(e);
        }
    }
}
