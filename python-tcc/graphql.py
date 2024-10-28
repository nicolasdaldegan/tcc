import json
import requests
import time

session = requests.Session()

GITHUB_GRAPHQL_URL = "https://api.github.com/graphql"

def do_graphql(body, token):

    headers = {
        "Content-Type": "application/json",
        "Authorization": f"Bearer {token}"
    }

    payload_request = len(body.encode('utf-8'))

    try:
        time_init = time.time()
        response = session.post(GITHUB_GRAPHQL_URL, data=body, headers=headers, timeout=10)
        duration = time.time() - time_init
    except requests.exceptions.RequestException as e:
        print(f"Erro ao executar requisição GraphQL: {e}")
        return 0, 0, 0


    if response.status_code == 200:
        body_response = response.content
        return len(body_response), payload_request, duration * 1000
    else:
        print(f"Erro na resposta GraphQL: {response.status_code}")
        print(f"Body: {response.text}")
        return 0, 0, 0