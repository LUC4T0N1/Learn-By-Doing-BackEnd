package interfaces.controller.resposta;


import java.io.Serializable;
import java.util.Set;
import java.util.stream.Collectors;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(
        name = "Resposta",
        description = "Resposta padrão da API. Contém mensagem e sucesso da operação.")
@ApplicationScoped
public class RespostaAPI implements Serializable {

    @Inject Validator validator;

    private String mensagem;
    private boolean sucesso;

    RespostaAPI() {
        /* Construtor Jackson */
    }

    public Response retornar(Object entity) {
        if (entity == null) {
            return Response.ok().build();
        } else if (entity instanceof Operacao) {
            return executar((Operacao) entity);
        } else if (entity instanceof String) {
            return Response.ok(new RespostaAPI((String) entity, true)).build();
        } else {
            return Response.ok(entity).build();
        }
    }

    public <T> Response validarDto(Set<ConstraintViolation<T>> violacoesCategoria, Object entity) {
        if (violacoesCategoria != null) {
            if (!violacoesCategoria.isEmpty()) {
                return Response.status(Status.BAD_REQUEST)
                        .entity(new RespostaAPI(violacoesCategoria))
                        .build();
            } else {
                return retornar(entity);
            }
        } else {
            return retornar(entity);
        }
    }

    public <T> Response retornar(Operacao operacao, T dto) {
        try {
            Set<ConstraintViolation<T>> violacoes = validator.validate(dto);
            if (violacoes.isEmpty()) {
                return operacao.executar();
            } else {

                return erro(violacoes);
            }
        } catch (Exception e) {
            return erro(e);
        }
    }

    public Response executar(Operacao operacao) {
        try {
            return operacao.executar();
        } catch (Exception e) {
            return erro(e);
        }
    }

    public static Response sucesso(String mensagem) {
        return Response.ok(new RespostaAPI(mensagem, true)).build();
    }

    public static Response erro(Set<? extends ConstraintViolation<?>> violacoes) {
        return Response.status(Status.BAD_REQUEST).entity(new RespostaAPI(violacoes)).build();
    }

    public static Response erro(Exception e) {
        if (e instanceof WebApplicationException) {
            return Response.status(((WebApplicationException) e).getResponse().getStatus())
                    .entity(new RespostaAPI(e.getMessage(), false))
                    .build();
        } else {
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity(new RespostaAPI(e.getMessage() + ":" + e.getCause(), false))
                    .build();
        }
    }


    private RespostaAPI(String mensagem, boolean sucesso) {
        this.mensagem = mensagem;
        this.sucesso = sucesso;
    }

    private RespostaAPI(Set<? extends ConstraintViolation<?>> violacoes) {
        this.sucesso = false;
        this.mensagem =
                violacoes.stream().map(ConstraintViolation::getMessage).collect(Collectors.joining("; "));
    }

    public String getMensagem() {
        return mensagem;
    }

    public boolean isSucesso() {
        return sucesso;
    }
}
