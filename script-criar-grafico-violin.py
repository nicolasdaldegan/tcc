import pandas as pd
import seaborn as sns
import matplotlib.pyplot as plt
import re

def extrair_tempos(caminho_arquivo):
    tempos = []
    with open(caminho_arquivo, "r") as file:
        for linha in file:
            match = re.search(r"Tempo: ([\d.]+)ms;", linha)
            if match:
                tempos.append(float(match.group(1)))
    return tempos

arquivos = {
    "Query 1": {
        "Go": ["QUERY1-REST-GO.txt", "QUERY1-GRAPHQL-GO.txt"],
        "Java": ["QUERY1-REST-JAVA.txt", "QUERY1-GRAPHQL-JAVA.txt"],
        "Python": ["QUERY1-REST-PYTHON.txt", "QUERY1-GRAPHQL-PYTHON.txt"]
    },
    "Query 2": {
        "Go": ["QUERY2-REST-GO.txt", "QUERY2-GRAPHQL-GO.txt"],
        "Java": ["QUERY2-REST-JAVA.txt", "QUERY2-GRAPHQL-JAVA.txt"],
        "Python": ["QUERY2-REST-PYTHON.txt", "QUERY2-GRAPHQL-PYTHON.txt"]
    },
    "Query 3": {
        "Go": ["QUERY3-REST-GO.txt", "QUERY3-GRAPHQL-GO.txt"],
        "Java": ["QUERY3-REST-JAVA.txt", "QUERY3-GRAPHQL-JAVA.txt"],
        "Python": ["QUERY3-REST-PYTHON.txt", "QUERY3-GRAPHQL-PYTHON.txt"]
    }
}

data = []
for query, linguagens in arquivos.items():
    for linguagem, (rest_file, graphql_file) in linguagens.items():
        tempos_rest = extrair_tempos(f"C:/Users/nicolas.vespucio_evo/Desktop/queries-filtradas/{rest_file}")
        tempos_graphql = extrair_tempos(f"C:/Users/nicolas.vespucio_evo/Desktop/queries-filtradas/{graphql_file}")
        for tempo in tempos_rest:
            data.append({"Tempo": tempo, "Tipo": "REST", "Query": query, "Linguagem": linguagem})
        for tempo in tempos_graphql:
            data.append({"Tempo": tempo, "Tipo": "GraphQL", "Query": query, "Linguagem": linguagem})

df = pd.DataFrame(data)

def violin_box_swarm(data, x, y, ax):
    sns.violinplot(data=data, x=x, y=y, inner=None, linewidth=1.5, width=0.7, color="white", ax=ax)

    sns.boxplot(
        data=data, x=x, y=y, width=0.2, whis=[5, 95], showcaps=False,
        boxprops={'facecolor': 'lightgrey', 'edgecolor': 'black', 'linewidth': 2, 'alpha': 0.7, 'zorder': 3},
        whiskerprops={'linewidth': 2, 'color': 'black', 'zorder': 3},  # Linhas dos bigodes
        flierprops={'marker': 'o', 'markersize': 5, 'markerfacecolor': 'black', 'zorder': 4},  # Outliers
        medianprops={'color': 'red', 'linewidth': 2, 'zorder': 4},  # Linha da mediana destacada
        ax=ax
    )

    sns.swarmplot(data=data, x=x, y=y, color="black", size=4, zorder=5, ax=ax)


sns.set(style="whitegrid", rc={"axes.facecolor": "0.9"})

for query in arquivos.keys():
    df_query = df[df["Query"] == query]

    fig, axes = plt.subplots(nrows=3, figsize=(8, 14), sharex=True, gridspec_kw={'height_ratios': [1, 1, 1.5]})
    fig.suptitle(f"Desempenho REST vs GraphQL - {query}", fontsize=14)

    for (linguagem, ax) in zip(["Go", "Java", "Python"], axes):
        data_linguagem = df_query[df_query["Linguagem"] == linguagem]
        violin_box_swarm(data=data_linguagem, x="Tempo", y="Tipo", ax=ax)
        ax.set_title(linguagem, fontsize=12)
        ax.set_xscale("log")

        min_tempo = data_linguagem["Tempo"].min() * 0.8
        max_tempo = data_linguagem["Tempo"].max() * 1.2
        ax.set_xlim(min_tempo, max_tempo)

        custom_ticks = [100, 200, 300, 400, 500, 600, 700, 800, 1000, 1200, 1400, 1600, 1800, 2000]
        custom_ticks = [tick for tick in custom_ticks if
                        min_tempo <= tick <= max_tempo]

        ax.set_xticks(custom_ticks)
        ax.set_xticklabels(
            [f"$10^{{{int(i / 1000)}}}$" if i % 1000 == 0 else f"${int(i / 100)} \\times 10^2$" for i in custom_ticks],
            fontsize=10,
            rotation=45,
            ha='right'
        )

        for tick in custom_ticks:
            ax.axvline(x=tick, color="gray", linestyle="--", linewidth=1.2)

        ax.set_xlabel("Tempo (ms)")
        ax.set_ylabel("")

    plt.tight_layout(rect=[0, 0, 1, 0.95])
    plt.subplots_adjust(bottom=0.15)
    plt.savefig(f"Desempenho_{query}.png", dpi=300, bbox_inches='tight')
    plt.show()