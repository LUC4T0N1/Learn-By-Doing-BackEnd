package infraestrutura.repository;

import dominio.*;
import infraestrutura.dto.*;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.logging.Log;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

@ApplicationScoped
public class ProvaRepository implements PanacheRepository<Prova> {
  public static final int TAMANHO_PAGINA = 5;
  @Inject EntityManager em;
  @Inject QuestaoRepository questaoRepository;
  @Inject ValorQuestaoRepository valorQuestaoRepository;

  @Inject ProvaRespondidaRepository provaRespondidaRepository;

  public Prova buscarPorId(Long id) {
    Prova prova = findById(id);
    if (prova == null)
      throw new WebApplicationException("Prova não encontrada", Response.Status.NOT_FOUND);
    return prova;
  }

  public Prova buscarPorIdUsuario(Long id, String usuario) {
    Prova prova = find("id = ?1 AND usuario = ?2", id, usuario).firstResult();
    if (prova == null) {
      throw new WebApplicationException(
          "Essa prova não existe ou não pertence a esse usuário!", Response.Status.NOT_FOUND);
    }
    return prova;
  }

  public ProvaDto buscarProvaPrivadaPorId(String id) {
    try {
      Prova prova = find("id_secreto = ?1", id).firstResult();
      if (prova == null)
        throw new WebApplicationException("Prova não encontrada", Response.Status.NOT_FOUND);
      List<ValorQuestao> valorQuestaos = valorQuestaoRepository.buscarValores(prova.getId());
      Log.info("Prova privada buscada com sucesso!");
      return ProvaDto.instanciarPorEntidade(prova, new ArrayList<>(), false, valorQuestaos, true);
    } catch (WebApplicationException e) {
      throw new WebApplicationException(e.getMessage(), e.getResponse());
    }
  }

  public ProvaDto buscarProvaInteira(
      Long id, List<QuestaoRespondida> respostas, Boolean setResposta) {
    try {
      Prova prova = findById(id);
      if (prova == null)
        throw new WebApplicationException("Prova não encontrada", Response.Status.NOT_FOUND);
      List<ValorQuestao> valorQuestaos = valorQuestaoRepository.buscarValores(id);
      return ProvaDto.instanciarPorEntidade(prova, respostas, setResposta, valorQuestaos, false);
    } catch (WebApplicationException e) {
      throw new WebApplicationException(e.getMessage(), e.getResponse());
    }
  }

  public ProvaDto buscarProvaInteiraFazer(
      Long id, List<QuestaoRespondida> respostas, Boolean setResposta, String usuario) {
    try {
      Prova prova = findById(id);
      if (prova == null)
        throw new WebApplicationException("Prova não encontrada", Response.Status.NOT_FOUND);
      List<ValorQuestao> valorQuestoes = valorQuestaoRepository.buscarValores(id);
      ProvaDto dto =
          ProvaDto.instanciarPorEntidade(prova, respostas, setResposta, valorQuestoes, true);
      dto.realizacoes =
          provaRespondidaRepository.buscarResolucoesProvaRespondidaInteira(prova.getId(), usuario);
      Log.info("Prova buscada com sucesso!");
      return dto;
    } catch (WebApplicationException e) {
      throw new WebApplicationException(e.getMessage(), e.getResponse());
    }
  }

  public Prova cadastrarProva(Prova prova) {
    try {
      persist(prova);
      return prova;
    } catch (WebApplicationException e) {
      throw new WebApplicationException(e.getMessage(), e.getResponse());
    }
  }

  public void alterarProva(ProvaDto provaDto, String usuario) {
    try {
      Prova prova = this.buscarPorIdUsuario(provaDto.id, usuario);
      prova.setDataFinal(provaDto.dataFinal);
      prova.setDataInicial(provaDto.dataInicial);
      prova.setNotaMaxima(provaDto.notaMaxima);
      prova.setPublica(provaDto.publica);
      prova.setTempo(provaDto.tempo);
      prova.setTentativas(provaDto.tentativas);
      prova.setNome(provaDto.nome);
      prova.setQuantidadeQuestoes(provaDto.quantidadeQuestoes);
      List<Questao> questoes = questaoRepository.buscarPorIds(provaDto.idsQuestoes);
      prova.getQuestoes().clear();
      prova.setQuestoes(questoes);
      Log.info("Prova alterada com sucesso!");
    } catch (WebApplicationException e) {
      throw new WebApplicationException(e.getMessage(), e.getResponse());
    }
  }

  public BuscaPaginadaDto buscarPorUsuario(
      Integer pagina, String usuario, String nome, int ordenacao, int ordem) {
    try {
      String filtro = "";
      if (ordenacao == 0) filtro = "inclusao";
      else if (ordenacao == 1) filtro = "nome";
      else if (ordenacao == 2) filtro = "quantidade_questoes";
      else if (ordenacao == 3) filtro = "realizacoes";
      else if (ordenacao == 4) filtro = "media_notas";
      else throw new WebApplicationException("Requisição errada!", Response.Status.BAD_REQUEST);
      List<Prova> provas = new ArrayList<>();
      Long total = 0L;

      if (ordem == 0) {
        if (!Objects.equals(nome, "null")) {
          provas =
              find(
                      " nome like ?1 AND usuario = ?2",
                      Sort.by(filtro).ascending(),
                      "%" + nome + "%",
                      usuario)
                  .page(Page.of(pagina, TAMANHO_PAGINA))
                  .list();
          total =
              (long)
                  find(
                          " nome like ?1 AND usuario = ?2",
                          Sort.by(filtro).ascending(),
                          "%" + nome + "%",
                          usuario)
                      .list()
                      .size();
        } else {
          provas =
              find("usuario =  ?1", Sort.by(filtro).ascending(), usuario)
                  .page(Page.of(pagina, TAMANHO_PAGINA))
                  .list();
          total = (long) find("usuario =  ?1", Sort.by(filtro).ascending(), usuario).list().size();
        }
      } else {
        if (!Objects.equals(nome, "null")) {
          provas =
              find(
                      "nome like ?1 AND usuario = ?2",
                      Sort.by(filtro).descending(),
                      "%" + nome + "%",
                      usuario)
                  .page(Page.of(pagina, TAMANHO_PAGINA))
                  .list();
          total =
              (long)
                  find(
                          "nome like ?1 AND usuario = ?2",
                          Sort.by(filtro).descending(),
                          "%" + nome + "%",
                          usuario)
                      .list()
                      .size();
        } else {
          provas =
              find("usuario =  ?1", Sort.by(filtro).descending(), usuario)
                  .page(Page.of(pagina, TAMANHO_PAGINA))
                  .list();
          total = (long) find("usuario =  ?1", Sort.by(filtro).descending(), usuario).list().size();
        }
      }
      List<ProvaDto> provasDto =
          provas.stream().map(ProvaDto::instanciarReduzido).collect(Collectors.toList());
      Log.info("Provas buscadas com sucesso!");
      return BuscaPaginadaDto.instanciar(null, provasDto, null, null, null, total);
    } catch (Exception e) {
      throw new WebApplicationException(
          "Erro ao obter provas", Response.Status.INTERNAL_SERVER_ERROR);
    }
  }

  public BuscaPaginadaDto buscarProvasPorConteudo(
      Integer pagina, String nome, Integer ordenacao, Long idConteudo, int ordem) {
    try {
      Query query =
          this.em.createNativeQuery(
              "SELECT provas_id FROM conteudo_prova WHERE conteudo_id = :idConteudo");
      query.setParameter("idConteudo", idConteudo);
      List vetor = query.getResultList();
      List<Long> ids = new ArrayList<>();
      for (Object id : vetor) {
        ids.add(Long.valueOf(String.valueOf(id)));
      }
      String filtro = "";
      if (ordenacao == 0) filtro = "nome";
      else if (ordenacao == 1) filtro = "quantidade_questoes";
      else if (ordenacao == 2) filtro = "realizacoes";
      else if (ordenacao == 3) filtro = "media_notas";
      else throw new WebApplicationException("Requisição errada!", Response.Status.BAD_REQUEST);
      List<Prova> provas = new ArrayList<>();
      List<ProvaDto> provasDto = new ArrayList<>();
      Long total = 0L;
      if (ordem == 0) {
        if (!Objects.equals(nome, "null")) {
          provas =
              find(
                      "publica = 1 AND nome like ?1 AND id IN ?2",
                      Sort.by(filtro).ascending(),
                      "%" + nome + "%",
                      ids)
                  .page(Page.of(pagina, TAMANHO_PAGINA))
                  .list();
        } else {
          provas =
              find("publica = 1 AND id IN ?1", Sort.by(filtro).ascending(), ids)
                  .page(Page.of(pagina, TAMANHO_PAGINA))
                  .list();
        }
      } else {
        if (!Objects.equals(nome, "null")) {
          provas =
              find(
                      "publica = 1 AND nome like ?1 AND id IN ?2",
                      Sort.by(filtro).descending(),
                      "%" + nome + "%",
                      ids)
                  .page(Page.of(pagina, TAMANHO_PAGINA))
                  .list();
        } else {
          provas =
              find("publica = 1 AND id IN ?1", Sort.by(filtro).descending(), ids)
                  .page(Page.of(pagina, TAMANHO_PAGINA))
                  .list();
        }
      }
      provasDto = provas.stream().map(ProvaDto::instanciarReduzido).collect(Collectors.toList());
      total = (long) ids.size();
      Log.info("Provas buscadas com sucesso!");
      return BuscaPaginadaDto.instanciar(null, provasDto, null, null, null, total);
    } catch (WebApplicationException e) {
      throw new WebApplicationException(e.getMessage(), e.getResponse());
    }
  }

  public Long obterIdProvaPrivada(String idSecreto) {
    try {
      Prova prova = find("id_secreto = ?1", idSecreto).singleResult();
      if (prova == null) {
        throw new WebApplicationException("Id secreto invalido!", Response.Status.NOT_FOUND);
      }
      return prova.getId();
    } catch (WebApplicationException e) {
      throw new WebApplicationException(e.getMessage(), e.getResponse());
    }
  }

  public Integer buscarProvasCriadasPorUsuario(String usuario) {
    try {
      return find("usuario = ?1", usuario).list().size();
    } catch (Exception e) {
      return 0;
    }
  }

  public Integer buscarProvasCorrigidasPorUsuario(String usuario) {
    try {
      Integer qtd = 0;
      List<Prova> provas = find("usuario = ?1", usuario).list();
      for (Prova p : provas) {
        qtd = qtd + p.getRealizacoes().intValue();
      }
      return qtd;
    } catch (Exception e) {
      return 0;
    }
  }

  public List<Long> buscarListaIdsPorNome(String nome) {
    List<Prova> provas = find("nome like ?1 ", "%" + nome + "%").list();
    List<Long> ids = new ArrayList<>();
    for (Prova p : provas) {
      ids.add(p.getId());
    }
    return ids;
  }
}
