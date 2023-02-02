package interfaces.controller;

import infraestrutura.dto.ConteudoDto;
import infraestrutura.repository.ConteudoRepository;
import interfaces.controller.resposta.RespostaAPI;
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

@RequestScoped
@Path("api/conteudo")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ConteudoController {

    private final String divisao = "--------------------------------------------------------------NOVA CHAMADA----------------------------------------------------------------------";



    @Inject
    ConteudoRepository conteudoRepository;
    @Inject
    RespostaAPI api;

    @Inject
    @Claim("usuario")
    String usuario;

    @POST
    @Transactional
    @Tag(name = "Conteúdo", description = "Controllers de Conteúdo")
    @Operation(summary = "Cadastrar Conteúdo",
            description = "Cadastra um novo conteúdo")
    public Response cadastrarConteudo(ConteudoDto dto) {
        return api.retornar(
                () -> {
                    System.out.println(divisao + "\n [Cadastrar Conteudo] cadastrando novo conteudo " + dto.nome);
                    return api.retornar(conteudoRepository.cadastrarConteudo(dto, usuario));
                },dto);
    }

    @POST
    @Path("/prova")
    @Tag(name = "Conteúdo", description = "Controllers de Conteúdo")
    @Operation(summary = "Salva prova",
            description = "Salva uma prova em um conteúdo")
    @Transactional
    public Response adicionarProvaAoConteudo(ConteudoDto dto) {
        return api.retornar(
                () -> {
                    conteudoRepository.cadastrarProva(dto);
                    return RespostaAPI.sucesso("Prova adicionada ao coneúdo com sucesso!");
                },dto);
    }

    @DELETE
    @Path("/removerProva")
    @Tag(name = "Conteúdo", description = "Controllers de Conteúdo")
    @Operation(summary = "Salva prova",
            description = "Salva uma prova em um conteúdo")
    @Transactional
    public Response removerProvaDoConteudo(ConteudoDto dto) {
        return api.retornar(
                () -> {
                    conteudoRepository.removerProva(dto, "usuario");
                    return RespostaAPI.sucesso("Prova removida do coneúdo com sucesso!");
                },dto);
    }

    @GET
    @Path("/filtro")
    @Tag(name = "Conteúdo", description = "Controllers de Conteúdo")
    @Operation(summary = "Obter conteúdos por ordem alfabética",
            description = "Faz uma busca paginada dos conteúdos por um filtro simples")
    public Response buscarConteudos(@QueryParam("nome") String nome, @QueryParam("pagina") Integer pagina, @QueryParam("ordenacao") int ordenacao, @QueryParam("ordem") int ordem) {
        System.out.println(divisao + "\n[Buscar Conteudos] iniciando filtreo simples -> pagina: " + pagina + " ordenacao: "+ ordenacao  + " nome: " + nome + " ordem: " + ordem);
        return api.retornar(
                conteudoRepository.filtroSimples(nome, pagina, ordenacao, ordem)
        );
    }


}
