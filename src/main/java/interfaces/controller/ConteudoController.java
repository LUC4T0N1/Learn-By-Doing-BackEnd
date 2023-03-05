package interfaces.controller;

import infraestrutura.dto.ConteudoDto;
import infraestrutura.repository.ConteudoRepository;
import interfaces.controller.resposta.RespostaAPI;
import io.quarkus.logging.Log;
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
@Path("api/conteudo")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ConteudoController {

  private final String divisao =
      "--------------------------------------------------------------NOVA CHAMADA----------------------------------------------------------------------";

  @Inject ConteudoRepository conteudoRepository;
  @Inject RespostaAPI api;

  @Inject
  @Claim("usuario")
  String usuario;

  @POST
  @Transactional
  @Tag(name = "Conteúdo", description = "Controllers de Conteúdo")
  @Operation(summary = "Cadastrar Conteúdo", description = "Cadastra um novo conteúdo")
  public Response cadastrarConteudo(ConteudoDto dto) {
    return api.retornar(
        () -> {
          Log.info(divisao + "\n [Cadastrar Conteudo] cadastrando novo conteudo " + dto.nome);
          return api.retornar(conteudoRepository.cadastrarConteudo(dto, usuario));
        },
        dto);
  }

  @GET
  @Path("/filtro")
  @Tag(name = "Conteúdo", description = "Controllers de Conteúdo")
  @Operation(
      summary = "Obter conteúdos por ordem alfabética",
      description = "Faz uma busca paginada dos conteúdos por um filtro simples")
  public Response buscarConteudos(
      @QueryParam("nome") String nome,
      @QueryParam("pagina") Integer pagina,
      @QueryParam("ordenacao") int ordenacao,
      @QueryParam("ordem") int ordem) {
    Log.info(divisao + "\n[Buscar Conteudos] iniciando busca de conteudos..");
    return api.retornar(conteudoRepository.filtroSimplesConteudos(nome, pagina, ordenacao, ordem));
  }
}
