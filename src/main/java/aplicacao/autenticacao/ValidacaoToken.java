package aplicacao.autenticacao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.smallrye.common.annotation.Blocking;
import io.smallrye.jwt.auth.principal.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import javax.annotation.Priority;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jose4j.jwa.AlgorithmConstraints;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;

@ApplicationScoped
@Alternative
@Priority(1)
public class ValidacaoToken extends JWTCallerPrincipalFactory {

  @ConfigProperty(name = "publickey")
  String publicKey;

  private static void validarClaims(JwtClaims claims, JWTAuthContextInfo authContextInfo)
      throws Exception {
    if (!claims.getClaimNames().containsAll(authContextInfo.getRequiredClaims()))
      throw new Exception("Os Claims do token são inválidos");
  }

  private static String obterKeyIdToken(String token) throws JsonProcessingException {
    HeaderJwt header =
        new ObjectMapper()
            .readValue(
                new String(Base64.getDecoder().decode(token.split("\\.")[0])), HeaderJwt.class);
    return header.kid;
  }

  @Override
  @Blocking
  public JWTCallerPrincipal parse(String token, JWTAuthContextInfo authContextInfo)
      throws ParseException {
    try {
      String keyId = obterKeyIdToken(token);
      JwtConsumer jwtConsumer = obterConsumerToken(keyId, authContextInfo);
      JwtClaims claims = jwtConsumer.processToClaims(token);
      //      validarClaims(claims, authContextInfo);
      return new DefaultJWTCallerPrincipal(claims);
    } catch (Exception ex) {
      ex.printStackTrace();
      throw new ParseException(ex.getMessage());
    }
  }

  private JwtConsumer obterConsumerToken(String keyId, JWTAuthContextInfo authContextInfo)
      throws Exception {
    PublicKey publicKey = obterChavePublica(keyId);
    return new JwtConsumerBuilder()
        .setRequireExpirationTime()
        .setSkipDefaultAudienceValidation()
        .setJweAlgorithmConstraints(
            AlgorithmConstraints.ConstraintType.PERMIT, AlgorithmIdentifiers.RSA_USING_SHA256)
        .setExpectedIssuer(authContextInfo.getIssuedBy())
        .setVerificationKey(publicKey)
        .build();
  }

  private PublicKey obterChavePublica(String keyId)
      throws NoSuchAlgorithmException, InvalidKeySpecException, CertificateException, IOException {
    return obterChavePublicaCaixas();
  }

  private PublicKey obterChavePublicaCaixas()
      throws NoSuchAlgorithmException, InvalidKeySpecException {
    byte[] teste = Base64.getDecoder().decode(publicKey.getBytes(StandardCharsets.UTF_8));
    X509EncodedKeySpec keySpec = new X509EncodedKeySpec(teste);
    KeyFactory keyFactory = KeyFactory.getInstance("RSA");
    return keyFactory.generatePublic(keySpec);
  }

  @SuppressWarnings("unchecked")
  private static class HeaderJwt {
    public String alg;
    public String typ;
    public String kid;
  }
}
