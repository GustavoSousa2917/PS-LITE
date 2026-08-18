package ufc.npi.pslite.controller;

import jakarta.validation.Valid;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ufc.npi.pslite.model.BalaoInformativo;
import ufc.npi.pslite.service.BalaoInformativoService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/balao-informativo")

public class BalaoInformativoController {
    private final BalaoInformativoService service;

    public BalaoInformativoController(BalaoInformativoService service) {
        this.service = service;
    }
    @PostMapping
    public ResponseEntity<BalaoInformativo> create(@Valid @RequestBody BalaoInformativo balao) {
        BalaoInformativo criado = service.create(balao);
        return ResponseEntity.status(HttpStatus.CREATED).body(criado);
    }
    @GetMapping("/por-processo/{idProcesso}")
    public ResponseEntity<List<BalaoInformativo>> listarPorProcesso(@PathVariable Long idProcesso) {
        List<BalaoInformativo> lista = service.listarPorProcesso(idProcesso);
        return ResponseEntity.ok(lista);
    }
    @GetMapping("/{id}")
    public ResponseEntity<BalaoInformativo> findById(@PathVariable Long id) {
        BalaoInformativo balao = service.findById(id);
        return ResponseEntity.ok(balao);
    }
    @PutMapping("/{id}")
    public ResponseEntity<BalaoInformativo> update(@PathVariable Long id, @Valid @RequestBody BalaoInformativo balaoAtualizado) {
        BalaoInformativo atualizado = service.update (id, balaoAtualizado);
        return ResponseEntity.ok(atualizado);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}