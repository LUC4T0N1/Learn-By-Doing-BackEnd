package aplicacao.service;

import aplicacao.autenticacao.GerarTokenService;
import dominio.TrocarSenhaDto;
import dominio.Usuario;
import infraestrutura.dto.AutenticacaoDto;
import infraestrutura.dto.UsuarioDto;
import infraestrutura.repository.*;
import io.quarkus.logging.Log;
import java.util.Objects;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.WebApplicationException;

@ApplicationScoped
public class UsuarioService {

  @Inject UsuarioRepository usuarioRepository;

  @Inject ProvaRepository provaRepository;

  @Inject QuestaoRepository questaoRepository;

  @Inject QuestaoRespondidaRepository questaoRespondidaRepository;

  @Inject ProvaRespondidaRepository provaRespondidaRepository;

  @Inject ConteudoRepository conteudoRepository;

  @Inject GerarTokenService gerarTokenService;

  @Transactional
  public void cadastrarUsuario(UsuarioDto dto) {
    try {
      Usuario existente = usuarioRepository.buscarUsuarioPorEmail(dto.email);
      if (existente == null) {
        Usuario usuarioObj = Usuario.instanciar(dto);
        usuarioRepository.cadastrarUsuario(usuarioObj);
        Log.info(
            "[Cadastrar Novo Usuario] Usuario de email " + dto.email + " cadastrado com sucesso!");
      } else {
        throw new WebApplicationException("Esse email já foi cadastrado!", 409);
      }
    } catch (WebApplicationException e) {
      throw new WebApplicationException(e.getMessage(), e.getResponse().getStatus());
    }
  }

  public UsuarioDto buscarUsuario(String email, String usuario) {
    try {
      UsuarioDto dto = new UsuarioDto();
      Usuario usuarioObj = usuarioRepository.buscarUsuarioPorEmail(email);
      if (usuarioObj != null) {
        dto.completo = true;
        dto.nome = usuarioObj.getNome();
        dto.email = usuarioObj.getEmail();
        dto.provasCriadas = provaRepository.buscarProvasCriadasPorUsuario(usuario);
        dto.provasResolvidas = provaRespondidaRepository.buscarProvasRealizadasPorUsuario(usuario);
        dto.provasCorrigidas = provaRepository.buscarProvasCorrigidasPorUsuario(usuario);
        dto.questoesCriadas = questaoRepository.buscarQuestoesCriadasPorUsuario(usuario);
        dto.questoesResolvidas =
            questaoRespondidaRepository.buscarQuestoesRespondidasPorUsuario(usuario);
        dto.conteudosCriados = conteudoRepository.buscarConteudosPorUsuario(usuario);
        dto.dataCriacao = usuarioRepository.buscarUsuario(usuario).getInclusao().toString();
      } else {
        throw new WebApplicationException("Erro gravissimo!", 404);
      }
      return dto;
    } catch (WebApplicationException e) {
      throw new WebApplicationException(e.getMessage(), e.getResponse().getStatus());
    }
  }

  public AutenticacaoDto logarUsuario(UsuarioDto dto) {
    try {
      Usuario usuario = usuarioRepository.buscarUsuarioPorEmail(dto.email);
      if (usuario == null) {
        throw new WebApplicationException("Esse email não foi encontrado!", 404);
      } else {
        validarSenha(dto.senha, usuario.getSenha());
        Log.info("[Logar Usuario] Usuario logado com sucesso!");
        return AutenticacaoDto.instanciar(
            gerarTokenService.gerarToken(usuario.getUsuario(), usuario.getEmail()),
            usuario.getUsuario());
      }

    } catch (WebApplicationException e) {
      throw new WebApplicationException(e.getMessage(), e.getResponse().getStatus());
    }
  }

  public void validarSenha(String senhaRecebida, String senhaUsuario) {
    try {
      Log.info("[Logar Usuario] Validando senha...");
      if (!Objects.equals(Usuario.cryptografarSenha(senhaRecebida), senhaUsuario)) {
        Log.info("Senha incorreta!");
        throw new WebApplicationException("Senha incorreta!", 401);
      }
    } catch (WebApplicationException e) {
      throw new WebApplicationException(e.getMessage(), e.getResponse().getStatus());
    }
  }

  @Transactional
  public void alterarSenha(TrocarSenhaDto dto, String usuario) {
    try {
      Usuario usu = usuarioRepository.buscarUsuario(usuario);
      if (!Objects.equals(Usuario.cryptografarSenha(dto.senhaAtual), usu.getSenha()))
        throw new WebApplicationException("Senha antiga incorreta!", 400);
      else {
        if (!Objects.equals(dto.senhaNova, dto.senhaNovaConfirmacao))
          throw new WebApplicationException("Senhas diferentes!", 400);
        else {
          usu.setSenha(Usuario.cryptografarSenha(dto.senhaNova));
        }
      }
    } catch (WebApplicationException e) {
      throw new WebApplicationException(e.getMessage(), e.getResponse().getStatus());
    }
  }
}
