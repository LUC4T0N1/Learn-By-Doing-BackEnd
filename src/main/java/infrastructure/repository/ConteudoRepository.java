package infrastructure.repository;

import domain.Conteudo;
import domain.Prova;
import infrastructure.dto.BuscaPaginadaDto;
import infrastructure.dto.ConteudoDto;
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
    public void cadastrarConteudo(Conteudo conteudo){
        try {
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
