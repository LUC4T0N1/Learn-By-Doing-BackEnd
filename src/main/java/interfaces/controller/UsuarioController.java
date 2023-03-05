package interfaces.controller;

import aplicacao.service.UsuarioService;
import dominio.TrocarSenhaDto;
import infraestrutura.dto.UsuarioDto;
import interfaces.controller.resposta.RespostaAPI;
import io.quarkus.logging.Log;
import javax.annotation.security.PermitAll;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.eclipse.microprofile.jwt.Claim;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@RequestScoped
@Path("api/usuario")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UsuarioController {

  private final String divisao =
      "--------------------------------------------------------------NOVA CHAMADA----------------------------------------------------------------------";

  @Inject UsuarioService usuarioService;

  @Inject RespostaAPI api;

  @Inject
  @Claim("usuario")
  String usuario;

  @Inject
  @Claim("email")
  String email;

  @POST
  @Transactional
  @Tag(name = "Usuario", description = "Controllers de Usuario")
  @Operation(summary = "Cadastra um novo Usuario", description = "Cadastra um novo Usuario")
  @PermitAll
  public Response cadastrarUsuario(UsuarioDto dto) {
    return api.executar(
        () -> {
          Log.info(
              divisao + "\n[Cadastrar Novo Usuario] nome: " + dto.nome + " email: " + dto.email);
          usuarioService.cadastrarUsuario(dto);
          return api.retornar("Usuario cadastrado com sucesso!");
        });
  }

  @GET
  @Tag(name = "Usuario", description = "Controllers de Usuario")
  @Operation(summary = "Cadastra um novo Usuario", description = "Cadastra um novo Usuario")
  public Response buscarUsuario() {
    return api.executar(
        () -> {
          Log.info(
              divisao
                  + "\n[Buscar Usuario] iniciando busca do usuario: "
                  + usuario
                  + " de email: "
                  + email);
          return api.retornar(usuarioService.buscarUsuario(email, usuario));
        });
  }

  @POST
  @Path("/login")
  @Tag(name = "Usuario", description = "Controllers de Usuario")
  @Operation(summary = "Loga com a conta de um Usuario", description = "Logar usuario")
  @PermitAll
  public Response logar(UsuarioDto dto) {
    return api.executar(
        () -> {
          Log.info(divisao + "\n[Logar Usuario] logando usuario de email: " + dto.email);
          return api.retornar(usuarioService.logarUsuario(dto));
        });
  }

  @PUT
  @Path("/trocarSenha")
  @Tag(name = "Usuario", description = "Controllers de Usuario")
  @Operation(summary = "Loga com a conta de um Usuario", description = "Logar usuario")
  @PermitAll
  public Response alterarSenha(TrocarSenhaDto dto) {
    return api.executar(
        () -> {
          Log.info(divisao + "\n[Trocar Senha] Usuario trocando de senha... ");
          usuarioService.alterarSenha(dto, usuario);
          Log.info("Senha alterada com sucesso!");
          return api.retornar("Senha alterada com sucesso!");
        });
  }
}
