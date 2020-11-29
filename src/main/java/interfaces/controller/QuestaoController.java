package interfaces.controller;

import dominio.Alternativa;
import infraestrutura.dto.AlternativaDto;
import infraestrutura.dto.QuestaoDto;
import infraestrutura.repository.AlternativaRepository;
import infraestrutura.repository.QuestaoRepository;
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
import java.util.ArrayList;
import java.util.List;

@RequestScoped
@Path("api/questao")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class QuestaoController {

    @Inject
    AlternativaRepository alternativaRepository;
    @Inject
    QuestaoRepository questaoRepository;
    @Inject
    RespostaAPI api;

    //@Inject
    //@Claim("usuario")
    String usuario = "usuario";

    @POST
    @Transactional
    @Tag(name = "Questão", description = "Controllers de Questão")
    @Operation(summary = "Cadastra uma nova Questão", description = "Cadastra uma novva questão")
    public Response cadastrarQuestao(QuestaoDto dto) {
        return api.retornar(
                () -> {
                    List<Alternativa> alternativas = new ArrayList<>();
                    for (AlternativaDto alternativaDto : dto.alternativas) {
                        alternativas.add(Alternativa.instanciar(alternativaDto, usuario));
                    }
                    alternativas = alternativaRepository.cadastrarAlternativas(alternativas);
                    questaoRepository.cadastrarQuestao(dto.paraDominio(dto, alternativas, usuario));
                    return RespostaAPI.sucesso("Questão cadastrada com sucesso!");
                }, dto);
    }
}
