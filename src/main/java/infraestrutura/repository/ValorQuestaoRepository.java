package infraestrutura.repository;

import dominio.ValorQuestao;
import io.quarkus.hibernate.orm.panache.PanacheRepository;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import javax.ws.rs.WebApplicationException;
import java.math.BigDecimal;
import java.util.List;
@ApplicationScoped
public class ValorQuestaoRepository implements PanacheRepository<ValorQuestao> {

@Transactional
        public void cadastrarValores(List<ValorQuestao> valores) {
                try {
                persist(valores);
                }catch (WebApplicationException e){
                throw new WebApplicationException(e.getMessage(), e.getResponse());
                }
        }

        @Transactional
        public void alterarValor(ValorQuestao valorQuestao, BigDecimal valor) {
                try {
                        valorQuestao.setValor(valor);
                }catch (WebApplicationException e){
                        throw new WebApplicationException(e.getMessage(), e.getResponse());
                }
        }

        public List<ValorQuestao> buscarValores(Long idProva) {
                try {
                      return  find("prova_id = ?1", idProva).list();
                }catch (WebApplicationException e){
                        throw new WebApplicationException(e.getMessage(), e.getResponse());
                }
        }

        public ValorQuestao buscarValor(Long idQuestao) {
                try {
                        return  find("questao_id = ?1", idQuestao).firstResult();
                }catch (WebApplicationException e){
                        throw new WebApplicationException(e.getMessage(), e.getResponse());
                }
        }
}
