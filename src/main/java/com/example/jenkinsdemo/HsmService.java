package com.example.jenkinsdemo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.Signature;
import java.security.cert.Certificate;
import java.util.Base64;

@Service
public class HsmService {

    private final KeyStore keyStore;
    private final Provider provider;
    private final String keyAlias;

    public HsmService(
            KeyStore keyStore,
            Provider hsmProvider,
            @Value("${hsm.key-alias}") String keyAlias) {

        this.keyStore = keyStore;
        this.provider = hsmProvider;
        this.keyAlias = keyAlias;
    }

    public boolean isConnected() {

        try {
            return keyStore.containsAlias(keyAlias);
        } catch (Exception e) {
            return false;
        }
    }

    public String getProviderName() {
        return provider.getName();
    }

    public String getKeyAlias() {
        return keyAlias;
    }

    public String sign(String message) throws Exception {

        Key key =
                keyStore.getKey(keyAlias, null);

        if (!(key instanceof PrivateKey)) {
            throw new IllegalStateException(
                    "Private key not found: " + keyAlias);
        }

        Signature signature =
                Signature.getInstance(
                        "SHA256withRSA",
                        provider);

        signature.initSign((PrivateKey) key);

        signature.update(
                message.getBytes(StandardCharsets.UTF_8));

        byte[] signed =
                signature.sign();

        return Base64.getEncoder()
                .encodeToString(signed);
    }

    public boolean verify(
            String message,
            String base64Signature) throws Exception {

        Certificate certificate =
                keyStore.getCertificate(keyAlias);

        if (certificate == null) {
            throw new IllegalStateException(
                    "Certificate not found: " + keyAlias);
        }

        Signature verifier =
                Signature.getInstance("SHA256withRSA");

        verifier.initVerify(
                certificate.getPublicKey());

        verifier.update(
                message.getBytes(StandardCharsets.UTF_8));

        return verifier.verify(
                Base64.getDecoder()
                        .decode(base64Signature));
    }
}





