package infraestrutura.repository;

import dominio.*;
import infraestrutura.dto.*;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.logging.Log;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;
import org.jboss.resteasy.annotations.jaxrs.QueryParam;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@ApplicationScoped
public class ProvaRespondidaRepository implements PanacheRepository<ProvaRespondida> {
    @Inject ProvaRepository provaRepository;
    @Inject UsuarioRepository usuarioRepository;
    @Inject QuestaoRespondidaRepository questaoRespondidaRepository;
    @Inject ValorQuestaoRepository valorQuestaoRepository;

    public static final int TAMANHO_PAGINA = 5;

    @Transactional
    public void realizarProva(ProvaRespondidaDto dto, String usuario) {
        try {
            Log.info("Buscando prova de id " + dto.id + "..");
            Prova prova = provaRepository.buscarPorId(dto.id);
            this.verificaResolucoes(dto.id, usuario, prova);
            this.verificarDatas(prova);
            List<QuestaoRespondida> questoesRespondidas = new ArrayList<>();
            for (QuestaoRespondidaDto questaoRespondidaDto : dto.questoesRespondidasDto) {
                questoesRespondidas.add(QuestaoRespondida.instanciar(questaoRespondidaDto,
                        prova.getQuestoes().stream().filter(questao -> questao.getId().equals(questaoRespondidaDto.idQuestao)).findFirst().get(), usuario));
            }
            questaoRespondidaRepository.cadastrarQuestoesRespondidas(questoesRespondidas);
            Usuario usuarioObj =  usuarioRepository.buscarUsuario(usuario);
            ProvaRespondida provaRespondida = ProvaRespondida.instanciar(prova, usuario, usuarioObj);
            this.incrementaPopularidade(dto.id, usuario, prova);
            Log.info("Salvando prova resolvida..");
            persist(provaRespondida);
            Log.info("Salvando questoes da prova resolvida..");
            provaRespondida.setQuestoesRespondidas(questoesRespondidas);
            for (QuestaoRespondida questaoRespondida : questoesRespondidas) {
                questaoRespondida.setProvaRespondida(provaRespondida);
            }
            Log.info("Buscando provas de id "+ dto.id + "resolvidas pelo usuario " + usuario +"..");
            List<ProvaRespondida> provasRespondidas = find("usuario = ?1 AND prova_id = ?2 ", usuario, dto.id).list();
            for(ProvaRespondida pr: provasRespondidas){
                pr.setResolucoes(provasRespondidas.size());
            }
            Log.info("Corrigindo questoes de multipla escolha..");
            corrigirQuestoesMultiplaEscolha(provaRespondida, prova);
        } catch (Exception e) {
            throw new WebApplicationException(e);
                    //WebApplicationException(e.getMessage(), e.getResponse());
        }
    }

    public void corrigirQuestoesMultiplaEscolha(ProvaRespondida provaRespondida, Prova prova) {
        try {
            System.out.println("corrigindo");
            System.out.println("Quantidade questoes: "+ provaRespondida.getQuestoesRespondidas().size());
            BigDecimal notaParcial = new BigDecimal(0);
            int questoes = 0;
            List<ValorQuestao> valores = valorQuestaoRepository.buscarValores(provaRespondida.getProva().getId());
            for(QuestaoRespondida questaoRespondida : provaRespondida.getQuestoesRespondidas()){
                System.out.println("é multipla escolha: "+ questaoRespondida.getQuestao().isMultipaEscolha());
                System.out.println("id: "+ questaoRespondida.getQuestao().getId());
                System.out.println("enunciado: "+ questaoRespondida.getQuestao().getEnunciado());
                if(questaoRespondida.getQuestao().isMultipaEscolha()) {
                    System.out.println("oi?");
                    questoes++;
                    if (!questaoRespondida.getRespostaAluno().equals(questaoRespondida.getQuestao().getRespostaCorreta())) {
                        questaoRespondida.setNotaAluno(new BigDecimal(0));
                        System.out.println("errou");
                    } else {
                        System.out.println("acertou");
                        BigDecimal valor = valores.stream().filter(v ->
                                v.getQuestao().equals(questaoRespondida.getQuestao().getId()))
                                .collect(Collectors.toList()).get(0).getValor();
                        questaoRespondida.setNotaAluno(valor);
                        notaParcial = notaParcial.add(valor);
                    }
                }
            }
            provaRespondida.setQuestoesCorrigidas(questoes);
            if(prova.getQuantidadeQuestoes()==questoes) provaRespondida.setCorrigida(true);
            provaRespondida.setNotaAluno(notaParcial);
            this.atualizarMediaNota(provaRespondida);
        } catch (WebApplicationException e) {
            throw new WebApplicationException(e.getMessage(), e.getResponse());
        }
    }

    public void verificarDatas(Prova prova){
//        Date hoje = DataUtils.dateParaDateFormatada(new Date());
//        if(prova.getDataInicial()!= null && prova.getDataFinal()!=null){
//            if (hoje.before(prova.getDataInicial())){
//                throw new WebApplicationException("A prova ainda não pode ser realizada! volte entre "
//                        + DataUtils.converterParaString(prova.getDataInicial()) + " e " + DataUtils.converterParaString(prova.getDataFinal()));
//            }else if(hoje.after(prova.getDataFinal())) {
//                throw new WebApplicationException("A prova não pode mais ser realizada! Seu período expirou no dia " + DataUtils.converterParaString(prova.getDataFinal()));
//            }}
    }

    public void incrementaPopularidade(Long id, String usuario, Prova prova){
        Log.info("Incrementando popularidade da prova de id " + id + "..");
        ProvaRespondida provaRespondida = find("usuario = ?1 AND prova_id = ?2 ", usuario, id).firstResult();
        if(provaRespondida == null){
            prova.setRealizacoes(prova.getRealizacoes() + 1L);
        }
    }

    public void verificaResolucoes(Long id, String usuario, Prova prova){
        try {
            Log.info("Verificando resoluções..");
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
            provaDto.setProvaDto(provaRepository.buscarProvaInteira(provaRespondida.getProva().getId(), provaRespondida.getQuestoesRespondidas(), true));
            return provaDto;
        } catch (WebApplicationException e) {
            throw new WebApplicationException(e.getMessage(), e.getResponse());
        }
    }

    public BuscaPaginadaDto buscarProvasRespondidasPorProvaCriada(Long id, Integer pagina, String nome, Integer ordenacao, Integer ordem) {
        try {
            List<ProvaRespondida> provasRespondidas = new ArrayList<>();
            long total = 0L;
            String filtro;
            if(ordenacao == 0) filtro = "inclusao";
            else if(ordenacao == 1) filtro = "nome_aluno";
            else if(ordenacao == 2)filtro = "nota_aluno";
            else filtro = "questoes_corrigidas";
            if(ordem == 0){
                if(!Objects.equals(nome, "null")) {
                    provasRespondidas = find("prova_id = ?1 AND nome_aluno = ?2",
                            Sort.by(filtro).ascending(), id, nome + "%").page(Page.of(pagina, TAMANHO_PAGINA)).list();
                    total = (long) find("prova_id = ?1 AND nome_aluno = ?2",
                            Sort.by(filtro).ascending(), id,nome + "%")
                            .list().size();
                }else{
                    provasRespondidas = find("prova_id = ?1",
                            Sort.by(filtro).ascending(),id).page(Page.of(pagina, TAMANHO_PAGINA)).list();
                    total = (long) find("prova_id = ?1",
                            Sort.by(filtro).ascending(),id)
                            .list().size();
                }
            }else{
                if(!Objects.equals(nome, "null")) {
                    provasRespondidas = find("prova_id = ?1 AND nome_aluno = ?2",
                            Sort.by(filtro).descending(), id, nome + "%").page(Page.of(pagina, TAMANHO_PAGINA)).list();
                    total = (long) find("prova_id = ?1 AND nome_aluno = ?2",
                            Sort.by(filtro).descending(), id,nome + "%")
                            .list().size();
                }else{
                    provasRespondidas = find("prova_id = ?1",
                            Sort.by(filtro).descending(),id).page(Page.of(pagina, TAMANHO_PAGINA)).list();
                    total = (long) find("prova_id = ?1",
                            Sort.by(filtro).descending(),id)
                            .list().size();
                }
            }

            List<ProvaRespondidaDto> provasRespondidasDtos = provasRespondidas.stream()
                    .map(provaRespondida -> {
                        ProvaRespondidaDto dto = ProvaRespondidaDto.instanciar(provaRespondida);
                        dto.setProvaDto(provaRepository.buscarProvaInteira(provaRespondida.getProva().getId(), provaRespondida.getQuestoesRespondidas(), true));
                        return dto;
                    }
                    )
                    .collect(Collectors.toList());
            return BuscaPaginadaDto.instanciar(null, null, null, provasRespondidasDtos,null, total);
        } catch (WebApplicationException e) {
            throw new WebApplicationException(e.getMessage(), e.getResponse());
        }
    }

    public BuscaPaginadaDto buscarProvasRespondidasPorUsuario(String usuario, Integer pagina, String nome, Integer ordenacao, Integer ordem) {
        try {
            String filtro;
            System.out.println("usuario: "+ usuario );
            if(ordenacao == 0) filtro = "inclusao";
            else throw new WebApplicationException("Requisição errada!", Response.Status.BAD_REQUEST);
            List<ProvaRespondida> provasRespondidas = new ArrayList<>();
            Long total = 0L;


            if(ordem == 0){
                if(!Objects.equals(nome, "null")) {
                    List<Long> provasIds = provaRepository.buscarListaIdsPorNome(nome);
                    provasRespondidas = find(" prova_id in ?1 AND usuario = ?2", Sort.by(filtro).ascending(), provasIds, usuario)
                            .page(Page.of(pagina, TAMANHO_PAGINA))
                            .list();
                    total = (long) find(" prova_id in ?1 AND usuario = ?2", Sort.by(filtro).ascending(), provasIds, usuario)
                            .list().size();
                }else{
                    provasRespondidas = find("usuario =  ?1", Sort.by(filtro).ascending(), usuario)
                            .page(Page.of(pagina, TAMANHO_PAGINA))
                            .list();
                    total = (long) find("usuario =  ?1", Sort.by(filtro).ascending(), usuario)
                            .list().size();
                }
            }else{
                if(!Objects.equals(nome, "null")) {
                    List<Long> provasIds = provaRepository.buscarListaIdsPorNome(nome);
                    provasRespondidas = find(" prova_id in ?1 AND usuario = ?2", Sort.by(filtro).descending(), provasIds, usuario)
                            .page(Page.of(pagina, TAMANHO_PAGINA))
                            .list();
                    total = (long) find(" prova_id in ?1 AND usuario = ?2", Sort.by(filtro).descending(), provasIds, usuario)
                            .list().size();
                }else{
                    provasRespondidas = find("usuario =  ?1", Sort.by(filtro).descending(), usuario)
                            .page(Page.of(pagina, TAMANHO_PAGINA))
                            .list();
                    total = (long) find("usuario =  ?1", Sort.by(filtro).descending(), usuario)
                            .list().size();
                }
            }

            List<ProvaRespondidaPreviewDto> provasRespondidasDtos = provasRespondidas.stream()
                    .map(provaRespondida -> {
                        List<String> conteudos = new ArrayList<>();
                        for(Conteudo c : provaRespondida.getProva().getConteudos()){
                            conteudos.add(c.getNome());
                        }
                        return ProvaRespondidaPreviewDto.instanciar(provaRespondida.getProva().getNome(), conteudos,
                                provaRespondida.getCorrigida(), provaRespondida.getProva().getPublica(),
                                provaRespondida.getInclusao().toString(), provaRespondida.getNotaAluno(), provaRespondida.getId(), provaRespondida.getProva().getNotaMaxima());
                            }
                    )
                    .collect(Collectors.toList());
            return BuscaPaginadaDto.instanciar( null, null,provasRespondidasDtos,null,null, total);
        } catch (WebApplicationException e) {
            throw new WebApplicationException(e);
        }
    }



    public void atualizarMediaNota(ProvaRespondida provaRespondida){
        Prova prova = provaRespondida.getProva();

    }


    public void corrigirQuestoesDissertativas(CorrigirQuestoesDissertativasDto dto) {
        try {
            ProvaRespondida provaRespondida = findById(dto.idProvaRealizada);
            if (provaRespondida == null) throw new WebApplicationException("Prova não encontrada", Response.Status.NOT_FOUND);
            BigDecimal notaParcial = new BigDecimal(0);
            int questoes = 0;
            for(QuestoesDissertativasDto questaoDissertativa : dto.questoes) {
                QuestaoRespondida questaoRespondida = provaRespondida.getQuestoesRespondidas().stream().filter(q -> q.getId().equals(questaoDissertativa.idQuestaoResolvida)).findFirst().get();
                if(questaoRespondida.getNotaAluno()==null) questoes = questoes + 1;
                questaoRespondida.setNotaAluno(questaoDissertativa.notaQuestao);
                questaoRespondida.setComentarioProfessor(questaoDissertativa.comentarioProfessor);
                notaParcial = notaParcial.add(questaoDissertativa.notaQuestao);
            }
            provaRespondida.setQuestoesCorrigidas(provaRespondida.getQuestoesCorrigidas() + questoes);
            atualizarNotaProva(provaRespondida);
            System.out.println(provaRespondida.getProva().getQuantidadeQuestoes()+ " idd");
            if(provaRespondida.getProva().getQuantidadeQuestoes() == provaRespondida.getQuestoesCorrigidas()) provaRespondida.setCorrigida(true);
            this.atualizarMediaNota(provaRespondida);
        } catch (Exception e) {
            throw new WebApplicationException(e);
        }
    }

    public void atualizarNotaProva(ProvaRespondida provaRespondida) {
        try {
            BigDecimal nota = new BigDecimal(0);
            for(QuestaoRespondida qr : provaRespondida.getQuestoesRespondidas()){
                nota = nota.add(qr.getNotaAluno());
            }
            provaRespondida.setNotaAluno(nota);
        } catch (Exception e) {
            throw new WebApplicationException(e);
        }
    }

    public Integer buscarProvasRealizadasPorUsuario(String usuario){
        try{
            return find("usuario = ?1", usuario).list().size();
        }catch (Exception e){
            return 0;
        }
    }


}

