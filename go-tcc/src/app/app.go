package main

import (
	"flag"
	"fmt"
	"log"
	"net/http"
	"os"
	"strings"
	"time"

	"github.com/joho/godotenv"
)

var client = &http.Client{Timeout: 10 * time.Second}

func main() {

	err := godotenv.Load()

	if err != nil {
		log.Fatal("Error loading .env file")
	}

	token := os.Getenv("TOKEN")

	mode := flag.String("mode", "rest", "Escolha entre 'rest' ou 'graphql'")
	flag.Parse()

	if *mode == "rest" {

		file, err := os.Create("resultados-rest-go.txt")

		if err != nil {
			log.Fatalf("Erro ao abrir arquivo: %v", err)
		}

		defer file.Close()

		runRest(file, token)

	} else if *mode == "graphql" {

		file, err := os.Create("resultados-graphql-go.txt")

		if err != nil {
			log.Fatalf("Erro ao abrir arquivo: %v", err)
		}

		defer file.Close()

		runGraphQL(file, token)

	} else {
		log.Fatalf("Modo inválido. Use 'rest' ou 'graphql'.")
	}

}

func runRest(file *os.File, token string) {

	var queries [][]string

	query_simples := []string{}
	query_media := []string{}
	query_grande := []string{}

	//query simples
	queryDefault := "repos/microsoft/vscode"
	query_simples = append(query_simples, queryDefault)

	//query media
	queryDefault = "repos/vercel/next.js"
	query_media = append(query_media, queryDefault)

	queryDefault = "repos/vercel/next.js/issues?state=open&per_page=2"
	query_media = append(query_media, queryDefault)

	//query grande
	queryDefault = "repos/facebook/react"
	query_grande = append(query_grande, queryDefault)

	queryDefault = "repos/facebook/react/issues?state=open&per_page=2"
	query_grande = append(query_grande, queryDefault)

	queryDefault = "repos/facebook/react/pulls?state=all&per_page=2"
	query_grande = append(query_grande, queryDefault)

	queryDefault = "repos/facebook/react/contributors?per_page=2"
	query_grande = append(query_grande, queryDefault)

	queries = append(queries, query_simples)
	queries = append(queries, query_media)
	queries = append(queries, query_grande)

	//aquecer
	for i := 0; i < 10; i++ {
		_, _, _ = doRest(queryDefault, token)
	}

	cont := 1

	var size_response_total int
	var size_request_total int
	var time_total time.Duration

	var results []string

	for _, list_query := range queries {

		for i := 0; i < 100; i++ {

			size_response_total = 0
			size_request_total = 0
			time_total = 0

			for _, query := range list_query {
				size_response, size_request, time := doRest(query, token)

				size_response_total += size_response
				size_request_total += size_request
				time_total += time
			}

			result := fmt.Sprintf("Query %d; Tempo: %v; Payload Request: %d; Payload Response: %d\n", cont, time_total, size_request_total, size_response_total)
			fmt.Print(result)
			results = append(results, result)
		}
		cont++
	}

	data := strings.Join(results, "")
	writeToFile(file, data)
}

func runGraphQL(file *os.File, token string) {

	queries := []string{}

	var query1 string = "{\"query\": \"query { repository(owner: \\\"microsoft\\\", name: \\\"vscode\\\") { name stargazerCount description createdAt updatedAt forkCount diskUsage homepageUrl isPrivate isArchived isFork primaryLanguage { name } pushedAt watchers { totalCount } } }\"}"

	var query2 string = "{\"query\": \"query { repository(owner: \\\"vercel\\\", name: \\\"next.js\\\") { name description stargazerCount forkCount diskUsage homepageUrl isPrivate isArchived isFork isTemplate licenseInfo { name } primaryLanguage { name } pushedAt openGraphImageUrl issues(first: 2, states: OPEN) { nodes { title createdAt bodyText number state updatedAt author { login } } } } }\"}"

	var query3 string = "{\"query\": \"query { repository(owner: \\\"facebook\\\", name: \\\"react\\\") { name stargazerCount description createdAt updatedAt forkCount watchers { totalCount } issues(states: OPEN, first: 2) { edges { node { title createdAt bodyText number state updatedAt authorAssociation comments { totalCount } } } } pullRequests(last: 2) { edges { node { title createdAt mergedAt additions deletions changedFiles number state updatedAt isDraft mergeable comments { totalCount } reviews { totalCount } } } } mentionableUsers(first: 2) { edges { node { login bio avatarUrl location company email createdAt isEmployee repositories { totalCount } } } } } }\"}"

	queries = append(queries, query1)
	queries = append(queries, query2)
	queries = append(queries, query3)

	//aquecer
	for i := 0; i < 10; i++ {
		_, _, _ = doGraphQL(query1, token)
	}

	cont := 1

	var results []string

	for _, query := range queries {
		for i := 0; i < 100; i++ {
			size_response, size_request, time := doGraphQL(query, token)
			result := fmt.Sprintf("Query %d; Tempo: %v; Payload Request: %d; Payload Response: %d\n", cont, time, size_request, size_response)
			fmt.Print(result)
			results = append(results, result)
		}
		cont++
	}

	data := strings.Join(results, "")
	writeToFile(file, data)

}

func writeToFile(file *os.File, data string) {
	if _, err := file.WriteString(data); err != nil {
		log.Printf("Erro ao escrever no arquivo: %v", err)
	}
}
