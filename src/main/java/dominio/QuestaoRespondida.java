package dominio;

import infraestrutura.dto.QuestaoRespondidaDto;
import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity(name = "questao_respondida")
public class QuestaoRespondida extends ObjetoDeDominio {
  @ManyToOne private Questao questao;

  @Column(length = 20000, columnDefinition = "TEXT", name = "resposta_aluno")
  private String respostaAluno;

  @Column(name = "nota_aluno")
  private BigDecimal notaAluno;

  @ManyToOne private ProvaRespondida prova;

  @Column(length = 20000, columnDefinition = "TEXT", name = "comentario_professor")
  private String comentarioProfessor;

  protected QuestaoRespondida() {}

  public static QuestaoRespondida instanciar(
      QuestaoRespondidaDto dto, Questao questao, String usuario) {
    QuestaoRespondida questaoRespondida = new QuestaoRespondida();
    questaoRespondida.setRespostaAluno(dto.respostaAluno);
    questaoRespondida.setQuestao(questao);
    questaoRespondida.setUsuario(usuario);
    return questaoRespondida;
  }

  public Questao getQuestao() {
    return questao;
  }

  public void setQuestao(Questao questao) {
    this.questao = questao;
  }

  public String getRespostaAluno() {
    return respostaAluno;
  }

  public void setRespostaAluno(String respostaAluno) {
    this.respostaAluno = respostaAluno;
  }

  public BigDecimal getNotaAluno() {
    return notaAluno;
  }

  public void setNotaAluno(BigDecimal notaAluno) {
    this.notaAluno = notaAluno;
  }

  public String getComentarioProfessor() {
    return comentarioProfessor;
  }

  public void setComentarioProfessor(String comentarioProfessor) {
    this.comentarioProfessor = comentarioProfessor;
  }

  public void setProvaRespondida(ProvaRespondida provaRespondida) {
    this.prova = provaRespondida;
  }
}
