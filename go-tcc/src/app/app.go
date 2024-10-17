package main

import (
	"bytes"
	"fmt"
	"io"
	"log"
	"net/http"
	"os"
	"time"
)

var client = &http.Client{Timeout: 10 * time.Second}

func main() {
	var GITHUB_REST_URL string = "https://api.github.com/users/nicolasdaldegan/repos?per_page=5"

	file, err := os.OpenFile("resultados.txt", os.O_APPEND|os.O_CREATE|os.O_WRONLY, 0644)

	if err != nil {
		log.Fatalf("Erro ao abrir arquivo: %v", err)
	}

	defer file.Close()

	for i := 0; i < 10; i++ {
		size, time := doRest(GITHUB_REST_URL)
		result := fmt.Sprintf("REST -> Size: %d bytes, Time: %v\n", size, time)
		fmt.Print(result)
		writeToFile(file, result)
	}

	var GITHUB_GRAPHQL_URL string = "https://api.github.com/graphql"

	var body string = "{\"query\": \"query { user(login: \\\"nicolasdaldegan\\\") { repositories(first: 5) { nodes { name url stargazerCount forkCount languages(first: 5) { nodes { name } } } } } }\"}"

	for i := 0; i < 10; i++ {
		size, time := doGraphQL(GITHUB_GRAPHQL_URL, body)
		result := fmt.Sprintf("GraphQL -> Size: %d bytes, Time: %v\n", size, time)
		fmt.Print(result)
		writeToFile(file, result)
	}

}

func doGraphQL(url_request string, body string) (int, time.Duration) {

	payload := bytes.NewBufferString(body)

	req, err := http.NewRequest(http.MethodPost, url_request, payload)

	if err != nil {
		log.Printf("Erro ao criar a request: %v", err)
		return 0, 0
	}

	req.Header.Add("Content-Type", "application/json")
	req.Header.Add("Authorization", "Bearer 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")

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

func doRest(url_request string) (int, time.Duration) {

	time_init := time.Now()

	resp, err := client.Get(url_request)

	duration := time.Since(time_init)

	if err != nil {
		log.Printf("Erro ao executar requisição REST: %v", err)
		return 0, 0
	}

	defer resp.Body.Close()

	body, err := io.ReadAll(resp.Body)

	if err != nil {
		log.Printf("Erro lendo resposta REST: %v", err)
		return 0, 0
	}

	return len(body), duration

}

func writeToFile(file *os.File, data string) {
	if _, err := file.WriteString(data); err != nil {
		log.Printf("Erro ao escrever no arquivo: %v", err)
	}
}
