package infrastructure.repository;

import domain.Alternativa;
import domain.Prova;
import domain.Questao;
import io.quarkus.hibernate.orm.panache.PanacheRepository;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.List;

@ApplicationScoped
public class QuestaoRepository implements PanacheRepository<Questao> {

    public List<Questao> buscarPorIds(List<Long> ids) {
        List<Questao> questoes = list("id in ?1", ids);
        if (questoes == null) throw new WebApplicationException("Questoes não encontrada", Response.Status.NOT_FOUND);
        return questoes;
    }

    public void cadastrarQuestao(Questao questao){
        try {
            persist(questao);
            for(Alternativa alternativa: questao.getAlternativas()){
                alternativa.setQuestao(questao);
            }
        } catch (WebApplicationException e) {
            throw new WebApplicationException(e.getMessage(), e.getResponse());
        }
    }

}
