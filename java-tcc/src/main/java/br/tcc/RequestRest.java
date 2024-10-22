package br.tcc;

import io.github.cdimascio.dotenv.Dotenv;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class RequestRest {

    private static final String GITHUB_REST_URL = "https://api.github.com/";

    public static Analytics doRest(String path, Integer type) throws IOException {

        Dotenv dotenv = Dotenv.load();
        String token = dotenv.get("TOKEN");

        Analytics analytics = new Analytics();
        String restEndpoint = GITHUB_REST_URL + path;

        URL url = new URL(restEndpoint);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization", "Bearer " + token);

        long time_init = System.currentTimeMillis();

        int responseCode = connection.getResponseCode();
        long time_end = System.currentTimeMillis();

        if (responseCode != HttpURLConnection.HTTP_OK) {
            throw new RuntimeException("Erro na requisição: " + connection.getResponseMessage());
        }

        StringBuilder responseBody = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"))) {
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                responseBody.append(responseLine.trim());
            }
        }

        String body = responseBody.toString();

        analytics.type_query = "Query " + type.toString();
        analytics.time_elapsed = time_end - time_init;
        analytics.size_payload_request = restEndpoint.getBytes().length;
        analytics.size_payload_response = body.getBytes().length;

        System.out.println("Query "+ type);
        System.out.println("Tempo: " + analytics.time_elapsed);
        System.out.println("Size Request: " + analytics.size_payload_request);
        System.out.println("Size Response: " + analytics.size_payload_response);

        return analytics;
    }
}
