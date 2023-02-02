package aplicacao.autenticacao;

import org.eclipse.microprofile.jwt.JsonWebToken;
import org.jboss.resteasy.core.ResourceMethodInvoker;

import javax.annotation.security.PermitAll;
import javax.inject.Inject;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.lang.reflect.Method;

@Provider
public class ValidacaoPermissoes implements ContainerRequestFilter {


  private static final Response TOKEN_INVALIDO =
      Response.status(Response.Status.FORBIDDEN.getStatusCode(), "Token inválido").build();

  @Override
  public void filter(ContainerRequestContext request) {
    ResourceMethodInvoker methodInvoker =
        (ResourceMethodInvoker)
            request.getProperty("org.jboss.resteasy.core.ResourceMethodInvoker");
    Method method = methodInvoker.getMethod();
    String autorization = request.getHeaderString(HttpHeaders.AUTHORIZATION);
    if (!method.isAnnotationPresent(PermitAll.class)) validarToken(request, autorization);
  }

  private void validarToken(
      ContainerRequestContext request, String autorization) {
    if (autorization == null) {
      System.out.println("TOKEN INVALIDO!");
      request.abortWith(TOKEN_INVALIDO);
    }
  }

}
