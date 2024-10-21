package main

import (
	"bytes"
	"io"
	"log"
	"net/http"
	"time"
)

func doGraphQL(url_request string, body string, token string) (int, time.Duration) {

	payload := bytes.NewBufferString(body)

	req, _ := http.NewRequest(http.MethodPost, url_request, payload)

	req.Header.Add("Content-Type", "application/json")
	req.Header.Add("Authorization", "Bearer "+token)

	time_init := time.Now()

	resp, err := client.Do(req)

	duration := time.Since(time_init)

	if err != nil {
		log.Printf("Erro ao executar requisição GraphQL: %v", err)
		return 0, 0
	}

	defer resp.Body.Close()

	body_response, err := io.ReadAll(resp.Body)

	if err != nil {
		log.Printf("Erro lendo resposta GraphQL: %v", err)
		return 0, 0
	}

	return len(body_response), duration
}
