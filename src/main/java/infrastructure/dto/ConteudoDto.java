package infrastructure.dto;

import domain.Conteudo;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(
        name = "Conteúdos",
        description = "JSON com informações sobre um conteúdo específico")
public class ConteudoDto {
    public String nome;
    public Long idProva;
    public Long idConteudo;
    public Long numeroProvas;

    protected ConteudoDto(){}

    public Conteudo paraDominio(String nome, Long numeroProvas, String usuario){
        return Conteudo.instanciar(nome, numeroProvas, usuario);
    }

    public static ConteudoDto instanciar(Conteudo conteudo){
        ConteudoDto conteudoDto = new ConteudoDto();
        conteudoDto.setNome(conteudo.getNome());
        conteudoDto.setIdConteudo(conteudo.getId());
        conteudoDto.setNumeroProvas(conteudo.getNumeroProvas());
        return conteudoDto;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setIdConteudo(Long idConteudo) {
        this.idConteudo = idConteudo;
    }

    public void setNumeroProvas(Long numeroProvas) {
        this.numeroProvas = numeroProvas;
    }
}
