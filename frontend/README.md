# Controle de Estoque

API REST e interface web para controle de estoque: cadastro de produtos,
entradas e saídas com validação de saldo, e relatórios de movimentação,
produtos por tipo e lucro.

## Tecnologias

- Backend: Java 21, Spring Boot 4, Spring Data JPA, Bean Validation, H2, Lombok, Maven
- Frontend: Vue 3, TypeScript, Vue Router, Vite, axios
- Infra: Docker, Docker Compose, Nginx
- Testes: JUnit, Mockito e AssertJ (backend);

## Como executar

Pré-requisito: Docker.

```
docker compose up --build
```

| Serviço | URL |
| --- | --- |
| Aplicação web | http://localhost:3000 |
| API | http://localhost:8080 |
| Swagger | http://localhost:8080/swagger-ui/index.html |

O banco H2 é em memória: os dados são perdidos ao reiniciar.

## Como rodar os testes

```
docker compose run --rm tests            # backend

```

## Endpoints

| Método | Rota | Descrição |
| --- | --- | --- |
| POST | /api/produtos | Cria produto (com estoque inicial opcional) |
| GET | /api/produtos | Lista produtos (paginado) |
| GET | /api/produtos/{id} | Busca produto |
| PUT | /api/produtos/{id} | Atualiza produto |
| DELETE | /api/produtos/{id} | Exclui produto sem movimentações |
| GET | /api/produtos/por-tipo?tipo= | Produtos de um tipo, com saída total e quantidade disponível |
| GET | /api/produtos/lucro | Quantidade total de saída e lucro por produto |
| GET | /api/produtos/movimentacao?inicio=&fim= | Entradas, saídas e saldo por produto, com período opcional |
| POST | /api/movimentos | Registra entrada ou saída |
| GET | /api/movimentos | Lista movimentos (paginado) |

## Regras de negócio

- O código do produto é único.
- O estoque inicial é opcional e vira uma ENTRADA; depois disso o saldo só muda por movimentação.
- Entrada soma ao saldo. Saída exige valor de venda e subtrai do saldo.
- Saída acima do saldo retorna 422 com a mensagem
  `Estoque insuficiente para o produto P001: disponível 5, solicitado 10`.
- Produto com movimentações não pode ser excluído (409).
- Lucro = soma de (valor de venda − custo unitário) × quantidade, nas saídas.

## Exemplos

Cadastro:
```json
{
  "codigo": "P001",
  "descricao": "Notebook",
  "tipo": "ELETRONICO",
  "valorFornecedor": 3500.00,
  "quantidadeInicial": 10
}
```

Saída:
```json
{ "produtoId": 1, "tipo": "SAIDA", "quantidade": 3, "valorVenda": 4200.00 }
```

## Mapeamento do enunciado

- "Data de venda" corresponde ao campo `dataMovimentacao`, preenchido pelo servidor e usado em entradas e saídas.
- O lucro usa o custo gravado no momento da saída, que é igual ao valor do fornecedor enquanto ele não muda.

