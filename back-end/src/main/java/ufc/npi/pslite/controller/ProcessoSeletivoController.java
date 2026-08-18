package ufc.npi.pslite.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ufc.npi.pslite.model.ProcessoSeletivo;
import ufc.npi.pslite.service.ProcessoSeletivoService;

import java.util.List;

@RestController
@RequestMapping("/processo-seletivo")
public class ProcessoSeletivoController {

    private final ProcessoSeletivoService service;

    public ProcessoSeletivoController(ProcessoSeletivoService service) {
        this.service = service;
    }

    @GetMapping
    public List<ProcessoSeletivo> findAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public ProcessoSeletivo findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProcessoSeletivo create(
            @Valid @RequestBody ProcessoSeletivo processoSeletivo) {
        return service.create(processoSeletivo);
    }

    @PutMapping("/{id}")
    public ProcessoSeletivo update(
            @PathVariable Long id,
            @Valid @RequestBody ProcessoSeletivo processoSeletivo) {
        return service.update(id, processoSeletivo);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}