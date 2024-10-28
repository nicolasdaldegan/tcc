# Projeto Go TCC  

Este projeto é uma aplicação Go que utiliza a API do GitHub para realizar análises de desempenho e payloads, usando as abordagens REST e GraphQL.  

## Pré-requisitos  

Antes de começar, verifique se você tem as seguintes ferramentas instaladas:  

- [Go 1.21+](https://go.dev/dl/)  
- Git  

### Instalando o Go  

1. **Baixe o Go**: Acesse a [página de download do Go](https://go.dev/dl/) e siga as instruções para o seu sistema operacional.  

2. **Configuração do Go**:  
   - Após a instalação, adicione o diretório `bin` do Go à variável de ambiente `PATH` do seu sistema.  

3. **Verifique a instalação**:  
    ```bash  
    go version  
    ```  

### Configurando o Token do GitHub  

1. No diretório `src/app`, crie um arquivo `.env`.  
2. Adicione a seguinte linha ao arquivo, substituindo **SEU_TOKEN** pelo seu token de acesso do GitHub:  

    ```  
    TOKEN=SEU_TOKEN  
    ```  

   O token pode ser gerado acessando as configurações da sua conta no GitHub, na seção **Developer settings > Personal access tokens**.  

### Instalando Dependências  

No diretório `src/app`, execute:  

```bash  
go mod tidy  
```  

### Executando a Aplicação

Navegue até o diretório src/app e substitua (modo) por rest ou graphql no seguinte comando:

    go run . --mode=(modo)  

