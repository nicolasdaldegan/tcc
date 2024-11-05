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

plt.xlim(100, 1000)

xticks = [100, 200, 300, 400, 500, 700, 1000]
plt.xticks(xticks, [r"$10^2$" if x == 100 else r"$10^3$" if x == 1000 else f"${int(x/100)} \\times 10^2$" for x in xticks])

plt.minorticks_off()

line_color = 'gray'
line_style = '--'
line_width = 1.2

for x in xticks + [150, 250, 350, 450, 600, 800]:
    plt.axvline(x=x, color=line_color, linestyle=line_style, linewidth=line_width)

plt.title("Name")
plt.xlabel("Duration (ms) (log10)")
plt.ylabel("")

plt.show()
