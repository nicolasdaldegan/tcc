import time
import requests

session = requests.Session()

GITHUB_REST_URL = "https://api.github.com/"

def do_rest(url_request, token):

    headers = {
        "Authorization": f"Bearer {token}"
    }

    url = GITHUB_REST_URL + url_request

    try:
        time_init = time.time()
        response = session.get(url, headers=headers, timeout=10)
        duration = time.time() - time_init
    except requests.exceptions.RequestException as e:
        print(f"Erro ao executar requisição REST: {e}")
        return 0, 0, 0

    if response.status_code == 200:
        body = response.content
        return len(body), len(url.encode('utf-8')), duration * 1000
    else:
        print(f"Erro na resposta REST: {response.status_code}")
        return 0, 0, 0