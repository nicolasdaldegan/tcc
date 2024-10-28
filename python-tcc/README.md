# Projeto Python TCC  

Este projeto é uma aplicação Python que utiliza a API do GitHub para realizar análises de desempenho e payloads, usando as abordagens REST e GraphQL.  

## Pré-requisitos  

Antes de começar, verifique se você tem as seguintes ferramentas instaladas:  

- [Python 3.10+](https://www.python.org/downloads/)  
- Git  

### Instalando o Python  

1. **Baixe o Python**: Acesse a [página de download do Python](https://www.python.org/downloads/) e siga as instruções para o seu sistema operacional.  

2. **Configuração do Python**:  
   - Durante a instalação, selecione a opção **Add Python to PATH**.  

3. **Verifique a instalação**:  
    ```bash  
    python --version  
    ```  

### Configurando o Token do GitHub  

1. No diretório do projeto `python-tcc`, crie um arquivo `.env`.  
2. Adicione a seguinte linha ao arquivo, substituindo **SEU_TOKEN** pelo seu token de acesso do GitHub:  

    ```  
    TOKEN=SEU_TOKEN  
    ```  

   O token pode ser gerado acessando as configurações da sua conta no GitHub, na seção **Developer settings > Personal access tokens**.  

### Instalando Dependências  

1. Navegue até o diretório do projeto:  
    ```bash  
    cd python-tcc  
    ```  

2. Crie um ambiente virtual (opcional, mas recomendado):  
    ```bash  
    python -m venv venv  
    source venv/bin/activate   # Linux/Mac  
    venv\Scripts\activate      # Windows  
    ```  

3. Instale as dependências:  
    ```bash  
    pip install -r requirements.txt  
    ```  

### Executando a Aplicação  

No diretório `python-tcc`, substitua `(modo)` por `rest` ou `graphql` e execute:  

```bash  
python main.py --mode=(modo)  
```  
