package domain;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;

@Entity(name = "prova")
public class Prova extends ObjetoDeDominio{

    @Column
    private String nome;

    @Column
    private Long dificuldade;

    @Column
    private Long realizacoes;

    @Column
    private Boolean publica;

    @ManyToMany
    @JoinTable(
            name = "prova_questao")
    private List<Questao> questoes;

    @Column(name = "nota_maxima")
    private BigDecimal notaMaxima;

    public static Prova instanciar(String nome, BigDecimal notaMaxima, List<Questao> questoes, String usuario, Boolean publica){
        Prova prova = new Prova();
        prova.setNome(nome);
        prova.setNotaMaxima(notaMaxima);
        prova.setQuestoes(questoes);
        prova.setUsuario(usuario);
        prova.setPublica(publica);
        return prova;
    }

    public Boolean getPublica() {
        return publica;
    }

    public void setPublica(Boolean publica) {
        this.publica = publica;
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

    public Long getDificuldade() {
        return dificuldade;
    }

    public void setDificuldade(Long dificuldade) {
        this.dificuldade = dificuldade;
    }

    public Long getRealizacoes() {
        return realizacoes;
    }

    public void setRealizacoes(Long realizacoes) {
        this.realizacoes = realizacoes;
    }
}
