package interfaces.controller;

import domain.Questao;
import infrastructure.dto.ProvaDto;
import infrastructure.repository.ProvaRepository;
import infrastructure.repository.QuestaoRepository;
import interfaces.controller.resposta.RespostaAPI;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
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

    @POST
    @Transactional
    @Tag(name = "Prova", description = "Controllers de Prova")
    @Operation(summary = "Cadastra prova", description = "Cadastra uma nova prova")
    public Response cadastrarProva(ProvaDto dto) {
        return api.retornar(
                () -> {
                    List<Questao> questoes = questaoRepository.buscarPorIds(dto.idsQuestoes);
                    provaRepository.cadastrarProva(dto.paraDominio(dto, questoes));
                    return RespostaAPI.sucesso("Prova cadastrada com sucesso!");
                },dto);
    }
}
