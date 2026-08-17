package ufc.npi.pslite.model;

import ufc.npi.pslite.model.ProcessoSeletivo;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.validation.constraints.NotBlank;

@Entity

public class BalaoInformativo {
    // com isso aqui ele sempre vai criar um id novo ao usar o set
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String titulo;
    @NotBlank
    private String mensagem;

    //esse é a entidade chamada de construtor, pelo que entendi precisa do
    // disso para o banco de dados instalado (JPA) acessar as funções e
    // metodos, e criar essa nova entidade com as informações dentro
    // desse constructor.
    public BalaoInformativo(){
    }

    //JBA pegue o constructor balao e cria um model vazio, após isso invoca
    // o setters para preencher o balão.
    public Long getId() {return id;}
    public void setId(Long id) {this.id = id;}

    public String getTitulo() {return titulo;}
    public void setTitulo(String titulo) {this.titulo = titulo;}

    public String getMensagem() {return mensagem;}
    public void setMensagem(String mensagem) {this.mensagem = mensagem;}


    @ManyToOne
    @JoinColumn(name = "processo_seletivo_id" , nullable = false)
    private ProcessoSeletivo processoSeletivo;

    public ProcessoSeletivo getProcessoSeletivo() {
        return processoSeletivo;
    }

    public void setProcessoSeletivo(ProcessoSeletivo processoSeletivo) {
        this.processoSeletivo = processoSeletivo;
    }
}
//O código sabe que está pegando o título e a mensagem do ID correspondente porque todos
// eles são empacotados juntos dentro de um único Objeto na memória do computador.
//
//Se você pedir uma lista com 10 balões, o JPA não vai criar variáveis soltas
// pelo código. Ele vai criar 10 "pacotes" (Objetos) diferentes usando o
// construtor vazio 10 vezes. Cada pacote terá o seu próprio ID, segurando
// o seu próprio Título e a sua própria Mensagem de forma completamente
// blindada e inseparável.