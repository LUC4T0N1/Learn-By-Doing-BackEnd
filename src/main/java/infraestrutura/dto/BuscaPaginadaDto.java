package infraestrutura.dto;

import java.util.List;

public class BuscaPaginadaDto {
    public List<ConteudoDto> conteudos;
    public List<ProvaDto> provas;
    public List<ProvaRespondidaPreviewDto> provasResolvidas;

    public List<ProvaRespondidaDto> provasCorrigir;

    public List<QuestaoDto> questoes;
    public Long quantidade;


    protected BuscaPaginadaDto() {}

    public static BuscaPaginadaDto instanciar(
             List<ConteudoDto> conteudos,
             List<ProvaDto> provas,
             List<ProvaRespondidaPreviewDto> provasResolvidas,
             List<ProvaRespondidaDto> provasCorrigir,
             List<QuestaoDto> questoes,
             Long quantidade) {
        BuscaPaginadaDto dto = new BuscaPaginadaDto();
        dto.conteudos = conteudos;
        dto.provas = provas;
        dto.quantidade = quantidade;
        dto.provasResolvidas = provasResolvidas;
        dto.provasCorrigir = provasCorrigir;
        dto.questoes = questoes;
        return dto;
    }
}
