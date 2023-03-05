package dominio;


public class TempoProvaDto {
  public Boolean temTempo;
  public Long tempoRestante;

  protected TempoProvaDto() {}

  public static TempoProvaDto instanciar(Boolean temTempo, Long tempoRestante) {
    TempoProvaDto dto = new TempoProvaDto();
    dto.temTempo = temTempo;
    dto.tempoRestante = tempoRestante;
    return dto;
  }
}
