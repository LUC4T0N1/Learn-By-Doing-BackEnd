package domain;

import infrastructure.dto.ProvaRespondidaDto;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;

@Entity(name = "prova_respondida")
public class ProvaRespondida extends ObjetoDeDominio{
    @Column(name = "questoes_corrigidas")
    private int questoesCorrigidas;

    @ManyToOne
    private Prova prova;

    @Column
    private Boolean corrigida;

    @Column
    public int resolucoes;

    @Column(name = "nome_aluno")
    private String nomeAluno;

    @Column(name = "email_aluno")
    private String emailAluno;

    @Column(name = "nota_aluno")
    private BigDecimal notaAluno;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "prova")
    private List<QuestaoRespondida> questoesRespondidas;


    protected ProvaRespondida(){}

    public static ProvaRespondida instanciar(ProvaRespondidaDto dto, Prova prova, String usuario){
        ProvaRespondida provaRespondida = new ProvaRespondida();
        provaRespondida.setEmailAluno(dto.emailAluno);
        provaRespondida.setNomeAluno(dto.nomeAluno);
        provaRespondida.setProva(prova);
        provaRespondida.setUsuario(usuario);
        provaRespondida.setNotaAluno(new BigDecimal(0));
        provaRespondida.setCorrigida(false);
        return  provaRespondida;
    }

    public Prova getProva() {
        return prova;
    }

    public String getNomeAluno() {
        return nomeAluno;
    }

    public String getEmailAluno() {
        return emailAluno;
    }

    public List<QuestaoRespondida> getQuestoesRespondidas() {
        return questoesRespondidas;
    }

    public BigDecimal getNotaAluno() {
        return notaAluno;
    }


    public void setProva(Prova prova) {
        this.prova = prova;
    }

    public void setNomeAluno(String nomeAluno) {
        this.nomeAluno = nomeAluno;
    }

    public void setQuestoesRespondidas(List<QuestaoRespondida> questoesRespondidas) {
        this.questoesRespondidas = questoesRespondidas;
    }

    public void setEmailAluno(String emailAluno) {
        this.emailAluno = emailAluno;
    }

    public void setNotaAluno(BigDecimal notaAluno) {
        this.notaAluno = notaAluno;
    }

    public int getQuestoesCorrigidas() {
        return questoesCorrigidas;
    }

    public void setQuestoesCorrigidas(int questoesCorrigidas) {
        this.questoesCorrigidas = questoesCorrigidas;
    }

    public Boolean getCorrigida() {
        return corrigida;
    }

    public int getResolucoes() {
        return resolucoes;
    }

    public void setResolucoes(int resolucoes) {
        this.resolucoes = resolucoes;
    }

    public void setCorrigida(Boolean corrigida) {
        this.corrigida = corrigida;
    }
}
