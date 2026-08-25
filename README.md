
--- 

# 🚀 PS Lite — Processo Seletivo NPI

> Sistema enxuto para gerenciamento de Processos Seletivos e Balões Informativos.  
> Projeto desenvolvido para o desafio de autoestudo do **NPI — UFC Quixadá**.

---

## 📌 Status do Projeto

![Status](https://img.shields.io/badge/Status-Conclu%C3%ADdo-brightgreen?style=for-the-badge)
![Java](https://img.shields.io/badge/Java-17-orange?style=for-the-badge&logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3-green?style=for-the-badge&logo=springboot)
![Vue.js](https://img.shields.io/badge/Vue.js-3-emerald?style=for-the-badge&logo=vuedotjs)
![TypeScript](https://img.shields.io/badge/TypeScript-007ACC?style=for-the-badge&logo=typescript&logoColor=white)
![Vite](https://img.shields.io/badge/Vite-646CFF?style=for-the-badge&logo=vite&logoColor=white)

---

## 🛠️ Tecnologias Utilizadas

### **Backend**
- **Linguagem & Framework:** Java 17 | Spring Boot 3
- **Módulos Spring:** Spring Web, Spring Data JPA, Bean Validation
- **Banco de Dados & Migrations:** H2 Database (em memória) | Flyway
- **Documentação:** Springdoc OpenAPI / Swagger UI

### **Frontend**
- **Framework & Core:** Vue 3 | TypeScript | Vite
- **UI & Roteamento:** Vuetify 3 | Vue Router
- **HTTP Client:** Axios
- **Formulários & Validação:** vee-validate | Zod

---

## 📁 Estrutura do Projeto

```text
ps-lite/
├── README.md
├── CONTRATO-API.md
├── back-end/
└── front-end/

```
---
---

## ⚙️ Pré-requisitos

Certifique-se de ter instalado em sua máquina:
* **Java 17+**
* **Node.js 20+**
* **npm**

> [!NOTE]
> **Não é necessário instalar PostgreSQL ou outro SGBD externo.** O projeto utiliza banco H2 em memória com controle de versionamento via Flyway.

---

## 🚀 Como Executar o Projeto

### 1️⃣ Backend

1. Acesse o diretório do backend:
   ```bash
   cd back-end
   ```

2. Execute a aplicação:
    - **Linux / macOS:**
      ```bash
      ./mvnw spring-boot:run
      ```
    - **Windows:**
      ```powershell
      .\mvnw.cmd spring-boot:run
      ```

3. A API estará disponível em: [`http://localhost:8080`](http://localhost:8080)

---

### 2️⃣ Frontend

1. Em outro terminal, acesse o diretório do frontend:
   ```bash
   cd front-end
   ```

2. Instale as dependências:
   ```bash
   npm ci
   ```

3. Inicie o servidor de desenvolvimento:
   ```bash
   npm run dev
   ```

4. A aplicação estará acessível em: [`http://localhost:5173`](http://localhost:5173)

#### 🌐 Variável de Ambiente
O frontend utiliza a seguinte configuração de URL base:
```env
VITE_API_BASE_URL=http://localhost:8080
```

---

## 📖 Documentação & Console

### 📑 Swagger UI
Acesse a documentação interativa para testar os endpoints da API:
👉 [`http://localhost:8080/swagger-ui/index.html`](http://localhost:8080/swagger-ui/index.html)

### 🗄️ Console do Banco H2
Acesse o painel do banco de dados em memória:
👉 [`http://localhost:8080/h2-console`](http://localhost:8080/h2-console)

> [!IMPORTANT]
> **Credenciais de Acesso ao H2:**
> - **JDBC URL:** `jdbc:h2:mem:pslite`
> - **User Name:** `sa`
> - **Password:** *(deixar em branco)*

#### 🔄 Migrations (Flyway)
O Hibernate opera no modo `ddl-auto=validate`. As tabelas são criadas e controladas estritamente via Flyway:
* `V1__processo_seletivo.sql`
* `V2__balao_informativo.sql`

---

## 📋 Funcionalidades & Regras de Negócio

### 🎯 1. Processo Seletivo
Permite listar, visualizar detalhes, cadastrar, editar e remover processos seletivos.

| Campo | Tipo | Regra / Validação |
| :--- | :--- | :--- |
| `id` | `Long` | Gerado automaticamente pela API |
| `nome` | `String` | **Obrigatório** |
| `descricao` | `String` | Opcional |
| `qtdVagas` | `Integer` | Maior ou igual a `0` |
| `status` | `Enum` | `CADASTRADO`, `FINALIZADO` ou `CANCELADO` |

---

### 💬 2. Balão Informativo
Permite associar balões informativos/avisos a um processo seletivo específico (listar por processo, visualizar, criar, editar e excluir).

| Campo | Tipo | Regra / Validação |
| :--- | :--- | :--- |
| `id` | `Long` | Gerado automaticamente pela API |
| `titulo` | `String` | **Obrigatório** |
| `mensagem` | `String` | **Obrigatório** |
| `processoSeletivo` | `FK (Long)`| **Obrigatório** |

---

## 🔗 Endpoints da API (Resumo)

Consulte o arquivo [`CONTRATO-API.md`](./CONTRATO-API.md) para detalhes completos de requisição e resposta.

### 📌 Processo Seletivo
| Método | Rota | Descrição |
| :--- | :--- | :--- |
| `GET` | `/processo-seletivo` | Listar todos os processos |
| `GET` | `/processo-seletivo/{id}` | Buscar processo por ID |
| `POST` | `/processo-seletivo` | Criar novo processo |
| `PUT` | `/processo-seletivo/{id}` | Atualizar dados do processo |
| `DELETE` | `/processo-seletivo/{id}` | Excluir processo |

### 📌 Balão Informativo
| Método | Rota | Descrição |
| :--- | :--- | :--- |
| `GET` | `/balao-informativo/por-processo/{idProcesso}` | Listar balões de um processo |
| `GET` | `/balao-informativo/{id}` | Buscar balão por ID |
| `POST` | `/balao-informativo` | Criar balão informativo |
| `PUT` | `/balao-informativo/{id}` | Atualizar balão informativo |
| `DELETE` | `/balao-informativo/{id}` | Excluir balão informativo |

> [!NOTE]
> - **CORS:** O backend já está configurado para receber requisições de `http://localhost:5173`.
> - **Tratamento de Erros:** Respostas padronizadas para `400 Bad Request` (validação de dados) e `404 Not Found` (recurso inexistente).

---

## 🧪 Testes e Build

### Executar Testes do Backend
```bash
cd back-end
# Linux / macOS
./mvnw test

# Windows
.\mvnw.cmd test
```

### Validar Build do Frontend
```bash
cd front-end
npm run build
```

---

## 🎬 Roteiro de Demonstração

Passo a passo sugerido para a apresentação da solução:

1. Iniciar o **Backend** e o **Frontend**.
2. Abrir o navegador em [`http://localhost:5173`](http://localhost:5173).
3. Exibir a listagem de processos seletivos.
4. Criar um novo processo seletivo e validá-lo na listagem.
5. Acessar a tela de detalhes do processo criado.
6. Editar os dados do processo.
7. Criar um novo **Balão Informativo** vinculado a ele.
8. Editar as informações do balão.
9. Remover o balão informativo.
10. Voltar e conferir a listagem/detalhes atualizados.

---

## 🚫 Fora do Escopo

Para manter o escopo enxuto proposto no desafio, **não** fazem parte desta versão:
- ❌ Autenticação e Autorização (Keycloak / Spring Security)
- ❌ Inscrição de candidatos
- ❌ Upload de arquivos (MinIO / S3)
- ❌ Ranking e pontuação de candidatos
- ❌ Gestão de comissão avaliadora
- ❌ Filtros avançados e automações complexas de workflow

---

## 👥 Equipe e Atribuições

Projeto desenvolvido em equipe para o Processo Seletivo do **NPI — UFC Quixadá**:

* **Dev A — Backend Core:** Configuração do Spring Boot, H2/Flyway V1, CRUD de Processo Seletivo, CORS, Swagger e tratamento global de exceções.
* **Dev B — Backend Balão:** Entidade, migration V2 e CRUD de Balão Informativo.
* **Dev C — Frontend Processo:** Arquitetura da SPA, telas e integração da entidade Processo Seletivo.
* **Dev D — Frontend Balão:** Interface, componentes e integração dos Balões Informativos.
```
