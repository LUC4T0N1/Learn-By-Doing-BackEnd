package infrastructure.dto;

import domain.Alternativa;
import domain.Questao;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
@Schema(
        name = "Questões",
        description = "JSON com informações sobre uma questão específica")
public class QuestaoDto {
    public String enunciado;
    public BigDecimal valor;
    public Boolean multiplaEscolha;
    public List<AlternativaDto> alternativas;

    public Questao paraDominio(QuestaoDto dto, List<Alternativa> alternativas){
        return Questao.instanciar(dto, alternativas);
    }

    public static QuestaoDto instanciar(Questao questao){
        QuestaoDto questaoDto = new QuestaoDto();
        questaoDto.setEnunciado(questao.getEnunciado());
        questaoDto.setMultiplaEscolha(questao.isMultipaEscolha());
        questaoDto.setValor(questao.getValor());
        List<AlternativaDto> alternativas = new ArrayList<>();
        for(Alternativa alternativa : questao.getAlternativas()){
            alternativas.add(AlternativaDto.instanciar(alternativa));
        }
        questaoDto.setAlternativas(alternativas);
        return questaoDto;
    }

    public void setEnunciado(String enunciado) {
        this.enunciado = enunciado;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public void setMultiplaEscolha(Boolean multiplaEscolha) {
        this.multiplaEscolha = multiplaEscolha;
    }

    public void setAlternativas(List<AlternativaDto> alternativas) {
        this.alternativas = alternativas;
    }
}
