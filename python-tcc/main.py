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

    try:
        with open('resultados.txt', 'a') as file:
            if args.mode == "rest":
                run_rest(file, token)
            elif args.mode == "graphql":
                run_graphql(file, token)
            else:
                raise ValueError("Modo inválido. Use 'rest' ou 'graphql'.")
    except OSError as e:
        print(f"Erro ao abrir arquivo: {e}")

def run_rest(file, token):
    GITHUB_REST_URL = "https://api.github.com/repos/facebook/react"

    for _ in range(2):
        size, duration = do_rest(GITHUB_REST_URL, token)
        result = f"REST -> Size: {size} bytes, Time: {duration}\n"
        print(result)
        write_to_file(file, result)

def run_graphql(file, token):
    GITHUB_GRAPHQL_URL = "https://api.github.com/graphql"
    body = {
        "query": """
        query {
            repository(owner: "facebook", name: "react") { 
                name 
                description 
                stargazerCount 
            }
        }"""
    }

    for _ in range(10):
        size, duration = do_graphql(GITHUB_GRAPHQL_URL, body, token)
        result = f"GraphQL -> Size: {size} bytes, Time: {duration}\n"
        print(result)
        write_to_file(file, result)

def write_to_file(file, data):
    try:
        file.write(data)
    except OSError as e:
        print(f"Erro ao escrever no arquivo: {e}")

if __name__ == "__main__":
    main()