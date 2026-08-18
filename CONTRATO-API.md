Contrato da API — PS Lite

Processo Seletivo

Base URL:

http://localhost:8080

Swagger UI:

http://localhost:8080/swagger-ui/index.html

1. GET /processo-seletivo

Lista todos os processos seletivos.

Resposta — 200 OK

[
{
"id": 1,
"nome": "Processo Seletivo 2026",
"descricao": "Processo para seleção de alunos",
"qtdVagas": 20,
"status": "CADASTRADO"
}
]

2. GET /processo-seletivo/{id}

Busca um processo seletivo pelo ID.

Parâmetro

Nome

Tipo

Obrigatório

id

Long

Sim

Resposta — 200 OK

{
"id": 1,
"nome": "Processo Seletivo 2026",
"descricao": "Processo para seleção de alunos",
"qtdVagas": 20,
"status": "CADASTRADO"
}

Processo não encontrado — 404 Not Found

{
"error": "Not Found",
"detail": "Processo seletivo não encontrado"
}

3. POST /processo-seletivo

Cria um novo processo seletivo.

Request

{
"nome": "Processo Seletivo 2026",
"descricao": "Processo para seleção de alunos",
"qtdVagas": 20,
"status": "CADASTRADO"
}

Resposta — 201 Created

{
"id": 1,
"nome": "Processo Seletivo 2026",
"descricao": "Processo para seleção de alunos",
"qtdVagas": 20,
"status": "CADASTRADO"
}

Erro de validação — 400 Bad Request

Exemplo:

{
"nome": "",
"descricao": "Teste",
"qtdVagas": -1,
"status": "CADASTRADO"
}

Resposta:

{
"error": "Bad Request",
"detail": "Erro de validação",
"messages": [
"nome: não deve estar em branco",
"qtdVagas: deve ser maior ou igual a 0"
]
}

4. PUT /processo-seletivo/{id}

Atualiza um processo seletivo existente.

Parâmetro

Nome

Tipo

Obrigatório

id

Long

Sim

Request

{
"nome": "Processo Seletivo 2026 - Atualizado",
"descricao": "Descrição atualizada",
"qtdVagas": 25,
"status": "CADASTRADO"
}

Resposta — 200 OK

{
"id": 1,
"nome": "Processo Seletivo 2026 - Atualizado",
"descricao": "Descrição atualizada",
"qtdVagas": 25,
"status": "CADASTRADO"
}

O id utilizado na atualização é o informado na URL.

Processo não encontrado — 404 Not Found

{
"error": "Not Found",
"detail": "Processo seletivo não encontrado"
}

5. DELETE /processo-seletivo/{id}

Remove um processo seletivo.

Parâmetro

Nome

Tipo

Obrigatório

id

Long

Sim

Resposta — 204 No Content

Sem corpo de resposta.

Processo não encontrado — 404 Not Found

{
"error": "Not Found",
"detail": "Processo seletivo não encontrado"
}

Modelo ProcessoSeletivo

Campo

Tipo

Obrigatório

Regra

id

Long

Não

Gerado pelo sistema

nome

String

Sim

Não pode estar em branco

descricao

String

Não

Opcional

qtdVagas

Integer

Não

Maior ou igual a 0; default 0

status

String

Não

Default CADASTRADO

Status disponíveis

CADASTRADO
FINALIZADO
CANCELADO

Formato de erros

400 Bad Request

Utilizado para erros de validação.

{
"error": "Bad Request",
"detail": "Erro de validação",
"messages": [
"campo: mensagem"
]
}

404 Not Found

Utilizado quando o processo seletivo solicitado não existe.

{
"error": "Not Found",
"detail": "Processo seletivo não encontrado"
}