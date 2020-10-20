package infrastructure.repository;
import domain.Alternativa;
import domain.Prova;
import domain.Questao;
import io.quarkus.hibernate.orm.panache.PanacheRepository;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

@ApplicationScoped
public class ProvaRepository  implements PanacheRepository<Prova> {

    public Prova buscarPorId(Long id) {
        Prova prova = findById(id);
        if (prova == null) throw new WebApplicationException("Prova não encontrada",Response.Status.NOT_FOUND);
        return prova;
    }

    public void cadastrarProva(Prova prova){
        try {
            persist(prova);
        } catch (WebApplicationException e) {
            throw new WebApplicationException(e.getMessage(), e.getResponse());
        }
    }


}
