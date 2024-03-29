package infraestrutura.repository;

import dominio.Alternativa;
import dominio.Conteudo;
import dominio.Questao;
import infraestrutura.dto.QuestaoDto;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.logging.Log;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

@ApplicationScoped
public class QuestaoRepository implements PanacheRepository<Questao> {

  @Inject EntityManager em;

  public static final int TAMANHO_PAGINA = 10;

  public List<Questao> buscarPorIds(List<Long> ids) {
    List<Questao> questoes = list("id in ?1", ids);
    if (questoes == null)
      throw new WebApplicationException("Questoes não encontradas", Response.Status.NOT_FOUND);
    return questoes;
  }

  public Questao buscarPorId(Long id) {
    Log.info("Buscando questão por id..");
    Questao questao = findById(id);
    if (questao == null)
      throw new WebApplicationException("Prova não encontrada", Response.Status.NOT_FOUND);
    return questao;
  }

  public Questao editarQuestao(Questao questao, QuestaoDto dto, List<Conteudo> conteudos) {
    try {
      Log.info("Editando questoes..");
      questao.setRespostaCorreta(dto.resposta);
      questao.setEnunciado(dto.enunciado);
      questao.setPublica(dto.publica);
      questao.setMultipaEscolha(dto.multiplaEscolha);
      return questao;
    } catch (WebApplicationException e) {
      throw new WebApplicationException(e.getMessage(), e.getResponse());
    }
  }

  public Questao cadastrarQuestao(Questao questao) {
    try {
      Log.info("Cadastrando questao..");
      persist(questao);
      for (Alternativa alternativa : questao.getAlternativas()) {
        alternativa.setQuestao(questao);
      }
      return questao;
    } catch (WebApplicationException e) {
      throw new WebApplicationException(e.getMessage(), e.getResponse());
    }
  }

  public Long quantidade(
      String enunciado,
      Integer ordenacao,
      Integer ordem,
      List<Integer> conteudos,
      int multiplaEscolha,
      boolean publica,
      List<Long> idsQuestoes,
      String usuario) {
    try {
      Log.info("Buscando quantidade..");
      List<Long> ids =
          construirQueryQuantidade(
              enunciado, ordenacao, ordem, conteudos, multiplaEscolha, publica, usuario);
      ids.removeIf(idsQuestoes::contains);
      return (long) find("id in ?1", ids).list().size();
    } catch (Exception e) {
      throw new WebApplicationException(e);
    }
  }

  public List<Questao> filtrar(
      Integer pagina,
      String enunciado,
      Integer ordenacao,
      Integer ordem,
      List<Integer> conteudos,
      int multiplaEscolha,
      boolean publica,
      List<Long> idsQuestoes,
      String usuario) {
    try {
      List<Long> ids =
          construirQuery(
              pagina, enunciado, ordenacao, ordem, conteudos, multiplaEscolha, publica, usuario);
      ids.removeIf(idsQuestoes::contains);
      String orderBy = "enunciado";
      if (ordenacao == 1) orderBy = "inclusao";
      String ascdesc = "ASC";
      if (ordem == 1) ascdesc = "DESC";
      return find("id in ?1 ORDER BY " + orderBy + " " + ascdesc, ids).list();
    } catch (Exception e) {
      throw new WebApplicationException(e);
    }
  }

  public List<Long> construirQueryQuantidade(
      String enunciado,
      Integer ordenacao,
      Integer ordem,
      List<Integer> conteudos,
      int multiplaEscolha,
      boolean publica,
      String usuario) {
    try {
      boolean tipoEspecifico = false;
      if (multiplaEscolha > 0) tipoEspecifico = true;
      Boolean tipoQuestao = false;
      if (multiplaEscolha == 1) tipoQuestao = true;
      else if (multiplaEscolha == 2) tipoQuestao = false;
      StringBuilder querySB = new StringBuilder("SELECT questao.id FROM questao ");
      if (conteudos.size() > 0) temConteudo(querySB);
      privacidade(querySB, publica, usuario);
      if (conteudos.size() > 0) conteudos(querySB);
      if (!Objects.equals(enunciado, "null")) enunciado(querySB, enunciado);
      if (tipoEspecifico) tipoEspecifico(querySB, tipoQuestao);
      ordenar(querySB, ordenacao, ordem);
      Query query = this.em.createNativeQuery(querySB.toString());
      if (conteudos.size() > 0) query.setParameter("conteudos", conteudos);
      List<Long> ids = new ArrayList<>();
      List quests = query.getResultList();
      for (Object produto : quests) {
        ids.add(Long.valueOf(String.valueOf(produto)));
      }
      return ids;
    } catch (Exception e) {
      throw new WebApplicationException(e);
    }
  }

  public List<Long> construirQuery(
      Integer pagina,
      String enunciado,
      Integer ordenacao,
      Integer ordem,
      List<Integer> conteudos,
      int multiplaEscolha,
      boolean publica,
      String usuario) {
    try {
      Log.info("Construindo query...");
      boolean tipoEspecifico = false;
      if (multiplaEscolha > 0) tipoEspecifico = true;
      Boolean tipoQuestao = false;
      if (multiplaEscolha == 1) tipoQuestao = true;
      else if (multiplaEscolha == 2) tipoQuestao = false;
      StringBuilder querySB = new StringBuilder("SELECT questao.id FROM questao ");
      if (conteudos.size() > 0) temConteudo(querySB);
      privacidade(querySB, publica, usuario);
      if (conteudos.size() > 0) conteudos(querySB);
      if (!Objects.equals(enunciado, "null")) enunciado(querySB, enunciado);
      if (tipoEspecifico) tipoEspecifico(querySB, tipoQuestao);
      ordenar(querySB, ordenacao, ordem);
      paginar(querySB, pagina);
      Query query = this.em.createNativeQuery(querySB.toString());
      Log.info("query: " + querySB);
      if (conteudos.size() > 0) query.setParameter("conteudos", conteudos);
      List<Long> ids = new ArrayList<>();
      List quests = query.getResultList();
      for (Object produto : quests) {
        ids.add(Long.valueOf(String.valueOf(produto)));
      }
      return ids;
    } catch (Exception e) {
      throw new WebApplicationException(e);
    }
  }

  private void temConteudo(StringBuilder query) {
    query.append(" INNER JOIN conteudo_questao cont_quest ON cont_quest.questao_id = questao.id ");
  }

  private void privacidade(StringBuilder query, Boolean publica, String usuario) {
    if (publica) {
      query.append(" WHERE publica = TRUE");
    } else {
      query.append(" WHERE publica = FALSE AND usuario = \"" + usuario + "\"");
    }
  }

  private void conteudos(StringBuilder query) {
    query.append(" AND cont_quest.conteudo_id IN :conteudos");
  }

  private void enunciado(StringBuilder query, String enunciado) {
    query.append(" AND enunciado like '%").append(enunciado).append("%'");
  }

  private void tipoEspecifico(StringBuilder query, Boolean multiplaEscolha) {
    query.append(" AND multipla_escolha = ").append(multiplaEscolha);
  }

  private void ordenar(StringBuilder query, int ordenacao, int ordem) {
    if (ordenacao == 0) query.append(" ORDER BY enunciado");
    else query.append(" ORDER BY  inclusao");
    if (ordem == 0) query.append(" ASC");
    else query.append(" DESC");
  }

  private void paginar(StringBuilder query, int pagina) {
    String p = "";
    if (pagina > 0) p = "0";
    query.append(" LIMIT ").append(pagina).append(p).append(",").append(TAMANHO_PAGINA);
  }

  public Integer buscarQuestoesCriadasPorUsuario(String usuario) {
    try {
      return find("usuario = ?1", usuario).list().size();
    } catch (Exception e) {
      return 0;
    }
  }
}
