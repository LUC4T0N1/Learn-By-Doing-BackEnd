package infraestrutura.repository;

import dominio.Conteudo;
import dominio.Prova;
import infraestrutura.dto.BuscaPaginadaDto;
import infraestrutura.dto.ConteudoDto;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;
import org.jboss.resteasy.annotations.jaxrs.QueryParam;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.transaction.Transactional;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


@ApplicationScoped
public class ConteudoRepository implements PanacheRepository<Conteudo> {
    @Inject
    EntityManager em;

    @Inject ProvaRepository provaRepository;
    public static final int TAMANHO_PAGINA = 5;

    @Transactional
    public ConteudoDto cadastrarConteudo(ConteudoDto conteudoDto, String usuario){
        try {
            Conteudo antigo = find("nome = ?1", conteudoDto.nome).firstResult();
            if(antigo!=null) throw new WebApplicationException("Esse nome já existe!", Response.Status.CONFLICT);
            Conteudo conteudo = Conteudo.instanciar(conteudoDto.nome, usuario);
            persist(conteudo);
            return ConteudoDto.instanciar(conteudoDto.nome, conteudo.getId(), null);
        } catch (WebApplicationException e) {
            throw new WebApplicationException(e.getMessage(), e.getResponse());
        }
    }

    public void obterConteudosDeQuestao(ConteudoDto conteudoDto, String usuario){
        try {
            Conteudo conteudo = Conteudo.instanciar(conteudoDto.nome, usuario);
            persist(conteudo);
        } catch (WebApplicationException e) {
            throw new WebApplicationException(e.getMessage(), e.getResponse());
        }
    }

    public Conteudo buscarPorID(Long id){
        Conteudo conteudo = findById(id);
        if(conteudo == null){
            throw new WebApplicationException("Conteudo não encontrado!", Response.Status.NOT_FOUND);
        }
        return conteudo;
    }

    public List<Conteudo> buscarConteudos(List<Long> ids){
        List<Conteudo> conteudo = find("id IN ?1", ids).list();
        if(conteudo == null){
            throw new WebApplicationException("Conteudo não encontrado!", Response.Status.NOT_FOUND);
        }
        return conteudo;
    }




    @Transactional
    public void cadastrarProva(ConteudoDto dto){
        try {
            Conteudo conteudo = this.buscarPorID(dto.idConteudo);
            Prova prova = provaRepository.buscarPorId(dto.idProva);
            if(conteudo.getProvas().contains(prova))
                throw new WebApplicationException("Essa prova ja pertence a esse conteúdo!", Response.Status.BAD_REQUEST);
            conteudo.getProvas().add(prova);
            conteudo.setNumeroProvas(conteudo.getNumeroProvas() + 1L);
        } catch (WebApplicationException e) {
            throw new WebApplicationException(e.getMessage(), e.getResponse());
        }

    }

    public void removerProva(ConteudoDto dto, String usuario){
        try {
            Conteudo conteudo = this.buscarPorID(dto.idConteudo);
            Prova prova = provaRepository.buscarPorIdUsuario(dto.idProva, usuario);
            if(prova == null)
                throw new WebApplicationException("Essa prova não existe ou não pertence a você!", Response.Status.BAD_REQUEST);
            if(conteudo.getProvas().contains(prova)) {
                conteudo.getProvas().remove(prova);
                Query query2 = this.em.createNativeQuery("DELETE from pgc1.conteudo_prova " +
                        "where conteudo_prova.conteudo_id = :idConteudo AND conteudo_prova.provas_id = :idProva");
                query2.setParameter("idConteudo", dto.idConteudo);
                query2.setParameter("idProva", dto.idProva);
                conteudo.setNumeroProvas(conteudo.getNumeroProvas() - 1L);
            }else throw new WebApplicationException("Esse conteúdo não contém essa prova!", Response.Status.BAD_REQUEST);
        } catch (Exception e) {
            throw new WebApplicationException(e);
        }

    }

    public BuscaPaginadaDto filtroSimples(String nome, Integer pagina,  int ordenacao, int ordem) {
        try {
            List<Conteudo> conteudos;
            String tipoOrdenacao = (ordenacao == 1 ? "numero_provas" : "nome");
            if(ordem == 0){
                if(!Objects.equals(nome, "null")) {
                    conteudos = find("nome like ?1", Sort.by(tipoOrdenacao).ascending(), nome + "%")
                            .page(Page.of(pagina, TAMANHO_PAGINA))
                            .list();
                }else{
                    conteudos = find("", Sort.by(tipoOrdenacao).ascending())
                            .page(Page.of(pagina, TAMANHO_PAGINA))
                            .list();
                }
            }else{
                if(!Objects.equals(nome, "null")) {
                    conteudos = find("nome like ?1", Sort.by(tipoOrdenacao).descending(), nome + "%")
                            .page(Page.of(pagina, TAMANHO_PAGINA))
                            .list();
                }else{
                    conteudos = find("", Sort.by(tipoOrdenacao).descending())
                            .page(Page.of(pagina, TAMANHO_PAGINA))
                            .list();
                }
            }
            Query query = this.em.createNativeQuery("SELECT COUNT(id)  FROM conteudo");
            Long total = Long.valueOf(String.valueOf(query.getSingleResult()));
            List<ConteudoDto> conteudosDto = conteudos.stream()
                    .map(
                            ConteudoDto::instanciarPorEntidade)
                    .collect(Collectors.toList());
            for(ConteudoDto id : conteudosDto){
                System.out.println("id: " + id.idConteudo);
            }
            return BuscaPaginadaDto.instanciar(conteudosDto, null,null,null,null, total);

        } catch (Exception e) {
            throw new WebApplicationException(e);
        }
    }

    public Integer buscarConteudosPorUsuario(String usuario){
        try {
            return find("usuario = ?1", usuario).list().size();
        }catch (Exception e){
            return 0;
        }
    }

}
