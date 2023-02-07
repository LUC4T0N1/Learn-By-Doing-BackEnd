package interfaces.controller;

import aplicacao.service.ProvaService;
import infraestrutura.dto.CorrigirQuestoesDissertativasDto;
import infraestrutura.dto.ProvaDto;
import infraestrutura.dto.ProvaRespondidaDto;
import infraestrutura.repository.ProvaRepository;
import infraestrutura.repository.ProvaRespondidaRepository;
import interfaces.controller.resposta.RespostaAPI;
import io.quarkus.logging.Log;
import org.eclipse.microprofile.jwt.Claim;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.resteasy.annotations.jaxrs.QueryParam;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;

@RequestScoped
@Path("api/prova")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ProvaController {

    private final String divisao = "--------------------------------------------------------------NOVA CHAMADA----------------------------------------------------------------------";

    @Inject ProvaService provaService;
    @Inject
    ProvaRespondidaRepository provaRespondidaRepository;
    @Inject
    RespostaAPI api;
    @Inject ProvaRepository provaRepository;

    @Inject
    @Claim("usuario")
    String usuario;

    @POST
    @Transactional
    @Tag(name = "Prova", description = "Controllers de Prova")
    @Operation(summary = "Cadastra prova", description = "Cadastra uma nova prova")
    public Response cadastrarProva(ProvaDto dto) {
        return api.retornar(
                () -> {
                    provaService.cadastrarNovaProva(dto, usuario);
                    return RespostaAPI.sucesso("Prova cadastrada com sucesso!");
                },dto);
    }
    @PUT
    @Path("alterar")
    @Transactional
    @Tag(name = "Prova", description = "Controllers de Prova")
    @Operation(summary = "Alterar prova",
            description = "Altera dados de uma prova já cadastrada")
    public Response alterarProva(ProvaDto dto) {
        return api.retornar(
                () -> {
                    provaRepository.alterarProva(dto, usuario);
                    return RespostaAPI.sucesso("Prova alterada com sucesso!");
                },dto);
    }



    @GET
    @Path("/buscarPU")
    @Tag(name = "Conteúdo", description = "Controllers de Conteúdo")
    @Operation(summary = "Obter conteúdo por quantidade de provas",
            description = "Faz uma busca paginada dos conteúdos ordenados por quantidade de provas")
    @Transactional
    public Response buscarProvasPorUsuario(
            @QueryParam("pagina") Integer pagina,
            @QueryParam("nome") String nome,
            @QueryParam("ordenacao") Integer ordenacao,
            @QueryParam("ordem") Integer ordem
            ) {
        System.out.println(divisao + "\n[Buscar Provas Por Usuario] iniciando filtro simples -> pagina: "
                + pagina + " ordenacao: "+ ordenacao  + " nome: " + nome + " usuario: " + usuario + " ordem: " + ordem);
        return api.retornar(
                provaRepository.buscarPorUsuario(pagina, usuario, nome, ordenacao, ordem)
        );
    }

    @GET
    @Path("/buscarPorConteudo")
    @Tag(name = "Conteúdo", description = "Controllers de Conteúdo")
    @Operation(summary = "Obter conteúdo por quantidade de provas",
            description = "Faz uma busca paginada dos conteúdos ordenados por quantidade de provas")
    @Transactional
    public Response buscarProvasPorConteudo(@QueryParam("pagina") Integer pagina,
                                            @QueryParam("nome") String nome,
                                            @QueryParam("ordenacao") Integer ordenacao,
                                            @QueryParam("idConteudo") Long idConteudo,
                                            @QueryParam("ordem") Integer ordem) {
        System.out.println(divisao + "\n[Buscar Provas Por Conteudo] iniciando filtro simples -> pagina: "
                + pagina + " ordenacao: "+ ordenacao  + " nome: " + nome + " idConteudo: " + idConteudo + " ordem: " + ordem);
        return api.retornar(
                provaRepository.buscarProvasPorConteudo(pagina, nome, ordenacao, idConteudo, ordem)
        );
    }

    @GET
    @Path("/buscarFazerID")
    @Tag(name = "Conteúdo", description = "Controllers de Conteúdo")
    @Operation(summary = "Obter conteúdo por quantidade de provas",

            description = "Faz uma busca paginada dos conteúdos ordenados por quantidade de provas")
    public Response buscarProvaFazerPorId(@QueryParam("id") Long id) {
        return api.retornar(
                provaRepository.buscarProvaInteiraFazer(id, new ArrayList<>(), false, usuario)
        );
    }

    @GET
    @Path("/buscarID")
    @Tag(name = "Conteúdo", description = "Controllers de Conteúdo")
    @Operation(summary = "Obter conteúdo por quantidade de provas",

            description = "Faz uma busca paginada dos conteúdos ordenados por quantidade de provas")
    public Response buscarProvaPorId(@QueryParam("id") Long id) {
        return api.retornar(
                provaRepository.buscarProvaInteira(id, new ArrayList<>(), true)
        );
    }

    @GET
    @Path("/buscarPrivadaID")
    public Response buscarProvaPrivadaPorId(@QueryParam("id") String id) {
        System.out.println("chegiu id: "+ id);
        return api.retornar(
                provaRepository.buscarProvaPrivadaPorId(id)
        );
    }

    @GET
    @Path("/buscarRID")
    @Tag(name = "Conteúdo", description = "Controllers de Conteúdo")
    @Operation(summary = "Obter conteúdo por quantidade de provas",

            description = "Faz uma busca paginada dos conteúdos ordenados por quantidade de provas")
    @Transactional
    public Response buscarProvaRespondidaPorId(@QueryParam("id") Long id) {
        System.out.println(divisao + "\n[Buscar Provas Respondidas Por Prova Criada] iniciando filtro simples ->  id: " + id + " usuario: " + usuario);
        return api.retornar(
                provaRespondidaRepository.buscarResolucoesProvaRespondidaInteira(id)
        );
    }

    @GET
    @Path("/buscarResolucoes")
    @Tag(name = "Conteúdo", description = "Controllers de Conteúdo")
    @Operation(summary = "Obter conteúdo por quantidade de provas",

            description = "Faz uma busca paginada dos conteúdos ordenados por quantidade de provas")
    @Transactional
    public Response buscarProvasRespondidasPorProvaCriada(@QueryParam("id") Long id, @QueryParam("pagina") Integer pagina,
                                                          @QueryParam("nome") String nome,
                                                          @QueryParam("ordenacao") Integer ordenacao,
                                                          @QueryParam("ordem") Integer ordem) {
        System.out.println(divisao + "\n[Buscar Provas Respondidas Por Prova Criada] iniciando filtro simples -> pagina: "
                + pagina + " id: " + id + " usuario: " + usuario);
        return api.retornar(
                provaRespondidaRepository.buscarProvasRespondidasPorProvaCriada(id, pagina, nome, ordenacao, ordem)
        );
    }

    @GET
    @Path("/buscarResolucoesPorUsuario")
    @Tag(name = "Conteúdo", description = "Controllers de Conteúdo")
    @Operation(summary = "Obter conteúdo por quantidade de provas",

            description = "Faz uma busca paginada dos conteúdos ordenados por quantidade de provas")
    @Transactional
    public Response buscarProvasRespondidasPorUsuario(@QueryParam("pagina") Integer pagina,
                                                      @QueryParam("nome") String nome,
                                                      @QueryParam("ordenacao") Integer ordenacao,
                                                      @QueryParam("ordem") Integer ordem) {
        System.out.println(divisao + "\n[Buscar Provas Respondidas Por Usuario] iniciando filtro simples -> pagina: "
                + pagina + " ordenacao: "+ ordenacao  + " nome: " + nome + " ordem: " + ordem + " usuario: " + usuario);
        return api.retornar(
                provaRespondidaRepository.buscarProvasRespondidasPorUsuario(usuario, pagina, nome, ordenacao, ordem)
        );
    }

    @POST
    @Path("/realizar")
    @Tag(name = "Conteúdo", description = "Controllers de Conteúdo")
    @Operation(summary = "Realizar uma prova",
            description = "salva as respostas que um usuário deu a uma prova")
    @Transactional
    public Response realizarProva(ProvaRespondidaDto dto) {
        return api.retornar(
                () -> {
                    Log.info(divisao + "\n prova recebida: " + dto.toString());
                    provaRespondidaRepository.realizarProva(dto, usuario);
                    return RespostaAPI.sucesso("Prova realizada com sucesso!");
                },dto);
    }

    @PUT
    @Path("/corrigirDissertativa")
    @Tag(name = "Conteúdo", description = "Controllers de Conteúdo")
    @Operation(summary = "Corrigir uma prova",
            description = "Corrige automaticamente as questões de multipla escolha de uma prova")
    @Transactional
    public Response corrigirQuestoesDissertativas(CorrigirQuestoesDissertativasDto dto) {
        return api.executar(
                () -> {
                    provaRespondidaRepository.corrigirQuestoesDissertativas(dto);
                    return RespostaAPI.sucesso("Prova corrigida com sucesso!");
                });
    }

    @GET
    @Path("/obterIdProvaPrivada")
    public Response obterIdProvaPrivada(@QueryParam("idSecreto") String idSecreto) {
        return api.retornar(provaRepository.obterIdProvaPrivada(idSecreto));
    }

    @GET
    @Path("/iniciarProva")
    public Response iniciarProvaPrivada(@QueryParam("id") Long id) {
        return api.retornar(provaRespondidaRepository.iniciarProvaPrivada(id, usuario));
    }

    @GET
    @Path("/validarResolucoes")
    public void validarResolucoes(@QueryParam("id") Long id) {
        provaRespondidaRepository.validarResolucoes(id, usuario);
    }

    @GET
    @Path("/validarDatas")
    public void validarDatas(@QueryParam("id") Long id) {
        provaRespondidaRepository.validarDatas(id);
    }

}
