import requests
import time

session = requests.Session()

def do_graphql(url_request, body, token):
    headers = {
        "Content-Type": "application/json",
        "Authorization": f"Bearer {token}"
    }

    time_init = time.time()

    try:
        response = session.post(url_request, json=body, headers=headers, timeout=10)
        duration = time.time() - time_init
    except requests.exceptions.RequestException as e:
        print(f"Erro ao executar requisição GraphQL: {e}")
        return 0, 0

    if response.status_code == 200:
        body_response = response.content
        return len(body_response), duration * 1000
    else:
        print(f"Erro na resposta GraphQL: {response.status_code}")
        return 0, 0