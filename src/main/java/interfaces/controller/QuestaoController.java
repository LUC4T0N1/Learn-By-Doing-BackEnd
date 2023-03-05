package interfaces.controller;

import aplicacao.service.QuestaoService;
import infraestrutura.dto.QuestaoDto;
import interfaces.controller.resposta.RespostaAPI;
import io.quarkus.logging.Log;
import java.util.List;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.eclipse.microprofile.jwt.Claim;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.resteasy.annotations.jaxrs.QueryParam;

@RequestScoped
@Path("api/questao")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class QuestaoController {

  @Inject QuestaoService questaoService;

  @Inject RespostaAPI api;

  @Inject
  @Claim("usuario")
  String usuario;

  private final String divisao =
      "--------------------------------------------------------------NOVA CHAMADA----------------------------------------------------------------------";

  @POST
  @Transactional
  @Tag(name = "Questão", description = "Controllers de Questão")
  @Operation(summary = "Cadastra uma nova Questão", description = "Cadastra uma novva questão")
  public Response cadastrarQuestao(QuestaoDto dto) {
    Log.info(divisao + "\n Cadastrando nova questao: " + dto.toString());
    return api.retornar(
        () -> {
          return api.retornar(questaoService.cadastrarNovaQuestao(dto, usuario));
        },
        dto);
  }

  @PUT
  @Transactional
  @Tag(name = "Questão", description = "Controllers de Questão")
  @Operation(summary = "Alterar uma nova Questão", description = "Cadastra uma novva questão")
  public Response editarQuestao(QuestaoDto dto) {
    return api.retornar(
        () -> {
          Log.info(divisao + "\n Editando questao: " + dto.toString());
          return api.retornar(questaoService.editarQuestao(dto, usuario));
        },
        dto);
  }

  @GET
  @Path("/filtrar")
  @Transactional
  @Tag(name = "Questão", description = "Controllers de Questão")
  @Operation(summary = "Cadastra uma nova Questão", description = "Cadastra uma nova questão")
  public Response filtrarQuestoes(
      @QueryParam("enunciado") String enunciado,
      @QueryParam("pagina") Integer pagina,
      @QueryParam("ordenacao") Integer ordenacao,
      @QueryParam("ordem") Integer ordem,
      @QueryParam("multiplaEscolha") Integer multiplaEscolha,
      @QueryParam("publica") Boolean publica,
      @QueryParam("conteudos") List<Integer> conteudos,
      @QueryParam("questoes") List<Long> idsQuestoes) {

    Log.info(divisao + "\n Iniciando Busca de Questões");
    return api.retornar(
        questaoService.filtrarQuestoes(
            pagina, enunciado, ordenacao, ordem, conteudos, multiplaEscolha, publica, idsQuestoes));
  }
}
