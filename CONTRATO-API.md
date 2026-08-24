# Contrato da API — PS Lite

## Processo Seletivo

### Base URL

http://localhost:8080

### Swagger UI

http://localhost:8080/swagger-ui/index.html

---

## GET /processo-seletivo

Lista todos os processos seletivos.

### Resposta — 200 OK

```json
[
  {
    "id": 1,
    "nome": "Processo Seletivo 2026",
    "descricao": "Processo para seleção de alunos",
    "qtdVagas": 20,
    "status": "CADASTRADO"
  }
]
```

---

## GET /processo-seletivo/{id}

Busca um processo seletivo pelo ID.

### Parâmetro

| Nome | Tipo | Obrigatório |
|---|---|---|
| id | Long | Sim |

### Resposta — 200 OK

```json
{
  "id": 1,
  "nome": "Processo Seletivo 2026",
  "descricao": "Processo para seleção de alunos",
  "qtdVagas": 20,
  "status": "CADASTRADO"
}
```

### Processo não encontrado — 404 Not Found

```json
{
  "error": "Not Found",
  "detail": "Processo seletivo não encontrado"
}
```

---

## POST /processo-seletivo

Cria um novo processo seletivo.

### Request Body

```json
{
  "nome": "Processo Seletivo 2026",
  "descricao": "Processo para seleção de alunos",
  "qtdVagas": 20,
  "status": "CADASTRADO"
}
```

### Resposta — 201 Created

```json
{
  "id": 1,
  "nome": "Processo Seletivo 2026",
  "descricao": "Processo para seleção de alunos",
  "qtdVagas": 20,
  "status": "CADASTRADO"
}
```

### Erro de validação — 400 Bad Request

Exemplo:

```json
{
  "nome": "",
  "descricao": "Teste",
  "qtdVagas": -1,
  "status": "CADASTRADO"
}
```

Resposta:

```json
{
  "error": "Bad Request",
  "detail": "Erro de validação",
  "messages": [
    "nome: não deve estar em branco",
    "qtdVagas: deve ser maior ou igual a 0"
  ]
}
```

---

## PUT /processo-seletivo/{id}

Atualiza um processo seletivo existente.

### Parâmetro

| Nome | Tipo | Obrigatório |
|---|---|---|
| id | Long | Sim |

### Request Body

```json
{
  "nome": "Processo Seletivo 2026 - Atualizado",
  "descricao": "Descrição atualizada",
  "qtdVagas": 25,
  "status": "CADASTRADO"
}
```

### Resposta — 200 OK

```json
{
  "id": 1,
  "nome": "Processo Seletivo 2026 - Atualizado",
  "descricao": "Descrição atualizada",
  "qtdVagas": 25,
  "status": "CADASTRADO"
}
```

O identificador utilizado na atualização é exclusivamente o valor de `{id}` informado na URL. O campo `id` **não deve ser enviado no corpo** da requisição.

### Processo não encontrado — 404 Not Found

```json
{
  "error": "Not Found",
  "detail": "Processo seletivo não encontrado"
}
```

---

## DELETE /processo-seletivo/{id}

Remove um processo seletivo.

### Parâmetro

| Nome | Tipo | Obrigatório |
|---|---|---|
| id | Long | Sim |

### Resposta — 204 No Content

Sem corpo de resposta.

### Processo não encontrado — 404 Not Found

```json
{
  "error": "Not Found",
  "detail": "Processo seletivo não encontrado"
}
```

---

## Modelo ProcessoSeletivo

| Campo | Tipo | Request | Regra |
|---|---|---|---|
| id | Long | Não | Gerado pelo sistema e retornado pela API |
| nome | String | Sim | Obrigatório; não pode estar em branco |
| descricao | String | Não | Opcional |
| qtdVagas | Integer | Não | Se omitido, assume `0`; se informado, deve ser ≥ 0 e não pode ser `null` |
| status | String/Enum | Não | Se omitido, assume `CADASTRADO`; se informado, deve ser `CADASTRADO`, `FINALIZADO` ou `CANCELADO` e não pode ser `null` |

### Status disponíveis

```text
CADASTRADO
FINALIZADO
CANCELADO
```

---

## Regras de validação

- `nome`: obrigatório e não pode estar em branco.
- `qtdVagas`: opcional no request; quando omitido, assume `0`. Quando informado, não pode ser `null` e deve ser maior ou igual a `0`.
- `status`: opcional no request; quando omitido, assume `CADASTRADO`. Quando informado, não pode ser `null` e deve ser um dos valores `CADASTRADO`, `FINALIZADO` ou `CANCELADO`.

# Formato de erros

## 400 Bad Request

Utilizado para erros de validação.

```json
{
  "error": "Bad Request",
  "detail": "Erro de validação",
  "messages": [
    "campo: mensagem"
  ]
}
```

## 404 Not Found

Utilizado quando o recurso solicitado não existe.

### Processo seletivo

```json
{
  "error": "Not Found",
  "detail": "Processo seletivo não encontrado"
}
```

### Balão informativo

```json
{
  "error": "Not Found",
  "detail": "Balão informativo não encontrado"
}
```

---

# Balão Informativo

### Base URL

http://localhost:8080

### Swagger UI

http://localhost:8080/swagger-ui/index.html

---

## GET /balao-informativo/por-processo/{idProcesso}

Lista os balões informativos vinculados a um processo seletivo.

### Parâmetro

| Nome | Tipo | Obrigatório |
|---|---|---|
| idProcesso | Long | Sim |

### Resposta — 200 OK

```json
[
  {
    "id": 1,
    "titulo": "Prazo de Inscrição",
    "mensagem": "As inscrições vão até sexta-feira às 23:59.",
    "processoSeletivo": {
      "id": 1,
      "nome": "Processo Seletivo 2026",
      "descricao": "Processo para seleção de alunos",
      "qtdVagas": 20,
      "status": "CADASTRADO"
    }
  }
]
```

### Processo não encontrado — 404 Not Found

```json
{
  "error": "Not Found",
  "detail": "Processo seletivo não encontrado com o ID: 99"
}
```

---

## GET /balao-informativo/{id}

Busca um balão informativo pelo seu ID.

### Parâmetro

| Nome | Tipo | Obrigatório |
|---|---|---|
| id | Long | Sim |

### Resposta — 200 OK

```json
{
  "id": 1,
  "titulo": "Prazo de Inscrição",
  "mensagem": "As inscrições vão até sexta-feira às 23:59.",
  "processoSeletivo": {
    "id": 1,
    "nome": "Processo Seletivo 2026",
    "descricao": "Processo para seleção de alunos",
    "qtdVagas": 20,
    "status": "CADASTRADO"
  }
}
```

### Balão não encontrado — 404 Not Found

```json
{
  "error": "Not Found",
  "detail": "Balão informativo não encontrado"
}
```

---

## POST /balao-informativo

Cria um novo balão informativo vinculado a um processo seletivo existente.

### Request Body

```json
{
  "titulo": "Prazo de Inscrição",
  "mensagem": "As inscrições vão até sexta-feira às 23:59.",
  "processoSeletivo": {
    "id": 1
  }
}
```

### Resposta — 201 Created

```json
{
  "id": 1,
  "titulo": "Prazo de Inscrição",
  "mensagem": "As inscrições vão até sexta-feira às 23:59.",
  "processoSeletivo": {
    "id": 1,
    "nome": "Processo Seletivo 2026",
    "descricao": "Processo para seleção de alunos",
    "qtdVagas": 20,
    "status": "CADASTRADO"
  }
}
```

### Erro de validação — 400 Bad Request

Ocorre quando os campos `titulo` ou `mensagem` estão vazios.

#### Exemplo

```json
{
  "titulo": "",
  "mensagem": "",
  "processoSeletivo": null
}
```

#### Resposta

```json
{
  "error": "Bad Request",
  "detail": "Erro de validação",
  "messages": [
    "titulo: não deve estar em branco",
    "mensagem: não deve estar em branco"
  ]
}
```

### Processo seletivo não informado — 400 Bad Request

Ocorre quando o campo `processoSeletivo` não é informado no corpo da requisição.

#### Exemplo

```json
{
  "titulo": "Balão de teste",
  "mensagem": "Mensagem de teste",
  "processoSeletivo": null
}
```

#### Resposta

```json
{
  "error": "Bad Request",
  "detail": "O ID do processo seletivo é obrigatório para criar um balão.",
  "messages": null
}
```

### Processo inexistente — 404 Not Found

Ocorre quando o ID do processo seletivo informado no corpo não existe no banco de dados.

#### Exemplo

```json
{
  "titulo": "Balão inválido",
  "mensagem": "Este processo não existe.",
  "processoSeletivo": {
    "id": 999
  }
}
```

#### Resposta

```json
{
  "error": "Not Found",
  "detail": "Processo seletivo não encontrado com o ID: 999",
  "messages": null
}
```

---

## PUT /balao-informativo/{id}

Atualiza os dados de um balão informativo existente, especificamente o título e a mensagem.

O campo `processoSeletivo` **não deve ser enviado** no PUT.

O vínculo existente entre o balão informativo e o processo seletivo é preservado durante a atualização.

### Parâmetro

| Nome | Tipo | Obrigatório |
|---|---|---|
| id | Long | Sim |

### Request Body

```json
{
  "titulo": "Prazo Prorrogado",
  "mensagem": "As inscrições foram prorrogadas até domingo."
}
```

O processo seletivo associado ao balão permanece o mesmo após a atualização.

### Resposta — 200 OK

```json
{
  "id": 1,
  "titulo": "Prazo Prorrogado",
  "mensagem": "As inscrições foram prorrogadas até domingo.",
  "processoSeletivo": {
    "id": 1,
    "nome": "Processo Seletivo 2026",
    "descricao": "Processo para seleção de alunos",
    "qtdVagas": 20,
    "status": "CADASTRADO"
  }
}
```

### Balão não encontrado — 404 Not Found

```json
{
  "error": "Not Found",
  "detail": "Balão informativo não encontrado"
}
```

---

## DELETE /balao-informativo/{id}

Remove um balão informativo pelo ID.

### Parâmetro

| Nome | Tipo | Obrigatório |
|---|---|---|
| id | Long | Sim |

### Resposta — 204 No Content

Sem corpo de resposta.

### Balão não encontrado — 404 Not Found

```json
{
  "error": "Not Found",
  "detail": "Balão informativo não encontrado"
}
```