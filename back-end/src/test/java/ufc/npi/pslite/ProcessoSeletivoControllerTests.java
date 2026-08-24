package ufc.npi.pslite;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ufc.npi.pslite.repository.BalaoInformativoRepository;
import ufc.npi.pslite.repository.ProcessoSeletivoRepository;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ProcessoSeletivoControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BalaoInformativoRepository balaoInformativoRepository;

    @Autowired
    private ProcessoSeletivoRepository processoSeletivoRepository;

    @BeforeEach
    void limparBanco() {
        balaoInformativoRepository.deleteAll();
        processoSeletivoRepository.deleteAll();
    }

    @Test
    void deveExecutarCrudCompletoDeProcesso() throws Exception {
        String processo = objectMapper.writeValueAsString(new ProcessoRequest(
                "Processo Seletivo 2026",
                "Processo para seleção de alunos",
                20,
                "CADASTRADO"
        ));

        String response = mockMvc.perform(post("/processo-seletivo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(processo))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.nome").value("Processo Seletivo 2026"))
                .andExpect(jsonPath("$.qtdVagas").value(20))
                .andExpect(jsonPath("$.status").value("CADASTRADO"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        long id = objectMapper.readTree(response).get("id").asLong();

        mockMvc.perform(get("/processo-seletivo/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id));

        mockMvc.perform(get("/processo-seletivo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        String update = objectMapper.writeValueAsString(new ProcessoRequest(
                "Processo Seletivo 2026 - Atualizado",
                "Descrição atualizada",
                25,
                "FINALIZADO"
        ));

        mockMvc.perform(put("/processo-seletivo/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(update))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.nome").value("Processo Seletivo 2026 - Atualizado"))
                .andExpect(jsonPath("$.qtdVagas").value(25))
                .andExpect(jsonPath("$.status").value("FINALIZADO"));

        mockMvc.perform(delete("/processo-seletivo/{id}", id))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/processo-seletivo/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.detail").value("Processo seletivo não encontrado"));
    }

    @Test
    void deveAplicarDefaultsNoCreate() throws Exception {
        String processo = """
                {
                  "nome": "Processo com defaults"
                }
                """;

        mockMvc.perform(post("/processo-seletivo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(processo))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.qtdVagas").value(0))
                .andExpect(jsonPath("$.status").value("CADASTRADO"));
    }

    @Test
    void deveRejeitarNomeVazioEQtdVagasNegativo() throws Exception {
        String processo = """
                {
                  "nome": "",
                  "descricao": "Teste",
                  "qtdVagas": -1,
                  "status": "CADASTRADO"
                }
                """;

        mockMvc.perform(post("/processo-seletivo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(processo))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.detail").value("Erro de validação"))
                .andExpect(jsonPath("$.messages").isArray())
                .andExpect(jsonPath("$.messages", hasItem("nome: não deve estar em branco")))
                .andExpect(jsonPath("$.messages", hasItem("qtdVagas: deve ser maior ou igual a 0")));
    }

    @Test
    void deveRejeitarQtdVagasENStatusNulos() throws Exception {
        String processo = """
                {
                  "nome": "Processo inválido",
                  "qtdVagas": null,
                  "status": null
                }
                """;

        mockMvc.perform(post("/processo-seletivo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(processo))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.messages", hasItem("qtdVagas: não pode ser nulo")))
                .andExpect(jsonPath("$.messages", hasItem("status: não pode ser nulo")));
    }

    @Test
    void deveRetornar404ParaProcessoInexistente() throws Exception {
        mockMvc.perform(get("/processo-seletivo/999999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.detail").value("Processo seletivo não encontrado"));
    }

    @Test
    void deveRetornar404ParaPutEDeleteInexistentes() throws Exception {
        String processo = """
                {
                  "nome": "Processo inexistente",
                  "qtdVagas": 1,
                  "status": "CADASTRADO"
                }
                """;

        mockMvc.perform(put("/processo-seletivo/999999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(processo))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.detail").value("Processo seletivo não encontrado"));

        mockMvc.perform(delete("/processo-seletivo/999999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.detail").value("Processo seletivo não encontrado"));
    }

    @Test
    void deveRetornar204NoDelete() throws Exception {
        String processo = """
                {
                  "nome": "Processo para delete"
                }
                """;

        String response = mockMvc.perform(post("/processo-seletivo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(processo))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        long id = objectMapper.readTree(response).get("id").asLong();

        mockMvc.perform(delete("/processo-seletivo/{id}", id))
                .andExpect(status().isNoContent());
    }

    @Test
    void deveRetornar400ParaJsonMalformado() throws Exception {
        mockMvc.perform(post("/processo-seletivo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"nome\": \"Processo\""))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.detail").value("Corpo da requisição inválido"));
    }

    private record ProcessoRequest(
            String nome,
            String descricao,
            Integer qtdVagas,
            String status
    ) {
    }
}
