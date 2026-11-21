# Controle de Estoque

API REST para controle de estoque (produtos e movimentações), com front em Vue.js.

## Tecnologias
- Java 21, Spring Boot 4, Spring Data JPA, Bean Validation
- H2 (banco em memória)
- Maven, Docker e Docker Compose
- JUnit 6, Mockito e AssertJ
- Swagger (springdoc-openapi)
- Vue.js (frontend)

## Como executar
```
docker compose up --build
```
- API: http://localhost:8080
- Swagger: http://localhost:8080/swagger-ui/index.html

## Como rodar os testes
```
docker compose run --rm tests
```

## Endpoints
| Método | Rota | Descrição |
|---|---|---|
| POST | /api/produtos | Cria produto |
| GET | /api/produtos | Lista produtos (paginado) |
| GET | /api/produtos/{id} | Busca produto |
| PUT | /api/produtos/{id} | Atualiza produto |
| DELETE | /api/produtos/{id} | Exclui produto (sem movimentações) |
| GET | /api/produtos/por-tipo?tipo= | Produtos por tipo, com saídas e saldo |
| GET | /api/produtos/lucro | Lucro e saídas por produto |
| POST | /api/movimentos | Registra entrada ou saída |
| GET | /api/movimentos | Lista movimentos (paginado) |

## Decisões de projeto
- Arquitetura em camadas (controller, service, repository), simples e proporcional ao tamanho do domínio.
- DTOs separados das entidades, com mapper dedicado.