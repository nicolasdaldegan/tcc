import numpy as np

def process_times(input_file, output_file):
    query_entries = []
    tempos = []

    with open(input_file, 'r') as file:
        for line in file:
            parts = line.strip().split("; ")
            query_id = parts[0].split()[1]

            tempo_str = parts[1].split(":")[1].strip()
            if "ms" in tempo_str:
                tempo_str = tempo_str.replace("ms", "")
                tempo = float(tempo_str)
            elif "s" in tempo_str:
                tempo_str = tempo_str.replace("s", "")
                tempo = float(tempo_str) * 1000
            elif tempo_str:
                tempo = float(tempo_str)

            payload_request = parts[2].split(":")[1].strip()
            payload_response = parts[3].split(":")[1].strip()

            tempos.append(tempo)
            query_entries.append((query_id, tempo, payload_request, payload_response))

    mean = np.mean(tempos)
    std_dev = np.std(tempos)

    filtered_entries = [
        entry for entry in query_entries
        if mean - 2 * std_dev <= entry[1] <= mean + 2 * std_dev
    ]

    with open(output_file, 'w') as file:
        for entry in filtered_entries:
            query_id, tempo, payload_request, payload_response = entry
            file.write(f"Query {query_id}; Tempo: {tempo}ms; Payload Request: {payload_request}; Payload Response: {payload_response}\n")

process_times("C:/Users/nicolas.vespucio_evo/Desktop/txts-tcc-2/QUERY3-GRAPHQL-JAVA.txt", "C:/Users/nicolas.vespucio_evo/Desktop/txts-filtrados/QUERY3-GRAPHQL-JAVA2.txt")
