package infrastructure.dto;

import domain.Alternativa;


public class AlternativaDto {
    public String enunciado;
    public boolean correta;

    public Alternativa paraDominio(AlternativaDto dto){
        return Alternativa.instanciar(dto);
    }

}
