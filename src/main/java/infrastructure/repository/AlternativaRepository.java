package infrastructure.repository;

import domain.Alternativa;
import domain.Questao;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.WebApplicationException;
import java.util.List;

@ApplicationScoped
public class AlternativaRepository implements PanacheRepository<Alternativa> {

    public List<Alternativa> cadastrarAlternativas(List<Alternativa> alternativas){
        try {
            persist(alternativas);
            return alternativas;
        } catch (WebApplicationException e) {
            throw new WebApplicationException(e.getMessage(), e.getResponse());
        }
    }
}
