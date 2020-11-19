package infrastructure.repository;

import application.utlis.DataUtils;
import domain.Prova;
import domain.ProvaRespondida;
import domain.QuestaoRespondida;
import infrastructure.dto.*;
import io.quarkus.hibernate.orm.panache.PanacheRepository;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@ApplicationScoped
public class ProvaRespondidaRepository implements PanacheRepository<ProvaRespondida> {
    @Inject ProvaRepository provaRepository;
    @Inject QuestaoRespondidaRepository questaoRespondidaRepository;

    @Transactional
    public void realizarProva(ProvaRespondidaDto dto, String usuario) {
        try {
            Prova prova = provaRepository.buscarPorId(dto.idProva);
            this.verificaResolucoes(dto.idProva, usuario, prova);
            this.verificarDatas(prova);
            List<QuestaoRespondida> questoesRespondidas = new ArrayList<>();
            for (QuestaoRespondidaDto questaoRespondidaDto : dto.questoesRespondidasDto) {
                questoesRespondidas.add(QuestaoRespondida.instanciar(questaoRespondidaDto));
            }
            questaoRespondidaRepository.cadastrarQuestoesRespondidas(questoesRespondidas);
            ProvaRespondida provaRespondida = ProvaRespondida.instanciar(dto, prova, usuario);
            this.incrementaPopularidade(dto.idProva, usuario, prova);
            persist(provaRespondida);
            provaRespondida.setQuestoesRespondidas(questoesRespondidas);
            for (QuestaoRespondida questaoRespondida : questoesRespondidas) {
                questaoRespondida.setProvaRespondida(provaRespondida);
            }
            List<ProvaRespondida> provasRespondidas = find("usuario = ?1 AND prova_id = ?2 ", usuario, dto.idProva).list();
            for(ProvaRespondida provaRespondida1 : provasRespondidas){
                provaRespondida1.setResolucoes(provasRespondidas.size());
            }
        } catch (Exception e) {
            throw new Error(e);
                    //WebApplicationException(e.getMessage(), e.getResponse());
        }
    }

    public void verificarDatas(Prova prova){
        Date hoje = DataUtils.dateParaDateFormatada(new Date());
        if(prova.getDataInicial()!= null && prova.getDataFinal()!=null){
            if (hoje.before(prova.getDataInicial())){
                throw new WebApplicationException("A prova ainda não pode ser realizada! volte entre "
                        + DataUtils.converterParaString(prova.getDataInicial()) + " e " + DataUtils.converterParaString(prova.getDataFinal()));
            }else if(hoje.after(prova.getDataFinal())) {
                throw new WebApplicationException("A prova não pode mais ser realizada! Seu período expirou no dia " + DataUtils.converterParaString(prova.getDataFinal()));
            }}
    }

    public void incrementaPopularidade(Long id, String usuario, Prova prova){
        ProvaRespondida provaRespondida = find("usuario = ?1 AND prova_id = ?2 ", usuario, id).firstResult();
        if(provaRespondida == null){
            prova.setRealizacoes(prova.getRealizacoes() + 1L);
        }
    }

    public void verificaResolucoes(Long id, String usuario, Prova prova){
        try {
            if (prova.getTentativas() != null) {
                ProvaRespondida provaRespondida = find("usuario = ?1 AND prova_id = ?2 ", usuario, id).firstResult();
                if (provaRespondida != null && provaRespondida.getResolucoes() == prova.getTentativas()) {
                    throw new WebApplicationException("Você já atingiu o limite de tentativas pemritidas para essa prova!", Response.Status.FORBIDDEN);
                }
            }
        }catch (WebApplicationException e) {
            throw new WebApplicationException(e.getMessage(), e.getResponse());
        }
    }

    public ProvaRespondidaDto buscarProvaRespondidaInteira(Long id) {
        try {
            ProvaRespondida provaRespondida = findById(id);
            if (provaRespondida == null) throw new WebApplicationException("Prova não encontrada", Response.Status.NOT_FOUND);
            ProvaRespondidaDto provaDto = ProvaRespondidaDto.instanciar(provaRespondida);
            provaDto.setProvaDto(provaRepository.buscarProvaInteira(provaRespondida.getProva().getId()));
            return provaDto;
        } catch (WebApplicationException e) {
            throw new WebApplicationException(e.getMessage(), e.getResponse());
        }
    }

    public void atualizarMediaNota(ProvaRespondida provaRespondida){
        Prova prova = provaRespondida.getProva();
        System.out.println("corrigidas: ");
        if(provaRespondida.getQuestoesCorrigidas() == prova.getQuantidadeQuestoes()){
            provaRespondida.setCorrigida(true);
            if(prova.getMediaNotas().equals(new BigDecimal(0))){
                BigDecimal media = (provaRespondida.getNotaAluno().multiply(new BigDecimal(100))).divide(prova.getNotaMaxima(),2, RoundingMode.HALF_EVEN);
                prova.setMediaNotas(media);
            }else {
                BigDecimal parcial = (prova.getNotaMaxima().multiply(prova.getMediaNotas())).divide(new BigDecimal(100), 2, RoundingMode.HALF_EVEN);
                BigDecimal mediaAtualizada = (((parcial.multiply(new BigDecimal(prova.getRealizacoes()-1)))
                        .add(provaRespondida.getNotaAluno()))
                        .divide(new BigDecimal(prova.getRealizacoes()),2, RoundingMode.HALF_UP))
                        .multiply(new BigDecimal(100)).divide(prova.getNotaMaxima(),2, RoundingMode.HALF_UP);
                prova.setMediaNotas(mediaAtualizada);
            }
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
            this.atualizarMediaNota(provaRespondida);
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
            this.atualizarMediaNota(provaRespondida);
        } catch (Exception e) {
            throw new Error(e);
        }
    }


}

