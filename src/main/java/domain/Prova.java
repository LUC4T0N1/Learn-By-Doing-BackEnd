package domain;

import application.utlis.DataUtils;
import infrastructure.dto.ProvaDto;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Entity(name = "prova")
public class Prova extends ObjetoDeDominio{

    @Column
    private String nome;

    @Column
    private BigDecimal tempo;

    @Column(name = "quantidade_questoes")
    private int quantidadeQuestoes;

    @Column(name = "data_inicial")
    private Date dataInicial;

    @Column(name = "data_final")
    private Date dataFinal;

    @Column(name = "media_notas")
    private BigDecimal mediaNotas;

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

    public static Prova instanciar(ProvaDto dto, List<Questao> questoes, String usuario, Boolean publica){
        Prova prova = new Prova();
        prova.setNome(dto.nome);
        prova.setNotaMaxima(dto.notaMaxima);
        prova.setQuestoes(questoes);
        prova.setUsuario(usuario);
        prova.setPublica(publica);
        prova.setRealizacoes(0L);
        prova.setMediaNotas(new BigDecimal(0));
        prova.setQuantidadeQuestoes(questoes.size());
        prova.setTempo(dto.tempo);
        if(dto.dataInicial!= null && dto.dataFinal!= null){
            prova.setDataInicial(DataUtils.converterParaDate(dto.dataInicial));
            prova.setDataFinal(DataUtils.converterParaDate(dto.dataFinal));
        }
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

    public BigDecimal getMediaNotas() {
        return mediaNotas;
    }

    public void setMediaNotas(BigDecimal mediaNotas) {
        this.mediaNotas = mediaNotas;
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




    public Long getRealizacoes() {
        return realizacoes;
    }

    public void setRealizacoes(Long realizacoes) {
        this.realizacoes = realizacoes;
    }

    public BigDecimal getTempo() {
        return tempo;
    }

    public void setTempo(BigDecimal tempo) {
        this.tempo = tempo;
    }

    public int getQuantidadeQuestoes() {
        return quantidadeQuestoes;
    }

    public void setQuantidadeQuestoes(int quantidadeQuestoes) {
        this.quantidadeQuestoes = quantidadeQuestoes;
    }

    public Date getDataInicial() {
        return dataInicial;
    }

    public void setDataInicial(Date dataInicial) {
        this.dataInicial = dataInicial;
    }

    public Date getDataFinal() {
        return dataFinal;
    }

    public void setDataFinal(Date dataFinal) {
        this.dataFinal = dataFinal;
    }
}
