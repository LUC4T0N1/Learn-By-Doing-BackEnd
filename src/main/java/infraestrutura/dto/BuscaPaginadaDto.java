package infraestrutura.dto;

import java.util.List;

public class BuscaPaginadaDto {
    public List<ConteudoDto> conteudos;
    public List<ProvaDto> provas;


    protected BuscaPaginadaDto() {}

    public static BuscaPaginadaDto instanciar(
             List<ConteudoDto> conteudos,
             List<ProvaDto> provas) {
        BuscaPaginadaDto dto = new BuscaPaginadaDto();
        dto.conteudos = conteudos;
        dto.provas = provas;
        return dto;
    }
}
