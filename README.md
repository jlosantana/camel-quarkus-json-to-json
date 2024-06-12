## Projeto de Integração JSON-to-JSON

### Descrição

Este repositório contém um projeto de integração JSON-to-JSON utilizando tecnologias como Apache Camel, Quarkus e JSONata para transformar e manipular dados JSON. O projeto é configurado utilizando Maven para gerenciamento de dependências e construção do projeto. Uma solução semelhante pode ser usada como proxy para traduzir o JSON source para um JSON target a partir de especificação Jsonata.

### Tecnologias Utilizadas

- **Quarkus**: Framework Java nativo na nuvem que proporciona arranques rápidos e memória reduzida.
- **Apache Camel**: Framework de integração que fornece uma série de componentes para conectar diferentes sistemas.
- **JSONata**: Linguagem de consulta e transformação para JSON.
- **Jakarta EE**: Conjunto de especificações para construir aplicações empresariais em Java.

### Estrutura do Projeto

#### `pom.xml`

O arquivo `pom.xml` é usado para gerenciar as dependências e plugins do Maven necessários para o projeto. Aqui estão alguns dos principais pontos:

- Gerenciamento de dependências:
  - `quarkus-bom`: Importa o Bill of Materials (BOM) do Quarkus para garantir a compatibilidade das versões.
  - `quarkus-camel-bom`: Importa o BOM do Camel Quarkus.
- Dependências:
  - `camel-quarkus-http`: Suporte para componentes HTTP no Camel Quarkus.
  - `camel-quarkus-direct`: Suporte para componentes direct no Camel Quarkus.
  - `camel-quarkus-rest`: Suporte para componentes REST no Camel Quarkus.
  - `camel-quarkus-jsonata`: Suporte para JSONata no Camel Quarkus.
  - `camel-quarkus-jackson`: Suporte para processamento de JSON com Jackson no Camel Quarkus.
- Plugins:
  - `quarkus-maven-plugin`: Plugin para construir e empacotar aplicações Quarkus.
  - `maven-compiler-plugin`: Plugin para compilar o código Java.

#### `HomeRoute.java`

```
import jakarta.enterprise.context.ApplicationScoped;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class HomeRoute extends RouteBuilder {

    @ConfigProperty(name = "routes.rest-path")
    String restPath;

    @ConfigProperty(name = "routes.source-url")
    String sourceUrl;

    @ConfigProperty(name = "routes.transform-spec-url")
    String transformSpecUrl;

    @ConfigProperty(name = "routes.transformer")
    String transformer;

    @Override
    public void configure() {
        restConfiguration()
                .component("platform-http")
                .bindingMode(RestBindingMode.json);

        rest(restPath)
                .get()
                .to("direct:external");

        from("direct:external")
            .to(sourceUrl + "?bridgeEndpoint=true&throwExceptionOnFailure=false")
                .unmarshal().json()
                .toD(transformer + ":" + transformSpecUrl);
    }
}

```

O arquivo `HomeRoute.java` define a rota Camel utilizada no projeto. Aqui está uma visão geral do que ele faz:

- Configura o componente REST para usar o componente `platform-http` e o modo de ligação JSON.
- Define um endpoint REST `/accounts` que responde a requisições GET e redireciona para uma rota interna `direct:external`.
- A rota `direct:external` faz uma chamada HTTP para `http://localhost:9000/source` para obter os dados JSON de origem.
- Depois de deserializar os dados JSON, aplica uma transformação JSONata utilizando a especificação de transformação disponível em `http://localhost:9000/transform-spec`.

Para os serviços `http://localhost:9000/source` e o `http://localhost:9000/transform-spec` foi utilizado o [Mockon](https://mockoon.com/).

`http://localhost:9000/source`

```
{
    "companyNIT": "909724053112505453544997058372",
    "checkDigit": "291004157"
}
```

`http://localhost:9000/transform-spec

```
{
  "data": [
    {
      "companyNIT": companyNIT,
      "checkDigit": [checkDigit]
    }
  ]
}
```

### Como Executar

Para executar este projeto localmente, siga os passos abaixo:

1. Certifique-se de ter o Maven e o JDK 17 instalados em seu ambiente.
2. Clone o repositório para sua máquina local.
3. Disponibilize os mocks `http://localhost:9000/source` e `http://localhost:9000/transform-spec` ou altere para as urls de sua preferência;
4. Navegue até o diretório do projeto e execute o comando: `./mvnw compile quarkus:dev`
5. A aplicação estará disponível em `http://localhost:8080`.

### Testando a Rota

Para testar a rota definida, você pode fazer uma requisição GET para `http://localhost:8080/accounts` e verificar a transformação aplicada aos dados recebidos.

### Contribuindo

Contribuições são bem-vindas! Sinta-se à vontade para abrir issues e pull requests para discutir mudanças e melhorias.

### Licença

Este projeto é licenciado sob a Licença Apache 2.0. Veja o arquivo `LICENSE` para mais detalhes.
