package aplicacao;

import javax.ws.rs.core.Application;
import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.info.Contact;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@OpenAPIDefinition(
    tags = {
      @Tag(name = "Conteúdo", description = "Controllers de Conteúdo"),
      @Tag(name = "Prova", description = "Controllers de Prova"),
      @Tag(name = "Questão", description = "Controllers de Questão"),
    },
    info =
        @Info(
            title = "API PGC I",
            version = "0.1",
            contact = @Contact(name = "Lucas Moniz de Arruda", email = "lucas_moniz@hotmail.com")))
public class Documentacao extends Application {}
