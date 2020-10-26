package infrastructure.dto;

import domain.Prova;
import domain.Questao;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.List;

@Schema(
        name = "Provas",
        description = "JSON com informações sobre uma prova específica")
public class ProvaDto {
    public String nome;
    public BigDecimal notaMaxima;
    public List<Long> idsQuestoes;

    public Prova paraDominio(ProvaDto dto, List<Questao> questoes){
        return Prova.instanciar(dto.nome, dto.notaMaxima, questoes);
    }
}
