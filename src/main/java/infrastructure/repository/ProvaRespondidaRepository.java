package infrastructure.repository;

import domain.Prova;
import domain.ProvaRespondida;
import domain.Questao;
import domain.QuestaoRespondida;
import infrastructure.dto.*;
import io.quarkus.hibernate.orm.panache.PanacheRepository;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class ProvaRespondidaRepository implements PanacheRepository<ProvaRespondida> {
    @Inject ProvaRepository provaRepository;
    @Inject QuestaoRespondidaRepository questaoRespondidaRepository;

    @Transactional
    public void realizarProva(RealizarProvaDto dto, String usuario) {
        try {
            Prova prova = provaRepository.buscarPorId(dto.idProva);
            List<QuestaoRespondida> questoesRespondidas = new ArrayList<>();
            for(QuestaoRespondidaDto questaoRespondidaDto : dto.questoesRespondidasDto){
                questoesRespondidas.add(QuestaoRespondida.instanciar(questaoRespondidaDto));
            }
            questaoRespondidaRepository.cadastrarQuestoesRespondidas(questoesRespondidas);
            ProvaRespondida provaRespondida = ProvaRespondida.instanciar(dto, prova, usuario);
            this.incrementaPopularidade(dto.idProva, usuario, prova);
            persist(provaRespondida);
            provaRespondida.setQuestoesRespondidas(questoesRespondidas);
            for(QuestaoRespondida questaoRespondida : questoesRespondidas){
               questaoRespondida.setProvaRespondida(provaRespondida);
            }
        } catch (Exception e) {
            throw new Error(e);
                    //WebApplicationException(e.getMessage(), e.getResponse());
        }
    }

    public void incrementaPopularidade(Long id, String usuario, Prova prova){
        ProvaRespondida provaRespondida = find("usuario = ?1 AND prova_id = ?2 ", usuario, id).firstResult();
        if(provaRespondida == null){
            prova.setRealizacoes(prova.getRealizacoes() + 1L);
        }
    }

    public RealizarProvaDto buscarProvaRespondidaInteira(Long id) {
        try {
            ProvaRespondida provaRespondida = findById(id);
            if (provaRespondida == null) throw new WebApplicationException("Prova não encontrada", Response.Status.NOT_FOUND);
            RealizarProvaDto provaDto = RealizarProvaDto.instanciar(provaRespondida);
            provaDto.setProvaDto(provaRepository.buscarProvaInteira(provaRespondida.getProva().getId()));
            return provaDto;
        } catch (WebApplicationException e) {
            throw new WebApplicationException(e.getMessage(), e.getResponse());
        }
    }

    public void corrigirQuestoesMultiplaEscolha(Long id) {
        try {
            ProvaRespondida provaRespondida = findById(id);
            if (provaRespondida == null) throw new WebApplicationException("Prova não encontrada", Response.Status.NOT_FOUND);
            BigDecimal notaParcial = new BigDecimal(0);
            int questoes = 0;
            for(QuestaoRespondida questaoRespondida : provaRespondida.getQuestoesRespondidas()){
                if(questaoRespondida.getQuestao().isMultipaEscolha()) {
                    questoes++;
                    if (!questaoRespondida.getRespostaAluno().equals(questaoRespondida.getQuestao().getRespostaCorreta())) {
                        questaoRespondida.setNotaAluno(new BigDecimal(0));
                    } else {
                        System.out.println("valor questao:" + questaoRespondida.getQuestao().getValor() );
                        questaoRespondida.setNotaAluno(questaoRespondida.getQuestao().getValor());
                        notaParcial = notaParcial.add(questaoRespondida.getQuestao().getValor());
                    }
                }
            }
            provaRespondida.setQuestoesCorrigidas(questoes);
            provaRespondida.setNotaAluno(notaParcial);
        } catch (WebApplicationException e) {
            throw new WebApplicationException(e.getMessage(), e.getResponse());
        }
    }

    public void corrigirQuestoesDissertativas(CorrigirQuestoesDissertativasDto dto) {
        try {
            ProvaRespondida provaRespondida = findById(dto.idProvaRealizada);
            if (provaRespondida == null) throw new WebApplicationException("Prova não encontrada", Response.Status.NOT_FOUND);
            BigDecimal notaParcial = new BigDecimal(0);
            int questoes = 0;
            for(QuestoesDissertativasDto questaoDissertativa : dto.questoes) {
                QuestaoRespondida questaoRespondida = provaRespondida.getQuestoesRespondidas().stream().filter(q -> q.getId().equals(questaoDissertativa.idQuestaoResolivda)).findFirst().get();
                questaoRespondida.setNotaAluno(questaoDissertativa.notaQuestao);
                questaoRespondida.setComentarioProfessor(questaoDissertativa.comentarioProfessor);
                notaParcial = notaParcial.add(questaoDissertativa.notaQuestao);
                questoes = questoes + 1;
            }
            System.out.println("questoes: "+ questoes);
            System.out.println("nota: "+ notaParcial);
            provaRespondida.setQuestoesCorrigidas(provaRespondida.getQuestoesCorrigidas() + questoes);
            provaRespondida.setNotaAluno(provaRespondida.getNotaAluno().add(notaParcial));
        } catch (WebApplicationException e) {
            throw new WebApplicationException(e.getMessage(), e.getResponse());
        }
    }


}

