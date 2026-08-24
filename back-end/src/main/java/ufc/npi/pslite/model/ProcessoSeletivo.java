package ufc.npi.pslite.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
public class ProcessoSeletivo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "não deve estar em branco")
    @Column(nullable = false)
    private String nome;

    private String descricao;

    @NotNull(message = "não pode ser nulo")
    @Min(value = 0, message = "deve ser maior ou igual a 0")
    @Column(nullable = false)
    private Integer qtdVagas = 0;

    @NotNull(message = "não pode ser nulo")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusProcessoSeletivo status = StatusProcessoSeletivo.CADASTRADO;

    public ProcessoSeletivo() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Integer getQtdVagas() {
        return qtdVagas;
    }

    public void setQtdVagas(Integer qtdVagas) {
        this.qtdVagas = qtdVagas;
    }

    public StatusProcessoSeletivo getStatus() {
        return status;
    }

    public void setStatus(StatusProcessoSeletivo status) {
        this.status = status;
    }
}
