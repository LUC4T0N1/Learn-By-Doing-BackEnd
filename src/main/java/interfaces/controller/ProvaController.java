package interfaces.controller;

import domain.Questao;
import infrastructure.dto.ProvaDto;
import infrastructure.dto.BuscarProvasDto;
import infrastructure.repository.ProvaRepository;
import infrastructure.repository.QuestaoRepository;
import interfaces.controller.resposta.RespostaAPI;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.resteasy.annotations.jaxrs.QueryParam;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@RequestScoped
@Path("api/prova")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ProvaController {
    @Inject
    ProvaRepository provaRepository;
    @Inject
    QuestaoRepository questaoRepository;
    @Inject
    RespostaAPI api;
    //@Inject
    //@Claim("usuario")
    String usuario = "usuario";

    @POST
    @Transactional
    @Tag(name = "Prova", description = "Controllers de Prova")
    @Operation(summary = "Cadastra prova", description = "Cadastra uma nova prova")
    public Response cadastrarProva(ProvaDto dto) {
        return api.retornar(
                () -> {
                    List<Questao> questoes = questaoRepository.buscarPorIds(dto.idsQuestoes);
                    provaRepository.cadastrarProva(dto.paraDominio(dto, questoes, usuario, dto.publica));
                    return RespostaAPI.sucesso("Prova cadastrada com sucesso!");
                },dto);
    }

    @GET
    @Path("/buscarPU")
    @Tag(name = "Conteúdo", description = "Controllers de Conteúdo")
    @Operation(summary = "Obter conteúdo por quantidade de provas", description = "Faz uma busca paginada dos conteúdos ordenados por quantidade de provas")
    @Transactional
    public Response buscarProvasPorUsuario(@QueryParam("pagina") Integer pagina) {
        return api.retornar(
                provaRepository.buscarPorUsuario(pagina, usuario)
        );
    }

    @GET
    @Path("/buscarPC")
    @Tag(name = "Conteúdo", description = "Controllers de Conteúdo")
    @Operation(summary = "Obter conteúdo por quantidade de provas", description = "Faz uma busca paginada dos conteúdos ordenados por quantidade de provas")
    @Transactional
    public Response buscarProvasPorConteudo(@QueryParam("pagina") Integer pagina, BuscarProvasDto dto) {
        return api.retornar(
                provaRepository.buscarPorOrdemAlfabetica(pagina, dto)
        );
    }

    @GET
    @Path("/buscarID")
    @Tag(name = "Conteúdo", description = "Controllers de Conteúdo")
    @Operation(summary = "Obter conteúdo por quantidade de provas", description = "Faz uma busca paginada dos conteúdos ordenados por quantidade de provas")
    @Transactional
    public Response buscarProvaPorId(@QueryParam("id") Long id) {
        return api.retornar(
                provaRepository.buscarProvaInteira(id)
        );
    }
}
