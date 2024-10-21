import time
import requests

session = requests.Session()

def do_rest(url_request, token):
    headers = {
        "Authorization": f"Bearer {token}"
    }

    time_init = time.time()

    try:
        response = session.get(url_request, headers=headers, timeout=10)
        duration = time.time() - time_init
    except requests.exceptions.RequestException as e:
        print(f"Erro ao executar requisição REST: {e}")
        return 0, 0

    if response.status_code == 200:
        body = response.content
        return len(body), duration * 1000
    else:
        print(f"Erro na resposta REST: {response.status_code}")
        return 0, 0