package infraestrutura.repository;

import dominio.Alternativa;
import dominio.Conteudo;
import infraestrutura.dto.AlternativaDto;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
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

    public List<Alternativa> buscarAlternativas(List<AlternativaDto> alternativas){
        List<Alternativa> alts =  new ArrayList<>();
        for(AlternativaDto alt : alternativas){
            Alternativa a = find("enunciado = ?1", alt.enunciado).firstResult();
            if(a!= null) alts.add(a);
        }
        return alts;
    }

}
