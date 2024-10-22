# Projeto Java TCC

Este projeto é uma aplicação Java que utiliza a API do GitHub para realizar análises de desempenho e payloads, usando as abordagens REST e GraphQL.

## Pré-requisitos

Antes de começar, verifique se você tem as seguintes ferramentas instaladas:

- [Java JDK 17](https://www.oracle.com/java/technologies/javase-jdk17-downloads.html) ou uma versão superior
- [Maven](https://maven.apache.org/download.cgi)

### Instalando o Maven

1. **Baixe o Maven**: Acesse a [página de download do Maven](https://maven.apache.org/download.cgi) e siga as instruções para o seu sistema operacional.
   
2. **Configuração do Maven**:
   - Extraia o arquivo baixado para um diretório de sua escolha.
   - Adicione o diretório `bin` do Maven à variável de ambiente `PATH` do seu sistema.

3. **Verifique a instalação**:
    ```bash
   mvn -v

### Instalando o Java JDK

1. **Baixe o JDK**: Acesse a [página de download do JDK](https://www.oracle.com/java/technologies/javase-jdk17-downloads.html) e siga as instruções para o seu sistema operacional.
   
2. **Configuração do JDK**:
   - Após a instalação, adicione o diretório `bin` do JDK à variável de ambiente `PATH` do seu sistema.

3. **Verifique a instalação**:
    ```bash
   java -version

### Configurando o Token do GitHub

Crie um arquivo .env na raiz do projeto

Adicione a seguinte linha ao arquivo, substituindo SEU_TOKEN pelo seu token de acesso do GitHub

    TOKEN=SEU_TOKEN

O token pode ser gerado acessando as configurações da sua conta do GitHub, na seção Developer settings > Personal access tokens.

### Instalando as dependências do projeto e compilando

Navegue até o diretório raiz da aplicação (java-tcc) e execute os seguintes comandos:

    mvn clean install
    mvn clean compile


### Executando a Aplicação

Neste mesmo diretório, substitua 'modo' por rest ou graphql e execute o seguinte comando:

    mvn exec:java -Dexec.mainClass="br.tcc.Main" -Dexec.args="--mode=(modo)"
