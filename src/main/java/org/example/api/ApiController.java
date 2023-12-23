package org.example.api;

import com.fasterxml.jackson.databind.DeserializationFeature;
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

import java.io.IOException;

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
}
