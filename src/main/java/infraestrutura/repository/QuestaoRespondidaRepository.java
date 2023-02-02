package infraestrutura.repository;

import dominio.QuestaoRespondida;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.logging.Log;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.WebApplicationException;
import java.util.List;

@ApplicationScoped
public class QuestaoRespondidaRepository implements PanacheRepository<QuestaoRespondida> {

    public void cadastrarQuestoesRespondidas(List<QuestaoRespondida> questoesRespondidas) {
        try {
            Log.info("Cadastrando questões resolvidas..");
            persist(questoesRespondidas);
            Log.info("Questões resolvidas cadastradas com sucesso!");
        }catch (WebApplicationException e){
            throw new WebApplicationException(e.getMessage(), e.getResponse());
        }
    }

    public Integer buscarQuestoesRespondidasPorUsuario(String usuario){
        try {
            return find("usuario = ?1", usuario).list().size();
        }catch (Exception e){
            return 0;
        }
    }

}
