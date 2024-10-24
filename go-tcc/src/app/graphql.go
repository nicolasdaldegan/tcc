package main

import (
	"bytes"
	"io"
	"log"
	"net/http"
	"time"
)

const GITHUB_GRAPHQL_URL = "https://api.github.com/graphql"

func doGraphQL(body string, token string) (int, int, time.Duration) {

	payload := bytes.NewBufferString(body)

	tamanho_request := payload.Len()

	req, _ := http.NewRequest(http.MethodPost, GITHUB_GRAPHQL_URL, payload)

	req.Header.Add("Content-Type", "application/json")
	req.Header.Add("Authorization", "Bearer "+token)

	time_init := time.Now()

	resp, err := client.Do(req)

	duration := time.Since(time_init)

	if err != nil {
		log.Printf("Erro ao executar requisição GraphQL: %v", err)
		return 0, 0, 0
	}

	defer resp.Body.Close()

	body_response, err := io.ReadAll(resp.Body)

	if err != nil {
		log.Printf("Erro lendo resposta GraphQL: %v", err)
		return 0, 0, 0
	}

	if resp.StatusCode != 200 {
		responseString := string(body_response)
		log.Printf("Erro ao executar requisição GraphQL, body: %v", responseString)
		return 0, 0, 0
	}

	return len(body_response), tamanho_request, duration
}
