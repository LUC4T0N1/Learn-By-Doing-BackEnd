package domain;

import infrastructure.dto.QuestaoDto;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;

@Entity(name = "questao")
public class Questao extends ObjetoDeDominio{

    @Column
    private String enunciado;

    @Column
    private BigDecimal valor;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "questao")
    private List<Alternativa> alternativas;

    @Column(name = "multipla_escolha")
    private boolean multipaEscolha;

    @Column
    private boolean publica;



    public static Questao instanciar(QuestaoDto dto, List<Alternativa> alternativas){
        Questao questao = new Questao();
        questao.setEnunciado(dto.enunciado);
        questao.setMultipaEscolha(dto.multiplaEscolha);
        questao.setAlternativas(alternativas);
        questao.setValor(dto.valor);
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


}
