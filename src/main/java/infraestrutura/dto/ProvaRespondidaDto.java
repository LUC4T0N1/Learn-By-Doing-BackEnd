package infraestrutura.dto;
import dominio.ProvaRespondida;
import dominio.QuestaoRespondida;
import io.quarkus.logging.Log;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Schema(
        name = "Provas",
        description = "JSON com informações sobre uma prova realizada")

public class ProvaRespondidaDto {
    public String nomeAluno;
    public String emailAluno;
    public Long id;
    public ProvaDto provaDto;
    public BigDecimal notaMaxima;
    public int questoesCorrigidas;
    public int totalQuestoes;
    public BigDecimal notaAluno;
    public Boolean totalmenteCorrigida;
    public List<QuestaoRespondidaDto> questoesRespondidasDto;
    public int resolucoes;

    protected ProvaRespondidaDto(){}

    public static ProvaRespondidaDto instanciar(ProvaRespondida provaRespondida){
        ProvaRespondidaDto realizarProvaDto = new ProvaRespondidaDto();
        realizarProvaDto.setId(provaRespondida.getId());
        realizarProvaDto.setEmailAluno(provaRespondida.getEmailAluno());
        realizarProvaDto.setNomeAluno(provaRespondida.getNomeAluno());
        realizarProvaDto.setNotaAluno(provaRespondida.getNotaAluno());
        realizarProvaDto.setNotaMaxima(provaRespondida.getProva().getNotaMaxima());
        Log.info("Aqui: "+ provaRespondida.getProva().getNotaMaxima());
        realizarProvaDto.setQuestoesCorrigidas(provaRespondida.getQuestoesCorrigidas());
        realizarProvaDto.setTotalQuestoes(provaRespondida.getProva().getQuestoes().size());
        realizarProvaDto.setResolucoes(provaRespondida.getResolucoes());
        realizarProvaDto.setTotalmenteCorrigida(provaRespondida.getCorrigida());
        return realizarProvaDto;
    }

    public String getNomeAluno() {
        return nomeAluno;
    }

    public void setNomeAluno(String nomeAluno) {
        this.nomeAluno = nomeAluno;
    }

    public String getEmailAluno() {
        return emailAluno;
    }

    public void setNotaMaxima(BigDecimal notaMaxima) {
        this.notaMaxima = notaMaxima;
    }

    public void setQuestoesCorrigidas(int questoesCorrigidas) {
        this.questoesCorrigidas = questoesCorrigidas;
    }

    public void setTotalQuestoes(int totalQuestoes) {
        this.totalQuestoes = totalQuestoes;
    }

    public void setEmailAluno(String emailAluno) {
        this.emailAluno = emailAluno;
    }

    public Long getId() {
        return id;
    }

    public void setResolucoes(int resolucoes) {
        this.resolucoes = resolucoes;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setProvaDto(ProvaDto provaDto) {
        this.provaDto = provaDto;
    }

    public BigDecimal getNotaAluno() {
        return notaAluno;
    }

    public void setNotaAluno(BigDecimal notaAluno) {
        this.notaAluno = notaAluno;
    }


    public void setTotalmenteCorrigida(Boolean totalmenteCorrigida) {
        this.totalmenteCorrigida = totalmenteCorrigida;
    }

    @Override
    public String toString() {
        return "ProvaRespondidaDto{" +
                "nomeAluno='" + nomeAluno + '\'' +
                ", emailAluno='" + emailAluno + '\'' +
                ", id=" + id +
                ", provaDto=" + provaDto +
                ", notaMaxima=" + notaMaxima +
                ", questoesCorrigidas=" + questoesCorrigidas +
                ", totalQuestoes=" + totalQuestoes +
                ", notaAluno=" + notaAluno +
                ", totalmenteCorrigida=" + totalmenteCorrigida +
                ", questoesRespondidasDto=" + questoesRespondidasDto +
                ", resolucoes=" + resolucoes +
                '}';
    }
}
