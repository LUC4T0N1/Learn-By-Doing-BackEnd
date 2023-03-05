package aplicacao.service;

import dominio.Conteudo;
import dominio.Prova;
import dominio.Questao;
import dominio.ValorQuestao;
import infraestrutura.dto.ProvaDto;
import infraestrutura.repository.ConteudoRepository;
import infraestrutura.repository.ProvaRepository;
import infraestrutura.repository.QuestaoRepository;
import infraestrutura.repository.ValorQuestaoRepository;
import io.quarkus.logging.Log;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

@ApplicationScoped
public class ProvaService {

  @Inject ConteudoRepository conteudoRepository;
  @Inject ProvaRepository provaRepository;
  @Inject QuestaoRepository questaoRepository;
  @Inject ValorQuestaoRepository valorQuestaoRepository;

  @Transactional
  public void cadastrarNovaProva(ProvaDto dto, String usuario) {
    List<Questao> questoes = questaoRepository.buscarPorIds(dto.idsQuestoes);
    String idSecreto = null;
    if (!dto.publica) {
      UUID uuid = UUID.randomUUID();
      idSecreto = uuid.toString();
    }
    Prova prova =
        provaRepository.cadastrarProva(dto.paraDominio(dto, questoes, usuario, idSecreto));
    List<ValorQuestao> valorQuestoes = new ArrayList<>();
    BigDecimal valorProva = BigDecimal.ZERO;
    for (Questao questao : questoes) {
      BigDecimal v =
          dto.questoes.stream()
              .filter(q -> q.id.equals(questao.getId()))
              .collect(Collectors.toList())
              .get(0)
              .valor;
      valorQuestoes.add(ValorQuestao.instanciar(prova.getId(), questao.getId(), v));
      valorProva = valorProva.add(v);
    }
    valorQuestaoRepository.cadastrarValores(valorQuestoes);
    prova.setNotaMaxima(valorProva);
    for (Long idConteudo : dto.conteudos) {
      Conteudo conteudo = conteudoRepository.buscarPorID(idConteudo);
      conteudo.getProvas().add(prova);
      conteudo.setNumeroProvas(conteudo.getNumeroProvas() + 1L);
      if(prova.getPublica())conteudo.setNumeroProvasPublicas(conteudo.getNumeroProvasPublicas() + 1L);
    }
    Log.info("Prova cadastrada com sucesso!");
  }
}
