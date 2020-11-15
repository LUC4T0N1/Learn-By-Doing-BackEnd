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
    public Long id;
    public Boolean publica;
    public Long dificuldade;
    public Long popularidade;
    public List<QuestaoDto> questoes;

    public Prova paraDominio(ProvaDto dto, List<Questao> questoes, String usuario, Boolean publica){
        return Prova.instanciar(dto.nome, dto.notaMaxima, questoes, usuario, publica);
    }

    public static ProvaDto instanciar(Prova prova){
        ProvaDto provaDto = new ProvaDto();
        provaDto.setNome(prova.getNome());
        provaDto.setId(prova.getId());
        provaDto.setDificuldade(prova.getDificuldade());
        provaDto.setPopularidade(prova.getRealizacoes());
        return provaDto;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public void setDificuldade(Long dificuldade) {
        this.dificuldade = dificuldade;
    }

    public Long getPopularidade() {
        return popularidade;
    }

    public void setPopularidade(Long popularidade) {
        this.popularidade = popularidade;
    }

    public void setQuestoes(List<QuestaoDto> questoes) {
        this.questoes = questoes;
    }
}
