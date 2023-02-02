import aplicacao.autenticacao.GerarTokenService;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

@QuarkusTest
public class GerarTokenTest {

    @Inject GerarTokenService service;

    @Test
    public void gerarToken(){service.gerarToken("d8cbfe9f-18b4-49bc-b71b-7114c896e5d8", "lucas_moniz@hotmail.com");
    }
}
