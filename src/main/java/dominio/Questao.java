package dominio;

import infraestrutura.dto.QuestaoDto;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;

@Entity(name = "questao")
public class Questao extends ObjetoDeDominio{

    @Column
    private String enunciado;

    @Column(name = "resposta_correta")
    private String respostaCorreta;

    @Column
    private BigDecimal valor;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "questao")
    private List<Alternativa> alternativas;

    @Column(name = "multipla_escolha")
    private boolean multipaEscolha;

    @Column
    private boolean publica;


    public static Questao instanciarPorId(Long id){
        Questao questao = new Questao();
        questao.setId(id);
        return questao;
    }


    public static Questao instanciar(QuestaoDto dto, List<Alternativa> alternativas, String usuario){
        Questao questao = new Questao();
        questao.setEnunciado(dto.enunciado);
        questao.setMultipaEscolha(dto.multiplaEscolha);
        questao.setAlternativas(alternativas);
        questao.setValor(dto.valor);
        questao.setRespostaCorreta(dto.resposta);
        questao.setUsuario(usuario);
        return questao;
    }

    public boolean isPublica() {
        return publica;
    }

    public void setPublica(boolean publica) {
        this.publica = publica;
    }

    public String getEnunciado() {
        return enunciado;
    }

    public void setEnunciado(String enunciado) {
        this.enunciado = enunciado;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public List<Alternativa> getAlternativas() {
        return alternativas;
    }

    public void setAlternativas(List<Alternativa> alternativas) {
        this.alternativas = alternativas;
    }

    public boolean isMultipaEscolha() {
        return multipaEscolha;
    }

    public void setMultipaEscolha(boolean multipaEscolha) {
        this.multipaEscolha = multipaEscolha;
    }

    public String getRespostaCorreta() {
        return respostaCorreta;
    }

    public void setRespostaCorreta(String respostaCorreta) {
        this.respostaCorreta = respostaCorreta;
    }
}
