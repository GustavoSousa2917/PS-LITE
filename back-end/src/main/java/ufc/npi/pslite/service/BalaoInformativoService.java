package ufc.npi.pslite.service;

import org.springframework.stereotype.Service;
import ufc.npi.pslite.exception.NotFoundException;
import ufc.npi.pslite.model.BalaoInformativo;
import ufc.npi.pslite.model.ProcessoSeletivo;
import ufc.npi.pslite.repository.BalaoInformativoRepository;
import ufc.npi.pslite.repository.ProcessoSeletivoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

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

    public BalaoInformativo create(BalaoInformativo balao){
        long idProcesso = balao.getProcessoSeletivo().getId();

        ProcessoSeletivo processoValidado = processoRepository.findById(idProcesso)
                .orElseThrow(() -> new NotFoundException("Processo seletivo não encontrado com o ID: " + idProcesso));

        balao.setProcessoSeletivo(processoValidado);
        return balaoRepository.save(balao);
    }
    public List<BalaoInformativo> listarPorProcesso(Long idProcesso) {

        return balaoRepository.findByProcessoSeletivoId(idProcesso);
    }
    public BalaoInformativo findById(Long id){
        return balaoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Balão informativo não encontrado"));
    }
    public BalaoInformativo update(Long id, BalaoInformativo balaoAtualizado){
        BalaoInformativo balaoExiste = this.findById(id);
        balaoExiste.setTitulo(balaoAtualizado.getTitulo());
        balaoExiste.setMensagem(balaoAtualizado.getMensagem());
        return balaoRepository.save(balaoExiste);
    }
    public void delete(Long id) {
        BalaoInformativo balaoExiste = this.findById(id);
        balaoRepository.delete(balaoExiste);
    }
}