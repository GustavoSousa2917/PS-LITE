package ufc.npi.pslite.service;

import org.springframework.stereotype.Service;
import ufc.npi.pslite.exception.NotFoundException;
import ufc.npi.pslite.model.ProcessoSeletivo;
import ufc.npi.pslite.repository.ProcessoSeletivoRepository;

import java.util.List;

@Service
public class ProcessoSeletivoService {

    private final ProcessoSeletivoRepository repository;

    public ProcessoSeletivoService(ProcessoSeletivoRepository repository) {
        this.repository = repository;
    }

    public List<ProcessoSeletivo> findAll() {
        return repository.findAll();
    }

    public ProcessoSeletivo findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() ->
                        new NotFoundException("Processo seletivo não encontrado"));
    }

    public ProcessoSeletivo create(ProcessoSeletivo processoSeletivo) {
        return repository.save(processoSeletivo);
    }

    public ProcessoSeletivo update(Long id, ProcessoSeletivo processoSeletivo) {
        ProcessoSeletivo existente = findById(id);

        existente.setNome(processoSeletivo.getNome());
        existente.setDescricao(processoSeletivo.getDescricao());
        existente.setQtdVagas(processoSeletivo.getQtdVagas());
        existente.setStatus(processoSeletivo.getStatus());

        return repository.save(existente);
    }

    public void delete(Long id) {
        ProcessoSeletivo processoSeletivo = findById(id);
        repository.delete(processoSeletivo);
    }
}