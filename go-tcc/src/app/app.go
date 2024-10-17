package main

import (
	"flag"
	"fmt"
	"log"
	"net/http"
	"os"
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

	file, err := os.OpenFile("resultados.txt", os.O_APPEND|os.O_CREATE|os.O_WRONLY, 0644)

	if err != nil {
		log.Fatalf("Erro ao abrir arquivo: %v", err)
	}

	defer file.Close()

	if *mode == "rest" {
		runRest(file, token)
	} else if *mode == "graphql" {
		runGraphQL(file, token)
	} else {
		log.Fatalf("Modo inválido. Use 'rest' ou 'graphql'.")
	}

}

func runRest(file *os.File, token string) {
	var GITHUB_REST_URL string = "https://api.github.com/users/nicolasdaldegan/repos?per_page=5"

	for i := 0; i < 100; i++ {
		size, time := doRest(GITHUB_REST_URL, token)
		result := fmt.Sprintf("REST -> Size: %d bytes, Time: %v\n", size, time)
		fmt.Print(result)
		writeToFile(file, result)
	}
}

func runGraphQL(file *os.File, token string) {
	var GITHUB_GRAPHQL_URL string = "https://api.github.com/graphql"

	var body string = "{\"query\": \"query { user(login: \\\"nicolasdaldegan\\\") { repositories(first: 5) { nodes { name url stargazerCount forkCount } } } }\"}"

	for i := 0; i < 100; i++ {
		size, time := doGraphQL(GITHUB_GRAPHQL_URL, body, token)
		result := fmt.Sprintf("GraphQL -> Size: %d bytes, Time: %v\n", size, time)
		fmt.Print(result)
		writeToFile(file, result)
	}
}

func writeToFile(file *os.File, data string) {
	if _, err := file.WriteString(data); err != nil {
		log.Printf("Erro ao escrever no arquivo: %v", err)
	}
}
