package br.tcc;

import io.github.cdimascio.dotenv.Dotenv;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class RequestGraphQL {

    private static final String GITHUB_GRAPHQL_URL = "https://api.github.com/graphql";

    public static Analytics doGraphQL(String query, Integer type) throws IOException {

        Analytics analytics = new Analytics();

        Dotenv dotenv = Dotenv.load();
        String token = dotenv.get("TOKEN");

        URL url = new URL(GITHUB_GRAPHQL_URL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Authorization", "Bearer " + token);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        long time_init = System.currentTimeMillis();
        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = query.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        int responseCode = connection.getResponseCode();
        long time_end = System.currentTimeMillis();

        StringBuilder responseBody = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                responseBody.append(responseLine.trim());
            }
        }

        if (responseCode != HttpURLConnection.HTTP_OK) {
            throw new RuntimeException("Erro na requisição: " + responseBody.toString());
        }

        analytics.type_query = "Query " + type.toString();
        analytics.time_elapsed = time_end - time_init;
        analytics.size_payload_request = query.getBytes().length;
        analytics.size_payload_response = responseBody.toString().getBytes(StandardCharsets.UTF_8).length;

        return analytics;
    }
}
