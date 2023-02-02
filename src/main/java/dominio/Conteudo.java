package dominio;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

@Entity(name = "conteudo")
public class Conteudo extends ObjetoDeDominio {
    @Column
    private String nome;

    @Column(name = "numero_provas")
    private Long numeroProvas;

    @Column(name = "numero_questoes")
    private Long numeroQuestoes;

    @ManyToMany
    @JoinTable(
            name = "conteudo_prova",
            joinColumns = @JoinColumn(name = "conteudo_id"),
            inverseJoinColumns = @JoinColumn(name = "provas_id"))
    private List<Prova> provas;

    @ManyToMany
    @JoinTable(
            name = "conteudo_questao",
            joinColumns = @JoinColumn(name = "conteudo_id"),
            inverseJoinColumns = @JoinColumn(name = "questao_id"))
    private List<Questao> questoes;


    public static Conteudo instanciar(String nome, String usuario){
        Conteudo conteudo = new Conteudo();
        conteudo.setNome(nome);
        conteudo.setNumeroProvas(0L);
        conteudo.setNumeroQuestoes(0L);
        conteudo.setUsuario(usuario);
        return conteudo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public List<Prova> getProvas() {
        return provas;
    }

    public void setProvas(List<Prova> provas) {
        this.provas = provas;
    }

    public void setNumeroProvas(Long numeroProvas) {
        this.numeroProvas = numeroProvas;
    }

    public Long getNumeroProvas() {
        return numeroProvas;
    }

    public List<Questao> getQuestoes() {
        return questoes;
    }

    public void setQuestoes(List<Questao> questoes) {
        this.questoes = questoes;
    }

    public Long getNumeroQuestoes() {
        return numeroQuestoes;
    }

    public void setNumeroQuestoes(Long numeroQuestoes) {
        this.numeroQuestoes = numeroQuestoes;
    }

    @Override
    public String toString() {
        return "Conteudo{" +
                "nome='" + nome + '\'' +
                ", numeroProvas=" + numeroProvas +
                ", numeroQuestoes=" + numeroQuestoes +
                ", provas=" + provas +
                ", questoes=" + questoes +
                '}';
    }
}
