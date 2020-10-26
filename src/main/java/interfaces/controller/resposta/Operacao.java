package interfaces.controller.resposta;

import javax.ws.rs.core.Response;

public interface Operacao {
    Response executar() throws Exception;
}
