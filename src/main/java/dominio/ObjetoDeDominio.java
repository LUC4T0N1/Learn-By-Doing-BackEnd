package dominio;

import java.util.Date;
import javax.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@MappedSuperclass
public abstract class ObjetoDeDominio {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", updatable = false, nullable = false)
  private Long id;

  @CreationTimestamp
  @Temporal(TemporalType.TIMESTAMP)
  private Date inclusao;

  @Column(name = "ultima_alteracao")
  @UpdateTimestamp
  @Temporal(TemporalType.TIMESTAMP)
  private Date ultimaAlteracao;

  private String usuario;

  public String getUsuario() {
    return usuario;
  }

  public void setUsuario(String usuario) {
    this.usuario = usuario;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Date getInclusao() {
    return inclusao;
  }

  public Date getUltimaAlteracao() {
    return ultimaAlteracao;
  }
}
