package infrastructure.repository;

import domain.Conteudo;
import domain.Prova;
import infrastructure.dto.ConteudoDto;
import io.quarkus.hibernate.orm.panache.PanacheRepository;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.transaction.Transactional;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Arrays;


@ApplicationScoped
public class ConteudoRepository implements PanacheRepository<Conteudo> {
    @Inject
    EntityManager em;
    @Inject ProvaRepository provaRepository;

    @Transactional
    public void cadastrarConteudo(Conteudo conteudo){
        try {
            persist(conteudo);
        } catch (WebApplicationException e) {
            throw new WebApplicationException(e.getMessage(), e.getResponse());
        }
    }

    @Transactional
    public void cadastrarProva(ConteudoDto dto){
        try {
            Conteudo conteudo = findById(dto.idConteudo);
            if(conteudo == null){
                throw new WebApplicationException("Conteudo não encontrado!", Response.Status.NOT_FOUND);
            }
            Prova prova = provaRepository.buscarPorId(dto.idProva);
            conteudo.setProvas(new ArrayList<>(Arrays.asList(prova)));

        } catch (WebApplicationException e) {
            throw new WebApplicationException(e.getMessage(), e.getResponse());
        }

    }

}
