package infrastructure.dto;

import domain.Conteudo;

public class ConteudoDto {
    public String nome;
    public Long idProva;
    public Long idConteudo;

    public Conteudo paraDominio(String nome){
        return Conteudo.instanciar(nome);
    }

}
