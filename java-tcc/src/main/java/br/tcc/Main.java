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
            if (arg.startsWith("--mode=")) {
                return arg.substring(7);
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

        //query básica
        //repos/microsoft/vscode
        String query_default = "repos/microsoft/vscode";
        query_1.add(query_default);

        //query média
        //repos/microsoft/vscode
        //repos/microsoft/vscode/issues?state=open&per_page=2
        query_default = "repos/microsoft/vscode";
        query_2.add(query_default);

        query_default = "repos/microsoft/vscode/issues?state=open&per_page=2";
        query_2.add(query_default);

        //query grande
        //repos/microsoft/vscode
        //repos/microsoft/vscode/issues?state=open&per_page=2
        //repos/microsoft/vscode/pulls?state=all&per_page=2
        //repos/microsoft/vscode/contributors?per_page=2
        query_default = "repos/microsoft/vscode";
        query_3.add(query_default);

        query_default = "repos/microsoft/vscode/issues?state=open&per_page=2";
        query_3.add(query_default);

        query_default = "repos/microsoft/vscode/pulls?state=all&per_page=2";
        query_3.add(query_default);

        query_default = "repos/microsoft/vscode/contributors?per_page=2";
        query_3.add(query_default);

        queries.add(query_1);
        queries.add(query_2);
        queries.add(query_3);

        Analytics analytics_rest;

        //Aquecer
        for (int i = 0; i < 10; i++) {
            analytics_rest = RequestRest.doRest(query_default, 1);
        }

        Integer cont = 1;

        for(List<String> query : queries) {
            for (int i = 0; i < 100; i++) {

                Analytics analytics_use = new Analytics();

                for(String get : query) {
                    analytics_rest = RequestRest.doRest(get, cont);

                    analytics_use.type_query = analytics_rest.type_query;
                    analytics_use.size_payload_request += analytics_rest.size_payload_request;
                    analytics_use.size_payload_response += analytics_rest.size_payload_response;
                    analytics_use.time_elapsed += analytics_rest.time_elapsed;
                }

                String result = String.format("Query %d; Tempo: %.2f; Payload Request: %.2f; Payload Response: %.2f\n",
                    cont, analytics_use.time_elapsed, analytics_use.size_payload_request, analytics_use.size_payload_response);
                System.out.println(result);
                list_analytics.add(analytics_use);
            }
            cont++;
        }

        return list_analytics;
    }

    public static List<Analytics> runGraphQL() throws IOException, InterruptedException {

        List<Analytics> list_analytics = new ArrayList<>();
        List<String> queries = new ArrayList<>();

        // query básica
        String query_1 = "{"
            + "\"query\": \"query { repository(owner: \\\"microsoft\\\", name: \\\"vscode\\\") { "
            + "name "
            + "stargazerCount "
            + "description "
            + "createdAt "
            + "forkCount "
            + "pushedAt "
            + "} }\""
            + "}";

        // query média
        String query_2 = "{"
            + "\"query\": \"query { repository(owner: \\\"microsoft\\\", name: \\\"vscode\\\") { "
            + "name "
            + "description "
            + "stargazerCount "
            + "forkCount "
            + "diskUsage "
            + "homepageUrl "
            + "isPrivate "
            + "isArchived "
            + "isFork "
            + "isTemplate "
            + "licenseInfo { name } "
            + "primaryLanguage { name } "
            + "pushedAt "
            + "openGraphImageUrl "
            + "issues(first: 2, states: OPEN) { "
            + "nodes { "
            + "title "
            + "createdAt "
            + "bodyText "
            + "number "
            + "state "
            + "updatedAt "
            + "author { login } "
            + "} "
            + "} "
            + "} }\""
            + "}";

        // query complexa
        String query_3 = "{"
            + "\"query\": \"query { repository(owner: \\\"microsoft\\\", name: \\\"vscode\\\") { "
            + "name "
            + "stargazerCount "
            + "description "
            + "createdAt "
            + "updatedAt "
            + "forkCount "
            + "watchers { totalCount } "
            + "issues(states: OPEN, first: 2) { "
            + "edges { "
            + "node { "
            + "title "
            + "createdAt "
            + "bodyText "
            + "number "
            + "state "
            + "updatedAt "
            + "authorAssociation "
            + "comments { totalCount } "
            + "} "
            + "} "
            + "} "
            + "pullRequests(last: 2) { "
            + "edges { "
            + "node { "
            + "title "
            + "createdAt "
            + "mergedAt "
            + "additions "
            + "deletions "
            + "changedFiles "
            + "number "
            + "state "
            + "updatedAt "
            + "isDraft "
            + "mergeable "
            + "comments { totalCount } "
            + "reviews { totalCount } "
            + "} "
            + "} "
            + "} "
            + "mentionableUsers(first: 2) { "
            + "edges { "
            + "node { "
            + "login "
            + "bio "
            + "avatarUrl "
            + "location "
            + "company "
            + "email "
            + "createdAt "
            + "isEmployee "
            + "repositories { totalCount } "
            + "} "
            + "} "
            + "} "
            + "} }\""
            + "}";

        queries.add(query_1);
        queries.add(query_2);
        queries.add(query_3);

        Analytics analytics_graphql;

        //Aquecer
        for (int i = 0; i < 10; i++) {
            analytics_graphql = RequestGraphQL.doGraphQL(query_1, 1);
        }

        Integer cont = 1;

        for(String query : queries) {
            for (int i = 0; i < 100; i++) {
                analytics_graphql = RequestGraphQL.doGraphQL(query, cont);
                list_analytics.add(analytics_graphql);
                String result = String.format("Query %d; Tempo: %.2f; Payload Request: %.2f; Payload Response: %.2f\n",
                              cont, analytics_graphql.time_elapsed, analytics_graphql.size_payload_request, analytics_graphql.size_payload_response);
                System.out.println(result);
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