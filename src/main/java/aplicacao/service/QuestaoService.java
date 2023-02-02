package aplicacao.service;

import dominio.*;
import infraestrutura.dto.*;
import infraestrutura.repository.AlternativaRepository;
import infraestrutura.repository.ConteudoRepository;
import infraestrutura.repository.QuestaoRepository;
import infraestrutura.repository.ValorQuestaoRepository;
import interfaces.controller.resposta.RespostaAPI;
import org.jboss.resteasy.annotations.jaxrs.QueryParam;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.WebApplicationException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@ApplicationScoped
public class QuestaoService {

    @Inject AlternativaRepository alternativaRepository;
    @Inject QuestaoRepository questaoRepository;
    @Inject ConteudoRepository conteudoRepository;

    @Inject
    ValorQuestaoRepository valorQuestaoRepository;

    @Transactional
    public Long cadastrarNovaQuestao(QuestaoDto dto, String usuario) {
        try {
            List<Alternativa> alternativas = new ArrayList<>();
            for (AlternativaDto alternativaDto : dto.alternativas) {
                alternativas.add(Alternativa.instanciar(alternativaDto, usuario));
            }
            alternativas = alternativaRepository.cadastrarAlternativas(alternativas);
            Questao questao = questaoRepository.cadastrarQuestao(dto.paraDominio(dto, alternativas, usuario));
            if(questao.isMultipaEscolha()) {
                System.out.println("oi");
                for(Alternativa alt : questao.getAlternativas()){
                    System.out.println("alt: " + alt.getId() + " corretw: " + alt.getCorreta());
                    if(alt.getCorreta()){
                        System.out.println("oi");
                        questao.setRespostaCorreta(alt.getId().toString());}
                }
            }
            for (Long idConteudo : dto.conteudos) {
                Conteudo conteudo = conteudoRepository.buscarPorID(idConteudo);
                conteudo.getQuestoes().add(questao);
                conteudo.setNumeroQuestoes(conteudo.getNumeroQuestoes() + 1L);
            }
            return questao.getId();
        }catch (Exception e){
            throw new WebApplicationException(e);
        }
    }

    public BuscaPaginadaDto filtrarQuestoes(Integer pagina,
            String enunciado,
            Integer ordenacao,
            Integer ordem,
            List<Integer> conteudos,
            int multiplaEscolha,
            boolean publica,
            List<Long> idsQuestoes) {
        try {
            List<Questao> questoes = questaoRepository.filtrar(pagina, enunciado, ordenacao, ordem, conteudos, multiplaEscolha, publica,idsQuestoes);
            Long quantidade = questaoRepository.quantidade(pagina, enunciado, ordenacao, ordem, conteudos, multiplaEscolha, publica, idsQuestoes);
            List<QuestaoDto> questoesDto = questoes.stream()
                    .map( questao ->  QuestaoDto.instanciar(questao, "", "", new BigDecimal(0), null, true, null, false))
                    .collect(Collectors.toList());
            return BuscaPaginadaDto.instanciar(null,null,null,null,questoesDto, quantidade);
        }catch (Exception e){
            throw new WebApplicationException(e);
        }
    }

    @Transactional
    public QuestaoDto editarQuestao(QuestaoDto dto, String usuario) {
        try {
            Questao questao = questaoRepository.buscarPorId(dto.id);
            List<Conteudo> conteudos = conteudoRepository.buscarConteudos(dto.conteudos);
            questaoRepository.editarQuestao(questao, dto, conteudos);
            List<Conteudo> listaRemoverConteudos = listaRemoverConteudos(questao.getConteudos(), conteudos);
            List<Conteudo> listaAddConteudos = listaAddConteudos(questao.getConteudos(), conteudos);
            for (Conteudo conteudo : listaRemoverConteudos) {
                    questao.getConteudos().remove(conteudo);
                    conteudo.getQuestoes().remove(questao);
            }
            for (Conteudo conteudo : listaAddConteudos) {
                conteudo.getQuestoes().add(questao);
                conteudo.setNumeroQuestoes(conteudo.getNumeroQuestoes() + 1L);
                questao.getConteudos().add(conteudo);
            }

            List<Alternativa> listaAddAlternativas = listaAddAlternativas(questao.getAlternativas(), dto.alternativas, usuario);

            List<Alternativa> alternativas = alternativaRepository.buscarAlternativas(dto.alternativas);

            List<Alternativa> listaRemoverAlternativas = listaRemoverAlternativas(questao.getAlternativas(), alternativas);


            for (Alternativa alternativa : listaRemoverAlternativas) {
                questao.getAlternativas().remove(alternativa);
                alternativaRepository.delete(alternativa);
            }

            for (Alternativa alternativa : listaAddAlternativas) {
                alternativa.setQuestao(questao);
                questao.getAlternativas().add(alternativa);
            }

            if(questao.isMultipaEscolha()) {
                System.out.println("oi");
                for(Alternativa alt : questao.getAlternativas()){
                    System.out.println("alt: " + alt.getId() + " corretw: " + alt.getCorreta());
                    if(alt.getCorreta()){
                        System.out.println("oi");
                        questao.setRespostaCorreta(alt.getId().toString());}
                }
            }

            return QuestaoDto.instanciar(questao, null,  null, null,
                    null,  false, dto.valor, false);
        }catch (Exception e){
            throw new WebApplicationException(e);
        }
    }

    private List<Conteudo> listaRemoverConteudos(List<Conteudo> conteudosAntigos, List<Conteudo> conteudosNovos){
        List<Conteudo> conteudosRemover = new ArrayList<>();
        for (Conteudo conteudo : conteudosAntigos) {
            if (!conteudosNovos.contains(conteudo)){
                conteudosRemover.add(conteudo);
            }
        }
        return conteudosRemover;
    }

    private List<Conteudo> listaAddConteudos(List<Conteudo> conteudosAntigos, List<Conteudo> conteudosNovos){
        List<Conteudo> conteudosAdicionar = new ArrayList<>();
        for (Conteudo conteudo : conteudosNovos) {
            if (!conteudosAntigos.contains(conteudo)){
                conteudosAdicionar.add(conteudo);
            }
        }
        return conteudosAdicionar;
    }


    private List<Alternativa> listaRemoverAlternativas(List<Alternativa> alternativasAntigas, List<Alternativa> alternativasNovas){
        List<Alternativa> alternativasRemover = new ArrayList<>();
        for (Alternativa alternativa : alternativasAntigas) {
            if (!alternativasNovas.contains(alternativa)){
                alternativasRemover.add(alternativa);
            }
        }
        return alternativasRemover;
    }

    private List<Alternativa> listaAddAlternativas(List<Alternativa> alternativasJaCriadas, List<AlternativaDto> alternativasRecebidas, String usuario){
        List<AlternativaDto> alternativasAdicionar = new ArrayList<>();
        List<Alternativa> novasAlternativas = new ArrayList<>();
        for (AlternativaDto alternativaDto : alternativasRecebidas) {
            if(alternativasJaCriadas.stream().noneMatch(alt -> Objects.equals(alt.getEnunciado(), alternativaDto.enunciado))){
                alternativasAdicionar.add(alternativaDto);
            }
        }
        System.out.println("Alternativas novas: " + alternativasAdicionar.size());
        for(AlternativaDto altAdd : alternativasAdicionar){
            novasAlternativas.add(Alternativa.instanciar(altAdd, usuario));
        }
        alternativaRepository.cadastrarAlternativas(novasAlternativas);
        return novasAlternativas;
    }

}
