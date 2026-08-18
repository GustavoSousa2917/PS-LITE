package ufc.npi.pslite.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "processo_seletivo")
public class ProcessoSeletivo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String nome;

    private String descricao;

    @Min(value = 0)
    private Integer qtdVagas = 0;

    @Enumerated(EnumType.STRING)
    private StatusProcessoSeletivo status = StatusProcessoSeletivo.CADASTRADO;

    public ProcessoSeletivo() {}

    public Long getIdd() {return id;}
    public void setId(Long id) {this.id = id;}

    public String getNome() {return nome;}
    public void setNome(String nome) {this.nome = nome;}

    public String getDescricao() {return descricao;}
    public void setDescricao(String descricao){this.descricao = descricao;}

    public Integer getQtdVagas() {return qtdVagas;}
    public void setQtdVagas(Integer qtdVagas) {this.qtdVagas = qtdVagas;}

    public StatusProcessoSeletivo getStatus() {return status;}
    public void setStatus(StatusProcessoSeletivo status) {this.status = status;}


}
