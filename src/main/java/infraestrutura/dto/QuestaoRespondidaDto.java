package infraestrutura.dto;
import dominio.QuestaoRespondida;

import java.math.BigDecimal;

public class QuestaoRespondidaDto {
    public Long idQuestao;
    public String respostaAluno;
    public BigDecimal notaAluno;
    public QuestaoDto questao;
    public String comentarioProfessor;

    protected QuestaoRespondidaDto(){}

    public static QuestaoRespondidaDto instanciar(QuestaoRespondida questaoRespondida) {
        QuestaoRespondidaDto questaoRespondidaDto = new QuestaoRespondidaDto();
        questaoRespondidaDto.setComentarioProfessor(questaoRespondida.getComentarioProfessor());
        questaoRespondidaDto.setNotaAluno(questaoRespondida.getNotaAluno());
        questaoRespondidaDto.setRespostaAluno(questaoRespondida.getRespostaAluno());
        questaoRespondidaDto.setIdQuestao(questaoRespondida.getId());
        return questaoRespondidaDto;
    }

    public void setIdQuestao(Long idQuestao) {
        this.idQuestao = idQuestao;
    }

    public void setComentarioProfessor(String comentarioProfessor) {
        this.comentarioProfessor = comentarioProfessor;
    }

    public void setRespostaAluno(String respostaAluno) {
        this.respostaAluno = respostaAluno;
    }

    public void setNotaAluno(BigDecimal notaAluno) {
        this.notaAluno = notaAluno;
    }

    public void setQuestao(QuestaoDto questao) {
        this.questao = questao;
    }
}
