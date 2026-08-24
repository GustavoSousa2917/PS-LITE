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


Balão Informativo
Base URL:
http://localhost:8080

Swagger UI:
http://localhost:8080/swagger-ui/index.html

GET /balao-informativo/por-processo/{idProcesso}

Resposta — 200 OK

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

Processo não encontrado — 404 Not Found
json


{
"error": "Not Found",
"detail": "Processo seletivo não encontrado com o ID: 99"
}

GET /balao-informativo/{id}
Busca um balão informativo pelo seu ID.

Resposta — 200 OK
json


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
Balão não encontrado — 404 Not Found
json


{
"error": "Not Found",
"detail": "Balão informativo não encontrado"
}

POST /balao-informativo
Cria um novo balão informativo vinculado a um processo seletivo existente.

Request Body
json


{
"titulo": "Prazo de Inscrição",
"mensagem": "As inscrições vão até sexta-feira às 23:59.",
"processoSeletivo": {
"id": 1
}
}
Resposta — 201 Created
json


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
Erro de Validação — 400 Bad Request
Exemplo de envio inválido:

json


{
"titulo": "",
"mensagem": "",
"processoSeletivo": null
}
Resposta:

json


{
"error": "Bad Request",
"detail": "Erro de validação",
"messages": [
"titulo: não deve estar em branco",
"mensagem: não deve estar em branco",
"processoSeletivo: Processo seletivo é obrigatório"
]
}
Processo Inexistente — 404 Not Found
Disparado quando o id do processo seletivo informado no corpo não existe no banco de dados.

json


{
"error": "Not Found",
"detail": "Processo seletivo não encontrado com o ID: 99"
}

PUT /balao-informativo/{id}

Atualiza os dados de um balão informativo existente (título e mensagem).

Request Body
json


{
"titulo": "Prazo Prorrogado",
"mensagem": "As inscrições foram prorrogadas até domingo.",
"processoSeletivo": {
"id": 1
}
}
Resposta — 200 OK
json


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
Balão não encontrado — 404 Not Found
json


{
"error": "Not Found",
"detail": "Balão informativo não encontrado"
}

DELETE /balao-informativo/{id}

Remove um balão informativo pelo ID.

Resposta — 204 No Content
(Sem corpo de resposta)

Balão não encontrado — 404 Not Found
json


{
"error": "Not Found",
"detail": "Balão informativo não encontrado"
}