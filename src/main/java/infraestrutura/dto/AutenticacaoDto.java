package infraestrutura.dto;

public class AutenticacaoDto {
  public String token;
  public String usuario;

  public AutenticacaoDto() {}

  public static AutenticacaoDto instanciar(String token, String usuario) {
    AutenticacaoDto dto = new AutenticacaoDto();
    dto.token = token;
    dto.usuario = usuario;
    return dto;
  }
}
