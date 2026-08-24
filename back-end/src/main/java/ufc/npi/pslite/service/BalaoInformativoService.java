package ufc.npi.pslite.service;

import org.springframework.stereotype.Service;
import ufc.npi.pslite.exception.NotFoundException;
import ufc.npi.pslite.model.BalaoInformativo;
import ufc.npi.pslite.model.ProcessoSeletivo;
import ufc.npi.pslite.repository.BalaoInformativoRepository;
import ufc.npi.pslite.repository.ProcessoSeletivoRepository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class BalaoInformativoService {

    //  pontes com o banco de dados
    private final BalaoInformativoRepository balaoRepository;
    private final ProcessoSeletivoRepository processoRepository;

    // Construto
    public BalaoInformativoService(BalaoInformativoRepository balaoRepository,
                                   ProcessoSeletivoRepository processoRepository) {
        this.balaoRepository = balaoRepository;
        this.processoRepository = processoRepository;
    }

    @Transactional
    public BalaoInformativo create(BalaoInformativo balao){
        if (balao.getProcessoSeletivo() == null || balao.getProcessoSeletivo().getId() == null) {
            throw new IllegalArgumentException("O ID do processo seletivo é obrigatório para criar um balão.");
        }
        Long idProcesso = balao.getProcessoSeletivo().getId();

        ProcessoSeletivo processoValidado = processoRepository.findById(idProcesso)
                .orElseThrow(() -> new NotFoundException("Processo seletivo não encontrado com o ID: " + idProcesso));

        balao.setProcessoSeletivo(processoValidado);
        balao.setId(null);
        return balaoRepository.save(balao);
    }
    @Transactional
    public List<BalaoInformativo> listarPorProcesso(Long idProcesso) {
        if (!processoRepository.existsById(idProcesso)) {
            throw new NotFoundException("Processo seletivo não encontrado com o ID: " + idProcesso);
        }
        return balaoRepository.findByProcessoSeletivoId(idProcesso);
    }
    @Transactional
    public BalaoInformativo findById(Long id){
        return balaoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Balão informativo não encontrado"));
    }
    @Transactional
    public BalaoInformativo update(Long id, BalaoInformativo balaoAtualizado){
        BalaoInformativo balaoExiste = this.findById(id);
        balaoExiste.setTitulo(balaoAtualizado.getTitulo());
        balaoExiste.setMensagem(balaoAtualizado.getMensagem());
        return balaoRepository.save(balaoExiste);
    }
    @Transactional
    public void delete(Long id) {
        BalaoInformativo balaoExiste = this.findById(id);
        balaoRepository.delete(balaoExiste);
    }
}