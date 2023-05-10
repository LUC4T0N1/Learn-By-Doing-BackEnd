package infraestrutura.repository;

import dominio.*;
import infraestrutura.dto.*;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.logging.Log;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

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
      List<ProvaRespondida> prAux = buscarProvaRespondidaInteira(dto.id, usuario);
      if (prAux.size() > 0) {
        for (ProvaRespondida prD : prAux) {
          delete(prD);
        }
      }
      Prova prova = provaRepository.buscarPorId(dto.id);
      this.verificaResolucoes(dto.id, usuario, prova);
      this.verificarDatas(prova);
      List<QuestaoRespondida> questoesRespondidas = new ArrayList<>();
      for (QuestaoRespondidaDto questaoRespondidaDto : dto.questoesRespondidasDto) {
        questoesRespondidas.add(
            QuestaoRespondida.instanciar(
                questaoRespondidaDto,
                prova.getQuestoes().stream()
                    .filter(questao -> questao.getId().equals(questaoRespondidaDto.idQuestao))
                    .findFirst()
                    .get(),
                usuario));
      }
      questaoRespondidaRepository.cadastrarQuestoesRespondidas(questoesRespondidas);
      Usuario usuarioObj = usuarioRepository.buscarUsuario(usuario);
      ProvaRespondida provaRespondida = ProvaRespondida.instanciar(prova, usuario, usuarioObj);
      this.incrementaPopularidade(dto.id, usuario, prova);
      Log.info("Salvando prova resolvida..");
      persist(provaRespondida);
      provaRespondida.setFinalizada(true);
      Log.info("Salvando questoes da prova resolvida..");
      provaRespondida.setQuestoesRespondidas(questoesRespondidas);
      for (QuestaoRespondida questaoRespondida : questoesRespondidas) {
        questaoRespondida.setProvaRespondida(provaRespondida);
      }
      Log.info("Buscando provas de id " + dto.id + "resolvidas pelo usuario " + usuario + "..");
      List<ProvaRespondida> provasRespondidas =
          find("usuario = ?1 AND prova_id = ?2 ", usuario, dto.id).list();
      for (ProvaRespondida pr : provasRespondidas) {
        pr.setResolucoes(provasRespondidas.size());
      }
      Log.info("Corrigindo questoes de multipla escolha..");
      corrigirQuestoesMultiplaEscolha(provaRespondida, prova);
      Log.info("Prova realizada com sucesso!");
    } catch (Exception e) {
      throw new WebApplicationException(e);
    }
  }

  public void corrigirQuestoesMultiplaEscolha(ProvaRespondida provaRespondida, Prova prova) {
    try {
      BigDecimal notaParcial = new BigDecimal(0);
      int questoes = 0;
      List<ValorQuestao> valores =
          valorQuestaoRepository.buscarValores(provaRespondida.getProva().getId());
      for (QuestaoRespondida questaoRespondida : provaRespondida.getQuestoesRespondidas()) {
        if (questaoRespondida.getQuestao().isMultipaEscolha()) {
          questoes++;
          if (!questaoRespondida
              .getRespostaAluno()
              .equals(questaoRespondida.getQuestao().getRespostaCorreta())) {
            questaoRespondida.setNotaAluno(new BigDecimal(0));
          } else {
            BigDecimal valor =
                valores.stream()
                    .filter(v -> v.getQuestao().equals(questaoRespondida.getQuestao().getId()))
                    .collect(Collectors.toList())
                    .get(0)
                    .getValor();
            questaoRespondida.setNotaAluno(valor);
            notaParcial = notaParcial.add(valor);
          }
        }
      }
      provaRespondida.setQuestoesCorrigidas(questoes);
      provaRespondida.setNotaAluno(notaParcial);
      if (prova.getQuantidadeQuestoes() == questoes) {
        provaRespondida.setCorrigida(true);
        this.atualizarMediaNota(provaRespondida);
      }
    } catch (WebApplicationException e) {
      throw new WebApplicationException(e.getMessage(), e.getResponse());
    }
  }

  public void verificarDatas(Prova prova) {
    //        Date hoje = DataUtils.dateParaDateFormatada(new Date());
    //        if(prova.getDataInicial()!= null && prova.getDataFinal()!=null){
    //            if (hoje.before(prova.getDataInicial())){
    //                throw new WebApplicationException("A prova ainda não pode ser realizada! volte
    // entre "
    //                        + DataUtils.converterParaString(prova.getDataInicial()) + " e " +
    // DataUtils.converterParaString(prova.getDataFinal()));
    //            }else if(hoje.after(prova.getDataFinal())) {
    //                throw new WebApplicationException("A prova não pode mais ser realizada! Seu
    // período expirou no dia " + DataUtils.converterParaString(prova.getDataFinal()));
    //            }}
  }

  public void incrementaPopularidade(Long id, String usuario, Prova prova) {
    Log.info("Incrementando popularidade da prova de id " + id + "..");
    ProvaRespondida provaRespondida =
        find("usuario = ?1 AND prova_id = ?2 ", usuario, id).firstResult();
    if (provaRespondida == null) {
      prova.setRealizacoes(prova.getRealizacoes() + 1L);
    }
  }

  public void verificaResolucoes(Long id, String usuario, Prova prova) {
    try {
      Log.info("Verificando resoluções..");
      if (prova.getTentativas() != null) {
        ProvaRespondida provaRespondida =
            find("usuario = ?1 AND prova_id = ?2 and finalizada =  true ", usuario, id)
                .firstResult();
        if (provaRespondida != null && provaRespondida.getResolucoes() == prova.getTentativas()) {
          throw new WebApplicationException(
              "Você já atingiu o limite de tentativas pemritidas para essa prova!",
              Response.Status.FORBIDDEN);
        }
      }
    } catch (WebApplicationException e) {
      Log.info("deu ruim ou nao bucetaa");
      throw new WebApplicationException(e.getMessage(), e.getResponse());
    }
  }

  public ProvaRespondidaDto buscarResolucoesProvaRespondidaInteira(Long id) {
    try {
      ProvaRespondida provaRespondida = findById(id);
      if (provaRespondida == null)
        throw new WebApplicationException("Prova não encontrada", Response.Status.NOT_FOUND);
      ProvaRespondidaDto provaDto = ProvaRespondidaDto.instanciar(provaRespondida);
      provaDto.setProvaDto(
          provaRepository.buscarProvaInteira(
              provaRespondida.getProva().getId(), provaRespondida.getQuestoesRespondidas(), true));
      Log.info("Prova obtida com sucesso!");
      return provaDto;
    } catch (WebApplicationException e) {
      throw new WebApplicationException(e.getMessage(), e.getResponse());
    }
  }

  public int buscarResolucoesProvaRespondidaInteira(Long idProva, String usuario) {
    try {
      ProvaRespondida provaRespondida =
          find("usuario = ?1 and prova_id = ?2", usuario, idProva).firstResult();
      if (provaRespondida == null) return 0;
      return provaRespondida.getResolucoes();
    } catch (WebApplicationException e) {
      throw new WebApplicationException(e.getMessage(), e.getResponse());
    }
  }

  public List<ProvaRespondida> buscarProvaRespondidaInteira(Long idProva, String usuario) {
    try {
      return find("usuario = ?1 and prova_id = ?2 and finalizada = null", usuario, idProva).list();
    } catch (WebApplicationException e) {
      throw new WebApplicationException(e.getMessage(), e.getResponse());
    }
  }

  public BuscaPaginadaDto buscarProvasRespondidasPorProvaCriada(
      Long id, Integer pagina, String nome, Integer ordenacao, Integer ordem) {
    try {
      List<ProvaRespondida> provasRespondidas = new ArrayList<>();
      long total = 0L;
      String filtro;
      if (ordenacao == 0) filtro = "inclusao";
      else if (ordenacao == 1) filtro = "nome_aluno";
      else if (ordenacao == 2) filtro = "nota_aluno";
      else filtro = "questoes_corrigidas";
      if (ordem == 0) {
        if (!Objects.equals(nome, "null")) {
          provasRespondidas =
              find("prova_id = ?1 AND nome_aluno = ?2", Sort.by(filtro).ascending(), id, nome + "%")
                  .page(Page.of(pagina, TAMANHO_PAGINA))
                  .list();
          total =
              (long)
                  find(
                          "prova_id = ?1 AND nome_aluno = ?2",
                          Sort.by(filtro).ascending(),
                          id,
                          nome + "%")
                      .list()
                      .size();
        } else {
          provasRespondidas =
              find("prova_id = ?1", Sort.by(filtro).ascending(), id)
                  .page(Page.of(pagina, TAMANHO_PAGINA))
                  .list();
          total = (long) find("prova_id = ?1", Sort.by(filtro).ascending(), id).list().size();
        }
      } else {
        if (!Objects.equals(nome, "null")) {
          provasRespondidas =
              find(
                      "prova_id = ?1 AND nome_aluno like ?2",
                      Sort.by(filtro).descending(),
                      id,
                      "%"+nome+"%")
                  .page(Page.of(pagina, TAMANHO_PAGINA))
                  .list();
          total =
              (long)
                  find(
                          "prova_id = ?1 AND nome_aluno like ?2",
                          Sort.by(filtro).descending(),
                          id,
                          "%"+nome+"%")
                      .list()
                      .size();
        } else {
          provasRespondidas =
              find("prova_id = ?1", Sort.by(filtro).descending(), id)
                  .page(Page.of(pagina, TAMANHO_PAGINA))
                  .list();
          total = (long) find("prova_id = ?1", Sort.by(filtro).descending(), id).list().size();
        }
      }

      List<ProvaRespondidaDto> provasRespondidasDtos =
          provasRespondidas.stream()
              .map(
                  provaRespondida -> {
                    ProvaRespondidaDto dto = ProvaRespondidaDto.instanciar(provaRespondida);
                    dto.setProvaDto(
                        provaRepository.buscarProvaInteira(
                            provaRespondida.getProva().getId(),
                            provaRespondida.getQuestoesRespondidas(),
                            true));
                    return dto;
                  })
              .collect(Collectors.toList());
      Log.info("Provas buscadas com sucesso!");
      return BuscaPaginadaDto.instanciar(null, null, null, provasRespondidasDtos, null, total);
    } catch (WebApplicationException e) {
      throw new WebApplicationException(e.getMessage(), e.getResponse());
    }
  }

  public BuscaPaginadaDto buscarProvasRespondidasPorUsuario(
      String usuario, Integer pagina, String nome, Integer ordenacao, Integer ordem) {
    try {
      String filtro;
      if (ordenacao == 0) filtro = "inclusao";
      else throw new WebApplicationException("Requisição errada!", Response.Status.BAD_REQUEST);
      List<ProvaRespondida> provasRespondidas = new ArrayList<>();
      Long total = 0L;
      if (ordem == 0) {
        if (!Objects.equals(nome, "null")) {
          List<Long> provasIds = provaRepository.buscarListaIdsPorNome(nome);
          provasRespondidas =
              find(
                      " prova_id in ?1 AND usuario = ?2",
                      Sort.by(filtro).ascending(),
                      provasIds,
                      usuario)
                  .page(Page.of(pagina, TAMANHO_PAGINA))
                  .list();
          total =
              (long)
                  find(
                          " prova_id in ?1 AND usuario = ?2",
                          Sort.by(filtro).ascending(),
                          provasIds,
                          usuario)
                      .list()
                      .size();
        } else {
          provasRespondidas =
              find("usuario =  ?1", Sort.by(filtro).ascending(), usuario)
                  .page(Page.of(pagina, TAMANHO_PAGINA))
                  .list();
          total = (long) find("usuario =  ?1", Sort.by(filtro).ascending(), usuario).list().size();
        }
      } else {
        if (!Objects.equals(nome, "null")) {
          List<Long> provasIds = provaRepository.buscarListaIdsPorNome(nome);
          provasRespondidas =
              find(
                      " prova_id in ?1 AND usuario = ?2",
                      Sort.by(filtro).descending(),
                      provasIds,
                      usuario)
                  .page(Page.of(pagina, TAMANHO_PAGINA))
                  .list();
          total =
              (long)
                  find(
                          " prova_id in ?1 AND usuario = ?2",
                          Sort.by(filtro).descending(),
                          provasIds,
                          usuario)
                      .list()
                      .size();
        } else {
          provasRespondidas =
              find("usuario =  ?1", Sort.by(filtro).descending(), usuario)
                  .page(Page.of(pagina, TAMANHO_PAGINA))
                  .list();
          total = (long) find("usuario =  ?1", Sort.by(filtro).descending(), usuario).list().size();
        }
      }

      List<ProvaRespondidaPreviewDto> provasRespondidasDtos =
          provasRespondidas.stream()
              .map(
                  provaRespondida -> {
                    List<String> conteudos = new ArrayList<>();
                    for (Conteudo c : provaRespondida.getProva().getConteudos()) {
                      conteudos.add(c.getNome());
                    }
                    return ProvaRespondidaPreviewDto.instanciar(
                        provaRespondida.getProva().getNome(),
                        conteudos,
                        provaRespondida.getCorrigida(),
                        provaRespondida.getProva().getPublica(),
                        provaRespondida.getInclusao().toString(),
                        provaRespondida.getNotaAluno(),
                        provaRespondida.getId(),
                        provaRespondida.getProva().getNotaMaxima());
                  })
              .collect(Collectors.toList());
      Log.info("Provas obtidas com sucesso!");
      return BuscaPaginadaDto.instanciar(null, null, provasRespondidasDtos, null, null, total);
    } catch (WebApplicationException e) {
      throw new WebApplicationException(e);
    }
  }

  public void atualizarMediaNota(ProvaRespondida provaRespondida) {
    Log.info("Atualizando media notas..");
    Prova prova = provaRespondida.getProva();
    Double notaMaxima = prova.getNotaMaxima().doubleValue();
    Log.info("notaMaxima: " + notaMaxima);
    Double provasCorrigidas = (double) provasCorrigidas(prova);
    Log.info("provasCorrigidas: " + provasCorrigidas);
    Double mediaAtual = prova.getMediaNotas().doubleValue();
    Log.info("mediaAtual: " + mediaAtual);
    Double notaAluno = provaRespondida.getNotaAluno().doubleValue();
    Log.info("notaAluno: " + notaAluno);
    Double mediaAluno = (notaAluno / notaMaxima) * 100;
    Log.info("mediaAluno: " + mediaAluno);
    Double mediaNova;
    if (!provaRespondida.getCorrigida()) {
      Log.info("1 ");
      mediaNova = ((mediaAtual * provasCorrigidas) + mediaAluno) / (provasCorrigidas + 1);
    } else {
      Log.info("2 ");
      Double notaAntiga = provaRespondida.getNotaAluno().doubleValue();
      Log.info("notaAntiga: " + notaAntiga);
      Double notaNova = obterNotaProva(provaRespondida).doubleValue();
      Log.info("notaNova: " + notaNova);
      mediaNova =
          ((((((mediaAtual / 100) * notaMaxima) * provasCorrigidas) - notaAntiga) + notaNova)
                  / (notaMaxima * provasCorrigidas))
              * 100;
    }
    Log.info("mediaNova: " + mediaNova);
    prova.setMediaNotas(new BigDecimal(mediaNova));
  }

  public int provasCorrigidas(Prova prova) {
    return find("prova_id = ?1 and corrigida = true", prova.getId()).list().size();
  }

  public void corrigirQuestoesDissertativas(CorrigirQuestoesDissertativasDto dto) {
    try {
      ProvaRespondida provaRespondida = findById(dto.idProvaRealizada);
      if (provaRespondida == null)
        throw new WebApplicationException("Prova não encontrada", Response.Status.NOT_FOUND);
      BigDecimal notaParcial = new BigDecimal(0);
      int questoes = 0;
      for (QuestoesDissertativasDto questaoDissertativa : dto.questoes) {
        QuestaoRespondida questaoRespondida =
            provaRespondida.getQuestoesRespondidas().stream()
                .filter(q -> q.getId().equals(questaoDissertativa.idQuestaoResolvida))
                .findFirst()
                .get();
        if (questaoRespondida.getNotaAluno() == null) questoes = questoes + 1;
        questaoRespondida.setNotaAluno(questaoDissertativa.notaQuestao);
        questaoRespondida.setComentarioProfessor(questaoDissertativa.comentarioProfessor);
        notaParcial = notaParcial.add(questaoDissertativa.notaQuestao);
      }
      provaRespondida.setQuestoesCorrigidas(provaRespondida.getQuestoesCorrigidas() + questoes);
      if (!provaRespondida.getCorrigida()) {
        atualizarNotaProva(provaRespondida);
        this.atualizarMediaNota(provaRespondida);
        provaRespondida.setCorrigida(true);
      } else {
        this.atualizarMediaNota(provaRespondida);
        atualizarNotaProva(provaRespondida);
        provaRespondida.setCorrigida(true);
      }
      Log.info("Prova corrigida com sucesso!");
    } catch (Exception e) {
      throw new WebApplicationException(e);
    }
  }

  public void atualizarNotaProva(ProvaRespondida provaRespondida) {
    try {
      BigDecimal nota = new BigDecimal(0);
      for (QuestaoRespondida qr : provaRespondida.getQuestoesRespondidas()) {
        nota = nota.add(qr.getNotaAluno());
      }
      provaRespondida.setNotaAluno(nota);
    } catch (Exception e) {
      throw new WebApplicationException(e);
    }
  }

  public BigDecimal obterNotaProva(ProvaRespondida provaRespondida) {
    try {
      BigDecimal nota = new BigDecimal(0);
      for (QuestaoRespondida qr : provaRespondida.getQuestoesRespondidas()) {
        nota = nota.add(qr.getNotaAluno());
      }
      return nota;
    } catch (Exception e) {
      throw new WebApplicationException(e);
    }
  }

  public Integer buscarProvasRealizadasPorUsuario(String usuario) {
    try {
      return find("usuario = ?1", usuario).list().size();
    } catch (Exception e) {
      return 0;
    }
  }

  @Transactional
  public TempoProvaDto iniciarProvaPrivada(Long id, String usuario) {
    try {
      ProvaRespondida provaJaIniciada =
          find("finalizada = null AND usuario = ?1 AND prova_id = ?2", usuario, id).firstResult();
      if (provaJaIniciada != null) {
        Long tempoPassado = calcularTempoRestante(provaJaIniciada);
        if (tempoPassado >= provaJaIniciada.getProva().getTempo() * 60) {
          return TempoProvaDto.instanciar(false, 0L);
        } else {
          return TempoProvaDto.instanciar(
              true, ((provaJaIniciada.getProva().getTempo() * 60) - tempoPassado));
        }
      } else {
        Prova prova = provaRepository.buscarPorId(id);
        Usuario usuarioObj = usuarioRepository.buscarUsuario(usuario);
        ProvaRespondida provaRespondida = ProvaRespondida.instanciar(prova, usuario, usuarioObj);
        persist(provaRespondida);
        return TempoProvaDto.instanciar(true, prova.getTempo() * 60);
      }
    } catch (WebApplicationException e) {
      throw new WebApplicationException(e.getMessage(), e.getResponse());
    }
  }

  private Long calcularTempoRestante(ProvaRespondida provaRespondida) {
    Log.info("Calculando tempo restante..");
    LocalDateTime dataInicio =
        provaRespondida
            .getInicioProva()
            .toInstant()
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime();
    LocalDateTime dataAtual =
        new Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    Duration diff = Duration.between(dataInicio, dataAtual);
    return diff.toSeconds();
  }
  ;

  public void validarResolucoes(Long id, String usuario) {
    try {
      int realizacoes =
          find("finalizada = true AND usuario = ?1 AND prova_id = ?2", usuario, id).list().size();
      Prova prova = provaRepository.buscarPorId(id);
      if (realizacoes >= prova.getTentativas())
        throw new WebApplicationException("Sem mais realizações permitidas!");
      Log.info("Resolucoes validadas com sucesso!");
    } catch (WebApplicationException e) {
      throw new WebApplicationException(e.getMessage(), e.getResponse());
    }
  }

  public void validarDatas(Long id) {
    try {
      Prova prova = provaRepository.buscarPorId(id);
      if (prova.getDataFinal() != null && prova.getDataInicial() != null) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate dataInicio = LocalDate.parse(prova.getDataInicial(), formatter);
        LocalDate dataFim = LocalDate.parse(prova.getDataFinal(), formatter);
        LocalDate dataAtual = LocalDate.parse(LocalDateTime.now().format(formatter));
        if (!(dataInicio.isBefore(dataAtual) && dataFim.isAfter(dataAtual))
            && !dataInicio.isEqual(dataAtual)
            && !dataFim.isEqual(dataAtual)) throw new WebApplicationException("Periodo invalido!");
      } else if (prova.getDataFinal() == null && prova.getDataInicial() != null) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate dataInicio = LocalDate.parse(prova.getDataInicial(), formatter);
        LocalDate dataAtual = LocalDate.parse(LocalDateTime.now().format(formatter));
        if (!dataInicio.isEqual(dataAtual) && !dataInicio.isBefore(dataAtual))
          throw new WebApplicationException("Periodo invalido!");
      } else if (prova.getDataFinal() != null && prova.getDataInicial() == null) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate dataFim = LocalDate.parse(prova.getDataFinal(), formatter);
        LocalDate dataAtual = LocalDate.parse(LocalDateTime.now().format(formatter));
        if (!dataFim.isAfter(dataAtual) && !dataFim.isEqual(dataAtual))
          throw new WebApplicationException("Periodo invalido!");
      }
      Log.info("Datas validadas com sucesso!");
    } catch (WebApplicationException e) {
      throw new WebApplicationException(e.getMessage(), e.getResponse());
    }
  }
}
