package infraestrutura.dto;

import dominio.Alternativa;
import dominio.Conteudo;
import dominio.Questao;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import javax.ws.rs.WebApplicationException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
@Schema(
        name = "Questões",
        description = "JSON com informações sobre uma questão específica")
public class QuestaoDto {
    public String enunciado;
    public BigDecimal valor;
    public Boolean multiplaEscolha;
    public List<AlternativaDto> alternativas;
    public List<Long> conteudos;
    public List<String> nomeConteudos;
    public String resposta;
    public String respostaAluno;
    public BigDecimal notaAluno;
    public Boolean publica;
    public Long id;
    public Long idQuestaoResolvida;
    public String comentario;

    public Questao paraDominio(QuestaoDto dto, List<Alternativa> alternativas, String usuario){
        return Questao.instanciar(dto, alternativas, usuario);
    }

    public static QuestaoDto instanciar(Questao questao, String respostaAluno, String comentario,BigDecimal notaAluno,
                                        Long idQuestaoResolvida, Boolean setResposta, BigDecimal valor, Boolean fazer){
        try {
            QuestaoDto questaoDto = new QuestaoDto();
           if(setResposta)questaoDto.setResposta(questao.getRespostaCorreta());
            questaoDto.setEnunciado(questao.getEnunciado());
            questaoDto.setMultiplaEscolha(questao.isMultipaEscolha());
            questaoDto.setValor(valor);
            questaoDto.setComentario(comentario);
            questaoDto.setId(questao.getId());
            questaoDto.setNotaAluno(notaAluno);
            List<AlternativaDto> alternativas = new ArrayList<>();
            for (Alternativa alternativa : questao.getAlternativas()) {
                if(!fazer) alternativas.add(AlternativaDto.instanciar(alternativa));
                else alternativas.add(AlternativaDto.instanciarSemResposta(alternativa));
            }
            questaoDto.setAlternativas(alternativas);
            List<Long> conteudos = new ArrayList<>();
            List<String> nomesConteudos = new ArrayList<>();
            for (Conteudo cont : questao.getConteudos()) {
             //   conteudos.add(cont.getId());
                nomesConteudos.add(cont.getNome());
            }
            questaoDto.setConteudos(conteudos);
            questaoDto.setNomeConteudos(nomesConteudos);
            questaoDto.setRespostaAluno(respostaAluno);
            questaoDto.setIdQuestaoResolvida(idQuestaoResolvida);
            return questaoDto;
        }catch (Exception e){
            throw new WebApplicationException(e);
        }
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public void setNotaAluno(BigDecimal notaAluno) {
        this.notaAluno = notaAluno;
    }

    public void setRespostaAluno(String respostaAluno) {
        this.respostaAluno = respostaAluno;
    }

    public void setEnunciado(String enunciado) {
        this.enunciado = enunciado;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public void setResposta(String resposta) {
        this.resposta = resposta;
    }

    public void setMultiplaEscolha(Boolean multiplaEscolha) {
        this.multiplaEscolha = multiplaEscolha;
    }

    public void setAlternativas(List<AlternativaDto> alternativas) {
        this.alternativas = alternativas;
    }

    public void setConteudos(List<Long> conteudos) {
        this.conteudos = conteudos;
    }

    public void setNomeConteudos(List<String> nomeConteudos) {
        this.nomeConteudos = nomeConteudos;
    }

    public void setPublica(Boolean publica) {
        this.publica = publica;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setIdQuestaoResolvida(Long idQuestaoResolvida) {
        this.idQuestaoResolvida = idQuestaoResolvida;
    }

    @Override
    public String toString() {
        return "QuestaoDto{" +
                "enunciado='" + enunciado + '\'' +
                ", valor=" + valor +
                ", multiplaEscolha=" + multiplaEscolha +
                ", alternativas=" + alternativas +
                ", conteudos=" + conteudos +
                ", nomeConteudos=" + nomeConteudos +
                ", resposta='" + resposta + '\'' +
                ", respostaAluno='" + respostaAluno + '\'' +
                ", notaAluno=" + notaAluno +
                ", publica=" + publica +
                ", id=" + id +
                ", idQuestaoResolvida=" + idQuestaoResolvida +
                ", comentario='" + comentario + '\'' +
                '}';
    }
}
