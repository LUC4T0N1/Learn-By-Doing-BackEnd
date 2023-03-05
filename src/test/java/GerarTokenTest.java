import aplicacao.autenticacao.GerarTokenService;
import io.quarkus.test.junit.QuarkusTest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class GerarTokenTest {

  @Inject GerarTokenService service;

  @Test
  public void gerarToken() {
    service.gerarToken("d8cbfe9f-18b4-49bc-b71b-7114c896e5d8", "lucas_moniz@hotmail.com");
  }

  @Test
  public void compararDatas() {
    try {
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
      LocalDate dataInicio = LocalDate.parse("2023-02-04", formatter);
      LocalDate dataFim = LocalDate.parse("2023-02-08", formatter);
      LocalDate dataAtual = LocalDate.parse(LocalDateTime.now().format(formatter));
      System.out.println("data final: " + dataFim);
      System.out.println("data inicial: " + dataInicio);
      System.out.println("data atual: " + dataAtual);
      if (!(dataInicio.isBefore(dataAtual) && dataFim.isAfter(dataAtual))
          && !dataInicio.isEqual(dataAtual)
          && !dataFim.isEqual(dataAtual)) throw new WebApplicationException("Periodo invalido!");
    } catch (WebApplicationException e) {
      throw new WebApplicationException(e.getMessage(), e.getResponse());
    }
  }
}
