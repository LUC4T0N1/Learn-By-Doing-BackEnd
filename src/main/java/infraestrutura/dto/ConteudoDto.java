package infraestrutura.dto;

import dominio.Conteudo;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(name = "Conteúdos", description = "JSON com informações sobre um conteúdo específico")
public class ConteudoDto {
  public String nome;
  public Long idProva;
  public Long idConteudo;
  public Long numeroProvas;

  public Long numeroProvasPublicas;

  protected ConteudoDto() {}

  public static ConteudoDto instanciarPorEntidade(Conteudo conteudo) {
    ConteudoDto conteudoDto = new ConteudoDto();
    conteudoDto.setNome(conteudo.getNome());
    conteudoDto.setIdConteudo(conteudo.getId());
    conteudoDto.setNumeroProvas(conteudo.getNumeroProvas());
    conteudoDto.setNumeroProvasPublicas(conteudo.getNumeroProvasPublicas());
    return conteudoDto;
  }

  public static ConteudoDto instanciar(String nome, Long idConteudo, Long idProva) {
    ConteudoDto conteudoDto = new ConteudoDto();
    conteudoDto.setNome(nome);
    conteudoDto.setIdConteudo(idConteudo);
    conteudoDto.setIdProva(idProva);
    return conteudoDto;
  }

  public void setIdProva(Long idProva) {
    this.idProva = idProva;
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

  public void setNumeroProvasPublicas(Long numeroProvasPublicas) {
    this.numeroProvasPublicas = numeroProvasPublicas;
  }
}
