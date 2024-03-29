package dominio;

import infraestrutura.dto.AlternativaDto;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity(name = "alternativa")
public class Alternativa extends ObjetoDeDominio {
  @Column(length = 10000, columnDefinition = "TEXT")
  private String enunciado;

  @Column private Boolean correta;

  @ManyToOne private Questao questao;

  public static Alternativa instanciar(AlternativaDto dto, String usuario) {
    Alternativa alternativa = new Alternativa();
    alternativa.setCorreta(dto.correta);
    alternativa.setEnunciado(dto.enunciado);
    alternativa.setUsuario(usuario);
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
