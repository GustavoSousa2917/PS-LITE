package ufc.npi.pslite.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ufc.npi.pslite.model.BalaoInformativo;

import java.util.List;
public interface BalaoInformativoRepository extends JpaRepository<BalaoInformativo, Long>{

    List<BalaoInformativo> findByProcessoSeletivoId(Long id);
}

