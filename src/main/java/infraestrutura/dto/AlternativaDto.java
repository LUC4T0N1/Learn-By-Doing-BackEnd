package infraestrutura.dto;

import dominio.Alternativa;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(
    name = "Alternativas",
    description = "JSON com informações sobre uma alternattiva específica")
public class AlternativaDto {
  public String enunciado;
  public boolean correta;
  public Long id;

  //    public Alternativa paraDominio(AlternativaDto dto){
  //        return Alternativa.instanciar(dto);
  //    }

  public static AlternativaDto instanciar(Alternativa alternativa) {
    AlternativaDto alternativaDto = new AlternativaDto();
    alternativaDto.setCorreta(alternativa.getCorreta());
    alternativaDto.setId(alternativa.getId());
    alternativaDto.setEnunciado(alternativa.getEnunciado());
    return alternativaDto;
  }

  public static AlternativaDto instanciarSemResposta(Alternativa alternativa) {
    AlternativaDto alternativaDto = new AlternativaDto();
    alternativaDto.setId(alternativa.getId());
    alternativaDto.setEnunciado(alternativa.getEnunciado());
    return alternativaDto;
  }

  public void setEnunciado(String enunciado) {
    this.enunciado = enunciado;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public void setCorreta(boolean correta) {
    this.correta = correta;
  }
}
