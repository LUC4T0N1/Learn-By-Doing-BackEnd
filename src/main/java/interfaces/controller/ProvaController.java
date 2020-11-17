package interfaces.controller;

import domain.Questao;
import infrastructure.dto.CorrigirQuestoesDissertativasDto;
import infrastructure.dto.ProvaDto;
import infrastructure.dto.BuscarProvasDto;
import infrastructure.dto.RealizarProvaDto;
import infrastructure.repository.ProvaRepository;
import infrastructure.repository.ProvaRespondidaRepository;
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
    ProvaRespondidaRepository provaRespondidaRepository;
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

    @GET
    @Path("/buscarRID")
    @Tag(name = "Conteúdo", description = "Controllers de Conteúdo")
    @Operation(summary = "Obter conteúdo por quantidade de provas", description = "Faz uma busca paginada dos conteúdos ordenados por quantidade de provas")
    @Transactional
    public Response buscarProvaRespondidaPorId(@QueryParam("id") Long id) {
        return api.retornar(
                provaRespondidaRepository.buscarProvaRespondidaInteira(id)
        );
    }

    @POST
    @Path("/realizar")
    @Tag(name = "Conteúdo", description = "Controllers de Conteúdo")
    @Operation(summary = "Realizar uma prova", description = "salva as respostas que um usuário deu a uma prova")
    @Transactional
    public Response realizarProva(RealizarProvaDto dto) {
        return api.retornar(
                () -> {
                    provaRespondidaRepository.realizarProva(dto, "a");
                    return RespostaAPI.sucesso("Prova realizada com sucesso!");
                },dto);
    }

    @GET
    @Path("/corrigirME")
    @Tag(name = "Conteúdo", description = "Controllers de Conteúdo")
    @Operation(summary = "Corrigir uma prova", description = "Corrige automaticamente as questões de multipla escolha de uma prova")
    @Transactional
    public Response corrigirQuestoesMultiplaEscolha(@QueryParam("id") Long id) {
        return api.executar(
                () -> {
                    provaRespondidaRepository.corrigirQuestoesMultiplaEscolha(id);
                    return RespostaAPI.sucesso("Prova corrigida com sucesso!");
                });
    }

    @PUT
    @Path("/corrigirD")
    @Tag(name = "Conteúdo", description = "Controllers de Conteúdo")
    @Operation(summary = "Corrigir uma prova", description = "Corrige automaticamente as questões de multipla escolha de uma prova")
    @Transactional
    public Response corrigirQuestoesDissertativas(CorrigirQuestoesDissertativasDto dto) {
        return api.executar(
                () -> {
                    provaRespondidaRepository.corrigirQuestoesDissertativas(dto);
                    return RespostaAPI.sucesso("Prova corrigida com sucesso!");
                });
    }

}
