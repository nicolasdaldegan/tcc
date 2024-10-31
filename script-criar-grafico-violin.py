import pandas as pd
import seaborn as sns
import matplotlib.pyplot as plt
import re
import numpy as np

def extrair_tempos(caminho_arquivo):
    tempos = []
    with open(caminho_arquivo, "r") as file:
        for linha in file:
            match = re.search(r"Tempo: ([\d.]+);", linha)
            if match:
                tempos.append(float(match.group(1)))
    return tempos

#rodar para cada query de cada linguagem
#ter um txt com as linhas neste formato:
#Tempo: 137.95733451843262;
tempos_rest = extrair_tempos(".txt")
tempos_graphql = extrair_tempos(".txt")

df = pd.DataFrame({
    "Tempo": tempos_rest + tempos_graphql,
    "Tipo": ["REST"] * len(tempos_rest) + ["GraphQL"] * len(tempos_graphql)
})

sns.set(style="whitegrid", rc={"axes.facecolor": "0.9"})
plt.figure(figsize=(10, 6))

sns.violinplot(y="Tipo", x="Tempo", data=df, inner=None, color="white", linewidth=1.5)

sns.boxplot(y="Tipo", x="Tempo", data=df, whis=[5, 95], width=0.2,
            boxprops={'facecolor':'None'}, showcaps=False, whiskerprops={'linewidth':1.5}, zorder=2)

sns.swarmplot(y="Tipo", x="Tempo", data=df, color="black", size=4, zorder=3)

plt.xscale("log")
plt.autoscale(enable=True, axis='x', tight=True)

plt.axvline(x=100, color='gray', linestyle='--', linewidth=0.8)
plt.axvline(x=200, color='gray', linestyle='--', linewidth=0.8)
plt.axvline(x=300, color='gray', linestyle='--', linewidth=0.8)

midpoint1 = np.sqrt(100 * 200)
midpoint2 = np.sqrt(200 * 300)
plt.axvline(x=midpoint1, color='gray', linestyle='--', linewidth=0.8)
plt.axvline(x=midpoint2, color='gray', linestyle='--', linewidth=0.8)

plt.title("Query 1 - Rest - Python")
plt.xlabel("Duration (ms) (log10)")
plt.ylabel("")

plt.show()
