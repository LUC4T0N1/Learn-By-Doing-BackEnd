package aplicacao.autenticacao;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.WebApplicationException;
import io.smallrye.jwt.build.Jwt;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class GerarTokenService {

    @ConfigProperty(name = "privatekey")
    String privatekey;

    @ConfigProperty(name = "issuer")
    String issuer;


    public String gerarToken(String usuario, String email) {
        try {
            System.out.println("Gerando token..");
            Long expiracaoToken = obterExpiracaoMillis(10080L);
            String token = gerarToken(usuario, email, expiracaoToken);
            System.out.println("token: \n " + token);
            return token;
        } catch (WebApplicationException we) {
            System.out.println("Erro ao gerar token do caixa!!");
            throw we;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }

    private String gerarToken(String usuario, String email, Long expiracao) throws NoSuchAlgorithmException, InvalidKeySpecException {
        Map<String, Object> claims =
                new HashMap<>() {
                    {
                        put("iss", issuer);
                        put("exp", expiracao / 1000);
                        put("usuario", usuario);
                        put("email", email);
                    }
                };
        return gerarToken(claims);
    }

    private String gerarToken(Map<String, Object> claims)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        PKCS8EncodedKeySpec keySpec =
                new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privatekey));
        PrivateKey privKey = KeyFactory.getInstance("RSA").generatePrivate(keySpec);
        String str = Jwt.claims(claims).jws().sign(privKey);
        return str;
    }


    private static Long obterExpiracaoMillis(Long minutosDuracao) {
        Long exp = System.currentTimeMillis() + (minutosDuracao * 60000);
        return exp;
    }
}
