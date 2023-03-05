package infraestrutura.repository;

import dominio.Usuario;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.logging.Log;
import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import javax.ws.rs.WebApplicationException;

@ApplicationScoped
public class UsuarioRepository implements PanacheRepository<Usuario> {

  @Transactional
  public void cadastrarUsuario(Usuario usuario) {
    try {
      persist(usuario);
    } catch (WebApplicationException e) {
      throw new WebApplicationException(e.getMessage(), e.getResponse());
    }
  }

  public Usuario buscarUsuarioPorEmail(String email) {
    try {
      Log.info(" Verificando se ja existe um usuario com email : " + email);
      return find("email  = ?1", email).firstResult();
    } catch (WebApplicationException e) {
      throw new WebApplicationException(e.getMessage(), e.getResponse());
    }
  }

  public Usuario buscarUsuario(String usuario) {
    try {
      Log.info(" Verificando se ja existe um usuario : " + usuario);
      return find("usuario  = ?1", usuario).firstResult();
    } catch (WebApplicationException e) {
      throw new WebApplicationException(e.getMessage(), e.getResponse());
    }
  }
}
