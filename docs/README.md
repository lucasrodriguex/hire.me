A aplicação foi desenvolvida utilizando Java com o framework Spring Boot. A escolha foi pelo conhecimento prévio do framework e por ter maior facilidade, o que iria agilizar o desenvolvimento do desafio (poderia fazer utilizando outra tecnologia sem problemas).

Para gerar o hash das urls encurtadas foi utilizado um encode em Base 62. A ideia é ter um hash significativamente menor que a url original.
Na base 62 temos 26 letras minusculas (a-z), 26 maiusculas (A-Z) e 10 numeros (0-9), 26 + 26 + 10 = 62
A ideia foi utilizar o ID gerado automaticamente pelo banco de dados (auto increment id) e a partir dele calcular o hash em base 62.
Foi necessário criar um tratamento para que caso exista um hash igual a uma custom label já criada no banco seja gerado um novo hash. Este loop acontece até que seja possível salvar o novo hash gerado no banco de dados

Os testes unitários foram feitos utilzando o MockMvc, que facilita bastante a implementação de testes unitários.

Foi criado um controller para tratar as exceptions automaticamente utilizando a anotação @ExceptionHandler, gosto de implementar dessa maneira pois melhora muito a organização e a legibilidade do código, além de eliminar a necessidade de colocar vários blocos de try/catch no código

A consulta para o endpoint das 10 urls mais vistas foi feita utilizando a notação do Spring Data (findTop10ByOrderByViewsDesc).

Foi criado um controller ClientController apenas para redirecionar para o index.html que se encontra na pasta resources. O ideal seria separar e criar um projeto apenas para consultar a API, mas acho que para esta prova não havia necessidade.
Foi criada uma interface front-end bem simples utilizando bootstrap e Jquery para chamar a API e criar as url encurtadas.

Na raiz do projeto tem um arquivo deploy.sh, ele é responsável por fazer o deploy do projeto utilizando o Docker com os containers necessários.

Para rodar a aplicação a partir de uma IDE basta ter um mysql instalado e rodando na porta 3306. No application.yml é possível encontrar as informações para conexão (usuários, nome da base, senha).
Caso tenha o docker instalado basta apenas rodar o comando abaixo para facilmente criar um container com um banco de dados mysql pronto para a aplicação

```
docker run -p 3306:3306 -d \
    --name mysql-dev \
    -e MYSQL_ROOT_PASSWORD=root \
    -e MYSQL_DATABASE=shorturl \
    -e MYSQL_USER=shorturl \
    -e MYSQL_PASSWORD=shorturl \
    mysql:5.6
    
```

Também é possível rodar toda a aplicação utilizando o docker, basta rodar o arquivo deploy.sh que se encontra na raiz do projeto.

Exemplos de endpoints para teste:

Criar url encurtada com uma custom label

```
curl -H "Content-Type: application/json" -X POST "http://localhost:8080/shorturl?url=http://bemobi.com.br&custom_label=bemobi"
```

Criar uma url encurtada sem custom label:

```
curl -H "Content-Type: application/json" -X POST "http://localhost:8080/shorturl?url=http://bemobi.com.br"
```

Consultar uma label:

```
curl "http://localhost:8080/shorturl?label=bemobi"
```

Ver os top 10 mais visitados:

```
curl "http://localhost:8080/shorturl/topTenViews"
```


