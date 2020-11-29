package infraestrutura.dto;
import dominio.ProvaRespondida;
import dominio.QuestaoRespondida;
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
    public Long idProva;
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
        realizarProvaDto.setEmailAluno(provaRespondida.getEmailAluno());
        realizarProvaDto.setNomeAluno(provaRespondida.getNomeAluno());
        realizarProvaDto.setNotaAluno(provaRespondida.getNotaAluno());
        realizarProvaDto.setNotaMaxima(provaRespondida.getProva().getNotaMaxima());
        realizarProvaDto.setQuestoesCorrigidas(provaRespondida.getQuestoesCorrigidas());
        realizarProvaDto.setTotalQuestoes(provaRespondida.getProva().getQuestoes().size());
        realizarProvaDto.setResolucoes(provaRespondida.getResolucoes());
        realizarProvaDto.setTotalmenteCorrigida(provaRespondida.getCorrigida());
        List<QuestaoRespondidaDto> questoesRespondidasDto = new ArrayList<>();
        System.out.println(provaRespondida.getQuestoesRespondidas().size());
        for(QuestaoRespondida questaoRespondida : provaRespondida.getQuestoesRespondidas()){
            questoesRespondidasDto.add(QuestaoRespondidaDto.instanciar(questaoRespondida));
        }
        realizarProvaDto.setQuestoesRespondidasDto(questoesRespondidasDto);
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

    public Long getIdProva() {
        return idProva;
    }

    public void setResolucoes(int resolucoes) {
        this.resolucoes = resolucoes;
    }

    public void setIdProva(Long idProva) {
        this.idProva = idProva;
    }

    public ProvaDto getProvaDto() {
        return provaDto;
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

    public List<QuestaoRespondidaDto> getQuestoesRespondidasDto() {
        return questoesRespondidasDto;
    }

    public void setQuestoesRespondidasDto(List<QuestaoRespondidaDto> questoesRespondidasDto) {
        this.questoesRespondidasDto = questoesRespondidasDto;
    }

    public void setTotalmenteCorrigida(Boolean totalmenteCorrigida) {
        this.totalmenteCorrigida = totalmenteCorrigida;
    }
}
