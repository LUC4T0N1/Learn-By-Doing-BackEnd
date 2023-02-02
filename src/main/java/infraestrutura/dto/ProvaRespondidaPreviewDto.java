package infraestrutura.dto;

import java.math.BigDecimal;
import java.util.List;

public class ProvaRespondidaPreviewDto {

    public String nomeProva;
    public List<String> conteudos;
    public Boolean corrigida;
    public Boolean publica;
    public String dataResolucao;
    public BigDecimal nota;
    public Long id;
    public BigDecimal notaMaxima;

    protected  ProvaRespondidaPreviewDto(){}



    public static ProvaRespondidaPreviewDto instanciar(String nomeProva, List<String> conteudos,
                          Boolean corrigida, Boolean publica, String dataResolucao, BigDecimal nota, Long id, BigDecimal notaMaxima){
        ProvaRespondidaPreviewDto dto = new  ProvaRespondidaPreviewDto();
        dto.nomeProva = nomeProva;
        dto.conteudos = conteudos;
        dto.corrigida = corrigida;
        dto.publica = publica;
        dto.dataResolucao = dataResolucao;
        dto.nota = nota;
        dto.id  = id;
        dto.notaMaxima = notaMaxima;
        return dto;
    }

}
