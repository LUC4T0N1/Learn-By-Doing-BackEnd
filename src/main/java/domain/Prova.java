package domain;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;

@Entity(name = "prova")
public class Prova extends ObjetoDeDominio{

    @Column
    private String nome;

    @ManyToMany
    @JoinTable(
            name = "prova_questao")
    private List<Questao> questoes;

    @Column(name = "nota_maxima")
    private BigDecimal notaMaxima;

    public static Prova instanciar(String nome, BigDecimal notaMaxima, List<Questao> questoes){
        Prova prova = new Prova();
        prova.setNome(nome);
        prova.setNotaMaxima(notaMaxima);
        prova.setQuestoes(questoes);
        return prova;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public List<Questao> getQuestoes() {
        return questoes;
    }

    public void setQuestoes(List<Questao> questoes) {
        this.questoes = questoes;
    }

    public BigDecimal getNotaMaxima() {
        return notaMaxima;
    }

    public void setNotaMaxima(BigDecimal notaMaxima) {
        this.notaMaxima = notaMaxima;
    }
}
