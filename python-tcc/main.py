import os
import argparse
from dotenv import load_dotenv
from graphql import do_graphql
from rest import do_rest

load_dotenv()

def main():
    token = os.getenv("TOKEN")

    parser = argparse.ArgumentParser(description="Escolha entre 'rest' ou 'graphql'.")
    parser.add_argument('--mode', default='rest', choices=['rest', 'graphql'], help="Modo: 'rest' ou 'graphql'.")
    args = parser.parse_args()

    print(f"args: {args.mode}")

    if args.mode == "rest":
        try:
            with open('resultados-rest-python.txt', 'a') as file:
                run_rest(file, token)
        except OSError as e:
            print(f"Erro ao abrir arquivo: {e}")
    elif args.mode == "graphql":
        try:
            with open('resultados-graphql-python.txt', 'a') as file:
                run_graphql(file, token)
        except OSError as e:
            print(f"Erro ao abrir arquivo: {e}")
    else:
        raise ValueError("Modo inválido. Use 'rest' ou 'graphql'.")
   

def run_rest(file, token):
    
    queries = []

    query_simples = []
    query_media = []
    query_grande = []

    # Query simples
    queryDefault = "repos/microsoft/vscode"
    query_simples.append(queryDefault)

    # Query média
    queryDefault = "repos/vercel/next.js"
    query_media.append(queryDefault)

    queryDefault = "repos/vercel/next.js/issues?state=open&per_page=2"
    query_media.append(queryDefault)

    # Query grande
    queryDefault = "repos/facebook/react"
    query_grande.append(queryDefault)

    queryDefault = "repos/facebook/react/issues?state=open&per_page=2"
    query_grande.append(queryDefault)

    queryDefault = "repos/facebook/react/pulls?state=all&per_page=2"
    query_grande.append(queryDefault)

    queryDefault = "repos/facebook/react/contributors?per_page=2"
    query_grande.append(queryDefault)

    queries.append(query_simples)
    queries.append(query_media)
    queries.append(query_grande)

    # Aquecer
    for _ in range(10):
        _ , _, _ = do_rest(queryDefault, token)

    cont = 1

    for list_queries in queries:
        for _ in range(3):

            size_response_total = 0
            size_request_total = 0
            time_total = 0

            for query in list_queries:
                size_response, size_request, duration = do_rest(query, token)

                size_response_total += size_response
                size_request_total += size_request
                time_total += duration

            result = f"Query {cont}; Tempo: {time_total}; Payload Request: {size_request_total}; Payload Response: {size_response_total}\n"
            print(result)
            write_to_file(file, result)

        cont += 1

    
        

def run_graphql(file, token):

    queries = []

    query1 = (
        "{\"query\": \"query { repository(owner: \\\"microsoft\\\", name: \\\"vscode\\\") { "
        "name stargazerCount "
        "} }\"}"
    )

    query2 = (
        "{\"query\": \"query { repository(owner: \\\"vercel\\\", name: \\\"next.js\\\") { "
        "name description "
        "issues(first: 2, states: OPEN) { nodes { title createdAt } } "
        "} }\"}"
    )

    query3 = (
        "{\"query\": \"query { repository(owner: \\\"facebook\\\", name: \\\"react\\\") { "
        "name stargazerCount "
        "issues(states: OPEN, first: 2) { edges { node { title createdAt } } } "
        "pullRequests(last: 2) { edges { node { title mergedAt } } } "
        "mentionableUsers(first: 2) { edges { node { login } } } "
        "} }\"}"
    )

    queries.append(query1)
    queries.append(query2)
    queries.append(query3)

    # Aquecer
    for _ in range(10):
        _ , _, _ = do_graphql(query1, token)

    cont = 1

    for query in queries:
        for _ in range(3):
            size_response, size_request, duration = do_graphql(query, token)
            result = f"Query {cont}; Tempo: {duration}; Payload Request: {size_request}; Payload Response: {size_response}\n"
            print(result)
            write_to_file(file, result)
        cont += 1
        

def write_to_file(file, data):
    try:
        file.write(data)
    except OSError as e:
        print(f"Erro ao escrever no arquivo: {e}")

if __name__ == "__main__":
    main()