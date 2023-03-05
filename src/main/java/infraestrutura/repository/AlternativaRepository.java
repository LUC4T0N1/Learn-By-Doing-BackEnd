package infraestrutura.repository;

import dominio.Alternativa;
import infraestrutura.dto.AlternativaDto;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.logging.Log;
import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.WebApplicationException;

@ApplicationScoped
public class AlternativaRepository implements PanacheRepository<Alternativa> {

  public List<Alternativa> cadastrarAlternativas(List<Alternativa> alternativas) {
    try {
      Log.info("Cadastrando alternativas..");
      persist(alternativas);
      return alternativas;
    } catch (WebApplicationException e) {
      throw new WebApplicationException(e.getMessage(), e.getResponse());
    }
  }

  public List<Alternativa> buscarAlternativas(List<AlternativaDto> alternativas) {
    List<Alternativa> alts = new ArrayList<>();
    for (AlternativaDto alt : alternativas) {
      Alternativa a = find("enunciado = ?1", alt.enunciado).firstResult();
      if (a != null) alts.add(a);
    }
    return alts;
  }
}
