package com.example.jenkinsdemo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.KeyStore;
import java.security.Provider;
import java.security.Security;

@Configuration
public class HsmConfig {

    @Bean
    public Provider hsmProvider(
            @Value("${hsm.pkcs11-config}") String configPath) {

        Provider baseProvider =
                Security.getProvider("SunPKCS11");

        if (baseProvider == null) {
            throw new IllegalStateException(
                    "SunPKCS11 provider is not available");
        }

        Provider provider =
                baseProvider.configure(configPath);

        Security.addProvider(provider);

        return provider;
    }

    @Bean
    public KeyStore hsmKeyStore(
            Provider hsmProvider,
            @Value("${hsm.pin}") String pin) throws Exception {

        KeyStore keyStore =
                KeyStore.getInstance("PKCS11", hsmProvider);

        keyStore.load(null, pin.toCharArray());

        return keyStore;
    }
}
