package br.tcc;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {

        List<Analytics> list_analytics = new ArrayList<>();

        //query básica
        String query_1 = "{"
                + "\"query\": \"query { user(login: \\\"nicolasdaldegan\\\") { repositories(first: 5) { nodes { name url stargazerCount forkCount languages(first: 5) { nodes { name } } } } } }\""
                + "}";

        //Analytics analytics_graphql_1 = RequestGraphQL.doGraphQL(query_1);

        //query média
        String query_2 = "{"
                + "\"query\": \"query { repository(owner: \\\"nubank\\\", name: \\\"fklearn\\\") { issues(states: OPEN, first: 5) { nodes { title createdAt url author { login } } } } }\""
                + "}";

        //Analytics analytics_graphql_2 = RequestGraphQL.doGraphQL(query_2);

        //query complexa
        String query_3 = "{"
                + "\"query\": \"{ repository(owner: \\\"Netflix\\\", name: \\\"Hystrix\\\") { pullRequests(first: 5) { nodes { title url state createdAt updatedAt mergedAt closedAt number additions deletions changedFiles author { login url } assignees(first: 5) { nodes { login url } } labels(first: 5) { nodes { name color } } milestone { title dueOn description } comments(first: 5) { nodes { body author { login url } createdAt updatedAt } } commits(first: 5) { nodes { commit { message committedDate url author { name email date } } } } files(first: 5) { nodes { path additions deletions } } reviews(first: 5) { nodes { body author { login url } state submittedAt } } } } } }\""
                + "}";

        for (int i = 0; i < 100; i++) {
            Thread.sleep(1000);
            Analytics analytics_graphql_3 = RequestRest.doRest("users/Netflix/repos?per_page=5");
            list_analytics.add(analytics_graphql_3);
        }


        //Analytics analytics_graphql_4 = RequestRest.doRest("users/Netflix/repos?per_page=5");
        //list_analytics.add(analytics_graphql_1);
        //list_analytics.add(analytics_graphql_2);


        String fileName = "analytics_graphql.csv";

        int count = 1;

        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(fileName))) {
            for (Analytics analytics : list_analytics) {

                bufferedWriter.write(analytics.time_elapsed + ";" + analytics.size_payload_request + ";" + analytics.size_payload_response);
                bufferedWriter.newLine();
                count++;
            }
        }catch(IOException e) {
            e.printStackTrace();
        }

    }
}