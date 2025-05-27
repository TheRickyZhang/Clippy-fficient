package com.example.ui.api;

import com.example.config.Env;
import com.example.core.logging.LogService;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

public class ApiClient {
    private final HttpClient client = HttpClient.newHttpClient();

    private final ObjectMapper mapper = new ObjectMapper();
    private final String baseUrl = Env.get("api.baseUrl");

    Logger log = LogService.get().forClass(ApiClient.class);

    public CompletableFuture<HttpResponse<String>> postJson(String endpoint, Map<String, String> payload) throws Exception {
        log.info("Sending payload " + payload + " to endpoint " + endpoint);
        String json = mapper.writeValueAsString(payload);
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + endpoint))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        return client.sendAsync(req, HttpResponse.BodyHandlers.ofString());
    }
}
