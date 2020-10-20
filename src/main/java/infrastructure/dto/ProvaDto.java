package infrastructure.dto;

import domain.Prova;
import domain.Questao;

import java.math.BigDecimal;
import java.util.List;

public class ProvaDto {
    public String nome;
    public BigDecimal notaMaxima;
    public List<Long> idsQuestoes;

    public Prova paraDominio(ProvaDto dto, List<Questao> questoes){
        return Prova.instanciar(dto.nome, dto.notaMaxima, questoes);
    }
}
