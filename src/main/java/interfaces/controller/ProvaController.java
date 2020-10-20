package interfaces.controller;

import domain.Questao;
import infrastructure.dto.ProvaDto;
import infrastructure.repository.ProvaRepository;
import infrastructure.repository.QuestaoRepository;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
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

    @POST
    @Transactional
    public void cadastrarProva(ProvaDto dto) {
        List<Questao> questoes = questaoRepository.buscarPorIds(dto.idsQuestoes);
        provaRepository.cadastrarProva(dto.paraDominio(dto, questoes));
    }
}
