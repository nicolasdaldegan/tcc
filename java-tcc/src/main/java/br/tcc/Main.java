package br.tcc;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {

        if (args.length > 0) {

            String mode = getModeFromArgs(args);

            String file_name = "";

            List<Analytics> analytics = null;

            if (mode != null) {
                switch (mode) {
                    case "rest":
                        System.out.println("Executando REST.");
                        analytics = runRest();
                        file_name = "analytics_rest.txt";
                        break;

                    case "graphql":
                        System.out.println("Executando GRAPHQL.");
                        analytics = runGraphQL();
                        file_name = "analytics_graphql.txt";
                        break;

                    default:
                        System.out.println("Modo desconhecido. Use 'mode=rest' ou 'mode=graphql'.");
                        break;
                }
            }

            if(analytics != null && !file_name.equals("")) {
                writeAnalyticsToFile(analytics, file_name);
            }

        }else{
            System.out.println("Nenhum argumento foi passado.");
        }

    }

    public static String getModeFromArgs(String[] args) {
        for (String arg : args) {
            if (arg.startsWith("mode=")) {
                return arg.substring(5);
            }
        }
        return null;
    }

    public static List<Analytics> runRest() throws InterruptedException, IOException {

        List<Analytics> list_analytics = new ArrayList<>();

        List<List<String>> queries = new ArrayList<>();

        List<String> query_1 = new ArrayList<>();
        List<String> query_2 = new ArrayList<>();
        List<String> query_3 = new ArrayList<>();

        //1° get
        String query_default = "repos/facebook/react";
        query_1.add(query_default);

        //2° get
        //repos/facebook/react
        //repos/facebook/react/forks?per_page=3
        //repos/facebook/react/issues?state=open&per_page=3
        query_default = "repos/facebook/react";
        query_2.add(query_default);

        query_default = "repos/facebook/react/forks?per_page=3";
        query_2.add(query_default);

        query_default = "repos/facebook/react/issues?state=open&per_page=3";
        query_2.add(query_default);

        //3° get
        //repos/facebook/react
        //repos/facebook/react/forks?per_page=10
        //repos/facebook/react/issues?state=open&per_page=10
        //repos/facebook/react/pulls?per_page=5
        query_default = "repos/facebook/react";
        query_3.add(query_default);

        query_default = "repos/facebook/react/forks?per_page=10";
        query_3.add(query_default);

        query_default = "repos/facebook/react/issues?state=open&per_page=10";
        query_3.add(query_default);

        query_default = "repos/facebook/react/pulls?per_page=5";
        query_3.add(query_default);

        queries.add(query_1);
        queries.add(query_2);
        queries.add(query_3);

        Analytics analytics_rest;

        Integer cont = 1;

        for(List<String> query : queries) {
            for (int i = 0; i < 10; i++) {
                for(String get : query) {
                    analytics_rest = RequestRest.doRest(get, cont);
                    list_analytics.add(analytics_rest);
                }
            }
            cont++;
        }

        return list_analytics;
    }

    public static List<Analytics> runGraphQL() throws IOException, InterruptedException {

        List<Analytics> list_analytics = new ArrayList<>();
        List<String> queries = new ArrayList<>();

        //query básica
        String query_1 = "{"
                + "\"query\": \"query { repository(owner: \\\"facebook\\\", name: \\\"react\\\") { name description stargazerCount } }\""
                + "}";

        //query média
        String query_2 = "{"
                + "\"query\": \"query { repository(owner: \\\"facebook\\\", name: \\\"react\\\") { name stargazerCount forks(first: 5) { nodes { name owner { login } } } issues(states: OPEN, first: 5) { nodes { title createdAt url author { login } } } } }\""
                + "}";

        //query complexa
        String query_3 = "{"
                + "\"query\": \"query { repository(owner: \\\"nubank\\\", name: \\\"fklearn\\\") { issues(states: OPEN, first: 10) { nodes { title createdAt url author { login } labels(first: 5) { nodes { name } } comments(first: 5) { nodes { author { login } body } } } } } }\""
                + "}";

        queries.add(query_1);
        queries.add(query_2);
        queries.add(query_3);

        Analytics analytics_graphql;

        Integer cont = 1;

        for(String query : queries) {
            for (int i = 0; i < 1; i++) {
                Thread.sleep(1000);
                analytics_graphql = RequestGraphQL.doGraphQL(query, cont);
                list_analytics.add(analytics_graphql);
            }
            cont++;
        }

        return list_analytics;
    }


    public static void writeAnalyticsToFile(List<Analytics> list_analytics, String fileName) {
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(fileName))) {
            for (Analytics analytics : list_analytics) {
                bufferedWriter.write(analytics.type_query + "; Tempo:" + analytics.time_elapsed + "; Payload Request:" + analytics.size_payload_request + "; Payload Response:" + analytics.size_payload_response);
                bufferedWriter.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}