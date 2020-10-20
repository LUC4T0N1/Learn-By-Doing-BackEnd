package domain;

import infrastructure.dto.AlternativaDto;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity(name = "alternativa")
public class Alternativa extends ObjetoDeDominio{
    @Column
    private String enunciado;

    @Column
    private Boolean correta;

    @ManyToOne
    private Questao questao;

    public static Alternativa instanciar(AlternativaDto dto){
        Alternativa alternativa = new Alternativa();
        alternativa.setCorreta(dto.correta);
        alternativa.setEnunciado(dto.enunciado);

        return alternativa;
    }

    public String getEnunciado() {
        return enunciado;
    }

    public void setEnunciado(String enunciado) {
        this.enunciado = enunciado;
    }

    public Boolean getCorreta() {
        return correta;
    }

    public void setCorreta(Boolean correta) {
        this.correta = correta;
    }

    public Questao getQuestao() {
        return questao;
    }

    public void setQuestao(Questao questao) {
        this.questao = questao;
    }
}
