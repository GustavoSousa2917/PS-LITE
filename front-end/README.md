# PS Lite - Processo Seletivo

Sistema simplificado para gestão de Processos Seletivos e Balões Informativos, desenvolvido como desafio de autoestudo.

## Pré-requisitos
* Java 17
* Node.js (versão 20 ou superior)
* Maven e NPM (ou Yarn)

## Como rodar o Back-end
1. Acesse a pasta `back-end`.
2. Execute o comando `./mvnw spring-boot:run` (ou `mvn spring-boot:run`).
3. A API estará disponível na porta `8080`.
4. A documentação do Swagger pode ser acessada em `http://localhost:8080/swagger-ui.html`.

*Observação:* O sistema utiliza o banco de dados H2 em memória. Os dados começam zerados a cada inicialização.

## Como rodar o Front-end
1. Acesse a pasta `front-end`.
2. Verifique se o arquivo `.env` (ou `.env.development`) possui a variável `VITE_API_BASE_URL=http://localhost:8080`.
3. Instale as dependências executando `npm install`.
4. Inicie o servidor de desenvolvimento com `npm run dev`.
5. Acesse a aplicação no navegador através de `http://localhost:5173`.

## Roteiro de Demonstração (Fluxo Feliz)
Para testar o fluxo completo de Processos e Balões Informativos em menos de 3 minutos, siga os passos:

1. Na tela inicial da aplicação, clique no botão **+ Novo processo**.
2. Preencha os campos obrigatórios (Nome, Vagas e Status) e crie o processo.
3. Na listagem principal, clique no processo recém-criado para abrir a Tela de Detalhes.
4. Role a página até encontrar a seção **Avisos do Processo** (Área de Balões Informativos).
5. Clique em **Novo Balão**. Tente salvar vazio para conferir a validação, depois preencha um título e uma mensagem válidos e salve.
6. O balão aparecerá listado instantaneamente.
7. Clique no ícone de **lápis** no balão criado para testar a edição.
8. Clique no ícone de **lixeira** e confirme para testar a exclusão.