package domain;

import javax.persistence.*;
import java.util.List;

@Entity(name = "conteudo")
public class Conteudo extends ObjetoDeDominio{
    @Column
    private String nome;

    @ManyToMany
    @JoinTable(
            name = "conteudo_prova")
    private List<Prova> provas;

    public static Conteudo instanciar(String nome){
        Conteudo conteudo = new Conteudo();
        conteudo.setNome(nome);
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
}
