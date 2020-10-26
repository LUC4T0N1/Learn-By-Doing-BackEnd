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

    public Conteudo paraDominio(String nome){
        return Conteudo.instanciar(nome);
    }

}
