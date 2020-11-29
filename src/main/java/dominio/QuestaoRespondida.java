package dominio;

import infraestrutura.dto.QuestaoRespondidaDto;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.math.BigDecimal;
@Entity(name = "questao_respondida")
public class QuestaoRespondida extends ObjetoDeDominio{
    @ManyToOne
    private Questao questao;

    @Column(name = "resposta_aluno")
    private String respostaAluno;

    @Column(name = "nota_aluno")
    private BigDecimal notaAluno;

    @ManyToOne
    private ProvaRespondida prova;

    @Column(name = "comentario_professor")
    private String comentarioProfessor;


    protected QuestaoRespondida(){}

    public static QuestaoRespondida instanciar(QuestaoRespondidaDto dto){
        QuestaoRespondida questaoRespondida = new QuestaoRespondida();
        questaoRespondida.setRespostaAluno(dto.respostaAluno);
        questaoRespondida.setQuestao(Questao.instanciarPorId(dto.idQuestao));
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
