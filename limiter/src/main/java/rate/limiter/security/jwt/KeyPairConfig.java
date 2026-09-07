package rate.limiter.security.jwt;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

@Configuration
public class KeyPairConfig {

    @Bean
    public KeyPair keyPair() {
        try{
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            return keyPairGenerator.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
    @Bean
    public RSAPublicKey publicKey() {
        return (RSAPublicKey) keyPair().getPublic();
    }
    @Bean
    public RSAPrivateKey privateKey() {
        return (RSAPrivateKey) keyPair().getPrivate();
    }
}
