package infraestrutura.repository;

import dominio.QuestaoRespondida;
import io.quarkus.hibernate.orm.panache.PanacheRepository;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.WebApplicationException;
import java.util.List;

@ApplicationScoped
public class QuestaoRespondidaRepository implements PanacheRepository<QuestaoRespondida> {

    public void cadastrarQuestoesRespondidas(List<QuestaoRespondida> questoesRespondidas){
        try {
            persist(questoesRespondidas);
        }catch (WebApplicationException e){
            throw new WebApplicationException(e.getMessage(), e.getResponse());
        }

    }

}
