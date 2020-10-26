package infrastructure.dto;

import domain.Alternativa;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(
        name = "Alternativas",
        description = "JSON com informações sobre uma alternattiva específica")
public class AlternativaDto {
    public String enunciado;
    public boolean correta;

    public Alternativa paraDominio(AlternativaDto dto){
        return Alternativa.instanciar(dto);
    }

}
