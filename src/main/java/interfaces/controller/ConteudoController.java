package interfaces.controller;

import infrastructure.dto.ConteudoDto;
import infrastructure.repository.ConteudoRepository;
import interfaces.controller.resposta.RespostaAPI;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

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

    @Inject
    ConteudoRepository conteudoRepository;
    @Inject
    RespostaAPI api;

    @POST
    @Transactional
    @Tag(name = "Conteúdo", description = "Controllers de Conteúdo")
    @Operation(summary = "Cadastrar Conteúdo", description = "Cadastra um novo conteúdo")
    public Response cadastrarConteudo(ConteudoDto dto) {
        return api.retornar(
                () -> {
                    conteudoRepository.cadastrarConteudo(dto.paraDominio(dto.nome));
                    return RespostaAPI.sucesso("Conteúdo cadastrado com sucesso!");
                },dto);
    }

    @POST
    @Path("/prova")
    @Tag(name = "Conteúdo", description = "Controllers de Conteúdo")
    @Operation(summary = "Salva prova", description = "Salva uma prova em um conteúdo")
    @Transactional
    public Response adicionarProvaConteudo(ConteudoDto dto) {
        return api.retornar(
                () -> {
                    conteudoRepository.cadastrarProva(dto);
                    return RespostaAPI.sucesso("Prova adicionada ao coneúdo com sucesso!");
                },dto);
    }

    }
