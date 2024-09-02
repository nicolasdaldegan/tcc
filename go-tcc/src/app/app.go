package main

import (
	"bytes"
	"fmt"
	"net/http"
	"time"
	"unsafe"
)

var client = &http.Client{}

func main() {
	var GITHUB_REST_URL string = "https://api.github.com/users/Netflix/repos?per_page=5"

	for i := 0; i < 0; i++ {
		size, time := doRest(GITHUB_REST_URL)
		fmt.Println("Size: ", size)
		fmt.Println("Time: ", time/100000)
	}

	var GITHUB_GRAPHQL_URL string = "https://api.github.com/graphql"

	var body string = "{\"query\": \"query { user(login: \\\"nicolasdaldegan\\\") { repositories(first: 5) { nodes { name url stargazerCount forkCount languages(first: 5) { nodes { name } } } } } }\"}"

	for i := 0; i < 10; i++ {
		size, time := doGraphQL(GITHUB_GRAPHQL_URL, body)
		fmt.Println("Size: ", size)
		fmt.Println("Time: ", time/100000)
	}

}

func doGraphQL(url_request string, body string) (uintptr, int) {

	payload := bytes.NewBufferString(body)

	req, _ := http.NewRequest(http.MethodPost, url_request, payload)

	req.Header.Add("Content-Type", "application/json")
	req.Header.Add("Authorization", "Bearer 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")

	time_init := time.Now()
	resp, err := client.Do(req)
	time_end := time.Now()

	if err != nil {
		panic(err)
	}

	fmt.Println("Response: ", resp.StatusCode)

	defer resp.Body.Close()

	return unsafe.Sizeof(resp.Request.RequestURI), int(time_end.Nanosecond() - time_init.Nanosecond())
}

func doRest(url_request string) (uintptr, int) {

	time_init := time.Now()
	resp, err := http.Get(url_request)
	time_end := time.Now()

	if err != nil {
		panic(err)
	}

	defer resp.Body.Close()

	return unsafe.Sizeof(resp.Request.RequestURI), int(time_end.Nanosecond() - time_init.Nanosecond())

}
