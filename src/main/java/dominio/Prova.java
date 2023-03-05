package dominio;

import infraestrutura.dto.ProvaDto;
import java.math.BigDecimal;
import java.util.List;
import javax.persistence.*;

@Entity(name = "prova")
public class Prova extends ObjetoDeDominio {

  @Column private String nome;

  @Column(name = "id_secreto")
  private String idSecreto;

  @Column private Long tempo;

  @Column(name = "quantidade_questoes")
  private int quantidadeQuestoes;

  @Column(name = "data_inicial")
  private String dataInicial;

  @Column(name = "data_final")
  private String dataFinal;

  @Column(name = "media_notas")
  private BigDecimal mediaNotas;

  @Column private Long realizacoes;

  @Column private Long tentativas;

  @Column private Boolean publica;

  @ManyToMany
  @JoinTable(name = "prova_questao")
  private List<Questao> questoes;

  @ManyToMany(mappedBy = "provas")
  private List<Conteudo> conteudos;

  @Column(name = "nota_maxima")
  private BigDecimal notaMaxima;

  public static Prova instanciar(
      ProvaDto dto, List<Questao> questoes, String usuario, String idSecreto) {
    Prova prova = new Prova();
    prova.setNome(dto.nome);
    BigDecimal notaMax = new BigDecimal(0);
    prova.setNotaMaxima(notaMax);
    prova.setIdSecreto(idSecreto);
    prova.setQuestoes(questoes);
    prova.setUsuario(usuario);
    prova.setPublica(dto.publica);
    prova.setRealizacoes(0L);
    prova.setTentativas(dto.tentativas);
    prova.setMediaNotas(new BigDecimal(0));
    prova.setQuantidadeQuestoes(questoes.size());
    prova.setTempo(dto.tempo);
    prova.setDataInicial(dto.dataInicial);
    prova.setDataFinal(dto.dataFinal);
    return prova;
  }

  public List<Conteudo> getConteudos() {
    return conteudos;
  }

  public void setConteudos(List<Conteudo> conteudos) {
    this.conteudos = conteudos;
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

  public Long getTentativas() {
    return tentativas;
  }

  public void setTentativas(Long tentativas) {
    this.tentativas = tentativas;
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

  public String getIdSecreto() {
    return idSecreto;
  }

  public void setIdSecreto(String idSecreto) {
    this.idSecreto = idSecreto;
  }

  public Long getRealizacoes() {
    return realizacoes;
  }

  public void setRealizacoes(Long realizacoes) {
    this.realizacoes = realizacoes;
  }

  public Long getTempo() {
    return tempo;
  }

  public void setTempo(Long tempo) {
    this.tempo = tempo;
  }

  public int getQuantidadeQuestoes() {
    return quantidadeQuestoes;
  }

  public void setQuantidadeQuestoes(int quantidadeQuestoes) {
    this.quantidadeQuestoes = quantidadeQuestoes;
  }

  public String getDataInicial() {
    return dataInicial;
  }

  public void setDataInicial(String dataInicial) {
    this.dataInicial = dataInicial;
  }

  public String getDataFinal() {
    return dataFinal;
  }

  public void setDataFinal(String dataFinal) {
    this.dataFinal = dataFinal;
  }
}
