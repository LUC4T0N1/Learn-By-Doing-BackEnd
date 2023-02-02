package infraestrutura.dto;

import aplicacao.utlis.DataUtils;
import dominio.Prova;
import dominio.Questao;
import dominio.QuestaoRespondida;
import dominio.ValorQuestao;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Schema(
        name = "Provas",
        description = "JSON com informações sobre uma prova específica")

public class ProvaDto {
    public String nome;
    public BigDecimal notaMaxima;
    public List<Long> idsQuestoes;
    public Long id;
    public Boolean publica;
    public BigDecimal mediaNotas;
    public Long popularidade;
    public List<QuestaoDto> questoes;
    public BigDecimal tempo;
    public String dataInicial;
    public String dataFinal;
    public Long tentativas;
    public int quantidadeQuestoes;
    public List<Long> conteudos;
    public String idSecreto;

    public Prova paraDominio(ProvaDto dto, List<Questao> questoes, String usuario, String idSecreto){
        return Prova.instanciar(dto, questoes, usuario, idSecreto);
    }

    public static ProvaDto instanciarPorEntidade(Prova prova, List<QuestaoRespondida> respostas, Boolean setResposta, List<ValorQuestao> valores, Boolean fazer){
        ProvaDto provaDto = new ProvaDto();
        provaDto.setNome(prova.getNome());
        provaDto.setId(prova.getId());
        provaDto.setMediaNotas(prova.getMediaNotas());
        provaDto.setPopularidade(prova.getRealizacoes());
        provaDto.setDataFinal(prova.getDataFinal());
        provaDto.setIdSecreto(prova.getIdSecreto());
        provaDto.setDataInicial(prova.getDataInicial());
        provaDto.setQuantidadeQuestoes(prova.getQuantidadeQuestoes());
        provaDto.setPublica(prova.getPublica());
        provaDto.setTentativas(prova.getTentativas());
        provaDto.setNotaMaxima(prova.getNotaMaxima());
        provaDto.setTempo(prova.getTempo());
        List<QuestaoDto> questaoDtos = new ArrayList<>();
        if(!respostas.isEmpty()){
            for (QuestaoRespondida questao : respostas) {
                questaoDtos.add(QuestaoDto.instanciar(questao.getQuestao(),
                        questao.getRespostaAluno(), questao.getComentarioProfessor(),questao.getNotaAluno()
                        ,questao.getId(), setResposta, valores.stream().filter(v -> v.getQuestao().equals(questao.getQuestao().getId())).collect(Collectors.toList()).get(0).getValor(), fazer));
            }
        }
        else{
            for (Questao questao : prova.getQuestoes()) {
                questaoDtos.add(QuestaoDto.instanciar(questao, "","", new BigDecimal(0), null, setResposta,
                        valores.stream().filter(v -> v.getQuestao().equals(questao.getId())).collect(Collectors.toList()).get(0).getValor(), fazer));
            }
        }
        provaDto.setQuestoes(questaoDtos);
        return provaDto;
    }

    public static ProvaDto instanciarReduzido(Prova prova){
        ProvaDto provaDto = new ProvaDto();
        provaDto.setNome(prova.getNome());
        provaDto.setId(prova.getId());
        provaDto.setMediaNotas(prova.getMediaNotas());
        provaDto.setPopularidade(prova.getRealizacoes());
        provaDto.setIdSecreto(prova.getIdSecreto());
        provaDto.setDataFinal(prova.getDataFinal());
        provaDto.setDataInicial(prova.getDataInicial());
        provaDto.setQuantidadeQuestoes(prova.getQuantidadeQuestoes());
        provaDto.setPublica(prova.getPublica());
        provaDto.setTentativas(prova.getTentativas());
        provaDto.setNotaMaxima(prova.getNotaMaxima());
        provaDto.setTempo(prova.getTempo());
        return provaDto;
    }

    public static ProvaDto instanciar(String nome) {
      ProvaDto provaDto = new ProvaDto();
        provaDto.nome = nome;
        return provaDto;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public void setNotaMaxima(BigDecimal notaMaxima) {
        this.notaMaxima = notaMaxima;
    }

    public void setIdsQuestoes(List<Long> idsQuestoes) {
        this.idsQuestoes = idsQuestoes;
    }

    public void setPublica(Boolean publica) {
        this.publica = publica;
    }

    public void setTempo(BigDecimal tempo) {
        this.tempo = tempo;
    }

    public void setDataInicial(String dataInicial) {
        this.dataInicial = dataInicial;
    }

    public void setDataFinal(String dataFinal) {
        this.dataFinal = dataFinal;
    }

    public void setTentativas(Long tentativas) {
        this.tentativas = tentativas;
    }

    public void setQuantidadeQuestoes(int quantidadeQuestoes) {
        this.quantidadeQuestoes = quantidadeQuestoes;
    }

    public void setMediaNotas(BigDecimal mediaNotas) {
        this.mediaNotas = mediaNotas;
    }

    public String getIdSecreto() {
        return idSecreto;
    }

    public void setIdSecreto(String idSecreto) {
        this.idSecreto = idSecreto;
    }

    public Long getPopularidade() {
        return popularidade;
    }

    public void setPopularidade(Long popularidade) {
        this.popularidade = popularidade;
    }

    public void setQuestoes(List<QuestaoDto> questoes) {
        this.questoes = questoes;
    }
}
