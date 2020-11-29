package infraestrutura.repository;

import dominio.Conteudo;
import dominio.Prova;
import infraestrutura.dto.BuscaPaginadaDto;
import infraestrutura.dto.ConteudoDto;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.transaction.Transactional;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import java.util.List;
import java.util.stream.Collectors;


@ApplicationScoped
public class ConteudoRepository implements PanacheRepository<Conteudo> {
    @Inject
    EntityManager em;
    @Inject ProvaRepository provaRepository;
    public static final int TAMANHO_PAGINA = 10;

    @Transactional
    public void cadastrarConteudo(ConteudoDto conteudoDto, String usuario){
        try {
            Conteudo conteudo = Conteudo.instanciar(conteudoDto.nome, 0L, usuario);
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



    @Transactional
    public void cadastrarProva(ConteudoDto dto){
        try {
            Conteudo conteudo = this.buscarPorID(dto.idConteudo);
            Prova prova = provaRepository.buscarPorId(dto.idProva);
            if(conteudo.getProvas().contains(prova)) throw new WebApplicationException("Essa prova ja pertence a esse conteúdo!", Response.Status.BAD_REQUEST);
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
            if(prova == null) throw new WebApplicationException("Essa prova não existe ou não pertence a você!", Response.Status.BAD_REQUEST);
            if(conteudo.getProvas().contains(prova)) {
                conteudo.getProvas().remove(prova);
                Query query2 = this.em.createNativeQuery("DELETE from pgc1.conteudo_prova where conteudo_prova.conteudo_id = :idConteudo AND conteudo_prova.provas_id = :idProva");
                query2.setParameter("idConteudo", dto.idConteudo);
                query2.setParameter("idProva", dto.idProva);
                conteudo.setNumeroProvas(conteudo.getNumeroProvas() - 1L);
            }else throw new WebApplicationException("Esse conteúdo não contém essa prova!", Response.Status.BAD_REQUEST);
        } catch (Exception e) {
            throw new Error(e);
        }

    }

    public BuscaPaginadaDto buscarPorOrdemAlfabetica(Integer pagina) {
        try {
                List<Conteudo> conteudos =  find("", Sort.by("nome"))
                        .page(Page.of(pagina, TAMANHO_PAGINA))
                        .list();
                List<ConteudoDto> conteudosDto = conteudos.stream()
                        .map(
                                ConteudoDto::instanciar)
                                            .collect(Collectors.toList());

                return BuscaPaginadaDto.instanciar(conteudosDto, null);

        } catch (Exception e) {
            throw new WebApplicationException(
                    "Erro ao obter produtos", Response.Status.INTERNAL_SERVER_ERROR);
        }
    }
    public BuscaPaginadaDto buscarPorNumeroDeProvas(Integer pagina) {
        try {
            List<Conteudo> conteudos =  find("", Sort.by("numero_provas").descending())
                    .page(Page.of(pagina, TAMANHO_PAGINA))
                    .list();
            List<ConteudoDto> conteudosDto = conteudos.stream()
                    .map(
                            ConteudoDto::instanciar)
                    .collect(Collectors.toList());

            return BuscaPaginadaDto.instanciar(conteudosDto, null);

        } catch (Exception e) {
            throw new WebApplicationException(
                    "Erro ao obter produtos", Response.Status.INTERNAL_SERVER_ERROR);
        }
    }


}
