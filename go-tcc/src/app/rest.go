package main

import (
	"bytes"
	"io"
	"log"
	"net/http"
	"time"
)

func doRest(url_request string, token string) (int, int, time.Duration) {

	req, _ := http.NewRequest("GET", url_request, nil)

	req.Header.Set("Authorization", "Bearer "+token)

	var buf bytes.Buffer

	if err := req.Write(&buf); err != nil {
		log.Printf("Erro ao escrever requisição: %v", err)
		return 0, 0, 0
	}

	totalRequestSize := buf.Len()

	time_init := time.Now()

	resp, err := client.Do(req)

	duration := time.Since(time_init)

	if err != nil {
		log.Printf("Erro ao executar requisição REST: %v", err)
		return 0, 0, 0
	}

	defer resp.Body.Close()

	body, err := io.ReadAll(resp.Body)

	if err != nil {
		log.Printf("Erro lendo resposta REST: %v", err)
		return 0, 0, 0
	}

	if resp.StatusCode != 200 {
		bodyString := string(body)
		log.Printf("Erro ao executar requisição REST, body: %v", bodyString)
		return 0, 0, 0
	}

	return len(body), totalRequestSize, duration

}
