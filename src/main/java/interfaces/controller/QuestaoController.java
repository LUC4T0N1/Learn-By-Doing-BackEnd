package interfaces.controller;

import domain.Alternativa;
import domain.Questao;
import infrastructure.dto.AlternativaDto;
import infrastructure.dto.ProvaDto;
import infrastructure.dto.QuestaoDto;
import infrastructure.repository.AlternativaRepository;
import infrastructure.repository.QuestaoRepository;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
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

    @POST
    @Transactional
    public void cadastrarQuestao(QuestaoDto dto) {
        List<Alternativa> alternativas = new ArrayList<>();
        for(AlternativaDto alternativaDto : dto.alternativas){
            alternativas.add(Alternativa.instanciar(alternativaDto));
        }
        alternativas = alternativaRepository.cadastrarAlternativas(alternativas);

        questaoRepository.cadastrarQuestao(dto.paraDominio(dto, alternativas));
    }
}
