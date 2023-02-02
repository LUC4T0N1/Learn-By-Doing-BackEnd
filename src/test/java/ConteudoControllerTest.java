import dominio.Conteudo;
import infraestrutura.dto.ConteudoDto;
import infraestrutura.dto.ProvaDto;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.h2.H2DatabaseTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;


@QuarkusTest
@QuarkusTestResource(H2DatabaseTestResource.class)
public class ConteudoControllerTest {

        @Test
        void cadastrarConteudoTest() {
            try {
                ConteudoDto conteudoDto = ConteudoDto.instanciar("novo conteudo teste", null, null);
                given()
                        .contentType(ContentType.JSON)
                        .body(conteudoDto)
                        .post("api/conteudo")
                        .then()
                        .statusCode(200);

            } catch (Exception e) {
                throw new Error(e);
            }
        }

    void cadastrarConteudo() {
        try {
            ConteudoDto conteudoDto = ConteudoDto.instanciar("novo conteudo teste", null, null);
            given()
                    .contentType(ContentType.JSON)
                    .body(conteudoDto)
                    .post("api/conteudo")
                    .then()
                    .statusCode(200);

        } catch (Exception e) {
            throw new Error(e);
        }
    }

    @Test
    void adicionarProvaInexistenteConteudo() {
        try {
            ConteudoDto conteudoDto = ConteudoDto.instanciar(null, 8L, 8L);
            given()
                    .contentType(ContentType.JSON)
                    .body(conteudoDto)
                    .post("api/conteudo/prova")
                    .then()
                    .statusCode(404);

        } catch (Exception e) {
            throw new Error(e);
        }
    }

    void adicionarProva(){
        ProvaDto provaDto = ProvaDto.instanciar("teste");
        given()
                .contentType(ContentType.JSON)
                .body(provaDto)
                .post("api/prova")
                .then()
                .statusCode(200);
    }

    @Test
    void adicionarProvaConteudo() {
        try {
            this.adicionarProva();
            this.cadastrarConteudo();
            ConteudoDto conteudoDto = ConteudoDto.instanciar(null, 1L, 1L);
            given()
                    .contentType(ContentType.JSON)
                    .body(conteudoDto)
                    .post("api/conteudo/prova")
                    .then()
                    .statusCode(200);
        } catch (Exception e) {
            throw new Error(e);
        }
    }

}
