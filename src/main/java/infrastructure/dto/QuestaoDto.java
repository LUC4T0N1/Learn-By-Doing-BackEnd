package infrastructure.dto;

import domain.Alternativa;
import domain.Questao;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.math.BigDecimal;
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
}
