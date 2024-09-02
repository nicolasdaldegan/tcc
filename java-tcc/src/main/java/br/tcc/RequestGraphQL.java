package br.tcc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class RequestGraphQL {

    private static final String GITHUB_GRAPHQL_URL = "https://api.github.com/graphql";
    private static final String TOKEN_AUTH = "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";

    public static Analytics doGraphQL(String query, boolean is_print) throws IOException {

        Analytics analytics = new Analytics();

        // Preparando a conexão HTTP
        URL url = new URL(GITHUB_GRAPHQL_URL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Authorization", "Bearer " + TOKEN_AUTH);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        // Enviando o corpo da requisição
        long time_init = System.currentTimeMillis();
        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = query.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        // Lendo a resposta
        int responseCode = connection.getResponseCode();
        long time_end = System.currentTimeMillis();

        StringBuilder responseBody = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"))) {
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                responseBody.append(responseLine.trim());
            }
        }

        // Verificando se a resposta foi bem-sucedida
        if (responseCode != HttpURLConnection.HTTP_OK) {
            throw new RuntimeException("Erro na requisição: " + responseBody.toString());
        }

        // Calculando os tempos e tamanhos
        analytics.time_elapsed = time_end - time_init;
        analytics.size_payload_request = query.getBytes().length;
        analytics.size_payload_response = responseBody.toString().getBytes().length;

        // Exibindo informações, se necessário
        if (is_print) {
            System.out.println("Tempo: " + analytics.time_elapsed);
            System.out.println("Size Request: " + analytics.size_payload_request);
            System.out.println("Size Response: " + analytics.size_payload_response);
        }

        return analytics;
    }
}
