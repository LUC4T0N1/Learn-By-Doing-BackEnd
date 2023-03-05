package dominio;

import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.Entity;

@Entity(name = "valor_questao")
public class ValorQuestao extends ObjetoDeDominio {

  @Column(name = "prova_id")
  private Long prova;

  @Column(name = "questao_id")
  private Long questao;

  @Column(name = "valor")
  private BigDecimal valor;

  protected ValorQuestao() {}

  public static ValorQuestao instanciar(Long prova, Long questao, BigDecimal valor) {
    ValorQuestao valorQuestao = new ValorQuestao();
    valorQuestao.setProva(prova);
    valorQuestao.setQuestao(questao);
    valorQuestao.setValor(valor);
    return valorQuestao;
  }

  public Long getProva() {
    return prova;
  }

  public void setProva(Long prova) {
    this.prova = prova;
  }

  public Long getQuestao() {
    return questao;
  }

  public void setQuestao(Long questao) {
    this.questao = questao;
  }

  public BigDecimal getValor() {
    return valor;
  }

  public void setValor(BigDecimal valor) {
    this.valor = valor;
  }
}
