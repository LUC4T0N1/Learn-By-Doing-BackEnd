package infrastructure.dto;

import domain.Alternativa;
import domain.Prova;
import domain.Questao;

import java.math.BigDecimal;
import java.util.List;

public class QuestaoDto {
    public String enunciado;
    public BigDecimal valor;
    public Boolean multiplaEscolha;
    public List<AlternativaDto> alternativas;

    public Questao paraDominio(QuestaoDto dto, List<Alternativa> alternativas){
        return Questao.instanciar(dto, alternativas);
    }
}
