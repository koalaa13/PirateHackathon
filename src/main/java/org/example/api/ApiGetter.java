package org.example.api;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.example.model.Scan;
import org.example.model.ScanResponse;

import java.io.IOException;

public class ApiGetter {
    private static final String API_URL = "https://datsblack.datsteam.dev/api";

    private static final String API_AUTH_HEADER = "X-API-Key";

    private static final String API_KEY = "e8af3a6f-6d95-4d2b-b864-521ab93af2be";

    private final ObjectMapper objectMapper;

    public ApiGetter() {
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
}
