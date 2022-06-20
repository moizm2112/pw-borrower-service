package com.paywallet.userservice.user.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class KeyConfigLoader {
    @Value("${aes.enc.key}")
    private String encKey;

    @Value("${aes.enc.iv}")
    private String encIv;

    @PostConstruct
    public void load() {
        KeyConfig.init(encKey, encIv);
    }
}
