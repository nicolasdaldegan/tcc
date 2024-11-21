import os
import numpy as np

def process_file(input_file, output_dir):
    query_entries = {}
    tempos = {}

    base_name = os.path.basename(input_file).replace(".txt", "")
    architecture = "GRAPHQL" if "graphql" in base_name.lower() else "REST"
    if "python" in base_name.lower():
        language = "PYTHON"
    elif "analytics" in base_name.lower():
        language = "JAVA"
    elif "go" in base_name.lower():
        language = "GO"
    else:
        language = "UNKNOWN"

    with open(input_file, 'r') as file:
        for line in file:
            parts = line.strip().split("; ")
            query_id = parts[0].split()[1]  
            tempo_str = parts[1].split(":")[1].strip()
            if "ms" in tempo_str:
                tempo = float(tempo_str.replace("ms", ""))
            elif "s" in tempo_str:
                tempo = float(tempo_str.replace("s", "")) * 1000
            else:
                tempo = float(tempo_str)

            payload_request = parts[2].split(":")[1].strip()
            payload_response = parts[3].split(":")[1].strip()

            if query_id not in query_entries:
                query_entries[query_id] = []
                tempos[query_id] = []

            query_entries[query_id].append((tempo, payload_request, payload_response))
            tempos[query_id].append(tempo)

    for query_id, entries in query_entries.items():
        mean = np.mean(tempos[query_id])
        std_dev = np.std(tempos[query_id])

        filtered_entries = [
            entry for entry in entries
            if mean - 2 * std_dev <= entry[0] <= mean + 2 * std_dev
        ]

        output_file = os.path.join(output_dir, f"QUERY{query_id}-{architecture}-{language}.txt")

        with open(output_file, 'a') as out_file:
            for tempo, payload_request, payload_response in filtered_entries:
                out_file.write(f"Query {query_id}; Tempo: {tempo}ms; Payload Request: {payload_request}; Payload Response: {payload_response}\n")

def process_all_files(files, output_dir):
    if not os.path.exists(output_dir):
        os.makedirs(output_dir)

    for file in files:
        process_file(file, output_dir)

input_files = [
    "analytics_graphql.txt",
    "analytics_rest.txt",
    "resultados-graphql-go.txt",
    "resultados-graphql-python.txt",
    "resultados-rest-go.txt",
    "resultados-rest-python.txt"
]

output_dir = "queries-filtradas"

process_all_files(input_files, output_dir)

print(f"Arquivos filtrados gerados em: {output_dir}")
